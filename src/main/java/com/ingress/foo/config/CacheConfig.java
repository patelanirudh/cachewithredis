package com.ingress.foo.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
@Slf4j
public class CacheConfig {

    @Value("${useCacheRepo:false}")
    private Boolean useCacheRepo;

    @Value("${cache.initialCap}")
    private Integer initialCap;

    @Value("${cache.maxSize}")
    private Integer maxSize;


    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager("rawData");
        caffeineCacheManager.setCaffeine(caffeineCacheBuilder());
        log.info("CaffeineCacheManager has been initialized successfully {}", caffeineCacheManager);

        return caffeineCacheManager;
    }

    Caffeine<Object, Object> caffeineCacheBuilder() {
        if (useCacheRepo) {
            log.info("CaffeineCacheBuilder has been set up based on initialCapacity {}, maximumSize {} ", initialCap, maxSize);
            return Caffeine.newBuilder()
                    .initialCapacity(initialCap)
                    .maximumSize(maxSize)
                    .expireAfterAccess(10, TimeUnit.MINUTES)
                    .recordStats();
        } else {
            return Caffeine.newBuilder()
                    .initialCapacity(1)
                    .maximumSize(1)
                    .expireAfterAccess(10, TimeUnit.MINUTES)
                    .recordStats();
        }
    }
}
