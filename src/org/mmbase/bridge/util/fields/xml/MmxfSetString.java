/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.util.fields.xml;
import org.mmbase.bridge.util.fields.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.Node;
import org.mmbase.util.*;
import org.mmbase.util.transformers.XmlField;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import org.mmbase.util.logging.*;

// only used for debugging in main.
import org.apache.xml.serialize.*;

/**
 * Set-processing for an `mmxf' field. This is the counterpart and inverse of {@link MmxfGetString}, for more
 * information see the javadoc of that class.
 * @author Michiel Meeuwissen
 * @version $Id: MmxfSetString.java,v 1.1 2005-05-18 22:06:28 michiel Exp $
 * @since MMBase-1.8
 */

public class MmxfSetString implements  Processor {
    private static final Logger log = Logging.getLoggerInstance(MmxfSetString.class);

    private static XmlField xmlField = new XmlField(XmlField.POOR);
    
    private Document parse(String value)  throws javax.xml.parsers.ParserConfigurationException, org.xml.sax.SAXException,  java.io.IOException {
        try {
            return parse(new java.io.ByteArrayInputStream(value.getBytes("UTF-8")));
        } catch (java.io.UnsupportedEncodingException uee) {
            // cannot happen, UTF-8 is supported..
            return null;
        }

    }
    private Document parse(java.io.InputStream value)  throws javax.xml.parsers.ParserConfigurationException, org.xml.sax.SAXException,  java.io.IOException {
        DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
        dfactory.setValidating(false);
        dfactory.setNamespaceAware(true);
        DocumentBuilder documentBuilder = dfactory.newDocumentBuilder();
        // dont log errors, and try to process as much as possible...
        XMLErrorHandler errorHandler = new XMLErrorHandler(false, org.mmbase.util.XMLErrorHandler.NEVER);
        documentBuilder.setErrorHandler(errorHandler);
        documentBuilder.setEntityResolver(new XMLEntityResolver());
        Document doc = documentBuilder.parse(value);
        if (! errorHandler.foundNothing()) {
            throw new IllegalArgumentException("xml invalid:\n" + errorHandler.getMessageBuffer() + "for xml:\n" + value);
        }
        return doc;
    }

    private Document parseKupu(Document document) {
        // unimplemented.
        return document; 
    }

    private void parseWiki(org.w3c.dom.Node element) {
    }

    private Document parseWiki(Document document) {
        Element root = document.getDocumentElement();
        org.w3c.dom.NodeList childs = root.getChildNodes();
        for (int i = 0; i < childs.getLength(); i++) {
            parseWiki(childs.item(i));
        }
        return document;
    }

    public Object process(Node node, Field field, Object value) {
        

        try {
            switch(MmxfGetString.getMode(node.getCloud().getProperty(Cloud.PROP_XMLMODE))) {
            case MmxfGetString.MODE_KUPU: {
                log.info("Handeling kupu-input: " + value);
                return parseKupu(parse("" + value));
            }
            case MmxfGetString.MODE_WIKI: {
                Document document  = parse(xmlField.transformBack(""  + value));
                return parseWiki(document);
            }
            case MmxfGetString.MODE_FLAT: {
                return parse(xmlField.transformBack("" + value));
            }
            default: {
                // 'raw' xml
                try {
                    return parse("" + value);
                } catch (Exception e) {
                    log.warn("Setting field " + field + " in node " + node.getNumber() + ", but " + e.getMessage());
                    // simply Istore it, as provided, then.
                    // fall trough
                }
                return value;
            } 

            }
        } catch (Exception e) {
            log.error(e);
            return value;
        }
    }

    /**
     * Invocation of the class from the commandline for testing. Uses RMMCI (on the default
     * configuration), gets the 'xmltest' node, and get and set processes it.
     */
    public static void main(String[] argv) {
        if (System.getProperty("mmbase.config") == null) {
            System.err.println("Please start up with -Dmmbase.config=<mmbase configuration directory> (needed to find the XSL's)");
            return;
        }
        Cloud cloud = ContextProvider.getCloudContext("rmi://127.0.0.1:1111/remotecontext").getCloud("mmbase", "anonymous", null);
        Node node = cloud.getNode("xmltest");
        cloud.setProperty(Cloud.PROP_XMLMODE, "wiki");
        
        Processor getProcessor = new MmxfGetString();
        String wiki = (String) getProcessor.process(node, node.getNodeManager().getField("body"), null);

        System.out.println("in:\n" + wiki);
        
        Processor setProcessor = new MmxfSetString();

        System.out.println("\n-------------\nout:\n");
        Document document = (Document) setProcessor.process(node, node.getNodeManager().getField("body"), wiki);
        try{
            XMLSerializer serializer = new XMLSerializer();
            serializer.setNamespaces(true);
            serializer.setOutputByteStream(System.out);
            serializer.serialize(document);
        } catch (java.io.IOException ioe) {
            System.err.println(ioe.getMessage());
        }

        
    }
  
}
