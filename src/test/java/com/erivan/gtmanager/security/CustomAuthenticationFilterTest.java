package com.erivan.gtmanager.security;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.erivan.gtmanager.data.UserRepository;
import com.erivan.gtmanager.data.entity.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class CustomAuthenticationFilterTest {

    @Mock
    private JWTUtil jwtUtil;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomAuthenticationFilter customAuthenticationFilter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testDoFilterInternal_ValidToken() throws ServletException, IOException {
        // Arrange
        String token = "Bearer valid-token";
        String userId = "user-id";
        String userEmail = "user@example.com";
        DecodedJWT decodedTokenMock = mock(DecodedJWT.class);
        Claim uIdClaim = mock(Claim.class);

        when(request.getHeader("Authorization")).thenReturn(token);
        when(jwtUtil.decodeToken(token.substring(7))).thenReturn(decodedTokenMock);
        when(uIdClaim.asString()).thenReturn(userId);
        when(decodedTokenMock.getClaim("uid")).thenReturn(uIdClaim);
        User user = new User();
        user.setId(userId);
        user.setEmail(userEmail);
        user.setLastToken(new User.AccessToken(token, ""));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        customAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_InvalidToken() throws ServletException, IOException {
        // Arrange
        String token = "Bearer invalid-token";

        when(request.getHeader("Authorization")).thenReturn(token);
        when(jwtUtil.decodeToken(token.substring(7))).thenReturn(null);

        // Act
        customAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        SecurityContext securityContext = SecurityContextHolder.getContext();
        assertNull(securityContext.getAuthentication());
    }

    @Test
    void testDoFilterInternal_NoToken() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        customAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        SecurityContext securityContext = SecurityContextHolder.getContext();
        assertNull(securityContext.getAuthentication());
    }
}