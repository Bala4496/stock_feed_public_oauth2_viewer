package ua.bala.stock_feed_public_oauth2_viewer.email;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import ua.bala.stock_feed_public_oauth2_viewer.model.User;
import ua.bala.stock_feed_public_oauth2_viewer.repository.TokenRepository;

import java.time.LocalDateTime;

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
    public Mono<Void> removeToken(String token) {
        return tokenRepository.removeByToken(token);
    }

    private Token createToken(User user, TokenType tokenType) {
        return new Token().setUserId(user.getId())
                .setToken(keyGenerator.generateKey())
                .setType(tokenType)
                .setExpireAt(LocalDateTime.now().plusMinutes(tokenType.getExpirationLimit()));
    }
}
