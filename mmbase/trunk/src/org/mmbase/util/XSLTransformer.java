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
 * @version $Id: XSLTransformer.java,v 1.8 2002-06-14 19:45:26 michiel Exp $
 *
 */
public class XSLTransformer {
    // logger
    private static Logger log = Logging.getLoggerInstance(XSLTransformer.class.getName());
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
     *
     * TODO: There are caches implemented in org.mmbase.cache.xslt. Perhaps they should be used here.
     *
     */
    public String transform(String xmlPath, String xslPath, boolean cutXML) {
        try {
            // xalan 2.0 implementation (xalan 1 implementation is in cvs history)
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
		s = s.substring(n + 1);
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
        XSLTransformer t = new XSLTransformer();
        log.info(t.transform("/bigdisk/dev/config/mmbase/test/applications/MyYahoo.xml",
                             "/bigdisk/dev/config/mmbase/test/xslt/appview.xsl"));
    }
}
