package com.conversantmedia.mpub.rsql.jpa.operator;

import com.conversantmedia.mpub.rsql.jpa.PredicateBuilder;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import java.util.List;

/**
 * Handler for GREATER_THAN operator
 */
public class GreaterThanOperatorHandler implements OperatorHandler{

    @Override
    @SuppressWarnings("unchecked")
    public Predicate createPredicate(PredicateBuilder predicateBuilder, Expression<?> propertyPath, List<?> arguments) {
        final Object argument = arguments.get(0);
        Predicate predicate;
        if (argument instanceof Number || argument == null) {
            final Expression<? extends Number> numberExpression = (Expression<? extends Number>) propertyPath;
            predicate = predicateBuilder.createGreaterThan(numberExpression, (Number) argument);
        } else if (argument instanceof Comparable) {
            @SuppressWarnings("rawtypes") final Expression comparableExpression =  propertyPath;
            @SuppressWarnings("rawtypes") final Comparable comparable = (Comparable) argument;
            predicate = predicateBuilder.createGreaterThanComparable(comparableExpression, comparable);
        } else {
            throw new IllegalArgumentException("Property " + propertyPath + " is not comparable");
        }
        return predicate;
    }
}
