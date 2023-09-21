package ua.bala.stock_feed_public_oauth2_viewer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ua.bala.stock_feed_public_oauth2_viewer.dto.AuthRequest;
import ua.bala.stock_feed_public_oauth2_viewer.dto.AuthResponse;
import ua.bala.stock_feed_public_oauth2_viewer.security.jwt.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthControllerV1 {

    private final AuthService authService;

    @PostMapping("/login")
    public Mono<AuthResponse> loginByEmail(@RequestBody AuthRequest authRequest) {
        return authService.login(authRequest.getUsername(), authRequest.getPassword())
                .map(AuthResponse::new);
    }
}
