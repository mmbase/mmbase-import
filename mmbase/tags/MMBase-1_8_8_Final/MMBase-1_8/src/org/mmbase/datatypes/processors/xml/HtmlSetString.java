/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.processors.xml;
import org.mmbase.datatypes.processors.Processor;
import org.mmbase.bridge.*;
import org.mmbase.util.logging.*;
import org.mmbase.util.*;
import org.mmbase.util.transformers.*;
import org.mmbase.util.functions.*;
import javax.xml.parsers.*;

/**
 * This set-processor is used for HTML fields. If a valid Document is given, this is supposed to be
 * HTML. Otherwise the value is converted to a String, and then parsed to SAX compliant XML. (Cross Site)
 * Scripting tags and attributes are removed using {@link
 * org.mmbase.util.transformers.TagStripperFactory}. If the XML is not a valid Document (not one,
 * but more  document element), a surrounding 'div' tag is implicetely added.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8
 */

public class HtmlSetString implements  Processor {
    private static final Logger log = Logging.getLoggerInstance(HtmlSetString.class);
    private static final long serialVersionUID = 1L;


    protected static final String PREF = "<div>";
    protected static final String POST = "</div>";


    public Object process(Node node, Field field, Object value) {
        if (value instanceof org.w3c.dom.Document) return value;
        TagStripperFactory factory = new TagStripperFactory();
        Parameters params = factory.createParameters();
        params.set("tags", "XSS");
        params.set("addbrs", Boolean.FALSE);
        params.set("escapeamps", Boolean.TRUE);
        CharTransformer htmlCleaner = (CharTransformer) factory.createTransformer(params);
        String cleanHtml = htmlCleaner.transform(Casting.toString(value));

        log.debug("Setting " + field + " from " + node + " as a String to " + cleanHtml, new Exception());

        try {
            DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
            dfactory.setNamespaceAware(true);
            DocumentBuilder documentBuilder = dfactory.newDocumentBuilder();
            try {
                return  documentBuilder.parse(new java.io.ByteArrayInputStream(cleanHtml.getBytes("UTF-8")));
            } catch (org.xml.sax.SAXException se) {
                log.warn(se);
                String reparedHtml = PREF + cleanHtml + POST;
                return  documentBuilder.parse(new java.io.ByteArrayInputStream(reparedHtml.getBytes("UTF-8")));
            }
        } catch (Exception e) {
            // give it up.
            log.warn(e);
            return Casting.toXML(cleanHtml);
        }

    }
    public String toString() {
        return "set_HTML";
    }
}
