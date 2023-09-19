package ua.bala.stock_feed_public_oauth2_viewer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ua.bala.stock_feed_public_oauth2_viewer.email.EmailServiceImpl;
import ua.bala.stock_feed_public_oauth2_viewer.email.Token;
import ua.bala.stock_feed_public_oauth2_viewer.email.TokenService;
import ua.bala.stock_feed_public_oauth2_viewer.email.TokenType;
import ua.bala.stock_feed_public_oauth2_viewer.model.Provider;
import ua.bala.stock_feed_public_oauth2_viewer.model.User;
import ua.bala.stock_feed_public_oauth2_viewer.repository.UserRepository;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, ReactiveUserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailServiceImpl emailService;
    private final TokenService tokenService;

    @Override
    public Mono<UserDetails> findByUsername(String email) {
        return userRepository.findByEmailAndEnabledTrue(email)
                .map(user -> new org.springframework.security.core.userdetails.User(
                        user.getEmail(),
                        user.getPassword(),
                        user.isEnabled(),
                        true,
                        true,
                        true,
                        Collections.singletonList(new SimpleGrantedAuthority(user.getRole().toString())))
                );
    }

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
    public Mono<Boolean> existsByEmailAndProvider(String email, Provider provider) {
        return userRepository.existsByEmailAndProviderAndEnabledTrue(email, provider);
    }

    @Override
    public Mono<Void> resetPasswordByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(user -> user.setEnabled(false))
                .flatMap(this::save)
                .doOnSuccess(emailService::sendResetPasswordEmail)
                .then();
    }

    @Override
    public Mono<User> setNewPassword(String token, String newPassword) {
        return tokenService.findToken(token)
                .filter(foundType -> TokenType.RESET_PASSWORD.equals(foundType.getType()))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Token type isn't right")))
                .map(Token::getUserId)
                .flatMap(this::getById)
                .map(user -> user.setPassword(encodePassword(newPassword)))
                .map(user -> user.setEnabled(true))
                .flatMap(this::save)
                .publishOn(Schedulers.boundedElastic())
                .doOnSuccess(user -> tokenService.removeToken(token).subscribe());
    }

    private String encodePassword(String newPassword) {
        return passwordEncoder.encode(newPassword);
    }
}
