/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.richtext.processors.xml;
import org.mmbase.datatypes.processors.xml.Modes;
import org.mmbase.datatypes.processors.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.xml.Generator;
import org.mmbase.bridge.Node;
import org.mmbase.util.logging.*;
import org.mmbase.util.*;
import org.mmbase.richtext.transformers.XmlField;
import org.mmbase.util.xml.XMLWriter;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.parsers.*;

import java.util.*;
import java.io.*;
import org.w3c.dom.*;


/**
 * If field was previously not 'mmxf' (but for example just a string), then it may be the case that
 * getXMLValue on that field returns &lt;p&gt;ascii-like text&lt;/p&gt;. This processor can be
 * chained before MmxfGetString to ensure that the field is really Mmxf, even if such 'p' fields are stored.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8
 */

public class UnPGet implements  Processor {
    private static final Logger log = Logging.getLoggerInstance(UnPGet.class);

    private static final int serialVersionUID = 1;

    public Object process(Node node, Field field, Object value) {
        // first, unhtml-ize, if starts with "<p>"
        if (value instanceof String) {
            String string = (String) value;
            if (string.startsWith("<p")) {
                try {
                    Document realValue = node.getXMLValue(field.getName());
                    java.net.URL u = ResourceLoader.getConfigurationRoot().getResource("xslt/text.xslt");
                    StringWriter res = new StringWriter();
                    XSLTransformer.transform(new DOMSource((Document) realValue), u, new StreamResult(res), null);
                    string = res.toString();
                } catch (Exception e) {
                    log.warn(e);
                    string = e.getMessage();
                }
                log.debug("Unhtml-lized " + string);
                // now, apply wiki format, to make it actual mmxf
                XmlField trans = new XmlField(XmlField.RICH);
                value = trans.transformBack(string);
                log.debug("MMXF-lized " + value);
                return value;
            } else {
                return value;
            }
        } else if (value instanceof Document) {
            Document xml = (Document) value;
            if (xml.getDocumentElement().getTagName().equals("p")) {
                String string;
                try {
                    java.net.URL u = ResourceLoader.getConfigurationRoot().getResource("xslt/text.xslt");
                    StringWriter res = new StringWriter();
                    XSLTransformer.transform(new DOMSource(xml), u, new StreamResult(res), null);
                    string = res.toString();
                } catch (Exception e) {
                    log.warn(e);
                    string = e.getMessage();
                }
                log.debug("Unhtml-lized " + string);
                // now, apply wiki format, to make it actual mmxf
                XmlField trans = new XmlField(XmlField.RICH);
                value = trans.transformBack(string);
                log.debug("MMXF-lized " + value);
                try {
                    value = Util.parse(value);
                } catch (Exception e) {
                    log.warn(e);
                    return value;
                }
                return value;
            } else {
                return xml;
            }
        } else {
            return value;
        }

    }

    public String toString() {
        return "get_unp";
    }

}
