package com.erivan.gtmanager.service;

import com.erivan.gtmanager.controller.UserManagementController;
import com.erivan.gtmanager.data.UserRepository;
import com.erivan.gtmanager.data.entity.User;
import com.erivan.gtmanager.error.UserAccountException;
import com.erivan.gtmanager.security.CryptoUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserManagementServiceTest {

    public UserManagementService service;
    public UserRepository userRepo;
    public CryptoUtil cryptoUtil;

    @BeforeEach
    void setUp() {
        userRepo = mock(UserRepository.class);
        cryptoUtil = mock(CryptoUtil.class);
        service = new UserManagementService(userRepo, cryptoUtil);
    }

    @Test
    @DisplayName("whenever call the createUser method, should insert a new user on database by calling the userRepository")
    void createUserMethodShouldInsertANewUserOnDatabase() {
        var user = new UserManagementController.UserSignUp("new user", "validemail@gmail.com", "password");

        Assertions.assertDoesNotThrow(() -> service.createUser(user));

        Mockito.verify(userRepo).save(any());
    }

    @Test
    @DisplayName("whenever creating an user should check if already exists other user with the same email")
    void createUserShouldCheckIfTheEmailAlreadyExists() {

        var user = new UserManagementController.UserSignUp("new user", "validemail@gmail.com", "password");
        //when(userRepo.findByEmail(any())).thenReturn(null);

        Assertions.assertDoesNotThrow(() -> service.createUser(user));

        Mockito.verify(userRepo).findByEmail(eq(user.email()));
    }

    @Test
    @DisplayName("whenever creating an user if user email already exists should throw an UserAccountException")
    void createUserShouldThrowAUserAccountExceptionIfTheEmailAlreadyExists() {

        var user = new UserManagementController.UserSignUp("new user", "validemail@gmail.com", "password");
        when(userRepo.findByEmail(any())).thenReturn(mock(User.class));

        Assertions.assertThrows(UserAccountException.class, () -> service.createUser(user));

        Mockito.verify(userRepo).findByEmail(eq(user.email()));
    }

    @Test
    @DisplayName("whenever creating an user should encrypt its password")
    void createUserMethodShouldEncrypuserPassword() {
        var user = new UserManagementController.UserSignUp("new user", "validemail@gmail.com", "password");

        Assertions.assertDoesNotThrow(() -> service.createUser(user));

        Mockito.verify(cryptoUtil).encrypt(eq(user.password()));
    }
}
