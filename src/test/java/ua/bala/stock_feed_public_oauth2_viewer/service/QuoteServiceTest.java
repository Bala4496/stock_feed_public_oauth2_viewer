package ua.bala.stock_feed_public_oauth2_viewer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ua.bala.stock_feed_public_oauth2_viewer.client.QuoteClient;
import ua.bala.stock_feed_public_oauth2_viewer.model.common.Quote;
import ua.bala.stock_feed_public_oauth2_viewer.model.common.QuoteReport;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class QuoteServiceTest {

    @InjectMocks
    private QuoteServiceImpl quoteService;
    @Mock
    private QuoteClient quoteClient;

    @Test
    void shouldGetQuotes() {
        var quotes = getQuotes();

        when(quoteClient.fetchQuotes()).thenReturn(Flux.fromIterable(quotes));

        var result = quoteService.getQuotes();

        StepVerifier.create(result)
                .expectNextSequence(quotes)
                .verifyComplete();
    }

    @Test
    void shouldGetQuoteByCompanyCode() {
        var testQuote = new Quote().setCompanyCode("AAPL")
                .setPrice(new BigDecimal("100.00"))
                .setGapPercentage(new BigDecimal("10.00"));

        when(quoteClient.fetchQuoteByCompanyCode("AAPL")).thenReturn(Mono.just(testQuote));

        var result = quoteService.getQuoteByCompanyCode("AAPL");

        StepVerifier.create(result)
                .consumeNextWith(quote -> {
                    assertEquals(quote.getCompanyCode(), "AAPL");
                    assertEquals(quote.getPrice(), new BigDecimal("100.00"));
                    assertEquals(quote.getGapPercentage(), new BigDecimal("10.00"));
                })
                .verifyComplete();
    }

    @Test
    void shouldGetReport() {
        var testQuoteReport = new QuoteReport().setTitle("TestQuoteReport").setQuotes(getQuotes());

        when(quoteClient.fetchReport()).thenReturn(Flux.just(testQuoteReport));

        var result = quoteService.getReport();

        StepVerifier.create(result)
                .consumeNextWith(quoteReport -> {
                    assertEquals(quoteReport.getTitle(), "TestQuoteReport");
                    assertEquals(quoteReport.getQuotes().size(), 3);
                })
                .verifyComplete();
    }

    private static List<Quote> getQuotes() {
        return List.of(
                new Quote().setCompanyCode("AAPL").setPrice(new BigDecimal("100.00")).setGapPercentage(new BigDecimal("10.00")),
                new Quote().setCompanyCode("GOOGL").setPrice(new BigDecimal("50.00")).setGapPercentage(new BigDecimal("20.00")),
                new Quote().setCompanyCode("GH").setPrice(new BigDecimal("75.00")).setGapPercentage(new BigDecimal("15.00"))
        );
    }
}
