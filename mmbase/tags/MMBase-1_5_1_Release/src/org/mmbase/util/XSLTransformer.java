/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.io.File;
import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Make XSL Transformations
 *
 * @author Case Roole, cjr@dds.nl
 * @version $Id: XSLTransformer.java,v 1.7 2001-05-23 14:03:49 michiel Exp $
 *
 * $Log: not supported by cvs2svn $
 * Revision 1.6  2001/04/19 15:32:26  pierre
 * pierre: added logging
 *
 * Revision 1.5  2000/10/31 14:52:28  vpro
 * Rico: removed import
 *
 * Revision 1.4  2000/10/19 11:54:12  case
 * cjr: Set entityresolver for XSL transformations so local DTD's of
 * XML documents are found. (Formerly, DTD's were looked for at mmbase.org,
 * which resulted in failure if an mmbase machine didn't have access to the net.)
 *
 * Revision 1.3  2000/10/18 12:48:53  case
 * cjr: added a method to cut off the <?xml version blabla ?> part that prevents
 * XSL from being used to create merely part of a new xml document.
 * I hope someone knows a real xml/xsl way to accomplish the same result.
 *
 * Revision 1.2  2000/08/10 19:53:54  case
 * cjr: Removed an obsolete comment
 *
 * Revision 1.1  2000/08/09 12:45:24  case
 * cjr: implements a transform(xmlPath,xslPath) method that returns a string
 *
 */
public class XSLTransformer {

    // logger
    private static Logger log = Logging.getLoggerInstance(XSLTransformer.class.getName());

    //private XSLTProcessor processor;
    /**
     * Empty constructor
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
	return transform(xmlPath,xslPath,false);
    }

    /**
     * Transform an XML document using a certain XSL document.
     *
     * @param xmlPath Path to XML file
     * @param xslPath Path to XSL file
     * @param cutXML if <code>true</code>, cuts the &lt;?xml&gt; line that normally starts an
     *               xml document
     * @return String with converted XML document
     */
    public String transform(String xmlPath, String xslPath, boolean cutXML) {
        try {

            /* 
               //xalan 1.2 implementation:
              
	    XMLParserLiaison liaison = (XMLParserLiaison)(new XercesLiaison());
            EntityResolver resolver = new XMLEntityResolver();
            liaison.setEntityResolver(resolver);

            processor = XSLTProcessorFactory.getProcessor(liaison);

            StringWriter res = new StringWriter();

            // Create the 3 objects the XSLTProcessor needs to perform the transformation.
            XSLTInputSource xmlSource = new XSLTInputSource (xmlPath);
            XSLTInputSource xslSheet = new XSLTInputSource (xslPath);
            XSLTResultTarget xmlResult = new XSLTResultTarget (res);

            // Perform the transformation.
            processor.process(xmlSource, xslSheet, xmlResult);

            */

            // xalan 2.0 implementation
            TransformerFactory tFactory = TransformerFactory.newInstance();
	
            // Use the TransformerFactory to instantiate a Transformer that will work with  
            // the stylesheet you specify. This method call also processes the stylesheet
            // into a compiled Templates object.
            Transformer transformer = tFactory.newTransformer(new StreamSource(new File(xslPath)));

            StringWriter res = new StringWriter();
           
            // Use the Transformer to apply the associated Templates object to an XML document
            // (foo.xml) and write the output to a file (foo.out).
            transformer.transform(new StreamSource(new File(xmlPath)),
                                  new StreamResult(res));
	        
            //return res.toString();
	    String s = res.toString();
	    int n = s.indexOf("\n");
	    if (cutXML && s.length() > n) {
		s = s.substring(n+1);
	    }
	    return s;

        } catch (Exception e) {
            log.error(e.getMessage());
	    log.error(Logging.stackTrace(e));
            return "Error during XSLT tranformation: "+e.getMessage();
        }
    }

    /**
     * Invocation of the class from the commandline for testing.
     */
    public static void main(String[] argv) {
        XSLTransformer T = new XSLTransformer();
        //log.info(T.transform("/opt2/mmbase/org/mmbase/config/default/applications/MyYahoo.xml","/opt2/mmbase/org/mmbase/config/default/xslt/appview.xsl"));
        log.info(T.transform("/bigdisk/dev/config/mmbase/test/applications/MyYahoo.xml","/bigdisk/dev/config/mmbase/test/xslt/appview.xsl"));
    }
}
