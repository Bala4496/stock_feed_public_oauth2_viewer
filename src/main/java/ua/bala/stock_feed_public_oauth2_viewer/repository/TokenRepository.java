package ua.bala.stock_feed_public_oauth2_viewer.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import ua.bala.stock_feed_public_oauth2_viewer.model.entity.Token;

public interface TokenRepository extends ReactiveCrudRepository<Token, Long> {

    Mono<Token> findByToken(String token);
    Mono<Void> removeByToken(String token);
}
