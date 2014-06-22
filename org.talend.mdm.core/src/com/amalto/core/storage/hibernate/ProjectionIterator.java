/*
 * Copyright (C) 2006-2012 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.storage.hibernate;

import com.amalto.core.query.user.*;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.storage.record.metadata.UnsupportedDataRecordMetadata;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.ScrollableResults;
import org.talend.mdm.commmon.metadata.*;

import javax.xml.XMLConstants;
import java.io.IOException;
import java.util.*;

class ProjectionIterator implements CloseableIterator<DataRecord> {

    private static final Logger LOGGER = Logger.getLogger(ProjectionIterator.class);

    private final CloseableIterator<Object> iterator;

    private final List<TypedExpression> selectedFields;

    private final Set<EndOfResultsCallback> callbacks;

    private final MappingRepository mappingMetadataRepository;

    public ProjectionIterator(MappingRepository mappingMetadataRepository,
                              CloseableIterator<Object> iterator,
                              List<TypedExpression> selectedFields,
                              Set<EndOfResultsCallback> callbacks) {
        this.iterator = iterator;
        this.selectedFields = selectedFields;
        this.callbacks = callbacks;
        this.mappingMetadataRepository = mappingMetadataRepository;
    }

    private boolean isClosed;

    public ProjectionIterator(MappingRepository mappingMetadataRepository,
                              final ScrollableResults results,
                              List<TypedExpression> selectedFields,
                              Set<EndOfResultsCallback> callbacks) {
        this(mappingMetadataRepository, new CloseableIterator<Object>() {
            private boolean isClosed = false;

            public boolean hasNext() {
                return results.next();
            }

            public Object next() {
                return results.get();
            }

            public void remove() {
            }

            @Override
            public void close() throws IOException {
                if (!isClosed) {
                    try {
                        results.close();
                    } finally {
                        isClosed = true;
                    }
                }
            }
        }, selectedFields, callbacks);
    }

    public boolean hasNext() {
        boolean hasNext = iterator.hasNext();
        if (!hasNext) {
            notifyCallbacks();
        }
        return hasNext;
    }

    private void notifyCallbacks() {
        if (!isClosed) {
            // TMDM-6712: Ensure all iterator resources are released.
            try {
                iterator.close();
            } catch (Throwable t) {
                LOGGER.error(t);
            }
            for (EndOfResultsCallback callback : callbacks) {
                try {
                    callback.onEndOfResults();
                } catch (Throwable t) {
                    LOGGER.error(t);
                }
            }
            isClosed = true;
        }
    }

    public DataRecord next() {
        DataRecord record;
        try {
            final ComplexTypeMetadata explicitProjectionType = new ComplexTypeMetadataImpl(StringUtils.EMPTY, Storage.PROJECTION_TYPE, false);
            record = new DataRecord(explicitProjectionType, UnsupportedDataRecordMetadata.INSTANCE);
            Object[] values;
            Object next = iterator.next();
            if (next instanceof Object[]) {
                values = (Object[]) next;
            } else {
                values = new Object[]{next};
            }
            ProjectionElementCreator projectionElementCreator = new ProjectionElementCreator(explicitProjectionType, values);
            List<ProjectionElement> elements = new LinkedList<ProjectionElement>();
            for (TypedExpression selectedField : selectedFields) {
                elements.add(selectedField.accept(projectionElementCreator));
            }
            for (ProjectionElement element : elements) {
                explicitProjectionType.addField(element.field);
                record.set(element.field, element.value);
            }
            DefaultValidationHandler handler = new DefaultValidationHandler();
            explicitProjectionType.freeze(handler);
            handler.end();
        } catch (Exception e) {
            notifyCallbacks();
            throw new RuntimeException(e);
        }
        return record;
    }

    public void remove() {
    }

    public void close() throws IOException {
        notifyCallbacks();
    }

    private static class ProjectionElement {
        FieldMetadata field;

        Object value;
    }

    private class ProjectionElementCreator extends VisitorAdapter<ProjectionElement> {

        int currentIndex = 0;

        boolean isAlias;

        private final ComplexTypeMetadata explicitProjectionType;

        private final Object[] values;

        private ProjectionElement currentElement;

        public ProjectionElementCreator(ComplexTypeMetadata explicitProjectionType, Object[] values) {
            this.explicitProjectionType = explicitProjectionType;
            this.values = values;
            isAlias = false;
        }

        private void createElement(String typeName, String fieldName) {
            SimpleTypeMetadata fieldType = new SimpleTypeMetadata(XMLConstants.W3C_XML_SCHEMA_NS_URI, typeName);
            FieldMetadata field = new SimpleTypeFieldMetadata(explicitProjectionType,
                    false,
                    false,
                    false,
                    fieldName,
                    fieldType,
                    Collections.<String>emptyList(),
                    Collections.<String>emptyList());
            currentElement = new ProjectionElement();
            currentElement.field = field;
        }

        private void createElement(String typeName, String aliasName, String realFieldName) {
            SimpleTypeMetadata fieldType = new SimpleTypeMetadata(XMLConstants.W3C_XML_SCHEMA_NS_URI, typeName);
            FieldMetadata field = new AliasedFieldMetadata(explicitProjectionType,
                    false,
                    false,
                    false,
                    aliasName,
                    fieldType,
                    Collections.<String>emptyList(),
                    Collections.<String>emptyList(),
                    realFieldName);
            currentElement = new ProjectionElement();
            currentElement.field = field;
        }

        private void createReferenceElement(ReferenceFieldMetadata fieldMetadata) {
            FieldMetadata field = new ReferenceFieldMetadata(explicitProjectionType,
                    false,
                    false,
                    false,
                    fieldMetadata.getName(),
                    fieldMetadata.getReferencedType(),
                    fieldMetadata.getReferencedField(),
                    fieldMetadata.getForeignKeyInfoFields(),
                    false,
                    false,
                    new SimpleTypeMetadata(XMLConstants.W3C_XML_SCHEMA_NS_URI, Types.STRING),
                    Collections.<String>emptyList(),
                    Collections.<String>emptyList());
            currentElement = new ProjectionElement();
            currentElement.field = field;
        }

        public ProjectionElement visit(Count count) {
            // Do nothing on field creation, count is expected to be nested in a com.amalto.core.query.user.Alias.
            currentElement.value = values[currentIndex++];
            return null;
        }

        public ProjectionElement visit(Alias alias) {
            isAlias = true;
            if (alias.getTypedExpression() instanceof Field) {
                Field fieldExpression = (Field) alias.getTypedExpression();
                String realFieldName = fieldExpression.getFieldMetadata().getName();
                createElement(alias.getTypeName(), alias.getAliasName(), realFieldName);
            } else {
                createElement(alias.getTypeName(), alias.getAliasName());
            }
            alias.getTypedExpression().accept(this);
            isAlias = false;
            return currentElement;
        }

        public ProjectionElement visit(Type type) {
            Object value = values[currentIndex++];
            if (value != null) {
                String typeName = value.toString();
                ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
                if (!(contextClassLoader instanceof StorageClassLoader)) {
                    throw new IllegalStateException("Expected a instance of " + StorageClassLoader.class.getName() + " as current class loader.");
                }
                try {
                    Class<?> aClass = contextClassLoader.loadClass(ClassCreator.getClassName(typeName));
                    ComplexTypeMetadata typeFromClass = ((StorageClassLoader) contextClassLoader).getTypeFromClass(aClass);
                    value = mappingMetadataRepository.getMappingFromDatabase(typeFromClass).getUser().getName();
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException("Exception occurred during type name conversion.", e);
                }
            }
            currentElement.value = value;
            return currentElement;
        }

        public ProjectionElement visit(StringConstant constant) {
            currentElement.value = values[currentIndex++];
            return currentElement;
        }

        public ProjectionElement visit(Timestamp timestamp) {
            if (!isAlias) {
                createElement(Timestamp.TIMESTAMP_TYPE_NAME, Storage.METADATA_TIMESTAMP);
            }
            currentElement.value = values[currentIndex++];
            return currentElement;
        }

        public ProjectionElement visit(TaskId taskId) {
            if (!isAlias) {
                createElement(TaskId.TASK_ID_TYPE_NAME, Storage.METADATA_TASK_ID);
            }
            currentElement.value = values[currentIndex++];
            return currentElement;
        }

        public ProjectionElement visit(Field field) {
            FieldMetadata fieldMetadata = field.getFieldMetadata();
            if (!isAlias) {
                if (fieldMetadata instanceof ReferenceFieldMetadata) {
                    createReferenceElement(((ReferenceFieldMetadata) fieldMetadata));
                } else {
                    createElement(fieldMetadata.getType().getName(), fieldMetadata.getName());
                }
            }
            if (fieldMetadata instanceof ReferenceFieldMetadata && ((ReferenceFieldMetadata) fieldMetadata).getReferencedField() instanceof CompoundFieldMetadata) {
                FieldMetadata referencedField = ((ReferenceFieldMetadata) fieldMetadata).getReferencedField();
                int length = ((CompoundFieldMetadata) referencedField).getFields().length;
                Object[] fieldValues = new Object[length];
                System.arraycopy(values, currentIndex, fieldValues, 0, length);
                // Only include composite FK value if there's an actual key value.
                currentElement.value = isNullValue(fieldValues) ? null : fieldValues;
                currentIndex += length;
            } else {
                currentElement.value = values[currentIndex++];
            }
            return currentElement;
        }

        private boolean isNullValue(Object[] fieldValues) {
            if (fieldValues == null) {
                return true;
            }
            for (Object o : fieldValues) {
                if (o != null) {
                    return false;
                }
            }
            return true;
        }

        public ProjectionElement visit(StagingStatus stagingStatus) {
            if (!isAlias) {
                createElement(stagingStatus.getTypeName(), Storage.METADATA_STAGING_STATUS);
            }
            currentElement.value = values[currentIndex++];
            return currentElement;
        }

        public ProjectionElement visit(StagingError stagingError) {
            if (!isAlias) {
                createElement(stagingError.getTypeName(), Storage.METADATA_STAGING_ERROR);
            }
            currentElement.value = values[currentIndex++];
            return currentElement;
        }

        public ProjectionElement visit(StagingSource stagingSource) {
            if (!isAlias) {
                createElement(stagingSource.getTypeName(), Storage.METADATA_STAGING_SOURCE);
            }
            currentElement.value = values[currentIndex++];
            return currentElement;
        }
    }
}
