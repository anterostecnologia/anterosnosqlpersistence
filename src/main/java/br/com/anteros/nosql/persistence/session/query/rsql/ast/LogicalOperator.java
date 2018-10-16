package br.com.anteros.nosql.persistence.session.query.rsql.ast;

public enum LogicalOperator {

    AND (";"),
    OR  (",");

    private final String symbol;

    private LogicalOperator(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }
}
