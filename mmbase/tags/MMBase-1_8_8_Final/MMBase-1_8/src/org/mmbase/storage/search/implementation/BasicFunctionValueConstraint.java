/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search.implementation;

import org.mmbase.storage.search.StepField;
import org.mmbase.storage.search.FunctionValueConstraint;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 *
 * @author Marcel Maatkamp
 * @version $Id$
 * @since MMBase-1.8.5
 */
public class BasicFunctionValueConstraint extends BasicFieldValueConstraint implements FunctionValueConstraint {
	
    private static final Logger log = Logging.getLoggerInstance(BasicFunctionValueConstraint.class);
    
    private final String function;
    
    public BasicFunctionValueConstraint(StepField field, Object value, String function) {
        super(field, value);
        this.function = function;
        if (log.isDebugEnabled()) {
            log.debug("field(" + getField() + ") and object(" + getValue() + "): setting function(" + function + ")");
        }
    }

    public String getFunction() { 
        if (log.isDebugEnabled()) {
            log.debug("for field(" + getField() + ") and object(" + getValue() + "): getting function(" + function + ")"); 
        }
    	return this.function; 
    }
}
