package ua.bala.stock_feed_public_oauth2_viewer.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Quote {

    private String companyCode;
    private BigDecimal price;
    private BigDecimal gapPercentage;
    private LocalDateTime createdAt;
}