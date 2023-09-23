package ua.bala.stock_feed_public_oauth2_viewer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.bala.stock_feed_public_oauth2_viewer.client.QuoteClient;
import ua.bala.stock_feed_public_oauth2_viewer.model.common.Quote;
import ua.bala.stock_feed_public_oauth2_viewer.model.common.QuoteReport;

@Service
@RequiredArgsConstructor
public class QuoteServiceImpl implements QuoteService {

    private final QuoteClient quoteClient;

    @Override
    public Flux<Quote> getQuotes() {
        return quoteClient.fetchQuotes();
    }

    @Override
    public Mono<Quote> getQuoteByCompanyCode(String companyCode) {
        return quoteClient.fetchQuoteByCompanyCode(companyCode)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NO_CONTENT, "Stock quote by code '%s' not found".formatted(companyCode))));
    }

    @Override
    public Flux<QuoteReport> getReport() {
        return quoteClient.fetchReport();
    }
}
