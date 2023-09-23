package ua.bala.stock_feed_public_oauth2_viewer.service.email;

import ua.bala.stock_feed_public_oauth2_viewer.model.entity.User;

public interface EmailService {

    void sendRegistrationEmail(User user);
    void sendResetPasswordEmail(User user);
}
