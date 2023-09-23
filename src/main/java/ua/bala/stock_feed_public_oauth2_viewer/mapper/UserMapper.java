package ua.bala.stock_feed_public_oauth2_viewer.mapper;

import org.mapstruct.Mapper;
import ua.bala.stock_feed_public_oauth2_viewer.dto.UserDTO;
import ua.bala.stock_feed_public_oauth2_viewer.model.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User map(UserDTO userDto);

    UserDTO map(User user);
}
