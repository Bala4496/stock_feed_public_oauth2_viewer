package ua.bala.stock_feed_public_oauth2_viewer.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ua.bala.stock_feed_public_oauth2_viewer.model.entity.User;
import ua.bala.stock_feed_public_oauth2_viewer.model.enums.Provider;
import ua.bala.stock_feed_public_oauth2_viewer.model.enums.UserRole;
import ua.bala.stock_feed_public_oauth2_viewer.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class ReactiveUserDetailsServiceTest {

    private static final String TEST_EMAIL = "test.account@gmail.com";
    private static final String TEST_PASSWORD = "password";

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;
    @Mock
    private UserRepository userRepository;

    @Test
    void shouldFindByUsername() {

        var user = getTestUser();
        when(userRepository.findByEmailAndEnabledTrue(TEST_EMAIL)).thenReturn(Mono.just(user));

        var result = userDetailsService.findByUsername(TEST_EMAIL);

        StepVerifier.create(result)
                .consumeNextWith(userDetails -> {
                    assertEquals(userDetails.getUsername(), user.getEmail());
                    assertEquals(userDetails.getPassword(), user.getPassword());
                    assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority(user.getRole().toString())));
                })
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
