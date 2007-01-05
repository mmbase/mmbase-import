/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes;

import javax.mail.internet.*;
import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;

/**
 * Validates a value by putting it into {@link javax.mail.internet.InternetAddress#parse(String)},
 * if not exception, the value is valid.
 *
 * @author Michiel Meeuwissen
 * @version $Id: InternetAddressDataType.java,v 1.2 2007-01-05 19:57:17 michiel Exp $
 * @since MMBase-1.9
 */
public class InternetAddressDataType extends StringDataType {
    private static final Logger log = Logging.getLoggerInstance(InternetAddressDataType.class);

    private static final long serialVersionUID = 1L; // increase this if object serialization changes (which we shouldn't do!)

    protected InternetAddressRestriction restriction = new InternetAddressRestriction(Integer.MAX_VALUE);

    /**
     * Constructor for string data type.
     * @param name the name of the data type
     */
    public  InternetAddressDataType(String name) {
        super(name);
    }

    protected Collection<LocalizedString> validateCastValue(Collection<LocalizedString> errors, Object castValue, Object value, Node node, Field field) {
        log.debug("Validating " + value);
        errors = super.validateCastValue(errors, castValue, value,  node, field);
        errors = restriction.validate(errors, castValue, node, field);
        return errors;
    }

    protected void inheritRestrictions(BasicDataType origin) {
        super.inheritRestrictions(origin);
        if (origin instanceof InternetAddressDataType) {
            InternetAddressDataType dataType = (InternetAddressDataType)origin;
            restriction.inherit(dataType.restriction);
        }
    }

    protected void cloneRestrictions(BasicDataType origin) {
        super.cloneRestrictions(origin);
        if (origin instanceof InternetAddressDataType) {
            InternetAddressDataType dataType = (InternetAddressDataType)origin;
            restriction = new InternetAddressRestriction(dataType.restriction);
        }
    }


    public void setMaxAddress(String i) {
        log.info("Setting value to " + i);
        restriction.setValue(Integer.valueOf(i));
    }

    class InternetAddressRestriction extends AbstractRestriction<Integer> {
        InternetAddressRestriction(InternetAddressRestriction source) {
            super(source);
        }
        InternetAddressRestriction(int max) {
            super("internetaddress", Integer.valueOf(max));
        }
        protected boolean simpleValid(Object v, Node node, Field field) {
            if (v == null) return true;
            log.debug("Validating " + v);
            try {
                String address = Casting.toString(v);
                InternetAddress[] ia = InternetAddress.parse(address);
                log.debug("Found " + Arrays.asList(ia) + " this is valid if " + ia.length + " <= " + value);
                return ia.length  <= value;

            } catch (AddressException ae) {
                log.debug("Not valid because " + ae.getMessage());
                return false;
            }
        }

    }


}
