package com.example.springsecurityjwtexample.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final FakeUserService fakeUserService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       return fakeUserService.getUserByUsername(username);
    }
}
