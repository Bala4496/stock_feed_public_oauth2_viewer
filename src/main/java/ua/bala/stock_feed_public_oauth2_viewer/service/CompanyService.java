package ua.bala.stock_feed_public_oauth2_viewer.service;

import reactor.core.publisher.Flux;
import ua.bala.stock_feed_public_oauth2_viewer.model.common.Company;

public interface CompanyService {
    Flux<Company> getCompanies();
}
