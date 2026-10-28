
package stockexchange.model;

import stockexchange.visitor.AssetVisitor;

import java.math.BigDecimal;

public class Etf implements Asset {

    private final String symbol;
    private final int units;
    private final BigDecimal price;
    private final int riskFactor;

    public Etf(
            String symbol,
            int units,
            BigDecimal price,
            int riskFactor
    ) {
        this.symbol = symbol;
        this.units = units;
        this.price = price;
        this.riskFactor = riskFactor;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getUnits() {
        return units;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getRiskFactor() {
        return riskFactor;
    }

    @Override
    public <R> R accept(AssetVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
