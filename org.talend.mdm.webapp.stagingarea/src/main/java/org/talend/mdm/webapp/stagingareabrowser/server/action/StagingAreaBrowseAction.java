// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.stagingareabrowser.server.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.talend.mdm.webapp.base.client.exception.ServiceException;
import org.talend.mdm.webapp.base.server.util.CommonUtil;
import org.talend.mdm.webapp.stagingareabrowser.client.StagingAreaBrowseService;
import org.talend.mdm.webapp.stagingareabrowser.client.model.ResultItem;
import org.talend.mdm.webapp.stagingareabrowser.client.model.SearchModel;
import org.talend.mdm.webapp.stagingareabrowser.client.view.SearchView;
import org.w3c.dom.Document;

import com.amalto.webapp.core.bean.Configuration;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSDataModelPK;
import com.amalto.webapp.util.webservices.WSGetBusinessConcepts;
import com.amalto.webapp.util.webservices.WSStringArray;
import com.amalto.webapp.util.webservices.WSStringPredicate;
import com.amalto.webapp.util.webservices.WSWhereAnd;
import com.amalto.webapp.util.webservices.WSWhereCondition;
import com.amalto.webapp.util.webservices.WSWhereItem;
import com.amalto.webapp.util.webservices.WSWhereOperator;
import com.amalto.webapp.util.webservices.WSXPathsSearch;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadResult;

public class StagingAreaBrowseAction implements StagingAreaBrowseService {

    private static final Logger LOG = Logger.getLogger(StagingAreaBrowseAction.class);

    @Override
    public List<BaseModel> getConcepts(String language) throws ServiceException {
        try {
            String model = getCurrentDataModel();
            String[] businessConcept = CommonUtil.getPort()
                    .getBusinessConcepts(new WSGetBusinessConcepts(new WSDataModelPK(model))).getStrings();
            List<BaseModel> conceptModels = new ArrayList<BaseModel>();
            for (String concept : businessConcept) {
                BaseModel conceptModel = new BaseModel();
                conceptModel.set("name", concept); //$NON-NLS-1$
                conceptModel.set("value", concept); //$NON-NLS-1$
                conceptModels.add(conceptModel);
            }
            return conceptModels;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ServiceException(e.getLocalizedMessage());
        }
    }

    private WSWhereItem buildWhereItem(SearchModel searchModel) {

        List<WSWhereItem> whereItems = new ArrayList<WSWhereItem>();

        String key = searchModel.getKey();
        if (key != null && key.trim().length() > 0) {
            WSWhereItem keyWhere = new WSWhereItem();
            keyWhere.setWhereCondition(new WSWhereCondition(searchModel.getEntity() + "/../../i", //$NON-NLS-1$
                    WSWhereOperator.EQUALS, key, WSStringPredicate.NONE, false));
            whereItems.add(keyWhere);
        }

        String source = searchModel.getSource();
        if (source != null && source.trim().length() > 0) {
            WSWhereItem sourceWhere = new WSWhereItem();
            sourceWhere.setWhereCondition(new WSWhereCondition(searchModel.getEntity() + "/$staging_source$", //$NON-NLS-1$
                    WSWhereOperator.CONTAINS, source, WSStringPredicate.NONE, false));
            whereItems.add(sourceWhere);
        }

        String statusCode = searchModel.getStatusCode();
        if (statusCode != null && statusCode.trim().length() > 0) {
            WSWhereItem statusWhere = new WSWhereItem();
            statusWhere.setWhereCondition(new WSWhereCondition(searchModel.getEntity() + "/$staging_status$", //$NON-NLS-1$
                    WSWhereOperator.EQUALS, statusCode, WSStringPredicate.NONE, false));
            whereItems.add(statusWhere);
        }

        Integer state = searchModel.getState();
        if (state != null) {
            WSWhereItem stateWhere = null;
            WSWhereItem stateWhereAnd = null;
            if (state.equals(SearchView.INVALID_RECORDS)) {
                stateWhere = new WSWhereItem();
                stateWhere.setWhereCondition(new WSWhereCondition(searchModel.getEntity() + "/$staging_status$", //$NON-NLS-1$
                        WSWhereOperator.GREATER_THAN_OR_EQUAL, "400", WSStringPredicate.NONE, false)); //$NON-NLS-1$
                stateWhereAnd = new WSWhereItem();
                stateWhereAnd.setWhereCondition(new WSWhereCondition(searchModel.getEntity() + "/$staging_status$", //$NON-NLS-1$
                        WSWhereOperator.LOWER_THAN_OR_EQUAL, "404", WSStringPredicate.NONE, false)); //$NON-NLS-1$
            } else if (state.equals(SearchView.VALID_RECORDS)) {
                stateWhere = new WSWhereItem();
                stateWhere.setWhereCondition(new WSWhereCondition(searchModel.getEntity() + "/$staging_status$", //$NON-NLS-1$
                        WSWhereOperator.GREATER_THAN_OR_EQUAL, "200", WSStringPredicate.NONE, false)); //$NON-NLS-1$
                stateWhereAnd = new WSWhereItem();
                stateWhereAnd.setWhereCondition(new WSWhereCondition(searchModel.getEntity() + "/$staging_status$", //$NON-NLS-1$
                        WSWhereOperator.LOWER_THAN_OR_EQUAL, "205", WSStringPredicate.NONE, false)); //$NON-NLS-1$
            }
            if (stateWhere != null) {
                whereItems.add(stateWhere);
            }
            if (stateWhereAnd != null) {
                whereItems.add(stateWhereAnd);
            }
        }

        Date startDate = searchModel.getStartDate();
        if (startDate != null) {
            WSWhereItem startDateWhere = new WSWhereItem();
            startDateWhere.setWhereCondition(new WSWhereCondition(searchModel.getEntity() + "/../../t", //$NON-NLS-1$
                    WSWhereOperator.GREATER_THAN_OR_EQUAL, Long.toString(startDate.getTime()), WSStringPredicate.NONE, false));
            whereItems.add(startDateWhere);
        }
        Date endDate = searchModel.getEndDate();
        if (endDate != null) {
            WSWhereItem endDateWhere = new WSWhereItem();
            endDateWhere.setWhereCondition(new WSWhereCondition(searchModel.getEntity() + "/../../t", //$NON-NLS-1$
                    WSWhereOperator.LOWER_THAN_OR_EQUAL, Long.toString(endDate.getTime()), WSStringPredicate.NONE, false));
            whereItems.add(endDateWhere);
        }

        WSWhereAnd whereAnd = new WSWhereAnd();
        whereAnd.setWhereItems(whereItems.toArray(new WSWhereItem[] {}));
        WSWhereItem whereItem = new WSWhereItem();
        whereItem.setWhereAnd(whereAnd);
        return whereItem;
    }

    @Override
    public PagingLoadResult<ResultItem> searchStaging(SearchModel searchModel) throws ServiceException {
        int totalSize = 0;
        List<ResultItem> items = new ArrayList<ResultItem>();
        try {
            List<String> viewablePathList = new ArrayList<String>();
            viewablePathList.add(searchModel.getEntity() + "/../../i"); //$NON-NLS-1$
            viewablePathList.add(searchModel.getEntity() + "/../../t"); //$NON-NLS-1$
            viewablePathList.add(searchModel.getEntity() + "/$staging_status$"); //$NON-NLS-1$
            viewablePathList.add(searchModel.getEntity() + "/$staging_error$"); //$NON-NLS-1$
            viewablePathList.add(searchModel.getEntity() + "/$staging_source$"); //$NON-NLS-1$
            viewablePathList.add(searchModel.getEntity() + "/../../taskId");//$NON-NLS-1$
            WSDataClusterPK wsDataClusterPK = new WSDataClusterPK(getCurrentDataCluster());
            WSStringArray viewablePaths = new WSStringArray(viewablePathList.toArray(new String[viewablePathList.size()]));

            WSWhereItem whereItem = buildWhereItem(searchModel);
            String sortXpath = getSortFieldXpath(searchModel.getEntity(), searchModel.getSortField());
            WSXPathsSearch wsXPathsSearch = new WSXPathsSearch(wsDataClusterPK, null, viewablePaths, whereItem, -1,
                    searchModel.getOffset(), searchModel.getLimit(), sortXpath, searchModel.getSortDir(), true);

            String[] results = CommonUtil.getPort().xPathsSearch(wsXPathsSearch).getStrings();
            for (int i = 0; i < results.length; i++) {
                if (i == 0) {
                    try {
                        // Qizx doesn't wrap the count in a XML element, so try to parse it
                        totalSize = Integer.parseInt(results[i]);
                    } catch (NumberFormatException e) {
                        totalSize = Integer.parseInt(com.amalto.webapp.core.util.Util.parse(results[i]).getDocumentElement()
                                .getTextContent());
                    }
                    continue;
                }

                Document doc = Util.parse(results[i]);
                String[] key = Util.getTextNodes(doc, "/result/i"); //$NON-NLS-1$
                String dateTime = Util.getFirstTextNode(doc, "/result/timestamp"); //$NON-NLS-1$
                String source = Util.getFirstTextNode(doc, "/result/staging_source"); //$NON-NLS-1$
                String status = Util.getFirstTextNode(doc, "/result/staging_status"); //$NON-NLS-1$
                String error = Util.getFirstTextNode(doc, "/result/staging_error"); //$NON-NLS-1$
                String taskId = Util.getFirstTextNode(doc, "/result/taskId"); //$NON-NLS-1$
                if (taskId == null || taskId.equals("null")) {
                    taskId = "";//$NON-NLS-1$//$NON-NLS-2$
                }
                ResultItem item = new ResultItem();
                item.setKey(Util.joinStrings(key, ".")); //$NON-NLS-1$
                item.setEntity(searchModel.getEntity());
                if (dateTime != null) {
                    item.setDateTime(new Date(Long.parseLong(dateTime)));
                }
                item.setSource(source);
                if (status != null) {
                    item.setStatus(Integer.valueOf(status));
                }
                item.setError(error);
                item.setGroup(taskId);// Is it ok to use taskId to classify group?
                items.add(item);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ServiceException(e.getLocalizedMessage());
        }

        PagingLoadResult<ResultItem> result = new BasePagingLoadResult<ResultItem>(items, searchModel.getOffset(), totalSize);
        return result;
    }

    private String getSortFieldXpath(String entity, String field) {
        if ("key".equals(field)) { //$NON-NLS-1$
            return entity + "/../../i"; //$NON-NLS-1$
        } else if ("dateTime".equals(field)) { //$NON-NLS-1$
            return entity + "/../../t"; //$NON-NLS-1$
        } else if ("source".equals(field)) { //$NON-NLS-1$
            return entity + "/$staging_source$"; //$NON-NLS-1$
        } else if ("group".equals(field)) { //$NON-NLS-1$
            return entity + "/../../taskId"; //$NON-NLS-1$
        } else if ("status".equals(field)) { //$NON-NLS-1$
            return entity + "/$staging_status$"; //$NON-NLS-1$
        } else if ("error".equals(field)) { //$NON-NLS-1$
            return entity + "/$staging_error$"; //$NON-NLS-1$
        } else {
            return field;
        }
    }

    public String getCurrentDataModel() throws ServiceException {
        try {
            Configuration config = Configuration.getConfiguration();
            return config.getModel();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ServiceException(e.getLocalizedMessage());
        }
    }

    public String getCurrentDataCluster() throws ServiceException {
        try {
            Configuration config = Configuration.getConfiguration();
            return config.getCluster() + "#STAGING"; //$NON-NLS-1$
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ServiceException(e.getLocalizedMessage());
        }
    }
}