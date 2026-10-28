
package stockexchange.model;

import stockexchange.visitor.AssetVisitor;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Account implements Asset {

    private final String name;
    private final List<Asset> assets =
            new CopyOnWriteArrayList<>();

    public Account(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addAsset(Asset asset) {
        assets.add(asset);
    }

    public List<Asset> getAssets() {
        return List.copyOf(assets);
    }

    @Override
    public <R> R accept(AssetVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
