
package stockexchange.feeds;

import stockexchange.events.*;
import stockexchange.observer.*;

import java.math.BigDecimal;

public class StockPriceFeed extends EventPublisher<MarketEvent> {

    public void updatePrice(String symbol, BigDecimal price) {
        if (price.signum() < 0) {
            throw new IllegalArgumentException(
                    "Stock price cannot be negative"
            );
        }

        publish(new MarketEvent(
                EventType.PRICE_CHANGED,
                symbol,
                price,
                "StockPriceFeed"
        ));
    }
}
