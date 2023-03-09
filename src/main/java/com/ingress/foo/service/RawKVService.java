package com.ingress.foo.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.ingress.foo.entity.RawKVData;
import com.ingress.foo.repository.RawKVRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@CacheConfig(cacheNames = "rawData")
@Slf4j
public class RawKVService {

    private CacheManager cacheManager;
    private CaffeineCache caffeineCache;

    private Cache<Object, Object> caffeineNativeCache;
    @Autowired
    private RawKVRepo rawKVRepo;

    public RawKVService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
        log.info("Print Caches {}", cacheManager.getCacheNames());

        caffeineCache = (CaffeineCache) this.cacheManager.getCache("rawData");
        caffeineNativeCache = caffeineCache.getNativeCache();
    }

    @PostConstruct
    public void init() {
        if (caffeineCache != null) {
            log.info("Caffeine Cache {} , and is not null : {}", caffeineCache.getName(), caffeineCache.getNativeCache().asMap().size());
        }
    }

    @Async("asyncWriteTaskPool")
    public CompletableFuture<RawKVData> saveRawData(RawKVData rawKVData) {
//        RawKVData savedRawKVData1 = (RawKVData) caffeineNativeCache.asMap().computeIfAbsent(rawKVData.getId(), lookupId -> {
//            return rawKVRepo.save(rawKVData);
//        });

//        RawKVData savedRawKVData = caffeineCache.get(rawKVData.getId(), () -> {
//            return rawKVRepo.save(rawKVData);
//        });
//        return CompletableFuture.completedFuture(savedRawKVData);

        RawKVData savedRawKVData = rawKVRepo.save(rawKVData);
        caffeineCache.putIfAbsent(rawKVData.getId(), savedRawKVData);

        return CompletableFuture.completedFuture(savedRawKVData);
    }

    @Async("asyncReadTaskPool")
    public CompletableFuture<RawKVData> getDataById(String id) {

        /* Check inside cache and if not present:
         * 1. Go to Redis Cache via Repo findById()
         * 2. Update the cache with the returned value
         */
        RawKVData foundRawKVData = (RawKVData) caffeineNativeCache.get(id, key -> {
            Optional<RawKVData> rawKVData = rawKVRepo.findById(id);
            if (rawKVData.isPresent()) {
                log.info("Data is being returned from Redis DB : Then updated to Cache");
                return rawKVData.get();
            } else {
                log.error("Data for id {} not found in DB", id);
                return null;
            }
        });

//                .get(id, () -> {
//            Optional<RawKVData> rawKVData = rawKVRepo.findById(id);
//             if (rawKVData.isPresent()) {
//                 log.info("Data is being returned from Redis DB : Then updated to Cache");
//                 return rawKVData.get();
//             } else {
//                 log.error("Data for id {} not found in DB", id);
//                 return null;
//             }
//        });

        return CompletableFuture.completedFuture(foundRawKVData);

//        Optional<RawKVData> rawKVData = rawKVRepo.findById(id);
//        if (rawKVData.isPresent()) {
//            log.info("Data is being returned from Redis DB : Subsequent Cacheable");
//            return CompletableFuture.completedFuture(rawKVData.get());
//        } else {
//            log.error("Data for id {} not found in DB", id);
//            return null;
//        }
    }

    @Async("asyncWriteTaskPool")
    public CompletableFuture<RawKVData> updateRawData(RawKVData rawKVData) {
        RawKVData updatedRawKVData = (RawKVData) caffeineNativeCache.asMap().compute(rawKVData.getId(), (key, val) -> {
            // this should not have happened as cache should not have this key if
            // data was evicted and deleted from cache & redis
            // Note: Cache is only first populated during getOp(read from DB)
            if (null == val) {   // Present in DB. Update DB and Cache
                if (rawKVRepo.findById(rawKVData.getId()).isPresent()) {
                    return rawKVRepo.save(rawKVData);
                }
                // Not present in DB. Do not update DB and Cache
                return null;
            }

            // If present in Cache. Update DB and Cache
            return rawKVRepo.save(rawKVData);
        });

        return CompletableFuture.completedFuture(updatedRawKVData);

//        if (rawKVRepo.findById(rawKVData.getId()).isPresent()) {
//            log.info("Data is being updated to Redis DB : Subsequent CachePut()");
//            return CompletableFuture.completedFuture(rawKVRepo.save(rawKVData));
//        } else {
//            log.error("Data for id {} not found in DB", rawKVData.getId());
//            return null;
//        }
    }

//    @Async("asyncPOCControllerTaskPool")


//    @Async("asyncPOCControllerTaskPool")

    public void deleteDataById(String id) {
        rawKVRepo.deleteById(id);
        log.info("Data is being deleted from Redis DB");
        if (caffeineCache.evictIfPresent(id)) {
            log.info("Data is being deleted from Cache via EvictIfPresent");
        }
    }

    //    @Async("asyncPOCControllerTaskPool")
    public void deleteAllData() {
        rawKVRepo.deleteAll();
        log.info("All Data is being deleted from Redis DB");
        if (caffeineCache.invalidate()) {
            log.info("All Data is being deleted from Cache via Invalidate()");
        }
    }

}
