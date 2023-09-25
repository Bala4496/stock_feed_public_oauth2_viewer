package ua.bala.stock_feed_public_oauth2_viewer.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.bala.stock_feed_public_oauth2_viewer.dto.RegisterUserDTO;
import ua.bala.stock_feed_public_oauth2_viewer.model.entity.User;

@Mapper(componentModel = "spring")
public interface RegisterUserMapper {

    User map(RegisterUserDTO registerUserDTO);

    @Mapping(target = "password", ignore = true)
    RegisterUserDTO map(User user);
}
