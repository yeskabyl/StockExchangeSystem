
package stockexchange.visitor;

import stockexchange.model.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class RiskScoreVisitor implements AssetVisitor<Double> {

    private BigDecimal totalValue = BigDecimal.ZERO;
    private BigDecimal weightedRisk = BigDecimal.ZERO;

    private void addRisk(BigDecimal value, int riskFactor) {
        totalValue = totalValue.add(value);
        weightedRisk = weightedRisk.add(
                value.multiply(BigDecimal.valueOf(riskFactor))
        );
    }

    public double getRiskScore() {
        if (totalValue.signum() == 0) {
            return 0.0;
        }

        return weightedRisk.divide(
                totalValue, 2, RoundingMode.HALF_UP
        ).doubleValue();
    }

    @Override
    public Double visit(Portfolio portfolio) {
        for (Account account : portfolio.getAccounts()) {
            account.accept(this);
        }

        return getRiskScore();
    }

    @Override
    public Double visit(Account account) {
        for (Asset asset : account.getAssets()) {
            asset.accept(this);
        }

        return getRiskScore();
    }

    @Override
    public Double visit(Stock stock) {
        BigDecimal value = stock.getPrice().multiply(
                BigDecimal.valueOf(stock.getQuantity())
        );

        addRisk(value, stock.getRiskFactor());
        return getRiskScore();
    }

    @Override
    public Double visit(Bond bond) {
        BigDecimal value = bond.getFaceValue().multiply(
                BigDecimal.valueOf(bond.getQuantity())
        );

        addRisk(value, bond.getRiskFactor());
        return getRiskScore();
    }

    @Override
    public Double visit(Fund fund) {
        BigDecimal value = fund.getNetAssetValue().multiply(
                BigDecimal.valueOf(fund.getUnits())
        );

        addRisk(value, fund.getRiskFactor());
        return getRiskScore();
    }

    @Override
    public Double visit(Etf etf) {
        BigDecimal value = etf.getPrice().multiply(
                BigDecimal.valueOf(etf.getUnits())
        );

        addRisk(value, etf.getRiskFactor());
        return getRiskScore();
    }
}
