package ua.bala.stock_feed_public_oauth2_viewer.service.email;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import ua.bala.stock_feed_public_oauth2_viewer.model.entity.Token;
import ua.bala.stock_feed_public_oauth2_viewer.model.entity.User;
import ua.bala.stock_feed_public_oauth2_viewer.model.enums.Provider;
import ua.bala.stock_feed_public_oauth2_viewer.model.enums.TokenType;
import ua.bala.stock_feed_public_oauth2_viewer.model.enums.UserRole;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class EmailServiceImplTest {

    private static final String TEST_EMAIL = "test.account@gmail.com";
    private static final String TEST_PASSWORD = "password";
    private static final String TEST_TOKEN = "super_secret_test_token";

    @InjectMocks
    private EmailServiceImpl emailService;
    @Mock
    private TokenService tokenService;
    @Mock
    private JavaMailSender mailSender;

    @Test
    void shouldSendRegistrationEmail() {
        var user = getTestUser();
        when(tokenService.createRegisterToken(user)).thenReturn(Mono.just(getTestRegisterToken()));
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendRegistrationEmail(getTestUser());

        verify(tokenService).createRegisterToken(user);
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void shouldSendResetPasswordEmail() {
        var user = getTestUser();
        when(tokenService.createResetPasswordToken(user)).thenReturn(Mono.just(getResetPasswordToken()));
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendResetPasswordEmail(getTestUser());

        verify(tokenService).createResetPasswordToken(user);
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    private static User getTestUser() {
        return new User()
                .setEmail(TEST_EMAIL)
                .setPassword(TEST_PASSWORD)
                .setRole(UserRole.ROLE_USER)
                .setProvider(Provider.LOCAL);
    }

    private Token getTestRegisterToken() {
        return getTestToken(TokenType.REGISTER);
    }

    private Token getResetPasswordToken() {
        return getTestToken(TokenType.RESET_PASSWORD);
    }

    private Token getTestToken(TokenType tokenType) {
        var now = LocalDateTime.now();
        return new Token().setUserId(1L)
                .setToken(TEST_TOKEN)
                .setType(tokenType)
                .setCreatedAt(now)
                .setExpireAt(now.plusMinutes(15000));
    }
}
