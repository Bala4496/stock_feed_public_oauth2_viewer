package ua.bala.stock_feed_public_oauth2_viewer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import ua.bala.stock_feed_public_oauth2_viewer.dto.CompanyDTO;
import ua.bala.stock_feed_public_oauth2_viewer.mapper.CompanyMapper;
import ua.bala.stock_feed_public_oauth2_viewer.service.CompanyService;

@RestController
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
public class CompaniesControllerV1 {

    private final CompanyService companyService;
    private final CompanyMapper companyMapper;

    @GetMapping
    public Flux<CompanyDTO> getCompanies() {
        return companyService.getCompanies()
                .map(companyMapper::map);
    }
}
