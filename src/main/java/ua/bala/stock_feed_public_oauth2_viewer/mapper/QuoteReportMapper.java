package ua.bala.stock_feed_public_oauth2_viewer.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.bala.stock_feed_public_oauth2_viewer.dto.QuoteReportDTO;
import ua.bala.stock_feed_public_oauth2_viewer.model.QuoteReport;

@Mapper(componentModel = "spring")
public interface QuoteReportMapper {

    QuoteReport map(QuoteReportDTO quoteReportDTO);

    @Mapping(target = "quotes", source = "quotes")
    @Mapping(target = "quotes.gapPercentage", ignore = true)
    @InheritInverseConfiguration
    QuoteReportDTO map(QuoteReport quoteReport);
}
