
package stockexchange.listeners;

import stockexchange.events.*;
import stockexchange.model.*;
import stockexchange.observer.Observer;

public class PortfolioTracker implements Observer<MarketEvent> {

    private final Portfolio portfolio;

    public PortfolioTracker(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    @Override
    public void onEvent(MarketEvent event) {
        if (event.type() != EventType.PRICE_CHANGED) {
            return;
        }

        for (Account account : portfolio.getAccounts()) {
            for (Asset asset : account.getAssets()) {
                if (asset instanceof Stock stock
                        && stock.getSymbol().equals(event.symbol())) {
                    stock.setPrice(event.value());
                }
            }
        }
    }
}
