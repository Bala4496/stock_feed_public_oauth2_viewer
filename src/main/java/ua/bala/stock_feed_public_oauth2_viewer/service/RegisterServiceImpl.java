package ua.bala.stock_feed_public_oauth2_viewer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ua.bala.stock_feed_public_oauth2_viewer.email.EmailService;
import ua.bala.stock_feed_public_oauth2_viewer.email.Token;
import ua.bala.stock_feed_public_oauth2_viewer.email.TokenService;
import ua.bala.stock_feed_public_oauth2_viewer.email.TokenType;
import ua.bala.stock_feed_public_oauth2_viewer.model.Provider;
import ua.bala.stock_feed_public_oauth2_viewer.model.User;
import ua.bala.stock_feed_public_oauth2_viewer.model.UserRole;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final EmailService emailService;

    @Override
    public Mono<User> registerUser(String registrationId, String authName) {
        return Mono.just(new User()
                        .setEmail(authName)
                        .setPassword("oAuthDriven")
                        .setRole(UserRole.ROLE_USER)
                        .setProvider(setProviderData(registrationId))
                        .setEnabled(true))
                .filterWhen(user -> userService.existsByEmailAndProvider(user.getEmail(), user.getProvider()).map(exists -> !exists))
                .flatMap(userService::save);
    }

    private Provider setProviderData(String registrationId) {
        return Optional.of(Provider.valueOf(registrationId.toUpperCase()))
                .filter(s -> !Provider.LOCAL.equals(s))
                .orElseThrow(() -> new IllegalArgumentException("RegistrationId LOCAL not supported for OAuth2 Users"));
    }

    @Override
    public Mono<User> registerUser(User user) {
        user.setRole(UserRole.ROLE_USER);
        user.setProvider(Provider.LOCAL);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userService.save(user)
                .doOnSuccess(emailService::sendRegistrationConfirmationEmail);
    }

    @Override
    public Mono<User> confirmEmail(String verificationToken) {
        return tokenService.findToken(verificationToken)
                .filter(foundType -> TokenType.REGISTER.equals(foundType.getType()))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Token type isn't right")))
                .map(Token::getUserId)
                .flatMap(userService::getById)
                .map(user -> user.setEnabled(true))
                .flatMap(userService::save)
                .publishOn(Schedulers.boundedElastic())
                .doOnSuccess(user -> tokenService.removeToken(verificationToken).subscribe());
    }
}
