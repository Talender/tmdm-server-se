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
package org.talend.mdm.webapp.browserecords.client.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.base.client.model.BasePagingLoadConfigImpl;
import org.talend.mdm.webapp.base.client.model.ForeignKeyBean;
import org.talend.mdm.webapp.base.client.model.ItemBaseModel;
import org.talend.mdm.webapp.base.client.model.ItemBasePageLoadResult;
import org.talend.mdm.webapp.base.client.widget.ColumnAlignGrid;
import org.talend.mdm.webapp.base.client.widget.PagingToolBarEx;
import org.talend.mdm.webapp.base.shared.EntityModel;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsEvents;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsServiceAsync;
import org.talend.mdm.webapp.browserecords.client.model.ColumnTreeLayoutModel;
import org.talend.mdm.webapp.browserecords.client.model.ForeignKeyDrawer;
import org.talend.mdm.webapp.browserecords.client.model.ForeignKeyModel;
import org.talend.mdm.webapp.browserecords.client.model.FormatModel;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.client.model.ItemResult;
import org.talend.mdm.webapp.browserecords.client.model.QueryModel;
import org.talend.mdm.webapp.browserecords.client.model.Restriction;
import org.talend.mdm.webapp.browserecords.client.model.UpdateItemModel;
import org.talend.mdm.webapp.browserecords.client.widget.ItemDetailToolBar;
import org.talend.mdm.webapp.browserecords.shared.AppHeader;
import org.talend.mdm.webapp.browserecords.shared.FKIntegrityResult;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;
import org.talend.mdm.webapp.browserecords.shared.VisibleRuleResult;

import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Mock Grid refresh() method is called<br>
 * After executing save successfully, when ItemDetailToolBar's isFkToolBar=false and isOutMost = false and
 * isHierarchyCall = false, gridRefresh() is called, otherwise gridRefresh() is not called.
 */
@SuppressWarnings("nls")
public class MockGridRefreshGWTTest extends GWTTestCase {

    boolean isGridRefresh;

    boolean isCreate = false;

    ItemBean createItemBean = null;

    ItemBean selectedItem = null;

    Grid<ItemBean> grid = null;

    ListStore<ItemBean> store = null;

    private PagingToolBarEx pagingBar = null;

    private String sortField;

    MockBrowseRecordsServiceAsync service = new MockBrowseRecordsServiceAsync();

    public void testGridRefresh() {

        AppEvent event = new AppEvent(BrowseRecordsEvents.SaveItem);
        ItemDetailToolBar detailToolBar = new ItemDetailToolBar(null);
        event.setData("itemDetailToolBar", detailToolBar);

        // 1. fkToolBar = false, isOutMost = false, isHierarchyCall = false
        detailToolBar.setFkToolBar(false);
        detailToolBar.setOutMost(false);
        detailToolBar.setHierarchyCall(false);
        onSaveItem(event);
        assertEquals(true, isGridRefresh);

        isGridRefresh = false;

        // 2. fkToolBar = true, isOutMost = false, isHierarchyCall = false
        detailToolBar.setFkToolBar(true);
        onSaveItem(event);
        assertEquals(false, isGridRefresh);

        // 3. fkToolBar = true, isOutMost = true, isHierarchyCall = false
        detailToolBar.setFkToolBar(true);
        detailToolBar.setOutMost(true);
        onSaveItem(event);
        assertEquals(false, isGridRefresh);

        // 4. fkToolBar = true, isOutMost = true, isHierarchyCall = true
        detailToolBar.setFkToolBar(true);
        detailToolBar.setOutMost(true);
        detailToolBar.setHierarchyCall(true);
        onSaveItem(event);
        assertEquals(false, isGridRefresh);

        // 5. fkToolBar = true, isOutMost = false, isHierarchyCall = true
        detailToolBar.setFkToolBar(true);
        detailToolBar.setOutMost(false);
        detailToolBar.setHierarchyCall(true);
        onSaveItem(event);
        assertEquals(false, isGridRefresh);

        // 6. fkToolBar = false, isOutMost = false, isHierarchyCall = true
        detailToolBar.setFkToolBar(false);
        detailToolBar.setOutMost(false);
        detailToolBar.setHierarchyCall(true);
        onSaveItem(event);
        assertEquals(false, isGridRefresh);

        // 7. fkToolBar = false, isOutMost = true, isHierarchyCall = true
        detailToolBar.setFkToolBar(false);
        detailToolBar.setOutMost(true);
        detailToolBar.setHierarchyCall(true);
        onSaveItem(event);
        assertEquals(false, isGridRefresh);

        // 8. fkToolBar = false, isOutMost = true, isHierarchyCall = false
        detailToolBar.setFkToolBar(false);
        detailToolBar.setOutMost(true);
        detailToolBar.setHierarchyCall(false);
        onSaveItem(event);
        assertEquals(false, isGridRefresh);

        detailToolBar.setFkToolBar(true);
        detailToolBar.setOutMost(true);
        detailToolBar.setHierarchyCall(false);
        onDeleteItemBeans();
        assertEquals(true, isGridRefresh);
    }

    public void testGridRefreshAfterSave() {

        AppEvent event = new AppEvent(BrowseRecordsEvents.SaveItem);
        ItemDetailToolBar detailToolBar = new ItemDetailToolBar(null);
        event.setData("itemDetailToolBar", detailToolBar);

        RpcProxy<PagingLoadResult<ItemBean>> proxy = new RpcProxy<PagingLoadResult<ItemBean>>() {

            @Override
            public void load(Object loadConfig, final AsyncCallback<PagingLoadResult<ItemBean>> callback) {
                return;
            }
        };

        PagingLoader<PagingLoadResult<ModelData>> loader = new BasePagingLoader<PagingLoadResult<ModelData>>(proxy);

        store = new ListStore<ItemBean>(loader);

        ColumnConfig idColumn = new ColumnConfig("id", "id", 200);

        ColumnConfig nameColumn = new ColumnConfig("name", "name", 200);

        List<ColumnConfig> ccList = new ArrayList<ColumnConfig>();

        ccList.add(idColumn);

        ccList.add(nameColumn);

        ColumnModel cm = new ColumnModel(ccList);

        grid = new ColumnAlignGrid<ItemBean>(store, cm);

        pagingBar = new PagingToolBarEx(3);

        pagingBar.bind(loader);

        sortField = "id";

        // init store data
        ItemBean itemBean1 = new ItemBean();
        itemBean1.set("id", "1");
        itemBean1.set("name", "Apple");
        store.add(itemBean1);
        ItemBean itemBean2 = new ItemBean();
        itemBean2.set("id", "4");
        itemBean2.set("name", "Banana");
        store.add(itemBean2);

        // test 1 pagesize=3 pageNumber=1 pageCount=1 position=2
        detailToolBar.setOutMost(false);
        detailToolBar.setFkToolBar(false);
        detailToolBar.setHierarchyCall(false);
        selectedItem = null;
        isCreate = true;
        ItemBean itemBean3 = new ItemBean();
        itemBean3.set("id", "3");
        itemBean3.set("name", "Zero");
        store.add(itemBean3);
        event.setData(itemBean3);
        onSaveItem(event);
        assertEquals(selectedItem.get("name"), "Zero");
        assertEquals(store.getAt(1).get("name"), "Zero");

        // test 2 pagesize=3 pageNumber=2 pageCount=2 position=4
        detailToolBar.setOutMost(false);
        detailToolBar.setFkToolBar(false);
        detailToolBar.setHierarchyCall(false);
        selectedItem = null;
        isCreate = true;
        ItemBean itemBean4 = new ItemBean();
        itemBean4.set("id", "6");
        itemBean4.set("name", "City");
        store.add(itemBean4);
        event.setData(itemBean4);
        onSaveItem(event);
        assertEquals(selectedItem.get("name"), "City");
        assertEquals(store.getAt(3).get("name"), "City");

        // test 3 pagesize=3 pageNumber=2 pageCount=2 position=2
        detailToolBar.setOutMost(false);
        detailToolBar.setFkToolBar(false);
        detailToolBar.setHierarchyCall(false);
        selectedItem = null;
        isCreate = true;
        ItemBean itemBean5 = new ItemBean();
        itemBean5.set("id", "2");
        itemBean5.set("name", "Orange");
        store.add(itemBean5);
        event.setData(itemBean5);
        onSaveItem(event);
        assertNull(selectedItem);
        assertEquals(store.getAt(1).get("name"), "Orange");

        // test 4 pagesize=3 pageNumber=2 pageCount=2 position=6
        detailToolBar.setOutMost(false);
        detailToolBar.setFkToolBar(false);
        detailToolBar.setHierarchyCall(false);
        selectedItem = null;
        isCreate = true;
        ItemBean itemBean6 = new ItemBean();
        itemBean6.set("id", "8");
        itemBean6.set("name", "Dance");
        store.add(itemBean6);
        event.setData(itemBean6);
        onSaveItem(event);
        assertEquals(selectedItem.get("name"), "Dance");
        assertEquals(store.getAt(5).get("name"), "Dance");

        sortField = null;

        // test 5 pagesize=3 pageNumber=2 pageCount=3 position=6
        detailToolBar.setOutMost(false);
        detailToolBar.setFkToolBar(false);
        detailToolBar.setHierarchyCall(false);
        selectedItem = null;
        isCreate = true;
        ItemBean itemBean7 = new ItemBean();
        itemBean7.set("id", "7");
        itemBean7.set("name", "Edit");
        store.add(itemBean7);
        event.setData(itemBean7);
        onSaveItem(event);
        assertEquals(selectedItem.get("name"), "Edit");
        assertEquals(store.getAt(5).get("name"), "Edit");

        // test 6
        detailToolBar.setOutMost(false);
        detailToolBar.setFkToolBar(false);
        detailToolBar.setHierarchyCall(false);
        selectedItem = null;
        isCreate = true;
        ItemBean itemBean8 = new ItemBean();
        itemBean8.set("id", "0");
        itemBean8.set("name", "Yello");
        store.add(itemBean8);
        event.setData(itemBean8);
        onSaveItem(event);
        assertEquals(store.getAt(0).get("name"), "Yello");
        assertEquals(0, pagingBar.getTotalPages());
    }

    private void onSaveItem(final AppEvent event) {

        final ItemDetailToolBar detailToolBar = event.getData("itemDetailToolBar");

        service.saveItem(new ViewBean(), "", "", false, "en", new SessionAwareAsyncCallback<ItemResult>() {

            public void onSuccess(ItemResult result) {
                assertEquals(ItemResult.SUCCESS, result.getStatus());
                // Grid will call refresh() when only fkToolBar = false, isOutMost = false, isHierarchyCall = false
                if (!detailToolBar.isFkToolBar() && !detailToolBar.isOutMost() && !detailToolBar.isHierarchyCall()) {
                    gridRefresh();
                }

                if (!detailToolBar.isOutMost() && !detailToolBar.isFkToolBar() && !detailToolBar.isHierarchyCall() && isCreate) {
                    createItemBean = event.getData();
                    isCreate = true;
                    if (sortField != null) {
                        store.sort(sortField, SortDir.ASC);
                        refreshGrid(createItemBean);
                    } else {
                        pagingBar.lastAfterCreate();
                        refreshGrid(createItemBean);
                    }
                }
            }

        });
    }

    private void refreshGrid(ItemBean itemBean) {
        if (selectedItem == null && itemBean != null) {
            fireLoadEvent();
        }
    }

    private void fireLoadEvent() {
        // mock grid loadListener
        if (store.getModels().size() > 0) {
            if (selectedItem == null) {
                // search and create
                if (isCreate && createItemBean != null) {
                    // mock pageRecord pageSize = 3
                    // totalRecordCount <= 3 pageNumber = 1
                    // totalRecordCount <= 6 pageNumber = 2
                    // totalRecordCount > 6 pageNumber = 2
                    int start = 0;
                    int end = 0;
                    if (store.getCount() <= 3) {
                        end = store.getCount() - 1;
                    } else if (store.getCount() <= 6) {
                        start = 3;
                        end = store.getCount() - 1;
                    } else {
                        start = 3;
                        end = 5;
                    }
                    for (int i = start; i <= end; i++) {
                        if (grid.getStore().getAt(i) == createItemBean) {
                            grid.getSelectionModel().select(-1, false);
                            grid.getSelectionModel().select(createItemBean, true);
                            break;
                        } else {
                            grid.getSelectionModel().select(-1, false);
                        }
                    }
                } else {
                    fail();
                }
                selectedItem = grid.getSelectionModel().getSelectedItem();
                isCreate = false;
                createItemBean = null;
            }
        }
    }


    private void onDeleteItemBeans() {
        service.deleteItemBeans(null, true, "en", new SessionAwareAsyncCallback<List<String>>() {

            public void onSuccess(List<String> result) {
                assertNotNull(result);
                gridRefresh();
            }
        });
    }

    private void gridRefresh() {
        isGridRefresh = true;
    }

    class MockBrowseRecordsServiceAsync implements BrowseRecordsServiceAsync {

        public void saveItem(ViewBean viewBean, String ids, String xml, boolean isCreate, String language,
                AsyncCallback<ItemResult> callback) {
            callback.onSuccess(new ItemResult(ItemResult.SUCCESS));
        }

        public void getForeignKeyList(BasePagingLoadConfigImpl config, TypeModel model, String dataClusterPK, boolean ifFKFilter,
                String value, String language,AsyncCallback<ItemBasePageLoadResult<ForeignKeyBean>> callback) {
        }

        public void getForeignKeyPolymTypeList(String xpathForeignKey, String language, AsyncCallback<List<Restriction>> callback) {
        }

        public void switchForeignKeyType(String targetEntityType, String xpathForeignKey, String xpathInfoForeignKey,
                String fkFilter, AsyncCallback<ForeignKeyDrawer> callback) {
        }

        public void queryItemBeans(QueryModel config, String language,AsyncCallback<ItemBasePageLoadResult<ItemBean>> callback) {
        }

        public void saveItemBean(ItemBean item, String language, AsyncCallback<String> callback) {
        }

        public void getItem(ItemBean itemBean, String viewPK, EntityModel entityModel, String language,
                AsyncCallback<ItemBean> callback) {
        }

        public void getView(String viewPk, String language, AsyncCallback<ViewBean> callback) {
        }

        public void deleteItemBeans(List<ItemBean> items, boolean override, String language, AsyncCallback<List<String>> callback) {
            callback.onSuccess(new ArrayList<String>());
        }

        public void checkFKIntegrity(List<ItemBean> selectedItems, AsyncCallback<Map<ItemBean, FKIntegrityResult>> asyncCallback) {
        }

        public void logicalDeleteItem(ItemBean item, String path, boolean override, AsyncCallback<Void> callback) {

        }

        public void logicalDeleteItems(List<ItemBean> items, String path, boolean override, AsyncCallback<Void> callback) {
        }

        public void getViewsList(String language, AsyncCallback<List<ItemBaseModel>> callback) {
        }

        public void getCriteriaByBookmark(String bookmark, AsyncCallback<String> callback) {
        }

        public void getUserCriterias(String view, AsyncCallback<List<ItemBaseModel>> callback) {
        }

        public void getAppHeader(AsyncCallback<AppHeader> callback) {
        }

        public void getCurrentDataModel(AsyncCallback<String> callback) {
        }

        public void getCurrentDataCluster(AsyncCallback<String> callback) {
        }

        public void querySearchTemplates(String view, boolean isShared, BasePagingLoadConfigImpl load,
                AsyncCallback<ItemBasePageLoadResult<ItemBaseModel>> callback) {
        }

        public void deleteSearchTemplate(String id, AsyncCallback<Void> callback) {
        }

        public void isExistCriteria(String dataObjectLabel, String id, AsyncCallback<Boolean> callback) {
        }

        public void saveCriteria(String viewPK, String templateName, boolean isShared, String criteriaString,
                AsyncCallback<Void> callback) {
        }

        public void getItemNodeModel(ItemBean item, EntityModel entity, String language, AsyncCallback<ItemNodeModel> callback) {
        }

        public void getMandatoryFieldList(String tableName, AsyncCallback<List<String>> callback) {
        }

        public void saveItem(String concept, String ids, String xml, boolean isCreate, String language,
                AsyncCallback<ItemResult> callback) {
        }

        public void getColumnTreeLayout(String concept, AsyncCallback<ColumnTreeLayoutModel> callback) {
        }

        public void getForeignKeyModel(String concept, String ids, String language, AsyncCallback<ForeignKeyModel> callback) {
        }

        public void getRunnableProcessList(String concept, String language, AsyncCallback<List<ItemBaseModel>> callback) {
        }

        public void processItem(String concept, String[] ids, String transformerPK, AsyncCallback<String> callback) {
        }

        public void getLineageEntity(String concept, AsyncCallback<List<String>> callback) {
        }

        public void getSmartViewList(String regex, AsyncCallback<List<ItemBaseModel>> callback) {
        }

        public void getItemBeanById(String concept, String[] ids, String language, AsyncCallback<ItemBean> callback) {

        }

        public void executeVisibleRule(ViewBean viewBean, String xml, AsyncCallback<List<VisibleRuleResult>> asyncCallback) {

        }

        public void isItemModifiedByOthers(ItemBean itemBean, AsyncCallback<Boolean> callback) {

        }

        public void updateItem(String concept, String ids, Map<String, String> changedNodes, String xml, String language,
                AsyncCallback<ItemResult> callback) {

        }

        public void updateItems(List<UpdateItemModel> updateItems, String language, AsyncCallback<List<ItemResult>> callback) {

        }

        public void formatValue(FormatModel model, AsyncCallback<String> callback) {

        }

        public void getEntityModel(String concept, String language, AsyncCallback<EntityModel> callback) {

        }

        public void createSubItemNodeModel(ViewBean viewBean, String xml, String typePath, String contextPath, String realType,
                String language, AsyncCallback<ItemNodeModel> callback) {
        }

        public void createDefaultItemNodeModel(ViewBean viewBean, Map<String, List<String>> initDataMap, String language,
                AsyncCallback<ItemNodeModel> callback) {

        }

        public void getForeignKeyValues(String concept, String[] ids, String language,
                AsyncCallback<Map<ViewBean, Map<String, List<String>>>> callback) {

        }

        public void isExistId(String concept, String[] ids, String language, AsyncCallback<Boolean> callback) {

        }

        /* (non-Javadoc)
         * @see org.talend.mdm.webapp.browserecords.client.BrowseRecordsServiceAsync#queryItemBeanById(java.lang.String, org.talend.mdm.webapp.browserecords.shared.ViewBean, org.talend.mdm.webapp.base.shared.EntityModel, java.lang.String, java.lang.String, com.google.gwt.user.client.rpc.AsyncCallback)
         */
        @Override
        public void queryItemBeanById(String dataClusterPK, ViewBean viewBean, EntityModel entityModel, String id,
                String language, AsyncCallback<ItemBean> callback) {
            // TODO Auto-generated method stub
            
        }

    }

    @Override
    public String getModuleName() {
        // GWTTestCase Required
        return "org.talend.mdm.webapp.browserecords.TestBrowseRecords"; //$NON-NLS-1$
    }
}
