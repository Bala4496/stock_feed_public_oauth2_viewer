package ua.bala.stock_feed_public_oauth2_viewer.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ua.bala.stock_feed_public_oauth2_viewer.model.entity.User;
import ua.bala.stock_feed_public_oauth2_viewer.service.email.EmailService;
import ua.bala.stock_feed_public_oauth2_viewer.service.email.TokenService;

@Service
public class RegisterServiceEmailDecorator extends RegisterServiceImpl {

    private final EmailService emailService;

    public RegisterServiceEmailDecorator(@Qualifier("userServiceImpl") UserService userService,
                                         PasswordEncoder passwordEncoder,
                                         TokenService tokenService,
                                         EmailService emailService) {
        super(userService, passwordEncoder, tokenService);
        this.emailService = emailService;
    }

    @Override
    public Mono<User> registerLocalUser(User user) {
        return super.registerLocalUser(user)
                .doOnSuccess(emailService::sendRegistrationEmail);
    }
}
