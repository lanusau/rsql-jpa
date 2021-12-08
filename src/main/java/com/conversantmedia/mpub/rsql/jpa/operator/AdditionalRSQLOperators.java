package com.conversantmedia.mpub.rsql.jpa.operator;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;

import java.util.Collections;
import java.util.Set;

/**
 * Additional operators that we support
 */
public class AdditionalRSQLOperators {
    public static final ComparisonOperator EXISTS = new ComparisonOperator("=ex=", false);

    public static Set<ComparisonOperator> operators() {
        return Collections.singleton(EXISTS);
    }
}
