package com.ingress.foo.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ingress.foo.entity.Customer;
import com.ingress.foo.entity.User;
import com.ingress.foo.service.CustomerService;
import com.ingress.foo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class ApplicationListener {

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private UserService userService;

    @Value("${mockData:true}")
    private Boolean mockData;

    @EventListener
    public void handleApplicationReadyEvent(ApplicationReadyEvent readyEvent) throws JsonProcessingException {
        log.info("!!! ApplicationReadyEvent !!!");

        if (redisConnectionFactory.getConnection().ping() == null) {
            throw new IllegalStateException("Redis connection did not respond !!! Abort !!!");
        }

        if (mockData) {
            var custIds = loadCustomers();
            getCustomersByKey(custIds);

            var userIds = loadUsers();
            getUsersByKey(userIds);
        }
    }

    //*************** Customers ***************//
    private List<String> loadCustomers() {
        log.info("ApplicationListener : Loading customers");
        Customer customer1 = Customer.builder()
                .id("101")
                .name("Anirudh")
                .data("data1")
                .build();

        Customer customer2 = Customer.builder()
                .id("102")
                .name("Shweta")
                .data("data2")
                .build();

        customerService.addCustomer(customer1);
        customerService.addCustomer(customer2);
        return Arrays.asList("101", "102");
    }

    private void getCustomersByKey(List<String> custIds) {
        log.info("ApplicationListener : Fetching customersById");

        for (String id : custIds) {
           customerService.getCustomer(id);
        }
    }

    //*************** Users ***************//
    private List<String> loadUsers() {
        log.info("ApplicationListener : Loading users");
        User user1 = User.builder()
                .id("1001")
                .name("User1")
                .city("Pune")
                .build();

        User user2 = User.builder()
                .id("1002")
                .name("User2")
                .city("Jabalpur")
                .build();

        userService.addUser(user1);
        userService.addUser(user2);
        return Arrays.asList("1001", "1002");
    }

    private void getUsersByKey(List<String> userIds) throws JsonProcessingException {
        log.info("ApplicationListener : Fetching usersById");

        for (String id : userIds) {
            userService.getUser(id);
            userService.getCityByUserId(id);
        }
    }

}
