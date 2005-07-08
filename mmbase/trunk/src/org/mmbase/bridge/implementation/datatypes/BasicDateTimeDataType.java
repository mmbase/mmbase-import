/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.implementation.datatypes;

import java.util.*;

import org.mmbase.bridge.MMBaseType;
import org.mmbase.bridge.DataType;
import org.mmbase.bridge.datatypes.DateTimeDataType;
import org.mmbase.bridge.implementation.AbstractDataType;
import org.mmbase.util.functions.Parameter;
import org.mmbase.util.Casting;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @version $Id: BasicDateTimeDataType.java,v 1.2 2005-07-08 08:02:18 pierre Exp $
 * @see org.mmbase.bridge.DataType
 * @see org.mmbase.bridge.datatypes.DateTimeDataType
 * @since MMBase-1.8
 */
public class BasicDateTimeDataType extends Parameter implements DateTimeDataType {

    protected Date minimum = null;
    protected int minimumPrecision = Calendar.SECOND;
    protected boolean minimumInclusive = true;
    protected Date maximum = null;
    protected int maximumPrecision = Calendar.SECOND;
    protected boolean maximumInclusive = true;

    /**
     * Constructor for DateTime field.
     */
    public BasicDateTimeDataType(String name) {
        super(name, MMBaseType.TYPE_DATETIME);
    }

    /**
     * Create a datetime field object
     * @param name the name of the data type
     * @param type the class of the data type's possible value
     */
    protected BasicDateTimeDataType(String name, BasicDateTimeDataType dataType) {
        super(name,dataType);
    }

    public Date getMin() {
        return minimum;
    }

    public int getMinPrecision() {
        return minimumPrecision;
    }

    public boolean getMinInclusive() {
        return minimumInclusive;
    }

    public Date getMax() {
        return maximum;
    }

    public int getMaxPrecision() {
        return maximumPrecision;
    }

    public boolean getMaxInclusive() {
        return maximumInclusive;
    }

    public DateTimeDataType setMin(Date value) {
        edit();
        minimum = value;
        return this;
    }

    public DateTimeDataType setMinPrecision(int precision) {
        edit();
        minimumPrecision = precision;
        return this;
    }

    public DateTimeDataType setMinInclusive(boolean inclusive) {
        edit();
        minimumInclusive = inclusive;
        return this;
    }

    public DateTimeDataType setMin(Date value, int precision, boolean inclusive) {
        setMin(value);
        setMinPrecision(precision);
        setMinInclusive(inclusive);
        return this;
    }

    public DateTimeDataType setMax(Date value) {
        edit();
        maximum = value;
        return this;
    }

    public DateTimeDataType setMaxPrecision(int precision) {
        edit();
        maximumPrecision = precision;
        return this;
    }

    public DateTimeDataType setMaxInclusive(boolean inclusive) {
        edit();
        maximumInclusive = inclusive;
        return this;
    }

    public DateTimeDataType setMax(Date value, int precision, boolean inclusive) {
        setMax(value);
        setMaxPrecision(precision);
        setMaxInclusive(inclusive);
        return this;
    }

    public void validate(Object value) {
        super.validate(value);
        Date dateValue = Casting.toDate(value);
        //
        // Todo: check on mindate/max date, taking into account precision and inclusiveness
        //
    }

    /**
     * Returns a new (and editable) instance of this datatype, inheriting all validation rules.
     * @param name the new name of the copied datatype.
     */
    public DataType copy(String name) {
        return new BasicDateTimeDataType(name,this);
    }

    /**
     * Clears all validation rules set after the instantiation of the type.
     * Note that validation rules can only be cleared for derived datatypes.
     * @throws UnsupportedOperationException if this datatype is read-only (i.e. defined by MBase)
     */
    public void copyValidationRules(DataType dataType) {
        super.copyValidationRules(dataType);
        DateTimeDataType dateTimeField = (DateTimeDataType)dataType;
        setMin(dateTimeField.getMin());
        setMinPrecision(dateTimeField.getMinPrecision());
        setMinInclusive(dateTimeField.getMinInclusive());
        setMax(dateTimeField.getMax());
        setMaxPrecision(dateTimeField.getMaxPrecision());
        setMaxInclusive(dateTimeField.getMaxInclusive());
    }

}
