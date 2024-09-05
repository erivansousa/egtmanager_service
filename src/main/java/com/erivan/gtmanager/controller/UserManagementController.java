package com.erivan.gtmanager.controller;

import com.erivan.gtmanager.dto.TokenPair;
import com.erivan.gtmanager.dto.UserManagementResult;
import com.erivan.gtmanager.dto.UserSignIn;
import com.erivan.gtmanager.dto.UserSignUp;
import com.erivan.gtmanager.error.UserAccountException;
import com.erivan.gtmanager.service.UserManagementService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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

        logger.info("validating new user information");
        if (isValidEmail(user.email())) {
            try {
                logger.info("start creating new user");
                service.createUser(user);
            } catch (UserAccountException e) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid email");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new UserManagementResult("created"));
    }

    @GetMapping("/signin")
    public ResponseEntity<TokenPair> userSignIn(@RequestBody UserSignIn user) {

        TokenPair pair;
        try {
            pair = service.userSignIn(user);
        } catch (UserAccountException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.OK).body(pair);
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }

        Pattern pattern = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
        return pattern.matcher(email).matches();
    }

}
