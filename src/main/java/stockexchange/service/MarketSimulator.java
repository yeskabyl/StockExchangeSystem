
package stockexchange.service;

import stockexchange.feeds.StockPriceFeed;
import stockexchange.feeds.MarketIndexFeed;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class MarketSimulator {

    private final StockPriceFeed stockFeed;
    private final MarketIndexFeed indexFeed;
    private final int threadCount;

    public MarketSimulator(
            StockPriceFeed stockFeed,
            MarketIndexFeed indexFeed,
            int threadCount
    ) {
        if (threadCount < 3) {
            throw new IllegalArgumentException(
                    "Use at least 3 producer threads"
            );
        }

        this.stockFeed = stockFeed;
        this.indexFeed = indexFeed;
        this.threadCount = threadCount;
    }

    public void runSimulation(int eventsPerProducer)
            throws InterruptedException, ExecutionException {

        if (eventsPerProducer < 0) {
            throw new IllegalArgumentException(
                    "Event count cannot be negative"
            );
        }

        ExecutorService executor =
                Executors.newFixedThreadPool(threadCount);

        List<Future<?>> futures = new ArrayList<>();

        try {
            for (int producer = 0; producer < threadCount; producer++) {
                final int producerId = producer;

                futures.add(executor.submit(() -> {
                    for (int i = 0; i < eventsPerProducer; i++) {

                        if (producerId == threadCount - 1) {
                            indexFeed.updateIndex(
                                    "SP500",
                                    BigDecimal.valueOf(4500L + i)
                            );
                        } else {
                            String symbol =
                                    producerId % 2 == 0 ? "AAPL" : "MSFT";

                            BigDecimal price = BigDecimal.valueOf(
                                    80L + producerId * 10L + (i % 50)
                            );

                            stockFeed.updatePrice(symbol, price);
                        }
                    }
                }));
            }

            for (Future<?> future : futures) {
                future.get();
            }
        } finally {
            executor.shutdown();

            try {
                if (!executor.awaitTermination(
                        30, TimeUnit.SECONDS)) {
                    executor.shutdownNow();

                    if (!executor.awaitTermination(
                            30, TimeUnit.SECONDS)) {
                        throw new IllegalStateException(
                                "Executor failed to terminate"
                        );
                    }
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
                throw e;
            }
        }
    }
}
