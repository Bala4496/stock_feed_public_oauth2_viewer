package ua.bala.stock_feed_public_oauth2_viewer.security.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthManager implements ReactiveAuthenticationManager {

    private final TokenUtil TokenUtil;
    private final ReactiveUserDetailsService reactiveUserDetailsService;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        System.out.println("AuthManager");
        return Mono.justOrEmpty(authentication)
                .cast(BearerToken.class)
                .flatMap(auth -> {
                    var subject = TokenUtil.extractSubject(auth.getCredentials());
                    return reactiveUserDetailsService.findByUsername(subject)
                            .switchIfEmpty(Mono.error(new UsernameNotFoundException("User by username: '%s' does not exist".formatted(subject))))
                            .filter(user -> TokenUtil.validateToken(user, auth.getCredentials()))
                            .map(user -> new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), user.getAuthorities()))
                            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token")));
                });
    }
}
