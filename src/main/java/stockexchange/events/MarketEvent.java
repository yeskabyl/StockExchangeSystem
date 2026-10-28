
package stockexchange.events;

import java.math.BigDecimal;
import java.util.Objects;

public record MarketEvent(
        EventType type,
        String symbol,
        BigDecimal value,
        String source
) implements TypedEvent {

    public MarketEvent {
        Objects.requireNonNull(type);
        Objects.requireNonNull(symbol);
        Objects.requireNonNull(value);
        Objects.requireNonNull(source);
    }
}