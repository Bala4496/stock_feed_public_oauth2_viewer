package ua.bala.stock_feed_public_oauth2_viewer.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import ua.bala.stock_feed_public_oauth2_viewer.model.User;

public interface UserRepository extends ReactiveCrudRepository<User, Long> {

    Mono<User> findByEmail(String email);
    Mono<User> findByEmailAndEnabledTrue(String email);
    Mono<Boolean> existsByEmailAndEnabledTrue(String email);
}
