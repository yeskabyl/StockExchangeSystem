
package stockexchange.listeners;

import stockexchange.events.MarketEvent;
import stockexchange.observer.Observer;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TradeLogger implements Observer<MarketEvent> {

    private final ConcurrentLinkedQueue<MarketEvent> events =
            new ConcurrentLinkedQueue<>();

    @Override
    public void onEvent(MarketEvent event) {
        events.add(event);
    }

    public int getEventCount() {
        return events.size();
    }

    public List<MarketEvent> getEvents() {
        return List.copyOf(events);
    }
}
