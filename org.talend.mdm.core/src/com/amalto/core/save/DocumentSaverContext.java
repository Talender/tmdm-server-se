/*
 * Copyright (C) 2006-2015 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package com.amalto.core.save;

import java.util.List;
import java.util.Map;

import com.amalto.core.history.Action;
import com.amalto.core.history.MutableDocument;
import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.save.context.DocumentSaver;

/**
 * A context holding information about the save of a <b>single</b> record in MDM.
 */
public interface DocumentSaverContext {

    /**
     * @return Creates a {@link DocumentSaver} chain able to store the record.
     */
    DocumentSaver createSaver();

    /**
     * @return The document as it is present (or not) in database. If document does not exist in database, an empty DOM
     * document is returned (<b>not</b> <code>null</code>).
     */
    MutableDocument getDatabaseDocument();

    /**
     * @return Document to be used for XML schema validation (doesn't contain MDM internal technical annotations).
     */
    MutableDocument getDatabaseValidationDocument();

    /**
     * @return Document provided by user for save
     */
    MutableDocument getUserDocument();

    /**
     * Changes document provided by user
     *
     * @param document New user provided document
     */
    void setUserDocument(MutableDocument document);

    /**
     * @return List of actions to be performed to the database
     */
    List<Action> getActions();

    /**
     * Set actions performed by the user.
     *
     * @param actions A list of {@link Action} to be performed.
     */
    void setActions(List<Action> actions);

    /**
     * @return {@link ComplexTypeMetadata} of the entity being saved.
     */
    ComplexTypeMetadata getType();

    /**
     * @return Id of the document being saved.
     */
    String[] getId();

    /**
     * Set the id of the soon-to-be-saved document.
     *
     * @param id Id of the document.
     */
    void setId(String[] id);

    /**
     * @return The data cluster name where the record should be stored.
     */
    String getDataCluster();

    /**
     * @return The data model name where the record entity type is defined.
     */
    String getDataModelName();

    /**
     * @return The revision of the record being saved.
     */
    String getRevisionID();

    void setDatabaseDocument(MutableDocument databaseDocument);

    void setDatabaseValidationDocument(MutableDocument databaseValidationDocument);

    void setRevisionId(String revisionID);

    void setType(ComplexTypeMetadata type);

    boolean hasMetAutoIncrement();

    void setHasMetAutoIncrement(boolean hasMetAutoIncrement);

    void setTaskId(String taskId);

    String getTaskId();

    /**
     * @return <code>true</code> if all values in collections of {@link #getDatabaseDocument()} must be preserved. In
     * case {@link #getUserDocument()} provides different values for the collection, these values won't replace the
     * existing ones but be added at the end of the collection.
     */
    boolean preserveOldCollectionValues();

    MutableDocument getUpdateReportDocument();

    void setUpdateReportDocument(MutableDocument updateReportDocument);

    UserAction getUserAction();

    void setUserAction(UserAction userAction);

    String getPartialUpdatePivot();

    String getPartialUpdateKey();

    int getPartialUpdateIndex();

    Map<String, String> getAutoIncrementFieldMap();
}
