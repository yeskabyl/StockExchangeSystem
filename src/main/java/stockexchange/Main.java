
package stockexchange;

import stockexchange.events.*;
import stockexchange.feeds.*;
import stockexchange.listeners.*;
import stockexchange.model.*;
import stockexchange.observer.*;
import stockexchange.service.MarketSimulator;
import stockexchange.visitor.*;

import java.math.BigDecimal;

public class Main {

    public static void main(String[] args) throws Exception {

        // 1. Build portfolio tree
        Portfolio portfolio = new Portfolio("My Investments");

        Account trading = new Account("Trading Account");
        Account retirement = new Account("Retirement Account");

        trading.addAsset(new Stock(
                "AAPL", 10, new BigDecimal("100"),
                8, new BigDecimal("2")
        ));

        trading.addAsset(new Bond(
                "US-BOND", 2, new BigDecimal("500"),
                new BigDecimal("0.05"), 2
        ));

        retirement.addAsset(new Fund(
                "GROWTH-FUND", 5, new BigDecimal("200"), 5
        ));

        retirement.addAsset(new Etf(
                "SP500-ETF", 4, new BigDecimal("250"), 4
        ));

        portfolio.addAccount(trading);
        portfolio.addAccount(retirement);

        // 2. Create the two data sources
        StockPriceFeed stockFeed = new StockPriceFeed();
        MarketIndexFeed indexFeed = new MarketIndexFeed();

        // 3. Create the report event source
        EventPublisher<MarketEvent> reportPublisher =
                new EventPublisher<>();

        // 4. Create observers
        PortfolioTracker portfolioTracker =
                new PortfolioTracker(portfolio);

        PriceAlertService alertService =
                new PriceAlertService(
                        portfolio,
                        new BigDecimal("90"),
                        reportPublisher
                );

        MovingAverageTracker movingAverage =
                new MovingAverageTracker(5);

        TradeLogger tradeLogger = new TradeLogger();

        MarketDashboard dashboard = new MarketDashboard();

        MarketAggregator aggregator = new MarketAggregator();

        // 5. Connect observers to price feed
        stockFeed.subscribe(
                EventType.PRICE_CHANGED, portfolioTracker
        );
        stockFeed.subscribe(
                EventType.PRICE_CHANGED, alertService
        );
        stockFeed.subscribe(
                EventType.PRICE_CHANGED, movingAverage
        );
        stockFeed.subscribe(
                EventType.PRICE_CHANGED, tradeLogger
        );
        stockFeed.subscribe(
                EventType.PRICE_CHANGED, dashboard
        );
        stockFeed.subscribe(
                EventType.PRICE_CHANGED, aggregator
        );

        // 6. Connect index feed
        indexFeed.subscribe(
                EventType.INDEX_CHANGED, dashboard
        );
        indexFeed.subscribe(
                EventType.INDEX_CHANGED, tradeLogger
        );

        // 7. Connect aggregated and risk events
        aggregator.subscribe(
                EventType.AGGREGATE_UPDATED, dashboard
        );
        aggregator.subscribe(
                EventType.AGGREGATE_UPDATED, tradeLogger
        );

        reportPublisher.subscribe(
                EventType.RISK_REPORT, dashboard
        );
        reportPublisher.subscribe(
                EventType.RISK_REPORT, tradeLogger
        );

        // 8. Run visitors before market simulation
        System.out.println("=== STOCK EXCHANGE SYSTEM ===");

        BigDecimal initialValue =
                portfolio.accept(new TotalValueVisitor());

        System.out.println("Initial portfolio value: "
                + initialValue);

        double initialRisk =
                portfolio.accept(new RiskScoreVisitor());

        System.out.println("Initial risk score: "
                + initialRisk);

        BigDecimal taxableIncome =
                portfolio.accept(new TaxReportVisitor());

        System.out.println("Estimated annual taxable income: "
                + taxableIncome);

        AssetFilterVisitor stockFilter =
                new AssetFilterVisitor(asset -> asset instanceof Stock);

        System.out.println("Stock assets found: "
                + portfolio.accept(stockFilter).size());

        // 9. Demonstrate an alert
        System.out.println("\n=== PRICE ALERT ===");

        stockFeed.updatePrice("AAPL", new BigDecimal("75"));

        System.out.println("Trading account risk: "
                + trading.accept(new RiskScoreVisitor()));

        // 10. Run concurrent market simulation
        System.out.println("\n=== MARKET SIMULATION ===");

        int threads = args.length > 0
                ? Integer.parseInt(args[0]) : 4;

        int eventsPerProducer = args.length > 1
                ? Integer.parseInt(args[1]) : 100;

        MarketSimulator simulator = new MarketSimulator(
                stockFeed, indexFeed, threads
        );

        simulator.runSimulation(eventsPerProducer);

        // 11. Display final results
        System.out.println("Final portfolio value: "
                + portfolio.accept(new TotalValueVisitor()));

        System.out.println("Final risk score: "
                + portfolio.accept(new RiskScoreVisitor()));

        System.out.println("AAPL moving average: "
                + movingAverage.getAverage("AAPL"));

        System.out.println("Dashboard notifications: "
                + dashboard.getEventCount());

        System.out.println("Logged events: "
                + tradeLogger.getEventCount());

        // 12. Export portfolio as JSON
        System.out.println("\n=== JSON EXPORT ===");

        System.out.println(
                portfolio.accept(new JsonExportVisitor())
        );

        System.out.println("\nSimulation completed successfully.");
    }
}
