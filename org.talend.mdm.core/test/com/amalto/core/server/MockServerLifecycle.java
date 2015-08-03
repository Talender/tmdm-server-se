/*
 * Copyright (C) 2006-2015 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package com.amalto.core.server;

import org.talend.mdm.commmon.util.core.MDMConfiguration;

import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageType;
import com.amalto.core.storage.datasource.DataSourceFactory;
import com.amalto.core.storage.hibernate.HibernateStorage;

public class MockServerLifecycle implements ServerLifecycle {

    @Override
    public Server createServer() {
        MDMConfiguration.getConfiguration().setProperty(DataSourceFactory.DB_DATASOURCES, MockServer.getDatasourcesFilePath());
        return new MockServer();
    }

    @Override
    public void destroyServer(Server server) {
        // nothing to do
    }

    @Override
    public StorageAdmin createStorageAdmin() {
        return new StorageAdminImpl();
    }

    @Override
    public void destroyStorageAdmin(StorageAdmin storageAdmin) {
        storageAdmin.close();
    }

    @Override
    public MetadataRepositoryAdmin createMetadataRepositoryAdmin() {
        return new MockMetadataRepositoryAdmin();
    }

    @Override
    public void destroyMetadataRepositoryAdmin(MetadataRepositoryAdmin metadataRepositoryAdmin) {
        metadataRepositoryAdmin.close();
    }

    @Override
    public Storage createStorage(String storageName, String dataSourceName, StorageType storageType) {
        return new HibernateStorage(storageName, storageType);
    }

    @Override
    public void destroyStorage(Storage storage, boolean dropExistingData) {
        storage.close(true);
    }

}
