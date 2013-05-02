// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.journal.client.util;

import junit.framework.TestCase;

import org.talend.mdm.webapp.journal.shared.JournalGridModel;
import org.talend.mdm.webapp.journal.shared.JournalParameters;

/**
 * created by yjli on 2013-4-25
 * Detailled comment
 *
 */
public class JournalSearchUtilTest extends TestCase {
    
    public void testBuildParameter() {
        String action = "current"; //$NON-NLS-1$
        boolean auth = true;
        JournalGridModel gridModel = new JournalGridModel();
        gridModel.setDataContainer("Product-Container"); //$NON-NLS-1$
        gridModel.setDataModel("Product-Model"); //$NON-NLS-1$
        gridModel.setEntity("Product-Entity"); //$NON-NLS-1$
        gridModel.setOperationTime("1366863377513"); //$NON-NLS-1$
        gridModel.setRevisionId("Head"); //$NON-NLS-1$
        gridModel.setIds("356"); //$NON-NLS-1$
        gridModel.setKey("3.5.6"); //$NON-NLS-1$
        
        JournalParameters parameter = JournalSearchUtil.buildParameter(gridModel, action, auth);
        assertEquals(parameter.getDataClusterName(), gridModel.getDataContainer());
        assertEquals(parameter.getDataModelName(), gridModel.getDataModel());
        assertEquals(parameter.getConceptName(), gridModel.getEntity());
        assertEquals(parameter.getRevisionId(), gridModel.getRevisionId());
        assertEquals(parameter.getIds(), gridModel.getIds());
        String[] id = gridModel.getKey().split("\\."); //$NON-NLS-1$
        assertEquals(parameter.getId()[0], id[0]);
        assertEquals(parameter.getId()[1], id[1]);
        assertEquals(parameter.getId()[2], id[2]);
        assertEquals(parameter.getAction(), action);
        assertEquals(parameter.isAuth(), auth);
    }
}