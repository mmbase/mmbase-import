/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes;

import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.datatypes.processors.Processor;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;

/**
 * A confirmed password datatype must have the same value as another field of the node (and makes
 * only sense as a field of a node).
 *
 * @author Michiel Meeuwissen
 * @version $Id: ConfirmPasswordDataType.java,v 1.10 2006-07-17 07:32:29 pierre Exp $
 * @since MMBase-1.8
 */
public class ConfirmPasswordDataType extends StringDataType {
    private static final Logger log = Logging.getLoggerInstance(ConfirmPasswordDataType.class);

    private static final long serialVersionUID = 1L; // increase this if object serialization changes (which we shouldn't do!)

    protected PasswordRestriction  passwordRestriction =  new PasswordRestriction("password");

    /**
     * Constructor for string data type.
     * @param name the name of the data type
     */
    public ConfirmPasswordDataType(String name) {
        super(name);
    }

    protected void inheritRestrictions(BasicDataType origin) {
        super.inheritRestrictions(origin);
        if (origin instanceof ConfirmPasswordDataType) {
            ConfirmPasswordDataType dataType = (ConfirmPasswordDataType) origin;
            passwordRestriction.inherit(dataType.passwordRestriction);
        }
    }
    protected void cloneRestrictions(BasicDataType origin) {
        super.cloneRestrictions(origin);
        if (origin instanceof ConfirmPasswordDataType) {
            ConfirmPasswordDataType dataType = (ConfirmPasswordDataType) origin;
            passwordRestriction = new PasswordRestriction(dataType.passwordRestriction);
        }
    }

    protected Collection validateCastValue(Collection errors, Object castValue, Object value, Node node, Field field) {
        errors = super.validateCastValue(errors, castValue, value, node, field);
        errors = passwordRestriction.validate(errors, castValue, node, field);
        return errors;
    }
    /**
     * The field property is the name of the other password field that this fields 'confirms'. It default to 'password'.
     * In datatype XML it can be set with the generic &lt;property name="field" value="..." /&gt;
     */
    public void setField(String field) {
        passwordRestriction.setValue(field);
    }
    /**
     * Returns the name of the field which is 'confirmed' by this datatype.
     */
    public String getField() {
        edit();
        return passwordRestriction.getField();
    }

    protected StringBuffer toStringBuffer() {
        StringBuffer buf = super.toStringBuffer();
        buf.append(" confirm(").append(passwordRestriction.getValue()).append(")");
        return buf;
    }

    protected class PasswordRestriction extends AbstractRestriction {
        PasswordRestriction(PasswordRestriction source) {
            super(source);
        }
        PasswordRestriction(String field) {
            super("confirmpassword", field);
        }
        protected final String getField() {
            return (String) value;
        }

        protected boolean simpleValid(final Object v, final Node node, final Field field) {
            if (node != null && field != null && v != null) {
                if (! node.isChanged(getField())) return true;

                Field passwordField = node.getNodeManager().getField(getField());
                Processor setProcessor = passwordField.getDataType().getProcessor(PROCESS_SET);
                Object processedValue = setProcessor.process(node, field, v);
                String passwordValue = (String) node.getObjectValue(getField());
                if (log.isDebugEnabled()) {
                    log.debug("Password checking " + (node.isNew() ? "new" : "existing") + " node. Password field " + passwordField + " set-processor " + setProcessor);
                    log.debug("Offered value '" + v + "' --> '" + processedValue);
                    log.debug("Comparing '" + passwordValue + "' with '" + processedValue + "'(" + v + ")");
                }
                return passwordValue.equals(v) || passwordValue.equals(processedValue);
            } else {
                return true;
            }
        }
    }

}
