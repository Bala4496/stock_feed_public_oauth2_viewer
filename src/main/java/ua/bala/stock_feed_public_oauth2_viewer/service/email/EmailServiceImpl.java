package ua.bala.stock_feed_public_oauth2_viewer.service.email;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ua.bala.stock_feed_public_oauth2_viewer.model.entity.User;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    @Value("${server.port}")
    public int hostPort;
    public static final String HOST_URL = "http://localhost:%d";
    public static final String USER_RESET_PASSWORD_TOKEN_API = "/api/v1/user/reset-password/verify?token=%s";
    public static final String REGISTER_REGISTRATION_CONFIRM_TOKEN_API = "/api/v1/register/verify?token=%s";
    private final TokenService tokenService;
    private final JavaMailSender mailSender;

    @Override
    public void sendRegistrationEmail(User user) {
        tokenService.createRegisterToken(user)
                .map(token -> buildRegistrationConfirmationEmail(user.getEmail(), token.getToken()))
                .doOnNext(mailSender::send)
                .subscribe();
    }

    private SimpleMailMessage buildRegistrationConfirmationEmail(String userEmail, String token) {
        var subject = "Registration Confirmation";
        var message = "Confirm, your email to finish registration on Stock Feed Viewer.";
        var confirmationUrl = HOST_URL.formatted(hostPort).concat(REGISTER_REGISTRATION_CONFIRM_TOKEN_API.formatted(token));
        return buildSimpleMessage(userEmail, subject, message, confirmationUrl);
    }

    @Override
    public void sendResetPasswordEmail(User user) {
        tokenService.createResetPasswordToken(user)
                .map(token -> buildResetPasswordEmail(user.getEmail(), token.getToken()))
                .doOnNext(mailSender::send)
                .subscribe();
    }

    private SimpleMailMessage buildResetPasswordEmail(String userEmail, String token) {
        var subject = "Reset password";
        var message = "Follow the link to reset password fot Stock Feed Viewer Account.";
        var confirmationUrl = HOST_URL.formatted(hostPort).concat(USER_RESET_PASSWORD_TOKEN_API.formatted(token));
        return buildSimpleMessage(userEmail, subject, message, confirmationUrl);
    }

    private static SimpleMailMessage buildSimpleMessage(String userEmail, String subject, String message, String confirmationUrl) {
        var email = new SimpleMailMessage();
        email.setTo(userEmail);
        email.setSubject(subject);
        email.setText("%s\r\n%s".formatted(message, confirmationUrl));
        return email;
    }
}
