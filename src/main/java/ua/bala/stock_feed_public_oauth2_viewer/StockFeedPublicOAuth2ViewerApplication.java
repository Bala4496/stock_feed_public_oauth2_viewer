package ua.bala.stock_feed_public_oauth2_viewer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StockFeedPublicOAuth2ViewerApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockFeedPublicOAuth2ViewerApplication.class, args);
    }

}
