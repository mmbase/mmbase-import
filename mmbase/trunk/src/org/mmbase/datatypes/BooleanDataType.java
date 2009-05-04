/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes;
import org.mmbase.bridge.*;
/**
 * The DataType associated with a boolean value.
 *
 * @author Pierre van Rooden
 * @version $Id$
 * @since MMBase-1.8
 */
public class BooleanDataType extends BasicDataType<Boolean> {

    private static final long serialVersionUID = 1L; 

    /**
     * Constructor for a boolean datatype (either a primitive boolean type or the Boolean class).
     *
     * @param name the name of the data type
     * @param primitive indicate if a primitive type should be used
     */
    public BooleanDataType(String name, boolean primitive) {
        super(name, primitive ? Boolean.TYPE : Boolean.class);
    }

    @Override
    protected <D> D preCast(D value, Cloud cloud, Node node, Field field) {
        if (value == null) return null;
        if (value instanceof String) {
            if ("".equals(value)) return null;
            if ("-1".equals(value)) return null;
        } else if (value instanceof Number) {
            double d = ((Number) value).doubleValue();
            if (d == -1.0) return null;
        }
        return super.preCast(value, cloud, node, field);
    }

    /**
     * Cast a bit more conservatively, because Casting aggressively casts everything to boolean,
     * which would make nearly every value valid.
     */
    @Override
    protected final Boolean cast(Object value, Cloud cloud, Node node, Field field) throws CastException {
        Object preCast = preCast(value, cloud, node, field);
        if (preCast == null) return null;
        if (value instanceof Boolean) return (Boolean) value;
        if (value instanceof String) {
            String s = ((String)value).toLowerCase();
            if ("".equals(value)) return null;
            if ("true".equals(s)) return Boolean.TRUE;
            if ("false".equals(s)) return Boolean.FALSE;
            if ("1".equals(s)) return Boolean.TRUE;
            if ("0".equals(s)) return Boolean.FALSE;
            throw new CastException("'" + value + "' of type " + value.getClass().getName() + "  cannot be cast to boolean");
        }
        if (value instanceof Number) {
            double d = ((Number) value).doubleValue();
            if (d == 1.0) return Boolean.TRUE;
            if (d == 0.0) return Boolean.FALSE;
            throw new CastException("The number '" + value + "' cannot be cast to boolean (boolean is 0 or 1)");
        }
        throw new CastException("'" + value + "' cannot be cast to boolean (boolean is 0 or 1)");
    }
}
