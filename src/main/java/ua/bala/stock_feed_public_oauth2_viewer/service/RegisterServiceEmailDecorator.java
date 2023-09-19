package ua.bala.stock_feed_public_oauth2_viewer.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ua.bala.stock_feed_public_oauth2_viewer.email.EmailService;
import ua.bala.stock_feed_public_oauth2_viewer.model.User;

@Service
public class RegisterServiceEmailDecorator extends RegisterServiceImpl {

    private final EmailService emailService;

    public RegisterServiceEmailDecorator(@Qualifier("userServiceImpl") UserService userService,
                                         PasswordEncoder passwordEncoder,
                                         EmailService emailService) {
        super(userService, passwordEncoder);
        this.emailService = emailService;
    }

    @Override
    public Mono<User> registerUser(User user) {
        return super.registerUser(user)
                .doOnSuccess(emailService::sendRegistrationEmail);
    }
}
