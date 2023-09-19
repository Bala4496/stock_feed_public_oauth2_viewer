package ua.bala.stock_feed_public_oauth2_viewer.security;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ua.bala.stock_feed_public_oauth2_viewer.service.RegisterService;

import javax.naming.OperationNotSupportedException;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomReactiveOAuth2AuthorizedClientService implements ReactiveOAuth2AuthorizedClientService {

    private final RegisterService registerService;

    @SneakyThrows
    @Override
    public <T extends OAuth2AuthorizedClient> Mono<T> loadAuthorizedClient(String clientRegistrationId, String principalName) {
        String operation = "loadAuthorizedClient";
        log.info(operation);
        throw new OperationNotSupportedException("Operation %s not supported".formatted(operation));
    }

    @Override
    public Mono<Void> saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication authentication) {
        log.info("saveAuthorizedClient");
        var registrationId = authorizedClient.getClientRegistration().getRegistrationId().toUpperCase();
        return Mono.justOrEmpty(authentication)
                .map(Authentication::getName)
                .flatMap(authName -> registerService.registerUser(registrationId, authName))
                .then();
    }

    @SneakyThrows
    @Override
    public Mono<Void> removeAuthorizedClient(String clientRegistrationId, String principalName) {
        String operation = "removeAuthorizedClient";
        log.info(operation);
        throw new OperationNotSupportedException("Operation %s not supported".formatted(operation));
    }
}
