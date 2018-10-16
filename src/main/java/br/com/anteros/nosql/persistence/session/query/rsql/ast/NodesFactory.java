package br.com.anteros.nosql.persistence.session.query.rsql.ast;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.anteros.nosql.persistence.session.query.rsql.UnknownOperatorException;

/**
 * Factory that creates {@link Node} instances for the parser.
 */
public class NodesFactory {

    private final Map<String, ComparisonOperator> comparisonOperators;


    public NodesFactory(Set<ComparisonOperator> operators) {

        comparisonOperators = new HashMap<>(operators.size());
        for (ComparisonOperator op : operators) {
            for (String sym : op.getSymbols()) {
                comparisonOperators.put(sym, op);
            }
        }
    }

    /**
     * Creates a specific {@link LogicalNode} instance for the specified operator and with the
     * given children nodes.
     *
     * @param operator The logical operator to create a node for.
     * @param children Children nodes, i.e. operands.
     * @return A subclass of the {@link LogicalNode} according to the specified operator.
     */
    public LogicalNode createLogicalNode(LogicalOperator operator, List<Node> children) {
        switch (operator) {
            case AND : return new AndNode(children);
            case OR  : return new OrNode(children);

            // this normally can't happen
            default  : throw new IllegalStateException("Unknown operator: " + operator);
        }
    }

    /**
     * Creates a {@link ComparisonNode} instance with the given parameters.
     *
     * @param operatorToken A textual representation of the comparison operator to be found in the
     *                      set of supported {@linkplain ComparisonOperator operators}.
     * @param selector The selector that specifies the left side of the comparison.
     * @param arguments A list of arguments that specifies the right side of the comparison.
     *
     * @throws UnknownOperatorException If no operator for the specified operator token exists.
     */
    public ComparisonNode createComparisonNode(
            String operatorToken, String selector, List<String> arguments) throws UnknownOperatorException {

        ComparisonOperator op = comparisonOperators.get(operatorToken);
        if (op != null) {
            return new ComparisonNode(op, selector, arguments);
        } else {
            throw new UnknownOperatorException(operatorToken);
        }
    }
}
