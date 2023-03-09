package com.ingress.foo.service;

import com.ingress.foo.entity.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@Slf4j
public class CustomerService {

    private final static String CUSTOMER_KEY = "CUSTOMER";

    @Resource(name = "redisTemplate")
    HashOperations<String, String, Customer> customerHashOps;

    public void addCustomer(Customer customer) {

        customerHashOps.putIfAbsent(CUSTOMER_KEY, customer.getId(), customer);
        log.info("Customer : {} saved", customer.getId());
    }

    public void getCustomer(String custId) {
        Customer customer;
        List<Customer> customers = new ArrayList<>();

        if (null != (customer = customerHashOps.get(CUSTOMER_KEY, custId))) {
            customers.add(customer);
        } else {
            log.error("Customer not found for Key {}, HashKey {}", CUSTOMER_KEY, custId);
        }

        Stream.of(customers)
                .filter(Objects::nonNull)
                .forEach(System.out::println);
    }

    public void deleteCustomer(String input_id) {

    }

}
