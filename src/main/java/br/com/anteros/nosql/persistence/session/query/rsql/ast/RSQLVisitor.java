package br.com.anteros.nosql.persistence.session.query.rsql.ast;

/**
 * An interface for visiting AST nodes of the RSQL.
 *
 * @param <R> Return type of the visitor's method.
 * @param <A> Type of an optional parameter passed to the visitor's method.
 */
public interface RSQLVisitor<R, A> {

    R visit(AndNode node, A param);

    R visit(OrNode node, A param);

    R visit(ComparisonNode node, A param);
}
