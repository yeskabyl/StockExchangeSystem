
package stockexchange;

import org.junit.jupiter.api.Test;

import stockexchange.events.*;
import stockexchange.feeds.StockPriceFeed;
import stockexchange.listeners.*;
import stockexchange.model.*;
import stockexchange.observer.EventPublisher;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class IntegrationTest {

    @Test
    void priceDropTriggersAccountRiskReport() {
        Portfolio portfolio = new Portfolio("Portfolio");
        Account account = new Account("Trading");

        Stock stock = new Stock(
                "AAPL", 10, new BigDecimal("100"),
                8, BigDecimal.ONE
        );

        account.addAsset(stock);
        portfolio.addAccount(account);

        StockPriceFeed feed = new StockPriceFeed();

        EventPublisher<MarketEvent> reports =
                new EventPublisher<>();

        PortfolioTracker tracker = new PortfolioTracker(portfolio);

        PriceAlertService alerts = new PriceAlertService(
                portfolio, new BigDecimal("90"), reports
        );

        AtomicInteger reportCount = new AtomicInteger();
        AtomicReference<MarketEvent> lastReport =
                new AtomicReference<>();

        reports.subscribe(EventType.RISK_REPORT, event -> {
            reportCount.incrementAndGet();
            lastReport.set(event);
        });

        feed.subscribe(EventType.PRICE_CHANGED, tracker);
        feed.subscribe(EventType.PRICE_CHANGED, alerts);

        feed.updatePrice("AAPL", new BigDecimal("75"));

        assertEquals(1, reportCount.get());
        assertEquals("Trading", lastReport.get().symbol());
        assertEquals(8.0, lastReport.get().value().doubleValue());
        assertEquals(
                0,
                new BigDecimal("75").compareTo(stock.getPrice())
        );
    }

    @Test
    void priceAboveThresholdDoesNotTriggerReport() {
        Portfolio portfolio = new Portfolio("Portfolio");
        Account account = new Account("Trading");

        account.addAsset(new Stock(
                "AAPL", 5, new BigDecimal("100"),
                5, BigDecimal.ZERO
        ));

        portfolio.addAccount(account);

        StockPriceFeed feed = new StockPriceFeed();
        EventPublisher<MarketEvent> reports =
                new EventPublisher<>();

        PriceAlertService alerts = new PriceAlertService(
                portfolio, new BigDecimal("90"), reports
        );

        AtomicInteger count = new AtomicInteger();

        reports.subscribe(
                EventType.RISK_REPORT,
                event -> count.incrementAndGet()
        );

        feed.subscribe(EventType.PRICE_CHANGED, alerts);

        feed.updatePrice("AAPL", new BigDecimal("110"));

        assertEquals(0, count.get());
    }

    @Test
    void unrelatedAccountIsNotAnalyzed() {
        Portfolio portfolio = new Portfolio("Portfolio");

        Account appleAccount = new Account("Apple Account");
        appleAccount.addAsset(new Stock(
                "AAPL", 5, new BigDecimal("100"),
                8, BigDecimal.ONE
        ));

        Account microsoftAccount = new Account("Microsoft Account");
        microsoftAccount.addAsset(new Stock(
                "MSFT", 5, new BigDecimal("200"),
                4, BigDecimal.ONE
        ));

        portfolio.addAccount(appleAccount);
        portfolio.addAccount(microsoftAccount);

        StockPriceFeed feed = new StockPriceFeed();
        EventPublisher<MarketEvent> reports =
                new EventPublisher<>();

        PriceAlertService alerts = new PriceAlertService(
                portfolio, new BigDecimal("90"), reports
        );

        AtomicReference<String> reportedAccount =
                new AtomicReference<>();

        reports.subscribe(
                EventType.RISK_REPORT,
                event -> reportedAccount.set(event.symbol())
        );

        feed.subscribe(EventType.PRICE_CHANGED, alerts);

        feed.updatePrice("AAPL", new BigDecimal("75"));

        assertEquals("Apple Account", reportedAccount.get());
    }
}
