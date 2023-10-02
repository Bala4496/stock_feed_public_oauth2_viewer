package ua.bala.stock_feed_public_oauth2_viewer.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.experimental.Accessors;
import ua.bala.stock_feed_public_oauth2_viewer.model.enums.UserRole;

@Data
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RegisterUserDTO {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private UserRole role;
    private boolean enabled;
}
