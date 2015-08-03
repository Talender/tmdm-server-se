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

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.metadata.MetadataRepository;
import com.amalto.core.query.user.OrderBy;
import com.amalto.core.query.user.UserQueryBuilder;
import com.amalto.core.query.user.UserStagingQueryBuilder;
import com.amalto.core.save.DefaultCommitter;
import com.amalto.core.save.context.DefaultSaverSource;
import com.amalto.core.server.Server;
import com.amalto.core.server.ServerContext;
import com.amalto.core.server.StorageAdmin;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageResults;
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.storage.task.StagingConfiguration;
import com.amalto.core.storage.task.StagingConstants;
import com.amalto.core.storage.task.Task;
import com.amalto.core.storage.task.TaskFactory;
import com.amalto.core.storage.task.TaskSubmitterFactory;
import com.amalto.core.util.Util;

import static com.amalto.core.query.user.UserQueryBuilder.*;

public class DefaultStagingTaskService implements StagingTaskServiceDelegate {

    private static final String EXECUTION_LOG_TYPE = "TALEND_TASK_EXECUTION"; //$NON-NLS-1$

    // SimpleDateFormat is not thread-safe
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"); //$NON-NLS-1$

    // SimpleDateFormat is not thread-safe
    private static final DateFormat userDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); //$NON-NLS-1$

    private static final Map<String, Task> runningTasks = new HashMap<String, Task>();

    public DefaultStagingTaskService() {
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    public StagingContainerSummary getContainerSummary() {
        String dataContainer;
        String dataModel;
        try {
            dataContainer = Util.getUserDataCluster();
            dataModel = Util.getUserDataModel();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return getContainerSummary(dataContainer, dataModel);
    }

    public String startValidation() {
        String dataContainer;
        String dataModel;
        try {
            dataContainer = Util.getUserDataCluster();
            dataModel = Util.getUserDataModel();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return startValidation(dataContainer, dataModel);
    }

    public StagingContainerSummary getContainerSummary(String dataContainer, String dataModel) {
        Server server = ServerContext.INSTANCE.get();
        Storage storage = server.getStorageAdmin().get(dataContainer + StorageAdmin.STAGING_SUFFIX, null);
        if (storage == null) {
            throw new IllegalStateException("No staging storage available for container '" + dataContainer + "'.");
        }
        MetadataRepository repository = storage.getMetadataRepository();
        int allRecords = countAllInstancesByStatus(storage, repository);
        int newRecords = countInstancesByStatus(storage, repository, StagingConstants.NEW);
        int validRecords = countInstancesByStatus(storage, repository, true);
        int invalidRecords = countInstancesByStatus(storage, repository, false);

        StagingContainerSummary containerStagingSummary = new StagingContainerSummary();
        containerStagingSummary.setTotalRecord(allRecords);
        containerStagingSummary.setWaitingForValidation(newRecords);
        containerStagingSummary.setValidRecords(validRecords);
        containerStagingSummary.setInvalidRecords(invalidRecords);
        containerStagingSummary.setDataContainer(dataContainer);
        containerStagingSummary.setDataModel(dataModel);

        return containerStagingSummary;
    }

    public String startValidation(String dataContainer, String dataModel) {
        synchronized (runningTasks) {
            Task runningTask = runningTasks.get(dataContainer);
            if (runningTask != null) {
                return runningTask.getId();
            }
            Server server = ServerContext.INSTANCE.get();
            Storage staging = server.getStorageAdmin().get(dataContainer + StorageAdmin.STAGING_SUFFIX, null);
            if (staging == null) {
                throw new IllegalStateException("No staging storage available for container '" + dataContainer + "'.");
            }
            Storage user = server.getStorageAdmin().get(dataContainer, null);
            if (user == null) {
                throw new IllegalStateException("No staging storage available for container '" + dataContainer + "'.");
            }
            MetadataRepository stagingRepository = staging.getMetadataRepository();
            MetadataRepository userRepository = user.getMetadataRepository();
            StagingConfiguration stagingConfig = new StagingConfiguration(staging,
                    stagingRepository,
                    userRepository,
                    new DefaultSaverSource(),
                    new DefaultCommitter(),
                    user);
            Task stagingTask = TaskFactory.createStagingTask(stagingConfig);
            TaskSubmitterFactory.getSubmitter().submit(stagingTask);
            runningTasks.put(dataContainer, stagingTask);
            return stagingTask.getId();
        }
    }

    public List<String> listCompletedExecutions(String dataContainer, String beforeDate, int start, int size) {
        Server server = ServerContext.INSTANCE.get();
        Storage staging = server.getStorageAdmin().get(dataContainer + StorageAdmin.STAGING_SUFFIX, null);
        if (staging == null) {
            throw new IllegalStateException("No staging storage available for container '" + dataContainer + "'.");
        }
        MetadataRepository stagingRepository = staging.getMetadataRepository();
        ComplexTypeMetadata executionType = stagingRepository.getComplexType(EXECUTION_LOG_TYPE);
        UserQueryBuilder qb = from(executionType)
                .select(executionType.getField("id")) //$NON-NLS-1$
                .where(eq(executionType.getField("completed"), "true")); //$NON-NLS-1$ //$NON-NLS-2$
        if (beforeDate != null && beforeDate.trim().length() > 0) {
            synchronized (userDateFormat) {
                try {
                    long beforeTime = userDateFormat.parse(beforeDate).getTime();
                    qb.where(lte(executionType.getField("start_time"), String.valueOf(beforeTime))); //$NON-NLS-1$
                } catch (ParseException e) {
                    throw new RuntimeException("Could not parse '" + beforeDate + "' as date.", e);
                }
            }
        }
        if (start >= 0) {
            qb.start(start);
        }
        if (size >= 0) {
            qb.limit(size);
        }
        qb.orderBy(executionType.getField("start_time"), OrderBy.Direction.ASC); //$NON-NLS-1$
        List<String> taskIds;
        StorageResults results = staging.fetch(qb.getSelect());
        try {
            if (size > 0) {
                taskIds = new ArrayList<String>(size);
            } else {
                taskIds = new LinkedList<String>();
            }
            for (DataRecord result : results) {
                taskIds.add(String.valueOf(result.get("id"))); //$NON-NLS-1$
            }
        } finally {
            results.close();
        }
        return taskIds;
    }

    public ExecutionStatistics getCurrentExecutionStats(String dataContainer, String dataModel) {
        synchronized (runningTasks) {
            Task task = runningTasks.get(dataContainer);
            if (task == null) {
                return null; // This is a way to say "no current running validation task".
            } else if (task.hasFinished()) {
                runningTasks.remove(dataContainer);
                return null;
            }
            ExecutionStatistics status = new ExecutionStatistics();
            status.setId(task.getId());
            status.setProcessedRecords(task.getProcessedRecords());
            status.setTotalRecords(task.getRecordCount());
            status.setInvalidRecords(task.getErrorCount());
            long elapsedTime = System.currentTimeMillis() - task.getStartDate();
            String formattedElapsedTime = formatElapsedTime(elapsedTime);
            status.setRunningTime(formattedElapsedTime);
            long timeLeft = (long) ((task.getRecordCount() - task.getProcessedRecords()) / task.getPerformance());
            synchronized (dateFormat) {
                status.setStartDate(dateFormat.format(new Date(task.getStartDate())));
                long endTime = (System.currentTimeMillis() + timeLeft);
                status.setEndDate(dateFormat.format(new Date(endTime)));
            }
            return status;
        }
    }

    private static String formatElapsedTime(long elapsedTime) {
        return String.format("%d:%02d:%02d", elapsedTime / 3600, (elapsedTime % 3600) / 60, (elapsedTime % 60)); //$NON-NLS-1$
    }

    public void cancelCurrentExecution(String dataContainer, String dataModel) {
        synchronized (runningTasks) {
            Task task = runningTasks.get(dataContainer);
            if (task == null) {
                return; // No running task, simply ignore call.
            }
            task.cancel();
            runningTasks.remove(dataContainer);
        }
    }

    public ExecutionStatistics getExecutionStats(String dataContainer, String dataModel, String executionId) {
        Server server = ServerContext.INSTANCE.get();
        Storage staging = server.getStorageAdmin().get(dataContainer + StorageAdmin.STAGING_SUFFIX, null);
        if (staging == null) {
            throw new IllegalStateException("No staging storage available for container '" + dataContainer + "'.");
        }
        // TMDM-4827: Returns current execution if execution id is current execution.
        synchronized (runningTasks) {
            Task declaredTask = runningTasks.get(dataContainer);
            if (declaredTask != null && declaredTask.getId().equals(executionId)) {
                return getCurrentExecutionStats(dataContainer, dataModel);
            }
        }
        MetadataRepository stagingRepository = staging.getMetadataRepository();
        ComplexTypeMetadata executionType = stagingRepository.getComplexType(EXECUTION_LOG_TYPE);
        UserQueryBuilder qb = from(executionType)
                .where(eq(executionType.getField("id"), executionId)); //$NON-NLS-1$
        StorageResults results = staging.fetch(qb.getSelect());
        ExecutionStatistics status = new ExecutionStatistics();
        try {
            for (DataRecord result : results) {
                status.setId(String.valueOf(result.get("id"))); //$NON-NLS-1$
                Date start_time = new Date((Long) result.get("start_time")); //$NON-NLS-1$
                Date end_time = new Date((Long) result.get("end_time")); //$NON-NLS-1$
                synchronized (dateFormat) {
                    status.setStartDate(dateFormat.format(start_time)); //$NON-NLS-1$
                    status.setEndDate(dateFormat.format(end_time)); //$NON-NLS-1$
                }
                status.setInvalidRecords(((BigDecimal) result.get("error_count")).intValue()); //$NON-NLS-1$
                status.setRunningTime(formatElapsedTime(end_time.getTime() - start_time.getTime()));
                int recordCount = ((BigDecimal) result.get("record_count")).intValue(); //$NON-NLS-1$
                status.setProcessedRecords(recordCount);
                status.setTotalRecords(recordCount);
            }
        } finally {
            results.close();
        }
        return status;
    }

    private static int countAllInstancesByStatus(Storage storage, MetadataRepository repository) {
        int totalCount = 0;
        for (ComplexTypeMetadata currentType : repository.getUserComplexTypes()) {
            if (currentType.isInstantiable() && !EXECUTION_LOG_TYPE.equals(currentType.getName())) {  //$NON-NLS-1$
                UserQueryBuilder qb = from(currentType)
                        .select(alias(UserQueryBuilder.count(), "count")); //$NON-NLS-1$
                StorageResults results = storage.fetch(qb.getSelect());
                try {
                    for (DataRecord result : results) {
                        totalCount += (Long) result.get("count"); //$NON-NLS-1$
                    }
                } finally {
                    results.close();
                }
            }
        }
        return totalCount;
    }

    private static int countInstancesByStatus(Storage storage, MetadataRepository repository, String status) {
        int totalCount = 0;
        for (ComplexTypeMetadata currentType : repository.getUserComplexTypes()) {
            if (currentType.isInstantiable() && !EXECUTION_LOG_TYPE.equals(currentType.getName())) {
                UserQueryBuilder qb = from(currentType)
                        .select(alias(UserQueryBuilder.count(), "count")); //$NON-NLS-1$
                if (StagingConstants.NEW.equals(status)) {
                    qb.where(or(eq(UserStagingQueryBuilder.status(), status), isNull(UserStagingQueryBuilder.status())));
                } else {
                    qb.where(eq(UserStagingQueryBuilder.status(), status));
                }
                StorageResults results = storage.fetch(qb.getSelect());
                try {
                    for (DataRecord result : results) {
                        totalCount += (Long) result.get("count"); //$NON-NLS-1$
                    }
                } finally {
                    results.close();
                }
            }
        }
        return totalCount;
    }

    private static int countInstancesByStatus(Storage storage, MetadataRepository repository, boolean valid) {
        int totalCount = 0;
        for (ComplexTypeMetadata currentType : repository.getUserComplexTypes()) {
            if (currentType.isInstantiable() && !EXECUTION_LOG_TYPE.equals(currentType.getName())) {
                UserQueryBuilder qb = from(currentType)
                        .select(alias(UserQueryBuilder.count(), "count")); //$NON-NLS-1$
                if (valid) {
                    qb.where(eq(UserStagingQueryBuilder.status(), StagingConstants.SUCCESS_VALIDATE));
                } else {
                    qb.where(gte(UserStagingQueryBuilder.status(), StagingConstants.FAIL));
                }

                StorageResults results = storage.fetch(qb.getSelect());
                try {
                    for (DataRecord result : results) {
                        totalCount += (Long) result.get("count"); //$NON-NLS-1$
                    }
                } finally {
                    results.close();
                }
            }
        }
        return totalCount;
    }
}
