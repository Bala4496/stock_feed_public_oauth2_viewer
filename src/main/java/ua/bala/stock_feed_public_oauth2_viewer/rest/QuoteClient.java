package ua.bala.stock_feed_public_oauth2_viewer.rest;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.bala.stock_feed_public_oauth2_viewer.model.Quote;
import ua.bala.stock_feed_public_oauth2_viewer.model.QuoteReport;

public interface QuoteClient {

    Mono<Quote> fetchQuoteByCompanyCode(String companyCode);
    Flux<Quote> fetchQuotes();
    Flux<QuoteReport> fetchReport();
}
