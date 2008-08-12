/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes;

import java.text.*;

import java.util.Locale;

import org.mmbase.bridge.*;
import org.mmbase.util.Casting;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * A DataType representing some kind of numeric value, like a floating point number or an integer number.
 *
 * @author Pierre van Rooden
 * @version $Id: NumberDataType.java,v 1.29 2008-08-12 16:11:36 michiel Exp $
 * @since MMBase-1.8
 */
abstract public class NumberDataType<E extends Number&Comparable<E>> extends ComparableDataType<E> {
    private static final Logger log = Logging.getLoggerInstance(NumberDataType.class);

    private static final long serialVersionUID = 1L;
    /**
     * Constructor for Number field.
     */
    public NumberDataType(String name, Class<E> classType) {
        super(name, classType);
    }


    protected Number castString(Object preCast, Cloud cloud) throws CastException {
         if (preCast instanceof String) {
             Locale l = cloud.getLocale();
             NumberFormat nf = NumberFormat.getNumberInstance(l);
             ParsePosition p = new ParsePosition(0);
             String s = (String) preCast;
             Number number =  nf.parse(s, p);
             if (log.isDebugEnabled()) {
                 log.debug("Parsed " + s + " to " + number + " (" + p + " " + l);
             }
             if (p.getIndex() < s.length() || p.getErrorIndex() >= 0) {
                 log.debug("Not correct, falling back to toDouble");
                 if (! StringDataType.DOUBLE_PATTERN.matcher((String) preCast).matches()) {
                     throw new CastException("Not a number: " + preCast);
                 }
                 return Casting.toDouble(preCast);
             }
             return number;
         }

         return Casting.toDouble(preCast); // this makes it e.g. possible to report that 1e20 is too big for an integer.
     }



    /**
     * @since MMBase-1.9
     */
    protected Object castToValidate(Object value, Node node, Field field) throws CastException {
        if (value == null) return null;
        Object preCast = preCast(value, node, field); // resolves enumerations
        return castString(preCast, getCloud(getCloud(node, field)));
    }
}
