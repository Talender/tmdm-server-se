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

import com.amalto.core.metadata.*;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.List;

/**
 *
 */
public class UserQueryBuilder {

    public static final String STAGING_STATUS_FIELD = "$staging_status$"; //$NON-NLS-1$

    public static final String STAGING_SOURCE_FIELD = "$staging_source$"; //$NON-NLS-1$

    public static final String STAGING_ERROR_FIELD = "$staging_error$"; //$NON-NLS-1$

    public static final String STAGING_STATUS_ALIAS = "staging_status"; //$NON-NLS-1$

    public static final String STAGING_SOURCE_ALIAS = "staging_source"; //$NON-NLS-1$

    public static final String STAGING_ERROR_ALIAS = "staging_error"; //$NON-NLS-1$

    public static final String TIMESTAMP_FIELD = "../../t"; //$NON-NLS-1$

    public static final String TIMESTAMP_ALIAS = "timestamp"; //$NON-NLS-1$

    public static final String TASK_ID_FIELD = "../../taskId"; //$NON-NLS-1$

    public static final String TASK_ID_ALIAS = "taskId"; //$NON-NLS-1$

    public static final String ID_FIELD = "../../i"; //$NON-NLS-1$

    public static final String ID_ALIAS = "i"; //$NON-NLS-1$
    
    public static final String ALL_FIELD = "../*";  //$NON-NLS-1$

    private static final Logger LOGGER = Logger.getLogger(UserQueryBuilder.class);

    private final Expression expression;

    private UserQueryBuilder(Expression expression) {
        this.expression = expression;
    }

    private Select expressionAsSelect() {
        return ((Select) expression);
    }

    public static Condition and(Condition left, Condition right) {
        assertConditionsArguments(left, right);
        return new BinaryLogicOperator(left, Predicate.AND, right);
    }

    public static Condition not(Condition condition) {
        return new UnaryLogicOperator(condition, Predicate.NOT);
    }

    public static Condition or(Condition left, Condition right) {
        assertConditionsArguments(left, right);
        return new BinaryLogicOperator(left, Predicate.OR, right);
    }

    public static Condition startsWith(FieldMetadata field, String constant) {
        assertValueConditionArguments(field, constant);
        Field userField = new Field(field);
        return startsWith(userField, constant);
    }

    public static Condition startsWith(TypedExpression field, String constant) {
        assertValueConditionArguments(field, constant);
        if (constant.charAt(0) == '^') {
            constant = constant.substring(1);
        }
        return new Compare(field, Predicate.STARTS_WITH, createConstant(field, constant));
    }

    public static Condition gt(FieldMetadata field, String constant) {
        assertValueConditionArguments(field, constant);
        Field userField = new Field(field);
        return gt(userField, constant);
    }

    public static Condition gt(TypedExpression expression, String constant) {
        assertValueConditionArguments(expression, constant);
        return new Compare(expression, Predicate.GREATER_THAN, createConstant(expression, constant));
    }

    public static Condition gte(FieldMetadata field, String constant) {
        assertValueConditionArguments(field, constant);
        Field userField = new Field(field);
        return gte(userField, constant);
    }

    public static Condition gte(TypedExpression expression, String constant) {
        assertValueConditionArguments(expression, constant);
        return new Compare(expression, Predicate.GREATER_THAN_OR_EQUALS, createConstant(expression, constant));
    }

    public static Compare lt(FieldMetadata field, String constant) {
        assertValueConditionArguments(field, constant);
        Field userField = new Field(field);
        return lt(userField, constant);
    }

    public static Compare lt(TypedExpression expression, String constant) {
        return new Compare(expression, Predicate.LOWER_THAN, createConstant(expression, constant));
    }

    public static Compare lte(FieldMetadata field, String constant) {
        assertValueConditionArguments(field, constant);
        Field userField = new Field(field);
        return lte(userField, constant);
    }

    public static Compare lte(TypedExpression expression, String constant) {
        return new Compare(expression, Predicate.LOWER_THAN_OR_EQUALS, createConstant(expression, constant));
    }

    public static Compare lte(FieldMetadata left, FieldMetadata right) {
        return new Compare(new Field(left), Predicate.LOWER_THAN_OR_EQUALS, new Field(right));
    }

    public static Compare eq(FieldMetadata left, FieldMetadata right) {
        return new Compare(new Field(left), Predicate.EQUALS, new Field(right));
    }

    public static Condition eq(TypedExpression expression, String constant) {
        assertValueConditionArguments(expression, constant);
        if (expression instanceof Field) {
            return eq(((Field) expression), constant);
        } else {
            return new Compare(expression, Predicate.EQUALS, createConstant(expression, constant));
        }
    }

    public static Condition eq(FieldMetadata field, String constant) {
        assertValueConditionArguments(field, constant);
        Field userField = new Field(field);
        return eq(userField, constant);
    }

    public static Condition eq(Field field, String constant) {
        assertValueConditionArguments(field, constant);
        if (field.getFieldMetadata() instanceof ReferenceFieldMetadata) {
            ReferenceFieldMetadata fieldMetadata = (ReferenceFieldMetadata) field.getFieldMetadata();
            return new Compare(field, Predicate.EQUALS, new Id(fieldMetadata.getReferencedType(), constant));
        } else {
            return new Compare(field, Predicate.EQUALS, createConstant(field, constant));
        }
    }

    public static Condition isa(FieldMetadata field, ComplexTypeMetadata type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null.");
        }
        if (field instanceof ReferenceFieldMetadata) {
            throw new IllegalArgumentException("Cannot perform type check on a foreign key.");
        }
        Condition current = new Isa(new Field(field), type);
        if (!type.getSubTypes().isEmpty()) {
            for (ComplexTypeMetadata subType : type.getSubTypes()) {
                current = or(current, isa(field, subType));
            }
        }
        return current;
    }

    public UserQueryBuilder isa(ComplexTypeMetadata type) {
        if (expression == null || expressionAsSelect().getTypes().isEmpty()) {
            throw new IllegalStateException("No type is currently selected.");
        }
        ComplexTypeMetadata mainType = getSelect().getTypes().get(0);
        if (!type.isAssignableFrom(mainType)) {
            throw new IllegalArgumentException("Type '" + type.getName() + "' is not assignable from '" + mainType.getName() + "'.");
        } else if (!mainType.equals(type) || (mainType.getSuperTypes().isEmpty() && !mainType.getSubTypes().isEmpty())) {
            where(new Isa(new ComplexTypeExpression(mainType), type));
            return this;
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Ignore 'is a' statement of type '" + type.getName() + "' since it is not part of an " +
                        "inheritance tree OR a expression like 'type1 isa type1' was detected.");
            }
            return this;
        }
    }

    private static Expression createConstant(TypedExpression expression, String constant) {
        String fieldTypeName = expression.getTypeName();
        if ("integer".equals(fieldTypeName)  //$NON-NLS-1$
                || "positiveInteger".equals(fieldTypeName) //$NON-NLS-1$
                || "negativeInteger".equals(fieldTypeName) //$NON-NLS-1$
                || "nonPositiveInteger".equals(fieldTypeName) //$NON-NLS-1$
                || "nonNegativeInteger".equals(fieldTypeName) //$NON-NLS-1$
                || "unsignedInt".equals(fieldTypeName) //$NON-NLS-1$
                || "int".equals(fieldTypeName)) { //$NON-NLS-1$
            return new IntegerConstant(Integer.parseInt(constant));
        } else if ("string".equals(fieldTypeName) //$NON-NLS-1$
                || "hexBinary".equals(fieldTypeName) //$NON-NLS-1$
                || "base64Binary".equals(fieldTypeName) //$NON-NLS-1$
                || "anyURI".equals(fieldTypeName) //$NON-NLS-1$
                || "QName".equals(fieldTypeName)) { //$NON-NLS-1$
            return new StringConstant(constant);
        } else if ("date".equals(fieldTypeName)) { //$NON-NLS-1$
            return new DateConstant(constant);
        } else if ("dateTime".equals(fieldTypeName)) { //$NON-NLS-1$
            return new DateTimeConstant(constant);
        } else if ("time".equals(fieldTypeName) || "duration".equals(fieldTypeName)) { //$NON-NLS-1$ //$NON-NLS-2$
            return new TimeConstant(constant);
        } else if ("boolean".equals(fieldTypeName)) { //$NON-NLS-1$
            boolean value = Boolean.parseBoolean(constant);
            return new BooleanConstant(value);
        } else if ("decimal".equals(fieldTypeName)) { //$NON-NLS-1$
            return new BigDecimalConstant(constant);
        } else if ("short".equals(fieldTypeName) || "unsignedShort".equals(fieldTypeName)) { //$NON-NLS-1$ //$NON-NLS-2$
            return new ShortConstant(constant);
        } else if ("byte".equals(fieldTypeName) || "unsignedByte".equals(fieldTypeName)) { //$NON-NLS-1$ //$NON-NLS-2$
            return new ByteConstant(constant);
        } else if ("long".equals(fieldTypeName) || "unsignedLong".equals(fieldTypeName)) { //$NON-NLS-1$ //$NON-NLS-2$
            return new LongConstant(constant);
        } else if ("double".equals(fieldTypeName)) { //$NON-NLS-1$
            return new DoubleConstant(constant);
        } else if ("float".equals(fieldTypeName)) { //$NON-NLS-1$
            return new FloatConstant(constant);
        } else {
            throw new IllegalArgumentException("Cannot create expression constant for expression type '" + expression.getTypeName() + "' (is expression allowed to contain values?)");
        }
    }

    public static Condition emptyOrNull(FieldMetadata field) {
        assertNullField(field);
        // Only do a isEmpty operator if field type is string, for all other known cases, isNull is enough.
        if ("string".equals(field.getType().getName())) {
            return new BinaryLogicOperator(isEmpty(field), Predicate.OR, isNull(field));
        } else {
            return isNull(field);
        }
    }

    public static Condition emptyOrNull(TypedExpression field) {
        assertNullField(field);
        // Only do a isEmpty operator if field type is string, for all other known cases, isNull is enough.
        if ("string".equals(field.getTypeName())) { //$NON-NLS-1$
            return new BinaryLogicOperator(isEmpty(field), Predicate.OR, isNull(field));
        } else {
            return isNull(field);
        }
    }

    public static Condition isEmpty(FieldMetadata field) {
        assertNullField(field);
        return new IsEmpty(new Field(field));
    }

    public static Condition isEmpty(TypedExpression typedExpression) {
        assertNullField(typedExpression);
        return or(new IsEmpty(typedExpression), isNull(typedExpression));
    }

    public static Condition isNull(FieldMetadata field) {
        assertNullField(field);
        return new IsNull(new Field(field));
    }

    public static Condition isNull(TypedExpression typedExpression) {
        assertNullField(typedExpression);
        return new IsNull(typedExpression);
    }

    public static Condition neq(FieldMetadata field, String constant) {
        assertValueConditionArguments(field, constant);
        return new UnaryLogicOperator(eq(field, constant), Predicate.NOT);
    }

    public static Condition neq(TypedExpression field, String constant) {
        assertValueConditionArguments(field, constant);
        return new UnaryLogicOperator(eq(field, constant), Predicate.NOT);
    }

    public static Condition fullText(String constant) {
        return new FullText(constant);
    }

    public static Condition fullText(FieldMetadata field, String constant) {
        return new FieldFullText(new Field(field), constant);
    }

    public static TypedExpression count() {
        return new Alias(new Count(), "count"); //$NON-NLS-1$
    }

    public static TypedExpression timestamp() {
        return Timestamp.INSTANCE;
    }

    public static TypedExpression taskId() {
        return TaskId.INSTANCE;
    }

    public static UserQueryBuilder from(ComplexTypeMetadata type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        Select select = new Select();
        select.addType(type);
        return new UserQueryBuilder(select);
    }

    public static UserQueryBuilder from(String nativeQuery) {
        NativeQuery select = new NativeQuery(nativeQuery);
        return new UserQueryBuilder(select);
    }

    public UserQueryBuilder select(FieldMetadata... fields) {
        if (fields == null) {
            throw new IllegalArgumentException("Fields cannot be null");
        }
        for (FieldMetadata field : fields) {
            select(field);
        }
        return this;
    }

    public UserQueryBuilder select(FieldMetadata field) {
        if (field == null) {
            throw new IllegalArgumentException("Field cannot be null");
        }
        TypedExpression typedExpression;
        if (field instanceof ContainedTypeFieldMetadata) {
            // Selecting a field without value is equivalent to select "" (empty string) with an alias name equals to
            // selected field. (see MSQL-50)
            typedExpression = new Alias(new StringConstant(StringUtils.EMPTY), field.getName());
        } else {
            typedExpression = new Field(field);
        }
        expressionAsSelect().getSelectedFields().add(typedExpression);
        expressionAsSelect().setProjection(true);
        return this;
    }

    public UserQueryBuilder select(ComplexTypeMetadata type, String fieldName) {
        if (type.hasField(fieldName)) {
            select(type.getField(fieldName));
        } else {
            if (STAGING_STATUS_FIELD.equals(fieldName)) {
                select(alias(UserStagingQueryBuilder.status(), STAGING_STATUS_ALIAS));
            } else if (STAGING_SOURCE_FIELD.equals(fieldName)) {
                select(alias(UserStagingQueryBuilder.source(), STAGING_SOURCE_ALIAS));
            } else if (STAGING_ERROR_FIELD.equals(fieldName)) {
                select(alias(UserStagingQueryBuilder.error(), STAGING_ERROR_ALIAS));
            } else if (TIMESTAMP_FIELD.equals(fieldName)) {
                select(alias(timestamp(), TIMESTAMP_ALIAS));
            } else if (TASK_ID_FIELD.equals(fieldName)) {
                select(alias(taskId(), TASK_ID_ALIAS));
            } else if (ID_FIELD.equals(fieldName)) {
                for (FieldMetadata keyField : type.getKeyFields()) {
                    select(alias(keyField, ID_ALIAS));
                }
            } else {
                throw new IllegalArgumentException("Field '" + fieldName + "' is not supported.");
            }
        }
        return this;
    }

    /**
     * Adds a {@link Condition} to the {@link Select} built by this {@link UserQueryBuilder}. If this method has previously
     * been called, a logic "and"/"or" condition (depends on <code>predicate</code> argument) is created between the
     * existing condition {@link com.amalto.core.query.user.Select#getCondition()} and the <code>condition</code> parameter.
     *
     * @param condition A {@link Condition} to be added to the user query.
     * @param predicate Predicate to use to link existing condition with <code>condition</code>.
     * @return Same {@link UserQueryBuilder} for chained method calls.
     * @throws IllegalArgumentException If <code>condition</code> parameter is null.
     */
    public UserQueryBuilder where(Condition condition, Predicate predicate) {
        if (condition == null) {
            throw new IllegalArgumentException("Condition cannot be null");
        }
        if (expressionAsSelect().getCondition() == null) {
            expressionAsSelect().setCondition(condition);
        } else {
            if (predicate == Predicate.OR) {
                expressionAsSelect().setCondition(or(expressionAsSelect().getCondition(), condition));
            } else if (predicate == Predicate.AND) {
                expressionAsSelect().setCondition(and(expressionAsSelect().getCondition(), condition));
            } else {
                throw new NotImplementedException("Not implemented: support of " + predicate);
            }
        }

        return this;
    }

    /**
     * <p>
     * Adds a {@link Condition} to the {@link Select} built by this {@link UserQueryBuilder}. If this method has previously
     * been called, a logic "and" condition is created between the existing condition {@link com.amalto.core.query.user.Select#getCondition()}
     * and the <code>condition</code> parameter.
     * </p>
     * <p>
     * This method is equivalent to:<br/>
     * <code>
     * Condition condition = ...<br/>
     * UserQueryBuilder qb = ...<br/>
     * qb.where(condition, Predicate.AND);<br/>
     * </code>
     * </p>
     *
     * @param condition A {@link Condition} to be added to the user query.
     * @return Same {@link UserQueryBuilder} for chained method calls.
     * @throws IllegalArgumentException If <code>condition</code> parameter is null.
     * @see #where(Condition, Predicate)
     */
    public UserQueryBuilder where(Condition condition) {
        where(condition, Predicate.AND);
        return this;
    }

    public UserQueryBuilder orderBy(FieldMetadata field, OrderBy.Direction direction) {
        if (field == null) {
            throw new IllegalArgumentException("Field cannot be null");
        }
        if (field instanceof ReferenceFieldMetadata) {
            // Order by a FK field is equivalent to a join on FK + a order by clause on referenced field.
            return join(field)
                    .orderBy(new Field(((ReferenceFieldMetadata) field).getReferencedField()), direction);
        } else {
            expressionAsSelect().setOrderBy(new OrderBy(new Field(field), direction));
        }
        return this;
    }

    public UserQueryBuilder orderBy(TypedExpression field, OrderBy.Direction direction) {
        if (field == null) {
            throw new IllegalArgumentException("Field cannot be null");
        }
        if (field instanceof Field) {
            orderBy(((Field) field).getFieldMetadata(), direction);
        } else {
            expressionAsSelect().setOrderBy(new OrderBy(field, direction));
        }
        return this;
    }

    public UserQueryBuilder join(TypedExpression leftField, FieldMetadata rightField) {
        if (!(leftField instanceof Field)) {
            throw new IllegalArgumentException("Can not perform join on a non-user field (was " + leftField.getClass().getName() + ")");
        }
        return join(((Field) leftField).getFieldMetadata(), rightField);
    }

    /**
     * <p>
     * Join a type's field with another.
     * </p>
     * <p>
     * If left field is a FK, use this method for Joins when right field is a simple PK (i.e. joined entity does not
     * have composite id). If this is the case, consider using {@link #join(com.amalto.core.metadata.FieldMetadata)}.
     * </p>
     *
     * @param leftField  The left field for the join operation.
     * @param rightField The right field for the join operation.
     * @return Same {@link UserQueryBuilder} for chained method calls.
     */
    public UserQueryBuilder join(FieldMetadata leftField, FieldMetadata rightField) {
        if (leftField == null) {
            throw new IllegalArgumentException("Left field cannot be null");
        }
        if (rightField == null) {
            throw new IllegalArgumentException("Right field cannot be null");
        }
        if (leftField instanceof ReferenceFieldMetadata) {
            FieldMetadata leftReferencedField = ((ReferenceFieldMetadata) leftField).getReferencedField();
            if (!leftReferencedField.equals(rightField)) {
                throw new IllegalArgumentException("Left field '" + leftReferencedField.getName() + "' is a FK, but right field isn't the one left is referring to.");
            }
        }
        Field leftUserField = new Field(leftField);
        Field rightUserField = new Field(rightField);
        // Implicit select joined type if it isn't already selected
        if (!expressionAsSelect().getTypes().contains(rightField.getContainingType())) {
            expressionAsSelect().addType(rightField.getContainingType());
        }
        JoinType joinType = leftField.isMandatory() ? JoinType.INNER : JoinType.LEFT_OUTER;
        expressionAsSelect().addJoin(new Join(leftUserField, rightUserField, joinType));
        return this;
    }

    /**
     * <p>
     * Join a type's field with another. This method expects field to be a {@link ReferenceFieldMetadata} and automatically
     * creates a Join between <code>field</code> parameter and the field(s) it targets.
     * </p>
     *
     * @param field The left field for the join operation.
     * @return Same {@link UserQueryBuilder} for chained method calls.
     * @throws IllegalArgumentException If <code>field</code> is not a {@link ReferenceFieldMetadata}.
     */
    public UserQueryBuilder join(FieldMetadata field) {
        if (field == null) {
            throw new IllegalArgumentException("Field cannot be null");
        }
        if (!(field instanceof ReferenceFieldMetadata)) {
            throw new IllegalArgumentException("Field must be a reference field.");
        }
        return join(field, ((ReferenceFieldMetadata) field).getReferencedField());
    }

    public UserQueryBuilder start(int start) {
        if (start < 0) {
            throw new IllegalArgumentException("Start index must be positive");
        }
        expressionAsSelect().getPaging().setStart(start);
        return this;
    }

    public UserQueryBuilder limit(int limit) {
        if (limit > 0) {
            // Only consider limit > 0 as worthy values.
            expressionAsSelect().getPaging().setLimit(limit);
        }
        return this;
    }

    public Select getSelect() {
        if (expression == null) {
            throw new IllegalStateException("No type has been selected");
        }
        return expressionAsSelect();
    }

    public Expression getExpression() {
        if (expression == null) {
            throw new IllegalStateException("No type has been selected");
        }
        return expression;
    }

    public UserQueryBuilder and(ComplexTypeMetadata type) {
        expressionAsSelect().addType(type);
        return this;
    }

    private static void assertConditionsArguments(Condition left, Condition right) {
        if (left == null) {
            throw new IllegalArgumentException("Left condition cannot be null");
        }
        if (right == null) {
            throw new IllegalArgumentException("Right condition cannot be null");
        }
    }

    private static void assertValueConditionArguments(Object field, String constant) {
        if (field == null) {
            throw new IllegalArgumentException("Field cannot be null");
        }
        if (constant == null) {
            throw new IllegalArgumentException("Constant cannot be null");
        }
    }

    private static void assertNullField(Object field) {
        if (field == null) {
            throw new IllegalArgumentException("Field cannot be null");
        }
    }

    /**
     * Adds a {@link TypedExpression} the query should return. If the typed expression has already been selected, an
     * {@link Alias} is automatically created.
     *
     * @param expression Expression that represents a value to return in query results.
     * @return This instance for method call chaining.
     */
    public UserQueryBuilder select(TypedExpression expression) {
        Select select = expressionAsSelect();
        List<TypedExpression> selectedFields = select.getSelectedFields();
        if (!selectedFields.contains(expression)) {
            selectedFields.add(expression);
        } else {
            if (expression instanceof Field) {
                // TMDM-5022: Automatic alias if a field with same name was already selected.
                selectedFields.add(alias(expression, ((Field) expression).getFieldMetadata().getName()));
            } else {
                throw new UnsupportedOperationException("Can't select twice a non-field expression.");
            }
        }
        select.setProjection(true);
        return this;
    }

    public static TypedExpression alias(FieldMetadata field, String alias) {
        return alias(new Field(field), alias);
    }

    public static TypedExpression alias(TypedExpression expression, String alias) {
        return new Alias(expression, alias);
    }

    public UserQueryBuilder selectId(ComplexTypeMetadata typeMetadata) {
        List<FieldMetadata> keyFields = typeMetadata.getKeyFields();
        for (FieldMetadata keyField : keyFields) {
            select(keyField);
        }
        return this;
    }

    public UserQueryBuilder select(List<FieldMetadata> viewableFields) {
        if (viewableFields == null) {
            throw new IllegalArgumentException("Viewable fields cannot be null");
        }

        for (FieldMetadata viewableField : viewableFields) {
            select(viewableField);
        }
        return this;
    }

    public static Condition contains(FieldMetadata field, String value) {
        assertValueConditionArguments(field, value);
        if (value.isEmpty()) {
            return UserQueryHelper.NO_OP_CONDITION;
        }
        Field userField = new Field(field);
        return contains(userField, value);
    }

    public static Condition contains(TypedExpression field, String value) {
        assertValueConditionArguments(field, value);
        Expression constant = createConstant(field, value);
        if (constant instanceof StringConstant) {
            return new Compare(field, Predicate.CONTAINS, constant);
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Change CONTAINS to EQUALS for '" + field + "' (type: " + field.getTypeName() + ").");
            }
            return new Compare(field, Predicate.EQUALS, constant);
        }
    }

    public static TypedExpression type(FieldMetadata field) {
        if (!(field.getType() instanceof ComplexTypeMetadata)) {
            throw new IllegalArgumentException("Expected a complex type for field '" + field.getName() + "'.");
        }
        ComplexTypeMetadata fieldType = (ComplexTypeMetadata) field.getType();
        if (fieldType.getSubTypes().isEmpty()) {
            return new StringConstant(fieldType.getName());
        } else {
            return new Type(new Field(field));
        }
    }
}
