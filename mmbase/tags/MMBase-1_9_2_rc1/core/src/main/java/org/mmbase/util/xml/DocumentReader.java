/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.xml;

import java.util.*;
import java.util.regex.Pattern;

import org.xml.sax.InputSource;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * The DocumentReader class provides methods for loading a xml document in memory.
 * It serves as the base class for DocumentWriter (which adds ways to write a document), and
 * XMLBasicReader, which adds path-like methods with which to retrieve elements.
 *
 * This can also be a class for general static dom utilities.
 *
 *
 * @author Case Roule
 * @author Rico Jansen
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.7
 */
public class DocumentReader  {
    private static Logger log = Logging.getLoggerInstance(DocumentReader.class);

    /** for the document builder of javax.xml. */
    private static Map<String, DocumentBuilder> documentBuilders = Collections.synchronizedMap(new HashMap<String, DocumentBuilder>());

    protected static final String FILENOTFOUND = "FILENOTFOUND://";

    /** Public ID of the Error DTD version 1.0 */
    public static final String PUBLIC_ID_ERROR_1_0 = "-//MMBase//DTD error 1.0//EN";
    /** DTD resource filename of the Error DTD version 1.0 */
    public static final String DTD_ERROR_1_0 = "error_1_0.dtd";

    /** Public ID of the most recent Error DTD */
    public static final String PUBLIC_ID_ERROR = PUBLIC_ID_ERROR_1_0;
    /** DTD respource filename of the most recent Error DTD */
    public static final String DTD_ERROR = DTD_ERROR_1_0;

    /**
     * Register the Public Ids for DTDs used by XMLBasicReader
     * This method is called by EntityResolver.
     */
    public static void registerPublicIDs() {
        EntityResolver.registerPublicID(PUBLIC_ID_ERROR_1_0, DTD_ERROR_1_0, DocumentReader.class);
    }

    protected Document document;

    private String systemId;

    static UtilReader.PropertiesMap<String> utilProperties = null;
    /**
     * Returns the default setting for validation for DocumentReaders.
     * @return true if validation is on
     */
    public static final boolean validate() {
        Object validate = utilProperties == null ? null : utilProperties.get("validate");
        return validate == null || validate.equals("true");
    }

    /**
     * Whether to validate given a request for that. So, the request is followed, unless it is configured to 'never' validate.
     * @since MMBase-1.8
     */
    protected static final boolean validate(boolean requested) {
        Object validate = utilProperties == null ? null : utilProperties.get("validate");
        if (validate != null && validate.equals("never")) return false;
        return requested;
    }


    /**
     * Creates an empty document reader.
     */
    protected DocumentReader() {
    }

    /**
     * Constructs the document by reading it from a source.
     * @param source the input source from which to read the document
     */
    public DocumentReader(InputSource source) {
        this(source, validate(), null);
    }

    /**
     * Constructs the document by reading it from a source.
     * @param source the input source from which to read the document
     * @param validating whether to validate the document
     */
    public DocumentReader(InputSource source, boolean validating) {
        this(source, validating, null);
    }

    /**
     * Constructs the document by reading it from a source.
     * You can pass a resolve class to this constructor, allowing you to indicate the package in which the dtd
     * of the document read is to be found. The dtd sould be in the resources package under the package of the class passed.
     * @param source the input source from which to read the document
     * @param resolveBase the base class whose package is used to resolve dtds, set to null if unknown
     */
    public DocumentReader(InputSource source, Class<?> resolveBase) {
        this(source, DocumentReader.validate(), resolveBase);
    }

    /**
     * Constructs the document by reading it from a source.
     * You can pass a resolve class to this constructor, allowing you to indicate the package in which the dtd
     * of the document read is to be found. The dtd sould be in the resources package under the package of the class passed.
     * @param source the input source from which to read the document
     * @param validating whether to validate the document
     * @param resolveBase the base class whose package is used to resolve dtds, set to null if unknown
     */
    public DocumentReader(InputSource source, boolean validating, Class<?> resolveBase) {
        if (source == null) {
            throw new IllegalArgumentException("InputSource cannot be null");
        }
        try {
            systemId = source.getSystemId();
            org.xml.sax.EntityResolver resolver = null;
            if (resolveBase != null) resolver = new EntityResolver(validating, resolveBase);
            DocumentBuilder dbuilder = getDocumentBuilder(validating, null/* no error handler */, resolver);
            if(dbuilder == null) throw new RuntimeException("failure retrieving document builder");
            if (log != null && log.isDebugEnabled()) {
                log.debug("Reading " + source.getSystemId());
            }
            document = dbuilder.parse(source);
        } catch(org.xml.sax.SAXException se) {
            throw new RuntimeException("failure reading document: " + source.getSystemId() + "\n" + Logging.stackTrace(se));
        } catch(java.io.IOException ioe) {
            throw new RuntimeException("failure reading document: " + source.getSystemId() + "\n" + ioe, ioe);
        }
    }

    /**
     * @since MMBase-1.8
     */
    public DocumentReader(Document doc) {
        document = doc;
        systemId = doc.getDocumentURI();
    }


    private static boolean warnedJAXP12 = false;
    private static boolean warnedXinclude = false;
    /**
     * Creates a DocumentBuilder using SAX.
     * @param validating if true, the documentbuilder will validate documents read
     * @param xsd     Whether to use XSD for validating
     * @param handler a ErrorHandler class to use for catching parsing errors, pass null to use a default handler
     * @param resolver a EntityResolver class used for resolving the document's dtd, pass null to use a default resolver
     * @return a DocumentBuilder instance, or null if none could be created
     */
    private static DocumentBuilder createDocumentBuilder(boolean validating, boolean xsd, org.xml.sax.ErrorHandler handler, org.xml.sax.EntityResolver resolver) {
        DocumentBuilder db;
        if (handler == null) handler = new ErrorHandler();
        if (resolver == null) resolver = new EntityResolver(validating);
        try {
            // get a new documentbuilder...
            DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
            // get document builder AFTER setting the validation
            dfactory.setValidating(validating);
            try {
                dfactory.setXIncludeAware(true);
            } catch(Exception e) {
                if (! warnedXinclude) {
                    log.warn(e + " Your current document builder factory does not support xi:include.");
                    warnedXinclude = true;
                }
            }
            if (validating && xsd) {
                try {
                    dfactory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage",
                                          "http://www.w3.org/2001/XMLSchema");
                } catch (IllegalArgumentException iae) {
                    if (! warnedJAXP12) {
                        log.warn("The XML parser does not support JAXP 1.2, XSD validation will not work.", iae);
                        warnedJAXP12 = true;
                    }
                }
            }
            dfactory.setNamespaceAware(true);

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

    /**
     * Creates a DocumentBuilder with default settings for handler, resolver, or validation,
     * obtaining it from the cache if available.
     * @return a DocumentBuilder instance, or null if none could be created
     */
    public static DocumentBuilder getDocumentBuilder() {
        return getDocumentBuilder(validate(), null, null);
    }


    /**
     * Obtain a DocumentBuilder
     */
    public static DocumentBuilder getDocumentBuilder(boolean validating) {
        return DocumentReader.getDocumentBuilder(validating, null, null);
    }

    /**
     * See {@link #getDocumentBuilder(boolean, ErrorHandler, EntityResolver)}
     */
    public static DocumentBuilder getDocumentBuilder(boolean validating, org.xml.sax.ErrorHandler handler, org.xml.sax.EntityResolver resolver) {
        return getDocumentBuilder(validating, false, handler, resolver);
    }

    /**
     * Creates a DocumentBuilder.
     * DocumentBuilders that use the default error handler or entity resolver are cached (one for validating,
     * one for non-validating document buidlers).
     * @param validating if true, the documentbuilder will validate documents read
     * @param xsd        if true, validating will be done by an XML schema definiton.
     * @param handler a ErrorHandler class to use for catching parsing errors, pass null to use the default handler
     * @param resolver a EntityResolver class used for resolving the document's dtd, pass null to use the default resolver
     * @return a DocumentBuilder instance, or null if none could be created
     * @since MMBase-1.8.
     */
    public static DocumentBuilder getDocumentBuilder(boolean validating, boolean xsd, org.xml.sax.ErrorHandler handler, org.xml.sax.EntityResolver resolver) {
        validating = validate(validating);
        if (handler == null && resolver == null) {
            String key = "" + validating + xsd;
            DocumentBuilder db = documentBuilders.get(key);
            if (db == null) {
                db = createDocumentBuilder(validating, xsd, null, null);
                documentBuilders.put(key, db);
            }
            return db;
        } else {
            return createDocumentBuilder(validating, xsd, handler, resolver);
        }
    }

    /**
     * Return the text value of a node.
     * It includes the contents of all child textnodes and CDATA sections, but ignores
     * everything else (such as comments)
     * The code trims excessive whitespace unless it is included in a CDATA section.
     *
     * @param n the Node whose value to determine
     * @return a String representing the node's textual value
     */
    public static String getNodeTextValue(Node n) {
        return getNodeTextValue(n, true);
    }
    /**
     * @since MMBase-1.8.5
     */
    public static String getNodeTextValue(Node n, boolean trim) {
        NodeList nl = n.getChildNodes();
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < nl.getLength(); i++) {
            Node textnode = nl.item(i);
            if (textnode.getNodeType() == Node.TEXT_NODE) {
                String s = textnode.getNodeValue();
                if (trim) s = s.trim();
                res.append(s);
            } else if (textnode.getNodeType() == Node.CDATA_SECTION_NODE) {
                res.append(textnode.getNodeValue());
            }
        }
        return res.toString();
    }

    /**
     * @since MMBase-1.8.1
     */
    public static void setNodeTextValue(Node n, String value) {
        Node child = n.getFirstChild();
        while (child != null) {
            Node next = child.getNextSibling();
            n.removeChild(child);
            child = next;
        }
        Text text = n.getOwnerDocument().createTextNode(value);
        n.appendChild(text);
    }


    /**
     * @since MMBase-1.8.5
     */
    public static void setPrefix(Document d, String ns, String prefix) {
        NodeList nl = d.getElementsByTagName("*");
        for (int i = 0; i < nl.getLength(); i++) {
            Node element = nl.item(i);
            if (ns.equals(element.getNamespaceURI())) {
                element.setPrefix(prefix);
            }
        }
    }

    /**
     * Returns whether an element has a certain attribute, either an unqualified attribute or an attribute that fits in the
     * passed namespace
     */
    static public boolean hasAttribute(Element element, String nameSpace, String localName) {
        return element.hasAttributeNS(nameSpace,localName) || element.hasAttribute(localName);
    }

    /**
     * Returns the value of a certain attribute, either an unqualified attribute or an attribute that fits in the
     * passed namespace
     */
    static public String getAttribute(Element element, String nameSpace, String localName) {
        if (element.hasAttributeNS(nameSpace, localName)) {
            return element.getAttributeNS(nameSpace, localName);
        } else {
            return element.getAttribute(localName);
        }
    }

    /**
     * Utility method to make a document of an element.
     * @since MMBase-1.8
     */
    static public Document toDocument(Element element) {
        DocumentBuilder documentBuilder = getDocumentBuilder(false, null, null);
        DOMImplementation impl = documentBuilder.getDOMImplementation();
        Document document = impl.createDocument(element.getNamespaceURI(), element.getLocalName(), null);
        Element dest = document.getDocumentElement();
        Element copy = (Element) document.importNode(element, false);
        NamedNodeMap attributes = copy.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Attr attribute = (Attr) (attributes.item(i).cloneNode(true));
            dest.setAttributeNode(attribute);

        }
        NodeList childs = element.getChildNodes();
        for (int i = 0; i < childs.getLength() ; i++) {
            Node child = document.importNode(childs.item(i), true);
            dest.appendChild(child);
        }
        document.normalize();
        return document;
    }


    /**
     * Appends a child to a parent at the right position. The right position is defined by a comma
     * separated list of regular expressions.  If the the child matches the last element of the
     * path, then the child is appended after similer childs, if not, then it will be appended
     * before them.
     *
     * @param parent The parent element, to which a new child will be added
     * @param newChild this new child
     * @param path The beforementioned comma separated list of regexps. See also {@link
     * java.util.regex.Pattern};
     * Namespace prefixes are ignored.
     * @since MMBase-1.8
     */
    static public void appendChild(Element parent, Element newChild, String path) {
        String[] p = path.split(",");
        int i = 0;
        Node refChild = null;
        NodeList childs = parent.getChildNodes();
        int j = 0;
        Pattern pattern = null;
        if (p.length > 0) pattern = Pattern.compile("\\A" + p[i] + "\\z");
        boolean matching = false;
        while (j < childs.getLength() && i < p.length) {
            if (childs.item(j) instanceof Element) {
                Element child = (Element) childs.item(j);
                if (pattern.matcher(child.getLocalName()).matches()) {
                    j++;
                    refChild = childs.item(j);
                    matching = true;
                } else {
                    if (! matching) { // append at the beginning, because actual child list does not start llike path
                        refChild = childs.item(j);
                        break;
                    }
                    i++;
                    pattern = i < p.length ? Pattern.compile("\\A" + p[i] + "\\z") : null;
                }
            } else {
                j++;
            }
        }
        parent.insertBefore(newChild, refChild);
    }

    /**
     * Returns the systemID of the InputSource used to read the document.
     * This is generally the document's file path.
     * @return the systemID as a String
     *
     * @since MMBase-1.8
     */
    public String getSystemId() {
        return systemId;
    }

    /**
     * @since MMBase-1.8
     */
    public void setSystemId(String url) {
        systemId = url;
    }

    /**
     * @param e Element
     * @return Tag name of the element
     */
    public String getElementName(Element e) {
        return e.getLocalName();
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
        if (e == null) {
            return "";
        } else {
            return e.getAttribute(attr);
        }
    }

    /**
     * Determine the root element of the contained document
     * @return root element
     * @deprecated
     */
    public Element getRootElement() {
        if (document == null) {
            log.error("Document is not defined, cannot get root element");
        }
        return document.getDocumentElement();
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
        return getElementByPath(document.getDocumentElement(), path);
    }

    /**
     * @param e Element from which the "relative" path is starting.
     *          NB the path starts with the name of the root element.
     * @param path Dot-separated list of tags describing path from root element to requested element
     * @return Leaf element of the path
     */
    public static Element getElementByPath(Element e, String path) {
        StringTokenizer st = new StringTokenizer(path,".");
        if (!st.hasMoreTokens()) {
            // faulty path
            log.error("No tokens in path");
            return null;
        } else {
            String root = st.nextToken();
            if (e.getLocalName().equals("error")) {
                // path should start with document root element
                log.error("Error occurred : (" + getElementValue(e) + ")");
                return null;
            } else if (!e.getLocalName().equals(root)) {
                // path should start with document root element
                log.error("path [" + path + "] with root (" + root + ") doesn't start with root element (" + e.getLocalName() + "): incorrect xml file" +
                          "(" + e.getOwnerDocument().getDocumentURI() + ")");
                return null;
            }
            OUTER:
            while (st.hasMoreTokens()) {
                String tag = st.nextToken();
                NodeList nl = e.getChildNodes();
                for(int i = 0; i < nl.getLength(); i++) {
                    if (! (nl.item(i) instanceof Element)) continue;
                    e = (Element) nl.item(i);
                    String tagName = e.getLocalName();
                    if (tagName == null || tagName.equals(tag) || "*".equals(tag)) continue OUTER;
                }
                // Handle error!
                return null;
            }
            return e;
        }
    }


    /**
     * @param path Path to the element
     * @return Text value of element
     */
    public  String getElementValue(String path) {
        return getElementValue(getElementByPath(path));
    }

    /**
     * @param e Element
     * @return Text value of element
     */
    public static String getElementValue(Element e) {
        if (e == null) {
            return "";
        } else {
            return getNodeTextValue(e);
        }
    }

    /**
     * @param path Path to the element
     * @return a <code>List</code> of child elements
     */
    public List<Element> getChildElements(String path) {
        return getChildElements(getElementByPath(path));
    }

    /**
     * @param e Element
     * @return a <code>List</code> of child elements
     */
    public static List<Element> getChildElements(Element e) {
        return getChildElements(e, "*");
    }

    /**
     * @param path Path to the element
     * @param tag tag to match ("*" means all tags")
     * @return a <code>List</code> of child elements with the given tag
     */
    public List<Element> getChildElements(String path, String tag) {
        return getChildElements(getElementByPath(path), tag);
    }

    /**
     * @param e Element
     * @param tag tag to match ("*" means all tags")
     * @return a <code>List</code> of child elements with the given tag
     */
    public static List<Element> getChildElements(Element e, String tag) {
        List<Element> v = new ArrayList<Element>();
        boolean ignoretag = tag.equals("*");
        if (e != null) {
            NodeList nl = e.getChildNodes();
            for (int i = 0; i < nl.getLength(); i++) {
                Node n = nl.item(i);
                if (n.getLocalName() == null) continue;
                if (n.getNodeType() == Node.ELEMENT_NODE &&
                    (ignoretag ||
                     ((Element)n).getLocalName().equalsIgnoreCase(tag))) {
                    v.add((Element) n);
                }
            }
        }
        return v;
    }

    /**
     * @since MMBase-1.9
     */
    public Document getDocument() {
        return document;
    }

    public static void main(String argv[]) throws Exception {
        org.mmbase.util.ResourceLoader.getSystemRoot().getDocument(argv[0]);
    }
}
