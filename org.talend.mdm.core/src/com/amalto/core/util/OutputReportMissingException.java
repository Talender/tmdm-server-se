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
package com.amalto.core.util;


/**
 * DOC talend2  class global comment. Detailled comment
 */
public class OutputReportMissingException extends RuntimeException {
    
    public OutputReportMissingException() {
        super();
    }

    public OutputReportMissingException(String message) {
        super(message);
    }

    public OutputReportMissingException(String message, Throwable cause) {
        super(message, cause);
    }

    public OutputReportMissingException(Throwable cause) {
        super(cause);
    }

}
