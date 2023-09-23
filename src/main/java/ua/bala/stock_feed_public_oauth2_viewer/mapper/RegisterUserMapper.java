package ua.bala.stock_feed_public_oauth2_viewer.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.bala.stock_feed_public_oauth2_viewer.dto.RegisterUserDTO;
import ua.bala.stock_feed_public_oauth2_viewer.model.entity.User;

@Mapper(componentModel = "spring")
public interface RegisterUserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "provider", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User map(RegisterUserDTO registerUserDTO);

    @Mapping(target = "password", ignore = true)
    @InheritInverseConfiguration
    RegisterUserDTO map(User user);
}
