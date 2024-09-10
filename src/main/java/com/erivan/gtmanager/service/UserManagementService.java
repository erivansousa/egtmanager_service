package com.erivan.gtmanager.service;

import com.erivan.gtmanager.data.UserRepository;
import com.erivan.gtmanager.data.entity.User;
import com.erivan.gtmanager.dto.TokenPair;
import com.erivan.gtmanager.dto.UserSignIn;
import com.erivan.gtmanager.dto.UserSignUp;
import com.erivan.gtmanager.error.UserAccountException;
import com.erivan.gtmanager.error.UserAccountException.AccountErrorType;
import com.erivan.gtmanager.security.CryptoUtil;
import com.erivan.gtmanager.security.JWTUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserManagementService {

    private final Logger logger = LogManager.getLogger();

    private final UserRepository userRepository;
    private final CryptoUtil cryptoUtil;
    private final JWTUtil jwtUtil;

    public UserManagementService(
            @Autowired UserRepository userRepo,
            @Autowired CryptoUtil cryptoUtil,
            @Autowired JWTUtil jwtUtil
    ) {
        this.userRepository = userRepo;
        this.cryptoUtil = cryptoUtil;
        this.jwtUtil = jwtUtil;
    }

    public void createUser(UserSignUp userSignUp) throws UserAccountException {

        logger.debug("creating user, check user existence");
        var existingUser = userRepository.findByEmail(userSignUp.email());
        if (existingUser != null) {
            throw new UserAccountException(AccountErrorType.ACCOUNT_ALREADY_EXISTS, "account already exists");
        }

        var user = new User(
                new ObjectId().toHexString(),
                userSignUp.name(),
                userSignUp.email(),
                cryptoUtil.encrypt(userSignUp.password()),
                LocalDateTime.now(),
                null
        );

        logger.debug("creating user, saving new user on database");
        this.userRepository.save(user);
    }

    public TokenPair userSignIn(UserSignIn user) throws UserAccountException {

        if (isNullOrEmpty(user.email()) || isNullOrEmpty(user.password())) {
            throw new UserAccountException(AccountErrorType.INVALID_CREDENTIALS, "invalid credentials provided");
        }

        var repoUser = userRepository.findByEmail(user.email());
        if (repoUser == null) {
            throw new UserAccountException(AccountErrorType.INVALID_CREDENTIALS, "invalid credentials provided");
        }

        var encryptedCredential = cryptoUtil.encrypt(user.password());
        if (!encryptedCredential.equals(repoUser.getPassword())) {
            throw new UserAccountException(AccountErrorType.INVALID_CREDENTIALS, "invalid credentials provided");
        }

        var token = jwtUtil.generateToken(repoUser.getId(), repoUser.getName());
        var refreshToken = jwtUtil.generateRefreshToken(repoUser.getId(), repoUser.getName());

        //should save the pair on the repoUser
        repoUser.setLastToken(new User.AccessToken(token, refreshToken));

        userRepository.save(repoUser);

        return new TokenPair(token, refreshToken);
    }

    public void userLogOut(String userId) {
        var optUser = userRepository.findById(userId);
        if (optUser.isPresent()) {
            var user = optUser.get();
            user.getLastToken().setValid(false);
            userRepository.save(user);
        }
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.isBlank();
    }

}
