package stockexchange;

import org.junit.jupiter.api.Test;

import stockexchange.events.*;
import stockexchange.feeds.*;
import stockexchange.listeners.MarketDashboard;
import stockexchange.service.MarketSimulator;

import java.math.BigDecimal;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class ConcurrencyTest {

    @Test
    void stressTestReceivesExactly4000Events() throws Exception {
        StockPriceFeed stockFeed = new StockPriceFeed();
        MarketIndexFeed indexFeed = new MarketIndexFeed();

        MarketDashboard dashboard = new MarketDashboard();

        stockFeed.subscribe(
                EventType.PRICE_CHANGED, dashboard
        );

        indexFeed.subscribe(
                EventType.INDEX_CHANGED, dashboard
        );

        MarketSimulator simulator = new MarketSimulator(
                stockFeed, indexFeed, 4
        );

        simulator.runSimulation(1000);

        assertEquals(4000, dashboard.getEventCount());
    }

    @Test
    void subscriberCanLeaveDuringConcurrentPublishing()
            throws Exception {

        StockPriceFeed feed = new StockPriceFeed();
        AtomicInteger count = new AtomicInteger();

        stockexchange.observer.Observer<MarketEvent> listener =
                event -> count.incrementAndGet();

        feed.subscribe(EventType.PRICE_CHANGED, listener);

        ExecutorService executor =
                Executors.newFixedThreadPool(4);

        try {
            CountDownLatch start = new CountDownLatch(1);

            Future<?>[] futures = new Future<?>[4];

            for (int thread = 0; thread < 4; thread++) {
                futures[thread] = executor.submit(() -> {
                    start.await();

                    for (int i = 0; i < 1000; i++) {
                        feed.updatePrice(
                                "AAPL", BigDecimal.valueOf(i + 1)
                        );
                    }
                    return null;
                });
            }

            start.countDown();

            feed.unsubscribe(EventType.PRICE_CHANGED, listener);

            for (Future<?> future : futures) {
                future.get();
            }

            assertTrue(count.get() >= 0);
            assertTrue(count.get() <= 4000);
        } finally {
            executor.shutdown();

            if (!executor.awaitTermination(
                    30, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        }
    }

    @Test
    void dashboardSupportsConcurrentUpdates() throws Exception {
        MarketDashboard dashboard = new MarketDashboard();

        ExecutorService executor =
                Executors.newFixedThreadPool(4);

        try {
            Future<?>[] futures = new Future<?>[4];

            for (int t = 0; t < 4; t++) {
                futures[t] = executor.submit(() -> {
                    for (int i = 0; i < 1000; i++) {
                        dashboard.onEvent(new MarketEvent(
                                EventType.PRICE_CHANGED,
                                "AAPL",
                                BigDecimal.ONE,
                                "Test"
                        ));
                    }
                });
            }

            for (Future<?> future : futures) {
                future.get();
            }

            assertEquals(4000, dashboard.getEventCount());
        } finally {
            executor.shutdown();

            if (!executor.awaitTermination(
                    30, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        }
    }
}
