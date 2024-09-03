package com.erivan.gtmanager.controller;

import com.erivan.gtmanager.error.UserAccountException;
import com.erivan.gtmanager.service.UserManagementService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.regex.Pattern;

@RestController
@RequestMapping("/account")
public class UserManagementController {
    private final Logger logger = LogManager.getLogger();

    private final UserManagementService service;

    public UserManagementController(@Autowired UserManagementService service) {
        this.service = service;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserManagementResult> createUser(@RequestBody UserSignUp user) {
        int statusCode = 201;
        String message = "created";

        logger.debug("validating new user information");
        if (isValidEmail(user.email())) {
            try {
                logger.debug("start creating new user");
                service.createUser(user);
            } catch (UserAccountException e) {
                statusCode = 409;
                message = e.getMessage();
            }
        } else {
            statusCode = 400;
            message = "invalid email";
        }

        return ResponseEntity.status(statusCode).body(new UserManagementResult(message));
    }

    @GetMapping("/signin")
    public ResponseEntity<UserManagementResult> userSignIn(@RequestBody UserSignIn user) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }

        Pattern pattern = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
        return pattern.matcher(email).matches();
    }

    public record UserManagementResult(String message) {
    }

    public record UserSignUp(String name, String email, String password) {
    }

    public record UserSignIn(String email, String password) {
    }
}
