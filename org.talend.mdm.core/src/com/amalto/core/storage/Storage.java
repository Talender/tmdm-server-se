/*
 * Copyright (C) 2006-2015 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package com.amalto.core.storage;

import com.amalto.core.query.user.Expression;
import com.amalto.core.storage.datasource.DataSource;
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.storage.transaction.StorageTransaction;
import org.talend.mdm.commmon.metadata.MetadataRepository;

import java.util.Set;

/**
 *
 */
public interface Storage {

    /**
     * Indicates storage supports transactions.
     */
    byte CAP_TRANSACTION = 1;

    /**
     * Indicates storage supports full text queries.
     */
    byte CAP_FULL_TEXT = 2;

    /**
     * Indicate storage supports referential integrity.
     */
    byte CAP_INTEGRITY = 4;

    /**
     * Name of the column where MDM timestamp is stored.
     */
    String METADATA_TIMESTAMP = "x_talend_timestamp"; //$NON-NLS-1$

    /**
     * Name of the column where MDM task id is stored.
     */
    String METADATA_TASK_ID = "x_talend_task_id"; //$NON-NLS-1$

    /**
     * Name of the column where MDM source is stored (for STAGING databases only).
     */
    String METADATA_STAGING_SOURCE = "x_talend_staging_source"; //$NON-NLS-1$

    /**
     * Name of the column where MDM status (validated...) is stored (for STAGING databases only).
     *
     * @see com.amalto.core.storage.task.StagingConstants
     */
    String METADATA_STAGING_STATUS = "x_talend_staging_status"; //$NON-NLS-1$

    /**
     * Name of the column where last MDM validation error is stored (for STAGING databases only).
     */
    String METADATA_STAGING_ERROR = "x_talend_staging_error"; //$NON-NLS-1$

    /**
     * Name of the column where a block key can be stored (for STAGING databases only).
     */
    String METADATA_STAGING_BLOCK_KEY = "x_talend_staging_blockkey"; //$NON-NLS-1$

    /**
     * Name of type for explicit projection (i.e. selection of a field within MDM entity). Declared fields in this type
     * varies from one query to another (if selected fields in query changed).
     */
    String PROJECTION_TYPE = "$ExplicitProjection$"; //$NON-NLS-1$

    /**
     * @return A storage implementation that does not perform any on-the-fly data modification.
     */
    Storage asInternal();

    /**
     * @return A bit mask of capabilities for this storage implementation.
     * @see #CAP_TRANSACTION
     * @see #CAP_INTEGRITY
     * @see #CAP_FULL_TEXT
     */
    int getCapabilities();

    /**
     * @return An implementation of {@link StorageTransaction} ready for usage. Implementations are always expected
     * to return a new transaction instance.
     */
    StorageTransaction newStorageTransaction();

    /**
     * Early initialization (i.e. might create pools): performs all actions that do not need to know what kind of types
     * this storage should take care of (usually stateless components).
     *
     * @param dataSource Represents the underlying data storage (e.g. RDBMS, XML DB...)
     * @see com.amalto.core.server.Server#getDataSource(String, String, StorageType)
     */
    void init(DataSource dataSource);

    /**
     * Prepare storage to handle types located in {@link MetadataRepository}.
     *
     * @param repository           A initialized {@link org.talend.mdm.commmon.metadata.MetadataRepository} instance.
     * @param optimizedExpressions A {@link java.util.Set} of {@link Expression} that need to be optimized. It is up to the
     *                             implementation to decide whether this information should be used or not. Callers of this method expects
     *                             implementation to take all necessary actions to allow quick execution on the queries in <code>optimizedExpressions</code>.
     * @param force                <code>true</code> will force the storage to prepare event if
     *                             {@link #prepare(org.talend.mdm.commmon.metadata.MetadataRepository, boolean)} has already been called.
     *                             <code>false</code> will be a "no op" operation if storage is already prepared.
     * @param dropExistingData     if <code>true</code>, storage preparation will drop all data that may previously exist.
     *                             Use this parameter with caution since recovery is not supported.
     * @see MetadataRepository#load(java.io.InputStream)
     * @see #prepare(MetadataRepository, boolean)
     */
    void prepare(MetadataRepository repository, Set<Expression> optimizedExpressions, boolean force, boolean dropExistingData);

    /**
     * Prepare storage to handle types located in {@link MetadataRepository}.
     *
     * @param repository       A initialized {@link MetadataRepository} instance.
     * @param dropExistingData if <code>true</code>, storage preparation will drop all data that may previously exist.
     *                         Use this parameter with caution since recovery is not supported.
     * @see MetadataRepository#load(java.io.InputStream)
     */
    void prepare(MetadataRepository repository, boolean dropExistingData);

    /**
     * @return The {@link MetadataRepository} used by this storage.
     * @throws IllegalStateException If the storage has not been prepared.
     * @see #prepare(MetadataRepository, boolean)
     */
    MetadataRepository getMetadataRepository();

    /**
     * Returns all records that match the {@link Expression}. The <code>expression</code> should be a valid
     * {@link com.amalto.core.query.user.Select}.
     *
     * @param userQuery A {@link com.amalto.core.query.user.Select} instance.
     * @return A {@link Iterable} instance to navigate through query results. This iterable class also provides ways to
     * get how many records are returned and how many matched query in database.
     * @see com.amalto.core.query.user.UserQueryBuilder
     */
    StorageResults fetch(Expression userQuery);

    /**
     * Updates storage with a new or existing record. Record might already exist, storage implementation (or underlying
     * storage framework) will decide whether this is new record or old one.
     *
     * @param record Record to be created or updated.
     */
    void update(DataRecord record);

    /**
     * Updates storage with new or existing records. Records might already exist, storage implementation (or underlying
     * storage framework) will decide whether this is all new records or old ones.
     *
     * @param records Records to be created or updated.
     */
    void update(Iterable<DataRecord> records);

    /**
     * <p>
     * Deletes all records that match the {@link Expression}. The <code>userQuery</code> should be a valid
     * {@link com.amalto.core.query.user.Select}.
     * </p>
     * <p>
     * Implementations are expected to throw {@link IllegalArgumentException} if <code>userQuery</code> does not match
     * any record.
     * </p>
     *
     * @param userQuery A {@link com.amalto.core.query.user.Select} instance.
     * @throws IllegalArgumentException If <code>userQuery</code> does not match any document in storage.
     * @see com.amalto.core.query.user.UserQueryBuilder
     * @see #delete(com.amalto.core.storage.record.DataRecord)
     */
    void delete(Expression userQuery);

    /**
     * Deletes a {@link DataRecord} record using the record instance (no need for a query as in
     * {@link #delete(com.amalto.core.query.user.Expression)}.
     * @param record The record to be deleted.
     * @see #delete(com.amalto.core.query.user.Expression)
     */
    void delete(DataRecord record);

    /**
     * Performs shutdown actions and internal objects clean up. Calling this method is <b>same</b> as calling
     * {@link #close(boolean)} with <b><code>false</code></b>.
     */
    void close();

    /**
     * Performs shutdown actions and internal objects clean up. If <code>dropExistingData</code> is true, data managed
     * by this Storage will be deleted.
     */
    void close(boolean dropExistingData);

    /**
     * <p>
     * Starts a transaction for current thread. If a previous call to this method has been made without calling any end
     * of transaction method (e.g. {@link #commit()}), calling this method has no effect.
     * </p>
     *
     * @throws IllegalStateException If a transaction was already started for the current thread.
     * @see #commit()
     * @see #rollback()
     * @see #end()
     */
    void begin();

    /**
     * Commit changes done during transaction for current thread. {@link #begin()} must have previously been called.
     * <b>Note</b>: Callers are expected to call {@link #rollback()} if any commit exception occurs.
     */
    void commit();

    /**
     * Rollback changes done during transaction for current thread. {@link #begin()} must have previously been called.
     */
    void rollback();

    /**
     * Free any resource allocated for a transaction. {@link #begin()} must have previously been called.
     */
    void end();

    /**
     * Re-index the whole database for full text indexing. This can be quite a high-cost operation (depending on the
     * database size). This is a blocking operation.
     */
    void reindex();

    /**
     * Returns suggested keywords (words that match result in full text index) for the <code>keyword</code>. Returned
     * results depend on {@link FullTextSuggestion}.
     *
     * @param keyword        A word to be used as input for this method (only one word).
     * @param mode           {@link FullTextSuggestion} suggestion mode.
     * @param suggestionSize Number of suggestions this method should return.
     * @return A {@link Set} of <code>suggestionSize</code> keywords that matches results in full text index.
     */
    Set<String> getFullTextSuggestion(String keyword, FullTextSuggestion mode, int suggestionSize);

    /**
     * @return Name of this storage instance. This is the name used for creation in {@link com.amalto.core.server.StorageAdmin#create(String, String, String, String)}.
     */
    String getName();

    /**
     * @return The {@link DataSource} used by this instance.
     */
    DataSource getDataSource();

    /**
     * @return the {@link StorageType} for this instance.
     */
    StorageType getType();

    enum FullTextSuggestion {
        /**
         * Returns keyword suggestion that start with a given set of characters.
         */
        START,
        /**
         * Returns keyword suggestion that
         */
        ALTERNATE
    }
}
