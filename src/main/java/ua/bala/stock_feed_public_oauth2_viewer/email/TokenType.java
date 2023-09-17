package ua.bala.stock_feed_public_oauth2_viewer.email;

import lombok.Getter;

@Getter
public enum TokenType {
    REGISTER(60),
    RESET_PASSWORD(15);

    private final long expirationLimit;

    TokenType(long expirationLimit) {
        this.expirationLimit = expirationLimit;
    }
}
