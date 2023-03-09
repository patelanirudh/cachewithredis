package com.ingress.foo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@RedisHash("RawData")
@NoArgsConstructor
@AllArgsConstructor
@Data
//@Builder
public class RawKVData implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    public String id;

    private String data;
}
