/*
 * Copyright (C) 2006-2016 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package com.amalto.core.load.process;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 *
 */
public class ProcessedCharacters implements PayloadProcessedElement {
    private final char[] characters;

    public ProcessedCharacters(char[] characters) {
        this.characters = characters;
    }

    public void flush(ContentHandler contentHandler) throws SAXException {
        contentHandler.characters(characters, 0, characters.length);
    }
}
