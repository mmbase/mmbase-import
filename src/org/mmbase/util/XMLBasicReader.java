/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
*/
package org.mmbase.util;

import java.io.*;
import java.util.*;

import org.xml.sax.*;
import org.apache.xerces.parsers.*;
import org.w3c.dom.*;
import org.w3c.dom.traversal.*;

import org.mmbase.module.corebuilders.*;

import org.mmbase.util.logging.*; 

/**
 * @author cjr@dds.nl
 *
 * @version $Id: XMLBasicReader.java,v 1.8 2001-05-18 11:46:14 daniel Exp $
 *
 * $Log: not supported by cvs2svn $
 * Revision 1.7  2001/03/23 11:07:16  vpro
 * Enhanced error message
 * CV: ----------------------------------------------------------------------
 *
 * Revision 1.6  2000/12/20 00:24:53  daniel
 * changed xml parser to file:/// for new xerces/win98/win2000
 *
 * Revision 1.5  2000/08/18 19:43:45  case
 * cjr: Added getChildElements(element,tag) method to get obtain all child
 *      elements with a certain tag.
 *
 * Revision 1.4  2000/08/17 21:16:00  case
 * cjr: returned value for non-set attributes now is "" (should it be null?)
 *
 */
public class XMLBasicReader  {

    private static Logger log = Logging.getLoggerInstance(XMLBasicReader.class.getName());

    Document document;
    DOMParser parser;

    Hashtable languageList; // Hashtable from languagecode to Hashtables with dictionaries

    String languagecode;  // code for language, e.g. 'nl'
    Hashtable dictionary; // dictionary of mmbase term identifiers to translations in language
    String loadedfile;


    public XMLBasicReader(String path) {
        try {
            parser = new DOMParser();
            parser.setFeature("http://apache.org/xml/features/dom/defer-node-expansion", true);
            parser.setFeature("http://apache.org/xml/features/continue-after-fatal-error", true);
            EntityResolver resolver = new XMLEntityResolver();
            parser.setEntityResolver(resolver);
	    path="file:///"+path;
	    loadedfile=path; // save for debug
            parser.parse(path);
            document = parser.getDocument();
		
        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @param path Dot-separated list of tags describing path from root element to requested element. 
     *             NB the path starts with the name of the root element.
     * @return Leaf element of the path
     */
    public Element getElementByPath(String path) {
        return getElementByPath(document.getDocumentElement(),path);
    }

    /**
     * @param e Element from which the "relative" path is starting. 
     *          NB the path starts with the name of the root element.
     * @param path Dot-separated list of tags describing path from root element to requested element
     * @return Leaf element of the path
     */
    public Element getElementByPath(Element e,String path) {
        StringTokenizer st = new StringTokenizer(path,".");
        if (!st.hasMoreTokens()) {
            // faulty path
            log.error("No tokens in path");
        } else {
            String root = st.nextToken();
            if (!e.getNodeName().equals(root)) {
                // path should start with document root element
                log.error("path ["+path+"] doesn't start with root element: incorrect xml file" +
                          "(Did you use new way to configure modules?)");
		log.error("XML file with problem ="+loadedfile);
            } else {
                while (st.hasMoreTokens()) {
                    String tag = st.nextToken();
                    NodeList nl = e.getElementsByTagName(tag);
                    if (nl.getLength()>0) {
                        e = (Element)nl.item(0);
                    } else {
                        // Handle error!
                        //System.err.println("No subelements found in "+e.getTagName());
                        return null;
                    }
                }
            }
            return e;
        }
        System.err.println("failed miserably");
        return null;
    }

    /**
     * @param e Element
     * @return Text value of element
     */
    public String getElementValue(Element e) {
        if (e == null) {
            return "";
        } else {
            NodeList nl = e.getChildNodes();
            for (int i=0;i<nl.getLength();i++) {
                Node n = nl.item(i);
                if (n.getNodeType() == n.TEXT_NODE) {
                    return n.getNodeValue();
                }
            }
            return "";
        }
    }

    /**
     * @param e Element
     * @return Tag name of the element
     */
    public String getElementName(Element e) {
        return e.getTagName();
    }

    /**
     * @param e Element
     * @param attr Attribute name
     * @return Value of attribute
     */
    public String getElementAttributeValue(Element e, String attr) {
        Node n = e.getAttributes().getNamedItem(attr);
        // XXX Add errorchecking
        if (n==null) {
            return "";
        } else {
            return n.getNodeValue();
        }
    }

    /**
     * @param e Element
     * @return Enumeration of child elements
     */
    public Enumeration getChildElements(Element e) {
        Vector v = new Vector();
        NodeList nl = e.getChildNodes();
        for (int i=0;i<nl.getLength();i++) {
            Node n = nl.item(i);
            if (n.getNodeType() == n.ELEMENT_NODE) {
                v.addElement(n);
            }
        }
        return v.elements();
    }

    /**
     * @param e Element
     * @return Enumeration of child elements with the given tag
     */
    public Enumeration getChildElements(Element e,String tag) {
        Vector v = new Vector();
        NodeList nl = e.getChildNodes();
        for (int i=0;i<nl.getLength();i++) {
            Node n = nl.item(i);
            if (n.getNodeType() == n.ELEMENT_NODE && ((Element)n).getTagName().equalsIgnoreCase(tag)) {
                v.addElement(n);
            }
        }
        return v.elements();
    }

}













