package ua.bala.stock_feed_public_oauth2_viewer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ua.bala.stock_feed_public_oauth2_viewer.dto.UserDTO;
import ua.bala.stock_feed_public_oauth2_viewer.mapper.UserMapper;
import ua.bala.stock_feed_public_oauth2_viewer.model.request.NewPasswordRequest;
import ua.bala.stock_feed_public_oauth2_viewer.model.request.ResetPasswordRequest;
import ua.bala.stock_feed_public_oauth2_viewer.service.UserService;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserControllerV1 {

    private final UserService userServiceEmailDecorator;
    private final UserMapper userMapper;

    @PostMapping("/reset-password/initiate")
    public Mono<Void> resetPasswordByEmail(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        return userServiceEmailDecorator.resetPasswordByEmail(resetPasswordRequest.getEmail())
                .then();
    }

    @PostMapping("/reset-password/verify")
    public Mono<UserDTO> setNewPassword(@RequestBody NewPasswordRequest newPasswordRequest) {
        return userServiceEmailDecorator.setNewPassword(newPasswordRequest.getToken(), newPasswordRequest.getNewPassword())
                .map(userMapper::map);
    }

    @GetMapping("/info")
    public Mono<UserDTO> getInfo(Principal principal) {
        return userServiceEmailDecorator.getByEmail(principal.getName())
                .map(userMapper::map);
    }
}
