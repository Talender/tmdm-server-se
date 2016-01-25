// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.core.schema.manage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.talend.mdm.commmon.util.datamodel.management.DataModelBean;
import org.talend.mdm.commmon.util.datamodel.management.DataModelID;

/**
 * DOC HSHU class global comment. Detailled comment
 */
public class DataModelCorePool {

    private static final int DEFAULT_POOL_SIZE = 1000;

    private static final float hashTableLoadFactor = 0.75f;

    private LinkedHashMap<DataModelID, DataModelBean> map;

    /** unique instance */
    private static DataModelCorePool sInstance = null;

    private int poolSize = 0;

    /**
     * Private constuctor
     */
    private DataModelCorePool() {
        super();
        poolSize = DEFAULT_POOL_SIZE;
        int hashTableCapacity = (int) Math.ceil(poolSize / hashTableLoadFactor) + 1;
        map = new LinkedHashMap<DataModelID, DataModelBean>(hashTableCapacity, hashTableLoadFactor, true) {

            // (an anonymous inner class)
            private static final long serialVersionUID = 1;

            @Override
            protected boolean removeEldestEntry(Map.Entry<DataModelID, DataModelBean> eldest) {
                return size() > DataModelCorePool.this.poolSize;
            }
        };

    }

    /**
     * Get the unique instance of this class.
     */
    public static synchronized DataModelCorePool getUniqueInstance() {

        if (sInstance == null) {
            sInstance = new DataModelCorePool();
        }

        return sInstance;

    }

    public synchronized DataModelBean get(DataModelID dataModelID) {
        return map.get(dataModelID);
    }

    public synchronized Collection<Map.Entry<DataModelID, DataModelBean>> getAll() {
        return new ArrayList<Map.Entry<DataModelID, DataModelBean>>(map.entrySet());
    }

    public synchronized void put(DataModelID dataModelID, DataModelBean dataModelBean) {
        map.put(dataModelID, dataModelBean);
    }

    public synchronized DataModelBean remove(DataModelID dataModelID) {
        return map.remove(dataModelID);
    }

    public synchronized void clear() {
        map.clear();
    }

    public synchronized int allEntries() {
        return poolSize;
    }

    public synchronized int usedEntries() {
        return map.size();
    }

}
