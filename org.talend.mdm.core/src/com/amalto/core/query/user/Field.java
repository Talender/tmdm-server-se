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


import com.amalto.core.metadata.MetadataUtils;
import org.talend.mdm.commmon.metadata.FieldMetadata;
import org.talend.mdm.commmon.metadata.TypeMetadata;

import java.util.Collections;
import java.util.List;

public class Field implements TypedExpression {

    private final FieldMetadata fieldMetadata;

    private List<FieldMetadata> path = Collections.emptyList();

    public Field(FieldMetadata fieldMetadata) {
        if (fieldMetadata == null) {
            throw new IllegalArgumentException("Field can not be null.");
        }
        this.fieldMetadata = fieldMetadata;
    }

    public FieldMetadata getFieldMetadata() {
        return fieldMetadata;
    }

    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    public Expression normalize() {
        return this;
    }

    public String getTypeName() {
        TypeMetadata type = MetadataUtils.getSuperConcreteType(fieldMetadata.getType());
        return type.getName();
    }

    public void setPath(List<FieldMetadata> path) {
        this.path = path;
    }

    public List<FieldMetadata> getPath() {
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Field)) {
            return false;
        }
        Field field = (Field) o;
        return !(fieldMetadata != null ? !fieldMetadata.getName().equals(field.fieldMetadata.getName()) : field.fieldMetadata != null);
    }

    @Override
    public int hashCode() {
        return fieldMetadata != null ? fieldMetadata.getName().hashCode() : 0;
    }
}
