package ua.bala.stock_feed_public_oauth2_viewer.email;

import reactor.core.publisher.Mono;
import ua.bala.stock_feed_public_oauth2_viewer.model.User;

import java.util.function.Function;

public interface TokenService {

    Mono<Token> createRegisterToken(User user);
    Mono<Token> createResetPasswordToken(User user);
    Mono<Token> findToken(String token);
    Mono<User> processUserToken(String token, Function<Long, Mono<User>> userFunction);
}
