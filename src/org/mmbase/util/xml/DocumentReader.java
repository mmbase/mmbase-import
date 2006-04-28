/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.xml;

import java.util.*;

import java.io.FileInputStream;
import java.io.StringReader;

import org.xml.sax.ErrorHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.mmbase.util.XMLEntityResolver;
import org.mmbase.util.XMLErrorHandler;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * The DocumentReader class provides methods for loading a xml document in memory.
 * It serves as the base class for DocumentWriter (which adds ways to write a document), and 
 * XMLBasicReader, which adds path-like methods with which to retrieve elements.
 *
 * @author Case Roule
 * @author Rico Jansen
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @version $Id: DocumentReader.java,v 1.1 2003-08-18 16:50:52 pierre Exp $
 */
public class DocumentReader  {
    private static Logger log = Logging.getLoggerInstance(DocumentReader.class);

    /** for the document builder of javax.xml. */
    private static Map documentBuilders = Collections.synchronizedMap(new HashMap());     

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
     * This method is called by XMLEntityResolver.
     */
    public static void registerPublicIDs() {
        XMLEntityResolver.registerPublicID(PUBLIC_ID_ERROR_1_0, DTD_ERROR_1_0, DocumentReader.class);
    }

    protected Document document;

    private String xmlFilePath;

    /**  
     * Returns the default setting for validation for DocumentReaders.
     * @todo add a way to configure this value, so validation can be turned off in i.e. production environments
     * @return true if validation is on
     */
    protected static final boolean validate() {
        return true;
    }
    
    /**
     * Creates an input source for a document, based on a filepath
     * If the file cannot be opened, the method returns an inputsource of an error document describing the condition 
     * under which this failed.
     * @param path the path to the file containing the doxument
     * @return the input source to the document.
     */
    private static InputSource getInputSource(String path) {
        try {
            // remove file protocol if present to avoid errors in accessing file
            if (path.startsWith("file://")) path= path.substring(7);
            InputSource is = new InputSource(new FileInputStream(path));
            is.setSystemId("file://" + path);
            return is;
        } catch (java.io.FileNotFoundException e) {
            log.error("Error reading " + path + ": " + e.toString());
            log.service("Using empty source");
            // try to handle more or less gracefully
            InputSource is = new InputSource();
            is.setSystemId(FILENOTFOUND + path);
            is.setCharacterStream(new StringReader("<?xml version=\"1.0\"?>\n" +
                                                   "<!DOCTYPE error PUBLIC \"" + PUBLIC_ID_ERROR + "\"" +
                                                   " \"http://www.mmbase.org/dtd/error_1_0.dtd\">\n" +
                                                   "<error>" + e.toString() + "</error>"));
            return is;
        }
    }

    /**
     * Creates an empty document reader.
     */
    protected DocumentReader() {
    }

    /**
     * Constructs the document by reading it from a file.
     * @param path the path to the file from which to read the document
     */
    public DocumentReader(String path) {
        this(getInputSource(path), validate(), null);
    }

    /**
     * Constructs the document by reading it from a source.
     * You can pass a resolve class to this constructor, allowing you to indicate the package in which the dtd
     * of the document read is to be found. The dtd sould be in the resources package under the package of the class passed.
     * @param path the path to the file from which to read the document
     * @param validating whether to validate the document
     * @param resolveBase the base class whose package is used to resolve dtds, set to null if unknown
     */
    public DocumentReader(String path, boolean validating, Class resolveBase) {
        this(getInputSource(path), validating, resolveBase);
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
     * You can pass a resolve class to this constructor, allowing you to indicate the package in which the dtd
     * of the document read is to be found. The dtd sould be in the resources package under the package of the class passed.
     * @param source the input source from which to read the document
     * @param validating whether to validate the document
     * @param resolveBase the base class whose package is used to resolve dtds, set to null if unknown
     */
    public DocumentReader(InputSource source, boolean validating, Class resolveBase) {
        try {
            xmlFilePath = source.getSystemId();
            XMLEntityResolver resolver = null;
            if (resolveBase != null) resolver = new XMLEntityResolver(validating, resolveBase);
            DocumentBuilder dbuilder = DocumentReader.getDocumentBuilder(validating, null, resolver);
            if(dbuilder == null) throw new RuntimeException("failure retrieving document builder");
            if (log.isDebugEnabled()) log.debug("Reading " + source.getSystemId());
            document = dbuilder.parse(source);
        } catch(org.xml.sax.SAXException se) {
            throw new RuntimeException("failure reading document: " + source.getSystemId() + "\n" + Logging.stackTrace(se));
        } catch(java.io.IOException ioe) {
            throw new RuntimeException("failure reading document: " + source.getSystemId() + "\n" + ioe);
        }
    }

    /**
     * Creates a DocumentBuilder using SAX.
     * @param validating if true, the documentbuilder will validate documents read
     * @param handler a ErrorHandler class to use for catching parsing errors, pass null to use a default handler
     * @param resolver a EntityResolver class used for resolving the document's dtd, pass null to use a default resolver
     * @return a DocumentBuilder instance, or null if none could be created
     */
    private static DocumentBuilder createDocumentBuilder(boolean validating, ErrorHandler handler, EntityResolver resolver) {
        DocumentBuilder db;
        if (handler == null) handler = new XMLErrorHandler();
        if (resolver == null) resolver = new XMLEntityResolver(validating);
        try {
            // get a new documentbuilder...
            DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
            // get document builder AFTER setting the validation
            dfactory.setValidating(validating);
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
     * Creates a DocumentBuilder.
     * DocumentBuilders that use the default error handler or entity resolver are cached (one for validating, 
     * one for non-validating document buidlers).
     * @param validating if true, the documentbuilder will validate documents read
     * @param handler a ErrorHandler class to use for catching parsing errors, pass null to use the default handler
     * @param resolver a EntityResolver class used for resolving the document's dtd, pass null to use the default resolver
     * @return a DocumentBuilder instance, or null if none could be created
     */
    public static DocumentBuilder getDocumentBuilder(boolean validating, ErrorHandler handler, EntityResolver resolver) {
        if (handler == null && resolver == null) {
            DocumentBuilder db = (DocumentBuilder) documentBuilders.get(new Boolean(validating));
            if (db == null) {
                db = createDocumentBuilder(validating, null, null);
                documentBuilders.put(new Boolean(validating), db);
            }
            return db;
        } else {
            return createDocumentBuilder(validating, handler, resolver);
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
    public String getNodeTextValue(Node n) {
        NodeList nl = n.getChildNodes();
        StringBuffer res = new StringBuffer();
        for (int i = 0; i < nl.getLength(); i++) {
            Node textnode = nl.item(i);
            if (textnode.getNodeType() == Node.TEXT_NODE) {
                res.append(textnode.getNodeValue().trim());
            } else if (textnode.getNodeType() == Node.CDATA_SECTION_NODE) {
                res.append(textnode.getNodeValue());
            }
        }
        return res.toString();
    }

    /**
     * Returns the systemID of the InputSource used to read the document.
     * This is generally the document's file path.
     * @return the systemID as a String
     */
    public String getFileName() {
        return xmlFilePath;
    }
}
