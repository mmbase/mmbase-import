/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.util.fields.xml;

import org.mmbase.bridge.util.fields.Processor;
import org.mmbase.bridge.*;

/**
 * Currently is like FieldGetString.
 * @author Michiel Meeuwissen
 * @version $Id: HtmlGetString.java,v 1.2 2005-07-09 11:08:54 nklasens Exp $
 * @since MMBase-1.8
 */

public class HtmlGetString implements  Processor {

    private Processor processor = new FieldGetString();

    public Object process(Node node, Field field, Object value) {
        return processor.process(node, field, value);
    }
}
