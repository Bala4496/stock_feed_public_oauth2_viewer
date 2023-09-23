package ua.bala.stock_feed_public_oauth2_viewer.service.email;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ua.bala.stock_feed_public_oauth2_viewer.model.entity.Token;
import ua.bala.stock_feed_public_oauth2_viewer.model.entity.User;
import ua.bala.stock_feed_public_oauth2_viewer.model.enums.Provider;
import ua.bala.stock_feed_public_oauth2_viewer.model.enums.TokenType;
import ua.bala.stock_feed_public_oauth2_viewer.model.enums.UserRole;
import ua.bala.stock_feed_public_oauth2_viewer.repository.TokenRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class TokenServiceTest {

    private static final String TEST_EMAIL = "test.account@gmail.com";
    private static final String TEST_PASSWORD = "password";
    private static final String TEST_TOKEN = "super_secret_test_token";

    @InjectMocks
    private TokenServiceImpl tokenService;
    @Mock
    private TokenRepository tokenRepository;

    @Test
    void createRegisterToken() {

        var user = getTestUser();
        when(tokenRepository.save(any(Token.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0, Token.class)));

        var result = tokenService.createRegisterToken(user);

        StepVerifier.create(result)
                .consumeNextWith(token -> {
                    assertEquals(token.getType(), TokenType.REGISTER);
                    assertTrue(StringUtils.hasText(token.getToken()));
                })
                .verifyComplete();

        verify(tokenRepository).save(any(Token.class));
    }

    @Test
    void createResetPasswordToken() {

        var user = getTestUser();
        when(tokenRepository.save(any(Token.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0, Token.class)));

        var result = tokenService.createResetPasswordToken(user);

        StepVerifier.create(result)
                .consumeNextWith(token -> {
                    assertEquals(token.getType(), TokenType.RESET_PASSWORD);
                    assertTrue(StringUtils.hasText(token.getToken()));
                })
                .verifyComplete();

        verify(tokenRepository).save(any(Token.class));
    }

    @Test
    void validateRegisterTokenAndEditUser() {
        var testToken = getTestRegisterToken();
        var testUser = getTestUser();
        when(tokenRepository.findByToken(TEST_TOKEN)).thenReturn(Mono.just(testToken));
        when(tokenRepository.removeByToken(TEST_TOKEN)).thenReturn(Mono.empty());

        var result = tokenService.validateRegisterTokenAndEditUser(TEST_TOKEN, id -> Mono.just(testUser));

        StepVerifier.create(result)
                .consumeNextWith(user -> {
                    assertEquals(user.getId(), testUser.getId());
                    assertEquals(user.getEmail(), testUser.getEmail());
                    assertEquals(user.getPassword(), testUser.getPassword());
                    assertEquals(user.getRole(), testUser.getRole());
                    assertEquals(user.getProvider(), testUser.getProvider());
                })
                .verifyComplete();

        verify(tokenRepository).findByToken(anyString());
    }

    @Test
    void validateResetPasswordTokenAndEditUser() {
        var testToken = getTestResetPasswordToken();
        var testUser = getTestUser();
        when(tokenRepository.findByToken(TEST_TOKEN)).thenReturn(Mono.just(testToken));
        when(tokenRepository.removeByToken(TEST_TOKEN)).thenReturn(Mono.empty());

        var result = tokenService.validateResetPasswordTokenAndEditUser(TEST_TOKEN, id -> Mono.just(testUser));

        StepVerifier.create(result)
                .consumeNextWith(user -> {
                    assertEquals(user.getId(), testUser.getId());
                    assertEquals(user.getEmail(), testUser.getEmail());
                    assertEquals(user.getPassword(), testUser.getPassword());
                    assertEquals(user.getRole(), testUser.getRole());
                    assertEquals(user.getProvider(), testUser.getProvider());
                })
                .verifyComplete();

        verify(tokenRepository).findByToken(anyString());
    }

    private static Token getTestRegisterToken() {
        return createTestToken(TokenType.REGISTER);
    }

    private static Token getTestResetPasswordToken() {
        return createTestToken(TokenType.RESET_PASSWORD);
    }

    private static Token createTestToken(TokenType tokenType) {
        var now = LocalDateTime.now();
        return new Token().setUserId(1L)
                .setToken(TEST_TOKEN)
                .setType(tokenType)
                .setCreatedAt(now)
                .setExpireAt(now.plusMinutes(15000));
    }

    private static User getTestUser() {
        return new User().setId(1L)
                .setEmail(TEST_EMAIL)
                .setPassword(TEST_PASSWORD)
                .setRole(UserRole.ROLE_USER)
                .setProvider(Provider.LOCAL);
    }
}
