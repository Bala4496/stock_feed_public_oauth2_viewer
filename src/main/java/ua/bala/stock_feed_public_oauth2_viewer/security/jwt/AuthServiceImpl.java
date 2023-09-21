package ua.bala.stock_feed_public_oauth2_viewer.security.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final ReactiveAuthenticationManager authenticationManager;
    private final ReactiveUserDetailsService reactiveUserDetailsService;
    private final TokenUtil TokenUtil;

    @Override
    public Mono<String> login(String username, String password) {
        return reactiveUserDetailsService.findByUsername(username)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User by username: '%s' does not exist".formatted(username))))
                .filterWhen(userDetails ->
                        Mono.just(new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities()))
                        .doOnNext(authenticationManager::authenticate)
                        .map(AbstractAuthenticationToken::isAuthenticated))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authorized")))
                .map(UserDetails::getUsername)
                .map(TokenUtil::generateToken);
    }
}
