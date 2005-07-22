/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes;

import java.util.Calendar;
import java.util.Date;
import org.mmbase.bridge.Cloud;
import org.mmbase.util.Casting;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @version $Id: DateTimeDataType.java,v 1.1 2005-07-22 12:35:47 pierre Exp $
 * @since MMBase-1.8
 */
public class DateTimeDataType extends DataType {

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
    public DateTimeDataType(String name) {
        super(name, Date.class);
        minProperty = createProperty(PROPERTY_MIN, PROPERTY_MIN_DEFAULT);
        maxProperty = createProperty(PROPERTY_MAX, PROPERTY_MAX_DEFAULT);
    }

    /**
     * Returns the minimum value for this data type.
     * @return the property defining the minimum value
     */
    public Date getMin() {
        return (Date)getMinProperty().getValue();
    }

    /**
     * Returns the minimum value for this data type.
     * @return the minimum value as an <code>Number</code>, or <code>null</code> if there is no minimum.
     */
    public DataType.Property getMinProperty() {
        return minProperty;
    }

    /**
     * Returns the precision for comparing the minimum value for this data type.
     * @return the minimum value as an <code>Date</code>, or <code>null</code> if there is no minimum.
     */
    public int getMinPrecision() {
        return minPrecision;
    }

    /**
     * Returns whether the minimum value for this data type is inclusive or not.
     * @return <code>true</code> if the minimum value if inclusive, <code>false</code> if it is not, or if there is no minimum.
     */
    public boolean isMinInclusive() {
        return minInclusive;
    }

    /**
     * Returns the maximum value for this data type.
     * @return the maximum value as an <code>Date</code>, or <code>null</code> if there is no maximum.
     */
    public Date getMax() {
        return (Date)getMaxProperty().getValue();
    }

    /**
     * Returns the maximum value for this data type.
     * @return the property defining the maximum value
     */
    public DataType.Property getMaxProperty() {
        return maxProperty;
    }

    /**
     * Returns the precision for comparing the maximum value for this data type.
     * @return the minimum value as an <code>Date</code>, or <code>null</code> if there is no minimum.
     */
    public int getMaxPrecision() {
        return maxPrecision;
    }

    /**
     * Returns whether the maximum value for this data type is inclusive or not.
     * @return <code>true</code> if the maximum value if inclusive, <code>false</code> if it is not, or if there is no minimum.
     */
    public boolean isMaxInclusive() {
        return maxInclusive;
    }

    /**
     * Sets the minimum Date value for this data type.
     * @param length the minimum as an <code>Date</code>, or <code>null</code> if there is no minimum.
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this data type is read-only (i.e. defined by MBase)
     */
    public DataType.Property setMin(Date value) {
        return setProperty(minProperty, value);
    }

    public void setMinPrecision(int precision) {
        minPrecision = precision;
    }

    public void setMinInclusive(boolean inclusive) {
        minInclusive = inclusive;
    }

    /**
     * Sets the minimum Date value for this data type.
     * @param length the minimum as an <code>Date</code>, or <code>null</code> if there is no minimum.
     * @param precision precision, a constant from {@link org.mmbase.storage.search.FieldValueDateConstraint}
     * @param inclusive whether the minimum value is inclusive or not
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this data type is read-only (i.e. defined by MBase)
     */
    public DataType.Property setMin(Date value, int precision, boolean inclusive) {
        edit();
        setMinPrecision(precision);
        setMinInclusive(inclusive);
        return setMin(value);
    }

    /**
     * Sets the maximum Date value for this data type.
     * @param length the maximum as an <code>Date</code>, or <code>null</code> if there is no maximum.
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this data type is read-only (i.e. defined by MBase)
     */
    public DataType.Property setMax(Date value) {
        return setProperty(maxProperty, value);
    }

    public void setMaxPrecision(int precision) {
        maxPrecision = precision;
    }

    public void setMaxInclusive(boolean inclusive) {
        maxInclusive = inclusive;
    }

    /**
     * Sets the maximum Date value for this data type.
     * @param length the maximum as an <code>Date</code>, or <code>null</code> if there is no maximum.
     * @param precision precision, a constant from {@link org.mmbase.storage.search.FieldValueDateConstraint}
     * @param inclusive whether the maximum value is inclusive or not
     * @throws Class Identifier: java.lang.UnsupportedOperationException if this data type is read-only (i.e. defined by MBase)
     */
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
        DateTimeDataType clone = (DateTimeDataType)super.clone(name);
        clone.minProperty = (DataType.Property)getMinProperty().clone(clone);
        clone.maxProperty = (DataType.Property)getMaxProperty().clone(clone);
        return clone;
    }

}
