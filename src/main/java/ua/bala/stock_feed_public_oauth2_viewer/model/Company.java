package ua.bala.stock_feed_public_oauth2_viewer.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Company {

    private String code;
    private String name;
}
