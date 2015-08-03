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

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

@Path(StagingTaskService.TASKS)
public class StagingTaskService {

    public static final String TASKS = "/tasks/staging";

    private final StagingTaskServiceDelegate delegate = new DefaultStagingTaskService();

    @GET
    @Path("/")
    public StagingContainerSummary getContainerSummary() {
        return delegate.getContainerSummary();
    }

    @POST
    @Path("/")
    public String startValidation() {
        return delegate.startValidation();
    }

    @GET
    @Path("{container}/")
    public StagingContainerSummary getContainerSummary(@PathParam("container") String dataContainer,
                                                       @QueryParam("model") String dataModel) {
        return delegate.getContainerSummary(dataContainer, dataModel);
    }

    @POST
    @Path("{container}/")
    public String startValidation(@PathParam("container") String dataContainer,
                                  @QueryParam("model") String dataModel) {
        return delegate.startValidation(dataContainer, dataModel);
    }

    @GET
    @Path("{container}/execs")
    public List<String> listCompletedTaskExecutions(@PathParam("container") String dataContainer,
            @QueryParam("before") String beforeDate,
                                                    @DefaultValue("1") @QueryParam("start") int start,
                                                    @DefaultValue("-1") @QueryParam("size") int size) {
        return SerializableList.create(delegate.listCompletedExecutions(dataContainer, beforeDate, start, size), "executions", "execution");
    }

    @GET
    @Path("{container}/execs/count")
    public int countCompletedTaskExecutions(@PathParam("container") String dataContainer,
                                            @QueryParam("before") String beforeDate) {
        return delegate.listCompletedExecutions(dataContainer, beforeDate, 1, -1).size();
    }

    @GET
    @Path("{container}/execs/current/")
    public ExecutionStatistics getCurrentExecutionStats(@PathParam("container") String dataContainer,
                                                        @QueryParam("model") String dataModel) {
        return delegate.getCurrentExecutionStats(dataContainer, dataModel);
    }

    @DELETE
    @Path("{container}/execs/current/")
    public void cancelCurrentExecution(@PathParam("container") String dataContainer,
                                       @QueryParam("model") String dataModel) {
        delegate.cancelCurrentExecution(dataContainer, dataModel);
    }

    @GET
    @Path("{container}/execs/{executionId}/")
    public ExecutionStatistics getExecutionStats(@PathParam("container") String dataContainer,
                                                 @QueryParam("model") String dataModel,
                                                 @PathParam("executionId") String executionId) {
        return delegate.getExecutionStats(dataContainer, dataModel, executionId);
    }
}
