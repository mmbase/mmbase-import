/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.io.*;
import java.util.*;

import javax.xml.parsers.*;

import org.xml.sax.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
* This is a flexible Properties version, it can handle saving of Properties with
* the comments that will stay in your file.   
* @author Jan van Oosterom
* @version $Id: XMLProperties.java,v 1.5 2003-07-07 13:35:21 keesj Exp $
*/
public class XMLProperties extends Properties implements ContentHandler {
    private static Logger log = Logging.getLoggerInstance(XMLProperties.class);
    private StringBuffer characterData = null;
    private String rootElementName = null;
    private String startElementName = null;
    private String endElementName = null;

    /** The current Locator. */
    private Locator locator = null;

    public XMLProperties() {}

    /**
    * Read from Properties and return them.
    * 
    */
    public Hashtable getProperties() {
        XMLProperties propsToReturn = new XMLProperties();
        Enumeration e = keys();
        while (e.hasMoreElements()) {
            String s = (String)e.nextElement();
            propsToReturn.put(s, get(s));
        }
        return (Hashtable)propsToReturn;
    }

    /**
     * Receive an object for locating the origin of SAX document events.
     */
    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }

    /**
     * Receive notification of the beginning of a document.
     */
    public void startDocument() throws SAXException {
        //reseting values
        clear();
        rootElementName = "";
        endElementName = null;
        startElementName = null;
        characterData = new StringBuffer();
    }

    /**
     * Receive notification of the end of a document.
     */
    public void endDocument() throws SAXException {}

    /**
     * Begin the scope of a prefix-URI Namespace mapping.
     */
    public void startPrefixMapping(String p, String uri) throws SAXException {
        //namespaces aren't used at this moment (GvE)
    }

    /**
     * End the scope of a prefix-URI mapping.
     */
    public void endPrefixMapping(String p) throws SAXException {
        //namespaces aren't used at this moment (GvE)
    }

    /**
     * Receive notification of the beginning of an element.
     */
    public void startElement(String uri, String loc, String raw, Attributes a) throws SAXException {
        String name = null;
        if (raw.length() > 0) {
            name = raw;
        } else if (loc.length() == 0) {
            throw new SAXException("No local name found");
        } else {
            name = loc;
        }
        startElementName = name;

        //check if element is a root element
        if (rootElementName.equals("")) {
            rootElementName = startElementName;
        }
    }

    /**
     * Receive notification of the end of an element.
     */
    public void endElement(String uri, String loc, String raw) throws SAXException {
        String name = null;
        if (raw.length() > 0) {
            name = raw;
        } else if (loc.length() == 0) {
            throw new SAXException("No local name found");
        } else {
            name = loc;
        }
        endElementName = name;
        if (!endElementName.equals(rootElementName)) {
            put(startElementName, characterData.toString());
            endElementName = startElementName;
            characterData = null;
            characterData = new StringBuffer();
        }
    }

    /**
     * Receive notification of character data.
     */
    public void characters(char ch[], int start, int len) {
        // only get the characters if endElement doesn't matches startElement 
        // and element isn't the rootelement  
        if (!startElementName.equals(endElementName) && !startElementName.equals(rootElementName)) {
            characterData.append(ch, start, len);
        }
    }

    /**
     * Receive notification of ignorable whitespace in element content.
     */
    public void ignorableWhitespace(char ch[], int start, int len) {}

    /**
     * Receive notification of a processing instruction.
     */
    public void processingInstruction(String target, String data) {}

    /**
     * Receive notification of a skipped entity.
     */
    public void skippedEntity(String name) {}

    /**
     * Read an XML file in which key/value pairs are represented as tag and content
     *
     * @param path Full path to XML file
     *
     * @return Hashtable with the key/value pairs or an empty Hashtable if something went wrong.
     */
    public static XMLProperties getPropertiesFromXML(String file) {
        XMLProperties xmlProperties = new XMLProperties();

        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        saxParserFactory.setValidating(false);

        try {
            SAXParser saxParser = saxParserFactory.newSAXParser();
            XMLReader reader = saxParser.getXMLReader();
            reader.setContentHandler(xmlProperties);
            reader.parse(new InputSource(new FileInputStream(new File(file))));
            //reader.parse(new InputSource(new FileInputStream(new File(configpath + File.separator + path))));
        } catch (Exception e) {
            log.error("Error reading xml properties file " +file + ". Returning empty hashtable.");
        }
        return xmlProperties;
    }
}
