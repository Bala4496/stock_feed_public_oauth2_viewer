package ua.bala.stock_feed_public_oauth2_viewer.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class TokenUtilTest {

    private static final String TEST_EMAIL = "test.account@gmail.com";
    private static final String TEST_PASSWORD = "password";

    @Autowired
    private TokenUtil tokenUtil;

    @Test
    void shouldGenerateToken() {
        var token = tokenUtil.generateToken(TEST_EMAIL);

        assertNotNull(token);
        assertTrue(token.length() > 1);
    }

    @Test
    void shouldValidateToken() {
        var token = tokenUtil.generateToken(TEST_EMAIL);
        var userDetails = new org.springframework.security.core.userdetails.User(TEST_EMAIL, TEST_PASSWORD, AuthorityUtils.NO_AUTHORITIES);

        boolean isValid = tokenUtil.validateToken(userDetails, token);

        assertTrue(isValid);
    }

    @Test
    void shouldExtractSubject() {
        var token = tokenUtil.generateToken(TEST_EMAIL);

        var subject = tokenUtil.extractSubject(token);

        assertEquals(TEST_EMAIL, subject);
    }
}
