package ua.bala.stock_feed_public_oauth2_viewer.model.messages;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RegisterUserMessage {
    String firstName;
    String lastName;
    String email;
    String token;
}
