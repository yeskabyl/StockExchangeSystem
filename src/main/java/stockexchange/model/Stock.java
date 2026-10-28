
package stockexchange.model;

import stockexchange.visitor.AssetVisitor;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;

public class Stock implements Asset {

    private final String symbol;
    private final int quantity;
    private final int riskFactor;
    private final BigDecimal annualDividendPerShare;

    private final AtomicReference<BigDecimal> price;

    public Stock(
            String symbol,
            int quantity,
            BigDecimal price,
            int riskFactor,
            BigDecimal annualDividendPerShare
    ) {
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = new AtomicReference<>(price);
        this.riskFactor = riskFactor;
        this.annualDividendPerShare = annualDividendPerShare;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price.get();
    }

    public void setPrice(BigDecimal newPrice) {
        if (newPrice.signum() < 0) {
            throw new IllegalArgumentException("Negative price");
        }

        price.set(newPrice);
    }

    public int getRiskFactor() {
        return riskFactor;
    }

    public BigDecimal getAnnualDividendPerShare() {
        return annualDividendPerShare;
    }

    @Override
    public <R> R accept(AssetVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
