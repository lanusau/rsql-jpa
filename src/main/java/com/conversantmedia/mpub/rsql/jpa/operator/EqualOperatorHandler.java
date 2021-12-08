package com.conversantmedia.mpub.rsql.jpa.operator;

import com.conversantmedia.mpub.rsql.jpa.PredicateBuilder;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import java.util.List;
import java.util.Objects;

/**
 * Handler for EQUAL operator
 */
public class EqualOperatorHandler implements OperatorHandler{

    @Override
    @SuppressWarnings("unchecked")
    public Predicate createPredicate(PredicateBuilder predicateBuilder, Expression<?> propertyPath, List<?> arguments) {
        final Object argument = arguments.get(0);
        if (argument instanceof String) {
            final Expression<String> stringExpression = (Expression<String>) propertyPath;
            return predicateBuilder.createLike(stringExpression, (String) argument);
        } else {
            return predicateBuilder.createEqual(propertyPath, argument);
        }
    }
}
