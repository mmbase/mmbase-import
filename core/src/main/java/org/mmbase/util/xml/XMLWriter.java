/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package org.mmbase.util.xml;

import java.io.*;

import org.w3c.dom.*;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.mmbase.util.logging.*;
/**
 * Util class to serialize xml (wrapper around javax.xml.transform.Transformer)
 * @author Kees Jongenburger <keesj@dds.nl>
 * @since MMBase-1.7
 **/
public class XMLWriter {
    private static Logger log = Logging.getLoggerInstance(XMLWriter.class);

    /**
     * defaulting version of {@link #write(Node, Writer, boolean, boolean)}. (Not ommitting xml declaration).
     */
    public static void write(Node node, Writer writer, boolean indent) throws TransformerConfigurationException, TransformerException{
        write(node, writer, indent, false);
    }
    /**
     * static method to serialize an DOM document
     * @param node the node to serialize
     * @param writer the writer to write the node to
     * @param indent if true the document wil be indented
     * @param omitxml
     **/
    public static void write(Node node, Writer writer, boolean indent, boolean omitxml) throws TransformerConfigurationException, TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        try {
            transformerFactory.setAttribute("http://saxon.sf.net/feature/version-warning", false);
        } catch (IllegalArgumentException iae) {
            // never mind
        }
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, indent ? "yes" : "no");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, omitxml ? "yes" : "no");
        if (! omitxml) {
            Document d = node.getOwnerDocument();
            if (d != null) {
                DocumentType dt = d.getDoctype();
                if (dt != null) {
                    transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, dt.getPublicId());
                    transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, dt.getSystemId());
                }
            }
        }
        transformer.transform(new DOMSource(node), new StreamResult(writer));
    }

    /**
     * Defaulting version of {@link #write(Node, boolean, boolean)}. (Not ommitting xml
     * declaration).
     */
    public static String write(Node node, boolean indent) {
        return write(node, indent, false);
    }

    /**
     * @since MMBase-1.9
     */
    public static String write(Node node) {
        return write(node, false);
    }
    /**
     * @since MMBase-1.9
     */
    public static String write(java.util.Collection<? extends Node> c) {
        StringBuilder b = new StringBuilder();
        for (Node n : c) {
            b.append(write(n));
        }
        return b.toString();
    }
    /**
     * static method to serialize a node to a string
     * @param node the node to serialize
     * @param indent , if true the node wil be indented
     * @param omitxml
     * @return the string represneation of the xml of null if an error occured
     **/
    public static String write(Node node, boolean indent, boolean omitxml) {
        try {
            StringWriter sw = new StringWriter();
            write(node, sw, indent, omitxml);
            return sw.toString();
        } catch  (Exception e){
            //sorry for this message. but this is a util class that just has to do the jobs
            //if it fails i can't help it
            log.fatal("error in XMLWriter. it must be possible to write any node to xml withoud errors:{"+ e.getMessage() +"} "  + Logging.stackTrace(e));
        }
        return null;
    }

    private XMLWriter() {
    }
}
