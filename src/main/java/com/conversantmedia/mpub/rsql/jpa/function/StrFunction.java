package com.conversantmedia.mpub.rsql.jpa.function;


import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.ParameterRegistry;
import org.hibernate.query.criteria.internal.Renderable;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.hibernate.query.criteria.internal.expression.function.BasicFunctionExpression;
import org.hibernate.query.criteria.internal.expression.function.FunctionExpression;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import java.io.Serializable;

/**
 * Hibernate function expression for "str(number)"
 */
public class StrFunction extends BasicFunctionExpression<String> implements FunctionExpression<String>, Serializable {
    public static final String FCT_NAME = "str";

    private final Expression<String> selection;

    public StrFunction(CriteriaBuilder criteriaBuilder, Expression<String> selection) {
        super((CriteriaBuilderImpl)criteriaBuilder, String.class, FCT_NAME);
        this.selection = selection;
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        Helper.possibleParameter(selection, registry);
    }

    @Override
    public String render(RenderingContext renderingContext) {
        return FCT_NAME + '(' + ((Renderable) selection).render(renderingContext) + ')';
    }
}
