package com.erivan.gtmanager.security;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.erivan.gtmanager.data.UserRepository;
import com.erivan.gtmanager.data.entity.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomAuthenticationFilterTest {

    @Mock
    private JWTUtil jwtUtil;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private CustomAuthenticationFilter customAuthenticationFilter;

    private SecurityContext context;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        context = SecurityContextHolder.createEmptyContext();
        SecurityContextHolder.setContext(context);
    }

    @AfterEach
    void cleanUp() {
        SecurityContextHolder.clearContext();
        context = null;
    }

    @Test
    void whenDoInternalFilterWithValidTokenShouldProcessAndCallFilterChain() throws ServletException, IOException {
        // Arrange
        String token = "Bearer valid-token";
        String userId = "user-id";
        String userEmail = "user@example.com";
        DecodedJWT decodedTokenMock = mock(DecodedJWT.class);
        Claim uIdClaim = mock(Claim.class);
        Claim typClaim = mock(Claim.class);

        when(request.getHeader("Authorization")).thenReturn(token);
        when(jwtUtil.decodeToken(token.substring(7))).thenReturn(decodedTokenMock);
        when(uIdClaim.asString()).thenReturn(userId);
        when(typClaim.asString()).thenReturn("t");
        when(decodedTokenMock.getClaim("uid")).thenReturn(uIdClaim);
        when(decodedTokenMock.getClaim("typ")).thenReturn(typClaim);
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
    void whenDoInternalFilterWithInvalidTokenShouldNotProcessAuthenticationAndCallFilterChain() throws ServletException, IOException {
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
    void whenDoInternalFilterWithoutTokenShouldNotProcessAuthenticationAndCallFilterChain() throws ServletException, IOException {
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