package ua.bala.stock_feed_public_oauth2_viewer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ua.bala.stock_feed_public_oauth2_viewer.model.entity.User;
import ua.bala.stock_feed_public_oauth2_viewer.model.enums.Provider;
import ua.bala.stock_feed_public_oauth2_viewer.model.enums.UserRole;
import ua.bala.stock_feed_public_oauth2_viewer.service.email.EmailService;
import ua.bala.stock_feed_public_oauth2_viewer.service.email.RegistrationEmailMessageProducer;
import ua.bala.stock_feed_public_oauth2_viewer.service.email.TokenService;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class RegisterServiceTest {

    private static final String TEST_EMAIL = "test.account@gmail.com";
    private static final String TEST_PASSWORD = "password";
    private static final String TEST_TOKEN = "super_secret_test_token";

    @InjectMocks
    private RegisterServiceEmailDecorator registerService;
    @Mock
    private UserServiceImpl userService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private TokenService tokenService;
    @Mock
    private RegistrationEmailMessageProducer registrationEmailMessageProducer;

    @Test
    void shouldRegisterLocalUser() {
        var testUser = getTestUser().setRole(null).setProvider(null).setEnabled(false);

        doNothing().when(registrationEmailMessageProducer).sendRegistrationEmail(testUser.setEnabled(false));
        when(userService.save(any(User.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0, User.class)));
        when(passwordEncoder.encode(anyString())).thenAnswer(invocation -> invocation.getArgument(0, String.class));

        var result = registerService.registerLocalUser(testUser);

        StepVerifier.create(result)
                .consumeNextWith(user -> {
                    assertEquals(user.getEmail(), testUser.getEmail());
                    assertEquals(user.getPassword(), testUser.getPassword());
                    assertEquals(user.getRole(), UserRole.ROLE_USER);
                    assertEquals(user.getProvider(), Provider.LOCAL);
                    assertFalse(user.isEnabled());
                })
                .verifyComplete();
    }

    @Test
    void shouldRegisterGoogleOAuth2User() {
        String registrationId = "google";
        String authName = "googleUser";

        when(userService.existsByEmail(authName)).thenReturn(Mono.just(false));
        when(userService.save(any(User.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0, User.class)));

        var result = registerService.registerOAuth2User(registrationId, authName);

        StepVerifier.create(result)
                .consumeNextWith(user -> {
                    assertEquals(user.getEmail(), authName);
                    assertEquals(user.getPassword(), "oAuthDriven");
                    assertEquals(user.getRole(), UserRole.ROLE_USER);
                    assertEquals(user.getProvider(), Provider.GOOGLE);
                    assertTrue(user.isEnabled());
                })
                .verifyComplete();
    }

    @Test
    void shouldRegisterGithubOAuth2User() {
        String registrationId = "github";
        String authName = "githubUser";

        when(userService.existsByEmail(authName)).thenReturn(Mono.just(false));
        when(userService.save(any(User.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0, User.class)));

        var result = registerService.registerOAuth2User(registrationId, authName);

        StepVerifier.create(result)
                .consumeNextWith(user -> {
                    assertEquals(user.getEmail(), authName);
                    assertEquals(user.getPassword(), "oAuthDriven");
                    assertEquals(user.getRole(), UserRole.ROLE_USER);
                    assertEquals(user.getProvider(), Provider.GITHUB);
                    assertTrue(user.isEnabled());
                })
                .verifyComplete();
    }

    @Test
    void shouldConfirmRegisterUser() {
        var testUser = getTestUser().setEnabled(false);
        Function<Long, Mono<User>> userFunction = id -> Mono.just(testUser)
                .filter(user -> user.getId().equals(id))
                .map(user -> user.setEnabled(true));

        when(tokenService.validateRegisterTokenAndEditUser(eq(TEST_TOKEN), any(Function.class))).thenReturn(userFunction.apply(testUser.getId()));

        var result = registerService.confirmRegisterUser(TEST_TOKEN);

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