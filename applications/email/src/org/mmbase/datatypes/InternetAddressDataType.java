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
 * @version $Id$
 * @since MMBase-1.9
 */
public class InternetAddressDataType extends StringDataType {
    private static final Logger log = Logging.getLoggerInstance(InternetAddressDataType.class);

    private static final long serialVersionUID = 1L; // increase this if object serialization changes (which we shouldn't do!)

    static final String DATATYPE_BUNDLE = "org.mmbase.datatypes.resources.emaildatatypes";

    protected InternetAddressRestriction restriction = new InternetAddressRestriction(Integer.MAX_VALUE);

    /**
     * Constructor for string data type.
     * @param name the name of the data type
     */
    public  InternetAddressDataType(String name) {
        super(name);
    }

    protected Collection/*<LocalizedString>*/ validateCastValue(Collection/*<LocalizedString>*/ errors, Object castValue, Object value, Node node, Field field) {
        log.debug("Validating " + value);
        errors = super.validateCastValue(errors, castValue, value,  node, field);
        if (value == null) return errors;
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
        log.service("Setting value to " + i);
        restriction.setValue(Integer.valueOf(i));
    }
    public void setLocal(String l) {
        restriction.setLocal(Boolean.valueOf(l));
    }

    class InternetAddressRestriction extends AbstractRestriction/*<Integer>*/ {
        protected boolean local = false;
        InternetAddressRestriction(InternetAddressRestriction source) {
            super(source);
            setLocal(source.isLocal());
        }
        InternetAddressRestriction(int max) {
            super("internetaddress", Integer.valueOf(max));
        }


        // just overrided to get the correct bundle for the datatype.
        // todo, this sucks.
        public LocalizedString getErrorDescription() {
            if (errorDescription == null) {
                // this is postponsed to first use, because otherwise 'getBaseTypeIdentifier' give correct value only after constructor of parent.
                String key = parent.getBaseTypeIdentifier() + "." + name + ".error";
                errorDescription = new LocalizedString(key);
                errorDescription.setBundle(DATATYPE_BUNDLE);
            }
            return errorDescription;
        }

        public void setLocal(boolean l) {
            local = l;
        }
        public boolean isLocal() {
            return local;
        }

        protected boolean simpleValid(Object v, Node node, Field field) {
            if (v == null) return true;
            log.debug("Validating " + v);
            try {
                String address = Casting.toString(v);
                InternetAddress[] ia = InternetAddress.parse(address);
                if (log.isDebugEnabled()) {
                    log.debug("Found " + Arrays.asList(ia) + " this is valid if " + ia.length + " <= " + value);
                }
                if (! local) {
                    for (InternetAddress a : ia) {
                        if (a.getAddress().indexOf("@") == -1) { // not entirely sure that this is absolutely correct
                            log.debug("Local addresses not allowed");
                            return false;
                        }
                    }
                }
                return ia.length  <= (Integer) value;

            } catch (AddressException ae) {
                log.debug("Not valid because " + ae.getMessage());
                return false;
            }
        }

    }


}
