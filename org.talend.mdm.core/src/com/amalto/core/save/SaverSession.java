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

package com.amalto.core.save;

import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.metadata.MetadataRepository;
import com.amalto.core.save.context.DefaultSaverSource;
import com.amalto.core.save.context.SaverContextFactory;
import com.amalto.core.save.context.SaverSource;
import org.talend.mdm.commmon.util.webapp.XSystemObjects;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class SaverSession {

    private static final String AUTO_INCREMENT_TYPE_NAME = "AutoIncrement"; //$NON-NLS-1$

    private static final Map<String, SaverSource> saverSourcePerUser = new HashMap<String, SaverSource>();

    private final SaverContextFactory contextFactory = new SaverContextFactory();

    private final Map<String, Set<ItemPOJO>> itemsPerDataCluster = new HashMap<String, Set<ItemPOJO>>();

    private final SaverSource dataSource;

    private static SaverSource defaultSaverSource;

    private static DefaultCommitter defaultCommitter;

    private boolean hasMetAutoIncrement = false;

    private SaverSession(SaverSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * @param saverSource A custom implementation of {@link SaverSource}.
     * @return A {@link SaverSession} with a custom {@link SaverSource} implementation.
     */
    public static SaverSession newSession(SaverSource saverSource) {
        return new SaverSession(saverSource);
    }

    /**
     * @return A {@link SaverSession} with default user.
     */
    public static synchronized SaverSession newSession() {
        if (defaultSaverSource == null) {
            defaultSaverSource = new DefaultSaverSource();
        }
        return new SaverSession(defaultSaverSource);
    }

    /**
     * @param userName A MDM user name
     * @return A {@link SaverSession} with the user name passed as parameter.
     */
    public static SaverSession newUserSession(String userName) {
        SaverSource saverSource = saverSourcePerUser.get(userName);
        if (saverSource == null) {
            saverSource = new DefaultSaverSource(userName);
            saverSourcePerUser.put(userName, saverSource);
        }
        SaverSource dataSource = saverSource;
        return new SaverSession(dataSource);
    }

    public SaverContextFactory getContextFactory() {
        return contextFactory;
    }

    /**
     * Start a transaction for this session on a given data cluster.
     *
     * @param dataCluster The data cluster where a transaction should be started.
     */
    public void begin(String dataCluster) {
        begin(dataCluster, getDefaultCommitter());
    }

    private DefaultCommitter getDefaultCommitter() {
        if (defaultCommitter == null) {
            defaultCommitter = new DefaultCommitter();
        }
        return defaultCommitter;
    }

    /**
     * Start a transaction for this session on a given data cluster.
     *
     * @param dataCluster The data cluster where a transaction should be started.
     * @param committer   A {@link Committer} committer for interaction between save session and underlying storage.
     */
    public void begin(String dataCluster, Committer committer) {
        synchronized (itemsPerDataCluster) {
            committer.begin(dataCluster);
            if (!itemsPerDataCluster.containsKey(dataCluster)) {
                itemsPerDataCluster.put(dataCluster, new HashSet<ItemPOJO>());
            }
        }
    }

    /**
     * End this session (means commit on all data clusters where a transaction was started).
     */
    public void end() {
        end(getDefaultCommitter());
    }

    /**
     * End this session (means commit on all data clusters where a transaction was started).
     *
     * @param committer A {@link Committer} committer to use when committing transactions on underlying storage.
     */
    public void end(Committer committer) {
        synchronized (itemsPerDataCluster) {
            SaverSource saverSource = getSaverSource();
            boolean needResetAutoIncrement = false;
            MetadataRepository repository = null;
            ComplexTypeMetadata type = null;
            for (Map.Entry<String, Set<ItemPOJO>> currentTransaction : itemsPerDataCluster.entrySet()) {
                String dataCluster = currentTransaction.getKey();
                begin(dataCluster, committer);
                Iterator<ItemPOJO> iterator = currentTransaction.getValue().iterator();
                while (iterator.hasNext()) {
                    ItemPOJO currentItemToCommit = iterator.next();
                    if (repository == null || type == null) {
                        String dataModelName = currentItemToCommit.getDataModelName();
                        if (dataModelName != null) { // TODO Every item should have a data mode name (but UpdateReport doesn't)
                            repository = saverSource.getMetadataRepository(dataModelName);
                            type = repository.getComplexType(currentItemToCommit.getConceptName());
                        }
                    }
                    if (!needResetAutoIncrement) {
                        needResetAutoIncrement = isAutoIncrementItem(currentItemToCommit);
                    }
                    // Don't do clean up in case of exception here: rollback (abort()) takes care of the clean up.
                    committer.save(currentItemToCommit, type, currentItemToCommit.getDataModelRevision());
                    // Keep update reports for routeItem (see below).
                    if (!XSystemObjects.DC_UPDATE_PREPORT.getName().equals(dataCluster)) {
                        iterator.remove();
                    }
                }
                committer.commit(dataCluster);
            }
            // If any change was made to data cluster "UpdateReport", route committed update reports.
            Set<ItemPOJO> updateReport = itemsPerDataCluster.get(XSystemObjects.DC_UPDATE_PREPORT.getName());
            if (updateReport != null) {
                Iterator<ItemPOJO> iterator = updateReport.iterator();
                while (iterator.hasNext()) {
                    ItemPOJO updateReportPOJO = iterator.next();
                    saverSource.routeItem(updateReportPOJO.getDataClusterPOJOPK().getUniqueId(),
                            updateReportPOJO.getConceptName(),
                            updateReportPOJO.getItemIds());
                    iterator.remove();
                }
            }
            // reset the AutoIncrement
            if (needResetAutoIncrement) {
                saverSource.initAutoIncrement();
            }
            // Save current state of autoincrement when save is completed.
            if (hasMetAutoIncrement) {
                // TMDM-3964 : Auto-Increment Id can not be saved immediately to DB
                String dataCluster = XSystemObjects.DC_CONF.getName();
                committer.begin(dataCluster);
                try {
                    saverSource.saveAutoIncrement();
                    committer.commit(dataCluster);
                } catch (Exception e) {
                    committer.rollback(dataCluster);
                    throw new RuntimeException("Could not save auto increment counter state.", e);
                }
            }
        }
    }

    /**
     * To check whether this item's concept model is "AutoIncrement" or not
     *
     * @param item The item to be checked.
     * @return <code>true</code> if item is an AutoIncrement document, <code>false</code> otherwise.
     */
    private static boolean isAutoIncrementItem(ItemPOJO item) {
        if (item == null || item.getDataModelName() == null || item.getConceptName() == null) {
            return false;
        } else if (item.getDataModelName().equals(XSystemObjects.DC_CONF.getName())
                && item.getConceptName().equals(AUTO_INCREMENT_TYPE_NAME)) {
            return true;
        }
        return false;
    }

    /**
     * Adds a new record to be saved in this session.
     *
     * @param dataCluster         Data cluster where the record should be saved.
     * @param itemToSave          The item to save.
     * @param hasMetAutoIncrement <code>true</code> if AUTO_INCREMENT type has been met during save of <code>item</code>,
     *                            <code>false</code> otherwise.
     */
    public void save(String dataCluster, ItemPOJO itemToSave, boolean hasMetAutoIncrement) {
        synchronized (itemsPerDataCluster) {
            if (!this.hasMetAutoIncrement) {
                this.hasMetAutoIncrement = hasMetAutoIncrement;
            }
            Set<ItemPOJO> itemsToSave = itemsPerDataCluster.get(dataCluster);
            if (itemsToSave == null) {
                itemsToSave = new HashSet<ItemPOJO>();
                itemsPerDataCluster.put(dataCluster, itemsToSave);
            }
            itemsToSave.add(itemToSave);
        }
    }

    /**
     * @return {@link SaverSource} to interact with MDM server.
     */
    public SaverSource getSaverSource() {
        return dataSource;
    }

    /**
     * Aborts current transaction (means rollback on all data clusters where a transaction was started).
     */
    public void abort() {
        abort(getDefaultCommitter());
    }

    /**
     * Aborts current transaction (means rollback on all data clusters where a transaction was started).
     *
     * @param committer A {@link Committer} committer for interaction between save session and underlying storage.
     */
    public void abort(Committer committer) {
        synchronized (itemsPerDataCluster) {
            for (Map.Entry<String, Set<ItemPOJO>> currentTransaction : itemsPerDataCluster.entrySet()) {
                String dataCluster = currentTransaction.getKey();
                committer.rollback(dataCluster);
            }
            itemsPerDataCluster.clear();
        }
    }

    /**
     * Invalidate any type cache for the data model.
     *
     * @param dataModelName A data model name.
     */
    public void invalidateTypeCache(String dataModelName) {
        dataSource.invalidateTypeCache(dataModelName);
    }

    public interface Committer {
        /**
         * Begin a transaction on a data cluster
         *
         * @param dataCluster A data cluster name.
         */
        void begin(String dataCluster);

        /**
         * Commit a transaction on a data cluster
         *
         * @param dataCluster A data cluster name
         */
        void commit(String dataCluster);

        /**
         * Saves a MDM record for a given revision.
         *
         * @param item       The item to save.
         * @param type       Type of the record to save. Parameter may be null if type is unknown (system type).
         * @param revisionId A revision id.
         */
        void save(ItemPOJO item, ComplexTypeMetadata type, String revisionId);

        /**
         * Rollbacks changes done on a data cluster.
         *
         * @param dataCluster Data cluster name.
         */
        void rollback(String dataCluster);
    }

}
