package ua.bala.stock_feed_public_oauth2_viewer.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.bala.stock_feed_public_oauth2_viewer.dto.CompanyDTO;
import ua.bala.stock_feed_public_oauth2_viewer.mapper.CompanyMapper;
import ua.bala.stock_feed_public_oauth2_viewer.model.common.Company;

@Component
@Slf4j
@RequiredArgsConstructor
public class HttpCompanyClient implements CompanyClient {

    private static final String COMPANIES_API = "/api/v1/companies";
    private final WebClient webClient;
    private final CompanyMapper companyMapper;

    @Override
    public Flux<Company> fetchCompanies() {
        return getCompaniesFromApi().map(companyMapper::map);
    }

    private Flux<CompanyDTO> getCompaniesFromApi() {
        return webClient.get()
                .uri(COMPANIES_API)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        error -> Mono.error(new RuntimeException("API not found")))
                .onStatus(HttpStatusCode::is5xxServerError,
                        error -> Mono.error(new RuntimeException("Server is not responding")))
                .bodyToFlux(CompanyDTO.class)
                .retry();
    }
}
