package com.example.blog.service;

import com.example.blog.model.AdminUser;
import com.example.blog.repository.AdminUserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
public class AdminUserDetailsService implements UserDetailsService {

    private final AdminUserRepository repo;

    public AdminUserDetailsService(AdminUserRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AdminUser u = repo.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("User not found"));
        return new User(u.getUsername(), u.getPasswordHash(), Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }
}
