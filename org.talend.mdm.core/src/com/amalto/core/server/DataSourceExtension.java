/*
 * Copyright (C) 2006-2016 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package com.amalto.core.server;

import org.w3c.dom.Node;

import com.amalto.core.storage.datasource.DataSource;

/**
 * A datasource extension that can be implemented to provide additional
 * {@link com.amalto.core.storage.datasource.DataSource} implementation that are not part of the original MDM server.
 */
public interface DataSourceExtension {

    /**
     * @param type The value as present in "type" element of the datasource configuration file.
     * @return <code>true</code> if the <code>type</code> is supported by this datasource extension.
     */
    boolean accept(String type);

    /**
     * Parse the datasource type specific part and returns a {@link com.amalto.core.storage.datasource.DataSource
     * datasource}.
     * 
     *
     * @param datasourceNode The XML node in the datasource configuration file to parse.
     * @return A {@link com.amalto.core.storage.datasource.DataSource datasource} implementation.
     */
    DataSource create(Node datasourceNode);
}
