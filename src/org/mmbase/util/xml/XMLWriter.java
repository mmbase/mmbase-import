/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */
package org.mmbase.util.xml;

import java.io.*;

import org.w3c.dom.Document;

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
    /** logger for the writer **/
    private static Logger log = Logging.getLoggerInstance(XMLWriter.class.getName());
    
    /**
     * static method to serialize an DOM document
     * @param document the document to serialize
     * @param writer the writer to write the document to
     * @param indent if true the document wil be indented
     **/
    public static void write(Document document,Writer writer,boolean indent) throws TransformerConfigurationException,TransformerException{
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT,(indent)?"yes":"no");
        transformer.transform(new DOMSource(document),new StreamResult(writer));
    }
    
    /**
     * static method to serialize a document to a string
     * @param document the document to serialize
     * @param indent , if true the document wil be indented
     * @return the string represneation of the xml of null if an error occured
     **/
    public static String write(Document document,boolean indent) {
        try {
            StringWriter sw = new StringWriter();
            write(document,sw, indent);
            return sw.toString();
        } catch  (Exception e){
            //sorry for this message. but this is a util class that just has to do the jobs
            //if it fails i can't help it
            log.fatal("error in XMLWriter. it must be possible to write any document to xml withoud errors:{"+ e.getMessage() +"} "  + Logging.stackTrace(e));
        }
        return null;
    }
}
