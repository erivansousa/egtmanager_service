package com.erivan.gtmanager.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;

@Component
public class CryptoUtil {

    private static final String ALGORITHM = "AES";
    private final String secretKey;

    public CryptoUtil(@Value("${crypto.secret}") String secretKey) {
        this.secretKey = secretKey;
    }

    public String encrypt(String plainText) {
        try {
            Key key = generateKey();
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private Key generateKey() throws Exception {
        return new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), ALGORITHM);
    }

}
