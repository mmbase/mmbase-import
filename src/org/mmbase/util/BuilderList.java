/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.io.*;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Document;


/**
 * Gives an xml-representation of a dir structure with builders
 * Used by the build script to create documentation for builders.
 * @since mmbase 1.6
 * @author Gerard van Enk
 * @author Pierre van Rooden
 * @version $Id$
 */
public class BuilderList {
    // logger not used at the moment
    //private static Logger log = Logging.getLoggerInstance(BuilderList.class.getName());

   /**
     * Generates the document and writes it to the result object.
     * @param result the StreamResult object where to store the configuration'
     */
    public void write(Document doc, StreamResult result) throws IOException, TransformerException {
        TransformerFactory tfactory = TransformerFactory.newInstance();
        tfactory.setURIResolver(new org.mmbase.util.xml.URIResolver(new java.io.File("")));
        // This creates a transformer that does a simple identity transform,
        // and thus can be used for all intents and purposes as a serializer.
        Transformer serializer = tfactory.newTransformer();
        // sets indent amount for xalan
        // should be done elsewhere, but where?
        serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        // xml output configuration
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");
        serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        serializer.transform(new DOMSource(doc), result);
    }

    /**
     * Lists all builders within a given path, including builders in sub-paths
     * @param ipath the path to start searching. The path need be closed with a File.seperator character.
     */
    void listBuilders(ResourceLoader config, Writer writer) throws IOException {
        Set xmls = config.getResourcePaths(ResourceLoader.XML_PATTERN, false);
        writer.write("<buildertype name=\"" + config.getContext() + "\">\n");
        Iterator i = xmls.iterator();
        while (i.hasNext()) {
            String name = (String) i.next();
            try {
                Document document = config.getDocument(name);
                //only process builder config files
                if (document.getDocumentElement().getTagName().equals("builder")) {
                    write(document, new StreamResult(writer));
                }
            } catch (Exception e) {
            }
        }
        writer.write("</buildertype>\n");
        Iterator j =  config.getChildContexts(null,  false).iterator();
        while (j.hasNext()) {
            String sub = (String) j.next();
            if ("CVS".equals(sub)) continue;
            listBuilders(config.getChildResourceLoader(sub), writer);
        }


    }

    /**
     * Main method can be called from an Ant build file and will return
     * the xml with a listing of all the builders
     *
     * @param args base dir to start with, it's possible to use more than one dir seperated by ;
     */
    public static void main(String[] args) throws UnsupportedEncodingException, IOException {
        if (args.length != 0) {
            BuilderList bulList = new BuilderList();
            Writer s = new OutputStreamWriter(System.out, "UTF-8");
            s.write("<builders>\n");
            String[] builderDirs = args[0].split(";");
            for (int i = 0; i < builderDirs.length ; i++) {
                ResourceLoader config = ResourceLoader.getConfigurationRoot().getChildResourceLoader(builderDirs[i]);
                bulList.listBuilders(config, s);
            }
            s.write("</builders>\n");
            s.flush();
        } else {
            System.out.println("usage: java BuilderList <basedirwithbuilderconfig>");
        }
    }
}
