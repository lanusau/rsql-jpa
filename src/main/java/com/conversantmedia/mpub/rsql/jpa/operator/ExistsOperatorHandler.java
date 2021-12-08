package com.conversantmedia.mpub.rsql.jpa.operator;

import com.conversantmedia.mpub.rsql.jpa.PredicateBuilder;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import java.util.List;

/**
 * Handler for EXISTS operator
 */
public class ExistsOperatorHandler implements OperatorHandler{

    @Override
    public Predicate createPredicate(PredicateBuilder predicateBuilder, Expression<?> propertyPath, List<?> arguments) {
        final Boolean existence = (Boolean) arguments.get(0);
        if (existence) {
            return predicateBuilder.createIsNotNull(propertyPath);
        } else {
            return predicateBuilder.createIsNull(propertyPath);
        }
    }
}
