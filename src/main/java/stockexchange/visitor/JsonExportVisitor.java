
package stockexchange.visitor;

import stockexchange.model.*;

import java.util.stream.Collectors;

public class JsonExportVisitor
        implements AssetVisitor<String> {

    private String quote(String value) {
        StringBuilder result = new StringBuilder("\"");

        for (char c : value.toCharArray()) {
            switch (c) {
                case '"' -> result.append("\\\"");
                case '\\' -> result.append("\\\\");
                case '\n' -> result.append("\\n");
                case '\r' -> result.append("\\r");
                case '\t' -> result.append("\\t");
                default -> {
                    if (c < 0x20) {
                        result.append(String.format("\\u%04x", (int) c));
                    } else {
                        result.append(c);
                    }
                }
            }
        }

        return result.append("\"").toString();
    }

    @Override
    public String visit(Portfolio portfolio) {
        String accounts = portfolio.getAccounts().stream()
                .map(account -> account.accept(this))
                .collect(Collectors.joining(","));

        return "{\"type\":\"portfolio\",\"name\":"
                + quote(portfolio.getName())
                + ",\"accounts\":[" + accounts + "]}";
    }

    @Override
    public String visit(Account account) {
        String assets = account.getAssets().stream()
                .map(asset -> asset.accept(this))
                .collect(Collectors.joining(","));

        return "{\"type\":\"account\",\"name\":"
                + quote(account.getName())
                + ",\"assets\":[" + assets + "]}";
    }

    @Override
    public String visit(Stock stock) {
        return "{\"type\":\"stock\",\"symbol\":"
                + quote(stock.getSymbol())
                + ",\"quantity\":" + stock.getQuantity()
                + ",\"price\":" + stock.getPrice()
                + ",\"riskFactor\":" + stock.getRiskFactor()
                + "}";
    }

    @Override
    public String visit(Bond bond) {
        return "{\"type\":\"bond\",\"symbol\":"
                + quote(bond.getSymbol())
                + ",\"quantity\":" + bond.getQuantity()
                + ",\"faceValue\":" + bond.getFaceValue()
                + ",\"annualCouponRate\":" + bond.getAnnualCouponRate()
                + ",\"riskFactor\":" + bond.getRiskFactor()
                + "}";
    }

    @Override
    public String visit(Fund fund) {
        return "{\"type\":\"fund\",\"symbol\":"
                + quote(fund.getSymbol())
                + ",\"units\":" + fund.getUnits()
                + ",\"netAssetValue\":" + fund.getNetAssetValue()
                + ",\"riskFactor\":" + fund.getRiskFactor()
                + "}";
    }

    @Override
    public String visit(Etf etf) {
        return "{\"type\":\"etf\",\"symbol\":"
                + quote(etf.getSymbol())
                + ",\"units\":" + etf.getUnits()
                + ",\"price\":" + etf.getPrice()
                + ",\"riskFactor\":" + etf.getRiskFactor()
                + "}";
    }
}
