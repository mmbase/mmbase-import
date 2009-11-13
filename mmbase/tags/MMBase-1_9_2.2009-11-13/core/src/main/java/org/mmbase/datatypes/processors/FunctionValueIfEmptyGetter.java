/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.processors;

import org.mmbase.bridge.*;
import org.mmbase.util.Casting;
import org.mmbase.util.logging.*;

/**
 * This processor can be used on a field to return a certain function value of the node, if the
 * field is empty. Noticeably, this can be used on 'virtual' field, to map their value to a function
 * value, which can come in handy sometimes.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8.6
 */

public class FunctionValueIfEmptyGetter implements Processor {

    private static final Logger log = Logging.getLoggerInstance(FunctionValueIfEmptyGetter.class);

    private static final long serialVersionUID = 1L;
    private String functionName;
    public void setFunctionName(String fn) {
        functionName = fn;
    }

    public Object process(Node node, Field field, Object value) {
        if (log.isDebugEnabled()) {
            log.debug("node " + node + " " + field + " "  + value);
        }
        if (value == null) {
            return Casting.toType(field.getDataType().getTypeAsClass(), node.getFunctionValue(functionName, null).get());
        } else if ("".equals("")) {
            return Casting.toString(node.getFunctionValue(functionName, null).get());
        } else {
            return value;
        }
    }

}


