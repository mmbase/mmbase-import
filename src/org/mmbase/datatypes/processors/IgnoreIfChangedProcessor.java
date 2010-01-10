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
 * If this processor is used on a certain field, that you can effectively set the value only once
 * (until the commit of the node). This is based on {@link
 * org.mmbase.bridge.Node#isChanged(String)}. It that returns true (and the node is not new), the old value is used.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8.1
 */

public class IgnoreIfChangedProcessor implements Processor {
    private static final Logger log = Logging.getLoggerInstance(IgnoreEmptyProcessor.class);
    private static final long serialVersionUID = 1L;

    public final Object process(Node node, Field field, Object value) {
    	if(node == null)return value;
        Object prevValue = node.getValue(field.getName());
        if (! node.isNew()) {
            if (node.isChanged(field.getName())) {
                return  prevValue;
            }
        }
        return value;
    }

    public String toString() {
        return "IGNOREIFCHANGED";
    }
}


