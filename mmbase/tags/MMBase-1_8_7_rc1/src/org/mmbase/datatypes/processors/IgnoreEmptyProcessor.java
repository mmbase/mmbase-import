/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.processors;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * If you use this processor for a field, then setting it to an empty value will be ignored (the
 * previous value will remain intact).
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8.1
 */

public class IgnoreEmptyProcessor implements Processor {
    private static final Logger log = Logging.getLoggerInstance(IgnoreEmptyProcessor.class);
    private static final long serialVersionUID = 1L;

    public final Object process(Node node, Field field, Object value) {
    	if(node == null)return value;
        Object prevValue = node.getValue(field.getName());
        if (value == null || "".equals(value)) {
            return  prevValue;
        }
        return value;
    }

    public String toString() {
        return "IGNORE";
    }
}


