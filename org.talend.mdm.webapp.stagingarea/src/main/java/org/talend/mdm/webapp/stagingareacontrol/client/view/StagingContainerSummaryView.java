// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.stagingareacontrol.client.view;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.base.client.util.StorageProvider;
import org.talend.mdm.webapp.stagingareacontrol.client.StagingareaControl;
import org.talend.mdm.webapp.stagingareacontrol.client.controller.ControllerContainer;
import org.talend.mdm.webapp.stagingareacontrol.client.model.ConceptRelationshipModel;
import org.talend.mdm.webapp.stagingareacontrol.client.model.FilterModel;
import org.talend.mdm.webapp.stagingareacontrol.client.model.StagingContainerModel;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.IconAlign;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.state.CookieProvider;
import com.extjs.gxt.ui.client.state.Provider;
import com.extjs.gxt.ui.client.widget.ProgressBar;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTMLPanel;

public class StagingContainerSummaryView extends AbstractView {

    public static final String STAGING_AREA_TITLE = "staging_area_title"; //$NON-NLS-1$

    public static final String STAGING_AREA_WAITING = "staging_area_waiting"; //$NON-NLS-1$

    public static final String STAGING_AREA_INVALID = "staging_area_invalid"; //$NON-NLS-1$

    public static final String STAGING_AREA_VALID = "staging_area_valid"; //$NON-NLS-1$

    private StagingContainerModel stagingContainerModel;

    private ProgressBar gaugesBar;

    private Button startValidate;

    private CheckBox withFiltering;

    private final int CHART_WIDTH = 400;

    private final int CHART_HEIGHT = 200;

    private HTMLPanel detailPanel;

    private Provider provider;

    private int totalRecordCount = 0;

    @Override
    protected void initComponents() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("<div style='margin-bottom:10px; font-weight: bold;' id='" + STAGING_AREA_TITLE + "'></div>"); //$NON-NLS-1$ //$NON-NLS-2$
        buffer.append("<div style='margin-left:20px; color:#0000ff; margin-bottom:5px;' id='" + STAGING_AREA_WAITING + "'></div>"); //$NON-NLS-1$ //$NON-NLS-2$
        buffer.append("<div style='margin-left:20px; color:#ff0000; margin-bottom:5px;' id='" + STAGING_AREA_INVALID + "'></div>"); //$NON-NLS-1$//$NON-NLS-2$
        buffer.append("<div style='margin-left:20px; color:#00aa00; margin-bottom:5px;' id='" + STAGING_AREA_VALID + "'></div>"); //$NON-NLS-1$//$NON-NLS-2$

        detailPanel = new HTMLPanel(buffer.toString());

        detailPanel.setSize("400px", "80px"); //$NON-NLS-1$//$NON-NLS-2$

        gaugesBar = new ProgressBar();
        startValidate = new Button(messages.start_validation());
        startValidate.setSize(200, 30);
        startValidate.setEnabled(false);
        startValidate.setIconAlign(IconAlign.TOP);

        withFiltering = new CheckBox(messages.with_filtering());
        provider = StorageProvider.newInstanceIfSupported();
        if (provider == null) {
            provider = new CookieProvider("/", null, null, Window.Location.getProtocol().toLowerCase().startsWith("https")); //$NON-NLS-1$ //$NON-NLS-2$
        }
        withFiltering.setValue(provider.getBoolean(StagingContainerSummaryView.class.getName() + "_withFiltering")); //$NON-NLS-1$

        mainPanel.setAutoHeight(true);
        mainPanel.setBodyBorder(false);
    }

    @Override
    protected void registerEvent() {
        startValidate.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {

                if (!withFiltering.getValue()) {
                    ControllerContainer.get().getSummaryController().startValidation();
                    return;
                }

                StagingareaControl.service.getConceptRelation(new SessionAwareAsyncCallback<ConceptRelationshipModel>() {

                    @Override
                    public void onSuccess(ConceptRelationshipModel relation) {
                        FilterDialog.showFilter(relation, new FilterDialog.FilterListener() {

                            @Override
                            public void onFilter(FilterModel filterModel) {
                                ControllerContainer.get().getSummaryController().startValidation(filterModel);
                            }
                        });
                    }
                });
            }
        });

        withFiltering.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                provider.set(StagingContainerSummaryView.class.getName() + "_withFiltering", withFiltering.getValue()); //$NON-NLS-1$
            }
        });
    }

    @Override
    protected void initLayout() {
        TableLayout mainLayout = new TableLayout(3);
        mainLayout.setWidth("100%"); //$NON-NLS-1$
        mainPanel.setLayout(mainLayout);
        TableData detailData = new TableData();
        detailData.setColspan(3);
        detailData.setRowspan(1);
        mainPanel.add(detailPanel, detailData);
        TableData gaugesData = new TableData();
        gaugesData.setColspan(1);
        gaugesData.setRowspan(1);
        mainPanel.add(gaugesBar, gaugesData);
        TableData startData = new TableData();
        startData.setWidth("220px"); //$NON-NLS-1$
        startData.setHorizontalAlign(HorizontalAlignment.CENTER);
        mainPanel.add(startValidate, startData);
        TableData withFilterData = new TableData();
        withFilterData.setWidth("100px"); //$NON-NLS-1$
        mainPanel.add(withFiltering, withFilterData);
    }

    public void initDetailPanel() {
        if (stagingContainerModel != null) {
            int waiting = stagingContainerModel.getWaitingValidationRecords();
            int valid = stagingContainerModel.getValidRecords();
            int invalid = stagingContainerModel.getInvalidRecords();

            Element titleEl = detailPanel.getElementById(STAGING_AREA_TITLE);
            titleEl.setInnerHTML(messages.total_desc("<b>" + stagingContainerModel.getTotalRecords() + "</b>")); //$NON-NLS-1$ //$NON-NLS-2$

            Element waitingEl = detailPanel.getElementById(STAGING_AREA_WAITING);
            waitingEl.setInnerHTML(messages.waiting_desc("<b>" + waiting + "</b>")); //$NON-NLS-1$ //$NON-NLS-2$

            Element invalidEl = detailPanel.getElementById(STAGING_AREA_INVALID);
            invalidEl
                    .setInnerHTML(messages
                            .invalid_desc(
                                    "<span id=\"open_invalid_record\" style=\"color:red; text-decoration:underline; cursor:pointer;\">", "<b>" + invalid + "</b>", "</span>")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            Element open_invalid_record = detailPanel.getElementById("open_invalid_record"); //$NON-NLS-1$
            addClickForRecord(2, open_invalid_record);

            Element validEl = detailPanel.getElementById(STAGING_AREA_VALID);
            validEl.setInnerHTML(messages
                    .valid_desc(
                            "<span id=\"open_valid_record\" style=\"color:green; text-decoration:underline; cursor:pointer;\">", "<b>" + valid + "</b>", "</span>")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            Element open_valid_record = detailPanel.getElementById("open_valid_record"); //$NON-NLS-1$
            addClickForRecord(3, open_valid_record);
        }
    }

    private native void addClickForRecord(int state, Element el)/*-{
                                                                var instance = this;
                                                                el.onclick = function() {
                                                                instance.@org.talend.mdm.webapp.stagingareacontrol.client.view.StagingContainerSummaryView::onOpenRecord(I)(state);
                                                                };
                                                                }-*/;

    void onOpenRecord(int state) {
        ControllerContainer.get().getSummaryController().openInvalidRecordToBrowseRecord(state);
    }

    public void refresh(StagingContainerModel stagingContainerModel) {
        this.stagingContainerModel = stagingContainerModel;
        initDetailPanel();
        updateChartData();
    }

    private void updateChartData() {
        int valid = stagingContainerModel.getValidRecords();
        int waiting = stagingContainerModel.getWaitingValidationRecords();
        int invalid = stagingContainerModel.getInvalidRecords();

        int total = valid + invalid + waiting;
        if (total == 0) {
            gaugesBar.reset();
            return;
        }
        double percentage = valid * 1D / total;
        NumberFormat format = NumberFormat.getFormat("#0.00"); //$NON-NLS-1$
        final double validPercentage = format.parse(format.format(valid * 100D / total));
        if (gaugesBar.getValue() < 1.0 || totalRecordCount != total) {
            gaugesBar.updateProgress(percentage, messages.percentage(valid, total, validPercentage));
        }
        totalRecordCount = total;
    }

    public StagingContainerModel getStagingContainerModel() {
        return stagingContainerModel;
    }

    public void setEnabledStartValidation(boolean enabled) {
        startValidate.setEnabled(enabled);
    }

    public boolean isEnabledStartValidation() {
        return startValidate.isEnabled();
    }

    public Button getStartValidateButton() {
        return startValidate;
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        ControllerContainer.get().getSummaryController().refreshView();
    }
}
