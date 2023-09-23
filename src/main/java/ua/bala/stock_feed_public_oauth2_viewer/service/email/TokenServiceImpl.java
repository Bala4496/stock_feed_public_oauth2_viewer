package ua.bala.stock_feed_public_oauth2_viewer.service.email;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ua.bala.stock_feed_public_oauth2_viewer.model.entity.Token;
import ua.bala.stock_feed_public_oauth2_viewer.model.entity.User;
import ua.bala.stock_feed_public_oauth2_viewer.model.enums.TokenType;
import ua.bala.stock_feed_public_oauth2_viewer.repository.TokenRepository;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final TokenRepository tokenRepository;
    private final Base64StringKeyGenerator keyGenerator = new Base64StringKeyGenerator();

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.DAYS)
    public void cleanUpExpiredTokens() {
        tokenRepository.findAll()
                .filter(Token::isExpired)
                .flatMap(tokenRepository::delete)
                .subscribe();
    }

    @Override
    public Mono<Token> createRegisterToken(User user) {
        return tokenRepository.save(createToken(user, TokenType.REGISTER));
    }

    @Override
    public Mono<Token> createResetPasswordToken(User user) {
        return tokenRepository.save(createToken(user, TokenType.RESET_PASSWORD));
    }

    @Override
    public Mono<User> validateRegisterTokenAndEditUser(String token, Function<Long, Mono<User>> userFunction) {
        return validateTokenAndEditUser(token, TokenType.REGISTER, userFunction);
    }

    @Override
    public Mono<User> validateResetPasswordTokenAndEditUser(String token, Function<Long, Mono<User>> userFunction) {
        return validateTokenAndEditUser(token, TokenType.RESET_PASSWORD, userFunction);
    }

    private Mono<User> validateTokenAndEditUser(String token, TokenType tokenType, Function<Long, Mono<User>> userFunction) {
        return verifyToken(token, tokenType)
                .map(Token::getUserId)
                .flatMap(userFunction)
                .publishOn(Schedulers.boundedElastic())
                .doOnSuccess(user -> deleteToken(token).subscribe());
    }

    private Mono<Token> findToken(String token) {
        System.out.println("findToken : token - " + token);
        return tokenRepository.findByToken(token)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Token doesn't exist")))
                .filter(foundToken -> !foundToken.isExpired())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token expired")));
    }

    private Mono<Void> deleteToken(String token) {
        return tokenRepository.removeByToken(token);
    }

    private Token createToken(User user, TokenType tokenType) {
        return new Token().setUserId(user.getId())
                .setToken(keyGenerator.generateKey().replaceAll("\\+", "_"))
                .setType(tokenType)
                .setExpireAt(LocalDateTime.now().plusMinutes(tokenType.getExpirationLimit()));
    }

    private Mono<Token> verifyToken(String token, TokenType tokenType) {
        return findToken(token)
                .filter(foundType -> tokenType.equals(foundType.getType()))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Token's type isn't right")));
    }
}
