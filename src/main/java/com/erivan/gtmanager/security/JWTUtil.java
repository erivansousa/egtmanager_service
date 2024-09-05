package com.erivan.gtmanager.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

@Component
public class JWTUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration.token}")
    private Integer expirationTime; //seconds

    @Value("${jwt.expiration.refresh-token}")
    private Integer refreshExpirationTime; //seconds

    public String generateToken(String userId, String username) {
        Algorithm algorithm = Algorithm.HMAC512(secretKey);

        var calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, expirationTime);

        return JWT.create()
                .withSubject(username)
                .withExpiresAt(calendar.getTime())
                .withIssuedAt(new Date())
                .withClaim("typ", "t")
                .withClaim("nme", username)
                .withClaim("uid", userId)
                .withIssuer("egtm")
                .sign(algorithm);
    }

    public String generateRefreshToken(String userId, String username) {
        Algorithm algorithm = Algorithm.HMAC512(secretKey);

        var calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, refreshExpirationTime);

        return JWT.create()
                .withSubject(username)
                .withExpiresAt(calendar.getTime())
                .withIssuedAt(new Date())
                .withClaim("typ", "r")
                .withClaim("uid", userId)
                .withIssuer("egtm")
                .sign(algorithm);
    }

    public boolean validateToken(String token) {
        DecodedJWT decodedJWT;
        try {
            Algorithm algorithm = Algorithm.HMAC512(secretKey);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("egtm")
                    .build();

            decodedJWT = verifier.verify(token);
        } catch (JWTVerificationException exception) {
            return false;
        }
        return true;
    }

}
