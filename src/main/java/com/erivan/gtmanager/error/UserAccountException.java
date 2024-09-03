package com.erivan.gtmanager.error;

public class UserAccountException extends Throwable {
    private AccountErrorType type;
    private String message;

    public enum AccountErrorType {
        ACCOUNT_ALREADY_EXISTS
    }
    public UserAccountException(AccountErrorType type, String message) {
        super(String.format("type: %s, message: %s", type, message));
        this.type = type;
        this.message = message;
    }
}
