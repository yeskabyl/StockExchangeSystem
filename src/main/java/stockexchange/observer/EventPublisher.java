
package stockexchange.observer;

import stockexchange.events.EventType;
import stockexchange.events.TypedEvent;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Map;

public class EventPublisher<E extends TypedEvent>
        implements Subject<E> {

    private final Map<EventType,
            CopyOnWriteArrayList<Observer<E>>> listeners =
            new ConcurrentHashMap<>();

    @Override
    public void subscribe(EventType type, Observer<E> observer) {
        listeners.computeIfAbsent(
                type,
                key -> new CopyOnWriteArrayList<>()
        ).addIfAbsent(observer);
    }

    @Override
    public void unsubscribe(EventType type, Observer<E> observer) {
        var subscribers = listeners.get(type);

        if (subscribers != null) {
            subscribers.remove(observer);
        }
    }

    @Override
    public void publish(E event) {
        var subscribers = listeners.get(event.type());

        if (subscribers == null) {
            return;
        }

        for (Observer<E> observer : subscribers) {
            observer.onEvent(event);
        }
    }
}
