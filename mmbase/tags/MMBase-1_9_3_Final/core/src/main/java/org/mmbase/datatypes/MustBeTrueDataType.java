/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes;
import org.mmbase.bridge.*;
import org.mmbase.util.*;
import java.util.*;
import org.mmbase.util.logging.*;
/**
 * A boolean datatype of which the value must be true. The
 * default value can be false though. This makes it possible to make
 * an input checkbox that the user must check. ("I agree").
 *
 * @author Michiel Meeuwissen
 * @version $Id: BooleanDataType.java 35587 2009-06-02 21:57:49Z michiel $
 * @since MMBase-1.9.3
 */
public class MustBeTrueDataType extends BooleanDataType {

    private static final Logger LOG = Logging.getLoggerInstance(MustBeTrueDataType.class);

    private static final long serialVersionUID = 1L;

    protected ValueRestriction  valueRestriction =  new ValueRestriction(Boolean.TRUE);

    /**
     * Constructor for a boolean datatype (either a primitive boolean type or the Boolean class).
     *
     * @param name the name of the data type
     */
    public MustBeTrueDataType(String name) {
        super(name, false);
    }

    @Override
    protected Collection<LocalizedString> validateCastValue(Collection<LocalizedString> errors, Object castValue, Object value, Node node, Field field) {
        LOG.debug("Validating " + castValue);
        errors = super.validateCastValue(errors, castValue, value,  node, field);
        errors = valueRestriction.validate(errors, castValue, node, field);
        return errors;
    }


    protected class ValueRestriction extends AbstractRestriction<Boolean> {
        private static final long serialVersionUID = 0L;
        ValueRestriction(ValueRestriction source) {
            super(source);
        }
        ValueRestriction(Boolean b) {
            super("value", b);
            setEnforceStrength(DataType.ENFORCE_ALWAYS);
        }

        @Override
        protected boolean simpleValid(final Object v, final Node node, final Field field) {
            return Boolean.valueOf(Casting.toBoolean(v)).equals(getValue());
        }
    }




}
