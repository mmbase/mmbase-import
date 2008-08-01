/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework;

import java.io.*;
import java.util.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;
import org.mmbase.util.*;
import org.mmbase.util.functions.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @author Michiel Meeuwissen
 * @version $Id: Utils.java,v 1.2 2008-08-01 16:29:07 michiel Exp $
 * @since MMBase-1.9
 */
public abstract class Utils {
    private static final Logger log = Logging.getLoggerInstance(Utils.class);

    private Utils() {
        // this class has no instances
    }

    public static Document renderToXml(Framework fw, Renderer renderer, Parameters blockParameters, Parameters frameworkParameters, Renderer.WindowState state,
                                       Class<?> baseClass) throws FrameworkException {
        boolean validation = true;
        boolean xsd = false;
        Writer w = new StringWriter();
        fw.render(renderer, blockParameters, frameworkParameters, w, state);
        if (log.isDebugEnabled()) {
            log.debug("Parsing " + w.toString() + " of " +  renderer + " of " + renderer.getBlock());
        }
        InputSource source = new InputSource(new StringReader(w.toString()));
        XMLEntityResolver resolver = new XMLEntityResolver(true, baseClass);
        DocumentBuilder dbuilder = org.mmbase.util.xml.DocumentReader.getDocumentBuilder(validation, xsd,
                                                                                         null/* default error handler */, resolver);
        if(dbuilder == null) throw new RuntimeException("failure retrieving document builder");
        try {
            Document doc = dbuilder.parse(source);
            return  doc;
        } catch (IOException ioe) {
            throw new FrameworkException(ioe);
        } catch (SAXException se) {
            throw new FrameworkException(se);
        }

    }

}
