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
 * Static utilitiy methods which are related to (combine functionality of)  other classes in the packages.
 *
 * @author Michiel Meeuwissen
 * @version $Id: Utils.java,v 1.8 2009-01-15 21:21:47 michiel Exp $
 * @since MMBase-1.9
 */
public abstract class Utils {
    private static final Logger log = Logging.getLoggerInstance(Utils.class);

    private Utils() {
        // this class has no instances
    }

    /**
     * Renders a {@link Renderer} into a new {@link Document}. This assumes that the renderer
     * indeed produces XML. This is e.g. used to include blocks into editwizard task XML's.
     */
    public static Document renderToXml(Framework fw, Renderer renderer, Parameters blockParameters, Parameters frameworkParameters, WindowState state,
                                       Class<?> baseClass) throws FrameworkException {
        boolean validation = true;
        boolean xsd = false;
        Writer w = new StringWriter();
        fw.render(renderer, blockParameters, frameworkParameters, w, state);

        String xml = w.toString();
        if (log.isDebugEnabled()) {
            log.debug("Parsing " + xml + " of " +  renderer + " of " + renderer.getBlock());
        }
        InputSource source = new InputSource(new StringReader(xml));
        java.net.URI uri = renderer.getUri();
        if (uri != null) {
            source.setSystemId(uri.toString());
        }
        EntityResolver resolver = new org.mmbase.util.xml.EntityResolver(true, baseClass);
        DocumentBuilder dbuilder = org.mmbase.util.xml.DocumentReader.getDocumentBuilder(validation, xsd,
                                                                                         null/* default error handler */, resolver);
        if(dbuilder == null) throw new RuntimeException("failure retrieving document builder");
        try {
            Document doc = dbuilder.parse(source);
            return  doc;
        } catch (IOException ioe) {
            throw new FrameworkException(uri + ": " + ioe.getMessage(), ioe);
        } catch (SAXException se) {
            throw new FrameworkException(uri + ": " + se.getMessage(), se);
        }

    }

}
