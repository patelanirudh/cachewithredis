package com.ingress.foo.repository;

import com.ingress.foo.entity.RawKVData;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RawKVRepo extends CrudRepository<RawKVData, String> {
}
