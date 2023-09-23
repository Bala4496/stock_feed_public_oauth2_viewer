package ua.bala.stock_feed_public_oauth2_viewer.controller;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;
import ua.bala.stock_feed_public_oauth2_viewer.config.WebFluxSecurityConfig;
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

@Testcontainers
@Import(WebFluxSecurityConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RegisterControllerV1Test {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () -> "r2dbc:postgresql://%s:%d/%s".formatted(
                postgres.getHost(),
                postgres.getFirstMappedPort(),
                postgres.getDatabaseName())
        );
        registry.add("spring.r2dbc.username", postgres::getUsername);
        registry.add("spring.r2dbc.password", postgres::getPassword);

        registry.add("spring.flyway.url", postgres::getJdbcUrl);
        registry.add("spring.flyway.user", postgres::getUsername);
        registry.add("spring.flyway.password", postgres::getPassword);
    }

    @BeforeAll
    static void startContainers() {
        postgres.start();
    }

    @AfterAll
    static void stopContainers() {
        postgres.stop();
    }

    private static final String TEST_EMAIL = "test.account@gmail.com";
    private static final String TEST_PASSWORD = "password";
    private static final String TEST_TOKEN = "super_secret_test_token";
    @Autowired
    private WebTestClient webClient;
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
