package com.conversantmedia.mpub.rsql.jpa;

import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.OrNode;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * rSQL visitor that creates JPA {@link Predicate} objects based on parsed rSQL query
 * Cloned from rsql-hibernate project and simplified
 * @param <E> entity class
 */
@Slf4j
@RequiredArgsConstructor
public class JpaPredicateVisitor<E> implements RSQLVisitor<Predicate,CriteriaBuilder> {

    private final Class<E> entityClass;
    private final Root<E> root;
    private final PredicateBuilder predicateBuilder;

    @Override
    public Predicate visit(AndNode node, CriteriaBuilder cb) {
        log.debug("Creating Predicate for AndNode: {}", node);
        return predicateBuilder.createPredicate(node, root, entityClass, cb);
    }

    @Override
    public Predicate visit(OrNode node, CriteriaBuilder cb) {
        log.debug("Creating Predicate for OrNode: {}", node);
        return predicateBuilder.createPredicate(node, root, entityClass, cb);
    }

    @Override
    public Predicate visit(ComparisonNode node, CriteriaBuilder cb) {
        log.debug("Creating Predicate for ComparisonNode: {}", node);
        return predicateBuilder.createPredicate(node, root, entityClass, cb);
    }
}
