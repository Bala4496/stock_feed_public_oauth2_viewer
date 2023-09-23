package ua.bala.stock_feed_public_oauth2_viewer.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.bala.stock_feed_public_oauth2_viewer.dto.QuoteDTO;
import ua.bala.stock_feed_public_oauth2_viewer.model.common.Quote;

@Mapper(componentModel = "spring")
public interface QuoteMapper {

    @Mapping(target = "gapPercentage", ignore = true)
    Quote map(QuoteDTO quoteDTO);

    @InheritInverseConfiguration
    QuoteDTO map(Quote quote);
}
