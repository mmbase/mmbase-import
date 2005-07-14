/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.implementation.datatypes;

import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.bridge.datatypes.DateTimeDataType;
import org.mmbase.bridge.implementation.AbstractDataType;
import org.mmbase.util.Casting;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @version $Id: BasicDateTimeDataType.java,v 1.7 2005-07-14 14:13:40 pierre Exp $
 * @see org.mmbase.bridge.DataType
 * @see org.mmbase.bridge.datatypes.DateTimeDataType
 * @since MMBase-1.8
 */
public class BasicDateTimeDataType extends AbstractDataType implements DateTimeDataType {

    public static final String PROPERTY_MIN = "min";
    public static final Number PROPERTY_MIN_DEFAULT = null;

    public static final String PROPERTY_MAX = "max";
    public static final Number PROPERTY_MAX_DEFAULT = null;

    protected DataType.Property minProperty = null;
    protected int minPrecision = Calendar.SECOND;
    protected boolean minInclusive = true;

    protected DataType.Property maxProperty = null;
    protected int maxPrecision = Calendar.SECOND;
    protected boolean maxInclusive = true;

    /**
     * Constructor for DateTime field.
     */
    public BasicDateTimeDataType(String name) {
        super(name, Date.class);
        minProperty = createProperty(PROPERTY_MIN, PROPERTY_MIN_DEFAULT);
        maxProperty = createProperty(PROPERTY_MAX, PROPERTY_MAX_DEFAULT);
    }

    public Date getMin() {
        return (Date)getMinProperty().getValue();
    }

    public DataType.Property getMinProperty() {
        return minProperty;
    }

    public int getMinPrecision() {
        return minPrecision;
    }

    public boolean isMinInclusive() {
        return minInclusive;
    }

    public Date getMax() {
        return (Date)getMaxProperty().getValue();
    }

    public DataType.Property getMaxProperty() {
        return maxProperty;
    }

    public int getMaxPrecision() {
        return maxPrecision;
    }

    public boolean isMaxInclusive() {
        return maxInclusive;
    }

    public DataType.Property setMin(Date value) {
        return setProperty(minProperty, value);
    }

    public void setMinPrecision(int precision) {
        minPrecision = precision;
    }

    public void setMinInclusive(boolean inclusive) {
        minInclusive = inclusive;
    }

    public DataType.Property setMin(Date value, int precision, boolean inclusive) {
        edit();
        setMinPrecision(precision);
        setMinInclusive(inclusive);
        return setMin(value);
    }

    public DataType.Property setMax(Date value) {
        return setProperty(maxProperty, value);
    }

    public void setMaxPrecision(int precision) {
        maxPrecision = precision;
    }

    public void setMaxInclusive(boolean inclusive) {
        maxInclusive = inclusive;
    }

    public DataType.Property setMax(Date value, int precision, boolean inclusive) {
        edit();
        setMaxPrecision(precision);
        setMaxInclusive(inclusive);
        return setMax(value);
    }

    public void validate(Object value, Cloud cloud) {
        super.validate(value);
        if (value != null) {
            Date dateValue = Casting.toDate(value);
            // Todo: check on mindate/max date, taking into account precision and inclusiveness
        }
    }

    public Object clone(String name) {
        BasicDateTimeDataType clone = (BasicDateTimeDataType)super.clone(name);
        clone.minProperty = (DataTypeProperty)getMinProperty().clone(clone);
        clone.maxProperty = (DataTypeProperty)getMaxProperty().clone(clone);
        return clone;
    }

}
