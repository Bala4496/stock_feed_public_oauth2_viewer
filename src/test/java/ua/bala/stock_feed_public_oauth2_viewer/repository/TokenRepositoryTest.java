package ua.bala.stock_feed_public_oauth2_viewer.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ua.bala.stock_feed_public_oauth2_viewer.model.entity.Token;
import ua.bala.stock_feed_public_oauth2_viewer.model.entity.User;
import ua.bala.stock_feed_public_oauth2_viewer.model.enums.Provider;
import ua.bala.stock_feed_public_oauth2_viewer.model.enums.TokenType;
import ua.bala.stock_feed_public_oauth2_viewer.model.enums.UserRole;

import java.time.LocalDateTime;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataR2dbcTest
@ExtendWith(SpringExtension.class)
class TokenRepositoryTest {

    private static final String FIRST_NAME = "First name";
    private static final String LAST_NAME = "Last name";
    private static final String TEST_TOKEN = "super_secret_test_token";
    private static final String TEST_EMAIL = "test.account@gmail.com";
    private static final String TEST_PASSWORD = "password";
    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void shouldSaveSingleToken() {
        var setupUser = userRepository.deleteAll().then(userRepository.save(getTestUser().setEnabled(true)));
        Function<Token, Mono<Token>> setupToken = token -> tokenRepository.deleteAll().then(tokenRepository.save(token));

        var composite = setupUser
                .map(user -> getTestRegisterToken().setUserId(user.getId()))
                .flatMap(setupToken);

        StepVerifier.create(composite)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void shouldFindByToken() {
        var setupUser = userRepository.deleteAll().then(userRepository.save(getTestUser().setEnabled(true))).block();
        Function<User, Token> createToken = savedUser -> getTestRegisterToken().setUserId(savedUser.getId());
        Function<Token, Mono<Token>> setupToken = token -> tokenRepository.deleteAll().then(tokenRepository.save(token));
        var findToken = tokenRepository.findByToken(TEST_TOKEN);

        var composite = Mono.justOrEmpty(setupUser)
                .map(createToken)
                .flatMap(setupToken)
                .then(findToken);

        StepVerifier.create(composite)
                .consumeNextWith(token -> {
                    assertEquals(token.getUserId(), setupUser.getId());
                    assertEquals(token.getToken(), TEST_TOKEN);
                    assertEquals(token.getType(), TokenType.REGISTER);
                })
                .verifyComplete();
    }

    @Test
    void shouldRemoveByToken() {
        var setupUser = userRepository.deleteAll().then(userRepository.save(getTestUser().setEnabled(true)));
        Function<Token, Mono<Token>> setupToken = token -> tokenRepository.deleteAll().then(tokenRepository.save(token));
        var removeToken = tokenRepository.removeByToken(TEST_TOKEN);

        var composite = setupUser
                .map(user -> getTestRegisterToken().setUserId(user.getId()))
                .map(setupToken)
                .then(removeToken);

        StepVerifier.create(composite)
                .expectNextCount(0)
                .verifyComplete();
    }

    private Token getTestRegisterToken() {
        var now = LocalDateTime.now();
        return new Token().setUserId(1L)
                .setToken(TEST_TOKEN)
                .setType(TokenType.REGISTER)
                .setCreatedAt(now)
                .setExpireAt(now.plusMinutes(15000));
    }

    private static User getTestUser() {
        return new User()
                .setFirstName(FIRST_NAME)
                .setLastName(LAST_NAME)
                .setEmail(TEST_EMAIL)
                .setPassword(TEST_PASSWORD)
                .setRole(UserRole.ROLE_USER)
                .setProvider(Provider.LOCAL);
    }
}
