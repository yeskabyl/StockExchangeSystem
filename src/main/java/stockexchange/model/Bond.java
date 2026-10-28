
package stockexchange.model;

import stockexchange.visitor.AssetVisitor;

import java.math.BigDecimal;

public class Bond implements Asset {

    private final String symbol;
    private final int quantity;
    private final BigDecimal faceValue;
    private final BigDecimal annualCouponRate;
    private final int riskFactor;

    public Bond(
            String symbol,
            int quantity,
            BigDecimal faceValue,
            BigDecimal annualCouponRate,
            int riskFactor
    ) {
        this.symbol = symbol;
        this.quantity = quantity;
        this.faceValue = faceValue;
        this.annualCouponRate = annualCouponRate;
        this.riskFactor = riskFactor;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getFaceValue() {
        return faceValue;
    }

    public BigDecimal getAnnualCouponRate() {
        return annualCouponRate;
    }

    public int getRiskFactor() {
        return riskFactor;
    }

    @Override
    public <R> R accept(AssetVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
