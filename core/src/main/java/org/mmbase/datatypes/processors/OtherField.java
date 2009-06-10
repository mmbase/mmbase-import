/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.processors;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.*;

/**
 * The set- and get- processors implemented in this file can be used to make a virtual field which
 * acts as another field of the same node
 *
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.9.1
 * @version $Id: Related.java 34900 2009-05-01 16:29:42Z michiel $
 */

public class OtherField {

    private static final Logger log = Logging.getLoggerInstance(OtherField.class);

    public abstract static class  AbstractProcessor implements Processor {

        protected String fieldName;

        public void setField(String fn) {
            fieldName = fn;
        }
    }


    public static class Setter extends AbstractProcessor {

        private static final long serialVersionUID = 1L;
        public Object process(final Node node, final Field field, final Object value) {
            node.setValue(fieldName, value);
            return value;
        }
    }

    public static class Getter extends AbstractProcessor {
        private static final long serialVersionUID = 1L;

        public Object process(Node node, Field field, Object value) {
            return node.getValue(fieldName);
        }
    }

}
