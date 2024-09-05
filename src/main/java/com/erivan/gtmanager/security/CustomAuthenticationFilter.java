package com.erivan.gtmanager.security;

import com.erivan.gtmanager.data.UserRepository;
import com.erivan.gtmanager.data.entity.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class CustomAuthenticationFilter extends OncePerRequestFilter {
    private final Logger logger = LogManager.getLogger();

    private static final String AUTH_HEADER_PARAMETER = "Authorization";
    private static final String TOKEN_TYPE = "Bearer";

    @Autowired
    private JWTUtil jwtUtil;
    @Autowired
    private UserRepository userRepository;

    public CustomAuthenticationFilter() {
    }

    public CustomAuthenticationFilter(
            JWTUtil jwtUtil,
            UserRepository userRepository
    ) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

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
        logger.debug("performing request authentication:");
        var token = getJWT(request);
        logger.debug("  * " + token);

        var decodedJWT = jwtUtil.decodeToken(token);
        if (decodedJWT != null) {
            var userId = decodedJWT.getClaim("uid");

            var optUser = userRepository.findById(userId.asString());
            if (optUser.isPresent()) {
                User user = optUser.get();
                var userAuth = new UserAuth(user.getId(), user.getEmail());
                var authentication = new UsernamePasswordAuthenticationToken(userAuth,null,
                        userAuth.getRoles());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                logger.debug("  * User not found. id:" + userId);
            }

        } else {
            logger.debug("  * Any token found");
        }

        filterChain.doFilter(request, response);
    }

}
