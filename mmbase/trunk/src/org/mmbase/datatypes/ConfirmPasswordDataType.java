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
 * @version $Id: ConfirmPasswordDataType.java,v 1.3 2005-11-23 12:11:25 michiel Exp $
 * @since MMBase-1.8
 */
public class ConfirmPasswordDataType extends StringDataType {
    private static final Logger log = Logging.getLoggerInstance(ConfirmPasswordDataType.class);

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

    protected Collection validateCastedValue(Collection errors, Object castedValue, Node node, Field field) {
        errors = super.validateCastedValue(errors, castedValue, node, field);
        errors = passwordRestriction.validate(errors, castedValue, node, field);
        return errors;
    }

    /**
     * The field property is the name of the other password field that this fields 'confirms'. It default to 'password'.
     * In datatype XML it can be set with the generic &lt;property name="field" value="..." /&gt;
     */
    public void setField(String field) {
        passwordRestriction.setValue(field);
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

        protected boolean simpleValid(Object v, Node node, Field field) {
            if (node != null && field != null && v != null) {
                Field passwordField = node.getNodeManager().getField(getField());
                Processor setProcessor = passwordField.getDataType().getProcessor(PROCESS_SET);
                v = setProcessor.process(node, field, v);
                String passwordValue = node.getStringValue(getField());
                log.info("Comparing '" + passwordValue + "' with '" + v + "'");
                return passwordValue.equals(v);
            } else {
                return true;
            }
        }
    }

}
