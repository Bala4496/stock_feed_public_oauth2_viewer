package ua.bala.stock_feed_public_oauth2_viewer.service;

import reactor.core.publisher.Mono;
import ua.bala.stock_feed_public_oauth2_viewer.model.entity.User;

public interface UserService {

    Mono<User> save(User user);
    Mono<User> getById(Long id);
    Mono<User> getByEmail(String email);
    Mono<Boolean> existsByEmail(String email);
    Mono<User> resetPasswordByEmail(String email);
    Mono<User> setNewPassword(String token, String newPassword);
}
