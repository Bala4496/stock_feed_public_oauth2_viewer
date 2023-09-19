package ua.bala.stock_feed_public_oauth2_viewer.model;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table(name = "users")
@Data
@Accessors(chain = true)
public class User {

    @Id
    private Long id;
    private String email;
    private String username;
    private String password;
    private UserRole role;
    private Provider provider;
    private boolean enabled;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
