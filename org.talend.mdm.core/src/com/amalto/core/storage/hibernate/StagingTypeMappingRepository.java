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

package com.amalto.core.storage.hibernate;

import com.amalto.core.storage.datasource.RDBMSDataSource;
import org.talend.mdm.commmon.metadata.*;
import com.amalto.core.storage.Storage;

import javax.xml.XMLConstants;
import java.util.Collections;
import java.util.UUID;

class StagingTypeMappingRepository extends InternalRepository {
    public StagingTypeMappingRepository(TypeMappingStrategy mappingStrategy, RDBMSDataSource.DataSourceDialect dialect) {
        super(mappingStrategy, dialect);
    }

    public MetadataRepository visit(ComplexTypeMetadata complexType) { 
        TypeMapping typeMapping = complexType.accept(getTypeMappingCreator(complexType, strategy));

        // Add MDM specific record specific metadata
        ComplexTypeMetadata database = typeMapping.getDatabase();
        if (database.isInstantiable() && !database.isFrozen()) {
            TypeMetadata intType = new SoftTypeRef(internalRepository, XMLConstants.W3C_XML_SCHEMA_NS_URI, Types.INT, false);
            TypeMetadata longType = new SoftTypeRef(internalRepository, XMLConstants.W3C_XML_SCHEMA_NS_URI, Types.LONG, false);
            TypeMetadata stringType = new SimpleTypeMetadata(XMLConstants.W3C_XML_SCHEMA_NS_URI, Types.STRING);
            TypeMetadata limitedStringType = new SimpleTypeMetadata(XMLConstants.W3C_XML_SCHEMA_NS_URI, Types.STRING);
            limitedStringType.setData(MetadataRepository.DATA_MAX_LENGTH, UUID.randomUUID().toString().length());
            database.addField(new SimpleTypeFieldMetadata(database, false, false, true, Storage.METADATA_TIMESTAMP, longType, Collections.<String>emptyList(), Collections.<String>emptyList(), Collections.<String>emptyList()));
            database.addField(new SimpleTypeFieldMetadata(database, false, false, false, Storage.METADATA_TASK_ID, limitedStringType, Collections.<String>emptyList(), Collections.<String>emptyList(), Collections.<String>emptyList()));
            database.addField(new SimpleTypeFieldMetadata(database, false, false, false, Storage.METADATA_STAGING_STATUS, intType, Collections.<String>emptyList(), Collections.<String>emptyList(), Collections.<String>emptyList()));
            database.addField(new SimpleTypeFieldMetadata(database, false, false, false, Storage.METADATA_STAGING_SOURCE, limitedStringType, Collections.<String>emptyList(), Collections.<String>emptyList(), Collections.<String>emptyList()));
            database.addField(new SimpleTypeFieldMetadata(database, false, false, false, Storage.METADATA_STAGING_BLOCK_KEY, limitedStringType, Collections.<String>emptyList(), Collections.<String>emptyList(), Collections.<String>emptyList()));
            SimpleTypeFieldMetadata errorField = new SimpleTypeFieldMetadata(database, false, false, false, Storage.METADATA_STAGING_ERROR, stringType, Collections.<String>emptyList(), Collections.<String>emptyList(), Collections.<String>emptyList());
            errorField.getType().setData(TypeMapping.SQL_TYPE, "text"); //$NON-NLS-1$
            database.addField(errorField);
        }

        // Register mapping
        internalRepository.addTypeMetadata(typeMapping.getDatabase());
        mappings.addMapping(typeMapping);
        return internalRepository;
    }
}
