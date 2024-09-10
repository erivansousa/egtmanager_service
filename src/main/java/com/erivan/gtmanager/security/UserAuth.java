package com.erivan.gtmanager.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

public record UserAuth(
        String id, String email, String tokenType
) {
    public List<GrantedAuthority> getRoles() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }
}
