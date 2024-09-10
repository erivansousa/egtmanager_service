package com.erivan.gtmanager.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CryptoUtilTest {

    public CryptoUtil cryptoUtil;

    @BeforeEach
    void setUp() {
        cryptoUtil = new CryptoUtil("502b8df037bb4861a16c792f8d3189b4");
    }

    @Test
    @DisplayName("encrypt test")
    void testEncrypt() {
        String encrypt = cryptoUtil.encrypt("encrypt");

        assertNotNull(encrypt);
    }
}