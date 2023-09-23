package ua.bala.stock_feed_public_oauth2_viewer.controller;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.StringUtils;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;
import ua.bala.stock_feed_public_oauth2_viewer.config.WebFluxSecurityConfig;
import ua.bala.stock_feed_public_oauth2_viewer.model.entity.User;
import ua.bala.stock_feed_public_oauth2_viewer.model.enums.Provider;
import ua.bala.stock_feed_public_oauth2_viewer.model.enums.UserRole;
import ua.bala.stock_feed_public_oauth2_viewer.model.request.AuthRequest;
import ua.bala.stock_feed_public_oauth2_viewer.model.response.AuthResponse;
import ua.bala.stock_feed_public_oauth2_viewer.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@Import(WebFluxSecurityConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerV1Test {

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
    @Autowired
    private WebTestClient webClient;
    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldLoginByEmail() {
        var user = userRepository.save(getTestUser()).block();

        var authRequest = new AuthRequest().setEmail(user.getEmail()).setPassword(user.getPassword());

        var result = webClient.post()
                .uri("/api/v1/auth/login")
                .bodyValue(authRequest)
                .exchange()
                .expectStatus().isOk()
                .returnResult(AuthResponse.class);

        StepVerifier.create(result.getResponseBody())
                .consumeNextWith(authResponse -> assertTrue(StringUtils.hasText(authResponse.getToken())))
                .verifyComplete();
    }

    private static User getTestUser() {
        return new User()
                .setEmail(TEST_EMAIL)
                .setPassword(TEST_PASSWORD)
                .setRole(UserRole.ROLE_USER)
                .setProvider(Provider.LOCAL)
                .setEnabled(true);
    }
}
