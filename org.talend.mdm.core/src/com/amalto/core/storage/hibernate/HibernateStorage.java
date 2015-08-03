/*
 * Copyright (C) 2006-2015 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package com.amalto.core.storage.hibernate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import net.sf.ehcache.CacheManager;

import org.apache.log4j.Logger;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.PropertyValueException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.search.MassIndexer;
import org.hibernate.search.Search;
import org.hibernate.search.event.ContextHolder;
import org.hibernate.search.impl.SearchFactoryImpl;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.hibernate.tool.hbm2ddl.SchemaValidator;
import org.talend.mdm.commmon.util.core.MDMConfiguration;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.metadata.ContainedComplexTypeMetadata;
import com.amalto.core.metadata.ContainedTypeFieldMetadata;
import com.amalto.core.metadata.DefaultMetadataVisitor;
import com.amalto.core.metadata.EnumerationFieldMetadata;
import com.amalto.core.metadata.FieldMetadata;
import com.amalto.core.metadata.MetadataRepository;
import com.amalto.core.metadata.MetadataUtils;
import com.amalto.core.metadata.ReferenceFieldMetadata;
import com.amalto.core.metadata.SimpleTypeFieldMetadata;
import com.amalto.core.metadata.TypeMetadata;
import com.amalto.core.query.optimization.ConfigurableContainsOptimizer;
import com.amalto.core.query.optimization.ContainsOptimizer;
import com.amalto.core.query.optimization.Optimizer;
import com.amalto.core.query.optimization.RangeOptimizer;
import com.amalto.core.query.optimization.UpdateReportOptimizer;
import com.amalto.core.query.user.Expression;
import com.amalto.core.query.user.Select;
import com.amalto.core.query.user.UserQueryDumpConsole;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageResults;
import com.amalto.core.storage.StorageType;
import com.amalto.core.storage.datasource.DataSource;
import com.amalto.core.storage.datasource.RDBMSDataSource;
import com.amalto.core.storage.prepare.FullTextIndexCleaner;
import com.amalto.core.storage.prepare.JDBCStorageCleaner;
import com.amalto.core.storage.prepare.JDBCStorageInitializer;
import com.amalto.core.storage.prepare.StorageCleaner;
import com.amalto.core.storage.prepare.StorageInitializer;
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.storage.record.DataRecordConverter;
import com.amalto.core.storage.record.metadata.DataRecordMetadata;

public class HibernateStorage implements Storage {

    public static final HibernateStorage.LocalEntityResolver ENTITY_RESOLVER = new HibernateStorage.LocalEntityResolver();

    private static final String CLASS_LOADER = "com.amalto.core.storage.hibernate.DefaultStorageClassLoader"; //$NON-NLS-1$

    private static final String ALTERNATE_CLASS_LOADER = "com.amalto.core.storage.hibernate.FullStorageClassLoader"; //$NON-NLS-1$

    private static final Logger LOGGER = Logger.getLogger(HibernateStorage.class);

    private static final Optimizer[] OPTIMIZERS = new Optimizer[] { new RangeOptimizer(), // Transforms (value > n AND
                                                                                          // value < p) into
                                                                                          // (RANGE(n,p)).
            new ContainsOptimizer(), // Transforms all '*' in CONTAINS into '%'.
            new UpdateReportOptimizer() // Adds queries on super types if update report query a concept name with super
                                        // types.
    };

    private static final String FORBIDDEN_PREFIX = "x_talend_"; //$NON-NLS-1$

    private static final MetadataChecker METADATA_CHECKER = new MetadataChecker();

    // Default value is "true" (meaning the storage will try to create database if it doesn't exist).
    private static final boolean autoPrepare = Boolean.valueOf(MDMConfiguration.getConfiguration().getProperty(
            "db.autoPrepare", "true")); //$NON-NLS-1$ //$NON-NLS-2$

    private final String storageName;

    private final StorageType storageType;

    private MappingRepository mappingRepository;

    private InternalRepository typeMappingRepository;

    private ClassCreator hibernateClassCreator;

    private StorageClassLoader storageClassLoader;

    private boolean isPrepared = false;

    private SessionFactory factory;

    private Configuration configuration;

    private RDBMSDataSource dataSource;

    private MetadataRepository userMetadataRepository;

    private TableResolver tableResolver;

    private int batchSize;

    public final ThreadLocal<Boolean> isPerformingDelete = new ThreadLocal<Boolean>() {

        @Override
        protected Boolean initialValue() {
            return false;
        }
    };

    /**
     * Create a {@link StorageType#MASTER} storage.
     * 
     * @param storageName Name for this storage. <b>by convention</b>, this is the MDM container name.
     * @see StorageType#MASTER
     */
    public HibernateStorage(String storageName) {
        this(storageName, StorageType.MASTER);
    }

    /**
     * @param storageName Name for this storage. <b>By convention</b>, this is the MDM container name.
     * @param type Tells whether this storage is a staging area or not.
     * @see StorageType
     */
    public HibernateStorage(String storageName, StorageType type) {
        this.storageName = storageName;
        this.storageType = type;
    }

    @Override
    public void init(DataSource dataSource) {
        // Stateless components
        if (dataSource == null) {
            throw new IllegalArgumentException("Data source named '" + dataSource + "' does not exist.");
        }
        if (!(dataSource instanceof RDBMSDataSource)) {
            throw new IllegalArgumentException("Data source is expected to be a RDBMS data source.");
        }
        this.dataSource = (RDBMSDataSource) dataSource;
        internalInit();
    }

    private void internalInit() {
        if (!dataSource.supportFullText()) {
            LOGGER.warn("Storage '" + storageName + "' (" + storageType + ") is not configured to support full text queries.");
        }
        configuration = new Configuration();
        // Setting our own entity resolver allows to ensure the DTD found/used are what we expect (and not potentially
        // one provided by the application server).
        configuration.setEntityResolver(ENTITY_RESOLVER);
    }

    @Override
    public synchronized void prepare(MetadataRepository repository, Set<FieldMetadata> indexedFields, boolean force,
            boolean dropExistingData) {
        if (!force && isPrepared) {
            return; // No op operation
        }
        if (isPrepared) {
            close();
            internalInit();
        }
        if (dataSource == null) {
            throw new IllegalArgumentException("Datasource is not set.");
        }
        // No support for data models including inheritance AND for g* XSD simple types AND fields that start with
        // X_TALEND_
        try {
            MetadataUtils.sortTypes(repository); // Do a "sort" to ensure there's no cyclic dependency.
            repository.accept(METADATA_CHECKER);
            userMetadataRepository = repository;
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred during unsupported features check.", e);
        }
        // Create class loader for storage's dynamically created classes.
        ClassLoader contextClassLoader = HibernateStorage.class.getClassLoader();
        Class<? extends StorageClassLoader> clazz;
        try {
            try {
                clazz = (Class<? extends StorageClassLoader>) Class.forName(ALTERNATE_CLASS_LOADER);
            } catch (ClassNotFoundException e) {
                clazz = (Class<? extends StorageClassLoader>) Class.forName(CLASS_LOADER);
            }
            Constructor<? extends StorageClassLoader> constructor = clazz.getConstructor(ClassLoader.class, String.class,
                    RDBMSDataSource.DataSourceDialect.class, StorageType.class);
            storageClassLoader = constructor.newInstance(contextClassLoader, storageName, dataSource.getDialectName(),
                    storageType);
            storageClassLoader.setDataSourceConfiguration(dataSource);
            storageClassLoader.generateHibernateConfig(); // Checks if configuration can be generated.
        } catch (Exception e) {
            throw new RuntimeException("Could not create storage class loader", e);
        }
        if (dropExistingData) {
            LOGGER.info("Cleaning existing database content.");
            StorageCleaner cleaner = new JDBCStorageCleaner(new FullTextIndexCleaner());
            cleaner.clean(this);
        } else {
            LOGGER.info("*NOT* cleaning existing database content.");
        }
        if (autoPrepare) {
            LOGGER.info("Preparing database before schema generation.");
            StorageInitializer initializer = new JDBCStorageInitializer();
            if (initializer.supportInitialization(this)) {
                if (!initializer.isInitialized(this)) {
                    initializer.initialize(this);
                } else {
                    LOGGER.info("Database is already prepared.");
                }
            } else {
                LOGGER.info("Datasource is not configured for automatic initialization.");
            }
        } else {
            LOGGER.info("*NOT* preparing database before schema generation.");
        }
        try {
            Thread.currentThread().setContextClassLoader(storageClassLoader);
            // Mapping of data model types to RDBMS (i.e. 'flatten' representation of types).
            MetadataRepository internalRepository;
            try {
                InternalRepository typeEnhancer = getTypeEnhancer();
                internalRepository = userMetadataRepository.accept(typeEnhancer);
                mappingRepository = typeEnhancer.getMappings();
            } catch (Exception e) {
                throw new RuntimeException("Exception occurred during type mapping creation.", e);
            }
            // Set fields to be indexed in database.
            Set<FieldMetadata> databaseIndexedFields = new HashSet<FieldMetadata>();
            for (FieldMetadata indexedField : indexedFields) {
                // TMDM-5311: Don't index TEXT fields
                TypeMetadata indexedFieldType = indexedField.getType();
                if (!isIndexable(indexedFieldType)) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Ignore index on field '" + indexedField.getName() + "' because value is stored in TEXT.");
                    }
                    continue;
                }
                // Go up the containment tree in case containing type is anonymous.
                ComplexTypeMetadata containingType = indexedField.getContainingType();
                while (containingType instanceof ContainedComplexTypeMetadata) {
                    containingType = ((ContainedComplexTypeMetadata) containingType).getContainerType();
                }
                TypeMapping mapping = mappingRepository.getMappingFromUser(containingType);
                FieldMetadata database = mapping.getDatabase(indexedField);
                if (database == null) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Ignore index on field '" + indexedField.getName()
                                + "' because it is an anonymous contained type field.");
                    }
                    continue;
                }
                if (!isIndexable(database.getType())) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Ignore index on field '" + indexedField.getName()
                                + "' because value (in database mapping) is stored in TEXT.");
                    }
                    continue; // Don't take into indexed fields long text fields
                }
                databaseIndexedFields.add(database);
            }
            // Set table/column name length limitation
            switch (dataSource.getDialectName()) {
            case ORACLE_10G:
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Oracle database is being used. Limit table name length to 30.");
                }
                tableResolver = new StorageTableResolver(databaseIndexedFields, 30);
                break;
            case MYSQL:
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("MySQL database is being used. Limit table name length to 64.");
                }
                tableResolver = new StorageTableResolver(databaseIndexedFields, 64);
                break;
            case SQL_SERVER:
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("SQL Server database is being used. Limit table name length to 128.");
                }
                tableResolver = new StorageTableResolver(databaseIndexedFields, 128);
                break;
            case POSTGRES:
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Postgres database is being used. Limit table name length to 63.");
                }
                tableResolver = new StorageTableResolver(databaseIndexedFields, 63);
                break;
            case H2:
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("No limitation for table name length.");
                }
                tableResolver = new StorageTableResolver(databaseIndexedFields);
                break;
            }
            storageClassLoader.setTableResolver(tableResolver);
            // Master and Staging share same class creator.
            switch (storageType) {
            case MASTER:
            case STAGING:
                hibernateClassCreator = new ClassCreator(storageClassLoader);
                break;
            }
            // Create Hibernate classes (after some modifications to the types).
            try {
                internalRepository.accept(hibernateClassCreator);
            } catch (Exception e) {
                throw new RuntimeException("Exception occurred during dynamic classes creation.", e);
            }
            // Last step: configuration of Hibernate
            try {
                // Hibernate needs to have dynamic classes in context class loader during configuration.
                InputStream ehCacheConfig = storageClassLoader.getResourceAsStream(StorageClassLoader.EHCACHE_XML_CONFIG);
                if (ehCacheConfig != null) {
                    CacheManager.create(ehCacheConfig);
                }
                configuration.configure(StorageClassLoader.HIBERNATE_CONFIG);
                batchSize = Integer.parseInt(configuration.getProperty("hibernate.jdbc.batch_size")); //$NON-NLS-1$
                // Logs DDL *before* initialization in case initialization (useful for debugging).
                if (LOGGER.isTraceEnabled()) {
                    traceDDL();
                }
                // Customize schema generation according to datasource content.
                RDBMSDataSource.SchemaGeneration schemaGeneration = dataSource.getSchemaGeneration();
                List exceptions = Collections.emptyList();
                switch (schemaGeneration) {
                case CREATE:
                    SchemaExport schemaExport = new SchemaExport(configuration);
                    schemaExport.create(false, true);
                    // Exception may happen during recreation (hibernate may perform statements on tables that does
                    // not exist): these exceptions are supposed to be harmless (but log them to DEBUG just in case).
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Exception(s) occurred during schema creation:");
                        for (Object exceptionObject : schemaExport.getExceptions()) {
                            LOGGER.debug(((Exception) exceptionObject).getMessage());
                        }
                    }
                    break;
                case VALIDATE:
                    SchemaValidator schemaValidator = new SchemaValidator(configuration);
                    schemaValidator.validate(); // This is supposed to throw exception on validation issue.
                    break;
                case UPDATE:
                    SchemaUpdate schemaUpdate = new SchemaUpdate(configuration);
                    schemaUpdate.execute(false, true);
                    exceptions = schemaUpdate.getExceptions();
                    break;
                }
                // Throw an exception if schema update met issue(s).
                if (!exceptions.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Could not prepare database schema: ");
                    Iterator iterator = exceptions.iterator();
                    while (iterator.hasNext()) {
                        Exception exception = (Exception) iterator.next();
                        if (exception instanceof SQLException) {
                            SQLException currentSQLException = (SQLException) exception;
                            while (currentSQLException != null) {
                                sb.append(currentSQLException.getMessage());
                                sb.append('\n');
                                currentSQLException = currentSQLException.getNextException();
                            }
                        } else if (exception != null) {
                            sb.append(exception.getMessage());
                        }
                        if (iterator.hasNext()) {
                            sb.append('\n');
                        }
                    }
                    throw new IllegalStateException(sb.toString());
                }
                // This method is deprecated but using a 4.1+ hibernate initialization, Hibernate Search can't be
                // started
                // (wait for Hibernate Search 4.1 to be ready before considering changing this).
                factory = configuration.buildSessionFactory();
            } catch (Exception e) {
                throw new RuntimeException("Exception occurred during Hibernate initialization.", e);
            }
            // All set: set prepared flag to true.
            isPrepared = true;
            LOGGER.info("Storage '" + storageName + "' (" + storageType + ") is ready.");
        } catch (Throwable t) {
            try {
                // This prevent PermGen OOME in case of multiple failures to start.
                close();
            } catch (Exception e) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Error occurred during clean up following failed prepare", e);
                }
            }
            throw new RuntimeException("Could not prepare '" + storageName + "'.", t);
        } finally {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }
    }

    private static boolean isIndexable(TypeMetadata fieldType) {
        if ("MULTI_LINGUAL".equals(fieldType.getName())) { //$NON-NLS-1$
            return false;
        }
        if (fieldType.getData(MetadataRepository.DATA_MAX_LENGTH) != null) {
            Object maxLength = fieldType.getData(MetadataRepository.DATA_MAX_LENGTH);
            if (maxLength != null && Integer.parseInt(String.valueOf(maxLength)) > MappingGenerator.MAX_VARCHAR_TEXT_LIMIT) {
                return false; // Don't take into indexed fields long text fields
            }
        }
        return true;
    }

    private void traceDDL() {
        try {
            if (configuration == null) {
                throw new IllegalStateException("Expect a Hibernate configuration to be set.");
            }
            String jbossServerTempDir = System.getProperty("java.io.tmpdir"); //$NON-NLS-1$
            RDBMSDataSource.DataSourceDialect dialectType = dataSource.getDialectName();
            SchemaExport export = new SchemaExport(configuration);
            export.setFormat(false);
            export.setOutputFile(jbossServerTempDir + File.separator + storageName
                    + "_" + storageType + "_" + dialectType + ".ddl"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            export.setDelimiter(";"); //$NON-NLS-1$
            export.execute(false, false, false, true);
            if (export.getExceptions().size() > 0) {
                for (int i = 0; i < export.getExceptions().size(); i++) {
                    LOGGER.error("Error occurred while producing ddl.",//$NON-NLS-1$
                            (Exception) export.getExceptions().get(i));
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error occurred while producing ddl.", e); //$NON-NLS-1$
        }
    }

    public InternalRepository getTypeEnhancer() {
        if (typeMappingRepository == null) {
            switch (storageType) {
            case MASTER:
                typeMappingRepository = new UserTypeMappingRepository();
                break;
            case STAGING:
                typeMappingRepository = new StagingTypeMappingRepository();
                break;
            default:
                throw new IllegalArgumentException("Storage type '" + storageType + "' is not supported.");
            }
        }
        return typeMappingRepository;
    }

    @Override
    public synchronized void prepare(MetadataRepository repository, boolean dropExistingData) {
        if (!isPrepared) {
            prepare(repository, Collections.<FieldMetadata> emptySet(), false, dropExistingData);
        }
    }

    @Override
    public MetadataRepository getMetadataRepository() {
        if (!isPrepared) {
            throw new IllegalStateException("Storage '" + storageName + "' has not been prepared.");
        }
        return userMetadataRepository;
    }

    @Override
    public StorageResults fetch(Expression userQuery) {
        assertPrepared();
        final ClassLoader previousClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(storageClassLoader);

            final Session session = factory.getCurrentSession();
            final Transaction transaction = session.getTransaction();
            if (!transaction.isActive()) {
                // Implicitly start a transaction
                transaction.begin();
            }
            // Call back closes session once calling code has consumed all results.
            Set<EndOfResultsCallback> callbacks;
            if (isPerformingDelete.get()) {
                callbacks = Collections.emptySet();
            } else {
                callbacks = Collections.<EndOfResultsCallback> singleton(new EndOfResultsCallback() {

                    @Override
                    public void onEndOfResults() {
                        if (!isPerformingDelete.get()) {
                            if (transaction.isActive()) {
                                transaction.commit();
                            } else {
                                if (LOGGER.isDebugEnabled()) {
                                    LOGGER.debug("Attempted to close session on end of query result, but it has already been done.");
                                }
                            }
                            Thread.currentThread().setContextClassLoader(previousClassLoader);
                        }
                    }
                });
            }
            return internalFetch(session, userQuery, callbacks);
        } catch (Exception e) {
            Thread.currentThread().setContextClassLoader(previousClassLoader);
            throw new RuntimeException("Exception occurred during fetch operation", e);
        }
    }

    @Override
    public void update(DataRecord record) {
        update(Collections.singleton(record));
    }

    @Override
    public void update(Iterable<DataRecord> records) {
        assertPrepared();
        ClassLoader previousClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(storageClassLoader);

            Session session = factory.getCurrentSession();
            DataRecordConverter<Object> converter = new ObjectDataRecordConverter(storageClassLoader, session);
            for (DataRecord currentDataRecord : records) {
                TypeMapping mapping = mappingRepository.getMappingFromUser(currentDataRecord.getType());
                Wrapper o = (Wrapper) currentDataRecord.convert(converter, mapping);
                if (session.contains(o) && session.isReadOnly(o)) { // A read only instance for an update?
                    session.setReadOnly(o, false);
                }
                o.timestamp(System.currentTimeMillis());
                DataRecordMetadata recordMetadata = currentDataRecord.getRecordMetadata();
                o.taskId(recordMetadata.getTaskId());
                Map<String, String> recordProperties = recordMetadata.getRecordProperties();
                for (Map.Entry<String, String> currentProperty : recordProperties.entrySet()) {
                    String key = currentProperty.getKey();
                    String value = currentProperty.getValue();
                    ComplexTypeMetadata database = mapping.getDatabase();
                    if (database.hasField(key)) {
                        Object convertedValue = MetadataUtils.convert(value, database.getField(key));
                        o.set(key, convertedValue);
                    } else {
                        throw new IllegalArgumentException("Can not store value '" + key
                                + "' because there is no database field '" + key + "' in type '" + mapping.getName() + "'");
                    }
                }
                if (session.getStatistics().getEntityCount() % batchSize == 0) {
                    // Periodically flush objects to avoid using too much memory.
                    session.flush();
                    session.clear();
                }
                session.saveOrUpdate(o);
            }
        } catch (ConstraintViolationException e) {
            throw new com.amalto.core.storage.exception.ConstraintViolationException(e);
        } catch (PropertyValueException e) {
            throw new RuntimeException("Invalid value in record to update.", e);
        } catch (NonUniqueObjectException e) {
            throw new RuntimeException("Attempted to update multiple times same record within same transaction.", e);
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred during update.", e);
        } finally {
            Thread.currentThread().setContextClassLoader(previousClassLoader);
        }
    }

    @Override
    public void begin() {
        assertPrepared();
        Session session = factory.getCurrentSession();
        session.beginTransaction();
        session.setFlushMode(FlushMode.AUTO);
    }

    @Override
    public void commit() {
        assertPrepared();
        Session session = factory.getCurrentSession();
        try {
            Transaction transaction = session.getTransaction();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("[" + this + "] Transaction #" + transaction.hashCode() + " -> Commit "
                        + session.getStatistics().getEntityCount() + " record(s).");
            }
            if (!transaction.isActive()) {
                throw new IllegalStateException("Can not commit transaction, no transaction is active.");
            }
            try {
                if (!transaction.wasCommitted()) {
                    transaction.commit();
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("[" + this + "] Transaction #" + transaction.hashCode() + " -> Commit done.");
                    }
                } else {
                    LOGGER.warn("Transaction was already committed.");
                }
            } catch (ConstraintViolationException e) {
                throw new com.amalto.core.storage.exception.ConstraintViolationException(e);
            }
        } finally {
            if (session.isOpen() && session.getTransaction().isActive()) {
                session.clear(); // TMDM-6192: Evicts cache in case session is reused without being closed.
            }
        }
    }

    @Override
    public void rollback() {
        assertPrepared();
        Session session = factory.getCurrentSession();
        Transaction transaction = session.getTransaction();
        if (!transaction.isActive()) {
            LOGGER.warn("Can not rollback transaction, no transaction is active.");
            return;
        }
        session.clear();
        if (!transaction.wasRolledBack()) {
            transaction.rollback();
        } else {
            LOGGER.warn("Transaction was already rollbacked.");
        }
    }

    @Override
    public synchronized void end() {
        assertPrepared();
        Session session = factory.getCurrentSession();
        if (session.getTransaction().isActive()) {
            LOGGER.warn("Current session has not been ended by either a commit or a rollback. Rolling back transaction.");
            session.getTransaction().rollback();
            if (session.isOpen()) {
                session.close();
                session.disconnect();
            }
        }
    }

    @Override
    public void reindex() {
        Session session = factory.getCurrentSession();
        MassIndexer indexer = Search.getFullTextSession(session).createIndexer();
        indexer.optimizeOnFinish(true);
        indexer.optimizeAfterPurge(true);
        try {
            session.getTransaction().begin();
            indexer.threadsForSubsequentFetching(2);
            indexer.threadsToLoadObjects(2);
            indexer.batchSizeToLoadObjects(batchSize);
            indexer.startAndWait();
            session.getTransaction().commit();
        } catch (InterruptedException e) {
            session.getTransaction().rollback();
            throw new RuntimeException(e);
        }
    }

    @Override
    public Set<String> getFullTextSuggestion(String keyword, FullTextSuggestion mode, int suggestionSize) {
        // TODO Need Lucene 3.0+ to implement this.
        /*
         * FullTextSession fullTextSession = Search.getFullTextSession(factory.getCurrentSession()); SearchFactory
         * searchFactory = fullTextSession.getSearchFactory();
         * 
         * Collection<ComplexTypeMetadata> complexTypes = internalRepository.getUserComplexTypes(); Set<String> fields =
         * new HashSet<String>(); List<DirectoryProvider> directoryProviders = new LinkedList<DirectoryProvider>(); for
         * (ComplexTypeMetadata complexType : complexTypes) { for (FieldMetadata fieldMetadata :
         * complexType.getFields()) { fields.add(fieldMetadata.getName()); } Class<?> generatedClass =
         * storageClassLoader.getClassFromType(complexType); DirectoryProvider[] providers =
         * searchFactory.getDirectoryProviders(generatedClass); Collections.addAll(directoryProviders, providers); }
         * 
         * DirectoryProvider[] providers = directoryProviders.toArray(new DirectoryProvider[directoryProviders.size()]);
         * IndexReader reader = searchFactory.getReaderProvider().openReader(providers);
         * 
         * try { switch (mode) { case START: try { IndexSearcher searcher = new IndexSearcher(reader);
         * 
         * String[] fieldsAsArray = fields.toArray(new String[fields.size()]); MultiFieldQueryParser parser = new
         * MultiFieldQueryParser(Version.LUCENE_29, fieldsAsArray, new KeywordAnalyzer()); StringBuilder queryBuffer =
         * new StringBuilder(); Iterator<String> fieldsIterator = fields.iterator(); while (fieldsIterator.hasNext()) {
         * queryBuffer.append(fieldsIterator.next()).append(':').append(keyword).append("*"); if
         * (fieldsIterator.hasNext()) { queryBuffer.append(" OR "); } } org.apache.lucene.search.Query query =
         * parser.parse(queryBuffer.toString());
         * 
         * MatchedWordsCollector collector = new MatchedWordsCollector(reader); searcher.search(query, collector);
         * return collector.getMatchedWords(); } catch (Exception e) { throw new RuntimeException(e); } case ALTERNATE:
         * try { IndexSearcher searcher = new IndexSearcher(reader);
         * 
         * String[] fieldsAsArray = fields.toArray(new String[fields.size()]); BooleanQuery query = new BooleanQuery();
         * for (String field : fieldsAsArray) { FuzzyQuery fieldQuery = new FuzzyQuery(new Term(field, '~' + keyword));
         * query.add(fieldQuery, BooleanClause.Occur.SHOULD); }
         * 
         * MatchedWordsCollector collector = new MatchedWordsCollector(reader); searcher.search(query, collector);
         * return collector.getMatchedWords(); } catch (Exception e) { throw new RuntimeException(e); } default: throw
         * new NotImplementedException("No support for suggestion mode '" + mode + "'"); } } finally { try {
         * reader.close(); } catch (IOException e) {
         * LOGGER.error("Exception occurred during full text suggestion searches.", e); } }
         */
        throw new UnsupportedOperationException("No support due to version of Lucene in use.");
    }

    public SessionFactory getFactory() {
        return factory;
    }

    @Override
    public String getName() {
        return storageName;
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public StorageType getType() {
        return storageType;
    }

    @Override
    public void delete(Expression userQuery) {
        ClassLoader previousClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(storageClassLoader);
            isPerformingDelete.set(true);
            Session session = factory.getCurrentSession();
            Iterable<DataRecord> records = internalFetch(session, userQuery, Collections.<EndOfResultsCallback> emptySet());
            for (DataRecord currentDataRecord : records) {
                ComplexTypeMetadata currentType = currentDataRecord.getType();
                TypeMapping mapping = mappingRepository.getMappingFromUser(currentType);
                if (mapping == null) {
                    throw new IllegalArgumentException("Type '" + currentType.getName() + "' does not have a database mapping.");
                }
                Class<?> clazz = storageClassLoader.getClassFromType(mapping.getDatabase());

                Serializable idValue;
                List<FieldMetadata> keyFields = currentType.getKeyFields();
                if (keyFields.size() == 1) {
                    idValue = (Serializable) currentDataRecord.get(keyFields.get(0));
                } else {
                    List<Object> compositeIdValues = new LinkedList<Object>();
                    for (FieldMetadata keyField : keyFields) {
                        compositeIdValues.add(currentDataRecord.get(keyField));
                    }
                    idValue = ObjectDataRecordConverter.createCompositeId(storageClassLoader, clazz, compositeIdValues);
                }

                Object object = session.get(clazz, idValue);
                if (object != null) {
                    session.delete(object);
                } else {
                    LOGGER.warn("Instance of type '" + currentType.getName() + "' and ID '" + idValue.toString()
                            + "' has already been deleted within same transaction.");
                }
            }
        } catch (ConstraintViolationException e) {
            throw new com.amalto.core.storage.exception.ConstraintViolationException(e);
        } catch (HibernateException e) {
            throw new RuntimeException(e);
        } finally {
            Thread.currentThread().setContextClassLoader(previousClassLoader);
            isPerformingDelete.set(false);
        }
    }

    @Override
    public synchronized void close() {
        LOGGER.info("Closing storage '" + storageName + "' (" + storageType + ").");
        ClassLoader previousClassLoader = Thread.currentThread().getContextClassLoader();
        if (previousClassLoader instanceof StorageClassLoader) { // Prevent restoring a closed classloader.
            previousClassLoader = previousClassLoader.getParent();
        }
        try {
            Thread.currentThread().setContextClassLoader(storageClassLoader);
            try {
                // Hack to prevent Hibernate Search to cause ConcurrentModificationException
                try {
                    Field contexts = ContextHolder.class.getDeclaredField("contexts"); //$NON-NLS-1$
                    contexts.setAccessible(true); // 'contexts' field is private.
                    ThreadLocal<WeakHashMap<Configuration, SearchFactoryImpl>> contextsPerThread = (ThreadLocal<WeakHashMap<Configuration, SearchFactoryImpl>>) contexts
                            .get(null);
                    WeakHashMap<Configuration, SearchFactoryImpl> contextMap = contextsPerThread.get();
                    if (contextMap != null) {
                        contextMap.remove(configuration);
                    }
                } catch (Exception e) {
                    LOGGER.error("Exception occurred during Hibernate Search clean up.", e);
                }
                if (factory != null) {
                    factory.close();
                    factory = null; // SessionFactory#close() documentation advises to remove all references to
                                    // SessionFactory.
                }
            } finally {
                if (storageClassLoader != null) {
                    storageClassLoader.close();
                    storageClassLoader = null;
                }
            }
        } finally {
            Thread.currentThread().setContextClassLoader(previousClassLoader);
        }
        // Reset caches
        ListIterator.resetTypeReaders();
        ScrollableIterator.resetTypeReaders();
        LOGGER.info("Storage '" + storageName + "' (" + storageType + ") closed.");
    }

    @Override
    public void close(boolean dropExistingData) {
        // Close hibernate so all connections get released before drop schema.
        close();
        if (dropExistingData) { // Drop schema if asked for...
            LOGGER.info("Deleting data and schema of storage '" + storageName + "' (" + storageType + ").");
            JDBCStorageCleaner cleaner = new JDBCStorageCleaner(new FullTextIndexCleaner());
            cleaner.clean(this);
            LOGGER.info("Data and schema of storage '" + storageName + "' (" + storageType + ") deleted.");
        }
    }

    private StorageResults internalFetch(Session session, Expression userQuery, Set<EndOfResultsCallback> callbacks) {
        // Always normalize the query to ensure query has expected format.
        Expression expression = userQuery.normalize();
        if (expression instanceof Select) {
            Select select = (Select) expression;
            // Contains optimizations (use of full text, disable it...)
            ConfigurableContainsOptimizer containsOptimizer = new ConfigurableContainsOptimizer(dataSource);
            containsOptimizer.optimize(select);
            // Other optimizations
            for (Optimizer optimizer : OPTIMIZERS) {
                optimizer.optimize(select);
            }
        }
        expression = expression.normalize();
        SelectAnalyzer selectAnalysis = new SelectAnalyzer(mappingRepository, storageClassLoader, session, callbacks, this,
                tableResolver);
        AbstractQueryHandler queryHandler = userQuery.accept(selectAnalysis);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Query after optimizations:");
            userQuery.accept(new UserQueryDumpConsole(LOGGER));
        }
        return expression.accept(queryHandler);
    }

    private void assertPrepared() {
        if (!isPrepared) {
            throw new IllegalStateException("Storage has not been prepared.");
        }
        if (storageClassLoader == null || storageClassLoader.isClosed()) {
            throw new IllegalStateException("Storage has been closed.");
        }
    }

    private static class MetadataChecker extends DefaultMetadataVisitor<Object> {

        final Set<TypeMetadata> processedTypes = new HashSet<TypeMetadata>();

        @Override
        public Object visit(SimpleTypeFieldMetadata simpleField) {
            String simpleFieldTypeName = simpleField.getType().getName();
            if ("gYearMonth".equals(simpleFieldTypeName) //$NON-NLS-1$
                    || "gYear".equals(simpleFieldTypeName) //$NON-NLS-1$
                    || "gMonthDay".equals(simpleFieldTypeName) //$NON-NLS-1$
                    || "gDay".equals(simpleFieldTypeName) //$NON-NLS-1$
                    || "gMonth".equals(simpleFieldTypeName)) { //$NON-NLS-1$
                throw new IllegalArgumentException("No support for field type '" + simpleFieldTypeName + "' (field '"
                        + simpleField.getName() + "' of type '" + simpleField.getContainingType().getName() + "').");
            }
            assertField(simpleField);
            return super.visit(simpleField);
        }

        @Override
        public Object visit(ReferenceFieldMetadata referenceField) {
            assertField(referenceField);
            return super.visit(referenceField);
        }

        @Override
        public Object visit(ContainedTypeFieldMetadata containedField) {
            assertField(containedField);
            if (processedTypes.contains(containedField.getContainedType())) {
                return null;
            } else {
                processedTypes.add(containedField.getContainedType());
            }
            return super.visit(containedField);
        }

        @Override
        public Object visit(EnumerationFieldMetadata enumField) {
            assertField(enumField);
            return super.visit(enumField);
        }

        private static void assertField(FieldMetadata field) {
            if (field.getName().toLowerCase().startsWith(FORBIDDEN_PREFIX)) {
                throw new IllegalArgumentException("Field '" + field.getName() + "' of type '"
                        + field.getContainingType().getName() + "' is not allowed to start with " + FORBIDDEN_PREFIX);
            }
        }
    }

    private static class LocalEntityResolver implements EntityResolver {

        @Override
        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
            if (StorageClassLoader.CONFIGURATION_PUBLIC_ID.equals(publicId)) {
                InputStream resourceAsStream = HibernateStorage.class.getResourceAsStream("hibernate.cfg.dtd"); //$NON-NLS-1$
                if (resourceAsStream == null) {
                    throw new IllegalStateException("Expected class path to contain Hibernate configuration DTD.");
                }
                return new InputSource(resourceAsStream);
            } else if (StorageClassLoader.MAPPING_PUBLIC_ID.equals(publicId)) {
                InputStream resourceAsStream = HibernateStorage.class.getResourceAsStream("hibernate.hbm.dtd"); //$NON-NLS-1$
                if (resourceAsStream == null) {
                    throw new IllegalStateException("Expected class path to contain Hibernate mapping DTD.");
                }
                return new InputSource(resourceAsStream);
            }
            return null;
        }
    }

    @Override
    public String toString() {
        return storageName + '(' + storageType + ')';
    }

    public static enum TypeMappingStrategy {
        FLAT,
        SCATTERED,
        AUTO
    }

}