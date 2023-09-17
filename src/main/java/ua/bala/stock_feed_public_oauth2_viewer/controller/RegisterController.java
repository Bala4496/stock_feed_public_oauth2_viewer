package ua.bala.stock_feed_public_oauth2_viewer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ua.bala.stock_feed_public_oauth2_viewer.dto.RegisterUserDTO;
import ua.bala.stock_feed_public_oauth2_viewer.dto.UserDTO;
import ua.bala.stock_feed_public_oauth2_viewer.mapper.RegisterUserMapper;
import ua.bala.stock_feed_public_oauth2_viewer.mapper.UserMapper;
import ua.bala.stock_feed_public_oauth2_viewer.service.RegisterServiceImpl;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/register")
public class RegisterController {

    private final RegisterServiceImpl registerService;
    private final RegisterUserMapper registerUserMapper;
    private final UserMapper userMapper;

    @PostMapping
    public Mono<RegisterUserDTO> register(@RequestBody RegisterUserDTO registerUserDTO) {
        return registerService.registerUser(registerUserMapper.map(registerUserDTO))
                .map(registerUserMapper::map);
    }

    @GetMapping("/verify")
    public Mono<UserDTO> verifyEmail(@RequestParam("token") String token) {
        return registerService.confirmEmail(token)
                .map(userMapper::map);
    }
}