
package stockexchange.visitor;

import stockexchange.model.*;

import java.math.BigDecimal;

public class TaxReportVisitor
        implements AssetVisitor<BigDecimal> {

    private BigDecimal taxableIncome = BigDecimal.ZERO;

    public BigDecimal getTaxableIncome() {
        return taxableIncome;
    }

    @Override
    public BigDecimal visit(Portfolio portfolio) {
        for (Account account : portfolio.getAccounts()) {
            account.accept(this);
        }

        return taxableIncome;
    }

    @Override
    public BigDecimal visit(Account account) {
        for (Asset asset : account.getAssets()) {
            asset.accept(this);
        }

        return taxableIncome;
    }

    @Override
    public BigDecimal visit(Stock stock) {
        BigDecimal dividends =
                stock.getAnnualDividendPerShare().multiply(
                        BigDecimal.valueOf(stock.getQuantity())
                );

        taxableIncome = taxableIncome.add(dividends);
        return taxableIncome;
    }

    @Override
    public BigDecimal visit(Bond bond) {
        BigDecimal coupons = bond.getFaceValue()
                .multiply(BigDecimal.valueOf(bond.getQuantity()))
                .multiply(bond.getAnnualCouponRate());

        taxableIncome = taxableIncome.add(coupons);
        return taxableIncome;
    }

    @Override
    public BigDecimal visit(Fund fund) {
        return taxableIncome;
    }

    @Override
    public BigDecimal visit(Etf etf) {
        return taxableIncome;
    }
}
