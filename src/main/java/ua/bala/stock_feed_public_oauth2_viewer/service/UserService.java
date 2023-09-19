package ua.bala.stock_feed_public_oauth2_viewer.service;

import reactor.core.publisher.Mono;
import ua.bala.stock_feed_public_oauth2_viewer.model.Provider;
import ua.bala.stock_feed_public_oauth2_viewer.model.User;

public interface UserService {

    Mono<User> save(User user);
    Mono<User> getById(Long id);
    Mono<User> getByEmail(String email);
    Mono<Boolean> existsByEmailAndProvider(String email, Provider provider);
    Mono<Void> resetPasswordByEmail(String email);
    Mono<User> setNewPassword(String token, String newPassword);
}
