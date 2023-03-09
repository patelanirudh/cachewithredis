package com.ingress.foo.repository;

import com.ingress.foo.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, String> {
}
