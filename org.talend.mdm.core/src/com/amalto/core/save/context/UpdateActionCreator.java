/*
 * Copyright (C) 2006-2015 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package com.amalto.core.save.context;

import com.amalto.core.history.Action;
import com.amalto.core.history.MutableDocument;
import com.amalto.core.history.accessor.Accessor;
import com.amalto.core.history.action.FieldInsertAction;
import com.amalto.core.history.action.FieldUpdateAction;
import com.amalto.core.metadata.*;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import org.talend.mdm.commmon.util.core.EUUIDCustomType;
import java.util.*;

// TODO Clean up: preserveCollectionOldValues is dedicated to partial update only!
class UpdateActionCreator extends DefaultMetadataVisitor<List<Action>> {

    private static final Logger LOGGER = Logger.getLogger(UpdateActionCreator.class);

    protected final Stack<String> path = new Stack<String>();

    protected final LinkedList<Action> actions = new LinkedList<Action>();

    protected final Date date;

    protected final String source;

    protected final String userName;

    protected final MutableDocument originalDocument;

    protected final MutableDocument newDocument;

    protected final int insertIndex;

    protected final MetadataRepository repository;

    private final Closure closure = new CompareClosure();

    private final Set<String> touchedPaths = new HashSet<String>();

    private final Map<FieldMetadata, Integer> originalFieldToLastIndex = new HashMap<FieldMetadata, Integer>();

    protected boolean preserveCollectionOldValues;

    private String lastMatchPath;

    private boolean isDeletingContainedElement = false;

    public UpdateActionCreator(MutableDocument originalDocument,
                               MutableDocument newDocument,
                               Date date,
                               boolean preserveCollectionOldValues,
                               String source,
                               String userName,
                               MetadataRepository repository) {
        this(originalDocument, newDocument, date, preserveCollectionOldValues, -1, source, userName, repository);
    }

    public UpdateActionCreator(MutableDocument originalDocument,
                               MutableDocument newDocument,
                               Date date,
                               boolean preserveCollectionOldValues,
                               int insertIndex,
                               String source,
                               String userName,
                               MetadataRepository repository) {
        this.preserveCollectionOldValues = preserveCollectionOldValues;
        this.originalDocument = originalDocument;
        this.newDocument = newDocument;
        this.insertIndex = insertIndex;
        this.repository = repository;
        this.date = date;
        this.source = source;
        this.userName = userName;
    }

    @Override
    public List<Action> visit(ComplexTypeMetadata complexType) {
        // This is an update, so both original and new document have a "entity root" element (TMDM-3883).
        generateNoOp("/"); //$NON-NLS-1$
        super.visit(complexType);
        return actions;
    }

    @Override
    public List<Action> visit(ContainedTypeFieldMetadata containedField) {
        handleField(containedField, new ContainedTypeClosure(containedField));
        return actions;
    }

    @Override
    public List<Action> visit(ReferenceFieldMetadata referenceField) {
        handleField(referenceField, getClosure());
        return actions;
    }

    @Override
    public List<Action> visit(SimpleTypeFieldMetadata simpleField) {
        handleField(simpleField, getClosure());
        return actions;
    }

    @Override
    public List<Action> visit(EnumerationFieldMetadata enumField) {
        handleField(enumField, getClosure());
        return actions;
    }

    protected Closure getClosure() {
        return closure;
    }

    /**
     * Interface to encapsulate action to execute on fields
     */
    interface Closure {
        void execute(FieldMetadata field);
    }

    String getLeftPath() {
        return computePath(path);
    }

    String getRightPath() {
        return computePath(path);
    }

    private String computePath(Stack<String> path) {
        if (path.isEmpty()) {
            throw new IllegalStateException();
        } else {
            StringBuilder builder = new StringBuilder();
            Iterator<String> pathIterator = path.iterator();
            while (pathIterator.hasNext()) {
                builder.append(pathIterator.next());
                if (pathIterator.hasNext()) {
                    builder.append('/');
                }
            }
            return builder.toString();
        }
    }

    protected void handleField(FieldMetadata field, Closure closure) {
        path.add(field.getName());
        if (field.isMany()) {
            String currentPath = getLeftPath();
            Accessor leftAccessor;
            Accessor rightAccessor;
            try {
                rightAccessor = newDocument.createAccessor(currentPath);
                if (!rightAccessor.exist() && !isDeletingContainedElement) {
                    // If new list does not exist, it means element was omitted in new version (legacy behavior).
                    return;
                }
                leftAccessor = originalDocument.createAccessor(currentPath);
            } finally {
                path.pop();
            }
            // Proceed in "reverse" order (highest index to lowest) so there won't be issues when deleting elements in
            // a sequence (if element #2 is deleted before element #3, element #3 becomes #2...).
            int max = Math.max(leftAccessor.size(), rightAccessor.size());
            for (int i = max; i > 0; i--) {
                // XPath indexes are 1-based (not 0-based).
                path.add(field.getName() + '[' + i + ']');
                closure.execute(field);
                path.pop();
            }
            path.add(field.getName() + '[' + max + ']');
            path.pop();
        } else {
            closure.execute(field);
            path.pop();
        }
    }

    protected void compare(FieldMetadata comparedField) {
        if (comparedField.isKey()) {
            // Can't update a key: don't even try to compare the field (but update lastMatchPath in case next compared
            // element is right after key field).
            lastMatchPath = getLeftPath();
            return;
        }
        if (path.isEmpty()) {
            throw new IllegalStateException("Path for compare can not be empty.");
        }
        String path = getLeftPath();
        Accessor originalAccessor = originalDocument.createAccessor(path);
        Accessor newAccessor = newDocument.createAccessor(path);
        if (!originalAccessor.exist()) {
            if (!newAccessor.exist()) {
                // No op
            } else { // new accessor exist
                if (newAccessor.get() != null && !newAccessor.get().isEmpty()) { // Empty accessor means no op to ensure legacy behavior
                    generateNoOp(lastMatchPath);
                    actions.add(new FieldUpdateAction(date, source, userName, path, StringUtils.EMPTY, newAccessor.get(), comparedField));
                    generateNoOp(path);
                } else {
                    // No op.
                }
            }
        } else { // original accessor exist
            String oldValue = originalAccessor.get();
            lastMatchPath = path;
            if (!newAccessor.exist()) {
                if (comparedField.isMany() && !preserveCollectionOldValues) {
                    // TMDM-5216: Visit sub fields include old/new values for sub elements.
                    if (comparedField instanceof ContainedTypeFieldMetadata) {
                        isDeletingContainedElement = true;
                        ((ContainedTypeFieldMetadata) comparedField).getContainedType().accept(this);
                        isDeletingContainedElement = false;
                    } else if (!isDeletingContainedElement) {
                        // TMDM-5257: RemoveSimpleTypeNodeWithOccurrence
                        // Null values may happen if accessor is targeting an element that contains other elements
                        actions.add(new FieldUpdateAction(date, source, userName, path, oldValue == null ? StringUtils.EMPTY
                                : oldValue, null, comparedField));
                    }
                }
                if (isDeletingContainedElement) {
                    // Null values may happen if accessor is targeting an element that contains other elements
                    actions.add(new FieldUpdateAction(date, source, userName, path, oldValue == null ? StringUtils.EMPTY
                            : oldValue, null, comparedField));
                }
            } else { // new accessor exist
                String newValue = newAccessor.get();
                if (newAccessor.get() != null && !(comparedField instanceof ContainedTypeFieldMetadata)) {
                    if (comparedField.isMany() && preserveCollectionOldValues) {
                        // Append at the end of the collection
                        if (!originalFieldToLastIndex.containsKey(comparedField)) {
                            originalFieldToLastIndex.put(comparedField, originalAccessor.size());
                        }
                        String previousPathElement = this.path.pop();
                        int insertIndex = getInsertIndex(comparedField);
                        this.path.push(comparedField.getName() + "[" + insertIndex + "]");
                        actions.add(new FieldInsertAction(date, source, userName, getLeftPath(), StringUtils.EMPTY, newValue, comparedField));
                        this.path.pop();
                        this.path.push(previousPathElement);
                    } else if (oldValue != null && !oldValue.equals(newValue)) {
                        if (!"string".equals(comparedField.getType().getName()) && !(comparedField instanceof ReferenceFieldMetadata)) {
                            // Field is not string. To ensure false positive difference detection, creates a typed value.
                            Object oldObject = MetadataUtils.convert(oldValue, comparedField);
                            Object newObject = MetadataUtils.convert(newValue, comparedField);
                            if (oldObject != null && newObject != null && oldObject instanceof Comparable) {
                                if (((Comparable) oldObject).compareTo(newObject) == 0) {
                                    // Objects are the 'same' (e.g. 10.0 is same as 10).
                                    return;
                                }
                            } else {
                                if (oldObject != null && oldObject.equals(newObject)) {
                                    return;
                                } else if (newObject != null && newObject.equals(oldObject)) {
                                    return;
                                }
                            }
                        }

                        actions.add(new FieldUpdateAction(date, source, userName, path, oldValue, newAccessor.get(), comparedField));

                    } else if (oldValue != null && oldValue.equals(newValue)) {
                        generateNoOp(path);
                    }
                }
            }
        }
    }

    private int getInsertIndex(FieldMetadata comparedField) {
        if (insertIndex < 0) {
            int newIndex = originalFieldToLastIndex.get(comparedField);
            newIndex = newIndex + 1;
            originalFieldToLastIndex.put(comparedField, newIndex);
            return newIndex;
        } else {
            return insertIndex;
        }
    }

    protected void generateNoOp(String path) {
        // TODO Do only this if type is a sequence (useless if type isn't ordered).
        if (!touchedPaths.contains(path) && path != null) {
            touchedPaths.add(path);
            actions.add(new TouchAction(path, date, source, userName));
        }
    }

    private class ContainedTypeClosure implements Closure {

        private final ContainedTypeFieldMetadata containedField;

        public ContainedTypeClosure(ContainedTypeFieldMetadata containedField) {
            this.containedField = containedField;
        }

        public void execute(FieldMetadata field) {
            ComplexTypeMetadata type = containedField.getContainedType();

            compare(field);

            Accessor leftAccessor = originalDocument.createAccessor(getLeftPath());
            Accessor rightAccessor = newDocument.createAccessor(getRightPath());
            if (rightAccessor.exist()) {
                String newType = rightAccessor.getActualType();
                String previousType = StringUtils.EMPTY;
                if (leftAccessor.exist()) {
                    previousType = leftAccessor.getActualType();
                }

                if (!newType.isEmpty()) {
                    ComplexTypeMetadata newTypeMetadata = (ComplexTypeMetadata) repository.getNonInstantiableType(repository.getUserNamespace(), newType);
                    ComplexTypeMetadata previousTypeMetadata = (ComplexTypeMetadata) repository.getNonInstantiableType(repository.getUserNamespace(), previousType);
                    // Perform some checks about the xsi:type value (valid or not?).
                    if (newTypeMetadata == null) {
                        throw new IllegalArgumentException("Type '" + newType + "' was not found.");
                    }
                    // Check if type of element isn't a subclass of declared type (use of xsi:type).
                    if (!newTypeMetadata.isAssignableFrom(field.getType())) {
                        throw new IllegalArgumentException("Type '" + field.getType().getName()
                                + "' is not assignable from type '" + newTypeMetadata.getName() + "'");
                    }
                    actions.add(new ChangeTypeAction(date, source, userName, getLeftPath(), previousTypeMetadata, newTypeMetadata));
                    type = newTypeMetadata;
                }
            }

            Action before = actions.getLast();
            type.accept(UpdateActionCreator.this);
            // Way to detect if there is a change in elements below: check if last action in list changed.
            boolean hasActions = actions.getLast() != before;
            if (leftAccessor.exist() || (rightAccessor.exist() && hasActions)) {
                lastMatchPath = getLeftPath();
            }
        }
    }

    private class CompareClosure implements Closure {

        private Logger LOGGER = Logger.getLogger(CompareClosure.class);

        public void execute(FieldMetadata field) {
            compare(field);
            if (field instanceof ReferenceFieldMetadata) {
                Accessor leftAccessor = originalDocument.createAccessor(getLeftPath());
                Accessor rightAccessor = newDocument.createAccessor(getRightPath());
                if (rightAccessor.exist()) {
                    String newType = rightAccessor.getActualType();
                    String previousType = StringUtils.EMPTY;
                    if (leftAccessor.exist()) {
                        previousType = leftAccessor.getActualType();
                    }

                    if (!newType.isEmpty()) {
                        ComplexTypeMetadata newTypeMetadata = (ComplexTypeMetadata) repository.getNonInstantiableType(repository.getUserNamespace(), newType);
                        ComplexTypeMetadata previousTypeMetadata = null;
                        if (newTypeMetadata != null && !newTypeMetadata.isInstantiable()) {
                            ComplexTypeMetadata actualNewTypeMetadata = null;
                            Collection<TypeMetadata> instantiableTypes = repository.getInstantiableTypes();
                            for (TypeMetadata instantiableType : instantiableTypes) {
                                if (newType.equals(instantiableType.getData(MetadataRepository.COMPLEX_TYPE_NAME))) {
                                    if (actualNewTypeMetadata != null) {
                                        // Multiple candidates for inheritance are forbidden / not supported.
                                        throw new IllegalArgumentException("Reusable type '" + newType + "'is at least used by " +
                                                "'" + actualNewTypeMetadata.getName() + "' and '" + instantiableType.getName() + "'.");
                                    }
                                    actualNewTypeMetadata = (ComplexTypeMetadata) instantiableType;
                                }
                            }
                            if (actualNewTypeMetadata == null) {
                                // Type is still a 'reusable' type (not entity): this is an error case.
                                throw new IllegalStateException("Can not find entity type using reusable type '" + newType + "'.");
                            }
                            if (LOGGER.isDebugEnabled()) {
                                LOGGER.debug("Replacing type '" + newType + "' with '" + actualNewTypeMetadata.getName() + ".");
                            }
                            newTypeMetadata = actualNewTypeMetadata;
                            previousTypeMetadata = (ComplexTypeMetadata) repository.getNonInstantiableType(repository.getUserNamespace(), previousType);
                        } else if (newTypeMetadata == null) {
                            newTypeMetadata = (ComplexTypeMetadata) repository.getType(newType);
                            previousTypeMetadata = (ComplexTypeMetadata) repository.getType(previousType);
                        }
                        
                        // TODO Perform some checks about the tmdm:type value (valid or not?).
                        actions.add(new ChangeReferenceTypeAction(date, source, userName, getLeftPath(), previousTypeMetadata, newTypeMetadata));
                    }
                }
                Action before = actions.getLast();
                // Way to detect if there is a change in elements below: check if last action in list changed.
                boolean hasActions = actions.getLast() != before;
                if (leftAccessor.exist() || (rightAccessor.exist() && hasActions)) {
                    lastMatchPath = getLeftPath();
                }
            }
        }
    }
}
