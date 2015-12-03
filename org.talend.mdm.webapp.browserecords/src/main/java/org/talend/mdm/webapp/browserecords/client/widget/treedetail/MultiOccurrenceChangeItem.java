package org.talend.mdm.webapp.browserecords.client.widget.treedetail;

import java.util.Map;

import org.talend.mdm.webapp.base.client.model.DataTypeConstants;
import org.talend.mdm.webapp.base.shared.ComplexTypeModel;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsEvents;
import org.talend.mdm.webapp.browserecords.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.browserecords.client.model.ComboBoxModel;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.client.mvc.BrowseRecordsView;
import org.talend.mdm.webapp.browserecords.client.util.CommonUtil;
import org.talend.mdm.webapp.browserecords.client.util.LabelUtil;
import org.talend.mdm.webapp.browserecords.client.util.Locale;
import org.talend.mdm.webapp.browserecords.client.widget.ItemDetailToolBar;
import org.talend.mdm.webapp.browserecords.client.widget.ItemsDetailPanel;
import org.talend.mdm.webapp.browserecords.client.widget.inputfield.ComboBoxField;
import org.talend.mdm.webapp.browserecords.client.widget.treedetail.TreeDetail.DynamicTreeItem;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MultiOccurrenceChangeItem extends HorizontalPanel {

    public interface AddRemoveHandler {

        void addedNode(DynamicTreeItem selectedItem, String optId);

        void removedNode(DynamicTreeItem selectedItem);

        void clearNodeValue(DynamicTreeItem selectedItem);
    }

    private AddRemoveHandler addRemoveHandler;

    private Image addNodeImg;

    private Image cloneNodeImg;

    private Image removeNodeImg;

    private Image warnImg;

    private TreeDetail treeDetail;

    private Field<?> field;

    private ViewBean viewBean;

    public void setAddRemoveHandler(AddRemoveHandler addRemoveHandler) {
        this.addRemoveHandler = addRemoveHandler;
    }

    public MultiOccurrenceChangeItem(final ItemNodeModel itemNode, final ViewBean viewBean, Map<String, Field<?>> fieldMap,
            String operation, final ItemsDetailPanel itemsDetailPanel) {
        this.viewBean = viewBean;
        // create Field
        String xPath = itemNode.getBindingPath();
        String typePath = itemNode.getTypePath();
        TypeModel typeModel = viewBean.getBindingEntityModel().getMetaDataTypes().get(typePath);
        String dynamicLabel = typeModel.getLabel(Locale.getLanguage());
        HTML label = new HTML();
        String html = itemNode.getLabel();

        if (LabelUtil.isDynamicLabel(dynamicLabel)) {
            if (itemNode.getDynamicLabel() != null && !"".equals(itemNode.getDynamicLabel())) { //$NON-NLS-1$
                html = itemNode.getDynamicLabel();
            } else {
                html = LabelUtil.getNormalLabel(html);
            }
        }

        if (itemNode.isKey() || (typeModel.getMinOccurs() == 1)) {
            html = html + "<span style=\"color:red\"> *</span>"; //$NON-NLS-1$
        }

        if (null != itemNode.getDescription() && (itemNode.getDescription().trim().length() > 0) && xPath.indexOf("/") > -1) { //$NON-NLS-1$
            html = html
                    + "<img style='margin-left:16px;' src='secure/img/genericUI/information_icon.png' title='" + LabelUtil.convertSpecialHTMLCharacter(itemNode.getDescription()) + "' />"; //$NON-NLS-1$ //$NON-NLS-2$
        }
        label.setHTML(html);
        this.add(label);
        if (typeModel.isSimpleType()
                || (!typeModel.isSimpleType() && ((ComplexTypeModel) typeModel).getReusableComplexTypes().size() > 0)) {

            if ((typeModel.getType().equals(DataTypeConstants.AUTO_INCREMENT) || typeModel.getType().equals(
                    DataTypeConstants.UUID))
                    && ItemDetailToolBar.DUPLICATE_OPERATION.equals(operation)) {
                itemNode.setObjectValue(""); //$NON-NLS-1$
            }
            field = TreeDetailGridFieldCreator.createField(itemNode, typeModel, Locale.getLanguage(), fieldMap, operation,
                    itemsDetailPanel);
            field.setWidth(200);

            if (field instanceof ComboBoxField) {
                @SuppressWarnings("unchecked")
                ComboBoxField<ComboBoxModel> comboBoxField = (ComboBoxField<ComboBoxModel>) field;
                comboBoxField.addSelectionChangedListener(new SelectionChangedListener<ComboBoxModel>() {

                    @Override
                    public void selectionChanged(SelectionChangedEvent<ComboBoxModel> se) {
                        executeVisibleRule(itemNode, itemsDetailPanel);
                    }
                });
            } else {
                field.addListener(Events.Change, new Listener<FieldEvent>() {

                    @Override
                    public void handleEvent(FieldEvent be) {
                        executeVisibleRule(itemNode, itemsDetailPanel);
                    }
                });
            }
            this.add(field);
        }

        if (typeModel.getMaxOccurs() < 0 || typeModel.getMaxOccurs() > 1) {
            addNodeImg = new Image("secure/img/genericUI/add.png"); //$NON-NLS-1$
            addNodeImg.getElement().setId("Add"); //$NON-NLS-1$
            addNodeImg.setTitle(MessagesFactory.getMessages().clone_title());
            addNodeImg.getElement().getStyle().setMarginLeft(20D, Unit.PX);
            addNodeImg.getElement().getStyle().setMarginTop(5D, Unit.PX);
            if (!typeModel.isReadOnly()) {
                addNodeImg.addClickHandler(handler);
            }
            removeNodeImg = new Image("secure/img/genericUI/delete.png"); //$NON-NLS-1$
            removeNodeImg.getElement().setId("Remove"); //$NON-NLS-1$
            removeNodeImg.setTitle(MessagesFactory.getMessages().remove_title());
            removeNodeImg.getElement().getStyle().setMarginLeft(5.0, Unit.PX);
            addNodeImg.getElement().getStyle().setMarginTop(5D, Unit.PX);
            if (!typeModel.isReadOnly()) {
                removeNodeImg.addClickHandler(handler);
            }

            this.add(addNodeImg);
            this.setCellVerticalAlignment(addNodeImg, VerticalPanel.ALIGN_BOTTOM);
            this.add(removeNodeImg);
            this.setCellVerticalAlignment(removeNodeImg, VerticalPanel.ALIGN_BOTTOM);
            if (!typeModel.isSimpleType() && itemNode.getParent() != null) {
                cloneNodeImg = new Image("secure/img/genericUI/add-group.png"); //$NON-NLS-1$
                cloneNodeImg.getElement().setId("Clone"); //$NON-NLS-1$
                cloneNodeImg.setTitle(MessagesFactory.getMessages().deepclone_title());
                cloneNodeImg.getElement().getStyle().setMarginLeft(5.0, Unit.PX);
                if (!typeModel.isReadOnly()) {
                    cloneNodeImg.addClickHandler(handler);
                }
                this.add(cloneNodeImg);
                this.setCellVerticalAlignment(cloneNodeImg, VerticalPanel.ALIGN_BOTTOM);
            }
        }
        this.add(new Label()); // format placeholder, align icon on line
        this.setCellWidth(label, "200px"); //$NON-NLS-1$
        warnImg = new Image("secure/img/genericUI/validateBadge.gif"); //$NON-NLS-1$
        warnImg.getElement().getStyle().setMarginLeft(5.0, Unit.PX);
        warnImg.setVisible(false);
        this.add(warnImg);
        this.setCellVerticalAlignment(warnImg, VerticalPanel.ALIGN_BOTTOM);
        this.getElement().getStyle().setMarginBottom(6D, Unit.PX);
        this.setVisible(typeModel.isVisible());
    }

    public void clearValue() {
        if (field != null) {
            field.clear();
        }
    }

    public void switchRemoveOpt(boolean isRemoveNode, boolean isFk) {
        if (removeNodeImg != null) {
            removeNodeImg.setVisible(true);
            if (isRemoveNode) {
                removeNodeImg.setUrl("secure/img/genericUI/delete.png"); //$NON-NLS-1$
                removeNodeImg.getElement().setId("Remove"); //$NON-NLS-1$
                removeNodeImg.setTitle(MessagesFactory.getMessages().remove_title());
            } else {
                removeNodeImg.setUrl("secure/img/genericUI/clear-value.png"); //$NON-NLS-1$
                removeNodeImg.setTitle(MessagesFactory.getMessages().reset_value_title());
                removeNodeImg.getElement().setId("Clear"); //$NON-NLS-1$
                if (isFk) {
                    removeNodeImg.setVisible(false);
                }
            }
        }
    }

    public void setAddIconVisible(boolean visible) {
        if (addNodeImg != null) {
            addNodeImg.setVisible(visible);
        }
        if (cloneNodeImg != null) {
            cloneNodeImg.setVisible(visible);
        }
    }

    public void warnMandatory() {
        warnImg.setVisible(true);
    }

    public void clearWarning() {
        warnImg.setVisible(false);
    }

    public void setTreeDetail(TreeDetail treeDetail) {
        this.treeDetail = treeDetail;
    }

    public boolean isAddRemoveHandlerNull() {
        return addRemoveHandler == null;
    }

    private ClickHandler handler = new ClickHandler() {

        @Override
        public void onClick(ClickEvent arg0) {
            final DynamicTreeItem selectedItem = treeDetail.getSelectedItem();
            if (selectedItem == null) {
                return;
            }

            if (addRemoveHandler != null) {
                if ("Add".equals(arg0.getRelativeElement().getId()) || "Clone".equals(arg0.getRelativeElement().getId())) { //$NON-NLS-1$ //$NON-NLS-2$ 
                    addRemoveHandler.addedNode(selectedItem, arg0.getRelativeElement().getId());
                } else if ("Remove".equals(arg0.getRelativeElement().getId())) { //$NON-NLS-1$
                    addRemoveHandler.removedNode(selectedItem);
                } else {
                    addRemoveHandler.clearNodeValue(selectedItem);
                }
            }
        }
    };

    private void executeVisibleRule(final ItemNodeModel itemNode, final ItemsDetailPanel itemsDetailPanel) {
        AppEvent app = new AppEvent(BrowseRecordsEvents.ExecuteVisibleRule);
        ItemNodeModel parent = CommonUtil.recrusiveRoot(itemNode);
        // maybe need other methods to get entire tree
        if (parent == null || parent.getChildCount() == 0) {
            return;
        }
        app.setData(parent);
        app.setData("viewBean", viewBean); //$NON-NLS-1$
        app.setData(BrowseRecordsView.ITEMS_DETAIL_PANEL, itemsDetailPanel);
        Dispatcher.forwardEvent(app);
    }
}
