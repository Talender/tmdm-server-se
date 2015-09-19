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

package com.amalto.core.save.context;

import com.amalto.core.save.DocumentSaverContext;
import com.amalto.core.save.SaverSession;
import org.apache.commons.lang.StringUtils;

public class AutoCommit implements DocumentSaver {

    private final DocumentSaver saver;

    private String[] id;

    private String typeName;

    public AutoCommit(DocumentSaver saver) {
        this.saver = saver;
    }

    @Override
    public void save(SaverSession session, DocumentSaverContext context) {
        saver.save(session, context);
        try {
            typeName = context.getUserDocument().getType().getName();
            id = context.getId();
            session.end();
            session.begin(context.getDataCluster());
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred during auto commit phase.", e); //$NON-NLS-1$
        }
    }

    @Override
    public String[] getSavedId() {
        return id;
    }

    @Override
    public String getSavedConceptName() {
        return typeName;
    }

    @Override
    public String getBeforeSavingMessage() {
        return StringUtils.EMPTY;
    }
}
