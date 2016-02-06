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
package org.talend.mdm.webapp.browserecordsinstaging.client;

import org.talend.mdm.webapp.browserecords.client.ToolBarFactory;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.client.widget.ItemDetailToolBar;
import org.talend.mdm.webapp.browserecords.client.widget.ItemsDetailPanel;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;
import org.talend.mdm.webapp.browserecordsinstaging.client.widget.ItemDetailToolBar4Staging;

public class ToolBarFactory4Staging extends ToolBarFactory {

    @Override
    public ItemDetailToolBar createItemDetailToolBar(ItemBean itemBean, String operation, ViewBean viewBean,
            ItemsDetailPanel itemsDetailPanel) {
        return new ItemDetailToolBar4Staging(itemBean, operation, viewBean, itemsDetailPanel);
    }

    @Override
    public ItemDetailToolBar createItemDetailToolBar(ItemBean itemBean, String operation, ViewBean viewBean,
            ItemsDetailPanel itemsDetailPanel, boolean openTab) {
        return new ItemDetailToolBar4Staging(itemBean, operation, viewBean, itemsDetailPanel, openTab);
    }

    @Override
    public ItemDetailToolBar createItemDetailToolBar(ItemBean itemBean, String operation, boolean isFkToolBar, ViewBean viewBean,
            ItemsDetailPanel itemsDetailPanel) {
        return new ItemDetailToolBar4Staging(itemBean, operation, isFkToolBar, viewBean, itemsDetailPanel);
    }

    @Override
    public ItemDetailToolBar createItemDetailToolBar(ItemsDetailPanel itemsDetailPanel) {
        return new ItemDetailToolBar4Staging(itemsDetailPanel);
    }

}
