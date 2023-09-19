package ua.bala.stock_feed_public_oauth2_viewer.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.bala.stock_feed_public_oauth2_viewer.dto.QuoteReportDTO;
import ua.bala.stock_feed_public_oauth2_viewer.model.QuoteReport;

@Mapper(componentModel = "spring")
public interface QuoteReportMapper {

    @Mapping(target = "quotes.gapPercentage", ignore = true)
    QuoteReport map(QuoteReportDTO quoteReportDTO);

    QuoteReportDTO map(QuoteReport quoteReport);
}
