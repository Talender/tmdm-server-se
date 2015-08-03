/*
 * Copyright (C) 2006-2015 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */
package com.amalto.core.save;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.amalto.core.history.MutableDocument;
import com.amalto.core.schema.validation.SkipAttributeDocumentBuilder;
import com.amalto.core.util.Util;

import junit.framework.TestCase;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

@SuppressWarnings("nls")
public class DOMDocumentTest extends TestCase {
    
    public void testExportToString() throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(DOMDocumentTest.class.getResourceAsStream("test1.xml")));
        String line = null;
        String xml = "";
        while ((line = in.readLine()) != null)
            xml += line;
        DOMDocument doc = new DOMDocument(Util.parse(xml));
        assertNotNull(doc);
        assertNotNull(doc.exportToString());
        assertFalse(doc.exportToString().contains("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
    }

    public void testIncludeXSINamespace() throws Exception {
        String xml = "<Organisation xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><IdOrganisation>5797</IdOrganisation></Organisation>";
        InputStream documentStream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
        // Parsing
        MutableDocument userDocument;
        DocumentBuilderFactory DOM_PARSER_FACTORY = DocumentBuilderFactory.newInstance();
        DOM_PARSER_FACTORY.setNamespaceAware(true);
        DOM_PARSER_FACTORY.setIgnoringComments(true);
        DOM_PARSER_FACTORY.setValidating(false);
        try {
            // Don't ignore talend internal attributes when parsing this document
            DocumentBuilder documentBuilder = new SkipAttributeDocumentBuilder(DOM_PARSER_FACTORY.newDocumentBuilder(), false);
            InputSource source = new InputSource(documentStream);
            Document userDomDocument = documentBuilder.parse(source);
            userDocument = new DOMDocument(userDomDocument);
        } catch (Exception e) {
            throw new RuntimeException("Unable to parse document to save.", e);
        }
        assertNotNull(userDocument);
        assertEquals(xml, userDocument.exportToString());
    }

}
