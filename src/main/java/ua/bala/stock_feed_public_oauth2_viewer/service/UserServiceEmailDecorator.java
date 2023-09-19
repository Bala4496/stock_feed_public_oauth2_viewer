package ua.bala.stock_feed_public_oauth2_viewer.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ua.bala.stock_feed_public_oauth2_viewer.email.EmailService;
import ua.bala.stock_feed_public_oauth2_viewer.email.TokenService;
import ua.bala.stock_feed_public_oauth2_viewer.model.User;
import ua.bala.stock_feed_public_oauth2_viewer.repository.UserRepository;

@Service
public class UserServiceEmailDecorator extends UserServiceImpl {

    private final EmailService emailService;

    public UserServiceEmailDecorator(UserRepository userRepository,
                                     PasswordEncoder passwordEncoder,
                                     TokenService tokenService,
                                     EmailService emailService) {
        super(userRepository, passwordEncoder, tokenService);
        this.emailService = emailService;
    }

    @Override
    public Mono<User> resetPasswordByEmail(String email) {
        return super.resetPasswordByEmail(email)
                .doOnSuccess(emailService::sendResetPasswordEmail);
    }
}
