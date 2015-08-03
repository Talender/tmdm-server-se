/*
 * Copyright (C) 2006-2015 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.storage.task.staging;

import java.util.List;

/**
 *
 */
public interface StagingTaskServiceDelegate {

    StagingContainerSummary getContainerSummary();

    String startValidation();

    StagingContainerSummary getContainerSummary(String dataContainer, String dataModel);

    String startValidation(String dataContainer, String dataModel);

    List<String> listCompletedExecutions(String dataContainer, String beforeDate, int start, int size);

    ExecutionStatistics getExecutionStats(String dataContainer, String dataModel, String executionId);

    ExecutionStatistics getCurrentExecutionStats(String dataContainer, String dataModel);

    void cancelCurrentExecution(String dataContainer, String dataModel);

}
