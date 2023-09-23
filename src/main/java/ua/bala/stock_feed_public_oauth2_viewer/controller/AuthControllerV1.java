package ua.bala.stock_feed_public_oauth2_viewer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ua.bala.stock_feed_public_oauth2_viewer.model.request.AuthRequest;
import ua.bala.stock_feed_public_oauth2_viewer.model.response.AuthResponse;
import ua.bala.stock_feed_public_oauth2_viewer.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthControllerV1 {

    private final AuthService authService;

    @PostMapping("/login")
    public Mono<AuthResponse> loginByEmail(@RequestBody AuthRequest authRequest) {
        return authService.login(authRequest.getEmail(), authRequest.getPassword())
                .map(AuthResponse::new);
    }
}
