
package stockexchange.listeners;

import stockexchange.events.*;
import stockexchange.observer.Observer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class MovingAverageTracker
        implements Observer<MarketEvent> {

    private final int windowSize;
    private final Map<String, Deque<BigDecimal>> history =
            new ConcurrentHashMap<>();

    public MovingAverageTracker(int windowSize) {
        if (windowSize <= 0) {
            throw new IllegalArgumentException(
                    "Window size must be positive"
            );
        }
        this.windowSize = windowSize;
    }

    @Override
    public void onEvent(MarketEvent event) {
        if (event.type() != EventType.PRICE_CHANGED) {
            return;
        }

        Deque<BigDecimal> prices = history.computeIfAbsent(
                event.symbol(),
                key -> new ArrayDeque<>()
        );

        synchronized (prices) {
            prices.addLast(event.value());

            if (prices.size() > windowSize) {
                prices.removeFirst();
            }
        }
    }

    public BigDecimal getAverage(String symbol) {
        Deque<BigDecimal> prices = history.get(symbol);

        if (prices == null) {
            return BigDecimal.ZERO;
        }

        synchronized (prices) {
            if (prices.isEmpty()) {
                return BigDecimal.ZERO;
            }

            BigDecimal total = BigDecimal.ZERO;

            for (BigDecimal price : prices) {
                total = total.add(price);
            }

            return total.divide(
                    BigDecimal.valueOf(prices.size()),
                    2,
                    RoundingMode.HALF_UP
            );
        }
    }
}
