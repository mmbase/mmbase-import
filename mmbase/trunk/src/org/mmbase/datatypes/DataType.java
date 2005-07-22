/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.datatypes;

import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.core.util.Fields;
import org.mmbase.core.AbstractDescriptor;
import org.mmbase.datatypes.DataTypes;
import org.mmbase.util.Casting;
import org.mmbase.util.LocalizedString;
import org.mmbase.util.logging.*;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @since  MMBase-1.8
 * @version $Id: DataType.java,v 1.1 2005-07-22 12:35:47 pierre Exp $
 */

public class DataType extends AbstractDescriptor implements Cloneable, Comparable, Descriptor {

    // DataTypes for base MMBase field types
    public static final DataType INTEGER  = DataTypes.getDataType(Field.TYPE_INTEGER);
    public static final DataType LONG     = DataTypes.getDataType(Field.TYPE_LONG);
    public static final DataType FLOAT    = DataTypes.getDataType(Field.TYPE_FLOAT);
    public static final DataType DOUBLE   = DataTypes.getDataType(Field.TYPE_DOUBLE);
    public static final DataType STRING   = DataTypes.getDataType(Field.TYPE_STRING);
    public static final DataType XML      = DataTypes.getDataType(Field.TYPE_XML);
    public static final DataType DATETIME = DataTypes.getDataType(Field.TYPE_DATETIME);
    public static final DataType BOOLEAN  = DataTypes.getDataType(Field.TYPE_BOOLEAN);
    public static final DataType BINARY   = DataTypes.getDataType(Field.TYPE_BINARY);
    public static final DataType NODE     = DataTypes.getDataType(Field.TYPE_NODE);
    public static final DataType UNKNOWN  = DataTypes.getDataType(Field.TYPE_UNKNOWN);

    public static final DataType LIST_UNKNOWN = DataTypes.getListDataType(Field.TYPE_UNKNOWN);
    public static final DataType LIST_INTEGER = DataTypes.getListDataType(Field.TYPE_INTEGER);
    public static final DataType LIST_LONG = DataTypes.getListDataType(Field.TYPE_LONG);
    public static final DataType LIST_FLOAT = DataTypes.getListDataType(Field.TYPE_FLOAT);
    public static final DataType LIST_DOUBLE = DataTypes.getListDataType(Field.TYPE_DOUBLE);
    public static final DataType LIST_STRING = DataTypes.getListDataType(Field.TYPE_STRING);
    public static final DataType LIST_XML = DataTypes.getListDataType(Field.TYPE_XML);
    public static final DataType LIST_DATETIME = DataTypes.getListDataType(Field.TYPE_DATETIME);
    public static final DataType LIST_BOOLEAN = DataTypes.getListDataType(Field.TYPE_BOOLEAN);
    public static final DataType LIST_NODE = DataTypes.getListDataType(Field.TYPE_NODE);

    /**
     * An empty Parameter array.
     */
    public static final DataType[] EMPTY  = new DataType[0];

    /**
     * @javadoc
     */
    public static final String DATATYPE_BUNDLE = "org.mmbase.datatypes.resources.datatypes.properties";

    private static final String PROPERTY_REQUIRED = "required";
    private static final Boolean PROPERTY_REQUIRED_DEFAULT = Boolean.FALSE;

    private static final Logger log = Logging.getLoggerInstance(DataType.class);

    protected DataType.Property requiredProperty = null;
    private Class classType;
    private Object defaultValue = null;
    private Object owner = null;

    /**
     * Create a data type object of unspecified class type
     * @param name the name of the data type
     */
    public DataType(String name) {
        this(name, Object.class);
    }

    /**
     * Create a data type object
     * @param name the name of the data type
     * @param classType the class of the data type's possible value
     */
    protected DataType(String name, Class classType) {
        super(name);
        this.classType = classType;
        requiredProperty = createProperty(PROPERTY_REQUIRED, PROPERTY_REQUIRED_DEFAULT);
    }

    protected String getBaseTypeIdentifier() {
        return Fields.getTypeDescription(DataTypes.classToType(classType)).toLowerCase();
    }

    /**
     * Returns the type of values that this data type accepts.
     * @return the type as a Class
     */
    public Class getTypeAsClass() {
        return classType;
    }

   /**
     * Checks if the passed object is of the correct class (compatible with the type of this DataType),
     * and throws an IllegalArgumentException if it doesn't.
     * @param value teh value whose type (class) to check
     * @throws IllegalArgumentException if the type is not compatible
     */
    protected boolean isCorrectType(Object value) {
        return Casting.isType(classType, value);
    }

    /**
     * Checks if the passed object is of the correct class (compatible with the type of this data type),
     * and throws an IllegalArgumentException if it doesn't.
     * @param value the value whose type (class) to check
     * @throws IllegalArgumentException if the type is not compatible
     */
    public void checkType(Object value) {
        if (!isCorrectType(value)) {
            // customize this?
            throw new IllegalArgumentException("DataType of '" + value + "' for '" + getName() + "' must be of type " + classType + " (but is " + (value == null ? value : value.getClass()) + ")");
        }
    }

    /**
     * Tries to 'cast' an object for use with this parameter. E.g. if value is a String, but this
     * parameter is of type Integer, then the string can be parsed to Integer.
     * @param value The value to be filled in in this Parameter.
     */
    public Object autoCast(Object value) {
        return Casting.toType(classType, value);
    }

    /**
     * Returns the default value of this data type.
     * @return the default value
     */
    public Object getDefaultValue() {
        return defaultValue;
    }

    /**
     * Sets the default value of this data type.
     * @param def the default value
     * @param InvalidStateException if the datatype was finished (and thus can no longer be changed)
     * @return this datatype
     */
    public DataType setDefaultValue(Object def) {
        edit();
        defaultValue = def;
        return this;
    }

    public boolean isFinished() {
        return owner != null;
    }

    /**
     * @javadoc
     */
    public DataType finish() {
        this.owner = new Object();
        return this;
    }

    /**
     * @javadoc
     */
    public DataType finish(Object owner) {
        this.owner = owner;
        return this;
    }

    /**
     * @javadoc
     */
    public DataType rewrite(Object owner) {
        if (this.owner !=null) {
            if (this.owner != owner) {
                throw new IllegalArgumentException("Cannot rewrite this datatype - specified owner is not correct");
            }
            this.owner = null;
        }
        return this;
    }

    /**
     * @javadoc
     */
    protected void edit() {
        if (isFinished()) {
            throw new IllegalStateException("This data type is finished and can not longer be changed.");
        }
    }

    /**
     * Checks if the passed object is of the correct type (compatible with the type of this data type),
     * and follows the restrictions defined for this type.
     * It throws an IllegalArgumentException if it doesn't.
     * @param value the value to validate
     * @throws IllegalArgumentException if the value is not compatible
     */
    public void validate(Object value) {
        validate(value,null);
    }

    protected final void failOnValidate(DataType.Property property, Object value, Cloud cloud) {
        String error = property.getErrorDescription(cloud==null? null : cloud.getLocale());
        // todo; replace ${NAME} with property name (??)
        // todo; replace ${PROPERTY} with property.getValue (??)
        // todo; replace ${VALUE} with value .toString (??)
        throw new IllegalArgumentException(error);
    }

    /**
     * Checks if the passed object is of the correct type (compatible with the type of this data type),
     * and follows the restrictions defined for this type.
     * It throws an IllegalArgumentException with a lozalized message (dependent on the cloud) if it doesn't.
     * @param value the value to validate
     * @param cloud the cloud used to determine the locale for the error message when validation fails
     * @throws IllegalArgumentException if the value is not compatible
     */
    public void validate(Object value, Cloud cloud) {
        checkType(value);
        // test required
        if (value == null && isRequired() && getDefaultValue() == null) {
            failOnValidate(getRequiredProperty(), value, cloud);
        }
    }

    public String toString() {
        return getTypeAsClass() + " " + getName();
    }

    /**
     * Returns a cloned instance of this datatype, inheriting all validation rules.
     * Unlike the original datatype though, the cloned copy is declared unfinished even if the original
     * was finished. This means that the cloned datatype can be changed.
     */
    public Object clone() {
        return clone (null);
    }

    /**
     * Returns a cloned instance of this datatype, inheriting all validation rules.
     * Similar to calling clone(), but changes the data type name if one is provided.
     * @param name the new name of the copied datatype (can be <code>null</code>, in which case the name is not changed).
     */
    public Object clone(String name) {
        try {
            DataType clone = (DataType)super.clone(name);
            clone.requiredProperty = (DataType.Property)getRequiredProperty().clone(clone);
            // reset owner if it was set, so this datatype can be changed
            clone.owner = null;
            return clone;
        } catch (CloneNotSupportedException cnse) {
            // should not happen
            log.error("Cannot clone this DataType");
            throw new RuntimeException("Cannot clone this DataType", cnse);
        }
    }

    public int compareTo(Object o) {
        if (o instanceof DataType) {
            DataType a = (DataType) o;
            int compared = getName().compareTo(a.getName());
            if (compared == 0) compared = getTypeAsClass().getName().compareTo(a.getTypeAsClass().getName());
            return compared;
        } else {
            throw new ClassCastException("Object is not of type DataType");
        }
    }

    /**
     * Whether data type equals to other data type. Only key and type are consided. DefaultValue and
     * required propererties are only 'utilities'.
     * @return true if o is a DataType of which key and type equal to this' key and type.
     */
    public boolean equals(Object o) {
        if (o instanceof DataType) {
            DataType a = (DataType) o;
            return getName().equals(a.getName()) && getTypeAsClass().equals(a.getTypeAsClass());
        }
        return false;
    }

    public int hashCode() {
        return getName().hashCode() * 13 + getTypeAsClass().hashCode();
    }

    /**
     * Returns whether this field is required (should have content).
     * Note that MMBase does not generally enforce required fields to be filled -
     * If not provided, a default value (generally an empty string or the integer value -1)
     * is filled in by the system.
     * As such, isRequired will mostly be used as an indicator for (generic) editors.
     *
     * @return  <code>true</code> if the field is required
     * @since  MMBase-1.6
     */
    public boolean isRequired() {
        return Boolean.TRUE.equals(getRequiredProperty().getValue());
    }

    /**
     * Returns the 'required' property, containing the value, errormessages, and fixed status of this attribute.
     * @return the property as a {@link DataType#Property}
     */
    public DataType.Property getRequiredProperty() {
        return requiredProperty;
    }

    /**
     * Sets whether the data type requires a value.
     * @param required <code>true</code> if a value is required
     * @param InvalidStateException if the datatype was finished (and thus can no longer be changed)
     * @return the datatype property that was just set
     */
    public DataType.Property setRequired(boolean required) {
        return setProperty(getRequiredProperty(),Boolean.valueOf(required));
    }

    protected DataType.Property createProperty(String name, Object value) {
        DataType.Property property =  new DataType.Property(name,value);
        String key = getBaseTypeIdentifier() + "." + name + ".error";
        LocalizedString localizedErrorDescription = new LocalizedString(key);
        localizedErrorDescription.setBundle(DATATYPE_BUNDLE);
        property.setLocalizedErrorDescription(localizedErrorDescription);
        return property;
    }

    protected DataType.Property setProperty(DataType.Property property, Object value) {
        property.setValue(value);
        return property;
    }

    public final class Property implements Cloneable {
        private String name;
        private Object value;
        private LocalizedString errorDescription;
        private boolean fixed;

        private Property(String name, Object value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            DataType.this.edit();
            if (fixed) {
                throw new IllegalStateException("Property '" + name + "' is fixed, cannot be changed");
            }
            this.value = value;
        }

        public LocalizedString getLocalizedErrorDescription() {
            return errorDescription;
        }

        public void setLocalizedErrorDescription(LocalizedString errorDescription) {
            this.errorDescription = errorDescription;
        }

        public String getErrorDescription(Locale locale) {
            if (errorDescription == null) {
                return null;
            } else {
                return errorDescription.get(locale);
            }
        }

        public String getErrorDescription() {
            return getDescription(null);
        }

        public boolean isFixed() {
            return fixed;
        }

        public void setFixed(boolean fixed) {
            if (this.fixed && !fixed) {
                throw new IllegalStateException("Property '" + name + "' is fixed, cannot be changed");
            }
            this.fixed = fixed;
        }

        public Object clone(DataType dataType) {
            DataType.Property clone = ((DataType)dataType).new Property(name, value);
            if (errorDescription != null) {
                clone.setLocalizedErrorDescription((LocalizedString)errorDescription.clone());
            }
            clone.setFixed(fixed);
            return clone;
        }

    }

}
