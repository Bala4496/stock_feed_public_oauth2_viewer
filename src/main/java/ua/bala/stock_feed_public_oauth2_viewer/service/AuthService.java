package ua.bala.stock_feed_public_oauth2_viewer.service;

import reactor.core.publisher.Mono;

public interface AuthService {

    Mono<String> login(String username, String password);
}
