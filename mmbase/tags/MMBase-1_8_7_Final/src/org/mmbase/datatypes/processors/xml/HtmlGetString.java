/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.processors.xml;
import org.mmbase.datatypes.processors.Processor;
import org.mmbase.bridge.*;

/**
 * Currently is like FieldGetString.
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8
 */

public class HtmlGetString implements  Processor {

    private static final long serialVersionUID = 1L;

    private Processor processor = new FieldGetString();

    public Object process(Node node, Field field, Object value) {
        return processor.process(node, field, value);
    }

    public String toString() {
        return "get_HTML";
    }
}
