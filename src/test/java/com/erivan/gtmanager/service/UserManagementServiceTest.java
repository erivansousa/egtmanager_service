package com.erivan.gtmanager.service;

import com.erivan.gtmanager.data.UserRepository;
import com.erivan.gtmanager.data.entity.User;
import com.erivan.gtmanager.dto.UserSignIn;
import com.erivan.gtmanager.dto.UserSignUp;
import com.erivan.gtmanager.error.UserAccountException;
import com.erivan.gtmanager.security.CryptoUtil;
import com.erivan.gtmanager.security.JWTUtil;
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
    public JWTUtil jwtUtil;

    @BeforeEach
    void setUp() {
        userRepo = mock(UserRepository.class);
        cryptoUtil = mock(CryptoUtil.class);
        jwtUtil = mock(JWTUtil.class);
        service = new UserManagementService(userRepo, cryptoUtil, jwtUtil);
    }

    @Test
    @DisplayName("when call the createUser method, should insert a new user on database by calling the userRepository")
    void createUserMethodShouldInsertANewUserOnDatabase() {
        var user = new UserSignUp("new user", "validemail@gmail.com", "password");

        Assertions.assertDoesNotThrow(() -> service.createUser(user));

        Mockito.verify(userRepo).save(any());
    }

    @Test
    @DisplayName("when creating an user should check if already exists other user with the same email")
    void createUserShouldCheckIfTheEmailAlreadyExists() {

        var user = new UserSignUp("new user", "validemail@gmail.com", "password");
        //when(userRepo.findByEmail(any())).thenReturn(null);

        Assertions.assertDoesNotThrow(() -> service.createUser(user));

        Mockito.verify(userRepo).findByEmail(eq(user.email()));
    }

    @Test
    @DisplayName("when creating an user if user email already exists should throw an UserAccountException")
    void createUserShouldThrowAUserAccountExceptionIfTheEmailAlreadyExists() {

        var user = new UserSignUp("new user", "validemail@gmail.com", "password");
        when(userRepo.findByEmail(any())).thenReturn(mock(User.class));

        Assertions.assertThrows(UserAccountException.class, () -> service.createUser(user));

        Mockito.verify(userRepo).findByEmail(eq(user.email()));
    }

    @Test
    @DisplayName("when creating an user should encrypt its password")
    void createUserMethodShouldEncrypuserPassword() {
        var user = new UserSignUp("new user", "validemail@gmail.com", "password");

        Assertions.assertDoesNotThrow(() -> service.createUser(user));

        Mockito.verify(cryptoUtil).encrypt(eq(user.password()));
    }

    @Test
    @DisplayName("when sing in, if email is null or empty, should throw a UserAccountException")
    void whenSignInIfNullEmailShouldThrowUserAccountException() {
        var userCredential = new UserSignIn(null, "blabla");

        Assertions.assertThrows(UserAccountException.class, () -> {
            service.userSignIn(userCredential);
        });
    }

    @Test
    @DisplayName("when sing in, if email is empty, should throw a UserAccountException")
    void whenSIgnInIfEmptyEmailShouldThrowUserAccountException() {
        var userCredential = new UserSignIn("  ", "blabla");
        Assertions.assertThrows(UserAccountException.class, () -> {
            service.userSignIn(userCredential);
        });
    }

    @Test
    @DisplayName("when sing in, if password is null, should throw a UserAccountException")
    void whenSIgnInIfNullPasswordShouldThrowUserAccountException() {
        var userCredential = new UserSignIn("blabla", null);
        Assertions.assertThrows(UserAccountException.class, () -> {
            service.userSignIn(userCredential);
        });
    }

    @Test
    @DisplayName("when sing in, if password is empty, should throw a UserAccountException")
    void whenSIgnInIfEmptyPasswordShouldThrowUserAccountException() {
        var userCredential = new UserSignIn("blabla", "   ");
        Assertions.assertThrows(UserAccountException.class, () -> {
            service.userSignIn(userCredential);
        });
    }

    @Test
    @DisplayName("when sing in, should retrieve user from database by email")
    void whenSIgnInShouldRetrieveUserByEmail() {
        var userCredential = new UserSignIn("blablabla@email.com", "blablabla");
        var dbuser = mock(User.class);

        Mockito.when(dbuser.getPassword()).thenReturn("asjldfhalasdf");
        Mockito.when(userRepo.findByEmail(userCredential.email())).thenReturn(dbuser);
        Mockito.when(cryptoUtil.encrypt(userCredential.password())).thenReturn("asjldfhalasdf");

        service.userSignIn(userCredential);

        Mockito.verify(userRepo).findByEmail("blablabla@email.com");
    }

    @Test
    @DisplayName("when sing in, if it could not find the user should throw an UserAccountException")
    void whenSIgnInIfCouldNotFindUserByEmailShouldThrowUserAccountExceptionInvalidCredential() {
        var userCredential = new UserSignIn("blablabla@email.com", "blablabla");

        Mockito.when(userRepo.findByEmail(userCredential.email())).thenReturn(null);

        Assertions.assertThrows(UserAccountException.class, () -> {
            service.userSignIn(userCredential);
        });
    }

    @Test
    @DisplayName("when sing in, should encrypt the credentials email")
    void whenSIgnInShouldEncryptUserCredentialsPassword() {
        var userCredential = new UserSignIn("blablabla@email.com", "blablabla");
        var dbuser = mock(User.class);

        Mockito.when(dbuser.getPassword()).thenReturn("asjldfhalasdf");
        Mockito.when(userRepo.findByEmail(userCredential.email())).thenReturn(dbuser);
        Mockito.when(cryptoUtil.encrypt(userCredential.password())).thenReturn("asjldfhalasdf");

        service.userSignIn(userCredential);

        Mockito.verify(cryptoUtil).encrypt(userCredential.password());
    }

    @Test
    @DisplayName("when sign in, if the encrypted credential password do not match the user password on database, should throw a UserAccountException")
    void whenSignInIfTheCredentialsPasswordDoNotMatchWithUserInDatabaseShouldThrowUserAccountException() {
        var userCredential = new UserSignIn("blablabla@email.com", "blablabla");
        var dbuser = mock(User.class);

        Mockito.when(dbuser.getPassword()).thenReturn("asjldfhalasdf");
        Mockito.when(userRepo.findByEmail(userCredential.email())).thenReturn(dbuser);
        Mockito.when(cryptoUtil.encrypt(userCredential.password())).thenReturn("alisghjdfiofg");

        Assertions.assertThrows(UserAccountException.class, () -> {
            service.userSignIn(userCredential);
        });
    }

    @Test
    @DisplayName("when sign in, if the encrypted credential password do match the user password on database, create a jwt token")
    void whenSignInIfTheCredentialsPasswordMatchWithUserInDatabaseShouldCreateJWTToken() {
        var userCredential = new UserSignIn("blablabla@email.com", "blablabla");
        var dbuser = mock(User.class);

        Mockito.when(dbuser.getPassword()).thenReturn("asjldfhalasdf");
        Mockito.when(dbuser.getId()).thenReturn("515151515");
        Mockito.when(dbuser.getName()).thenReturn("nananan nananana");
        Mockito.when(userRepo.findByEmail(userCredential.email())).thenReturn(dbuser);
        Mockito.when(cryptoUtil.encrypt(userCredential.password())).thenReturn("asjldfhalasdf");
        Mockito.when(jwtUtil.generateToken(any(), any())).thenReturn("");

        service.userSignIn(userCredential);

        Mockito.verify(jwtUtil).generateToken(dbuser.getId(),dbuser.getName());
    }

    @Test
    @DisplayName("when sign in, if the encrypted credential password do match the user password on database, create a jwt refresh token")
    void whenSignInIfTheCredentialsPasswordMatchWithUserInDatabaseShouldCreateJWTRefreshToken() {
        var userCredential = new UserSignIn("blablabla@email.com", "blablabla");
        var dbuser = mock(User.class);

        Mockito.when(dbuser.getPassword()).thenReturn("asjldfhalasdf");
        Mockito.when(dbuser.getId()).thenReturn("515151515");
        Mockito.when(dbuser.getName()).thenReturn("nananan nananana");
        Mockito.when(userRepo.findByEmail(userCredential.email())).thenReturn(dbuser);
        Mockito.when(cryptoUtil.encrypt(userCredential.password())).thenReturn("asjldfhalasdf");
        Mockito.when(jwtUtil.generateToken(any(), any())).thenReturn("");
        Mockito.when(jwtUtil.generateRefreshToken(any(), any())).thenReturn("");

        service.userSignIn(userCredential);

        Mockito.verify(jwtUtil).generateRefreshToken(dbuser.getId(),dbuser.getName());
    }

    @Test
    @DisplayName("when sign in, if the encrypted credential password do match the user password on database, should return the pair of token and refresh token created")
    void whenSignInIfTheCredentialsPasswordMatchWithUserInDatabaseShouldPairTokenRefreshToken() {
        var userCredential = new UserSignIn("blablabla@email.com", "blablabla");
        var dbuser = mock(User.class);

        Mockito.when(dbuser.getPassword()).thenReturn("asjldfhalasdf");
        Mockito.when(dbuser.getId()).thenReturn("515151515");
        Mockito.when(dbuser.getName()).thenReturn("nananan nananana");
        Mockito.when(userRepo.findByEmail(userCredential.email())).thenReturn(dbuser);
        Mockito.when(cryptoUtil.encrypt(userCredential.password())).thenReturn("asjldfhalasdf");
        Mockito.when(jwtUtil.generateToken(any(), any())).thenReturn("token");
        Mockito.when(jwtUtil.generateRefreshToken(any(), any())).thenReturn("refresh");

        var result = service.userSignIn(userCredential);

        Assertions.assertEquals("token", result.token());
        Assertions.assertEquals("refresh", result.refreshToken());
    }

    @Test
    @DisplayName("when sign in, if the encrypted credential password do match the user password on database, should update the user with the new token pair")
    void whenSignInIfTheCredentialsPasswordMatchWithUserInDatabaseShouldUpdateTheUser() {
        var userCredential = new UserSignIn("blablabla@email.com", "blablabla");
        var dbuser = mock(User.class);

        Mockito.when(dbuser.getPassword()).thenReturn("asjldfhalasdf");
        Mockito.when(dbuser.getId()).thenReturn("515151515");
        Mockito.when(dbuser.getName()).thenReturn("nananan nananana");
        Mockito.when(userRepo.findByEmail(userCredential.email())).thenReturn(dbuser);
        Mockito.when(cryptoUtil.encrypt(userCredential.password())).thenReturn("asjldfhalasdf");
        Mockito.when(jwtUtil.generateToken(any(), any())).thenReturn("token");
        Mockito.when(jwtUtil.generateRefreshToken(any(), any())).thenReturn("refresh");

        service.userSignIn(userCredential);

        Mockito.verify(userRepo).save(any(User.class));
    }
}
