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
 * A confirmed datatype must have the same value as another field of the node (and makes
 * only sense as a field of a node).
 *
 * @author Michiel Meeuwissen
 * @author Sander de Boer
 * @version  $Id: CheckEqualityDataType.java,v 1.2 2009-04-28 13:59:11 michiel Exp $
 * @since MMBase-1.8
 */
public class CheckEqualityDataType extends StringDataType {
    private static final Logger log = Logging.getLoggerInstance(CheckEqualityDataType.class);

    private static final long serialVersionUID = 1L; // increase this if object serialization changes (which we shouldn't do!)

    protected FieldRestriction  fieldRestriction =  new FieldRestriction("");

    /**
     * Constructor for string data type.
     * @param name the name of the data type
     */
    public CheckEqualityDataType(String name) {
        super(name);
    }

    @Override
    protected void inheritRestrictions(BasicDataType origin) {
        super.inheritRestrictions(origin);
        if (origin instanceof CheckEqualityDataType) {
            CheckEqualityDataType dataType = (CheckEqualityDataType) origin;
            fieldRestriction.inherit(dataType.fieldRestriction);
        }
    }
    @Override
    protected void cloneRestrictions(BasicDataType origin) {
        super.cloneRestrictions(origin);
        if (origin instanceof CheckEqualityDataType) {
            CheckEqualityDataType dataType = (CheckEqualityDataType) origin;
            fieldRestriction = new FieldRestriction(dataType.fieldRestriction);
        }
    }

    @Override
    protected Collection validateCastValue(Collection errors, Object castValue, Object value, Node node, Field field) {
        errors = super.validateCastValue(errors, castValue, value, node, field);
        errors = fieldRestriction.validate(errors, castValue, node, field);
        return errors;
    }
    /**
     * The field property is the name of the other field that this fields 'confirms'. It default to ''.
     * In datatype XML it can be set with the generic &lt;property name="field" value="..." /&gt;
     */
    public void setField(String field) {
        fieldRestriction.setValue(field);
    }
    /**
     * Returns the name of the field which is 'confirmed' by this datatype.
     */
    public String getField() {
        edit();
        return fieldRestriction.getField();
    }

    @Override
    protected StringBuilder toStringBuilder() {
        StringBuilder buf = super.toStringBuilder();
        buf.append(" confirm(").append(fieldRestriction.getValue()).append(")");
        return buf;
    }

    protected class FieldRestriction extends AbstractRestriction {
        FieldRestriction(FieldRestriction source) {
            super(source);
        }
        FieldRestriction(String field) {
            super("confirmfield", field);
        }
        protected final String getField() {
            return (String) value;
        }

        @Override
        protected boolean simpleValid(final Object v, final Node node, final Field field) {
            if (node != null && field != null && v != null) {
                if (! node.isChanged(getField())) return true;

                Field checkField = node.getNodeManager().getField(getField());
                Processor setProcessor = checkField.getDataType().getProcessor(PROCESS_SET);
                Object processedValue = setProcessor.process(node, field, v);
                String fieldValue = (String) node.getObjectValue(getField());
                if (log.isDebugEnabled()) {
                    log.debug("Field checking " + (node.isNew() ? "new" : "existing") + " node. Field " + checkField + " set-processor " + setProcessor);
                    log.debug("Offered value '" + v + "' --> '" + processedValue);
                    log.debug("Comparing '" + fieldValue + "' with '" + processedValue + "'(" + v + ")");
                }
                return fieldValue.equals(v) || fieldValue.equals(processedValue);
            } else {
                return true;
            }
        }
    }

}
