package com.erivan.gtmanager.security;

import com.erivan.gtmanager.data.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


class CustomAuthenticationFilterTest {

    CustomAuthenticationFilter filter;
    JWTUtil jwtUtil;
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        jwtUtil = Mockito.mock(JWTUtil.class);
        userRepository = Mockito.mock(UserRepository.class);
        filter = new CustomAuthenticationFilter(jwtUtil, userRepository);
    }

    @Test
    void when() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        FilterChain filterChain = Mockito.mock(FilterChain.class);

        Mockito.when(request.getHeader("Authorization")).thenReturn("");

        try {
            filter.doFilterInternal(request, response, filterChain);
        } catch (Exception ignored) {

        }
    }
}