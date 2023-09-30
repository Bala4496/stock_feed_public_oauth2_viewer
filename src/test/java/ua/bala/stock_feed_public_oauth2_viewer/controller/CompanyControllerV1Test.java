package ua.bala.stock_feed_public_oauth2_viewer.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import ua.bala.stock_feed_public_oauth2_viewer.client.CompanyClient;
import ua.bala.stock_feed_public_oauth2_viewer.dto.CompanyDTO;
import ua.bala.stock_feed_public_oauth2_viewer.mapper.CompanyMapper;
import ua.bala.stock_feed_public_oauth2_viewer.model.common.Company;

import static org.mockito.Mockito.when;

class CompanyControllerV1Test extends BaseIntegrationTest {

    @MockBean
    private CompanyClient companyClient;
    @Autowired
    private CompanyMapper companyMapper;

    @Test
    @WithMockUser(username = TEST_EMAIL)
    void shouldGetCompanies() {
        var companyA = new Company().setCode("AAPL").setName("Apple");
        var companyB = new Company().setCode("GOOGL").setName("Google");
        var companyC = new Company().setCode("GH").setName("GitHub");

        when(companyClient.fetchCompanies()).thenReturn(Flux.just(companyA, companyB, companyC));

        var result = webClient.get()
                .uri("/api/v1/companies")
                .exchange()
                .expectStatus().isOk()
                .returnResult(CompanyDTO.class);

        StepVerifier.create(result.getResponseBody())
                .expectNext(companyMapper.map(companyA))
                .expectNext(companyMapper.map(companyB))
                .expectNext(companyMapper.map(companyC))
                .verifyComplete();
    }
}
