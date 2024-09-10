package com.erivan.gtmanager.controller;

import com.erivan.gtmanager.dto.TokenPair;
import com.erivan.gtmanager.dto.UserManagementResult;
import com.erivan.gtmanager.dto.UserSignIn;
import com.erivan.gtmanager.dto.UserSignUp;
import com.erivan.gtmanager.error.UserAccountException;
import com.erivan.gtmanager.error.UserAccountException.AccountErrorType;
import com.erivan.gtmanager.security.UserAuth;
import com.erivan.gtmanager.service.UserManagementService;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserManagementControllerTest {

    public UserManagementController userManagementController;
    public UserManagementService userManagementService;

    private SecurityContext context;

    @BeforeEach
    void setUp() {
        context = SecurityContextHolder.createEmptyContext();
        SecurityContextHolder.setContext(context);

        userManagementService = mock(UserManagementService.class);
        userManagementController = new UserManagementController(userManagementService);
    }

    @AfterEach
    void cleanUp() {
        SecurityContextHolder.clearContext();
        context = null;
    }

    @Test
    @DisplayName("when receive a create new user request with valid email and password should execute without errors and return status code 201")
    public void createUserValidUsernameAndPassword() {

        var user = new UserSignUp("valid", "validemail@gmail.com", "password");

        ResponseEntity<UserManagementResult> result = userManagementController.createUser(user);

        assertEquals(201, result.getStatusCode().value());
    }

    @Test
    @DisplayName("when receive a create new user request with invalid email return a http error 400")
    public void createUserInvalidEmail() {
        var user = new UserSignUp("invalid", "validemail@g", "password");

        Assertions.assertThrows(ResponseStatusException.class, () -> userManagementController.createUser(user));
    }

    @Test
    @DisplayName("when receive a create new user request should call the service")
    public void createUserWithValidEmailAndPassword() {
        var user = new UserSignUp("new user", "validemail@gmail.com", "password");

        userManagementController.createUser(user);

        try {
            verify(userManagementService).createUser(user);
        } catch (UserAccountException e) {
            Assertions.assertNull(e);
        }
    }

    @Test
    @DisplayName("when receive a sign in request should call the service sign in method")
    void signInCallShouldCallServiceSignIn() {
        var user = new UserSignIn("validemail@gmail.com", "password");

        userManagementController.userSignIn(user);

        verify(userManagementService).userSignIn(user);
    }

    @Test
    @DisplayName("when receive a sign in request if authenticate successfully should return status 200")
    void signInCallCallServiceSignInShouldReturnStatusCode200() {
        var user = new UserSignIn("validemail@gmail.com", "password");

        when(userManagementService.userSignIn(any(UserSignIn.class))).thenReturn(mock(TokenPair.class));

        var result = userManagementController.userSignIn(user);

        assertEquals(200, result.getStatusCode().value());
    }

    @Test
    @DisplayName("when receive a sign in request, if authenticate successfully should return the result of the service call")
    void signInCallCallServiceSignInShouldReturnServiceCallResult() {
        var user = new UserSignIn("validemail@gmail.com", "password");
        var tokenPair = new TokenPair("token", "refreshToken");

        when(userManagementService.userSignIn(any(UserSignIn.class))).thenReturn(tokenPair);

        var result = userManagementController.userSignIn(user);

        assertEquals(tokenPair, Objects.requireNonNull(result.getBody()));
    }

    @Test
    @DisplayName("when receive a sign in request, if authenticate fail with UserAccountException, should return a 403 status")
    void signInCallCallServiceSignInFailShouldReturn403Status() {
        var user = new UserSignIn("validemail@gmail.com", "password");

        Mockito.when(userManagementService.userSignIn(any(UserSignIn.class))).thenThrow(new UserAccountException(AccountErrorType.INVALID_CREDENTIALS, ""));

        Assertions.assertThrows(ResponseStatusException.class, () -> userManagementController.userSignIn(user));
    }

    @Test
    @DisplayName("when receive a logout request, should call the service")
    void logoutRequestShouldCallService() {

        Authentication authentication = mock(Authentication.class);
        context.setAuthentication(authentication);

        // Stub mocks
        when(authentication.getPrincipal()).thenReturn(new UserAuth("12334", "blabla", "t"));
        when(userManagementService.userSignIn(any(UserSignIn.class))).thenThrow(new UserAccountException(AccountErrorType.INVALID_CREDENTIALS, ""));

        userManagementController.userLogOut();

        verify(userManagementService).userLogOut("12334");
    }

}
