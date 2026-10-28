
package stockexchange.visitor;

import stockexchange.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class AssetFilterVisitor
        implements AssetVisitor<List<Asset>> {

    private final Predicate<Asset> condition;
    private final List<Asset> matches = new ArrayList<>();

    public AssetFilterVisitor(Predicate<Asset> condition) {
        this.condition = condition;
    }

    private List<Asset> addIfMatching(Asset asset) {
        if (condition.test(asset)) {
            matches.add(asset);
        }

        return List.copyOf(matches);
    }

    @Override
    public List<Asset> visit(Portfolio portfolio) {
        for (Account account : portfolio.getAccounts()) {
            account.accept(this);
        }

        return List.copyOf(matches);
    }

    @Override
    public List<Asset> visit(Account account) {
        for (Asset asset : account.getAssets()) {
            asset.accept(this);
        }

        return List.copyOf(matches);
    }

    @Override
    public List<Asset> visit(Stock stock) {
        return addIfMatching(stock);
    }

    @Override
    public List<Asset> visit(Bond bond) {
        return addIfMatching(bond);
    }

    @Override
    public List<Asset> visit(Fund fund) {
        return addIfMatching(fund);
    }

    @Override
    public List<Asset> visit(Etf etf) {
        return addIfMatching(etf);
    }
}
