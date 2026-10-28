
package stockexchange.listeners;

import stockexchange.events.*;
import stockexchange.model.*;
import stockexchange.observer.*;
import stockexchange.visitor.RiskScoreVisitor;

import java.math.BigDecimal;

public class PriceAlertService implements Observer<MarketEvent> {

    private final Portfolio portfolio;
    private final BigDecimal threshold;
    private final Subject<MarketEvent> reportPublisher;

    public PriceAlertService(
            Portfolio portfolio,
            BigDecimal threshold,
            Subject<MarketEvent> reportPublisher
    ) {
        this.portfolio = portfolio;
        this.threshold = threshold;
        this.reportPublisher = reportPublisher;
    }

    @Override
    public void onEvent(MarketEvent event) {
        if (event.type() != EventType.PRICE_CHANGED) {
            return;
        }

        if (event.value().compareTo(threshold) >= 0) {
            return;
        }

        for (Account account : portfolio.getAccounts()) {
            boolean containsStock = account.getAssets().stream()
                    .anyMatch(asset ->
                            asset instanceof Stock stock
                                    && stock.getSymbol()
                                    .equals(event.symbol())
                    );

            if (!containsStock) {
                continue;
            }

            RiskScoreVisitor visitor = new RiskScoreVisitor();
            double score = account.accept(visitor);

            reportPublisher.publish(new MarketEvent(
                    EventType.RISK_REPORT,
                    account.getName(),
                    BigDecimal.valueOf(score),
                    "PriceAlertService"
            ));
        }
    }
}
