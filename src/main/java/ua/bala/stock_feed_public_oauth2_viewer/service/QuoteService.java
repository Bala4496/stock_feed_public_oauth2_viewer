package ua.bala.stock_feed_public_oauth2_viewer.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.bala.stock_feed_public_oauth2_viewer.model.common.Quote;
import ua.bala.stock_feed_public_oauth2_viewer.model.common.QuoteReport;

public interface QuoteService {

    Flux<Quote> getQuotes();
    Mono<Quote> getQuoteByCompanyCode(String companyCode);
    Flux<QuoteReport> getReport();
}
