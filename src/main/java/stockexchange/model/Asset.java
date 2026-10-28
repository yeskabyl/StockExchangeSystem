package stockexchange.model;

import stockexchange.visitor.AssetVisitor;

public interface Asset {
    <R> R accept(AssetVisitor<R> visitor);
}
