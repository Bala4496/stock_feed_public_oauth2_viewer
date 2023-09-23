package ua.bala.stock_feed_public_oauth2_viewer.model.common;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class QuoteReport {

    private String title;
    private List<Quote> quotes;
}
