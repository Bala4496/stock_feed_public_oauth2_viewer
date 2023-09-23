package ua.bala.stock_feed_public_oauth2_viewer.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ua.bala.stock_feed_public_oauth2_viewer.model.entity.User;
import ua.bala.stock_feed_public_oauth2_viewer.model.enums.Provider;
import ua.bala.stock_feed_public_oauth2_viewer.model.enums.UserRole;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataR2dbcTest
@ExtendWith(SpringExtension.class)
class UserRepositoryTest {

    private static final String TEST_EMAIL = "test.account@gmail.com";
    private static final String TEST_PASSWORD = "password";
    @Autowired
    private UserRepository repository;

    @Test
    public void shouldSaveSingleUser() {
        var setup = repository.deleteAll().thenMany(repository.save(getTestUser()));

        StepVerifier.create(setup)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void shouldFindByEmail() {
        var setup = repository.deleteAll().then(repository.save(getTestUser()));
        var find = repository.findByEmail(TEST_EMAIL);

        var composite = Mono.from(setup).then(find);

        StepVerifier.create(composite)
                .consumeNextWith(account -> {
                    assertEquals(account.getEmail(), TEST_EMAIL);
                    assertEquals(account.getPassword(), TEST_PASSWORD);
                })
                .verifyComplete();
    }

    @Test
    void shouldFindByEmailAndEnabledTrue() {
        var setup = repository.deleteAll().then(repository.save(getTestUser().setEnabled(true)));
        var find = repository.findByEmailAndEnabledTrue(TEST_EMAIL);

        var composite = Mono.from(setup).then(find);

        StepVerifier.create(composite)
                .consumeNextWith(account -> {
                    assertEquals(account.getEmail(), TEST_EMAIL);
                    assertEquals(account.getPassword(), TEST_PASSWORD);
                })
                .verifyComplete();
    }

    @Test
    void shouldExistsByEmailAndEnabledTrue() {
        var setup = repository.deleteAll().then(repository.save(getTestUser().setEnabled(true)));
        var find = repository.existsByEmailAndEnabledTrue(TEST_EMAIL);

        var composite = Mono.from(setup).then(find);

        StepVerifier.create(composite)
                .consumeNextWith(Assertions::assertTrue)
                .verifyComplete();
    }

    private static User getTestUser() {
        return new User()
                .setEmail(TEST_EMAIL)
                .setPassword(TEST_PASSWORD)
                .setRole(UserRole.ROLE_USER)
                .setProvider(Provider.LOCAL);
    }
}
