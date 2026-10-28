
package stockexchange.observer;

import stockexchange.events.EventType;
import stockexchange.events.TypedEvent;

public interface Subject<E extends TypedEvent> {

    void subscribe(EventType type, Observer<E> observer);

    void unsubscribe(EventType type, Observer<E> observer);

    void publish(E event);
}
