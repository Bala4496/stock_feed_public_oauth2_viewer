package ua.bala.stock_feed_public_oauth2_viewer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ua.bala.stock_feed_public_oauth2_viewer.dto.RegisterUserDTO;
import ua.bala.stock_feed_public_oauth2_viewer.dto.UserDTO;
import ua.bala.stock_feed_public_oauth2_viewer.mapper.RegisterUserMapper;
import ua.bala.stock_feed_public_oauth2_viewer.mapper.UserMapper;
import ua.bala.stock_feed_public_oauth2_viewer.service.RegisterService;

@RestController
@RequestMapping("/api/v1/register")
@RequiredArgsConstructor
public class RegisterControllerV1 {

    private final RegisterService registerServiceEmailDecorator;
    private final RegisterUserMapper registerUserMapper;
    private final UserMapper userMapper;

    @PostMapping
    public Mono<RegisterUserDTO> registerUser(@RequestBody RegisterUserDTO registerUserDTO) {
        return registerServiceEmailDecorator.registerUser(registerUserMapper.map(registerUserDTO))
                .map(registerUserMapper::map);
    }

    @GetMapping("/verify")
    public Mono<UserDTO> verifyEmail(@RequestParam("token") String token) {
        return registerServiceEmailDecorator.confirmRegisterUser(token)
                .map(userMapper::map);
    }
}
