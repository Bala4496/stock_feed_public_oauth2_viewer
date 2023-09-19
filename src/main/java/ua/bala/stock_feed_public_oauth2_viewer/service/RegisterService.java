package ua.bala.stock_feed_public_oauth2_viewer.service;

import reactor.core.publisher.Mono;
import ua.bala.stock_feed_public_oauth2_viewer.model.User;

public interface RegisterService {

    Mono<User> registerUser(User user);
    Mono<User> registerOAuth2User(String registrationId, String authName);
    Mono<User> confirmRegisterUser(String token);
}
