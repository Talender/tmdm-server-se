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
package com.amalto.core.storage.record;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

import com.amalto.core.metadata.FieldMetadata;
import com.amalto.core.metadata.ReferenceFieldMetadata;


/**
 * created by talend2 on 2013-3-4
 * Detailled comment
 *
 */
public class DataRecordDefaultWriterTest extends TestCase {
    
    public void testWrite() throws IOException {
        String xml = "<referenceField>[111][222][444]</referenceField>"; //$NON-NLS-1$
        
        FieldMetadata fieldMetadata = new ReferenceFieldMetadata(null,true,false,true,"referenceField",null,null,null,true,true,null,null,null); //$NON-NLS-1$
        DataRecord record = new DataRecord(null,null);
        Object[] values = new Object[3];
        values[0] = "111"; //$NON-NLS-1$
        values[1] = "222"; //$NON-NLS-1$
        values[2] = "444"; //$NON-NLS-1$
        record.set(fieldMetadata, values);
        
        ByteArrayOutputStream output = new ByteArrayOutputStream();        
        DataRecordWriter dataRecordWriter = new DataRecordDefaultWriter();
        dataRecordWriter.write(record,output);
        String document = new String(output.toByteArray());        
        assertEquals(true, document.contains(xml));
    }

}
