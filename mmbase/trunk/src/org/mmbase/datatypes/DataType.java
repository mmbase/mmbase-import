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
import org.mmbase.core.util.Fields;
import org.mmbase.core.AbstractDescriptor;
import org.mmbase.datatypes.DataTypes;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @since  MMBase-1.8
 * @version $Id: DataType.java,v 1.23 2005-09-15 15:05:02 michiel Exp $
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

    private static final String CONSTRAINT_REQUIRED = "required";
    private static final Boolean CONSTRAINT_REQUIRED_DEFAULT = Boolean.FALSE;

    private static final String CONSTRAINT_UNIQUE = "unique";
    private static final Boolean CONSTRAINT_UNIQUE_DEFAULT = Boolean.FALSE;

    private static final Logger log = Logging.getLoggerInstance(DataType.class);

    public static final Collection VALID = Collections.EMPTY_LIST;

    /**
     * The 'required' property.
     */
    protected DataType.ValueConstraint requiredConstraint;

    /**
     * The 'unique' property.
     */
    protected DataType.ValueConstraint uniqueConstraint;

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

    private LocalizedEntryListFactory enumerationValues = null;

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
        requiredConstraint = null;
        uniqueConstraint = null;
        commitProcessor = null;
        enumerationValues = null;
        getProcessors = null;
        setProcessors = null;
    }

    protected DataType.ValueConstraint inheritConstraint(DataType.ValueConstraint property) {
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
            enumerationValues = (LocalizedEntryListFactory) enumerationValues.clone();
        }
        requiredConstraint = inheritConstraint(origin.requiredConstraint);
        uniqueConstraint = inheritConstraint(origin.uniqueConstraint);
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
     * Return the DataType from which this one inherited, or <code>null</code>
     */
    public DataType getOrigin() {
        return origin;
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
            throw new IllegalStateException("This data type '" + getName() + "' is finished and can no longer be changed.");
        }
    }

    /**
     * Adds a new error message to the errors collection, based on given Constraint. If this
     * error-collection is unmodifiable (VALID), it is replace with a new empty one first.
     */
    protected final Collection addError(Collection errors, DataType.ValueConstraint property, Object value) {
        if (errors == VALID) errors = new ArrayList();
        if (property.getErrorDescription() == null) {
            throw new IllegalArgumentException("Failed " + property + " for value " + value);
        }
        ReplacingLocalizedString error = new ReplacingLocalizedString(property.getErrorDescription());
        error.replaceAll("\\$\\{NAME\\}",       property.getName());
        error.replaceAll("\\$\\{CONSTRAINT\\}",   ""+property.getValue());
        error.replaceAll("\\$\\{VALUE\\}",      ""+value);
        errors.add(error);
        return errors;
    }

    /**
     * Checks if the passed object is of the correct type (compatible with the type of this data type),
     * and follows the restrictions defined for this type.
     * @return An error message if the value is not compatible. An empty collection if valid.
     * @param value the value to validate
     */
    public final Collection /*<LocalizedString>*/ validate(Object value) {
        return validate(value, null, null);
    }

    /**
     * Checks if the passed object follows the restrictions defined for this type.
     * @param value the value to validate
     * @param node the node for which the datatype is checked. If not <code>null</code>, and the
     *        datatype is determined as unique, than uniquness is checked for this value using the passed field.
     * @param field the field for which the datatype is checked.
     *
     * @return The error message(s) if the value is not compatible. An empty collection if the value is valid.
     */
    public Collection /*<LocalizedString> */ validate(Object value, Node node, Field field) {
        Collection errors = VALID;
        if (value == null && isRequired() && getDefaultValue() == null && commitProcessor == null) {
            // only fail for fields users may actually edit
            if (field == null || field.getState() == Field.STATE_PERSISTENT || field.getState() == Field.STATE_SYSTEM_VIRTUAL) {
                errors = addError(errors, getRequiredConstraint(), value);
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
                errors = addError(errors, getUniqueConstraint(), value);
            }
        }
        // test enumerations
        // if (enumerationValues != null && enumerationValues.size() == 0) {
        //     ...
        // }
        return errors;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(getName() + " (" + getTypeAsClass() + (defaultValue != null ? ":" + defaultValue : "") + ")");
        buf.append(commitProcessor == null ? "" : " commit: " + commitProcessor.getClass().getName() + "");
        if (getProcessors != null) {
            for (int i = 0; i < 13; i++) {
                buf.append(getProcessors[i] == null ? "" : ("; get [" + DataTypes.typeToClass(i) + "]:" + getProcessors[i] + " "));
            }
        }
        if (setProcessors != null) {
            for (int i =0; i < 13; i++) {
                buf.append(setProcessors[i] == null ? "" : ("; set [" + DataTypes.typeToClass(i) + "]:" + setProcessors[i] + " "));
            }
        }
        if (isRequired()) {
            buf.append("  required");
        }
        if (isUnique()) {
            buf.append("  unique");
        }
        return buf.toString();
    }

    /**
     * Returns a cloned instance of this datatype, inheriting all validation rules.
     * Unlike the original datatype though, the cloned copy is declared unfinished even if the original
     * was finished. This means that the cloned datatype can be changed.
     * This method is final, override {@link #clone(String)} in stead.

     */
    public final Object clone() {
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
        if (requiredConstraint == null) {
            return CONSTRAINT_REQUIRED_DEFAULT.booleanValue();
        } else {
            return Boolean.TRUE.equals(requiredConstraint.getValue());
        }
    }

    /**
     * Returns the 'required' property, containing the value, errormessages, and fixed status of this attribute.
     * @return the property as a {@link DataType#Constraint}
     */
    public DataType.ValueConstraint getRequiredConstraint() {
        if (requiredConstraint == null) requiredConstraint = new ValueConstraint(CONSTRAINT_REQUIRED, CONSTRAINT_REQUIRED_DEFAULT);
        return requiredConstraint;
    }

    /**
     * Sets whether the data type requires a value.
     * @param required <code>true</code> if a value is required
     * @param InvalidStateException if the datatype was finished (and thus can no longer be changed)
     * @return the datatype property that was just set
     */
    public DataType.ValueConstraint setRequired(boolean required) {
        return getRequiredConstraint().setValue(Boolean.valueOf(required));
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
        if (uniqueConstraint == null) {
            return CONSTRAINT_UNIQUE_DEFAULT.booleanValue();
        } else {
            return Boolean.TRUE.equals(uniqueConstraint.getValue());
        }
    }

    /**
     * Returns the 'unique' property, containing the value, error messages, and fixed status of this attribute.
     * @return the property as a {@link DataType#Constraint}
     */
    public DataType.ValueConstraint getUniqueConstraint() {
        if (uniqueConstraint == null) uniqueConstraint = new ValueConstraint(CONSTRAINT_UNIQUE, CONSTRAINT_UNIQUE_DEFAULT);
        return uniqueConstraint;
    }

    /**
     * Sets whether the data type requires a value.
     * @param unique <code>true</code> if a value is unique
     * @param InvalidStateException if the datatype was finished (and thus can no longer be changed)
     * @return the datatype property that was just set
     */
    public DataType.ValueConstraint setUnique(boolean unique) {
        return getUniqueConstraint().setValue(Boolean.valueOf(unique));
    }


    /**
     * @return A List of all possible values for this datatype, as {@link java.util.Map.Entry}s, or
     * <code>null</code> if no restrictions apply. Every Map entry contains as key the 'value' for
     * this datatype and as value it contains the description for this value in the given locale.
     * 
     * @param locale for which locale to produce
     * @param node   Possibly the possible values depend on a cloud (security)
     * @param node   Possibly the possible values depend on an actual node (this may be, and in the default implementation is, ignored)
     * @param field   Possibly the possible values depend on an actual field (this may be, and in the default implementation is, ignored)
     *
     */
    public Collection getEnumerationValues(Locale locale, Cloud cloud, Node node, Field field) {
        if (enumerationValues == null || enumerationValues.size() == 0) return null;
        return enumerationValues.get(locale);
    }

    /**
     * @return the LocalizedEntryListFactory which will be used to produce the result of {@link
     * #getEnumerationValues}. Never <code>null</code>. This can be used to add more possible values.
     */
    public LocalizedEntryListFactory getEnumerationFactory() {
        if(enumerationValues == null) {
            enumerationValues = new LocalizedEntryListFactory();
        }
        return enumerationValues;
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
                    log.debug("process for " + this + processor.getClass().getName());
                }

                result = processor.process(node, field, value);
            }
        }

        return result;
    }


    /**
     * Returns the default processor for this action
     * @param action either {@link #PROCESS_COMMIT}, {@link #PROCESS_GET}, or {@link #PROCESS_SET}
     * XXX What exactly would be against getCommitProcessor(), getGetProcesor(), getSetProcessor() ?
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


    public class ValueConstraint implements Cloneable {
        private String name;
        private Object value;
        private LocalizedString errorDescription;
        private boolean fixed = false;

        protected ValueConstraint(String name, Object value) {
            this.name = name;
            this.value = value;
            String key = DataType.this.getBaseTypeIdentifier() + "." + name + ".error";
            errorDescription = new LocalizedString(key);
            errorDescription.setBundle(DataType.DATATYPE_BUNDLE);
        }

        public String getName() {
            return name;
        }

        public Object getValue() {
            return value;
        }

        public ValueConstraint setValue(Object value) {
            DataType.this.edit();
            if (fixed) {
                throw new IllegalStateException("Constraint '" + name + "' is fixed, cannot be changed");
            }
            this.value = value;
            return this;
        }

        public LocalizedString getErrorDescription() {
            return errorDescription;
        }

        public void setErrorDescription(LocalizedString errorDescription) {
            this.errorDescription = errorDescription;
        }


        public boolean isFixed() {
            return fixed;
        }

        public void setFixed(boolean fixed) {
            if (this.fixed && !fixed) {
                throw new IllegalStateException("Constraint '" + name + "' is fixed, cannot be changed");
            }
            this.fixed = fixed;
        }

        protected final Collection validate(Collection errors, Object value, Node node, Field field) {
            if (! valid(value, node, field)) {
                return DataType.this.addError(errors, this, value);
            } else {
                return errors;

            }
        }
        /**
         * Proposal: make this abstract. And see NodeDataType
         *
         * Validates for a given value whether this constraints applies.
         */
        public boolean valid(Object value, Node node, Field field) {
            throw new UnsupportedOperationException("Not supported");
        }

        protected void inherit(ValueConstraint val) {
            value = val.value;
            errorDescription = (LocalizedString) val.errorDescription.clone();
        }


        public DataType.ValueConstraint clone(DataType dataType) {
            DataType.ValueConstraint clone = ((DataType)dataType).new ValueConstraint(name, value);
            if (errorDescription != null) {
                clone.setErrorDescription((LocalizedString)errorDescription.clone());
            }
            clone.setFixed(fixed);
            return clone;
        }

        public String toString() {
            return name + " : " + value + ( fixed ? " (fixed)" : "");
        }

    }

}
