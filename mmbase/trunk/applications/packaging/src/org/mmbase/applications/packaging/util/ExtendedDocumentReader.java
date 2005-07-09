/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.packaging.util;

import java.util.*;

import javax.xml.parsers.DocumentBuilder;

import org.mmbase.util.logging.*;
import org.mmbase.util.xml.DocumentReader;
import org.mmbase.util.*;
import org.w3c.dom.*;
import org.xml.sax.*;

/**
 * XMLBasicReader has two goals.
 *  <ul>
 *    <li>It provides a way for parsing XML</li>
 *    <li>It provides a way for searching in this XML, without the need for an XPath implementation, and without the hassle of org.w3c.dom alone.
 *    It uses dots to lay a path in the XML (XPath uses slashes).</li>
 *  </ul>
 *
 * @author Case Roule
 * @author Rico Jansen
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @version $Id: ExtendedDocumentReader.java,v 1.2 2005-07-09 11:46:11 nklasens Exp $
 */
public class ExtendedDocumentReader extends DocumentReader {
    private static final Logger log = Logging.getLoggerInstance(ExtendedDocumentReader.class);

    public ExtendedDocumentReader(String path) {
        super(path);
    }

    public ExtendedDocumentReader(String path, boolean validating) {
        super(path, validating, null);
    }

    public ExtendedDocumentReader(String path, Class resolveBase) {
        super(path, DocumentReader.validate(), resolveBase);
    }

    public ExtendedDocumentReader(InputSource source) {
        super(source, DocumentReader.validate(), null);
    }

    public ExtendedDocumentReader(InputSource source, boolean validating) {
        super(source, validating, null);
    }

    public ExtendedDocumentReader(InputSource source, Class resolveBase) {
        super(source, DocumentReader.validate(), resolveBase);
    }

    public ExtendedDocumentReader(InputSource source, boolean validating, Class resolveBase) {
        super(source, validating, resolveBase);
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
            if (e.getNodeName().equals("error")) {
                // path should start with document root element
                log.error("Error occurred : (" + getElementValue(e) + ")");
                return null;
            } else if (!e.getNodeName().equals(root)) {
                // path should start with document root element
                log.error("path ["+path+"] with root ("+root+") doesn't start with root element ("+e.getNodeName()+"): incorrect xml file" +
                          "("+getSystemId()+")");
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
            return getNodeTextValue(e);
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
                if (n.getNodeType() == Node.ELEMENT_NODE &&
                    (ignoretag ||
                     ((Element)n).getTagName().equalsIgnoreCase(tag))) {
                    v.addElement(n);
                }
            }
        }
        return v.elements();
    }


    public static DocumentBuilder getDocumentBuilder(boolean validating, ErrorHandler handler, EntityResolver resolver) {
        return DocumentReader.getDocumentBuilder(validating, handler, resolver);
    }

    public static DocumentBuilder getDocumentBuilder(Class refer) {
        return DocumentReader.getDocumentBuilder(DocumentReader.validate(), null, new XMLEntityResolver(DocumentReader.validate(), refer));
    }
}
