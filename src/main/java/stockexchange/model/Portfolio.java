
package stockexchange.model;

import stockexchange.visitor.AssetVisitor;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Portfolio implements Asset {

    private final String name;
    private final List<Account> accounts =
            new CopyOnWriteArrayList<>();

    public Portfolio(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addAccount(Account account) {
        accounts.add(account);
    }

    public List<Account> getAccounts() {
        return List.copyOf(accounts);
    }

    @Override
    public <R> R accept(AssetVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
