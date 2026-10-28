
package stockexchange.feeds;

import stockexchange.events.*;
import stockexchange.observer.*;

import java.math.BigDecimal;

public class MarketIndexFeed extends EventPublisher<MarketEvent> {

    public void updateIndex(String indexName, BigDecimal indexValue) {
        if (indexValue.signum() < 0) {
            throw new IllegalArgumentException(
                    "Index value cannot be negative"
            );
        }

        publish(new MarketEvent(
                EventType.INDEX_CHANGED,
                indexName,
                indexValue,
                "MarketIndexFeed"
        ));
    }
}
