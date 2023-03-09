package com.ingress.foo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@RedisHash("User")
@Data
@Builder
@AllArgsConstructor
public class User implements Serializable {
    @Id
    @NotBlank
    private String id;

    @NotBlank
    private String name;

    @NotBlank
    private String city;
}
