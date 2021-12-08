package com.conversantmedia.mpub.rsql.jpa.operator;

import com.conversantmedia.mpub.rsql.jpa.PredicateBuilder;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import java.util.List;

/**
 * Handler for NOT_IN operator
 */
public class NotInOperatorHandler implements OperatorHandler{

    @Override
    public Predicate createPredicate(PredicateBuilder predicateBuilder, Expression<?> propertyPath, List<?> arguments) {
        return predicateBuilder.createNotIn(propertyPath, arguments);
    }
}
