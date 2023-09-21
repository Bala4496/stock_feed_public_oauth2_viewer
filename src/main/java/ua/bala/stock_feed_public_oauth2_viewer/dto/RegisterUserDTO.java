package ua.bala.stock_feed_public_oauth2_viewer.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import ua.bala.stock_feed_public_oauth2_viewer.model.UserRole;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RegisterUserDTO {

    private String email;
    private String password;
    private UserRole role;
    private boolean enabled;
}
