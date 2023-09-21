package ua.bala.stock_feed_public_oauth2_viewer.client;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.bala.stock_feed_public_oauth2_viewer.model.Quote;
import ua.bala.stock_feed_public_oauth2_viewer.model.QuoteReport;

public interface QuoteClient {

    Flux<Quote> fetchQuotes();
    Mono<Quote> fetchQuoteByCompanyCode(String companyCode);
    Flux<QuoteReport> fetchReport();
}
