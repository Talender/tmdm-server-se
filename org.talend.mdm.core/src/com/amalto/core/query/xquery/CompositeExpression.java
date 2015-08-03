/*
 * Copyright (C) 2006-2015 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.query.xquery;

/**
 *
 */
public class CompositeExpression implements Expression {
    private final Expression[] expressions;

    public CompositeExpression(Expression... expressions) {
        this.expressions = expressions;
    }

    public void accept(ExpressionVisitor visitor, ExpressionVisitorContext context) {
        for (Expression expression : expressions) {
            expression.accept(visitor, context);
        }
    }
}
