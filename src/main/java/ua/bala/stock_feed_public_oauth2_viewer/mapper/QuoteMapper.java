package ua.bala.stock_feed_public_oauth2_viewer.mapper;

import org.mapstruct.Mapper;
import ua.bala.stock_feed_public_oauth2_viewer.dto.QuoteDTO;
import ua.bala.stock_feed_public_oauth2_viewer.model.common.Quote;

@Mapper(componentModel = "spring")
public interface QuoteMapper {

    Quote map(QuoteDTO quoteDTO);

    QuoteDTO map(Quote quote);
}
