
package stockexchange;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import stockexchange.model.*;
import stockexchange.visitor.*;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VisitorTest {

    private Portfolio portfolio;
    private Account trading;

    @BeforeEach
    void setup() {
        portfolio = new Portfolio("Test Portfolio");
        trading = new Account("Trading");

        trading.addAsset(new Stock(
                "AAPL", 10, new BigDecimal("100"),
                8, new BigDecimal("2")
        ));

        trading.addAsset(new Bond(
                "BOND", 2, new BigDecimal("500"),
                new BigDecimal("0.05"), 2
        ));

        Account retirement = new Account("Retirement");

        retirement.addAsset(new Fund(
                "FUND", 5, new BigDecimal("200"), 5
        ));

        retirement.addAsset(new Etf(
                "ETF", 4, new BigDecimal("250"), 4
        ));

        portfolio.addAccount(trading);
        portfolio.addAccount(retirement);
    }

    @Test
    void portfolioValueIs4000() {
        BigDecimal result =
                portfolio.accept(new TotalValueVisitor());

        assertEquals(
                0,
                new BigDecimal("4000").compareTo(result)
        );
    }

    @Test
    void accountValueIs2000() {
        BigDecimal result =
                trading.accept(new TotalValueVisitor());

        assertEquals(
                0,
                new BigDecimal("2000").compareTo(result)
        );
    }

    @Test
    void riskScoreIs475() {
        double risk =
                portfolio.accept(new RiskScoreVisitor());

        assertEquals(4.75, risk, 0.001);
    }

    @Test
    void annualTaxableIncomeIs70() {
        BigDecimal income =
                portfolio.accept(new TaxReportVisitor());

        assertEquals(
                0,
                new BigDecimal("70").compareTo(income)
        );
    }

    @Test
    void filterFindsOnlyStocks() {
        AssetFilterVisitor visitor =
                new AssetFilterVisitor(
                        asset -> asset instanceof Stock
                );

        List<Asset> result = portfolio.accept(visitor);

        assertEquals(1, result.size());
        assertInstanceOf(Stock.class, result.get(0));
    }

    @Test
    void jsonExportContainsNestedStructure() {
        String json =
                portfolio.accept(new JsonExportVisitor());

        assertTrue(json.contains("\"accounts\""));
        assertTrue(json.contains("\"assets\""));
        assertTrue(json.contains("\"type\":\"stock\""));
        assertTrue(json.contains("\"type\":\"bond\""));
        assertTrue(json.contains("\"type\":\"fund\""));
        assertTrue(json.contains("\"type\":\"etf\""));
    }

    @Test
    void emptyPortfolioHasZeroValue() {
        Portfolio empty = new Portfolio("Empty");

        assertEquals(
                0,
                BigDecimal.ZERO.compareTo(
                        empty.accept(new TotalValueVisitor())
                )
        );
    }
}
