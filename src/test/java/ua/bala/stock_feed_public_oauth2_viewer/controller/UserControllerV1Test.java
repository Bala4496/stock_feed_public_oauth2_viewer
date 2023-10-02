package ua.bala.stock_feed_public_oauth2_viewer.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import reactor.test.StepVerifier;
import ua.bala.stock_feed_public_oauth2_viewer.dto.UserDTO;
import ua.bala.stock_feed_public_oauth2_viewer.model.entity.Token;
import ua.bala.stock_feed_public_oauth2_viewer.model.enums.Provider;
import ua.bala.stock_feed_public_oauth2_viewer.model.enums.TokenType;
import ua.bala.stock_feed_public_oauth2_viewer.model.enums.UserRole;
import ua.bala.stock_feed_public_oauth2_viewer.model.request.NewPasswordRequest;
import ua.bala.stock_feed_public_oauth2_viewer.model.request.ResetPasswordRequest;
import ua.bala.stock_feed_public_oauth2_viewer.repository.TokenRepository;
import ua.bala.stock_feed_public_oauth2_viewer.repository.UserRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

class UserControllerV1Test extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;
    @MockBean
    private JavaMailSender mailSender;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @AfterEach
    void afterEach() {
        userRepository.deleteAll().block();
        tokenRepository.deleteAll().block();
    }

    @Test
    void shouldResetPasswordByEmail() {
        userRepository.save(getTestUser()).block();

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        var resetPasswordDto = new ResetPasswordRequest().setEmail(TEST_EMAIL);

        var result = webClient.post()
                .uri("/api/v1/users/reset-password/initiate")
                .bodyValue(resetPasswordDto)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Void.class);

        StepVerifier.create(result.getResponseBody())
                .verifyComplete();

        StepVerifier.create(userRepository.count())
                .expectNextCount(1)
                .verifyComplete();

        StepVerifier.create(tokenRepository.count())
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void shouldSetNewPassword() {
        var testUser = userRepository.save(getTestUser().setEnabled(false)).block();
        var testToken = tokenRepository.save(getTestResetPasswordToken(testUser.getId())).block();

        System.out.println("testUser - " + testUser);
        System.out.println("testToken - " + testToken);

        var newPassword = "newPassword";
        var newPasswordDto = new NewPasswordRequest().setToken(TEST_TOKEN).setNewPassword(newPassword);

        var result = webClient.post()
                .uri("/api/v1/users/reset-password/verify")
                .bodyValue(newPasswordDto)
                .exchange()
                .expectStatus().isOk()
                .returnResult(UserDTO.class);

        StepVerifier.create(result.getResponseBody())
                .consumeNextWith(user -> {
                    assertEquals(user.getEmail(), TEST_EMAIL);
                    assertTrue(passwordEncoder.matches(newPassword, user.getPassword()));
                    assertEquals(user.getRole(), UserRole.ROLE_USER);
                    assertEquals(user.getProvider(), Provider.LOCAL);
                    assertTrue(user.isEnabled());
                })
                .verifyComplete();

        StepVerifier.create(userRepository.count())
                .expectNext(1L)
                .verifyComplete();

        StepVerifier.create(tokenRepository.count())
                .expectNext(0L)
                .verifyComplete();
    }

    @Test
    @WithMockUser(username = TEST_EMAIL)
    void shouldGetInfo() {
        userRepository.save(getTestUser()).block();

        var result = webClient.get()
                .uri("/api/v1/users/info")
                .exchange()
                .expectStatus().isOk()
                .returnResult(UserDTO.class);

        StepVerifier.create(result.getResponseBody())
                .consumeNextWith(user -> {
                    assertEquals(user.getEmail(), TEST_EMAIL);
                    assertEquals(user.getPassword(), TEST_PASSWORD);
                    assertEquals(user.getRole(), UserRole.ROLE_USER);
                    assertEquals(user.getProvider(), Provider.LOCAL);
                    assertTrue(user.isEnabled());
                })
                .verifyComplete();
    }

    protected static Token getTestResetPasswordToken(long userId) {
        var now = LocalDateTime.now();
        return new Token().setUserId(userId)
                .setToken(TEST_TOKEN)
                .setType(TokenType.RESET_PASSWORD)
                .setCreatedAt(now)
                .setExpireAt(now.plusMinutes(15000));
    }
}
