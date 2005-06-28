/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.util.fields.xml;
import org.mmbase.bridge.util.fields.Processor;
import org.mmbase.bridge.*;
import org.mmbase.util.*;
import org.mmbase.util.xml.XMLWriter;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.mmbase.util.logging.*;


/**
 * @see FieldSetString
 * @author Michiel Meeuwissen
 * @version $Id: FieldGetString.java,v 1.1 2005-06-28 08:15:36 michiel Exp $
 * @since MMBase-1.8
 */

public class FieldGetString implements  Processor {
    private static final Logger log = Logging.getLoggerInstance(FieldGetString.class);

    public Object process(Node node, Field field, Object value) {
        if (! (value instanceof Document)) {
            String s = Casting.toString(value);
            value = Casting.toXML(s);
        }
        if (value instanceof Document) {
            try {
                java.net.URL u = ResourceLoader.getConfigurationRoot().getResource("xslt/text.xslt");
                java.io.StringWriter res = new java.io.StringWriter();            
                XSLTransformer.transform(new DOMSource((Document) value), u, new StreamResult(res), null);
                value = res.toString();
            } catch (Exception e) {
                log.warn(e);
            }

        }
        String s = Casting.toString(value);

        return s;        
    }
}
