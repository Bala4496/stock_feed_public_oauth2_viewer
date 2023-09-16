package ua.bala.stock_feed_public_oauth2_viewer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${stock-feed-viewer-api.url}")
    private String apiUrl;

    @Bean
    public WebClient apiWebClient() {
        return WebClient.builder().baseUrl("http://" + apiUrl).build();
    }
}
