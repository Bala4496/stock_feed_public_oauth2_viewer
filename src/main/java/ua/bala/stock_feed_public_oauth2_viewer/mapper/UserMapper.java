package ua.bala.stock_feed_public_oauth2_viewer.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import ua.bala.stock_feed_public_oauth2_viewer.dto.UserDTO;
import ua.bala.stock_feed_public_oauth2_viewer.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @InheritInverseConfiguration
    UserDTO map(User user);

    User map(UserDTO userDto);
}
