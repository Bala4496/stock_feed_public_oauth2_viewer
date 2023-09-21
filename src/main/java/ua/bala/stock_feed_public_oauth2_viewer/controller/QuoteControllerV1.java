package ua.bala.stock_feed_public_oauth2_viewer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.bala.stock_feed_public_oauth2_viewer.dto.QuoteDTO;
import ua.bala.stock_feed_public_oauth2_viewer.dto.QuoteReportDTO;
import ua.bala.stock_feed_public_oauth2_viewer.mapper.QuoteMapper;
import ua.bala.stock_feed_public_oauth2_viewer.mapper.QuoteReportMapper;
import ua.bala.stock_feed_public_oauth2_viewer.service.QuoteService;

@RestController
@RequestMapping("/api/v1/quotes")
@RequiredArgsConstructor
public class QuoteControllerV1 {

    private final QuoteService quoteService;
    private final QuoteMapper quoteMapper;
    private final QuoteReportMapper quoteReportMapper;

    @GetMapping
    public Flux<QuoteDTO> getQuotes() {
        return quoteService.getQuotes().map(quoteMapper::map);
    }

    @GetMapping("/{code}")
    public Mono<QuoteDTO> getQuotesByCompanyCode(@PathVariable String code) {
        return quoteService.getQuoteByCompanyCode(code).map(quoteMapper::map);
    }

    @GetMapping("/report")
    public Flux<QuoteReportDTO> getQuoteReport() {
        return quoteService.getReport().map(quoteReportMapper::map);
    }
}
