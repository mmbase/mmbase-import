/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Enumeration;

import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;

import org.apache.xerces.parsers.DOMParser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * @author Case Roule
 * @author Rico Jansen
 * @author Pierre van Rooden
 * @version $Id: XMLBasicReader.java,v 1.19 2002-10-04 22:16:03 michiel Exp $
 */
public class XMLBasicReader  {
    private static Logger log = Logging.getLoggerInstance(XMLBasicReader.class.getName());

    /** for the document builder of javax.xml. */
    private static DocumentBuilder documentBuilder = null;
    private static DocumentBuilder altDocumentBuilder = null;

    /** set this one to true, and parser will be loaded...  */

    /** set this one to true, when all document pars */
    private static final boolean VALIDATE = true;

    protected Document document;

    private String xmlFilePath;



    public XMLBasicReader(String path) {
        this(path, VALIDATE);
    }
    public XMLBasicReader(InputSource source) {
        this(source, VALIDATE);
    }

    public XMLBasicReader(String path, boolean validating) {
        this(new InputSource("file:///" + path), validating);
    }

    public XMLBasicReader(InputSource source, boolean validating) {
        try {
            DocumentBuilder dbuilder = getDocumentBuilder(validating);
            if(dbuilder == null) throw new RuntimeException("failure retrieving document builder");
            if (log.isDebugEnabled()) log.debug("Reading " + source.getSystemId());
            document = dbuilder.parse(source);
        }
        catch(org.xml.sax.SAXException se) {
            throw new RuntimeException("failure reading document: " + source.getSystemId() + "\n" + se);
        }
        catch(java.io.IOException ioe) {
            throw new RuntimeException("failure reading document: " + source.getSystemId() + "\n" + ioe);
        }
    }

    private static DocumentBuilder createDocumentBuilder(boolean validating, ErrorHandler handler) {
        DocumentBuilder db;
        try {
            // get a new documentbuilder...
            DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();

            // turn validating on, or not
            XMLEntityResolver resolver = new XMLEntityResolver(validating); // strange to ask the resolver is mmbase is initilized
            boolean validate = validating && resolver.canResolve();

            // get docuemtn builder AFTER setting the validation
            dfactory.setValidating(validate);
            dfactory.setValidating(validate);
            db = dfactory.newDocumentBuilder();

            db.setErrorHandler(handler);

            // set the entity resolver... which tell us where to find the dtd's
            db.setEntityResolver(resolver);

        } catch(ParserConfigurationException pce) {
            log.error("a DocumentBuilder cannot be created which satisfies the configuration requested");
            log.error(Logging.stackTrace(pce));
            return null;
        }
        return db;
    }

    private static DocumentBuilder createDocumentBuilder(boolean validating) {
        return createDocumentBuilder(validating, new XMLErrorHandler());

    }


    public static DocumentBuilder getDocumentBuilder(boolean validating) {
        if (validating == VALIDATE) { 
            return getDocumentBuilder();
        } else {
            if (altDocumentBuilder == null) {
                altDocumentBuilder = createDocumentBuilder(validating);
            }
            return altDocumentBuilder;
        }       
    }

    public static DocumentBuilder getDocumentBuilder(boolean validating, ErrorHandler handler) {
        return createDocumentBuilder(validating, handler);
    }

    public static DocumentBuilder getDocumentBuilder() {
        if(documentBuilder == null)  {
            documentBuilder = createDocumentBuilder(VALIDATE);
        }
        return documentBuilder;
    }

    /**
     * @param path Dot-separated list of tags describing path from root element to requested element.
     *             NB the path starts with the name of the root element.
     * @return Leaf element of the path
     */
    public Element getElementByPath(String path) {
        if (document == null) {
            log.error("Document is not defined, cannot get " + path);
        }
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
            return null;
        } else {
            String root = st.nextToken();
            if (!e.getNodeName().equals(root)) {
                // path should start with document root element
                log.error("path ["+path+"] with root ("+root+") doesn't start with root element ("+e.getNodeName()+"): incorrect xml file" +
                          "("+xmlFilePath+")");
                return null;
            }
            while (st.hasMoreTokens()) {
                String tag = st.nextToken();
                NodeList nl = e.getElementsByTagName(tag);
                if (nl.getLength()>0) {
                    e = (Element)nl.item(0);
                } else {
                    // Handle error!
                    return null;
                }
            }
            return e;
        }
    }

    /**
     * @param path Path to the element
     * @return Text value of element
     */
    public String getElementValue(String path) {
        return getElementValue(getElementByPath(path));
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
     * @param path Path to the element
     * @param attr Attribute name
     * @return Value of attribute
     */
    public String getElementAttributeValue(String path, String attr) {
        return getElementAttributeValue(getElementByPath(path),attr);
    }


    /**
     * @param e Element
     * @param attr Attribute name
     * @return Value of attribute
     */
    public String getElementAttributeValue(Element e, String attr) {
        if (e==null)
            return "";
        else
            return e.getAttribute(attr);
    }

    /**
     * @param path Path to the element
     * @return Enumeration of child elements
     */
    public Enumeration getChildElements(String path) {
        return getChildElements(getElementByPath(path));
    }

    /**
     * @param e Element
     * @return Enumeration of child elements
     */
    public Enumeration getChildElements(Element e) {
        return getChildElements(e,"*");
    }

    /**
     * @param path Path to the element
     * @param tag tag to match ("*" means all tags")
     * @return Enumeration of child elements with the given tag
     */
    public Enumeration getChildElements(String path,String tag) {
        return getChildElements(getElementByPath(path),tag);
    }

    /**
     * @param e Element
     * @param tag tag to match ("*" means all tags")
     * @return Enumeration of child elements with the given tag
     */
    public Enumeration getChildElements(Element e,String tag) {
        Vector v = new Vector();
        boolean ignoretag=tag.equals("*");
        if (e!=null) {
            NodeList nl = e.getChildNodes();
            for (int i=0;i<nl.getLength();i++) {
                Node n = nl.item(i);
                if (n.getNodeType() == n.ELEMENT_NODE &&
                    (ignoretag ||
                     ((Element)n).getTagName().equalsIgnoreCase(tag))) {
                    v.addElement(n);
                }
            }
        }
        return v.elements();
    }

    public String getFileName() {
        return xmlFilePath;
    }
}
