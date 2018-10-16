package br.com.anteros.nosql.persistence.session.query.rsql.ast;

/**
 * An adapter for the {@link RSQLVisitor} interface with a simpler contract that omits the optional
 * second argument.
 *
 * @param <R> Return type of the visitor's method.
 */
public abstract class NoArgRSQLVisitorAdapter<R> implements RSQLVisitor<R, Void> {

    public abstract R visit(AndNode node);

    public abstract R visit(OrNode node);

    public abstract R visit(ComparisonNode node);


    public R visit(AndNode node, Void param) {
        return visit(node);
    }

    public R visit(OrNode node, Void param) {
        return visit(node);
    }

    public R visit(ComparisonNode node, Void param) {
        return visit(node);
    }
}
