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

package com.amalto.core.storage.hibernate;

import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.metadata.CompoundFieldMetadata;
import com.amalto.core.metadata.ContainedComplexTypeMetadata;
import com.amalto.core.metadata.FieldMetadata;
import com.amalto.core.metadata.ReferenceFieldMetadata;
import com.amalto.core.query.user.*;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageResults;
import com.amalto.core.storage.datasource.DataSource;
import com.amalto.core.storage.datasource.RDBMSDataSource;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Session;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

class SelectAnalyzer extends VisitorAdapter<AbstractQueryHandler> {

    private static final Logger LOGGER = Logger.getLogger(SelectAnalyzer.class);

    private final List<TypedExpression> selectedFields = new LinkedList<TypedExpression>();

    private final MappingRepository mappings;

    private final StorageClassLoader storageClassLoader;

    private final Session session;

    private final Set<EndOfResultsCallback> callbacks;

    private final Storage storage;

    private final TableResolver resolver;

    private boolean isFullText = false;

    private boolean isCheckingProjection;

    private FullText fullTextExpression;

    SelectAnalyzer(MappingRepository storageRepository, StorageClassLoader storageClassLoader, Session session, Set<EndOfResultsCallback> callbacks, Storage storage, TableResolver resolver) {
        this.mappings = storageRepository;
        this.storageClassLoader = storageClassLoader;
        this.session = session;
        this.callbacks = callbacks;
        this.storage = storage;
        this.resolver = resolver;
    }

    @Override
    public AbstractQueryHandler visit(NativeQuery nativeQuery) {
        return new NativeQueryHandler(storage, mappings, storageClassLoader, session, callbacks);
    }

    @Override
    public AbstractQueryHandler visit(Select select) {
        List<TypedExpression> selectedFields = select.getSelectedFields();
        isCheckingProjection = true;
        {
            for (Expression selectedField : selectedFields) {
                selectedField.accept(this);
            }
        }
        isCheckingProjection = false;
        Condition condition = select.getCondition();
        if (condition != null) {
            condition.accept(this);
        }
        // Full text
        if (isFullText) {
            DataSource dataSource = storage.getDataSource();
            RDBMSDataSource rdbmsDataSource = (RDBMSDataSource) dataSource;
            String fullTextValue = fullTextExpression.getValue().trim();
            if (fullTextValue.isEmpty() || StringUtils.containsOnly(fullTextValue, new char[]{'*'})) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Using \"standard query\" strategy (full text query on '*' or ' ')");
                }
                return new StandardQueryHandler(storage, mappings, resolver, storageClassLoader, session, select, this.selectedFields, callbacks);
            }
            if (rdbmsDataSource.supportFullText()) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Using \"full text query\" strategy");
                }
                return new FullTextQueryHandler(storage, mappings, storageClassLoader, session, select, this.selectedFields, callbacks);
            } else {
                throw new IllegalArgumentException("Storage '" + storage.getName() + "' is not configured to support full text queries.");
            }
        }
        // Condition optimizations
        if (condition != null) {
            ConditionChecks conditionChecks = new ConditionChecks(select);
            ConditionChecks.Result result = condition.accept(conditionChecks);
            if (result.id && !select.isProjection()) { // TMDM-5965: IdQueryHandler has trouble with projections using reusable type's elements.
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Using \"get by id\" strategy");
                }
                return new IdQueryHandler(storage, mappings, storageClassLoader, session, select, this.selectedFields, callbacks);
            }
        }
        // Instance paging (TMDM-5388).
        if (!select.isProjection() && select.getTypes().size() == 1) {
            ComplexTypeMetadata uniqueType = select.getTypes().get(0);
            if (uniqueType.getSubTypes().isEmpty() && uniqueType.getSuperTypes().isEmpty()) {
                TypeMapping mappingFromDatabase = mappings.getMappingFromDatabase(uniqueType);
                if (allowInClauseOptimization(mappingFromDatabase)) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Using \"id in clause\" strategy");
                    }
                    return new InClauseOptimization(storage,
                            mappings,
                            resolver,
                            storageClassLoader,
                            session,
                            select,
                            this.selectedFields,
                            callbacks,
                            InClauseOptimization.Mode.CONSTANT);
                }
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Using \"standard query\" strategy");
        }
        return new StandardQueryHandler(storage, mappings, resolver, storageClassLoader, session, select, this.selectedFields, callbacks);
    }

    private static boolean allowInClauseOptimization(TypeMapping mappingFromDatabase) {
        boolean allowInClauseOptimization = mappingFromDatabase instanceof ScatteredTypeMapping;
        boolean containsManyField = false;
        int referenceFieldCount = 0;
        if (mappingFromDatabase instanceof FlatTypeMapping) {
            Collection<FieldMetadata> fields = mappingFromDatabase.getDatabase().getFields();
            for (FieldMetadata field : fields) {
                if (field instanceof ReferenceFieldMetadata) {
                    referenceFieldCount++;
                }
                if (field.isMany()) {
                    containsManyField = true;
                }
            }
        }
        return allowInClauseOptimization || containsManyField || referenceFieldCount > 1;
    }

    @Override
    public AbstractQueryHandler visit(Isa isa) {
        return null;
    }

    @Override
    public AbstractQueryHandler visit(StringConstant constant) {
        return null;
    }

    @Override
    public AbstractQueryHandler visit(IsEmpty isEmpty) {
        return null;
    }

    @Override
    public AbstractQueryHandler visit(IsNull isNull) {
        return null;
    }

    @Override
    public AbstractQueryHandler visit(NotIsEmpty notIsEmpty) {
        return null;
    }

    @Override
    public AbstractQueryHandler visit(NotIsNull notIsNull) {
        return null;
    }

    @Override
    public AbstractQueryHandler visit(Alias alias) {
        if (isCheckingProjection) {
            selectedFields.add(alias);
        }
        return null;
    }

    @Override
    public AbstractQueryHandler visit(Timestamp timestamp) {
        if (isCheckingProjection) {
            selectedFields.add(timestamp);
        }
        return null;
    }

    @Override
    public AbstractQueryHandler visit(TaskId taskId) {
        if (isCheckingProjection) {
            selectedFields.add(taskId);
        }
        return null;
    }

    @Override
    public AbstractQueryHandler visit(Type type) {
        if (isCheckingProjection) {
            selectedFields.add(type);
        }
        return null;
    }

    @Override
    public AbstractQueryHandler visit(BinaryLogicOperator condition) {
        condition.getLeft().accept(this);
        condition.getRight().accept(this);
        return null;
    }

    @Override
    public AbstractQueryHandler visit(UnaryLogicOperator condition) {
        condition.getCondition().accept(this);
        return null;
    }

    @Override
    public AbstractQueryHandler visit(Compare condition) {
        return null;
    }

    @Override
    public AbstractQueryHandler visit(StagingStatus stagingStatus) {
        return null;
    }

    @Override
    public AbstractQueryHandler visit(StagingError stagingError) {
        return null;
    }

    @Override
    public AbstractQueryHandler visit(StagingSource stagingSource) {
        return null;
    }

    @Override
    public AbstractQueryHandler visit(Condition condition) {
        return null;
    }

    @Override
    public AbstractQueryHandler visit(Count count) {
        if (isCheckingProjection) {
            selectedFields.add(count);
        }
        return null;
    }

    @Override
    public AbstractQueryHandler visit(FullText fullText) {
        fullTextExpression = fullText;
        isFullText = true;
        return null;
    }

    @Override
    public AbstractQueryHandler visit(FieldFullText fieldFullText) {
        fullTextExpression = fieldFullText;
        isFullText = true;
        return null;
    }

    @Override
    public AbstractQueryHandler visit(Range range) {
        return null;
    }

    @Override
    public AbstractQueryHandler visit(Field field) {
        selectedFields.add(field);
        return null;
    }

    private static class ConditionChecks extends VisitorAdapter<ConditionChecks.Result> {

        private final Select select;

        private FieldMetadata keyField;

        public ConditionChecks(Select select) {
            this.select = select;
        }

        class Result {
            boolean id = false;
            public boolean limitJoins = false;
        }

        @Override
        public Result visit(Isa isa) {
            Result fieldResult = new Result();
            fieldResult.id = false;
            return fieldResult;
        }

        @Override
        public Result visit(IsEmpty isEmpty) {
            Result fieldResult = new Result();
            fieldResult.id = false;
            return fieldResult;
        }

        @Override
        public Result visit(NotIsEmpty notIsEmpty) {
            Result fieldResult = new Result();
            fieldResult.id = false;
            return fieldResult;
        }

        @Override
        public Result visit(UnaryLogicOperator condition) {
            // TMDM-5319: Using a 'not' predicate, don't do a get by id.
            if (condition.getPredicate() == Predicate.NOT) {
                Result fieldResult = new Result();
                fieldResult.id = false;
                return fieldResult;
            } else {
                return condition.getCondition().accept(this);
            }
        }

        @Override
        public Result visit(Condition condition) {
            Result conditionResult = new Result();
            conditionResult.id = condition != UserQueryHelper.NO_OP_CONDITION;
            return conditionResult;
        }

        @Override
        public Result visit(BinaryLogicOperator condition) {
            Result conditionResult = new Result();
            Result leftResult = condition.getLeft().accept(this);
            Result rightResult = condition.getRight().accept(this);
            conditionResult.id = leftResult.id && rightResult.id;
            conditionResult.limitJoins = leftResult.limitJoins && rightResult.limitJoins;
            return conditionResult;
        }

        @Override
        public Result visit(StagingStatus stagingStatus) {
            Result fieldResult = new Result();
            fieldResult.id = false;
            return fieldResult;
        }

        @Override
        public Result visit(StagingError stagingError) {
            Result fieldResult = new Result();
            fieldResult.id = false;
            return fieldResult;
        }

        @Override
        public Result visit(StagingSource stagingSource) {
            Result fieldResult = new Result();
            fieldResult.id = false;
            return fieldResult;
        }

        @Override
        public Result visit(Compare condition) {
            Result conditionResult = new Result();
            Result result = condition.getLeft().accept(this);
            conditionResult.id = result.id && condition.getPredicate() == Predicate.EQUALS;
            conditionResult.limitJoins = result.limitJoins;
            return conditionResult;
        }

        @Override
        public Result visit(Id id) {
            Result fieldResult = new Result();
            fieldResult.id = false;
            return fieldResult;
        }

        @Override
        public Result visit(Timestamp timestamp) {
            Result fieldResult = new Result();
            fieldResult.id = false;
            return fieldResult;
        }

        @Override
        public Result visit(IsNull isNull) {
            Result fieldResult = new Result();
            fieldResult.id = false;
            return fieldResult;
        }

        @Override
        public Result visit(NotIsNull notIsNull) {
            Result fieldResult = new Result();
            fieldResult.id = false;
            return fieldResult;
        }

        @Override
        public Result visit(FullText fullText) {
            Result fieldResult = new Result();
            fieldResult.id = false;
            return fieldResult;
        }

        @Override
        public Result visit(Range range) {
            Result fieldResult = new Result();
            fieldResult.id = false;
            return fieldResult;
        }

        @Override
        public Result visit(Field field) {
            Result result = new Result();
            FieldMetadata fieldMetadata = field.getFieldMetadata();
            // Limit join for contained fields
            if (!result.limitJoins) {
                int level = 0;
                ComplexTypeMetadata containingType = fieldMetadata.getContainingType();
                while (containingType instanceof ContainedComplexTypeMetadata) {
                    containingType = ((ContainedComplexTypeMetadata) containingType).getContainerType();
                    level++;
                }
                if (level > 2 || !select.getTypes().contains(fieldMetadata.getContainingType())) {
                    result.limitJoins = true;
                }
            }
            if (fieldMetadata.getContainingType().getKeyFields().size() == 1) {
                if (fieldMetadata.isKey() && !(fieldMetadata instanceof CompoundFieldMetadata)) {
                    if (keyField != null) {
                        // At least twice an Id field means different Id values
                        // TODO Support for "entity/id = n AND entity/id = n" (could simplified to "entity/id = n").
                        result.id = false;
                    } else {
                        keyField = fieldMetadata;
                        result.id = true;
                    }
                }
            } // TODO Support compound key field.
            return result;
        }
    }
}
