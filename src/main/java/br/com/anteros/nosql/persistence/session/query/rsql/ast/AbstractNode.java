package br.com.anteros.nosql.persistence.session.query.rsql.ast;

public abstract class AbstractNode implements Node {

    /**
     * Accepts the visitor, calls its visit() method and returns the result.
     * This method just calls {@link #accept(RSQLVisitor, Object)} with
     * <tt>null</tt> as the second argument.
     */
    public <R, A> R accept(RSQLVisitor<R, A> visitor) {
        return accept(visitor, null);
    }
}
