package ua.bala.stock_feed_public_oauth2_viewer.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.bala.stock_feed_public_oauth2_viewer.dto.QuoteDTO;
import ua.bala.stock_feed_public_oauth2_viewer.dto.QuoteReportDTO;
import ua.bala.stock_feed_public_oauth2_viewer.mapper.QuoteMapper;
import ua.bala.stock_feed_public_oauth2_viewer.mapper.QuoteReportMapper;
import ua.bala.stock_feed_public_oauth2_viewer.model.common.Quote;
import ua.bala.stock_feed_public_oauth2_viewer.model.common.QuoteReport;

@Component
@Slf4j
@RequiredArgsConstructor
public class HttpQuoteClient implements QuoteClient {

    private static final String QUOTES_API = "/api/v1/stocks/quotes";
    private static final String QUOTE_BY_CODE_API = "/api/v1/stocks/%s/quote";
    private static final String REPORTS_API = "/api/v1/stocks/reports";

    private final WebClient webClient;
    private final QuoteMapper quoteMapper;
    private final QuoteReportMapper quoteReportMapper;

    @Override
    public Flux<Quote> fetchQuotes() {
        return getQuotesFromApi().map(quoteMapper::map);
    }

    private Flux<QuoteDTO> getQuotesFromApi() {
        return webClient.get()
                .uri(QUOTES_API)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        error -> Mono.error(new RuntimeException("API not found")))
                .onStatus(HttpStatusCode::is5xxServerError,
                        error -> Mono.error(new RuntimeException("Server is not responding")))
                .bodyToFlux(QuoteDTO.class);
    }

    @Override
    public Mono<Quote> fetchQuoteByCompanyCode(String companyCode) {
        return getQuoteByCompanyCodeFromApi(companyCode).map(quoteMapper::map);
    }

    private Mono<QuoteDTO> getQuoteByCompanyCodeFromApi(String companyCode) {
        return webClient.get()
                .uri(QUOTE_BY_CODE_API.formatted(companyCode))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        error -> Mono.error(new RuntimeException("API not found")))
                .onStatus(HttpStatusCode::is5xxServerError,
                        error -> Mono.error(new RuntimeException("Server is not responding")))
                .bodyToMono(QuoteDTO.class)
                .retry();
    }

    @Override
    public Flux<QuoteReport> fetchReport() {
        return getReportFromApi().map(quoteReportMapper::map);
    }

    private Flux<QuoteReportDTO> getReportFromApi() {
        return webClient.get()
                .uri(REPORTS_API)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        error -> Mono.error(new RuntimeException("API not found")))
                .onStatus(HttpStatusCode::is5xxServerError,
                        error -> Mono.error(new RuntimeException("Server is not responding")))
                .bodyToFlux(QuoteReportDTO.class)
                .retry();
    }
}
