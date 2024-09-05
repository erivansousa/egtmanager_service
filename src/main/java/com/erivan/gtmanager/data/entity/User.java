package com.erivan.gtmanager.data.entity;


import org.bson.BsonType;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonRepresentation;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;

/**
 * @param id       could be generated using new ObjectId().toHexString();
 * @param name
 * @param email
 * @param password
 */
public class User {
    @BsonId()
    @BsonRepresentation(BsonType.OBJECT_ID)
    String id;
    String name;
    @Indexed(unique = true)
    String email;
    String password;
    LocalDateTime createdAt;
    AccessToken lastToken;

    public User(){}
    public User(
            String id,
            String name,
            String email,
            String password,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.createdAt = createdAt;
    }

    public User(
            String id,
            String name,
            String email,
            String password,
            LocalDateTime createdAt,
            AccessToken lastToken
    ) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.createdAt = createdAt;
        this.lastToken = lastToken;
    }

    public static class AccessToken {
        String token;
        String refreshToken;
        boolean isValid;

        public AccessToken(
                String token,
                String refreshToken
        ) {
            this.token = token;
            this.refreshToken = refreshToken;
            this.isValid = true;
        }

        public boolean isValid() {
            return isValid;
        }

        public void setValid(boolean valid) {
            isValid = valid;
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public AccessToken getLastToken() {
        return lastToken;
    }

    public void setLastToken(AccessToken lastToken) {
        this.lastToken = lastToken;
    }
}
