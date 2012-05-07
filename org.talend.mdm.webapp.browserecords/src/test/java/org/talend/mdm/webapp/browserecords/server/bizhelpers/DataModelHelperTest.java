// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.browserecords.server.bizhelpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Map;

import junit.framework.TestCase;

import org.talend.mdm.commmon.util.datamodel.management.DataModelID;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.shared.EntityModel;

import com.amalto.webapp.core.dmagent.SchemaMockAgent;

@SuppressWarnings("nls")
public class DataModelHelperTest extends TestCase {

    public void testParsingMetadata() throws Exception {

        EntityModel entityModel=new EntityModel();
        String datamodelName="Contract";
        String concept="Contract";
        String[] ids={""};
        String[] roles={"Demo_Manager", "System_Admin", "authenticated", "administration"};
        InputStream stream = getClass().getResourceAsStream("Contract.xsd");
        String xsd = inputStream2String(stream);
        
        DataModelHelper.alwaysEnterprise = true;
        DataModelHelper.overrideSchemaManager(new SchemaMockAgent(xsd, new DataModelID(datamodelName, null)));
        DataModelHelper.parseSchema("Contract", "Contract", DataModelHelper.convertXsd2ElDecl(concept, xsd), ids, entityModel,
                Arrays.asList(roles));
        Map<String, TypeModel> metaDataTypes = entityModel.getMetaDataTypes();
        assertEquals(13, metaDataTypes.size());
        assertTrue(!metaDataTypes.get("Contract/detail").isSimpleType());
    }

    private String inputStream2String(InputStream is) throws IOException {

        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        StringBuffer buffer = new StringBuffer();
        String line = "";
        while ((line = in.readLine()) != null) {
            buffer.append(line);
        }
        return buffer.toString();

    }

}
