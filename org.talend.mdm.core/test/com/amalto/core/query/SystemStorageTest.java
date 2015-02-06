/*
 * Copyright (C) 2006-2013 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.query;

import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.initdb.InitDBUtil;
import com.amalto.core.metadata.ClassRepository;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJO;
import com.amalto.core.query.user.Expression;
import org.apache.commons.io.IOUtils;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.FieldMetadata;
import com.amalto.core.objects.menu.ejb.MenuEntryPOJO;
import com.amalto.core.objects.menu.ejb.MenuPOJO;
import com.amalto.core.query.user.UserQueryBuilder;
import com.amalto.core.server.MockServerLifecycle;
import com.amalto.core.server.ServerContext;
import com.amalto.core.storage.DispatchWrapper;
import com.amalto.core.storage.SecuredStorage;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageResults;
import com.amalto.core.storage.StorageType;
import com.amalto.core.storage.datasource.DataSource;
import com.amalto.core.storage.hibernate.HibernateStorage;
import com.amalto.core.storage.record.*;
import junit.framework.TestCase;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import static com.amalto.core.query.user.UserQueryBuilder.*;

public class SystemStorageTest extends TestCase {

    private static Logger LOG = Logger.getLogger(StorageTestCase.class);

    public void testSystemRepository() throws Exception {
        ClassRepository repository = buildRepository();
        // The additional loaded type is the sub type of ServiceBean
        assertEquals(ObjectPOJO.OBJECT_TYPES.length + 1, repository.getUserComplexTypes().size());
    }

    private ClassRepository buildRepository() {
        ClassRepository repository = new ClassRepository();
        Class[] objectsToParse = new Class[ObjectPOJO.OBJECT_TYPES.length];
        int i = 0;
        for (Object[] objects : ObjectPOJO.OBJECT_TYPES) {
            objectsToParse[i++] = (Class) objects[1];
        }
        repository.load(objectsToParse);
        return repository;
    }

    public void testStorageInit() throws Exception {
        LOG.info("Setting up MDM server environment...");
        ServerContext.INSTANCE.get(new MockServerLifecycle());
        LOG.info("MDM server environment set.");

        LOG.info("Preparing storage for tests...");
        Storage storage = new SecuredStorage(new HibernateStorage("MDM", StorageType.SYSTEM), new SecuredStorage.UserDelegator() {
            @Override
            public boolean hide(FieldMetadata field) {
                return false;
            }

            @Override
            public boolean hide(ComplexTypeMetadata type) {
                return false;
            }
        });
        ClassRepository repository = buildRepository();
        storage.init(getDatasource("H2-Default"));
        storage.prepare(repository, Collections.<Expression>emptySet(), true, true);
        LOG.info("Storage prepared.");
    }

    public void testDOMParsing() throws Exception {
        Collection<File> files = getConfigFiles();
        ClassRepository repository = buildRepository();

        DataRecordReader<Element> dataRecordReader = new XmlDOMDataRecordReader();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder documentBuilder = factory.newDocumentBuilder();
        int error = 0;
        for (File file : files) {
            FileInputStream fis1 = new FileInputStream(file);
            String typeName;
            Document document;
            try {
                document = documentBuilder.parse(fis1);
                typeName = document.getDocumentElement().getNodeName();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } finally {
                fis1.close();
            }
            ComplexTypeMetadata complexType = repository.getComplexType(typeName);
            if (complexType == null) {
                System.out.println("Ignore: " + file);
                continue;
            }
            try {
                dataRecordReader.read("1", repository, complexType, document.getDocumentElement());
            } catch (Exception e) {
                error++;
            }
        }
        assertEquals(0, error);
    }

    public void testSAXParsing() throws Exception {
        Collection<File> files = getConfigFiles();
        ClassRepository repository = buildRepository();

        DataRecordReader<XmlSAXDataRecordReader.Input> dataRecordReader = new XmlSAXDataRecordReader();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder documentBuilder = factory.newDocumentBuilder();
        XMLReader reader = XMLReaderFactory.createXMLReader();
        int error = 0;
        for (File file : files) {
            FileInputStream fis1 = new FileInputStream(file);
            String typeName;
            Document document;
            try {
                document = documentBuilder.parse(fis1);
                typeName = document.getDocumentElement().getNodeName();
            } finally {
                fis1.close();
            }
            ComplexTypeMetadata complexType = repository.getComplexType(typeName);
            if (complexType == null) {
                System.out.println("Ignore: " + file);
                continue;
            }
            FileInputStream fis2 = new FileInputStream(file);
            try {
                dataRecordReader.read("1", repository, complexType, new XmlSAXDataRecordReader.Input(reader, new InputSource(fis2)));
            } catch (Exception e) {
                System.out.println("Error: " + file);
                error++;
            } finally {
                fis2.close();
            }
        }
        assertEquals(0, error);
    }

    public void testStringParsing() throws Exception {
        Collection<File> files = getConfigFiles();
        ClassRepository repository = buildRepository();

        DataRecordReader<String> dataRecordReader = new XmlStringDataRecordReader();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder documentBuilder = factory.newDocumentBuilder();
        int error = 0;
        for (File file : files) {
            FileInputStream fis1 = new FileInputStream(file);
            String typeName;
            Document document;
            try {
                document = documentBuilder.parse(fis1);
                typeName = document.getDocumentElement().getNodeName();
            } finally {
                fis1.close();
            }
            ComplexTypeMetadata complexType = repository.getComplexType(typeName);
            if (complexType == null) {
                System.out.println("Ignore: " + file);
                continue;
            }
            try {
                dataRecordReader.read("1", repository, complexType, FileUtils.readFileToString(file));
            } catch (Exception e) {
                error++;
            }
        }
        assertEquals(0, error);
    }

    public void testStorageInitPopulate() throws Exception {
        LOG.info("Setting up MDM server environment...");
        ServerContext.INSTANCE.get(new MockServerLifecycle());
        LOG.info("MDM server environment set.");

        LOG.info("Preparing storage for tests...");
        Storage storage = new SecuredStorage(new HibernateStorage("MDM", StorageType.SYSTEM), new SecuredStorage.UserDelegator() {
            @Override
            public boolean hide(FieldMetadata field) {
                return false;
            }

            @Override
            public boolean hide(ComplexTypeMetadata type) {
                return false;
            }
        });
        ClassRepository repository = buildRepository();
        storage.init(getDatasource("H2-Default"));
        storage.prepare(repository, Collections.<Expression>emptySet(), true, true);
        LOG.info("Storage prepared.");

        Collection<File> files = getConfigFiles();

        DataRecordReader<Element> dataRecordReader = new XmlDOMDataRecordReader();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder documentBuilder = factory.newDocumentBuilder();
        int error = 0;
        int ignore = 0;
        List<DataRecord> records = new LinkedList<DataRecord>();
        Set<ComplexTypeMetadata> presentTypes = new HashSet<ComplexTypeMetadata>();
        for (File file : files) {
            FileInputStream fis1 = new FileInputStream(file);
            String typeName;
            Document document;
            try {
                document = documentBuilder.parse(fis1);
                typeName = document.getDocumentElement().getNodeName();
            } finally {
                fis1.close();
            }
            ComplexTypeMetadata complexType = repository.getComplexType(typeName);
            if (complexType == null) {
                System.out.println("Ignore: " + file);
                ignore++;
                continue;
            }
            presentTypes.add(complexType);
            try {
                records.add(dataRecordReader.read("1", repository, complexType, document.getDocumentElement()));
            } catch (Exception e) {
                System.out.println("Error: " + file);
                e.printStackTrace();
                error++;
            }
        }
        assertEquals(0, error);

        storage.begin();
        storage.update(records);
        storage.commit();

        int total = 0;
        storage.begin();
        try {
            for (ComplexTypeMetadata presentType : presentTypes) {
                UserQueryBuilder qb = UserQueryBuilder.from(presentType);
                StorageResults results = storage.fetch(qb.getSelect());
                try {
                    total += results.getCount();
                    SystemDataRecordXmlWriter writer = new SystemDataRecordXmlWriter((ClassRepository) storage.getMetadataRepository(), presentType);
                    for (DataRecord result : results) {
                        StringWriter stringWriter = new StringWriter();
                        if ("menu-pOJO".equals(presentType.getName())) {
                            writer.write(result, stringWriter);
                            MenuPOJO menuPOJO = ObjectPOJO.unmarshal(MenuPOJO.class, stringWriter.toString());
                            assertNotNull(menuPOJO);
                            for (MenuEntryPOJO menuEntry : menuPOJO.getMenuEntries()) {
                                assertNotNull(menuEntry.getApplication());
                            }
                        }
                        if ("data-model-pOJO".equals(presentType.getName())) {
                            writer.write(result, stringWriter);
                            DataModelPOJO dataModelPOJO = ObjectPOJO.unmarshal(DataModelPOJO.class, stringWriter.toString());
                            assertNotNull(dataModelPOJO.getSchema());
                        }
                    }
                } finally {
                    results.close();
                }
            }
        } finally {
            storage.commit();
        }
        assertEquals(files.size() - ignore, total);
    }
    
    public void testUserInformationWithRoles() throws Exception {
        LOG.info("Setting up MDM server environment...");
        ServerContext.INSTANCE.get(new MockServerLifecycle());
        LOG.info("MDM server environment set.");
        LOG.info("Preparing storage for tests...");
        Storage storage = new SecuredStorage(new HibernateStorage("MDM", StorageType.SYSTEM), new SecuredStorage.UserDelegator() {

            @Override
            public boolean hide(FieldMetadata field) {
                return false;
            }

            @Override
            public boolean hide(ComplexTypeMetadata type) {
                return false;
            }
        });
        ClassRepository repository = buildRepository();
        // Additional setup to get User type in repository
        String[] models = new String[] { "/com/amalto/core/initdb/data/datamodel/PROVISIONING" //$NON-NLS-1$
        };
        for (String model : models) {
            InputStream builtInStream = this.getClass().getResourceAsStream(model);
            if (builtInStream == null) {
                throw new RuntimeException("Built in model '" + model + "' cannot be found.");
            }
            try {
                DataModelPOJO modelPOJO = ObjectPOJO.unmarshal(DataModelPOJO.class, IOUtils.toString(builtInStream, "UTF-8")); //$NON-NLS-1$
                repository.load(new ByteArrayInputStream(modelPOJO.getSchema().getBytes("UTF-8"))); //$NON-NLS-1$
            } catch (Exception e) {
                throw new RuntimeException("Could not parse builtin data model '" + model + "'.", e);
            } finally {
                try {
                    builtInStream.close();
                } catch (IOException e) {
                    // Ignored
                }
            }
        }
        storage.init(getDatasource("H2-Default"));
        storage.prepare(repository, Collections.<Expression> emptySet(), true, true);
        LOG.info("Storage prepared.");
        // Create users
        DataRecordReader<Element> dataRecordReader = new XmlDOMDataRecordReader();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        List<DataRecord> records = new LinkedList<DataRecord>();
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(SystemStorageTest.class.getResourceAsStream("user1.xml")); //$NON-NLS-1$
        Element element = (Element) document.getElementsByTagName("User").item(0); //$NON-NLS-1$
        records.add(dataRecordReader.read("1", repository, repository.getComplexType("User"), element)); //$NON-NLS-1$ //$NON-NLS-2$
        document = builder.parse(SystemStorageTest.class.getResourceAsStream("user2.xml")); //$NON-NLS-1$
        element = (Element) document.getElementsByTagName("User").item(0); //$NON-NLS-1$
        records.add(dataRecordReader.read("1", repository, repository.getComplexType("User"), element)); //$NON-NLS-1$ //$NON-NLS-2$
        // Commit users
        storage.begin();
        storage.update(records);
        storage.commit();
        // Query test
        storage.begin();
        try {
            ComplexTypeMetadata user = repository.getComplexType("User"); //$NON-NLS-1$
            UserQueryBuilder qb = from(user);
            qb.start(0);
            qb.limit(2);
            StorageResults results = storage.fetch(qb.getSelect());
            assertEquals(2, results.getCount());
            try {
                java.util.Iterator<DataRecord> it = results.iterator();
                int count = 0;
                while (it.hasNext()) {
                    count++;
                    it.next();
                }
                assertEquals(2, count);
            } finally {
                results.close();
            }

        } finally {
            storage.commit();
        }
    }

    private static Collection<File> getConfigFiles() throws URISyntaxException {
        URL data = InitDBUtil.class.getResource("data");
        return FileUtils.listFiles(new File(data.toURI()), new IOFileFilter() {
                    @Override
                    public boolean accept(File file) {
                        return true;
                    }

                    @Override
                    public boolean accept(File file, String s) {
                        return true;
                    }
                }, new IOFileFilter() {
                    @Override
                    public boolean accept(File file) {
                        return !".svn".equals(file.getName());
                    }

                    @Override
                    public boolean accept(File file, String s) {
                        return !".svn".equals(file.getName());
                    }
                }
        );
    }

    protected static DataSource getDatasource(String dataSourceName) {
        return ServerContext.INSTANCE.get().getDataSource(dataSourceName, "MDM", StorageType.MASTER);
    }

}
