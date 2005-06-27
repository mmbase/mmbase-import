/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.util.fields.xml;
import org.mmbase.bridge.util.fields.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.xml.Generator;
import org.mmbase.bridge.Node;
import org.mmbase.util.logging.*;
import org.mmbase.util.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.parsers.*;

import java.util.*;
import java.io.*;
import org.w3c.dom.*;


/**
 * This class implements the `get' for `mmxf' fields.
 *
 * @author Michiel Meeuwissen
 * @version $Id: MmxfGetString.java,v 1.4 2005-06-27 17:01:21 michiel Exp $
 * @since MMBase-1.8
 */

public class MmxfGetString implements  Processor {
    private static final Logger log = Logging.getLoggerInstance(MmxfGetString.class);

    public static final int MODE_XML   = 0;
    public static final int MODE_PRETTYXML   = 1;
    public static final int MODE_FLAT  = 2;
    public static final int MODE_WIKI  = 3;
    public static final int MODE_KUPU  = 4;
    

    public static int getMode(Object mode) {
        if ("xml".equals(mode)) {
            return MODE_XML;
        } else if ("prettyxml".equals(mode)) {
            return MODE_PRETTYXML;
        } else if ("flat".equals(mode)) {
            return MODE_FLAT;
        } else if ("wiki".equals(mode)) {
            return MODE_WIKI;
        } else if ("kupu".equals(mode)) {
            return MODE_KUPU;
        } else {
            log.warn("Unknown mode " + mode);
            return MODE_XML;
        }
    }


    public static Document getDocument(Node node, Field field)  {
        try {
           DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
           dfactory.setNamespaceAware(true);
           DocumentBuilder documentBuilder = dfactory.newDocumentBuilder();
           org.xml.sax.ErrorHandler handler = new org.mmbase.util.XMLErrorHandler();
           documentBuilder.setErrorHandler(handler);
           documentBuilder.setEntityResolver( new org.mmbase.util.XMLEntityResolver());
           Generator generator = new Generator(documentBuilder, node.getCloud());
           generator.setNamespaceAware(true);
           generator.add(node, field);
           generator.add(node.getRelatedNodes("images", "idrel", "both"));
           generator.add(node.getRelations("idrel", "images"));
           
           generator.add(node.getRelatedNodes("urls", "idrel", "both"));
           generator.add(node.getRelations("idrel", "urls"));


           generator.add(node.getRelatedNodes("segments", "idrel", "both"));
           generator.add(node.getRelations("idrel", "segments"));

           generator.add(node.getRelatedNodes("attachments", "idrel", "both"));
           generator.add(node.getRelations("idrel", "attachments"));
           
           return generator.getDocument();
        } catch (ParserConfigurationException pce) {
            throw new RuntimeException(pce.getMessage(), pce);
        }
    }

    public Object process(Node node, Field field, Object value) {
        log.info("Getting " + field + " from " + node + " as a String");
        
        try {
            switch(getMode(node.getCloud().getProperty(Cloud.PROP_XMLMODE))) {
            case MODE_KUPU: {
                //
                log.debug("Generating kupu-compatible XML for" + value);
                Document xml = getDocument(node, field);
                java.net.URL u = ResourceLoader.getConfigurationRoot().getResource("xslt/mmxf2kupu.xslt");
                java.io.StringWriter res = new java.io.StringWriter();
                // TODO: XSL transformation parameter stuff must be generalized (not only cloud, but only e.g. request specific stuff must be dealt with).
                Map params = new HashMap();
                params.put("cloud", node.getCloud());
                XSLTransformer.transform(new DOMSource(xml), u, new StreamResult(res), params);
                return res.toString();
            }
            case MODE_WIKI: {
                log.debug("Generating 'wiki'  for" + value);
                Document xml = getDocument(node, field);
                java.net.URL u = ResourceLoader.getConfigurationRoot().getResource("xslt/2rich.xslt");
                java.io.StringWriter res = new java.io.StringWriter();
                Map params = new HashMap();
                params.put("cloud", node.getCloud());
                XSLTransformer.transform(new DOMSource(xml), u, new StreamResult(res), params);
                return res.toString();
            }
            case MODE_FLAT: {
                log.debug("Generating 'flat'  for" + value);
                Document xml = getDocument(node, field);
                java.net.URL u = ResourceLoader.getConfigurationRoot().getResource("xslt/mmxf2rich.xslt");
                java.io.StringWriter res = new java.io.StringWriter();
                Map params = new HashMap();
                params.put("cloud", node.getCloud());
                XSLTransformer.transform(new DOMSource(xml), u, new StreamResult(res), params);
                return res.toString();
            }
            case MODE_PRETTYXML: {
                // get the XML from this thing....
                // javax.xml.parsers.DocumentBuilderFactory dfactory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
                // javax.xml.parsers.DocumentBuilder dBuilder = dfactory.newDocumentBuilder();
                // org.w3c.dom.Element xml = node.getXMLValue(field.getName(), dBuilder.newDocument());
                Document xml = node.getXMLValue(field.getName());
                
                if(xml != null) {
                    return org.mmbase.util.xml.XMLWriter.write(xml, true, true);
                }
                return "";
                
            }                
            case MODE_XML:
            default:
                Document xml = node.getXMLValue(field.getName());
                
                if(xml != null) {
                    // make a string from the XML
                    return org.mmbase.util.xml.XMLWriter.write(xml, false, true);
                }
                return "";
            }          
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return value;
        }
    }
  
}
