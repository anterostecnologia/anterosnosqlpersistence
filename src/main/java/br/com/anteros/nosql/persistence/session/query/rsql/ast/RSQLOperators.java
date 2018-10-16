package br.com.anteros.nosql.persistence.session.query.rsql.ast;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;

public abstract class RSQLOperators {

    public static final ComparisonOperator
            EQUAL = new ComparisonOperator("=="),
            NOT_EQUAL = new ComparisonOperator("!="),
            GREATER_THAN = new ComparisonOperator("=gt=", ">"),
            GREATER_THAN_OR_EQUAL = new ComparisonOperator("=ge=", ">="),
            LESS_THAN = new ComparisonOperator("=lt=", "<"),
            LESS_THAN_OR_EQUAL = new ComparisonOperator("=le=", "<="),
            IN = new ComparisonOperator("=in=", true),
            NOT_IN = new ComparisonOperator("=out=", true);


    public static Set<ComparisonOperator> defaultOperators() {
        return new HashSet<>(asList(EQUAL, NOT_EQUAL, GREATER_THAN, GREATER_THAN_OR_EQUAL,
                                    LESS_THAN, LESS_THAN_OR_EQUAL, IN, NOT_IN));
    }
}
