package com.erivan.gtmanager.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class CustomAuthenticationFilter extends OncePerRequestFilter {
    private final Logger logger = LogManager.getLogger();

    private static final String AUTH_HEADER_PARAMETER = "Authorization";
    private static final String TOKEN_TYPE = "Bearer";

    private String getJWT(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTH_HEADER_PARAMETER);

        if (bearerToken != null && !bearerToken.isBlank() && bearerToken.startsWith(TOKEN_TYPE)) {
            return bearerToken.substring(7);
        } else {
            return "";
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        logger.debug("performing request authentication");

        var token = getJWT(request);
        logger.debug(token);

        //write token validation

        filterChain.doFilter(request, response);
    }
}
