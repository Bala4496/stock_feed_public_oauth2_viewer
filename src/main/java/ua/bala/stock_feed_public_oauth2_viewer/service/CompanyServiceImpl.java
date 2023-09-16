package ua.bala.stock_feed_public_oauth2_viewer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import ua.bala.stock_feed_public_oauth2_viewer.model.Company;
import ua.bala.stock_feed_public_oauth2_viewer.rest.CompanyClient;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyClient companyClient;

    @Override
    public Flux<Company> getCompanies() {
        return companyClient.fetchCompanies();
    }
}
