/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.magicfile;
import java.io.*;
import org.w3c.dom.Element;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import java.util.regex.*;
import org.mmbase.util.logging.*;
import org.mmbase.util.xml.ErrorHandler;

/**
 * A detector which can match on XML namespaces, publicId.
 *
 * @version $Id$
 * @author Michiel Meeuwissen
 * @since MMBase-1.9.3
 */

class XmlDetector extends AbstractDetector {
    private static final Logger log = Logging.getLoggerInstance(XmlDetector.class);


    protected String namespace = null;
    protected Pattern publicId   = null;

    public void setXmlns(String xmlns) {
        namespace = xmlns;
    }

    public void setPublicId(String dt) {
        publicId = Pattern.compile(dt);
    }

    /**
     * @return Whether detector matches the prefix/lithmus of the file
     */
    @Override
    public boolean test(byte[] lithmus, InputStream input) {
        return test(input);
    }
    protected boolean test(InputStream input) {
        try {
            XMLReader parser = XMLReaderFactory.createXMLReader();
            Handler handler = new Handler();
            parser.setContentHandler(handler);
            parser.setDTDHandler(handler);
            parser.setEntityResolver(handler);
            parser.setErrorHandler(new ErrorHandler(false, ErrorHandler.FATAL_ERROR));
            InputSource source = new InputSource(input);
            parser.parse(source);

            // successfully parsed (which is remarkable while the byte array is truncated),
            // but no matches found.
            return false;
        } catch (Matched m) {
            // Parsing interrupted because of a match!
            log.debug("Matched " + m.getMessage());
            return true;
        } catch (SAXException e) {
            // probably not XML, probably end of array reached.
            return false;
        } catch (java.io.IOException ioe) {
            // this really is exceptional, and should not happen
            log.warn(ioe);
            return false;
        } finally {
        }
    }

    @Override
    public void configure(Element el) {
        super.configure(el);
        if (namespace == null && publicId == null) {
            throw new IllegalStateException("Not configured with either namespace or publicId");
        }
    }

    /**
     * In case a match is found, this exception is thrown and further parsing is interrupted.
     */
    protected class Matched extends RuntimeException {
        public Matched(String mes) {
            super(mes);
        }
    }


    protected class Handler extends DefaultHandler {

        @Override
        public void startPrefixMapping(String prefix, String uri)  {
            if (uri.equals(XmlDetector.this.namespace)) {
                throw new Matched("Namespace " + uri);
            }
        }
        @Override
        public InputSource resolveEntity(String publicId, String systemId) {
            if (XmlDetector.this.publicId != null && XmlDetector.this.publicId.matcher(publicId).matches()) {
                throw new Matched("publicId " + publicId);
            }
            return new InputSource(new ByteArrayInputStream(new byte[0]));
        }

    }


    @Override
    public String toString() {
        return "xmlns:" + namespace + " publicid:" + publicId;
    }
}
