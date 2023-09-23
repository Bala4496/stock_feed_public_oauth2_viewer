package ua.bala.stock_feed_public_oauth2_viewer.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import ua.bala.stock_feed_public_oauth2_viewer.dto.CompanyDTO;
import ua.bala.stock_feed_public_oauth2_viewer.model.common.Company;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

    Company map(CompanyDTO companyDTO);

    @InheritInverseConfiguration
    CompanyDTO map(Company company);
}
