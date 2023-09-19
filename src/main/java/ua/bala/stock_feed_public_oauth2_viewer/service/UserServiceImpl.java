package ua.bala.stock_feed_public_oauth2_viewer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import ua.bala.stock_feed_public_oauth2_viewer.email.TokenService;
import ua.bala.stock_feed_public_oauth2_viewer.model.Provider;
import ua.bala.stock_feed_public_oauth2_viewer.model.User;
import ua.bala.stock_feed_public_oauth2_viewer.repository.UserRepository;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Override
    public Mono<User> save(User user) {
        return userRepository.save(user);
    }

    @Override
    public Mono<User> getById(Long id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NO_CONTENT, "User by id: %s - not found".formatted(id))));
    }

    @Override
    public Mono<User> getByEmail(String email) {
        return userRepository.findByEmailAndEnabledTrue(email)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NO_CONTENT, "User by email: %s - not found".formatted(email))));
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return userRepository.existsByEmailAndEnabledTrue(email);
    }

    @Override
    public Mono<User> resetPasswordByEmail(String email) {
        return userRepository.findByEmail(email)
                .filter(user -> Provider.LOCAL.equals(user.getProvider()))
                .map(user -> user.setEnabled(false))
                .flatMap(this::save);
    }

    @Override
    public Mono<User> setNewPassword(String token, String newPassword) {
        return processToken(token, user -> user.setEnabled(true).setPassword(encodePassword(newPassword)));
    }

    @Override
    public Mono<User> processToken(String token, Function<User, User> userFunction) {
        return tokenService.processUserToken(token, id -> getById(id).map(userFunction)
                .flatMap(this::save));
    }

    private String encodePassword(String newPassword) {
        return passwordEncoder.encode(newPassword);
    }
}
