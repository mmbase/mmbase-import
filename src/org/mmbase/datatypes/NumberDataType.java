/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes;

import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.util.Casting;
import org.mmbase.util.logging.*;

/**
 * A DataType representing some kind of numeric value, like a floating point number or an integer number.
 *
 * @author Pierre van Rooden
 * @version $Id$
 * @since MMBase-1.8
 */
abstract public class NumberDataType extends ComparableDataType {

    private static final Logger log = Logging.getLoggerInstance(NumberDataType.class);

    private static final long serialVersionUID = 1L; 
    /**
     * Constructor for Number field.
     */
    public NumberDataType(String name, Class classType) {
        super(name, classType);
    }


    protected Object castToValidate(Object value, Node node, Field field) throws CastException {
        if (value == null) return null;
        Object preCast = preCast(value, node, field); // resolves enumerations
        if (preCast instanceof String) {
            if (! StringDataType.DOUBLE_PATTERN.matcher((String) preCast).matches()) {
                throw new CastException("Not a number: " + preCast);
            }
        } 
        return new Double(Casting.toDouble(preCast)); // this makes it e.g. possible to report that 1e20 is too big for an integer.
    }
}
