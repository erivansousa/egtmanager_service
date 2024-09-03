package com.erivan.gtmanager.service;

import com.erivan.gtmanager.controller.UserManagementController.UserSignUp;
import com.erivan.gtmanager.data.UserRepository;
import com.erivan.gtmanager.data.entity.User;
import com.erivan.gtmanager.error.UserAccountException;
import com.erivan.gtmanager.error.UserAccountException.AccountErrorType;
import com.erivan.gtmanager.security.CryptoUtil;
import io.micrometer.observation.GlobalObservationConvention;
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

    public UserManagementService(
            @Autowired UserRepository userRepo,
            @Autowired CryptoUtil cryptoUtil
    ) {
        this.userRepository = userRepo;
        this.cryptoUtil = cryptoUtil;
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
}
