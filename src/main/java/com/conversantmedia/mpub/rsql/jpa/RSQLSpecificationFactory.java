package com.conversantmedia.mpub.rsql.jpa;

import com.conversantmedia.mpub.rsql.jpa.operator.AdditionalRSQLOperators;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.Node;
import cz.jirutka.rsql.parser.ast.RSQLOperators;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.EntityManagerFactory;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Spring's {@link Specification} factory that uses rSQL parser to generate predicate
 */
@RequiredArgsConstructor
public class RSQLSpecificationFactory {

    private final ConversionService conversionService;
    private final EntityManagerFactory entityManagerFactory;

    private final Set<ComparisonOperator> operators =
            Stream.concat(
                RSQLOperators.defaultOperators().stream(),
                AdditionalRSQLOperators.operators().stream()
            ).collect(Collectors.toSet());

    /**
     * Create {@link Specification} using rSQL query
     * @param rSQL - rSQL query text
     * @param entityClass - entity class
     * @param <T> - entity type
     * @return - specification
     */
    public <T> Specification<T> createRSQLSpecification(String rSQL, Class<T> entityClass) {
        return (root, query, criteriaBuilder) -> {
            if (!(criteriaBuilder instanceof CriteriaBuilderImpl)) {
                throw new UnsupportedOperationException("Only Hibernate supported");
            }

            final Node parsedRSQL = new RSQLParser(operators).parse(rSQL);
            final PredicateBuilder predicateBuilder = new PredicateBuilder(entityManagerFactory, new ArgumentParser(conversionService));
            final JpaPredicateVisitor<T> jpaPredicateVisitor = new JpaPredicateVisitor<>(entityClass, root, predicateBuilder);
            return parsedRSQL.accept(jpaPredicateVisitor, criteriaBuilder);
        };
    }

}
