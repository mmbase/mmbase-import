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
 * Currently is like FieldGetString.
 * @author Michiel Meeuwissen
 * @version $Id: HtmlGetString.java,v 1.1 2005-06-28 14:19:54 michiel Exp $
 * @since MMBase-1.8
 */

public class HtmlGetString implements  Processor {
    private static final Logger log = Logging.getLoggerInstance(HtmlGetString.class);

    private Processor processor = new FieldGetString();

    public Object process(Node node, Field field, Object value) {
        return processor.process(node, field, value);
    }
}
