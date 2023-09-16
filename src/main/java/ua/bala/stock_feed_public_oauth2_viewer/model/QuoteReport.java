package ua.bala.stock_feed_public_oauth2_viewer.model;

import lombok.Data;

import java.util.List;

@Data
public class QuoteReport {

    private String title;
    private List<Quote> quotes;
}
