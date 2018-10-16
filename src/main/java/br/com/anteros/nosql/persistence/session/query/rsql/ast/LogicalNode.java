package br.com.anteros.nosql.persistence.session.query.rsql.ast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.util.Collections.unmodifiableList;

/**
 * Superclass of all logical nodes that represents a logical operation that connects
 * children nodes.
 */
public abstract class LogicalNode extends AbstractNode implements Iterable<Node> {

    private final List<Node> children;

    private final LogicalOperator operator;


    /**
     * @param operator Must not be <tt>null</tt>.
     * @param children Children nodes, i.e. operands; must not be <tt>null</tt>.
     */
    protected LogicalNode(LogicalOperator operator, List<? extends Node> children) {
        assert operator != null : "operator must not be null";
        assert children != null : "children must not be null";

        this.operator = operator;
        this.children = unmodifiableList(new ArrayList<>(children));
    }


    /**
     * Returns a copy of this node with the specified children nodes.
     */
    public abstract LogicalNode withChildren(List<? extends Node> children);


    /**
     * Iterate over children nodes. The underlying collection is unmodifiable!
     */
    public Iterator<Node> iterator() {
        return children.iterator();
    }

    public LogicalOperator getOperator() {
        return operator;
    }

    /**
     * Returns a copy of the children nodes.
     */
    public List<Node> getChildren() {
        return new ArrayList<>(children);
    }


    @Override
    public String toString() {
        return "(" + join(children, operator.toString()) + ")";
    }
    
    public static String join(List<?> list, String glue) {

        StringBuilder line = new StringBuilder();
        for (Object s : list) {
            line.append(s).append(glue);
        }
        return list.isEmpty() ? "" : line.substring(0, line.length() - glue.length());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LogicalNode)) return false;
        LogicalNode nodes = (LogicalNode) o;

        return children.equals(nodes.children)
            && operator == nodes.operator;
    }

    @Override
    public int hashCode() {
        int result = children.hashCode();
        result = 31 * result + operator.hashCode();
        return result;
    }
}
