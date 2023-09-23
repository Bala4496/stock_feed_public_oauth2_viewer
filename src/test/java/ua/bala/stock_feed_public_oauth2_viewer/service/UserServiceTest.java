package ua.bala.stock_feed_public_oauth2_viewer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ua.bala.stock_feed_public_oauth2_viewer.model.entity.User;
import ua.bala.stock_feed_public_oauth2_viewer.model.enums.Provider;
import ua.bala.stock_feed_public_oauth2_viewer.model.enums.UserRole;
import ua.bala.stock_feed_public_oauth2_viewer.repository.UserRepository;
import ua.bala.stock_feed_public_oauth2_viewer.service.email.EmailService;
import ua.bala.stock_feed_public_oauth2_viewer.service.email.TokenService;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class UserServiceTest {

    private static final String TEST_EMAIL = "test.account@gmail.com";
    private static final String TEST_PASSWORD = "password";
    private static final String TEST_TOKEN = "super_secret_test_token";

    @InjectMocks
    private UserServiceEmailDecorator userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TokenService tokenService;
    @Mock
    private EmailService emailService;

    @Test
    void shouldSave() {
        var testUser = getTestUser();

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0, User.class)));

        var result = userService.save(testUser);

        StepVerifier.create(result)
                .consumeNextWith(user -> {
                    assertEquals(user.getId(), testUser.getId());
                    assertEquals(user.getEmail(), testUser.getEmail());
                    assertEquals(user.getPassword(), testUser.getPassword());
                    assertEquals(user.getRole(), testUser.getRole());
                    assertEquals(user.getProvider(), testUser.getProvider());
                    assertTrue(user.isEnabled());
                })
                .verifyComplete();
    }

    @Test
    void shouldGetById() {
        var testUser = getTestUser();
        Long userId = testUser.getId();

        when(userRepository.findById(userId)).thenReturn(Mono.just(testUser));

        var result = userService.getById(userId);

        StepVerifier.create(result)
                .consumeNextWith(user -> {
                    assertEquals(user.getId(), testUser.getId());
                    assertEquals(user.getEmail(), testUser.getEmail());
                    assertEquals(user.getPassword(), testUser.getPassword());
                    assertEquals(user.getRole(), testUser.getRole());
                    assertEquals(user.getProvider(), testUser.getProvider());
                    assertTrue(user.isEnabled());
                })
                .verifyComplete();
    }

    @Test
    void shouldGetByEmail() {
        var testUser = getTestUser();

        when(userRepository.findByEmailAndEnabledTrue(TEST_EMAIL)).thenReturn(Mono.just(testUser));

        var result = userService.getByEmail(TEST_EMAIL);

        StepVerifier.create(result)
                .consumeNextWith(user -> {
                    assertEquals(user.getId(), testUser.getId());
                    assertEquals(user.getEmail(), testUser.getEmail());
                    assertEquals(user.getPassword(), testUser.getPassword());
                    assertEquals(user.getRole(), testUser.getRole());
                    assertEquals(user.getProvider(), testUser.getProvider());
                    assertTrue(user.isEnabled());
                })
                .verifyComplete();
    }

    @Test
    void shouldExistsByEmail() {
        when(userRepository.existsByEmailAndEnabledTrue(TEST_EMAIL)).thenReturn(Mono.just(Boolean.TRUE));

        var result = userService.existsByEmail(TEST_EMAIL);

        StepVerifier.create(result)
                .expectNext(Boolean.TRUE)
                .verifyComplete();
    }

    @Test
    void shouldResetPasswordByEmail() {
        var testUser = getTestUser();

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Mono.just(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0, User.class)));
        doNothing().when(emailService).sendResetPasswordEmail(testUser.setEnabled(false));

        var result = userService.resetPasswordByEmail(TEST_EMAIL);

        StepVerifier.create(result)
                .consumeNextWith(user -> {
                    assertEquals(user.getId(), testUser.getId());
                    assertEquals(user.getEmail(), testUser.getEmail());
                    assertEquals(user.getPassword(), testUser.getPassword());
                    assertEquals(user.getRole(), testUser.getRole());
                    assertEquals(user.getProvider(), testUser.getProvider());
                    assertFalse(user.isEnabled());
                })
                .verifyComplete();
    }

    @Test
    void shouldSetNewPassword() {
        var testUser = getTestUser().setEnabled(false);
        String newPassword = "newPassword";
        Function<Long, Mono<User>> userFunction = id -> Mono.just(testUser)
                .filter(user -> user.getId().equals(id))
                .map(user -> user.setEnabled(true))
                .map(user -> user.setPassword(newPassword));

        when(tokenService.validateResetPasswordTokenAndEditUser(eq(TEST_TOKEN), any(Function.class))).thenReturn(userFunction.apply(testUser.getId()));

        var result = userService.setNewPassword(TEST_TOKEN, newPassword);

        StepVerifier.create(result)
                .consumeNextWith(user -> {
                    assertEquals(user.getId(), testUser.getId());
                    assertEquals(user.getEmail(), testUser.getEmail());
                    assertEquals(user.getPassword(), newPassword);
                    assertEquals(user.getRole(), testUser.getRole());
                    assertEquals(user.getProvider(), testUser.getProvider());
                    assertTrue(user.isEnabled());
                })
                .verifyComplete();
    }

    private static User getTestUser() {
        return new User()
                .setId(1L)
                .setEmail(TEST_EMAIL)
                .setPassword(TEST_PASSWORD)
                .setRole(UserRole.ROLE_USER)
                .setProvider(Provider.LOCAL)
                .setEnabled(true);
    }
}