package ua.bala.stock_feed_public_oauth2_viewer.email;

import ua.bala.stock_feed_public_oauth2_viewer.model.User;

public interface EmailService {

    void sendRegistrationConfirmationEmail(User user);
    void sendResetPasswordEmail(User user);
}
