package ua.bala.stock_feed_public_oauth2_viewer.model.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import ua.bala.stock_feed_public_oauth2_viewer.model.enums.TokenType;

import java.time.LocalDateTime;

@Table("tokens")
@Data
@Accessors(chain = true)
public class Token {

    @Id
    private Long id;
    private String token;
    private TokenType type;
    @Column("user_id")
    private long userId;
    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;
    @Column("expire_at")
    private LocalDateTime expireAt;
    @Transient
    private boolean isExpired;

    public boolean isExpired() {
        return expireAt.isBefore(LocalDateTime.now());
    }
}
