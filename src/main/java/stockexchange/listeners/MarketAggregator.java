
package stockexchange.listeners;

import stockexchange.events.*;
import stockexchange.observer.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class MarketAggregator
        extends EventPublisher<MarketEvent>
        implements Observer<MarketEvent> {

    private final Map<String, BigDecimal> latestPrices =
            new ConcurrentHashMap<>();

    @Override
    public void onEvent(MarketEvent event) {
        if (event.type() != EventType.PRICE_CHANGED) {
            return;
        }

        latestPrices.put(event.symbol(), event.value());

        BigDecimal total = BigDecimal.ZERO;

        for (BigDecimal price : latestPrices.values()) {
            total = total.add(price);
        }

        BigDecimal average = total.divide(
                BigDecimal.valueOf(latestPrices.size()),
                2,
                RoundingMode.HALF_UP
        );

        publish(new MarketEvent(
                EventType.AGGREGATE_UPDATED,
                "MARKET_AVERAGE",
                average,
                "MarketAggregator"
        ));
    }
}
