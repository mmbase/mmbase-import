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
 * @version $Id: NumberDataType.java,v 1.31 2008-09-01 17:39:44 michiel Exp $
 * @since MMBase-1.8
 */
abstract public class NumberDataType<E extends Number & Comparable<E>> extends ComparableDataType<E> {
    private static final Logger log = Logging.getLoggerInstance(NumberDataType.class);

    private static final long serialVersionUID = 1L;
    /**
     * Constructor for Number field.
     */
    public NumberDataType(String name, Class<E> classType) {
        super(name, classType);
    }


    protected Number castString(Object preCast, Cloud cloud) throws CastException {
        if (preCast == null) return null;
        if (preCast instanceof String) {
            Locale l = cloud != null ? cloud.getLocale() : Locale.getDefault();
            NumberFormat nf = NumberFormat.getNumberInstance(l);
            nf.setGroupingUsed(false); // we never want to parse e.g. "1.2" to "12". It simply makes
                                       // no sense, and hard to make backwards compatible
            ParsePosition p = new ParsePosition(0);
            String s = (String) preCast;
            Number number =  nf.parse(s, p);
            if (log.isDebugEnabled()) {
                log.debug("Parsed " + s + " to " + number + " (" + p + " " + l);
            }
             if (p.getIndex() < s.length() || p.getErrorIndex() >= 0) {
                 log.debug("Not correct, falling back to toDouble");
                 if (! StringDataType.DOUBLE_PATTERN.matcher(s).matches()) {
                     log.debug("Not a valid double");
                     throw new CastException("Not a number: " + s);
                 } else {
                     log.debug("Casting to double " + s);
                     return Casting.toDouble(s);
                 }
             }
             return number;
        }

        return Casting.toDouble(preCast); // this makes it e.g. possible to report that 1e20 is too big for an integer.
    }



    /**
     * @since MMBase-1.9
     */
    @Override
    protected Object castToValidate(Object value, Node node, Field field) throws CastException {
        if (value == null) return null;
        Object preCast = preCast(value, node, field); // resolves enumerations
        return castString(preCast, getCloud(getCloud(node, field)));
    }

    @Override
    protected E cast(Object value, Cloud cloud, Node node, Field field) throws CastException {
        Number preCast = castString(preCast(value, cloud, node, field), cloud);
        if (preCast == null) return null;
        E cast = Casting.toType(getTypeAsClass(), cloud, preCast);
        return cast;
    }
}
