package ua.bala.stock_feed_public_oauth2_viewer.model.common;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class Quote {

    private String companyCode;
    private BigDecimal price;
    private BigDecimal gapPercentage;
    private LocalDateTime createdAt;
}