package com.example.springsecurityjwtexample.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class FakeUserService {

    private Map<String, User> users = new HashMap<>();

    @PostConstruct
    public void initialize() {
        users.put("Rana", new User("Rana", "123",new ArrayList<>()));

    }

    public User getUserByUsername(String username) {
        return users.get(username);
    }

}
