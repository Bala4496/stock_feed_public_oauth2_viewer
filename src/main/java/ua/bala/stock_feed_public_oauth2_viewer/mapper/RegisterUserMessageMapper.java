package ua.bala.stock_feed_public_oauth2_viewer.mapper;

import org.mapstruct.Mapper;
import ua.bala.stock_feed_public_oauth2_viewer.model.entity.User;
import ua.bala.stock_feed_public_oauth2_viewer.model.messages.RegisterUserMessage;

@Mapper(componentModel = "spring")
public interface RegisterUserMessageMapper {

    RegisterUserMessage map(User user);
}
