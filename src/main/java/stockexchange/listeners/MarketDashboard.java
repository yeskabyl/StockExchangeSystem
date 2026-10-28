
package stockexchange.listeners;

import stockexchange.events.MarketEvent;
import stockexchange.observer.Observer;

import java.util.concurrent.atomic.AtomicInteger;

public class MarketDashboard
        implements Observer<MarketEvent> {

    private final AtomicInteger eventCount = new AtomicInteger();

    @Override
    public void onEvent(MarketEvent event) {
        eventCount.incrementAndGet();
    }

    public int getEventCount() {
        return eventCount.get();
    }
}
