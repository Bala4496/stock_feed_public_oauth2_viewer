package ua.bala.stock_feed_public_oauth2_viewer.model.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import ua.bala.stock_feed_public_oauth2_viewer.model.enums.Provider;
import ua.bala.stock_feed_public_oauth2_viewer.model.enums.UserRole;

import java.time.LocalDateTime;

@Table(name = "users")
@Data
@Accessors(chain = true)
public class User {

    @Id
    private Long id;
    @Column("first_name")
    private String firstName;
    @Column("last_name")
    private String lastName;
    private String email;
    private String password;
    private UserRole role;
    private Provider provider;
    private boolean enabled;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
