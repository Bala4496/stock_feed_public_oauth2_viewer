package ua.bala.stock_feed_public_oauth2_viewer.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ua.bala.stock_feed_public_oauth2_viewer.client.QuoteClient;
import ua.bala.stock_feed_public_oauth2_viewer.config.WebFluxSecurityConfig;
import ua.bala.stock_feed_public_oauth2_viewer.dto.QuoteDTO;
import ua.bala.stock_feed_public_oauth2_viewer.dto.QuoteReportDTO;
import ua.bala.stock_feed_public_oauth2_viewer.mapper.QuoteMapper;
import ua.bala.stock_feed_public_oauth2_viewer.model.common.Quote;
import ua.bala.stock_feed_public_oauth2_viewer.model.common.QuoteReport;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@Import(WebFluxSecurityConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class QuoteControllerV1Test {

    private static final String TEST_EMAIL = "test.account@gmail.com";
    @Autowired
    private WebTestClient webClient;
    @MockBean
    private QuoteClient quoteClient;
    @Autowired
    private QuoteMapper quoteMapper;

    @Test
    @WithMockUser(username = TEST_EMAIL)
    void shouldGetQuotes() {
        var quoteA = new Quote().setCompanyCode("AAPL").setPrice(new BigDecimal("100.00")).setGapPercentage(new BigDecimal("10.00"));
        var quoteB = new Quote().setCompanyCode("GOOGL").setPrice(new BigDecimal("50.00")).setGapPercentage(new BigDecimal("20.00"));
        var quoteC = new Quote().setCompanyCode("GH").setPrice(new BigDecimal("75.00")).setGapPercentage(new BigDecimal("15.00"));

        when(quoteClient.fetchQuotes()).thenReturn(Flux.just(quoteA, quoteB, quoteC));

        var result = webClient.get()
                .uri("/api/v1/quotes")
                .exchange()
                .expectStatus().isOk()
                .returnResult(QuoteDTO.class);

        StepVerifier.create(result.getResponseBody())
                .expectNext(quoteMapper.map(quoteA))
                .expectNext(quoteMapper.map(quoteB))
                .expectNext(quoteMapper.map(quoteC))
                .verifyComplete();
    }

    @Test
    @WithMockUser(username = TEST_EMAIL)
    void shouldGetQuotesByCompanyCode() {
        var testQuote = new Quote().setCompanyCode("AAPL")
                .setPrice(new BigDecimal("100.00"))
                .setGapPercentage(new BigDecimal("10.00"));

        when(quoteClient.fetchQuoteByCompanyCode("AAPL")).thenReturn(Mono.just(testQuote));

        var result = webClient.get()
                .uri("/api/v1/quotes/{code}", "AAPL")
                .exchange()
                .expectStatus().isOk()
                .returnResult(QuoteDTO.class);

        StepVerifier.create(result.getResponseBody())
                .expectNext(quoteMapper.map(testQuote))
                .verifyComplete();
    }

    @Test
    @WithMockUser(username = TEST_EMAIL)
    void shouldGetQuoteReport() {
        var quoteA = new Quote().setCompanyCode("AAPL").setPrice(new BigDecimal("100.00")).setGapPercentage(new BigDecimal("10.00"));
        var quoteB = new Quote().setCompanyCode("GOOGL").setPrice(new BigDecimal("50.00")).setGapPercentage(new BigDecimal("20.00"));
        var quoteC = new Quote().setCompanyCode("GH").setPrice(new BigDecimal("75.00")).setGapPercentage(new BigDecimal("15.00"));

        var testQuoteReport = new QuoteReport().setTitle("TestQuoteReport").setQuotes(List.of(quoteA, quoteB, quoteC));

        when(quoteClient.fetchReport()).thenReturn(Flux.just(testQuoteReport));

        var result = webClient.get()
                .uri("/api/v1/quotes/report")
                .exchange()
                .expectStatus().isOk()
                .returnResult(QuoteReportDTO.class);

        StepVerifier.create(result.getResponseBody())
                .consumeNextWith(quoteReportDTO -> {
                    assertEquals(quoteReportDTO.getTitle(), "TestQuoteReport");
                    assertEquals(quoteReportDTO.getQuotes().size(), 3);
                })
                .verifyComplete();
    }
}
