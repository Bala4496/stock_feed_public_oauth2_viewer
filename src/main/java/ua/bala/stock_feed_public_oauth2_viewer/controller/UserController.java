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
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/resetPassword")
    public Mono<Void> resetPasswordByEmail(@RequestParam("email") String email) {
        return userService.resetPasswordByEmail(email);
    }

    @PostMapping("/resetPassword")
    public Mono<UserDTO> setNewPassword(@RequestParam("token") String token,
                                        @RequestBody NewPasswordDTO newPasswordDTO) {
        return userService.setNewPassword(token, newPasswordDTO.getNewPassword())
                .map(userMapper::map);
    }

    @GetMapping("/info")
    public Mono<UserDTO> getInfo(Principal principal) {
        return userService.getByEmail(principal.getName())
                .map(userMapper::map);
    }
}
