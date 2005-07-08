/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;

import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.DataTypes;
import org.mmbase.util.Casting;
import org.mmbase.util.LocalizedString;
import org.mmbase.util.logging.*;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @author Daniel Ockeloen (MMFunctionParam)
 * @since  MMBase-1.8
 * @version $Id: AbstractDataType.java,v 1.6 2005-07-08 08:02:17 pierre Exp $
 */

public class AbstractDataType extends AbstractDescriptor implements DataType, MMBaseType, Comparable {

    private static final Logger log = Logging.getLoggerInstance(AbstractDataType.class);

    private DataType parentDataType = null;

    private Class classType;
    private int type;
    private boolean finished = false;
    private Object defaultValue = null;
    private Object owner = null;

    private Map properties = new HashMap();

    /**
     * Create a data type object
     * @param name the name of the data type
     * @param classType the class of the data type's possible value
     */
    protected AbstractDataType(String name, Class classType) {
        super(name);
        this.type = MMBaseType.TYPE_UNKNOWN;
        this.classType = classType;
    }

    /**
     * Create a data type object
     * @param name the name of the data type
     * @param type the class of the data type's possible value
     */
    protected AbstractDataType(String name, int type) {
        super(name);
        this.type = type;
        this.classType = DataTypes.getTypeAsClass(type);
    }

    /**
     * Create an data type object
     * @param name the name of the data type
     * @param dataType the parent data type whose constraints to inherit
     */
    protected AbstractDataType(String name, DataType dataType) {
        super(name);
        this.parentDataType = dataType;
        if (dataType != null) {
            this.type = dataType.getType();
            this.classType = dataType.getTypeAsClass();
            copyValidationRules(dataType);
        }
    }

    public Class getTypeAsClass() {
        return classType;
    }

    /**
     * {@inheritDoc}
     * @since MMBase 1.7
     */
    public int getType() {
        return type;
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

    public void checkType(Object value) {
        if (!isCorrectType(value)) {
            throw new IllegalArgumentException("DataType of '" + value + "' for '" + getName() + "' must be of type " + classType + " (but is " + (value == null ? value : value.getClass()) + ")");
        }
    }

    public Object autoCast(Object value) {
        return Casting.toType(classType, value);
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

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

    public void validate(Object value) {
        validate(value,null);
    }

    private final void failOnValidate(DataType.Property property, String message, Cloud cloud) {
        String error = property.getErrorDescription(cloud==null? null : cloud.getLocale());
        if (error == null) error = message;
        // todo; replace ${NAME} with property name (??)
        throw new IllegalArgumentException(message);
    }

    public void validate(Object value, Cloud cloud) {
        if (parentDataType != null) {
            parentDataType.validate(value);
        }
        checkType(value);
        // test required
        if (value == null && isRequired() && getDefaultValue() == null) {
            failOnValidate(getProperty(PROPERTY_REQUIRED), "Datatype " + getName()  + " requires a value.", cloud);
        }
    }

    public String toString() {
        return getTypeAsClass() + " " + getName();
    }

    public DataType copy(String name) {
        throw new UnsupportedOperationException("Copy of this datatype is not implemented");
    }

    /**
     * @javadoc
     */
    protected void copyValidationRules(DataType dataType) {
        super.copy(dataType);
        setDefaultValue(dataType.getDefaultValue());
        setRequired(dataType.isRequired());
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

    public boolean isRequired() {
        DataType.Property property = getProperty(PROPERTY_REQUIRED);
        return (property!=null) && Casting.toBoolean(property.getValue());
    }

    public DataType setRequired(boolean required) {
        return setRequired(required,null,false);
    }

    public DataType setRequired(boolean required, LocalizedString errorDescription, boolean fixed) {
        setProperty(PROPERTY_REQUIRED, Boolean.valueOf(required), errorDescription, fixed);
        return this;
    }


    public DataType.Property setProperty(String name, Object value, LocalizedString errorDescription, boolean fixed) {
        // should we check on properties or not ???
        edit();
        DataType.Property property = (DataType.Property) properties.get(name);
        if (property == null) {
            property = new DataTypeProperty(name);
        }
        property.setValue(value);
        property.setLocalizedErrorDescription(errorDescription);
        property.setFixed(fixed);
        properties.put(name,property);
        return property;
    }

    public DataType.Property getProperty(String name){
        return (DataType.Property) properties.get(name);
    }

    public class DataTypeProperty implements DataType.Property {
        private String name;
        private Object value;
        private LocalizedString errorDescription;
        private boolean fixed;

        private DataTypeProperty(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
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

    }

}
