package ua.bala.stock_feed_public_oauth2_viewer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import ua.bala.stock_feed_public_oauth2_viewer.client.CompanyClient;
import ua.bala.stock_feed_public_oauth2_viewer.model.common.Company;

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class CompanyServiceTest {

    @InjectMocks
    private CompanyServiceImpl companyService;
    @Mock
    private CompanyClient companyClient;

    @Test
    void shouldGetCompanies() {
        var companies = getCompanies();

        when(companyClient.fetchCompanies()).thenReturn(Flux.fromIterable(companies));

        var result = companyService.getCompanies();

        StepVerifier.create(result)
                .expectNextSequence(companies)
                .verifyComplete();
    }

    private static List<Company> getCompanies() {
        return List.of(
                new Company().setCode("AAPL").setName("Apple"),
                new Company().setCode("GOOGL").setName("Google"),
                new Company().setCode("GH").setName("GitHub")
        );
    }
}
