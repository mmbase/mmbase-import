/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
*/
package org.mmbase.util;

import java.io.*;

import org.xml.sax.*;
import org.apache.xerces.parsers.*;

import org.apache.xalan.*;
import org.apache.xalan.xslt.*;

/**
 * Make XSL Transformations
 *
 * XXX I don't know yet how to fit this into MMBase. I want to pass paths 
 * to both XML en XSL files and I don't think it looks good to do this
 * with the $MOD method calling syntax. On the other hand, I don't 
 * want a server doing the conversion as I want to be able to use the 
 * standard MMBase <SECURE> tag.
 *
 * @author Case Roole, cjr@dds.nl
 * @version $Id: XSLTransformer.java,v 1.1 2000-08-09 12:45:24 case Exp $
 *
 * $Log: not supported by cvs2svn $
 */
public class XSLTransformer {
    private XSLTProcessor processor;
    /**
     * empty constructor
     */
    public XSLTransformer() {}

    /**
     * Transform an XML document using a certain XSL document. 
     *
     * @param xmlPath Path to XML file
     * @param xslPath Path to XSL file
     * @return String with converted XML document
     */
    public String transform(String xmlPath, String xslPath) {
        try {
            processor = XSLTProcessorFactory.getProcessor();

            StringWriter res = new StringWriter();

            // Create the 3 objects the XSLTProcessor needs to perform the transformation.
            XSLTInputSource xmlSource = new XSLTInputSource (xmlPath);
            XSLTInputSource xslSheet = new XSLTInputSource (xslPath);
            XSLTResultTarget xmlResult = new XSLTResultTarget (res);

            // Perform the transformation.
            processor.process(xmlSource, xslSheet, xmlResult);

            return res.toString();
        } catch (SAXException e) {
            return "Fout bij XSLT tranformatie: "+e.getMessage();
        }
    }

    public static void main(String[] argv) {
        XSLTransformer T = new XSLTransformer();
        System.out.println(T.transform("/opt2/mmbase/org/mmbase/config/default/applications/MyYahoo.xml","/opt2/mmbase/org/mmbase/config/default/xslt/appview.xsl"));
    }
}
