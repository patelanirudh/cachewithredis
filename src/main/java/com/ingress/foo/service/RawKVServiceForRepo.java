package com.ingress.foo.service;

import com.ingress.foo.entity.RawKVData;
import com.ingress.foo.repository.RawKVRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class RawKVServiceForRepo {

    @Autowired
    private RawKVRepo rawKVRepo;


    @Async("asyncWriteTaskPool")
    public CompletableFuture<RawKVData> saveRawData(RawKVData rawKVData) {
        return CompletableFuture.completedFuture(rawKVRepo.save(rawKVData));
    }

    @Async("asyncReadTaskPool")
    public CompletableFuture<RawKVData> getDataById(String id) {
        Optional<RawKVData> rawKVData = rawKVRepo.findById(id);
        if (rawKVData.isPresent()) {
            log.info("Data is being returned from Redis DB");
            return CompletableFuture.completedFuture(rawKVData.get());
        } else {
//            log.error("Data for id {} not found in DB", id);
            return null;
        }
    }

    @Async("asyncWriteTaskPool")
    public CompletableFuture<RawKVData> updateRawData(RawKVData rawKVData) {
        if (rawKVRepo.findById(rawKVData.getId()).isPresent()) {
            log.info("Data is being updated to Redis DB");
            return CompletableFuture.completedFuture(rawKVRepo.save(rawKVData));
        } else {
//            log.error("Data for id {} not found in DB", rawKVData.getId());
            return null;
        }
    }

    public void deleteDataById(String id) {
        rawKVRepo.deleteById(id);
    }

    public void deleteAllData() {
        rawKVRepo.deleteAll();
    }

}
