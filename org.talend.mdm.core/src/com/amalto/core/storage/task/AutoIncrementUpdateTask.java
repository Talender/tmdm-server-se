// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package com.amalto.core.storage.task;

import static com.amalto.core.query.user.UserQueryBuilder.eq;
import static com.amalto.core.query.user.UserQueryBuilder.from;

import java.util.*;

import com.amalto.core.save.generator.AutoIncrementGenerator;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.FieldMetadata;
import org.talend.mdm.commmon.metadata.MetadataRepository;

import com.amalto.core.query.user.Condition;
import com.amalto.core.query.user.UserQueryBuilder;
import com.amalto.core.query.user.UserQueryHelper;
import com.amalto.core.server.Server;
import com.amalto.core.server.ServerContext;
import com.amalto.core.server.StorageAdmin;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageResults;
import com.amalto.core.storage.StorageType;
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.storage.transaction.Transaction;
import com.amalto.core.storage.transaction.TransactionManager;

public class AutoIncrementUpdateTask implements Task {

    private final Storage origin;

    private final Storage destination;

    private final ComplexTypeMetadata type;

    private final Map<String, Integer> values = new HashMap<String, Integer>();

    private String id = UUID.randomUUID().toString();

    private boolean hasFinished;

    private boolean hasFailed;

    private Storage system;

    private FieldMetadata entryField;

    private FieldMetadata valueField;

    private ComplexTypeMetadata autoIncrementType;

    private FieldMetadata keyField;
    private DataRecord autoIncrementRecord;

    public AutoIncrementUpdateTask(Storage origin, Storage destination, ComplexTypeMetadata type) {
        this.origin = origin;
        this.destination = destination;
        this.type = type;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public int getRecordCount() {
        return 1;
    }

    @Override
    public int getErrorCount() {
        return 0;
    }

    @Override
    public int getProcessedRecords() {
        return 0;
    }

    @Override
    public double getPerformance() {
        return 0;
    }

    @Override
    public void cancel() {
    }

    @Override
    public void waitForCompletion() throws InterruptedException {
    }

    @Override
    public long getStartDate() {
        return 0;
    }

    @Override
    public boolean hasFinished() {
        return hasFinished;
    }

    @Override
    public boolean hasFailed() {
        return hasFailed;
    }

    @Override
    public Condition getDefaultFilter() {
        return UserQueryHelper.TRUE;
    }

    @Override
    public void run() {
        Server server = ServerContext.INSTANCE.get();
        // Create or get current transaction
        TransactionManager manager = server.getTransactionManager();
        Transaction previousTransaction = null;
        if (manager.hasTransaction()) {
            previousTransaction = manager.currentTransaction(); // Remember existing transaction (re-set at the end).
        }
        // Get System storage
        system = server.getStorageAdmin().get(StorageAdmin.SYSTEM_STORAGE, StorageType.SYSTEM, null);
        MetadataRepository repository = system.getMetadataRepository();
        autoIncrementType = repository.getComplexType("AutoIncrement");
        entryField = autoIncrementType.getField("entry");
        ComplexTypeMetadata entryType = (ComplexTypeMetadata) entryField.getType();
        keyField = entryType.getField("key");
        valueField = entryType.getField("value");
        // Query and update auto increment value using custom lock strategy
        Transaction transaction = manager.create(Transaction.Lifetime.LONG);
        manager.associate(transaction);
        try {
            // Set lock strategy
            transaction.setLockStrategy(Transaction.LockStrategy.LOCK_FOR_UPDATE);
            system.begin(); // Implicitly adds system in current transaction
            // Get AutoIncrement record
            UserQueryBuilder qb = from(autoIncrementType).where(eq(autoIncrementType.getField("id"), "AutoIncrement")) //$NON-NLS-1 //$NON-NLS-2
                    .limit(1).forUpdate();
            StorageResults autoIncrementRecordResults = system.fetch(qb.getSelect());
            Iterator<DataRecord> iterator = autoIncrementRecordResults.iterator();
            while (iterator.hasNext()) {
                autoIncrementRecord = iterator.next();
                if (iterator.hasNext()) {
                    throw new IllegalArgumentException("Expected only 1 auto increment to be returned.");
                }
            }
            // Build auto increment values
            buildAutoIncrementValues(origin);
            buildAutoIncrementValues(destination);
            // Update auto increment with computed values
            for (Map.Entry<String, Integer> autoIncrementValues : values.entrySet()) {
                List<DataRecord> entries = (List<DataRecord>) autoIncrementRecord.get(entryField);
                if (entries != null) {
                    for (DataRecord entry : entries) { // Find entry for type in database object
                        if (autoIncrementValues.getKey().equals(String.valueOf(entry.get(keyField)))) {
                            entry.set(valueField, autoIncrementValues.getValue());
                        }
                    }
                }
            }
            system.update(autoIncrementRecord);
            // Re-init for in-memory auto increment generator (storage based auto increment should be no op).
            AutoIncrementGenerator.get().init();
            // Done, commit changes
            transaction.commit();
            hasFailed = false;
        } catch (Exception e) {
            hasFailed = true;
            transaction.rollback();
            throw new RuntimeException("Unable to get auto increment value.", e);
        } finally {
            if (previousTransaction != null) {
                manager.associate(previousTransaction); // Restore previous transaction (if any).
            }
            hasFinished = true;
        }
    }

    private void buildAutoIncrementValues(Storage storage) {
        // Build max auto increment values
        String storageName = storage.getName();
        if (storage.getType() == StorageType.STAGING) {
            storageName += StorageAdmin.STAGING_SUFFIX;
        }
        String destinationName = destination.getName();
        if (destination.getType() == StorageType.STAGING) {
            destinationName += StorageAdmin.STAGING_SUFFIX;
        }
        for (FieldMetadata typeKeyField : type.getKeyFields()) {
            String destinationStorageKey = destinationName + '.' + type.getName() + '.' + typeKeyField.getName();
            String key = storageName + '.' + type.getName() + '.' + typeKeyField.getName();
            List<DataRecord> entries = (List<DataRecord>) autoIncrementRecord.get(entryField);
            if (entries != null) {
                for (DataRecord entry : entries) { // Find entry for type in database object
                    if (key.equals(String.valueOf(entry.get(keyField)))) {
                        Integer integer = (Integer) entry.get(valueField);
                        if (values.containsKey(destinationStorageKey)) {
                            values.put(destinationStorageKey, Math.max(values.get(destinationStorageKey), integer));
                        } else {
                            values.put(destinationStorageKey, integer);
                        }
                    }
                }
            }
        }
    }
}
