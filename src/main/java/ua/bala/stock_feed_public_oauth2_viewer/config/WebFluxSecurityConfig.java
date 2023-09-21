package ua.bala.stock_feed_public_oauth2_viewer.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;

@Slf4j
@Configuration
@EnableWebFluxSecurity
public class WebFluxSecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http,
                                                            AuthenticationWebFilter JWTAuthWebFilter) {
        return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(spec -> spec
                        .pathMatchers("/api/v1/register/**",
                                "/api/v1/users/reset-password/**",
                                "/api/v1/auth/login").permitAll()
                        .anyExchange().authenticated()
                )
                .addFilterAt(JWTAuthWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .oauth2Login(Customizer.withDefaults())
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationWebFilter JWTAuthWebFilter(ReactiveAuthenticationManager authManager,
                                                    ServerAuthenticationConverter authConverter) {
        var authWebFilter = new AuthenticationWebFilter(authManager);
        authWebFilter.setServerAuthenticationConverter(authConverter);
        return authWebFilter;
    }
}
