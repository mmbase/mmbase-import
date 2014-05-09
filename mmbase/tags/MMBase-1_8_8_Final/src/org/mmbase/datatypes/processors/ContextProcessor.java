/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.processors;

import org.mmbase.bridge.*;
import org.mmbase.util.Casting;

/**
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8.5
 */

public abstract class ContextProcessor {


    public static class Get implements Processor {
        private static final long serialVersionUID = 1L;

        public final Object process(Node node, Field field, Object value) {
            return node.getContext();
        }
        public String toString() {
            return "GET_CONTEXT";
        }
    }

    public static class Set implements Processor {
        private static final long serialVersionUID = 1L;
        
        public final Object process(Node node, Field field, Object value) {
            node.setContext(Casting.toString(value));
            return node.getContext();
        }

        public String toString() {
            return "SET_CONTEXT";
        }
    }


}


