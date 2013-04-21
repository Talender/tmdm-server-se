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
package org.talend.mdm.webapp.journal.client;

import org.talend.mdm.webapp.base.client.exception.ServiceException;
import org.talend.mdm.webapp.journal.shared.JournalGridModel;
import org.talend.mdm.webapp.journal.shared.JournalParameters;
import org.talend.mdm.webapp.journal.shared.JournalSearchCriteria;
import org.talend.mdm.webapp.journal.shared.JournalTreeModel;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("JournalService")
public interface JournalService extends RemoteService {

    PagingLoadResult<JournalGridModel> getJournalList(JournalSearchCriteria criteria, PagingLoadConfig load)
            throws ServiceException;

    JournalTreeModel getDetailTreeModel(String ids) throws ServiceException;

    JournalTreeModel getComparisionTree(JournalParameters parameter, String language) throws ServiceException;

    boolean isEnterpriseVersion();

    boolean restoreRecord(JournalParameters parameter, String language) throws ServiceException;

    boolean checkDCAndDM(String dataContainer, String dataModel);

    String getReportString(int start, int limit, String sort, String field, String language, String entity, String key,
            String source, String operationType, String startDate, String endDate, boolean isBrowseRecord)
            throws ServiceException;

    boolean isAdmin();
}