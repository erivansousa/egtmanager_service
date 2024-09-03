package com.erivan.gtmanager.controller;

import com.erivan.gtmanager.controller.UserManagementController.UserSignUp;
import com.erivan.gtmanager.error.UserAccountException;
import com.erivan.gtmanager.service.UserManagementService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class UserManagementControllerTest {

    public UserManagementController userManagementController;
    public UserManagementService userManagementService;

    @BeforeEach
    void setUp() {
        userManagementService = mock(UserManagementService.class);
        userManagementController = new UserManagementController(userManagementService);
    }

    @Test
    @DisplayName("create new user with valid email and password should execute without errors and return status code 201")
    public void createUserValidUsernameAndPassword() {

        var user = new UserSignUp("valid", "validemail@gmail.com", "password");

        ResponseEntity<UserManagementController.UserManagementResult> result = userManagementController.createUser(user);

        assertEquals(201, result.getStatusCode().value());
    }

    @Test
    @DisplayName("create new user with invalid email return a http error 400")
    public void createUserInvalidEmail() {
        var user = new UserSignUp("invalid", "validemail@g", "password");

        ResponseEntity<UserManagementController.UserManagementResult> result = userManagementController.createUser(user);

        assertEquals(400, result.getStatusCode().value());
    }

    @Test
    @DisplayName("create new user should call the service")
    public void createUserWithValidEmailAndPassword() {
        var user = new UserSignUp("new user", "validemail@gmail.com", "password");

        userManagementController.createUser(user);

        try {
            Mockito.verify(userManagementService).createUser(user);
        } catch (UserAccountException e) {
            Assertions.assertNull(e);
        }

    }
}
