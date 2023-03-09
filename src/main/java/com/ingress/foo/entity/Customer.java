package com.ingress.foo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Customer implements Serializable {

        private static final long serialVersionUID = 1L;

        @Id
        @NotBlank
        private String id;

        @NotBlank
        private String data;

        @NotBlank
        private String name;
}
