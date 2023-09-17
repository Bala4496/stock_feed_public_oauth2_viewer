package ua.bala.stock_feed_public_oauth2_viewer.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;
import ua.bala.stock_feed_public_oauth2_viewer.security.oidc.CustomAuthenticationSuccessHandler;
import ua.bala.stock_feed_public_oauth2_viewer.security.oidc.CustomOAuth2AuthorizedClientService;

@Slf4j
@Configuration
@EnableWebFluxSecurity
public class WebFluxSecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http,
                                                            CustomOAuth2AuthorizedClientService oAuth2AuthorizedClientService,
                                                            CustomAuthenticationSuccessHandler authenticationSuccessHandler) {
        return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(Customizer.withDefaults())
//                .oauth2Login(spec -> {
//                    spec.authorizedClientService(oAuth2AuthorizedClientService);
//                    spec.authenticationSuccessHandler(authenticationSuccessHandler);
//                })
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authorizeExchange(spec -> {
//                    spec.pathMatchers("/login", "/favicon.ico").permitAll();
                    spec.pathMatchers("/api/v1/register/**", "/api/v1/users/resetPassword").permitAll();
                    spec.anyExchange().authenticated();
//                    spec.anyExchange().permitAll();
                })
                .exceptionHandling(spec -> spec
                        .authenticationEntryPoint((exchange, exception) -> {
                            log.error("Error while authorization : {}", exception.getMessage(), exception);
                            return Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED));
                        })
                        .accessDeniedHandler((exchange, exception) -> {
                            log.error("Error while getting access to resource : {}", exception.getMessage(), exception);
                            return Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN));
                        }))
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
