package ua.bala.stock_feed_public_oauth2_viewer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import ua.bala.stock_feed_public_oauth2_viewer.TestContainerConfig;
import ua.bala.stock_feed_public_oauth2_viewer.config.WebFluxSecurityConfig;

@Import(WebFluxSecurityConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TestContainerConfig.class)
class BaseIntegrationTest {

    protected static final String TEST_EMAIL = "test.account@gmail.com";
    protected static final String TEST_PASSWORD = "password";
    protected static final String TEST_TOKEN = "super_secret_test_token";
    @Autowired
    protected WebTestClient webClient;
}
