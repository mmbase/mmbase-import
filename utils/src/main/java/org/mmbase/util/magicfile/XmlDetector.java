/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.magicfile;
import java.util.*;
import java.io.*;
import org.w3c.dom.Element;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import org.mmbase.util.xml.ErrorHandler;
import org.mmbase.util.xml.*;
import org.mmbase.util.logging.*;

/**

 * @version $Id: Detector.java 41036 2010-02-15 22:30:54Z michiel $
 */

public class XmlDetector extends AbstractDetector {
    private static final Logger log = Logging.getLoggerInstance(XmlDetector.class);



    protected String namespace = null;
    protected String doctype   = null;

    public void setXmlns(String xmlns) {
        namespace = xmlns;
    }

    public void setDocType(String dt) {
        doctype = dt;
    }

    /**
     * @return Whether detector matches the prefix/lithmus of the file
     */
    public boolean test(byte[] lithmus) {
        try {
            XMLReader parser = XMLReaderFactory.createXMLReader();
            Handler handler = new Handler();
            parser.setContentHandler(handler);
            parser.setErrorHandler(new ErrorHandler(false, ErrorHandler.FATAL_ERROR));
            InputSource source = new InputSource(new ByteArrayInputStream(lithmus));
            parser.parse(source);
            return false;
        } catch (Matched m) {
            return true;
        } catch (SAXException e) {
            return false;
        } catch (java.io.IOException ioe) {
            log.warn(ioe);
            return false;
        }
    }

    @Override
    public void configure(Element el) {
        super.configure(el);
        if (namespace == null && doctype == null) {
            throw new IllegalStateException("Not configured with either namespace or doctype");
        }
        if (doctype != null) {
            throw new UnsupportedOperationException("Needs implementing");
        }
    }

    protected class Matched extends RuntimeException {
    }

    protected class Handler extends DefaultHandler {


        @Override
        public void startPrefixMapping(String prefix, String uri)  {
            if (uri.equals(XmlDetector.this.namespace)) {
                throw new Matched();
            }
        }
    }

}
