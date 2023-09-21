package ua.bala.stock_feed_public_oauth2_viewer.email;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ua.bala.stock_feed_public_oauth2_viewer.model.Token;
import ua.bala.stock_feed_public_oauth2_viewer.model.TokenType;
import ua.bala.stock_feed_public_oauth2_viewer.model.User;
import ua.bala.stock_feed_public_oauth2_viewer.repository.TokenRepository;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final TokenRepository tokenRepository;
    private final Base64StringKeyGenerator keyGenerator = new Base64StringKeyGenerator();

    @Override
    public Mono<Token> createRegisterToken(User user) {
        return tokenRepository.save(createToken(user, TokenType.REGISTER));
    }

    @Override
    public Mono<Token> createResetPasswordToken(User user) {
        return tokenRepository.save(createToken(user, TokenType.RESET_PASSWORD));
    }

    @Override
    public Mono<Token> findToken(String token) {
        return tokenRepository.findByToken(token)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Token doesn't exist")))
                .filter(foundToken -> !foundToken.isExpired())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token expired")));
    }

    @Override
    public Mono<User> processUserToken(String token, Function<Long, Mono<User>> userFunction) {
        return verifyToken(token)
                .map(Token::getUserId)
                .flatMap(userFunction)
                .publishOn(Schedulers.boundedElastic())
                .doOnSuccess(user -> removeToken(token).subscribe());
    }

    private Mono<Token> verifyToken(String token) {
        return findToken(token)
                .filter(foundType -> TokenType.REGISTER.equals(foundType.getType()))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Token type isn't right")));
    }

    public Mono<Void> removeToken(String token) {
        return tokenRepository.removeByToken(token);
    }

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.DAYS)
    public void cleanUpExpiredTokens() {
        tokenRepository.findAll()
                .filter(Token::isExpired)
                .flatMap(tokenRepository::delete)
                .subscribe();
    }

    private Token createToken(User user, TokenType tokenType) {
        return new Token().setUserId(user.getId())
                .setToken(keyGenerator.generateKey().replaceAll("\\+", "_"))
                .setType(tokenType)
                .setExpireAt(LocalDateTime.now().plusMinutes(tokenType.getExpirationLimit()));
    }
}
