package ua.bala.stock_feed_public_oauth2_viewer.service.email;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ua.bala.stock_feed_public_oauth2_viewer.mapper.RegisterUserMessageMapper;
import ua.bala.stock_feed_public_oauth2_viewer.model.entity.User;
import ua.bala.stock_feed_public_oauth2_viewer.model.messages.RegisterUserMessage;

@Service
@RequiredArgsConstructor
public class RegistrationEmailMessageProducerImpl implements RegistrationEmailMessageProducer {

    private final KafkaTemplate<Long, RegisterUserMessage> kafkaRegisterTemplate;
    private final RegisterUserMessageMapper registerUserMessageMapper;
    private final TokenService tokenService;

    @Override
    public void sendRegistrationEmail(User user) {
        tokenService.createRegisterToken(user)
                .doOnSuccess(token -> kafkaRegisterTemplate.sendDefault(registerUserMessageMapper.map(user).setToken(token.getToken())))
                .subscribe();
    }

}
