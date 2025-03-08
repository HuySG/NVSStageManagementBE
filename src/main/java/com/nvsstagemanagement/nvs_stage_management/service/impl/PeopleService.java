package com.nvsstagemanagement.nvs_stage_management.service.impl;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class PeopleService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) {
        return User.withUsername("admin")
                .password("{noop}password") // "{noop}" để không mã hóa mật khẩu
                .roles("USER")
                .build();
    }
}
