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
 * @version $Id: AbstractDataType.java,v 1.5 2005-06-28 14:01:41 pierre Exp $
 */

public class AbstractDataType extends AbstractDescriptor implements DataType, MMBaseType, Comparable {

    private static final Logger log = Logging.getLoggerInstance(AbstractDataType.class);

    private DataType parentDataType = null;

    private Class classType;
    private int type;
    private boolean finished = false;
    private Object defaultValue = null;
    private boolean required = false;
    private Object owner = null;

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

    public boolean isRequired() {
        return required;
    }

    public DataType setRequired(boolean required) {
        edit();
        this.required = required;
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
        if (parentDataType != null) {
            parentDataType.validate(value);
        }
        checkType(value);
        // test required
        if (value == null && isRequired() && getDefaultValue() == null) {
            throw new IllegalArgumentException("Datatype " + getName()  + " requires a value.");
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

}
