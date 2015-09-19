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
package org.talend.mdm.webapp.browserecords.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * DOC HSHU class global comment. Detailled comment
 */
public class AppHeader implements IsSerializable {

    private String datamodel = null;

    private String datacluster = null;

    private boolean isStandAloneMode = false;

    private int autoTextAreaLength;

    private boolean isAutoValidate = true;

    private boolean dataMigrationMultiLingualFieldAuto;

    private boolean isUseRelations = true;

    /**
     * DOC HSHU AppHeader constructor comment.
     */
    public AppHeader() {

    }

    public boolean isStandAloneMode() {
        return isStandAloneMode;
    }

    public void setStandAloneMode(boolean isStandAloneMode) {
        this.isStandAloneMode = isStandAloneMode;
    }

    public String getDatamodel() {
        return datamodel;
    }

    public void setDatamodel(String datamodel) {
        this.datamodel = datamodel;
    }

    public String getDatacluster() {
        return datacluster;
    }

    public void setDatacluster(String datacluster) {
        this.datacluster = datacluster;
    }

    public int getAutoTextAreaLength() {
        return autoTextAreaLength;
    }

    public void setAutoTextAreaLength(int autoTextAreaLength) {
        this.autoTextAreaLength = autoTextAreaLength;
    }

    public boolean isDataMigrationMultiLingualFieldAuto() {
        return dataMigrationMultiLingualFieldAuto;
    }

    public void setDataMigrationMultiLingualFieldAuto(boolean dataMigrationMultiLingualFieldAuto) {
        this.dataMigrationMultiLingualFieldAuto = dataMigrationMultiLingualFieldAuto;
    }

    @Override
    public String toString() {
        return "AppHeader [datamodel=" + datamodel + ", datacluster=" + datacluster + "]";//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$  
    }

    public boolean isAutoValidate() {
        return isAutoValidate;
    }

    public void setAutoValidate(boolean isAutoValidate) {
        this.isAutoValidate = isAutoValidate;
    }

    public boolean isUseRelations() {
        return isUseRelations;
    }

    public void setUseRelations(boolean isUseRelations) {
        this.isUseRelations = isUseRelations;
    }
}
