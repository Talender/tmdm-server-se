/*
 * Copyright (C) 2006-2016 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.storage.task.staging;

import com.amalto.core.storage.task.ConfigurableFilter;
import com.amalto.core.storage.task.DefaultFilter;
import com.amalto.core.storage.task.Filter;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.ws.rs.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.io.StringReader;
import java.util.List;

@Path(StagingTaskService.TASKS)
public class StagingTaskService {

    public static final String TASKS = "/tasks/staging"; //$NON-NLS-1$

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
    @Consumes("application/xml")
    public String startValidation(@PathParam("container") String dataContainer,
                                  @QueryParam("model") String dataModel,
                                  InputStream body) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Filter filter;
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            String content = IOUtils.toString(body);
            if (!content.isEmpty()) {
                Document doc = builder.parse(new InputSource(new StringReader(content)));
                filter = new ConfigurableFilter(doc);
            } else {
                filter = DefaultFilter.INSTANCE;
            }
        } catch (Exception e) {
            filter = DefaultFilter.INSTANCE;
            if (Logger.getLogger(StagingTaskService.class).isDebugEnabled()) {
                Logger.getLogger(StagingTaskService.class).debug("Ignored parse error for staging filter: ", e);
            }
        }
        return delegate.startValidation(dataContainer, dataModel, filter);
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
