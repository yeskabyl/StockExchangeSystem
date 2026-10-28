
package stockexchange.model;

import stockexchange.visitor.AssetVisitor;

import java.math.BigDecimal;

public class Fund implements Asset {

    private final String symbol;
    private final int units;
    private final BigDecimal netAssetValue;
    private final int riskFactor;

    public Fund(
            String symbol,
            int units,
            BigDecimal netAssetValue,
            int riskFactor
    ) {
        this.symbol = symbol;
        this.units = units;
        this.netAssetValue = netAssetValue;
        this.riskFactor = riskFactor;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getUnits() {
        return units;
    }

    public BigDecimal getNetAssetValue() {
        return netAssetValue;
    }

    public int getRiskFactor() {
        return riskFactor;
    }

    @Override
    public <R> R accept(AssetVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
