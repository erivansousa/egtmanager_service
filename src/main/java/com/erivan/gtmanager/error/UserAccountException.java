package com.erivan.gtmanager.error;

public class UserAccountException extends RuntimeException {
    public final AccountErrorType type;
    public final String message;

    public enum AccountErrorType {
        INVALID_CREDENTIALS, ACCOUNT_ALREADY_EXISTS
    }
    public UserAccountException(AccountErrorType type, String message) {
        super(String.format("type: %s, message: %s", type, message));
        this.type = type;
        this.message = message;
    }
}
