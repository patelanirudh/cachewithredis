package com.ingress.foo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ingress.foo.entity.User;
import com.ingress.foo.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@Slf4j
public class UserService {

    private final static String USER_KEY = "User:{REPLACE}";
    private final static String FIND_BY_CITY = "city";

    @Autowired
    private UserRepository userRepo;

    @Resource(name = "redisTemplate2")
    private HashOperations<String, String, String> userHashOps;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public void addUser(User user) {
        userRepo.save(user);
        // customerHashOps.putIfAbsent(CUSTOMER_KEY, customer.getId(), customer);
        log.info("User : {} saved", user.getId());
    }

    public void getUser(String userId) {
        Optional<User> user = Optional.empty();
        List<User> users = new ArrayList<>();

        user = userRepo.findById(userId);

        if (user.isPresent()) {
            users.add(user.get());
        } else {
            log.error("User not found for Key 'User', HashKey {}", userId);
        }

//        if (null != (customer = customerHashOps.get(CUSTOMER_KEY, custId))) {
//            customers.add(customer);
//        } else {
//            log.error("Customer not found for Key {}, HashKey {}", CUSTOMER_KEY, custId);
//        }

        Stream.of(users)
                .filter(Objects::nonNull)
                .forEach(System.out::println);
    }

    public void getCityByUserId(String userId) throws JsonProcessingException {
        String finalUserId = StringUtils.replace(USER_KEY, "{REPLACE}", userId);
        log.info("getCityByUserId {} ", finalUserId);

        String cityFromStringRedisTemplate = (String) stringRedisTemplate.opsForHash().get(finalUserId, FIND_BY_CITY);

        String cityFound = userHashOps.get(finalUserId, FIND_BY_CITY);
        log.info("CityFound {} ", cityFound);
        log.info("CityFromStringRedisTemplate {} ", cityFromStringRedisTemplate);
    }

    public void deleteUser(String input_id) {

    }
}
