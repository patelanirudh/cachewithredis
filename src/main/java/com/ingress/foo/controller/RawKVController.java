package com.ingress.foo.controller;

import com.ingress.foo.entity.RawKVData;
import com.ingress.foo.service.RawKVService;
import com.ingress.foo.service.RawKVServiceForRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/v1/poc")
@Slf4j
public class RawKVController {

    @Autowired
    private RawKVService rawKVService;

    @Autowired
    RawKVServiceForRepo rawKVServiceForRepo;

    @Value("${useCacheRepo:false}")
    private Boolean useCacheRepo;

    @PostMapping(path = "/data", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<RawKVData> saveRawData(@RequestBody RawKVData data) {
        if (useCacheRepo) {
            return rawKVService.saveRawData(data);
        } else {
            return rawKVServiceForRepo.saveRawData(data);
        }
    }

    @PutMapping(path = "/data/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<RawKVData> updateRawData(@RequestBody RawKVData data) {
        if (useCacheRepo) {
            return rawKVService.updateRawData(data);
        } else {
            return rawKVServiceForRepo.updateRawData(data);
        }
    }

    @GetMapping(path = "/data/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<RawKVData> getDataById(@PathVariable(name = "id") String id) {
        if (useCacheRepo) {
            return rawKVService.getDataById(id);
        } else {
            return rawKVServiceForRepo.getDataById(id);
        }
    }

    @DeleteMapping(path = "/data/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteData(@PathVariable(name = "id") String id) {
        if (useCacheRepo) {
            rawKVService.deleteDataById(id);
        } else {
            rawKVServiceForRepo.deleteDataById(id);
        }
    }

    @DeleteMapping(path = "/data/all", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteAllData() {
        if (useCacheRepo) {
            rawKVService.deleteAllData();
        } else {
            rawKVServiceForRepo.deleteAllData();
        }
    }
}
