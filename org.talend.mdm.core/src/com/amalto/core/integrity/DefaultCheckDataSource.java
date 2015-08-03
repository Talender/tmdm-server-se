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

package com.amalto.core.integrity;

import static com.amalto.core.integrity.FKIntegrityCheckResult.ALLOWED;
import static com.amalto.core.integrity.FKIntegrityCheckResult.FORBIDDEN;
import static com.amalto.core.integrity.FKIntegrityCheckResult.FORBIDDEN_OVERRIDE_ALLOWED;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.ejb.ItemPOJOPK;
import com.amalto.core.metadata.FieldMetadata;
import com.amalto.core.metadata.MetadataRepository;
import com.amalto.core.metadata.ReferenceFieldMetadata;
import com.amalto.core.metadata.TypeMetadata;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJO;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJOPK;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;
import com.amalto.xmlserver.interfaces.IWhereItem;
import com.amalto.xmlserver.interfaces.WhereCondition;

class DefaultCheckDataSource implements FKIntegrityCheckDataSource {

    private final static Logger logger = Logger.getLogger(DefaultCheckDataSource.class);

    public String getDataModel(String clusterName, String concept, String[] ids) throws XtentisException {
        String dataModel;
        try {
            ItemPOJOPK pk = new ItemPOJOPK(new DataClusterPOJOPK(clusterName), concept, ids);
            ItemPOJO item = Util.getItemCtrl2Local().getItem(pk);
            if (item == null) {
                String id = StringUtils.EMPTY;
                for (String currentIdValue : ids) {
                    id += "[" + currentIdValue + "]"; //$NON-NLS-1$ //$NON-NLS-2$
                }

                throw new RuntimeException("Document with id '" //$NON-NLS-1$
                        + id
                        + "' (concept name: '" //$NON-NLS-1$
                        + concept
                        + "') has already been deleted."); //$NON-NLS-1$
            } else {

                dataModel = item.getDataModelName();
            }
        } catch (Exception e) {
            throw new XtentisException(e);
        }
        return dataModel;
    }

    public long countInboundReferences(String clusterName, String[] ids, String fromTypeName, ReferenceFieldMetadata fromReference)
            throws XtentisException {

        // For the anonymous type and leave the type name empty
        if (fromTypeName == null || fromTypeName.trim().equals("")) //$NON-NLS-1$
            return 0;

        // Transform ids into the string format expected in base
        String referencedId = ""; //$NON-NLS-1$
        for (String id : ids) {
            referencedId += '[' + id + ']';
        }

        LinkedHashMap<String, String> conceptPatternsToClusterName = new LinkedHashMap<String, String>();
        conceptPatternsToClusterName.put(".*", clusterName); //$NON-NLS-1$

        String leftPath = fromReference.getData(ForeignKeyIntegrity.ATTRIBUTE_XPATH);
        IWhereItem whereItem = new WhereCondition(leftPath,
                WhereCondition.EQUALS, referencedId, WhereCondition.NO_OPERATOR);

        return Util.getXmlServerCtrlLocal()
                .countItems(new LinkedHashMap(), conceptPatternsToClusterName, fromTypeName, whereItem);
    }

    public Set<ReferenceFieldMetadata> getForeignKeyList(String concept, String dataModel) throws XtentisException {
        // Get FK(s) to check
        MetadataRepository mr = new MetadataRepository();
        try {
            DataModelPOJO dataModelObject = Util.getDataModelCtrlLocal().getDataModel(new DataModelPOJOPK(dataModel));
            mr.load(new ByteArrayInputStream(dataModelObject.getSchema().getBytes("utf-8"))); //$NON-NLS-1$
        } catch (Exception e) {
            throw new XtentisException(e);
        }

        TypeMetadata type = mr.getType(concept);
        if (type != null) {
            return mr.accept(new ForeignKeyIntegrity(type));
        } else {
            logger.warn("Type '" + concept + "' does not exist anymore in data model '" + dataModel + "'. No integrity check will be performed."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            return Collections.emptySet();
        }
    }

    public void resolvedConflict(Map<FKIntegrityCheckResult, Set<FieldMetadata>> checkResultToFields, FKIntegrityCheckResult conflictResolution) {
        if (logger.isInfoEnabled()) {
            logger.info("Found conflicts in data model relative to FK integrity checks"); //$NON-NLS-1$
            logger.info("= Forbidden deletes ="); //$NON-NLS-1$
            dumpFields(FORBIDDEN, checkResultToFields);
            logger.info("= Forbidden deletes (override allowed) ="); //$NON-NLS-1$
            dumpFields(FORBIDDEN_OVERRIDE_ALLOWED, checkResultToFields);
            logger.info("= Allowed deletes ="); //$NON-NLS-1$
            dumpFields(ALLOWED, checkResultToFields);
            logger.info("Conflict resolution: " + conflictResolution); //$NON-NLS-1$
        }
    }

    private static void dumpFields(FKIntegrityCheckResult checkResult, Map<FKIntegrityCheckResult, Set<FieldMetadata>> checkResultToFields) {
        Set<FieldMetadata> fields = checkResultToFields.get(checkResult);
        if (fields != null) {
            for (FieldMetadata fieldMetadata : fields) {
                logger.info(fieldMetadata.toString());
            }
        }
    }
}
