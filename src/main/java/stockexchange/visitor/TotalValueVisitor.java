
package stockexchange.visitor;

import stockexchange.model.*;

import java.math.BigDecimal;

public class TotalValueVisitor
        implements AssetVisitor<BigDecimal> {

    @Override
    public BigDecimal visit(Portfolio portfolio) {
        BigDecimal total = BigDecimal.ZERO;

        for (Account account : portfolio.getAccounts()) {
            total = total.add(account.accept(this));
        }

        return total;
    }

    @Override
    public BigDecimal visit(Account account) {
        BigDecimal total = BigDecimal.ZERO;

        for (Asset asset : account.getAssets()) {
            total = total.add(asset.accept(this));
        }

        return total;
    }

    @Override
    public BigDecimal visit(Stock stock) {
        return stock.getPrice().multiply(
                BigDecimal.valueOf(stock.getQuantity())
        );
    }

    @Override
    public BigDecimal visit(Bond bond) {
        return bond.getFaceValue().multiply(
                BigDecimal.valueOf(bond.getQuantity())
        );
    }

    @Override
    public BigDecimal visit(Fund fund) {
        return fund.getNetAssetValue().multiply(
                BigDecimal.valueOf(fund.getUnits())
        );
    }

    @Override
    public BigDecimal visit(Etf etf) {
        return etf.getPrice().multiply(
                BigDecimal.valueOf(etf.getUnits())
        );
    }
}
