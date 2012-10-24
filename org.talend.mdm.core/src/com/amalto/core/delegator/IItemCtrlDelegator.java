package com.amalto.core.delegator;

import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.ejb.ItemPOJOPK;
import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.ejb.local.XmlServerSLWrapperLocal;
import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.metadata.FieldMetadata;
import com.amalto.core.metadata.MetadataRepository;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJO;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.objects.role.ejb.RolePOJO;
import com.amalto.core.objects.role.ejb.RolePOJOPK;
import com.amalto.core.objects.universe.ejb.UniversePOJO;
import com.amalto.core.objects.view.ejb.ViewPOJO;
import com.amalto.core.objects.view.ejb.ViewPOJOPK;
import com.amalto.core.query.user.OrderBy;
import com.amalto.core.query.user.UserQueryBuilder;
import com.amalto.core.query.user.UserQueryHelper;
import com.amalto.core.server.Server;
import com.amalto.core.server.ServerContext;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageResults;
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.storage.record.DataRecordWriter;
import com.amalto.core.storage.record.DataRecordXmlWriter;
import com.amalto.core.storage.record.ViewSearchResultsWriter;
import com.amalto.core.util.*;
import com.amalto.xmlserver.interfaces.IWhereItem;
import com.amalto.xmlserver.interfaces.WhereAnd;
import com.amalto.xmlserver.interfaces.XmlServerException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.talend.mdm.commmon.util.core.MDMConfiguration;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

public abstract class IItemCtrlDelegator implements IBeanDelegator,
        IItemCtrlDelegatorService {
    Logger logger = Logger.getLogger(IItemCtrlDelegator.class);

    // methods from ItemCtrl2Bean
    public ArrayList<String> getItemsPivotIndex(String clusterName,
                                                String mainPivotName,
                                                LinkedHashMap<String, String[]> pivotWithKeys, String[] indexPaths,
                                                IWhereItem whereItem, String[] pivotDirections,
                                                String[] indexDirections, int start, int limit)
            throws XtentisException {
        try {

            // validate parameters
            if (pivotWithKeys.size() == 0) {
                String err = "The Map of pivots must contain at least one element";
                logger.error(err);
                throw new XtentisException(err);
            }

            if (indexPaths.length == 0) {
                String err = "The Array of Index Paths must contain at least one element";
                logger.error(err);
                throw new XtentisException(err);
            }

            // get the universe and revision ID
            ILocalUser localuser = getLocalUser();
            UniversePOJO universe = localuser.getUniverse();
            if (universe == null) {
                String err = "ERROR: no Universe set for user '"
                        + localuser.getUsername() + "'";
                logger.error(err);
                throw new XtentisException(err);
            }

            ViewPOJOPK viewPOJOPK = new ViewPOJOPK("Browse_items_" //$NON-NLS-1$
                    + mainPivotName);
            ViewPOJO view = getViewPOJO(viewPOJOPK);

            // Create an ItemWhere which combines the search and and view wheres
            IWhereItem fullWhere;
            ArrayList conditions = view.getWhereConditions().getList();
            Util.fixCondtions(conditions);
            fullWhere = getFullWhereCondition(whereItem, conditions);

            // Add View Filters from the Roles
            ArrayList<IWhereItem> roleWhereConditions = getViewWCFromRole(viewPOJOPK);
            fullWhere = getFullWhereCondition(fullWhere, roleWhereConditions);

            return runPivotIndexQuery(clusterName, mainPivotName,
                    pivotWithKeys, universe.getItemsRevisionIDs(),
                    universe.getDefaultItemRevisionID(), indexPaths, fullWhere,
                    pivotDirections, indexDirections, start, limit);

        } catch (XtentisException e) {
            throw (e);
        } catch (Exception e) {
            String err = "Unable to search: " + ": " + e.getClass().getName()
                    + ": " + e.getLocalizedMessage();
            logger.error(err, e);
            throw new XtentisException(err);
        }
    }

    public ArrayList<String> getChildrenItems(String clusterName,
                                              String conceptName, String[] PKXpaths, String FKXpath,
                                              String labelXpath, String fatherPK, IWhereItem whereItem,
                                              int start, int limit) throws XtentisException {
        try {
            // get the universe and revision ID
            ILocalUser localuser = getLocalUser();
            UniversePOJO universe = localuser.getUniverse();
            if (universe == null) {
                String err = "ERROR: no Universe set for user '"
                        + localuser.getUsername() + "'";
                logger.error(err);
                throw new XtentisException(err);
            }

            ViewPOJOPK viewPOJOPK = new ViewPOJOPK("Browse_items_" //$NON-NLS-1$
                    + conceptName);
            ViewPOJO view = getViewPOJO(viewPOJOPK);

            // Create an ItemWhere which combines the search and and view wheres
            IWhereItem fullWhere;
            ArrayList conditions = view.getWhereConditions().getList();
            Util.fixCondtions(conditions);
            fullWhere = getFullWhereCondition(whereItem, conditions);

            return runChildrenItemsQuery(clusterName, conceptName, PKXpaths,
                    FKXpath, labelXpath, fatherPK,
                    universe.getItemsRevisionIDs(),
                    universe.getDefaultItemRevisionID(), fullWhere, start,
                    limit);

        } catch (XtentisException e) {
            throw (e);
        } catch (Exception e) {
            String err = "Unable to search: " + ": " + e.getClass().getName()
                    + ": " + e.getLocalizedMessage();
            logger.error(err, e);
            throw new XtentisException(err);
        }
    }

    public void resendFailtSvnMessage() throws Exception {

    }

    public ArrayList<String> viewSearch(DataClusterPOJOPK dataClusterPOJOPK,
                                        ViewPOJOPK viewPOJOPK, IWhereItem whereItem, int spellThreshold,
                                        String orderBy, String direction, int start, int limit)
            throws XtentisException {

        // get the universe and revision ID
        UniversePOJO universe = getLocalUser().getUniverse();
        if (universe == null) {
            String err = "ERROR: no Universe set for user '" + LocalUser.getLocalUser().getUsername() + "'";
            org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
            throw new XtentisException(err);
        }

        try {
            ViewPOJO view = getViewPOJO(viewPOJOPK);
            whereItem = Util.fixWebCondtions(whereItem);
            // Create an ItemWhere which combines the search and and view wheres
            IWhereItem fullWhere;
            ArrayList conditions = view.getWhereConditions().getList();
            // fix conditions:value of condition do not generate xquery.
            Util.fixCondtions(conditions);

            if (conditions == null || conditions.size() == 0) {
                if (whereItem == null)
                    fullWhere = null;
                else
                    fullWhere = whereItem;
            } else {
                if (whereItem == null) {
                    fullWhere = new WhereAnd(conditions);
                } else {
                    WhereAnd viewWhere = new WhereAnd(conditions);
                    WhereAnd wAnd = new WhereAnd();
                    wAnd.add(whereItem);
                    wAnd.add(viewWhere);
                    fullWhere = wAnd;
                }
            }

            // Add Filters from the Roles
            ILocalUser user = getLocalUser();
            HashSet<String> roleNames = user.getRoles();
            ArrayList<IWhereItem> roleWhereConditions = new ArrayList<IWhereItem>();
            String objectType = "View"; //$NON-NLS-1$
            for (String roleName : roleNames) {
                if ("administration".equals(roleName) || "authenticated".equals(roleName)) { //$NON-NLS-1$ //$NON-NLS-2$
                    continue;
                }
                //load Role
                RolePOJO role = ObjectPOJO.load(RolePOJO.class, new RolePOJOPK(roleName));
                //get Specifications for the View Object
                RoleSpecification specification = role.getRoleSpecifications().get(objectType);
                if (specification != null) {
                    if (!specification.isAdmin()) {
                        Set<String> regexIds = specification.getInstances().keySet();
                        for (String regexId : regexIds) {
                            if (viewPOJOPK.getIds()[0].matches(regexId)) {
                                HashSet<String> parameters = specification.getInstances().get(regexId).getParameters();
                                for (String marshaledWhereCondition : parameters) {
                                    if (marshaledWhereCondition == null || marshaledWhereCondition.trim().length() == 0) {
                                        continue;
                                    }
                                    String conditionValue = RoleWhereCondition.parse(marshaledWhereCondition).toWhereCondition().getRightValueOrPath();

                                    if (conditionValue != null && conditionValue.length() > 0) {
                                        roleWhereConditions.add(RoleWhereCondition.parse(marshaledWhereCondition).toWhereCondition());
                                    }
                                }
                            }
                        }
                    }
                }
            }
            // add collected additional conditions
            if (roleWhereConditions.size() > 0) {
                if (fullWhere == null) {
                    fullWhere = new WhereAnd(roleWhereConditions);
                } else {
                    WhereAnd viewWhere = new WhereAnd(roleWhereConditions);
                    WhereAnd wAnd = new WhereAnd();
                    wAnd.add(fullWhere);
                    wAnd.add(viewWhere);
                    fullWhere = wAnd;
                }
            }

            Server server = ServerContext.INSTANCE.get();
            Storage storage = server.getStorageAdmin().get(dataClusterPOJOPK.getUniqueId(), universe.getDefaultItemRevisionID());

            if (storage != null) {
                MetadataRepository repository = server.getMetadataRepositoryAdmin().get(dataClusterPOJOPK.getUniqueId());
                // Build query (find 'main' type)
                String typeName = ((String) view.getSearchableBusinessElements().getList().get(0)).split("/")[0]; //$NON-NLS-1$
                ComplexTypeMetadata type = repository.getComplexType(typeName);
                if (type == null) {
                    throw new IllegalArgumentException("Type '" + typeName + "' does not exist in data cluster '" + dataClusterPOJOPK.getUniqueId() + "'.");
                }
                UserQueryBuilder qb = UserQueryBuilder.from(type);

                // Select fields
                ArrayListHolder<String> viewableBusinessElements = view.getViewableBusinessElements();
                for (String viewableBusinessElement : viewableBusinessElements.getList()) {
                    String viewableTypeName = StringUtils.substringBefore(viewableBusinessElement, "/"); //$NON-NLS-1$
                    String viewablePath = StringUtils.substringAfter(viewableBusinessElement, "/"); //$NON-NLS-1$
                    ComplexTypeMetadata viewableType = repository.getComplexType(viewableTypeName);
                    qb.select(viewableType.getField(viewablePath));
                }

                // Condition and paging
                qb.where(UserQueryHelper.buildCondition(qb, fullWhere, repository));
                qb.start(start < 0 ? 0 : start); // UI can send negative start index
                qb.limit(limit);

                // Order by
                if (orderBy != null) {
                    FieldMetadata field = type.getField(StringUtils.substringAfter(orderBy, "/")); //$NON-NLS-1$
                    OrderBy.Direction queryDirection;
                    if ("ascending".equals(direction)) { //$NON-NLS-1$
                        queryDirection = OrderBy.Direction.ASC;
                    } else {
                        queryDirection = OrderBy.Direction.DESC;
                    }
                    qb.orderBy(field, queryDirection);
                }

                // Get records
                StorageResults results = storage.fetch(qb.getSelect());
                ArrayList<String> resultsAsString = new ArrayList<String>();
                resultsAsString.add("<totalCount>" + results.getCount() + "</totalCount>"); //$NON-NLS-1$ //$NON-NLS-2$
                DataRecordWriter writer = new ViewSearchResultsWriter();
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                for (DataRecord result : results) {
                    try {
                        writer.write(result, output);
                    } catch (IOException e) {
                        throw new XmlServerException(e);
                    }
                    String document = new String(output.toByteArray(), Charset.forName("UTF-8"));
                    resultsAsString.add(document);
                    output.reset();
                }
                return resultsAsString;
            } else {
                // build the patterns to revision ID map
                LinkedHashMap<String, String> conceptPatternsToRevisionID = new LinkedHashMap<String, String>(
                        universe.getItemsRevisionIDs());
                if (universe.getDefaultItemRevisionID() != null
                        && universe.getDefaultItemRevisionID().length() > 0)
                    conceptPatternsToRevisionID.put(".*", //$NON-NLS-1$
                            universe.getDefaultItemRevisionID());

                // build the patterns to cluster map - only one cluster at this stage
                LinkedHashMap<String, String> conceptPatternsToClusterName = new LinkedHashMap<String, String>();
                conceptPatternsToClusterName.put(".*", dataClusterPOJOPK.getUniqueId()); //$NON-NLS-1$

                Map<String, ArrayList<String>> metaDataTypes = getMetaTypes(fullWhere);
                return runItemsQuery(conceptPatternsToRevisionID,
                        conceptPatternsToClusterName, null, view
                        .getViewableBusinessElements().getList(),
                        fullWhere, orderBy, direction, start, limit,
                        spellThreshold, true, metaDataTypes, true);
            }
        } catch (XtentisException e) {
            throw (e);
        } catch (Exception e) {
            String err = "Unable to single search: " + ": "
                    + e.getClass().getName() + ": " + e.getLocalizedMessage();
            org.apache.log4j.Logger.getLogger(this.getClass()).error(err, e);
            throw new XtentisException(err, e);
        }
    }

    public ItemPOJOPK putItem(ItemPOJO item, String schema, String dataModelName) throws XtentisException {
        if (logger.isTraceEnabled()) {
            logger.trace("putItem() " + item.getItemPOJOPK().getUniqueID());
        }

        try {
            //make sure the plan is reset
            item.setPlanPK(null);
            //used for binding data model
            if (dataModelName != null) {
                item.setDataModelName(dataModelName);
            }

            //Store
            XmlServerSLWrapperLocal xmlServerCtrlLocal = Util.getXmlServerCtrlLocal();
            String dataClusterName = item.getDataClusterPOJOPK().getUniqueId();
            ItemPOJOPK pk;
            try {
                xmlServerCtrlLocal.start(dataClusterName);
                {
                    pk = item.store();
                }
                xmlServerCtrlLocal.commit(dataClusterName);
            } catch (XtentisException e) {
                try {
                    xmlServerCtrlLocal.rollback(dataClusterName);
                } catch (XtentisException e1) {
                    Logger.getLogger(IItemCtrlDelegator.class).error("Rollback error.", e1);
                }
                throw new RuntimeException(e);
            }

            if (pk == null) {
                throw new XtentisException("Could not put item " + Util.joinStrings(item.getItemIds(), ".") + ". Check server logs.");
            }

            return pk;
        } catch (Exception e) {
            String prefix = "Unable to create/update the item " + item.getDataClusterPOJOPK().getUniqueId() + "." + Util.joinStrings(item.getItemIds(), ".") + ": ";
            String err = prefix + e.getClass().getName() + ": " + e.getLocalizedMessage();
            logger.error(err, e);
            // simplify the error message
            if ("Reporting".equalsIgnoreCase(dataModelName)) {
                if (err.contains("One of '{ListOfFilters}'")) {
                    err = prefix + "At least one filter must be defined";
                }
            }
            throw new XtentisException(err, e);
        }
    }

    public ArrayList<String> xPathsSearch(DataClusterPOJOPK dataClusterPOJOPK,
                                          String forceMainPivot, ArrayList<String> viewablePaths,
                                          IWhereItem whereItem, int spellThreshold, String orderBy,
                                          String direction, int start, int limit, boolean returnCount)
            throws XtentisException {
        try {
            if (viewablePaths.size() == 0) {
                String err = "The list of viewable xPaths must contain at least one element";
                logger.error(err);
                throw new XtentisException(err);
            }
            // Check if user is allowed to read the cluster
            ILocalUser user = getLocalUser();
            boolean authorized = false;
            if (MDMConfiguration.getAdminUser().equals(user.getUsername())
                    || LocalUser.UNAUTHENTICATED_USER
                    .equals(user.getUsername())) {
                authorized = true;
            } else if (user.userCanRead(DataClusterPOJO.class,
                    dataClusterPOJOPK.getUniqueId())) {
                authorized = true;
            }
            if (!authorized) {
                throw new XtentisException(
                        "Unauthorized read access on data cluster '"
                                + dataClusterPOJOPK.getUniqueId()
                                + "' by user '" + user.getUsername() + "'");
            }

            // get the universe and revision ID
            UniversePOJO universe = user.getUniverse();
            if (universe == null) {
                String err = "ERROR: no Universe set for user '"
                        + LocalUser.getLocalUser().getUsername() + "'";
                logger.error(err);
                throw new XtentisException(err);
            }

            // build the patterns to revision ID map
            LinkedHashMap<String, String> conceptPatternsToRevisionID = new LinkedHashMap<String, String>(
                    universe.getItemsRevisionIDs());
            if (universe.getDefaultItemRevisionID() != null) {
                conceptPatternsToRevisionID.put(".*",
                        universe.getDefaultItemRevisionID());
            }

            // build the patterns to cluster map - only one cluster at this
            // stage
            LinkedHashMap<String, String> conceptPatternsToClusterName = new LinkedHashMap<String, String>();
            conceptPatternsToClusterName.put(".*",
                    dataClusterPOJOPK.getUniqueId());

            // add recordsSecurity filters for the Role
            whereItem = getFullWhereCondition(whereItem, new ArrayList<IWhereItem>(0));
            return runItemsQuery(conceptPatternsToRevisionID,
                    conceptPatternsToClusterName, forceMainPivot,
                    viewablePaths, whereItem, orderBy, direction, start, limit,
                    spellThreshold, returnCount, Collections.emptyMap(), false);

        } catch (XtentisException e) {
            throw (e);
        } catch (Exception e) {
            String err = "Unable to single search: " + ": "
                    + e.getClass().getName() + ": " + e.getLocalizedMessage();
            logger.error(err, e);
            throw new XtentisException(err, e);
        }
    }

    public ArrayList<String> getItems(DataClusterPOJOPK dataClusterPOJOPK,
                                      String conceptName, IWhereItem whereItem, int spellThreshold,
                                      String orderBy, String direction, int start, int limit,
                                      boolean totalCountOnFirstRow) throws XtentisException {
        // get the universe and revision ID
        ILocalUser user = getLocalUser();
        UniversePOJO universe = user.getUniverse();
        if (universe == null) {
            String err = "ERROR: no Universe set for user '" + user.getUsername() + "'";
            logger.error(err);
            throw new XtentisException(err);
        }
        Server server = ServerContext.INSTANCE.get();
        Storage storage = server.getStorageAdmin().get(dataClusterPOJOPK.getUniqueId(), universe.getDefaultItemRevisionID());

        if (storage != null) {
            MetadataRepository repository = server.getMetadataRepositoryAdmin().get(dataClusterPOJOPK.getUniqueId());
            ComplexTypeMetadata type = repository.getComplexType(conceptName);
            UserQueryBuilder qb = UserQueryBuilder.from(type);

            // Condition and paging
            qb.where(UserQueryHelper.buildCondition(qb, whereItem, repository));
            qb.start(start < 0 ? 0 : start); // UI can send negative start index
            qb.limit(limit);

            // Order by
            if (orderBy != null) {
                FieldMetadata field = type.getField(StringUtils.substringAfter(orderBy, "/")); //$NON-NLS-1$
                if (field == null) {
                    throw new IllegalArgumentException("Field '" + orderBy + "' does not exist.");
                }
                OrderBy.Direction queryDirection;
                if ("ascending".equals(direction)) { //$NON-NLS-1$
                    queryDirection = OrderBy.Direction.ASC;
                } else {
                    queryDirection = OrderBy.Direction.DESC;
                }
                qb.orderBy(field, queryDirection);
            }

            // Get records
            StorageResults results;
            ArrayList<String> resultsAsString = new ArrayList<String>();
            if (totalCountOnFirstRow) {
                results = storage.fetch(qb.getSelect());
                resultsAsString.add("<totalCount>" + results.getCount() + "</totalCount>"); //$NON-NLS-1$ //$NON-NLS-2$
            }

            results = storage.fetch(qb.getSelect());
            DataRecordWriter writer = new DataRecordXmlWriter();
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            for (DataRecord result : results) {
                try {
                    if (totalCountOnFirstRow) {
                        output.write("<result>".getBytes()); //$NON-NLS-1$
                    }
                    writer.write(result, output);
                    if (totalCountOnFirstRow) {
                        output.write("</result>".getBytes()); //$NON-NLS-1$
                    }
                } catch (IOException e) {
                    throw new XtentisException(e);
                }
                String document = new String(output.toByteArray());
                resultsAsString.add(document);
                output.reset();
            }
            return resultsAsString;
        } else {
            // ******* Old behavior **********
            // build the patterns to revision ID map
            LinkedHashMap<String, String> conceptPatternsToRevisionID = new LinkedHashMap<String, String>(
                    universe.getItemsRevisionIDs());
            if (universe.getDefaultItemRevisionID() != null
                    && universe.getDefaultItemRevisionID().length() > 0)
                conceptPatternsToRevisionID.put(".*", //$NON-NLS-1$
                        universe.getDefaultItemRevisionID());

            // build the patterns to cluster map - only one cluster at this stage
            LinkedHashMap<String, String> conceptPatternsToClusterName = new LinkedHashMap<String, String>();
            conceptPatternsToClusterName.put(".*", dataClusterPOJOPK.getUniqueId());

            try {
                ArrayList<String> elements = new ArrayList<String>();
                elements.add(conceptName);
                // add recordsSecurity filters for the Role
                whereItem = getFullWhereCondition(whereItem, new ArrayList<IWhereItem>(0));

                return runItemsQuery(conceptPatternsToRevisionID,
                        conceptPatternsToClusterName, null, elements, whereItem,
                        orderBy, direction, start, limit, spellThreshold,
                        totalCountOnFirstRow, Collections.emptyMap(), false);
            } catch (XtentisException e) {
                throw (e);
            } catch (Exception e) {
                String err = "Unable to get the items: " + ": "
                        + e.getClass().getName() + ": " + e.getLocalizedMessage();
                logger.error(err, e);
                throw new XtentisException(err, e);
            }
        }
    }

    protected Map<String, ArrayList<String>> getMetaTypes(IWhereItem fullWhere) throws Exception {
        return Util.getMetaDataTypes(fullWhere);
    }

    /**
     * get view where conditions from Role CE version return empty
     *
     * @param viewPOJOPK
     * @return
     * @throws Exception
     */
    protected abstract ArrayList<IWhereItem> getViewWCFromRole(ViewPOJOPK viewPOJOPK) throws Exception;

    protected IWhereItem getFullWhereCondition(IWhereItem whereItem,
                                               ArrayList<IWhereItem> conditions) {
        IWhereItem fullWhere;
        if (conditions == null || conditions.size() == 0) {
            if (whereItem == null)
                fullWhere = null;
            else
                fullWhere = whereItem;
        } else {
            if (whereItem == null) {
                fullWhere = new WhereAnd(conditions);
            } else {
                WhereAnd viewWhere = new WhereAnd(conditions);
                WhereAnd wAnd = new WhereAnd();
                wAnd.add(whereItem);
                wAnd.add(viewWhere);
                fullWhere = wAnd;
            }
        }
        return fullWhere;
    }

    /**
     * *************** test interfaces *****************************
     */

    public ViewPOJO getViewPOJO(ViewPOJOPK viewPOJOPK) throws Exception {
        return Util.getViewCtrlLocalHome().create().getView(viewPOJOPK);
    }

    public ILocalUser getLocalUser() throws XtentisException {
        return LocalUser.getLocalUser();
    }

    public ArrayList<String> runItemsQuery(
            LinkedHashMap conceptPatternsToRevisionID,
            LinkedHashMap conceptPatternsToClusterName, String forceMainPivot,
            ArrayList viewableFullPaths, IWhereItem whereItem, String orderBy,
            String direction, int start, int limit, int spellThreshold,
            boolean firstTotalCount, Map metaDataTypes, boolean withStartLimit)
            throws XtentisException {
        XmlServerSLWrapperLocal server = Util.getXmlServerCtrlLocal();
        String query = server.getItemsQuery(conceptPatternsToRevisionID,
                conceptPatternsToClusterName,
                forceMainPivot, // the main pivots will be that of the first
                // element of the viewable list
                viewableFullPaths, whereItem, orderBy, direction, start, limit,
                spellThreshold, firstTotalCount, metaDataTypes);
        logger.debug(query);
        if (withStartLimit)
            return server.runQuery(null, null, query, null, start, limit, true);
        else
            return server.runQuery(null, null, query, null);
    }

    public ArrayList<String> runChildrenItemsQuery(String clusterName,
                                                   String conceptName, String[] PKXpaths, String FKXpath,
                                                   String labelXpath, String fatherPK, LinkedHashMap itemsRevisionIDs,
                                                   String defaultRevisionID, IWhereItem whereItem, int start, int limit)
            throws XtentisException {
        XmlServerSLWrapperLocal server = Util.getXmlServerCtrlLocal();
        String query = server.getChildrenItemsQuery(clusterName, conceptName,
                PKXpaths, FKXpath, labelXpath, fatherPK, itemsRevisionIDs,
                defaultRevisionID, whereItem, start, limit);

        logger.debug(query);
        return server.runQuery(null, null, query, null);
    }

    public ArrayList<String> runPivotIndexQuery(String clusterName,
                                                String mainPivotName, LinkedHashMap pivotWithKeys,
                                                LinkedHashMap itemsRevisionIDs, String defaultRevisionID,
                                                String[] indexPaths, IWhereItem whereItem,
                                                String[] pivotDirections, String[] indexDirections, int start,
                                                int limit) throws XtentisException {
        XmlServerSLWrapperLocal server = Util.getXmlServerCtrlLocal();
        String query = server.getPivotIndexQuery(clusterName, mainPivotName,
                pivotWithKeys, itemsRevisionIDs, defaultRevisionID, indexPaths,
                whereItem, pivotDirections, indexDirections, start, limit);
        logger.debug(query);
        return server.runQuery(null, null, query, null, start, limit, false);
    }

}
