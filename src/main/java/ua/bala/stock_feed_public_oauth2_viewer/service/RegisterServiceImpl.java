package ua.bala.stock_feed_public_oauth2_viewer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ua.bala.stock_feed_public_oauth2_viewer.model.Provider;
import ua.bala.stock_feed_public_oauth2_viewer.model.User;
import ua.bala.stock_feed_public_oauth2_viewer.model.UserRole;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {

    private final UserService userServiceImpl;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Mono<User> registerUser(User user) {
        user.setRole(UserRole.ROLE_USER);
        user.setProvider(Provider.LOCAL);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userServiceImpl.save(user);
    }

    @Override
    public Mono<User> registerOAuth2User(String registrationId, String authName) {
        var user = new User().setEmail(authName)
                .setPassword("oAuthDriven")
                .setRole(UserRole.ROLE_USER)
                .setProvider(setProvider(registrationId))
                .setEnabled(true);
        return Mono.just(user)
                .filterWhen(usr -> userServiceImpl.existsByEmail(usr.getEmail()).map(exists -> !exists))
                .flatMap(userServiceImpl::save);
    }

    private Provider setProvider(String registrationId) {
        return Optional.of(Provider.valueOf(registrationId.toUpperCase()))
                .filter(s -> !Provider.LOCAL.equals(s))
                .orElseThrow(() -> new IllegalArgumentException("RegistrationId LOCAL not supported for OAuth2 Users"));
    }

    @Override
    public Mono<User> confirmRegisterUser(String token) {
        return userServiceImpl.processToken(token, user -> user.setEnabled(true));
    }
}
