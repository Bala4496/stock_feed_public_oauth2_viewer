package ua.bala.stock_feed_public_oauth2_viewer.service.email;

import reactor.core.publisher.Mono;
import ua.bala.stock_feed_public_oauth2_viewer.model.entity.Token;
import ua.bala.stock_feed_public_oauth2_viewer.model.entity.User;

import java.util.function.Function;

public interface TokenService {

    Mono<Token> createRegisterToken(User user);
    Mono<Token> createResetPasswordToken(User user);
    Mono<User> validateRegisterTokenAndEditUser(String token, Function<Long, Mono<User>> userFunction);
    Mono<User> validateResetPasswordTokenAndEditUser(String token, Function<Long, Mono<User>> userFunction);
}
