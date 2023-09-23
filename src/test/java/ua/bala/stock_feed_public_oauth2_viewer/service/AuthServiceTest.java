package ua.bala.stock_feed_public_oauth2_viewer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ua.bala.stock_feed_public_oauth2_viewer.util.TokenUtil;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class AuthServiceTest {

    private static final String TEST_EMAIL = "test.account@gmail.com";
    private static final String TEST_PASSWORD = "password";
    private static final String TEST_TOKEN = "super_secret_test_token";

    @InjectMocks
    private AuthServiceImpl authService;
    @Mock
    private ReactiveAuthenticationManager authenticationManager;
    @Mock
    private ReactiveUserDetailsService reactiveUserDetailsService;
    @Mock
    private TokenUtil tokenUtil;

    @Test
    void shouldLogin() {

        var userDetails = new User(TEST_EMAIL, TEST_PASSWORD, AuthorityUtils.NO_AUTHORITIES);
        var authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, TEST_PASSWORD, userDetails.getAuthorities());

        when(reactiveUserDetailsService.findByUsername(TEST_EMAIL)).thenReturn(Mono.just(userDetails));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(Mono.just(authenticationToken));
        when(tokenUtil.generateToken(TEST_EMAIL)).thenReturn(TEST_TOKEN);

        var result = authService.login(TEST_EMAIL, TEST_PASSWORD);

        StepVerifier.create(result)
                .expectNext(TEST_TOKEN)
                .verifyComplete();

        verify(reactiveUserDetailsService).findByUsername(TEST_EMAIL);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenUtil).generateToken(TEST_EMAIL);
    }
}
