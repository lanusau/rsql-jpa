package com.conversantmedia.mpub.rsql.jpa.operator;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.RSQLOperators;

import java.util.Map;

/**
 * Support class to help get a handler for an operator
 */
public class OperatorSupport {

    private static final Map<ComparisonOperator, OperatorHandler> handlers = Map.of(
            AdditionalRSQLOperators.EXISTS, new ExistsOperatorHandler(),
            RSQLOperators.EQUAL, new EqualOperatorHandler(),
            RSQLOperators.NOT_EQUAL, new NotEqualOperatorHandler(),
            RSQLOperators.GREATER_THAN, new GreaterThanOperatorHandler(),
            RSQLOperators.GREATER_THAN_OR_EQUAL, new GreaterThanOrEqualOperatorHandler(),
            RSQLOperators.LESS_THAN, new LessThanOperatorHandler(),
            RSQLOperators.LESS_THAN_OR_EQUAL, new LessThanOrEqualOperatorHandler(),
            RSQLOperators.IN, new InOperatorHandler(),
            RSQLOperators.NOT_IN, new NotInOperatorHandler()
    );

    public static OperatorHandler getHandler(ComparisonOperator operator) {
        final OperatorHandler operatorHandler = handlers.get(operator);
        if (operatorHandler == null) {
            throw new IllegalArgumentException("Unknown operator + " + operator);
        }
        return operatorHandler;
    }
}
