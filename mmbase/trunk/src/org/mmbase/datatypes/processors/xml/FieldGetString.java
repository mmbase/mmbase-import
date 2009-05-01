/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.processors.xml;
import org.mmbase.datatypes.processors.Processor;
import org.mmbase.bridge.*;
import org.mmbase.util.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.mmbase.util.logging.*;


/**

 * @see FieldSetString
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8
 */

public class FieldGetString implements  Processor {
    private static final Logger log = Logging.getLoggerInstance(FieldGetString.class);
    private static final long serialVersionUID = 1L;

    public Object process(Node node, Field field, Object value) {

        Object realValue =  node.getObjectValue(field.getName());
        if (realValue == null || value == null) return "";


        if (! (realValue  instanceof Document)) {
            throw new RuntimeException("FieldGetString should only be defined for XML fields. " + node.getNumber() + "/" + field.getName() + " returned a " + realValue.getClass() + "'" + realValue + "'");
        }
        Document document = (Document) realValue;


        if (value instanceof Document) {
            // requested XML, give it!
            return document;
        } else {
            // requested something else, String, probably
            try {
                switch(Modes.getMode(node.getCloud().getProperty(Cloud.PROP_XMLMODE))) {
                case Modes.WIKI:
                case Modes.KUPU:
                case Modes.FLAT: {
                    // unXMLize first, then cast back to wanted type.
                    String string;
                    try {
                        java.net.URL u = ResourceLoader.getConfigurationRoot().getResource("xslt/text.xslt");
                        java.io.StringWriter res = new java.io.StringWriter();
                        XSLTransformer.transform(new DOMSource((Document) realValue), u, new StreamResult(res), null);
                        string = res.toString();
                    } catch (Exception e) {
                        log.warn(e);
                        string = e.getMessage();
                    }
                    return Casting.toType(value.getClass(), node.getCloud(), string);
                }
                default: {
                    return value;
                }

                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return value;
            }
        }
    }
}
