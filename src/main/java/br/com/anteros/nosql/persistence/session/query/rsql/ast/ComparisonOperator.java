package br.com.anteros.nosql.persistence.session.query.rsql.ast;

import java.util.regex.Pattern;

import br.com.anteros.core.utils.Assert;

public final class ComparisonOperator {

    private static final Pattern SYMBOL_PATTERN = Pattern.compile("=[a-zA-Z]*=|[><]=?|!=");

    private final String[] symbols;

    private final boolean multiValue;


    /**
     * @param symbols Textual representation of this operator (e.g. <tt>=gt=</tt>); the first item
     *          is primary representation, any others are alternatives. Must match
     *          <tt>=[a-zA-Z]*=|[><]=?|!=</tt>.
     * @param multiValue Whether this operator may be used with multiple arguments. This is then
     *          validated in {@link NodesFactory}.
     *
     * @throws IllegalArgumentException If the {@code symbols} is either <tt>null</tt>, empty,
     *          or contain illegal symbols.
     */
    public ComparisonOperator(String[] symbols, boolean multiValue) {
        Assert.notEmpty(symbols, "symbols must not be null or empty");
        for (String sym : symbols) {
            isTrue(isValidOperatorSymbol(sym), "symbol must match: %s", SYMBOL_PATTERN);
        }
        this.multiValue = multiValue;
        this.symbols = symbols.clone();
    }
    
    public static void isTrue(boolean expression, String message, Object... args) {
        if (!expression) {
            throw new IllegalArgumentException(String.format(message, args));
        }
    }

    /**
     * @see #ComparisonOperator(String[], boolean)
     */
    public ComparisonOperator(String symbol, boolean multiValue) {
        this(new String[]{symbol}, multiValue);
    }

    /**
     * @see #ComparisonOperator(String[], boolean)
     */
    public ComparisonOperator(String symbol, String altSymbol, boolean multiValue) {
        this(new String[]{symbol, altSymbol}, multiValue);
    }

    /**
     * @see #ComparisonOperator(String[], boolean)
     */
    public ComparisonOperator(String... symbols) {
        this(symbols, false);
    }


    /**
     * Returns the primary representation of this operator.
     */
    public String getSymbol() {
        return symbols[0];
    }

    /**
     * Returns all representations of this operator. The first item is always the primary
     * representation.
     */
    public String[] getSymbols() {
        return symbols.clone();
    }

    /**
     * Whether this operator may be used with multiple arguments.
     */
    public boolean isMultiValue() {
        return multiValue;
    }


    /**
     * Whether the given string can represent an operator.
     * Note: Allowed symbols are limited by the RSQL syntax (i.e. parser).
     */
    private boolean isValidOperatorSymbol(String str) {
        return !isBlank(str) && SYMBOL_PATTERN.matcher(str).matches();
    }
    
    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }


    @Override
    public String toString() {
        return getSymbol();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ComparisonOperator)) return false;

        ComparisonOperator that = (ComparisonOperator) o;
        return getSymbol().equals(that.getSymbol());
    }

    @Override
    public int hashCode() {
        return getSymbol().hashCode();
    }
}
