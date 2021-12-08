/*
 * The MIT License
 *
 * Copyright 2013 Jakub Jirutka <jakub@jirutka.cz>.
 * Copyright 2015 Antonio Rabelo.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.conversantmedia.mpub.rsql.jpa;

import com.conversantmedia.mpub.rsql.jpa.function.StrFunction;
import com.conversantmedia.mpub.rsql.jpa.operator.AdditionalRSQLOperators;
import com.conversantmedia.mpub.rsql.jpa.operator.OperatorSupport;
import cz.jirutka.rsql.parser.ast.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.PluralAttribute;
import java.util.*;

/**
 * Predicate builder cloned from rsql-hibernate project and simplified
 */
@Slf4j
@RequiredArgsConstructor
public final class PredicateBuilder {

    private static final Character LIKE_WILDCARD = '*';

    /**
     * Map to cache already created joins
     */
    private final Map<String, Join<?, ?>> joinMap = new HashMap<>();

    private final EntityManagerFactory entityManagerFactory;
    private final ArgumentParser argumentParser;

    /**
     * Create predicate from RSQL {@link Node}
     */
    Predicate createPredicate(Node node, From<?,?> root, Class<?> entity, CriteriaBuilder cb) {

        log.debug("Creating Predicate for: {}", node);

        if (node instanceof LogicalNode) {
            return createPredicate((LogicalNode)node, root, entity, cb);
        }

        if (node instanceof ComparisonNode) {
            return createPredicate((ComparisonNode)node, root);
        }

        throw new IllegalArgumentException("Unknown expression type: " + node.getClass());
    }


    /**
     * Create predicate using {@link LogicalNode}
     */
    private Predicate createPredicate(LogicalNode logical, From<?,?> root, Class<?> entity, CriteriaBuilder cb) {

        log.debug("Creating Predicate for logical node: {}", logical);

        final List<Predicate> predicates = new ArrayList<>();

        log.debug("Creating Predicates from all children nodes.");
        for (Node node : logical.getChildren()) {
            predicates.add(createPredicate(node, root, entity, cb));
        }

        switch (logical.getOperator()) {
            case AND : return cb.and(predicates.toArray(new Predicate[0]));
            case OR : return cb.or(predicates.toArray(new Predicate[0]));
        }

        throw new IllegalArgumentException("Unknown operator: " + logical.getOperator());
    }

    /**
     * Create predicate using {@link ComparisonNode}
     */
    private Predicate createPredicate(ComparisonNode comparison, From<?,?> startRoot) {

        if (startRoot == null) {
            String msg = "From root node was undefined.";
            log.error( msg);
            throw new IllegalArgumentException(msg);
        }
        log.debug("Creating Predicate for comparison node: {}", comparison);

        log.debug("Property graph path : {}", comparison.getSelector());
        final Path<?> propertyPath = findPropertyPath(comparison.getSelector(), startRoot);

        Class<?> javaType = propertyPath.getJavaType();

        // Exists (==ex=) operators always has boolean arguments
        if (comparison.getOperator() == AdditionalRSQLOperators.EXISTS) {
            javaType = Boolean.class;
        }

        log.debug("Cast all arguments to type {}.", javaType.getName());
        final List<?> castedArguments = argumentParser.parse(comparison.getArguments(), javaType);

        return createPredicate(propertyPath, comparison.getOperator(), castedArguments);

    }

    /**
     * Find {@link Path} of property path using dotted notation
     */
    private Path<?> findPropertyPath(String propertyPath, Path<?> startRoot) {

        final String[] graph = propertyPath.split("\\.");

        final Metamodel metaModel = entityManagerFactory.getMetamodel();
        ManagedType<?> classMetadata = metaModel.managedType(startRoot.getJavaType());

        Path<?> root = startRoot;
        String currentPath = "";

        for (String property : graph) {
            currentPath = currentPath + property;

            if (!hasPropertyName(property, classMetadata)) {
                throw new IllegalArgumentException("Unknown property: " + property + " from entity " + classMetadata.getJavaType().getName());
            }

            if (isAssociationType(property, classMetadata)) {
                final Class<?> associationType = findPropertyType(property, classMetadata);
                final String previousClass = classMetadata.getJavaType().getName();
                classMetadata = metaModel.managedType(associationType);

                if (root instanceof Join) {
                    root = root.get(property);
                } else {
                    // Check if we already created join for the current path
                    if (joinMap.containsKey(currentPath)) {
                        root = joinMap.get(currentPath);
                    } else {

                        log.debug("Creating a join between {} and {}.", previousClass, classMetadata.getJavaType().getName());
                        root = ((From<?,?>) root).join(property, JoinType.LEFT);
                        joinMap.put(currentPath, (Join<?,?>) root);
                    }
                }
            } else {
                log.debug("Create property path for type {} property {}.", classMetadata.getJavaType().getName(), property);
                root = root.get(property);

                if (isEmbeddedType(property, classMetadata)) {
                    final Class<?> embeddedType = findPropertyType(property, classMetadata);
                    classMetadata = metaModel.managedType(embeddedType);
                }
            }

            currentPath = currentPath + ".";
        }

        return root;
    }

    /**
     * Create Predicate for comparison operators.
     *
     * @param propertyPath  Property path that we want to compare.
     * @param operator      Comparison operator.
     * @param arguments     Arguments (1 for binary comparisons, n for multi-value comparisons [in, not in (out)])
     * @return              Predicate a predicate representation.
     */
    private Predicate createPredicate(Expression<?> propertyPath, ComparisonOperator operator, List<?> arguments) {
        log.debug("Creating predicate: propertyPath {} {} {}", propertyPath, operator, arguments);
        return OperatorSupport.getHandler(operator).createPredicate(this, propertyPath, arguments);
    }


    /**
     * Apply a case-insensitive "like" constraint to the property path. Value
     * could contain wildcards "*" (% in SQL) and "_".
     *
     * @param propertyPath  Property path that we want to compare.
     * @param argument      Argument with/without wildcards
     * @return              Predicate a predicate representation.
     */
    public Predicate createLike(Expression<String> propertyPath, String argument) {
        final String like = argument.replace(LIKE_WILDCARD, '%');
        final CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();

        if(propertyPath.getJavaType().getName().contains("Long") &&
                argument.indexOf(LIKE_WILDCARD) != -1){
            return builder.like(new StrFunction(builder, propertyPath), like);
        } else {
            return builder.like(builder.lower(propertyPath), like.toLowerCase());
        }
    }

    /**
     * Apply an "is null" constraint to the property path.
     *
     * @param propertyPath  Property path that we want to compare.
     * @return              Predicate a predicate representation.
     */
    public Predicate createIsNull(Expression<?> propertyPath) {
        CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
        return builder.isNull(propertyPath);
    }

    /**
     * Apply an "equal" constraint to property path.
     *
     * @param propertyPath  Property path that we want to compare.
     * @param argument      Argument
     * @return              Predicate a predicate representation.
     */
    public Predicate createEqual(Expression<?> propertyPath, Object argument) {
        CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
        return builder.equal(propertyPath, argument);
    }

    /**
     * Apply a "not equal" constraint to the property path.
     *
     * @param propertyPath  Property path that we want to compare.
     * @param argument      Argument
     * @return              Predicate a predicate representation.
     */
    public Predicate createNotEqual(Expression<?> propertyPath, Object argument) {
        CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
        return builder.notEqual(propertyPath, argument);
    }

    /**
     * Apply a negative case-insensitive "like" constraint to the property path.
     * Value should contains wildcards "*" (% in SQL) and "_".
     *
     * @param propertyPath  Property path that we want to compare.
     * @param argument      Argument with/without wildcards
     * @return              Predicate a predicate representation.
     */
    public Predicate createNotLike(Expression<String> propertyPath, String argument) {
        CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
        return builder.not(createLike(propertyPath, argument));
    }

    /**
     * Apply an "is not null" constraint to the property path.
     *
     * @param propertyPath  Property path that we want to compare.
     * @return              Predicate a predicate representation.
     */
    public Predicate createIsNotNull(Expression<?> propertyPath) {
        CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
        return builder.isNotNull(propertyPath);
    }

    /**
     * Apply a "greater than" constraint to the property path.
     *
     * @param propertyPath  Property path that we want to compare.
     * @param argument      Argument number.
     * @return              Predicate a predicate representation.
     */
    public Predicate createGreaterThan(Expression<? extends Number> propertyPath, Number argument) {
        CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
        return builder.gt(propertyPath, argument);
    }

    /**
     * Apply a "greater than" constraint to the property path.
     *
     * @param propertyPath  Property path that we want to compare.
     * @param argument      Argument.
     * @return              Predicate a predicate representation.
     */
    public <Y extends Comparable<? super Y>> Predicate createGreaterThanComparable(Expression<? extends Y> propertyPath, Y argument) {
        CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
        return builder.greaterThan(propertyPath, argument);
    }

    /**
     * Apply a "greater than or equal" constraint to the property path.
     *
     * @param propertyPath  Property path that we want to compare.
     * @param argument      Argument number.
     * @return              Predicate a predicate representation.
     */
    public Predicate createGreaterEqual(Expression<? extends Number> propertyPath, Number argument) {
        CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
        return builder.ge(propertyPath, argument);
    }

    /**
     * Apply a "greater than or equal" constraint to the property path.
     *
     * @param propertyPath  Property path that we want to compare.
     * @param argument      Argument.
     * @return              Predicate a predicate representation.
     */
    public <Y extends Comparable<? super Y>> Predicate createGreaterEqualComparable(Expression<? extends Y> propertyPath, Y argument) {
        CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
        return builder.greaterThanOrEqualTo(propertyPath, argument);
    }

    /**
     * Apply a "less than" constraint to the property path.
     *
     * @param propertyPath  Property path that we want to compare.
     * @param argument      Argument number.
     * @return              Predicate a predicate representation.
     */
    public Predicate createLessThan(Expression<? extends Number> propertyPath, Number argument) {
        CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
        return builder.lt(propertyPath, argument);
    }

    /**
     * Apply a "less than" constraint to the property path.
     *
     * @param propertyPath  Property path that we want to compare.
     * @param argument      Argument.
     * @return              Predicate a predicate representation.
     */
    public <Y extends Comparable<? super Y>> Predicate createLessThanComparable(Expression<? extends Y> propertyPath, Y argument) {
        CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
        return builder.lessThan(propertyPath, argument);
    }

    /**
     * Apply a "less than or equal" constraint to the property path.
     *
     * @param propertyPath  Property path that we want to compare.
     * @param argument      Argument number.
     * @return              Predicate a predicate representation.
     */
    public Predicate createLessEqual(Expression<? extends Number> propertyPath, Number argument) {
        CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
        return builder.le(propertyPath, argument);
    }

    /**
     * Apply a "less than or equal" constraint to the property path.
     *
     * @param propertyPath  Property path that we want to compare.
     * @param argument      Argument.
     * @return              Predicate a predicate representation.
     */
    public <Y extends Comparable<? super Y>> Predicate createLessEqualComparable(Expression<? extends Y> propertyPath, Y argument) {
        CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
        return builder.lessThanOrEqualTo(propertyPath, argument);
    }

    /**
     * Apply a "in" constraint to the property path.
     *
     * @param propertyPath  Property path that we want to compare.
     * @param arguments     List of arguments.
     * @return              Predicate a predicate representation.
     */
    public Predicate createIn(Expression<?> propertyPath, List<?> arguments) {
        return propertyPath.in(arguments);
    }

    /**
     * Apply a "not in" (out) constraint to the property path.
     *
     * @param propertyPath  Property path that we want to compare.
     * @param arguments     List of arguments.
     * @return              Predicate a predicate representation.
     */
    public Predicate createNotIn(Expression<?> propertyPath, List<?> arguments) {
        CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
        return builder.not(createIn(propertyPath,arguments));
    }

    /**
     * Verify if a property is an Association type.
     *
     * @param property       Property to verify.
     * @param classMetadata  Metamodel of the class we want to check.
     * @return               <tt>true</tt> if the property is an associantion, <tt>false</tt> otherwise.
     */
    private <T> boolean isAssociationType(String property, ManagedType<T> classMetadata){
        return classMetadata.getAttribute(property).isAssociation();
    }

    /**
     * Verify if a property is an Embedded type.
     *
     * @param property       Property to verify.
     * @param classMetadata  Metamodel of the class we want to check.
     * @return               <tt>true</tt> if the property is an embedded attribute, <tt>false</tt> otherwise.
     */
    private <T> boolean isEmbeddedType(String property, ManagedType<T> classMetadata){
        return classMetadata.getAttribute(property).getPersistentAttributeType() == PersistentAttributeType.EMBEDDED;
    }

    /**
     * Verifies if a class metamodel has the specified property.
     *
     * @param property       Property name.
     * @param classMetadata  Class metamodel that may hold that property.
     * @return               <tt>true</tt> if the class has that property, <tt>false</tt> otherwise.
     */
    private <T> boolean  hasPropertyName(String property, ManagedType<T> classMetadata) {
        Set<Attribute<? super T, ?>> names = classMetadata.getAttributes();
        for (Attribute<? super T, ?> name : names) {
            if (name.getName().equals(property)) return true;
        }
        return false;
    }

    /**
     * Get the property Type out of the metamodel.
     *
     * @param property       Property name for type extraction.
     * @param classMetadata  Reference class metamodel that holds property type.
     * @return               Class java type for the property,
     * 						 if the property is a pluralAttribute it will take the bindable java type of that collection.
     */
    private <T> Class<?> findPropertyType(String property, ManagedType<T> classMetadata) {
        Class<?> propertyType;
        if (classMetadata.getAttribute(property).isCollection()) {
            propertyType = ((PluralAttribute<?,?,?>)classMetadata.getAttribute(property)).getBindableJavaType();
        } else {
            propertyType = classMetadata.getAttribute(property).getJavaType();
        }
        return propertyType;
    }
}
