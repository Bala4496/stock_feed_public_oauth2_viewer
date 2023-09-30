package ua.bala.stock_feed_public_oauth2_viewer.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import reactor.test.StepVerifier;
import ua.bala.stock_feed_public_oauth2_viewer.dto.RegisterUserDTO;
import ua.bala.stock_feed_public_oauth2_viewer.dto.UserDTO;
import ua.bala.stock_feed_public_oauth2_viewer.model.entity.Token;
import ua.bala.stock_feed_public_oauth2_viewer.model.entity.User;
import ua.bala.stock_feed_public_oauth2_viewer.model.enums.Provider;
import ua.bala.stock_feed_public_oauth2_viewer.model.enums.TokenType;
import ua.bala.stock_feed_public_oauth2_viewer.model.enums.UserRole;
import ua.bala.stock_feed_public_oauth2_viewer.repository.TokenRepository;
import ua.bala.stock_feed_public_oauth2_viewer.repository.UserRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

class RegisterControllerV1Test extends BaseIntegrationTest {

    @MockBean
    private JavaMailSender mailSender;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenRepository tokenRepository;

    @AfterEach
    void afterEach() {
        userRepository.deleteAll().block();
        tokenRepository.deleteAll().block();
    }

    @Test
    void shouldRegisterUser() {
        var registerUserDTO = new RegisterUserDTO().setEmail(TEST_EMAIL).setPassword(TEST_PASSWORD);

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        var result = webClient.post()
                .uri("/api/v1/register")
                .bodyValue(registerUserDTO)
                .exchange()
                .expectStatus().isOk()
                .returnResult(RegisterUserDTO.class);

        StepVerifier.create(result.getResponseBody())
                .consumeNextWith(response -> {
                    assertEquals(response.getEmail(), TEST_EMAIL);
                    assertNull(response.getPassword());
                    assertEquals(response.getRole(), UserRole.ROLE_USER);
                    assertFalse(response.isEnabled());
                })
                .verifyComplete();
    }

    @Test
    void shouldVerifyEmail() {
        var testUser = userRepository.save(getTestUser()).block();
        var testToken = tokenRepository.save(getTestRegisterToken(testUser.getId())).block();

        var result = webClient.get()
                .uri("/api/v1/register/verify?token={token}", testToken.getToken())
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

    private static User getTestUser() {
        return new User()
                .setEmail(TEST_EMAIL)
                .setPassword(TEST_PASSWORD)
                .setRole(UserRole.ROLE_USER)
                .setProvider(Provider.LOCAL);
    }

    private static Token getTestRegisterToken(long userId) {
        var now = LocalDateTime.now();
        return new Token().setUserId(userId)
                .setToken(TEST_TOKEN)
                .setType(TokenType.REGISTER)
                .setCreatedAt(now)
                .setExpireAt(now.plusMinutes(15000));
    }
}
