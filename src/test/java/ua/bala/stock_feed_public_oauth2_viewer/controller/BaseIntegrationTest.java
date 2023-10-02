package ua.bala.stock_feed_public_oauth2_viewer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import ua.bala.stock_feed_public_oauth2_viewer.TestContainerConfig;
import ua.bala.stock_feed_public_oauth2_viewer.config.WebFluxSecurityConfig;
import ua.bala.stock_feed_public_oauth2_viewer.model.entity.Token;
import ua.bala.stock_feed_public_oauth2_viewer.model.entity.User;
import ua.bala.stock_feed_public_oauth2_viewer.model.enums.Provider;
import ua.bala.stock_feed_public_oauth2_viewer.model.enums.TokenType;
import ua.bala.stock_feed_public_oauth2_viewer.model.enums.UserRole;

import java.time.LocalDateTime;

@Import(WebFluxSecurityConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TestContainerConfig.class)
public class BaseIntegrationTest {

    protected static final String FIRST_NAME = "First name";
    protected static final String LAST_NAME = "Last name";
    protected static final String TEST_EMAIL = "test.account@gmail.com";
    protected static final String TEST_PASSWORD = "password";
    protected static final String TEST_TOKEN = "super_secret_test_token";

    @Autowired
    protected WebTestClient webClient;

    protected static User getTestUser() {
        return new User()
                .setFirstName(FIRST_NAME)
                .setLastName(LAST_NAME)
                .setEmail(TEST_EMAIL)
                .setPassword(TEST_PASSWORD)
                .setRole(UserRole.ROLE_USER)
                .setProvider(Provider.LOCAL)
                .setEnabled(true);
    }
}
