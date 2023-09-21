package ua.bala.stock_feed_public_oauth2_viewer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ua.bala.stock_feed_public_oauth2_viewer.dto.NewPasswordDTO;
import ua.bala.stock_feed_public_oauth2_viewer.dto.UserDTO;
import ua.bala.stock_feed_public_oauth2_viewer.mapper.UserMapper;
import ua.bala.stock_feed_public_oauth2_viewer.service.UserService;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserControllerV1 {

    private final UserService userServiceEmailDecorator;
    private final UserMapper userMapper;

    @PostMapping("/reset-password/initiate")
    public Mono<Void> resetPasswordByEmail(@RequestParam("email") String email) {
        return userServiceEmailDecorator.resetPasswordByEmail(email)
                .then();
    }

    @PostMapping("/reset-password/verify")
    public Mono<UserDTO> setNewPassword(@RequestParam("token") String token,
                                        @RequestBody NewPasswordDTO newPasswordDTO) {
        return userServiceEmailDecorator.setNewPassword(token, newPasswordDTO.getNewPassword())
                .map(userMapper::map);
    }

    @GetMapping("/info")
    public Mono<UserDTO> getInfo(Principal principal) {
        return userServiceEmailDecorator.getByEmail(principal.getName())
                .map(userMapper::map);
    }
}
