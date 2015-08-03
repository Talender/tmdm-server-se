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

package com.amalto.core.query.user;

public class FieldFullText extends FullText {

    private final Field field;

    public FieldFullText(Field field, String value) {
        super(value);
        this.field = field;
    }

    public Expression normalize() {
        return this;
    }

    public Field getField() {
        return field;
    }

    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FieldFullText)) {
            return false;
        }
        FieldFullText that = (FieldFullText) o;
        return !(field != null ? !field.equals(that.field) : that.field != null);
    }

    @Override
    public int hashCode() {
        return field != null ? field.hashCode() : 0;
    }
}
