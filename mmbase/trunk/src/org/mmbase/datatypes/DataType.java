/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.datatypes;

import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.fields.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.BasicFieldValueConstraint;
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
 * @version $Id: DataType.java,v 1.11 2005-08-18 12:21:51 pierre Exp $
 */

public class DataType extends AbstractDescriptor implements Cloneable, Comparable, Descriptor {

    public static final int PROCESS_COMMIT = 0;
    public static final int PROCESS_GET    = 1;
    public static final int PROCESS_SET    = 2;

    /**
     * An empty Parameter array.
     */
    public static final DataType[] EMPTY  = new DataType[0];

    /**
     * The bundle used by datatype to determine default prompts for error messages when a
     * validation fails.
     */
    public static final String DATATYPE_BUNDLE = "org.mmbase.datatypes.resources.datatypes";

    private static final String PROPERTY_REQUIRED = "required";
    private static final Boolean PROPERTY_REQUIRED_DEFAULT = Boolean.FALSE;

    private static final String PROPERTY_UNIQUE = "unique";
    private static final Boolean PROPERTY_UNIQUE_DEFAULT = Boolean.FALSE;

    private static final Logger log = Logging.getLoggerInstance(DataType.class);

    /**
     * The 'required' property.
     */
    protected DataType.Property requiredProperty;

    /**
     * The 'unique' property.
     */
    protected DataType.Property uniqueProperty;

    /**
     * The datatype from which this datatype originally inherited it's properties.
     * Used to restore default values when calling the {@link clear} method.
     */
    protected DataType origin = null;

    private Object owner;
    private Class classType;
    private Object defaultValue;

    private Processor commitProcessor;
    private Processor[] getProcessors;
    private Processor[] setProcessors;

    private List enumerationValues;

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
        owner = null;
        clear();
    }

    protected String getBaseTypeIdentifier() {
        return Fields.getTypeDescription(DataTypes.classToType(classType)).toLowerCase();
    }

    /**
     * Clears the constraints and processors in this Datatype, setting them to the default values.
     * If the properties were originally inherited from another datatype (such as through calling the clone method),
     * The property values of that datatype are used instead (even if that datatype changed in the mean time).
     * This approach allows for datatypes that depend on each other to gain the same properties if the
     * datatype set is reloaded.
     * <br />
     * Otherwise, system-defined defaults are used (in most cases resulting in no restrictive properties or processors).
     */
    public void clear() {
        if (origin != null) {
            inherit(origin);
        } else {
            erase();
        }
    }

    /**
     * Clears the constraints and processors in this Datatype, setting it to the default values.
     */
    public void erase() {
        edit();
        origin = null;
        defaultValue = null;
        requiredProperty = null;
        uniqueProperty = null;
        commitProcessor = null;
        enumerationValues = null;
        getProcessors = null;
        setProcessors = null;
    }

    protected DataType.Property inheritProperty(DataType.Property property) {
        if (property == null) {
            return null;
        } else {
            return property.clone(this);
        }
    }

    /**
     * Inherit properties and processors from the passed datatype and
     * sets the passed datatype as the origin for this datatype.
     */
    public void inherit(DataType origin) {
        edit();
        // call erase to clear values
        // need only be done if the origin is NOT an instance of the current class
        // (which would mean that not all values can be inherited)
        if (! this.getClass().isInstance(origin)) {
            erase();
        }
        this.origin = origin;
        defaultValue = origin.defaultValue;
        commitProcessor = origin.commitProcessor;
        if (origin.enumerationValues == null) {
            enumerationValues = null;
        } else {
            enumerationValues = new ArrayList(origin.enumerationValues);
        }
        requiredProperty = inheritProperty(origin.requiredProperty);
        uniqueProperty = inheritProperty(origin.uniqueProperty);
        if (origin.getProcessors == null) {
            getProcessors = null;
        } else {
            getProcessors = (Processor[])origin.getProcessors.clone();
        }
        if (origin.setProcessors == null) {
            setProcessors = null;
        } else {
            setProcessors = (Processor[])origin.setProcessors.clone();
        }
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
        return finish(new Object());
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

    protected final void failOnValidate(DataType.Property property, Object value, Cloud cloud) {
        String error = property.getErrorDescription(cloud==null? null : cloud.getLocale());
        if (error != null) {
            error = error.replaceAll("\\$\\{NAME\\}", property.getName());
            error = error.replaceAll("\\$\\{PROPERTY\\}", ""+property.getValue());
            error = error.replaceAll("\\$\\{VALUE\\}", ""+value);
        }
        throw new IllegalArgumentException(error);
    }

    /**
     * Checks if the passed object is of the correct type (compatible with the type of this data type),
     * and follows the restrictions defined for this type.
     * It throws an IllegalArgumentException if it doesn't.
     * @param value the value to validate
     * @throws IllegalArgumentException if the value is not compatible
     */
    public void validate(Object value) {
        validate(value, null, null, null);
    }

    /**
     * Checks if the passed object follows the restrictions defined for this type.
     * It throws an IllegalArgumentException with a localized message (dependent on the cloud) if it doesn't.
     * @param value the value to validate
     * @param cloud the cloud used to determine the locale for the error message when validation fails
     * @throws IllegalArgumentException if the value is not compatible
     */
    public void validate(Object value, Cloud cloud) {
        validate(value, null, null, cloud);
    }

    /**
     * Checks if the value in the field of the passed node follows the restrictions defined for this type.
     * It throws an IllegalArgumentException with a localized message (dependent on the cloud) if it doesn't.
     * @param node the node for which the datatype is checked. If not <code>null</code>, and the
     *        datatype is determined as unique, than uniquness is checked for this value using the passed field.
     * @param field the field for which the datatype is checked.
     * @throws IllegalArgumentException if the value is not compatible
     */
    public void validate(Node node, Field field) {
        validate(node.getValueWithoutProcess(field.getName()), node, field, node == null ? null : node.getCloud());
    }

    /**
     * Checks if the passed object follows the restrictions defined for this type.
     * It throws an IllegalArgumentException with a localized message (dependent on the cloud) if it doesn't.
     * @param value the value to validate
     * @param node the node for which the datatype is checked. If not <code>null</code>, and the
     *        datatype is determined as unique, than uniquness is checked for this value using the passed field.
     * @param field the field for which the datatype is checked.
     * @param cloud the cloud used to determine the locale for the error message when validation fails
     *        if null, it is retrieved from the passed node if possible.
     * @throws IllegalArgumentException if the value is not compatible
     */
    public void validate(Object value, Node node, Field field, Cloud cloud) {
        if (cloud == null && node != null) cloud = node.getCloud();
        if (value == null && isRequired() && getDefaultValue() == null && commitProcessor == null) {
            // only fail for fields users may actually edit
            if (field == null || field.getState() == Field.STATE_PERSISTENT || field.getState() == Field.STATE_SYSTEM_VIRTUAL) {
                failOnValidate(getRequiredProperty(), value, cloud);
            }
        }
        // test uniqueness
        if (node != null && field != null && value != null && isUnique() && !field.getName().equals("number")) {
            // create a query and query for the value
            NodeQuery query = field.getNodeManager().createQuery();
            Constraint constraint = Queries.createConstraint(query, field.getName(), FieldCompareConstraint.EQUAL, value);
            Queries.addConstraint(query,constraint);
            if (!node.isNew()) {
                constraint = Queries.createConstraint(query, "number", FieldCompareConstraint.NOT_EQUAL, new Integer(node.getNumber()));
                Queries.addConstraint(query,constraint);
            }
            log.debug(query);
            if (Queries.count(query) > 0) {
                failOnValidate(getUniqueProperty(), value, node.getCloud());
            }
        }
        // test enumerations
        // if (enumerationValues != null && enumerationValues.size() == -1) {
        //     ...
        // }
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(getName() + " (" + getTypeAsClass() + ")\n");
        buf.append(commitProcessor == null ? "" : "commit:" + commitProcessor.getClass().getName() + "\n");
        if (getProcessors == null) {
            for (int i =0; i < 13; i++) {
                buf.append(getProcessors[i] == null ? "" : "\nget [" + DataTypes.typeToClass(i) + "]:" + getProcessors[i].getClass().getName() + "\n");
            }
        }
        if (setProcessors == null) {
            for (int i =0; i < 13; i++) {
                buf.append(setProcessors[i] == null ? "" : "\nset [" + DataTypes.typeToClass(i) + "]:" + setProcessors[i].getClass().getName() + "\n");
            }
        }
        if (isRequired()) {
            buf.append("required\n");
        }
        if (isUnique()) {
            buf.append("unique\n");
        }
        return buf.toString();
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
            // reset owner if it was set, so this datatype can be changed
            clone.owner = null;
            // properly inherit from this datatype (this also clones properties and processor arrays)
            clone.inherit(this);
            return clone;
        } catch (CloneNotSupportedException cnse) {
            // should not happen
            log.error("Cannot clone this DataType: " + name);
            throw new RuntimeException("Cannot clone this DataType: " + name, cnse);
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
     * required properties are only 'utilities'.
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
     * Note that the MMBase core does not generally enforce required fields to be filled -
     * If not provided, a default value (generally an empty string or the integer value -1)
     * is filled in by the system.
     *
     * @return  <code>true</code> if the field is required
     * @since  MMBase-1.6
     */
    public boolean isRequired() {
        if (requiredProperty == null) {
            return PROPERTY_REQUIRED_DEFAULT.booleanValue();
        } else {
            return Boolean.TRUE.equals(requiredProperty.getValue());
        }
    }

    /**
     * Returns the 'required' property, containing the value, errormessages, and fixed status of this attribute.
     * @return the property as a {@link DataType#Property}
     */
    public DataType.Property getRequiredProperty() {
        if (requiredProperty == null) requiredProperty = createProperty(PROPERTY_REQUIRED, PROPERTY_REQUIRED_DEFAULT);
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

    /**
     * Returns whether this field has a unique constraint.
     * Uniqueness is generally achieved through association of the datatype with one or more sets of fields.
     * This is notably different from other datatype properties.
     *
     * Note that the MMBase core does not generally enforce uniqueness, but the storage layer might.
     *
     * @return  <code>true</code> if the field is unique
     * @since  MMBase-1.6
     */
    public boolean isUnique() {
        if (uniqueProperty == null) {
            return PROPERTY_UNIQUE_DEFAULT.booleanValue();
        } else {
            return Boolean.TRUE.equals(uniqueProperty.getValue());
        }
    }

    /**
     * Returns the 'unique' property, containing the value, error messages, and fixed status of this attribute.
     * @return the property as a {@link DataType#Property}
     */
    public DataType.Property getUniqueProperty() {
        if (uniqueProperty == null) uniqueProperty = createProperty(PROPERTY_UNIQUE, PROPERTY_UNIQUE_DEFAULT);
        return uniqueProperty;
    }

    /**
     * Sets whether the data type requires a value.
     * @param unique <code>true</code> if a value is unique
     * @param InvalidStateException if the datatype was finished (and thus can no longer be changed)
     * @return the datatype property that was just set
     */
    public DataType.Property setUnique(boolean unique) {
        return setProperty(getUniqueProperty(),Boolean.valueOf(unique));
    }

    public DataType.EnumerationValue addEnumerationValue(Object value) {
        DataType.EnumerationValue enumerationValue = new EnumerationValue(value);
        if (enumerationValues == null) {
            enumerationValues = new ArrayList();
        }
        enumerationValues.add(enumerationValue);
        return enumerationValue;
    }

    public List getEnumerationValues() {
        return enumerationValues;
    }

    public void setEnumerationValues(List enumerationValues) {
        this.enumerationValues = enumerationValues;
    }

    /**
     * Processes a value, according to the default processors set on this datatype.
     * @see #process(int, Node, Field, Object, int)
     * @param action either PROCESS_COMMIT, PROCESS_GET, or PROCESS_SET
     * @param node the node for wich the values should be processed
     * @param field the field for wioch the values should be processed
     * @param value The value to process
     * @return the processed value
     */
    public Object process(int action, Node node, Field field, Object value) {
        return process(action, node, field, value, Field.TYPE_UNKNOWN);
    }

    /**
     * Processes a value, according to the processors set on this datatype.
     * Also, when committing, if the value is <code>null</code>, but is required,
     * the default value (if one exists) is assigned instead.
     * <br />
     * If you ask for a PROCESS_COMMIT action, and the commit processor is defined,
     * eitehr the commit() action is called (if the processor is a Commitprocessor),
     * or the process() method on the commit processor (with <code>null</code> passed
     * as a value.
     * <br />
     * If you ask for a PROCESS_GET action, and a get processor is defined, the process()
     * method of that processor is called.
     * <br />
     * If you ask for a PROCESS_SET action, and a set processor is defined, the process()
     * method of that processor is called. If a set processor is not defnied but a
     * commitProcessor is, the process() method on the commit processor is called.
     * <br />
     * @param action either PROCESS_COMMIT, PROCESS_GET, or PROCESS_SET
     * @param node the node for which the values should be processed
     * @param field the field for wich the values should be processed
     * @param value The value to process
     * @param processingType the MMBase type defining the type of value to process
     * @return the processed value
     */
    public Object process(int action, Node node, Field field, Object value, int processingType) {
        Object result = value;
        Processor processor = getProcessor(action, processingType);
        if (processor == null) processor = getProcessor(action);
        if (processor == null && action == PROCESS_SET) {
            processor = getProcessor(PROCESS_COMMIT, processingType);
            if (processor == null) {
                processor = getProcessor(PROCESS_COMMIT);
            }
        }
        if (processor != null) {
            if (action == PROCESS_COMMIT && processor instanceof CommitProcessor) {
                if (log.isDebugEnabled()) {
                    log.debug("commit:" + processor.getClass().getName());
                }
                ((CommitProcessor)processor).commit(node, field);

            } else {
                if (log.isDebugEnabled()) {
                    log.debug("process:" + processor.getClass().getName());
                }
                result = processor.process(node, field, value);
            }
        }
        // only with commit: if the data is required but the value is null,
        // set to the default value.
        if (action == PROCESS_COMMIT && result == null && isRequired()) {
            result = getDefaultValue();
        }
        return result;
    }

    /**
     * Returns the default processor for this action
     * @param action either {@link #PROCESS_COMMIT}, {@link #PROCESS_GET}, or {@link #PROCESS_SET}
     */
    public Processor getProcessor(int action) {
        Processor processor = null;
        if (action == PROCESS_COMMIT) {
            processor =  commitProcessor;
        } else if (action == PROCESS_GET) {
            processor =  getProcessors == null ? null : getProcessors[0];
        } else {
            processor =  setProcessors == null ? null : setProcessors[0];
        }
        return processor;
    }

    /**
     * Returns the processor for this action and processing type
     * @param action either PROCESS_COMMIT, PROCESS_GET, or PROCESS_SET
     * @param processingType the MMBase type defining the type of value to process, ignored if action - PROCESS_COMMIT
     */
    public Processor getProcessor(int action, int processingType) {
        if (processingType == Field.TYPE_UNKNOWN) {
            return getProcessor(action);
        } else {
            Processor processor = null;
            if (action == PROCESS_COMMIT) {
                processor =  commitProcessor;
            } else if (action == PROCESS_GET) {
                processor =  getProcessors == null ? null : getProcessors[processingType];
            } else {
                processor =  setProcessors == null ? null : setProcessors[processingType];
            }
            return processor;
        }
    }

    /**
     * Sets the processor for this action
     * @param action either PROCESS_COMMIT, PROCESS_GET, or PROCESS_SET
     */
    public void setProcessor(int action, Processor processor) {
        setProcessor(action, processor, Field.TYPE_UNKNOWN);
    }

    private Processor[] newProcessorsArray() {
        return new Processor[] {
             null /* object   */, null /* string  */, null /* integer */, null /* not used */, null /* byte */,
             null /* float    */, null /* double  */, null /* long    */, null /* xml      */, null /* node */,
             null /* datetime */, null /* boolean */, null /* list    */
        };
    }

    /**
     * Sets the processor for this action
     * @param action either PROCESS_COMMIT, PROCESS_GET, or PROCESS_SET
     * @param processingType the MMBase type defining the type of value to process, ignored if action - PROCESS_COMMIT
     */
    public void setProcessor(int action, Processor processor, int processingType) {
        if (processingType == Field.TYPE_UNKNOWN) {
            processingType = 0;
        }
        if (action == PROCESS_COMMIT) {
            commitProcessor = processor;
        } else if (action == PROCESS_GET) {
            if (getProcessors == null) getProcessors = newProcessorsArray();
            getProcessors[processingType] = processor;
        } else {
            if (setProcessors == null) setProcessors = newProcessorsArray();
            setProcessors[processingType] = processor;
        }
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
        private LocalizedString errorDescription = null;
        private boolean fixed = false;

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

        public void setErrorDescription(String description) {
            setErrorDescription(description, null);
        }

        public void setErrorDescription(String description, Locale locale) {
            if (errorDescription == null) {
                errorDescription = new LocalizedString(description);
            }
            errorDescription.set(description, locale);
        }

        public String getErrorDescription(Locale locale) {
            if (errorDescription == null) {
                return null;
            } else {
                return errorDescription.get(locale);
            }
        }

        public String getErrorDescription() {
            return errorDescription.get(null);
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

        public DataType.Property clone(DataType dataType) {
            DataType.Property clone = ((DataType)dataType).new Property(name, value);
            if (errorDescription != null) {
                clone.setLocalizedErrorDescription((LocalizedString)errorDescription.clone());
            }
            clone.setFixed(fixed);
            return clone;
        }

        public String toString() {
            return name + " : " + value + ( fixed ? " (fixed)" : "");
        }

    }

    public final class EnumerationValue implements Cloneable {
        private Object value;
        private LocalizedString description = null;

        private EnumerationValue(Object value) {
            this.value = value;
        }

        public Object getValue() {
            return value;
        }

        public LocalizedString getLocalizedDescription() {
            return description;
        }

        public void setLocalizedDescription(LocalizedString description) {
            this.description = description;
        }

        public void setDescription(String description) {
            setDescription(description, null);
        }

        public void setDescription(String descriptionText, Locale locale) {
            if (description == null) {
                description = new LocalizedString(descriptionText);
            }
            description.set(descriptionText, locale);
        }

        public String getDescription(Locale locale) {
            if (description == null) {
                return null;
            } else {
                return description.get(locale);
            }
        }

        public String getDescription() {
            return description.get(null);
        }

        public DataType.EnumerationValue clone(DataType dataType) {
            DataType.EnumerationValue clone = ((DataType)dataType).new EnumerationValue(value);
            if (description != null) {
                clone.setLocalizedDescription((LocalizedString)description.clone());
            }
            return clone;
        }

        public String toString() {
            return value + " : " + getDescription();
        }

    }
}
