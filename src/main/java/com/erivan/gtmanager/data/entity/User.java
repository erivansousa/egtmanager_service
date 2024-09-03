package com.erivan.gtmanager.data.entity;


import org.bson.BsonType;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonRepresentation;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;

/**
 *
 * @param id could be generated using new ObjectId().toHexString();
 * @param name
 * @param email
 * @param password
 */
public record User(
        @BsonId()
        @BsonRepresentation(BsonType.OBJECT_ID)
        String id,
        String name,
        @Indexed(unique = true)
        String email,
        String password,
        LocalDateTime createdAt,
        AccessToken lastToken
) {
    public static class AccessToken {
        String token;
        String refreshToken;
        boolean isValid;
    }
}
