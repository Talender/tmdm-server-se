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
package org.talend.mdm.webapp.general.client.i18n;

import com.google.gwt.i18n.client.Messages;


public interface GeneralMessages extends Messages {

    String menus();
    
    String actions();
    
    String error();
    
    String data_container();
    
    String data_model();
    
    String save();
    
    String status();
    
    String status_msg_success();
    
    String status_msg_failure();
    
    String logout();
    
    String enterprise();
    
    String community();
    
    String othermenu();

    String edition();
    
    String connected_to();

    String application_undefined(String applicationName);

    String nocontainer();

    String nomodel();
}
