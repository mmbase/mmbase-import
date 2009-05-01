/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.processors;

import org.mmbase.bridge.*;

/**
 * If value is a node, returns it's number (as a String)
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8
 */

public class NodeNumber implements Processor {

    private static final long serialVersionUID = 1L;

    public Object process(Node node, Field field, Object value) {
        if (value == null) return null;
        if (value instanceof Node) {
            return "" + ((Node) value).getNumber();
        }
        return value;
    }

    public String toString() {
        return "node number";
    }

}
