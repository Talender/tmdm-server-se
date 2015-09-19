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

package com.amalto.core.load.context;

import org.apache.log4j.Logger;
import org.talend.mdm.commmon.util.webapp.XSystemObjects;

import com.amalto.core.server.XmlServer;
import com.amalto.core.util.AutoIncrementGenerator;
import com.amalto.core.util.LocalUser;
import com.amalto.core.util.XtentisException;

public class AutoIncrementIdGenerator implements AutoIdGenerator {

    private static final Logger LOGGER = Logger.getLogger(AutoIncrementIdGenerator.class);

    public String generateId(String dataClusterName, String conceptName, String keyElementName) {
        try {
            String universe = LocalUser.getLocalUser().getUniverse().getName();
            // Include keyElementName in concept name (this is rather important).
            long id = AutoIncrementGenerator.generateNum(universe,
                    dataClusterName,
                    conceptName + "." + keyElementName); // $NON-NLS-1$
            return String.valueOf(id);
        } catch (XtentisException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveState(XmlServer server) {
        try {
            server.start(XSystemObjects.DC_CONF.getName());
            AutoIncrementGenerator.saveToDB();
            server.commit(XSystemObjects.DC_CONF.getName());
        } catch (XtentisException e) {
            try {
                server.rollback(XSystemObjects.DC_CONF.getName()); 
            } catch (XtentisException ex) { 
                LOGGER.error("Rollback exception occurred", ex); //$NON-NLS-1$
            }
            throw new RuntimeException("Could not save auto increment count.", e); //$NON-NLS-1$
        }
    }
}