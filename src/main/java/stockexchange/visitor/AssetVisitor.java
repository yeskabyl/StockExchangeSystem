
package stockexchange.visitor;

import stockexchange.model.*;

public interface AssetVisitor<R> {

    R visit(Portfolio portfolio);

    R visit(Account account);

    R visit(Stock stock);

    R visit(Bond bond);

    R visit(Fund fund);

    R visit(Etf etf);
}
