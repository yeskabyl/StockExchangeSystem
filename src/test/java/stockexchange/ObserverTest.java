
package stockexchange;

import org.junit.jupiter.api.Test;

import stockexchange.events.*;
import stockexchange.feeds.*;
import stockexchange.listeners.*;
import stockexchange.observer.*;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class ObserverTest {

    @Test
    void subscriberReceivesPriceEvent() {
        StockPriceFeed feed = new StockPriceFeed();
        AtomicInteger count = new AtomicInteger();

        feed.subscribe(EventType.PRICE_CHANGED,
                event -> count.incrementAndGet());

        feed.updatePrice("AAPL", new BigDecimal("100"));

        assertEquals(1, count.get());
    }

    @Test
    void differentEventTypeIsIgnored() {
        EventPublisher<MarketEvent> publisher =
                new EventPublisher<>();

        AtomicInteger count = new AtomicInteger();

        publisher.subscribe(EventType.INDEX_CHANGED,
                event -> count.incrementAndGet());

        publisher.publish(new MarketEvent(
                EventType.PRICE_CHANGED,
                "AAPL", BigDecimal.TEN, "Test"
        ));

        assertEquals(0, count.get());
    }

    @Test
    void listenerCanUnsubscribe() {
        StockPriceFeed feed = new StockPriceFeed();
        AtomicInteger count = new AtomicInteger();

        Observer<MarketEvent> listener =
                event -> count.incrementAndGet();

        feed.subscribe(EventType.PRICE_CHANGED, listener);
        feed.updatePrice("AAPL", BigDecimal.TEN);

        feed.unsubscribe(EventType.PRICE_CHANGED, listener);
        feed.updatePrice("AAPL", BigDecimal.TEN);

        assertEquals(1, count.get());
    }

    @Test
    void listenerCanUnsubscribeInsideHandler() {
        StockPriceFeed feed = new StockPriceFeed();
        AtomicInteger count = new AtomicInteger();

        @SuppressWarnings("unchecked")
        Observer<MarketEvent>[] reference = new Observer[1];

        reference[0] = event -> {
            count.incrementAndGet();
            feed.unsubscribe(
                    EventType.PRICE_CHANGED, reference[0]
            );
        };

        feed.subscribe(
                EventType.PRICE_CHANGED, reference[0]
        );

        feed.updatePrice("AAPL", BigDecimal.TEN);
        feed.updatePrice("AAPL", BigDecimal.TEN);

        assertEquals(1, count.get());
    }

    @Test
    void duplicateSubscriptionIsPrevented() {
        StockPriceFeed feed = new StockPriceFeed();
        AtomicInteger count = new AtomicInteger();

        Observer<MarketEvent> listener =
                event -> count.incrementAndGet();

        feed.subscribe(EventType.PRICE_CHANGED, listener);
        feed.subscribe(EventType.PRICE_CHANGED, listener);

        feed.updatePrice("AAPL", BigDecimal.ONE);

        assertEquals(1, count.get());
    }

    @Test
    void movingAverageIsCalculatedCorrectly() {
        MovingAverageTracker tracker =
                new MovingAverageTracker(3);

        StockPriceFeed feed = new StockPriceFeed();

        feed.subscribe(EventType.PRICE_CHANGED, tracker);

        feed.updatePrice("AAPL", new BigDecimal("10"));
        feed.updatePrice("AAPL", new BigDecimal("20"));
        feed.updatePrice("AAPL", new BigDecimal("30"));
        feed.updatePrice("AAPL", new BigDecimal("40"));

        assertEquals(
                0,
                new BigDecimal("30.00").compareTo(
                        tracker.getAverage("AAPL")
                )
        );
    }

    @Test
    void aggregatorRepublishesUpdates() {
        StockPriceFeed feed = new StockPriceFeed();
        MarketAggregator aggregator = new MarketAggregator();

        AtomicInteger count = new AtomicInteger();

        feed.subscribe(EventType.PRICE_CHANGED, aggregator);

        aggregator.subscribe(
                EventType.AGGREGATE_UPDATED,
                event -> count.incrementAndGet()
        );

        feed.updatePrice("AAPL", new BigDecimal("100"));
        feed.updatePrice("MSFT", new BigDecimal("200"));

        assertEquals(2, count.get());
    }

    @Test
    void negativePriceIsRejected() {
        StockPriceFeed feed = new StockPriceFeed();

        assertThrows(
                IllegalArgumentException.class,
                () -> feed.updatePrice(
                        "AAPL", new BigDecimal("-5")
                )
        );
    }
}
