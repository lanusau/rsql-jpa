package com.conversantmedia.mpub.rsql.jpa.operator;

import com.conversantmedia.mpub.rsql.jpa.PredicateBuilder;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import java.util.List;
import java.util.Objects;

/**
 * Handler for NOT_EQUAL operator
 */
public class NotEqualOperatorHandler implements OperatorHandler{

    @Override
    @SuppressWarnings("unchecked")
    public Predicate createPredicate(PredicateBuilder predicateBuilder, Expression<?> propertyPath, List<?> arguments) {
        final Object argument = arguments.get(0);
        if (argument instanceof String) {
            final Expression<String> stringExpression = (Expression<String>) propertyPath;
            return predicateBuilder.createNotLike(stringExpression, (String) argument);
        } else if (Objects.isNull(argument)) {
            return predicateBuilder.createIsNotNull(propertyPath);
        } else {
            return predicateBuilder.createNotEqual(propertyPath, argument);
        }
    }
}
