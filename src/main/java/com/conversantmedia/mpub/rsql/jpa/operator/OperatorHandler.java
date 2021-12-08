package com.conversantmedia.mpub.rsql.jpa.operator;

import com.conversantmedia.mpub.rsql.jpa.PredicateBuilder;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import java.util.List;

/**
 * Interface for various operator handlers
 */
public interface OperatorHandler {

    /**
     * Create {@link Predicate} for particular property path and argumens
     */
    Predicate createPredicate(PredicateBuilder predicateBuilder, Expression<?> propertyPath, List<?> arguments);
}
