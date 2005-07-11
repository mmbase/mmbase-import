/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;

import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.core.util.Fields;
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
 * @version $Id: AbstractDataType.java,v 1.10 2005-07-11 17:49:20 pierre Exp $
 */

abstract public class AbstractDataType extends AbstractDescriptor implements DataType, Comparable {

    public static final String PROPERTY_REQUIRED = "required";
    public static final Boolean PROPERTY_REQUIRED_DEFAULT = Boolean.FALSE;

    private static final Logger log = Logging.getLoggerInstance(AbstractDataType.class);

    private Class classType;
    private Object defaultValue = null;
    private Object owner = null;

    protected Map properties = new HashMap();

    /**
     * Create a data type object
     * @param name the name of the data type
     * @param classType the class of the data type's possible value
     */
    protected AbstractDataType(String name, Class classType) {
        super(name);
        this.classType = classType;
        createProperty(PROPERTY_REQUIRED, Boolean.FALSE,
                new LocalizedString ("Datatype ${NAME} requires a value."), // use resource bundle
                false);
    }

    /**
     * Create an data type object
     * @param name the name of the data type
     * @param dataType the parent data type whose constraints to inherit
     */
    protected AbstractDataType(String name, DataType dataType) {
        super(name);
        if (dataType instanceof AbstractDataType) {
            this.classType = dataType.getTypeAsClass();
            copyValidationRules((AbstractDataType)dataType);
        }
    }

    public Class getTypeAsClass() {
        return classType;
    }

    abstract public int getBaseType();

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
            // customize this?
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

    protected final void failOnValidate(DataType.Property property, Object value, Cloud cloud) {
        String error = property.getErrorDescription(cloud==null? null : cloud.getLocale());
        // todo; replace ${NAME} with property name (??)
        // todo; replace ${PROPERTY} with property.getValue (??)
        // todo; replace ${VALUE} with value .toString (??)
        throw new IllegalArgumentException(error);
    }

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

    public DataType copy(String name) {
        try {
            java.lang.reflect.Constructor constructor = this.getClass().getConstructor(new Class[] { String.class, DataType.class });
            return (DataType) constructor.newInstance(new Object[] { name, this });
        } catch (Exception e) {
            UnsupportedOperationException uoe =  new UnsupportedOperationException("Cannot copy this datatype  : " + e.getMessage());
            uoe.initCause(e);
            throw uoe;
        }
    }

    /**
     * @javadoc
     */
    protected void copyValidationRules(AbstractDataType dataType) {
        super.copy(dataType);
        setDefaultValue(dataType.getDefaultValue());
        // TODO: copy all properties! Plus find a way to share localized strings
        for (Iterator i = dataType.properties.entrySet().iterator(); i.hasNext();) {
            Map.Entry entrySet = (Map.Entry)i.next();
            DataTypeProperty property = (DataTypeProperty)entrySet.getValue();
            try {
                properties.put(entrySet.getKey(),(DataType.Property)property.clone());
            } catch (CloneNotSupportedException cnse) {
                // should not happen!
                log.error(cnse.getMessage());
            }
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

    public boolean isRequired() {
        return Boolean.TRUE.equals(getRequiredProperty().getValue());
    }

    public DataType.Property getRequiredProperty() {
        return getProperty(PROPERTY_REQUIRED, PROPERTY_REQUIRED_DEFAULT);
    }

    public DataType.Property setRequired(boolean required) {
        return setProperty(PROPERTY_REQUIRED, Boolean.valueOf(required));
    }

    public DataType.Property createProperty(String name, Object value, LocalizedString localizedErrorDescription, boolean fixed) {
        // should we check on properties or not ???
        edit();
        DataType.Property property = (DataType.Property) properties.get(name);
        if (property == null) {
            property = new DataTypeProperty(name,value);
        } else {
            property.setValue(value);
        }
        property.setFixed(fixed);
        if (localizedErrorDescription == null) {
            String key = Fields.getTypeDescription(getBaseType()).toLowerCase() + "." + name + ".error";
            localizedErrorDescription = new LocalizedString(key);
            String bundle = "org.mmbase.bridge.implementation.datatypes.resources.datatypes.properties";
            localizedErrorDescription.setBundle(bundle);
        }
        property.setLocalizedErrorDescription(localizedErrorDescription);
        properties.put(name,property);
        return property;
    }

    public DataType.Property getProperty(String name) {
        return (DataType.Property) properties.get(name);
    }

    public DataType.Property getProperty(String name, Object defaultValue) {
        DataType.Property property = getProperty(name);
        if (property == null) {
            property = createProperty(name, defaultValue, null, false);
            properties.put(name, property);
        }
        return property;
    }

    public DataType.Property setProperty(String name, Object value) {
        DataType.Property property = getProperty(name);
        if (property == null) {
            property = createProperty(name, value, null, false);
            properties.put(name, property);
        } else {
            property.setValue(value);
        }
        return property;
    }

    public class DataTypeProperty implements DataType.Property, Cloneable {
        private String name;
        private Object value;
        private LocalizedString errorDescription;
        private boolean fixed;
        private Class propertyClass;

        private DataTypeProperty(String name, Object value) {
            this.name = name;
            this.value = value;
            this.propertyClass = value.getClass();
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
            if (value != null && !propertyClass.isInstance(value)) {
                throw new IllegalArgumentException("Property '" + name + "' is of class " + propertyClass.getName() + ", specified value is: " + value + ".");
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

        public Object clone() throws CloneNotSupportedException {
            DataTypeProperty clone = (DataTypeProperty)super.clone();
            if (errorDescription != null) {
                clone.setLocalizedErrorDescription((LocalizedString)errorDescription.clone());
            }
            return clone;
        }

    }

}
