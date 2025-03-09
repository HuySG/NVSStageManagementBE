package com.nvsstagemanagement.nvs_stage_management.dto.config;


import com.nvsstagemanagement.nvs_stage_management.model.Role;
import com.nvsstagemanagement.nvs_stage_management.model.User;
import com.nvsstagemanagement.nvs_stage_management.repository.RoleRepository;
import com.nvsstagemanagement.nvs_stage_management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("Lỗi"));
        return new org.springframework.security.core.userdetails.User(user.getEmail(),user.getPassword(),mapRolesToAuthorities(String.valueOf(user.getRole().getId())));
    }

    private Collection<GrantedAuthority> mapRolesToAuthorities(String roleID) {
        Role role = roleRepository.getById(roleID);
        return Collections.singleton(new SimpleGrantedAuthority(role.getRoleName()));
    }


}
