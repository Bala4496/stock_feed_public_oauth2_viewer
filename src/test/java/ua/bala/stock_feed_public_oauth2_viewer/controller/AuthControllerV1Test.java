package ua.bala.stock_feed_public_oauth2_viewer.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import reactor.test.StepVerifier;
import ua.bala.stock_feed_public_oauth2_viewer.model.entity.User;
import ua.bala.stock_feed_public_oauth2_viewer.model.enums.Provider;
import ua.bala.stock_feed_public_oauth2_viewer.model.enums.UserRole;
import ua.bala.stock_feed_public_oauth2_viewer.model.request.AuthRequest;
import ua.bala.stock_feed_public_oauth2_viewer.model.response.AuthResponse;
import ua.bala.stock_feed_public_oauth2_viewer.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthControllerV1Test extends BaseIntegrationTest {

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
