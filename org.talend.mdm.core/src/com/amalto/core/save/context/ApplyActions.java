/*
 * Copyright (C) 2006-2012 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.save.context;

import com.amalto.core.history.Action;
import com.amalto.core.history.MutableDocument;
import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.metadata.FieldMetadata;
import com.amalto.core.metadata.MetadataRepository;
import com.amalto.core.save.DocumentSaverContext;
import com.amalto.core.save.SaverSession;
import com.amalto.core.save.UserAction;
import com.amalto.core.schema.validation.SkipAttributeDocumentBuilder;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;

class ApplyActions implements DocumentSaver {

    private static final Logger LOGGER = Logger.getLogger(ApplyActions.class);

    private final DocumentSaver next;

    private MetadataRepository metadataRepository;

    ApplyActions(DocumentSaver next) {
        this.next = next;
    }

    public void save(SaverSession session, DocumentSaverContext context) {
        MutableDocument databaseDocument = context.getDatabaseDocument();
        MutableDocument validationDocument = context.getDatabaseValidationDocument();
        for (Action action : context.getActions()) {
            action.perform(databaseDocument);
            action.perform(validationDocument);
        }

        metadataRepository = session.getSaverSource().getMetadataRepository(context.getDataModelName());
        // Never store empty elements in database
        clean(context.getType(), databaseDocument.asDOM().getDocumentElement(), EmptyElementCleaner.INSTANCE, false);
        if (context.getUserAction() == UserAction.CREATE || context.getUserAction() == UserAction.REPLACE) {
            // See TMDM-4038
            clean(context.getType(), validationDocument.asDOM().getDocumentElement(), EmptyElementCleaner.INSTANCE, true);
        } else {
            clean(context.getType(), validationDocument.asDOM().getDocumentElement(), EmptyElementCleaner.INSTANCE, false);
        }
        next.save(session, context);
    }

    public String[] getSavedId() {
        return next.getSavedId();
    }

    public String getSavedConceptName() {
        return next.getSavedConceptName();
    }

    public String getBeforeSavingMessage() {
        return next.getBeforeSavingMessage();
    }

    private void clean(ComplexTypeMetadata type, Element element, Cleaner cleaner, boolean removeTalendAttributes) {
        NodeList children = element.getChildNodes();
        if (removeTalendAttributes) {
            element.removeAttributeNS(SkipAttributeDocumentBuilder.TALEND_NAMESPACE, "type"); //$NON-NLS-1$
        }
        ComplexTypeMetadata parentType = type;
        if (element.getOwnerDocument() != element.getParentNode()) {
            String fieldName = element.getNodeName();
            FieldMetadata field = type.getField(fieldName);
            if (field.getType() instanceof ComplexTypeMetadata) {
                type = (ComplexTypeMetadata) field.getType();
            }
            String actualType = element.getAttributeNS(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "type"); //$NON-NLS-1$
            if (actualType != null && !actualType.trim().isEmpty()) {
                type = metadataRepository.getComplexType(actualType);
                if (type == null) {
                    type = (ComplexTypeMetadata) metadataRepository.getNonInstantiableType(actualType);
                }
            }
        }
        for (int i = children.getLength(); i >= 0; i--) {
            Node node = children.item(i);
            if (node instanceof Element) {
                Element currentElement = (Element) node;
                clean(type, currentElement, cleaner, removeTalendAttributes);
            }
        }
        if (cleaner.clean(parentType, element)) {
            element.getParentNode().removeChild(element);
        }
    }

    interface Cleaner {
        /**
         * Indicates to the caller whether <code>element</code> should be deleted or not.<br/>
         *
         * @param type    Definition of entity type where <code>element</code> is a field.
         * @param element An element to clean
         * @return <code>true</code> if element should be removed by caller, <code>false</code> otherwise.
         */
        boolean clean(ComplexTypeMetadata type, Element element);
    }

    private static class EmptyElementCleaner implements Cleaner {

        static Cleaner INSTANCE = new EmptyElementCleaner();

        public boolean clean(ComplexTypeMetadata type, Element element) {
            if (element == null) {
                return true;
            }
            if (element.hasChildNodes()) {
                return false;
            }
            if (element.hasAttributes()) {
                // Returns true (isEmpty) if all attributes are empty.
                NamedNodeMap attributes = element.getAttributes();
                for (int i = 0; i < attributes.getLength(); i++) {
                    String attributeValue = attributes.item(i).getNodeValue();
                    if (attributeValue != null && !attributeValue.trim().isEmpty()) {
                        return false;
                    }
                }
            }
            return true;
        }
    }
}
