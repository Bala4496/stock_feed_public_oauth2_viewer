package ua.bala.stock_feed_public_oauth2_viewer.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.bala.stock_feed_public_oauth2_viewer.dto.UserDTO;
import ua.bala.stock_feed_public_oauth2_viewer.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "provider", ignore = true)
    User map(UserDTO userDto);

    UserDTO map(User user);
}
