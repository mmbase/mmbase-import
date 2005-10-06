/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.datatypes;

import java.util.*;
import java.io.*; // because of Serializable
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
 * Every DataType extends this one. It's extensions can however implement several extensions of the
 * DataType interface (e.g. some datatypes (at least {@link StringDataType}) are both {@link LengthDataType}
 * and {@link ComparableDataType}, and some are only one ({@link BinaryDataType}, {@link
 * NumberDatatype}). In other words, this arrangement is like this, because java does not support
 * Multipible inheritance.
 *
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @since  MMBase-1.8
 * @version $Id: BasicDataType.java,v 1.1 2005-10-06 23:02:03 michiel Exp $
 */

public class BasicDataType extends AbstractDescriptor implements DataType, Cloneable, Comparable, Descriptor {
    /**
     * The bundle used by datatype to determine default prompts for error messages when a
     * validation fails.
     */
    public static final String DATATYPE_BUNDLE = "org.mmbase.datatypes.resources.datatypes";

    private static final String CONSTRAINT_REQUIRED = "required";
    private static final Boolean CONSTRAINT_REQUIRED_DEFAULT = Boolean.FALSE;

    private static final String CONSTRAINT_UNIQUE = "unique";
    private static final Boolean CONSTRAINT_UNIQUE_DEFAULT = Boolean.FALSE;

    private static final Logger log = Logging.getLoggerInstance(BasicDataType.class);


    protected DataType.ValueConstraint requiredConstraint;
    protected DataType.ValueConstraint uniqueConstraint;

    /**
     * The datatype from which this datatype originally inherited it's properties.
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
    public BasicDataType(String name) {
        this(name, Object.class);
    }

    /**
     * Create a data type object
     * @param name the name of the data type
     * @param classType the class of the data type's possible value
     */
    protected BasicDataType(String name, Class classType) {
        super(name);
        this.classType = classType;
        owner = null;
    }

    // implementation of serializable
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(requiredConstraint);
        out.writeObject(uniqueConstraint);
        if (owner instanceof Serializable) {
            out.writeObject(owner);
        } else {
            out.writeObject(owner == null ? null : "OWNER");
        }
        out.writeObject(classType);
        if (defaultValue instanceof Serializable) {
            out.writeObject(defaultValue);
        } else {
            log.warn("Default value is not serializable");
            out.writeObject(null);
        }
        out.writeObject(commitProcessor);
        out.writeObject(getProcessors);
        out.writeObject(setProcessors);
    }
    // implementation of serializable
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        requiredConstraint = (DataType.ValueConstraint) in.readObject();
        uniqueConstraint   = (DataType.ValueConstraint) in.readObject();
        owner              = in.readObject();
        classType          = (Class) in.readObject();
        defaultValue       = in.readObject();
        commitProcessor    = (CommitProcessor) in.readObject();
        getProcessors      = (Processor[]) in.readObject();
        setProcessors      = (Processor[]) in.readObject();
    }



    protected String getBaseTypeIdentifier() {
        return Fields.getTypeDescription(Fields.classToType(classType)).toLowerCase();
    }



    /**
     * @inheritDoc
     */
    public void inherit(BasicDataType origin) {
        edit();
        this.origin = origin;
        defaultValue = origin.getDefaultValue();
        commitProcessor = origin.commitProcessor;
        if (origin.getEnumerationFactory() == null) {
            enumerationValues = null;
        } else {
            enumerationValues = (LocalizedEntryListFactory) origin.getEnumerationFactory().clone();
        }
        requiredConstraint = new AbstractValueConstraint(origin.getRequiredConstraint());
        uniqueConstraint   = new AbstractValueConstraint(origin.getUniqueConstraint());

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
     * @inheritDoc
     */
    public DataType getOrigin() {
        return origin;
    }

    /**
     * @inheritDoc
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
     * @inheritDoc
     */
    public void checkType(Object value) {
        if (!isCorrectType(value)) {
            // customize this?
            throw new IllegalArgumentException("DataType of '" + value + "' for '" + getName() + "' must be of type " + classType + " (but is " + (value == null ? value : value.getClass()) + ")");
        }
    }

    /**
     * @inheritDoc
     */
    public Object autoCast(Object value) {
        return Casting.toType(classType, value);
    }

    /**
     * @inheritDoc
     */
    public Object getDefaultValue() {
        return defaultValue;
    }

    /**
     * @inheritDoc
     */
    public DataType setDefaultValue(Object def) {
        edit();
        defaultValue = autoCast(def);
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
    protected final Collection addError(Collection errors, DataType.ValueConstraint constraint, Object value) {
        if (errors == VALID) errors = new ArrayList();
        if (constraint.getErrorDescription() == null) {
            throw new IllegalArgumentException("Failed " + constraint+ " for value " + value);
        }
        ReplacingLocalizedString error = new ReplacingLocalizedString(constraint.getErrorDescription());
        error.replaceAll("\\$\\{NAME\\}",       constraint.getName());
        error.replaceAll("\\$\\{CONSTRAINT\\}",   "" + constraint.toString());
        error.replaceAll("\\$\\{VALUE\\}",      "" + value);
        errors.add(error);
        return errors;
    }

    /**
     * @inheritDoc
     */
    public final Collection /*<LocalizedString>*/ validate(Object value) {
        return validate(value, null, null);
    }

    /**
     * @inheritDoc
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
            // XXX This will test for uniquness using the cloud, so you'll miss objects you can't
            // see (and database doesn't know that!)
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
                buf.append(getProcessors[i] == null ? "" : ("; get [" + Fields.typeToClass(i) + "]:" + getProcessors[i] + " "));
            }
        }
        if (setProcessors != null) {
            for (int i =0; i < 13; i++) {
                buf.append(setProcessors[i] == null ? "" : ("; set [" + Fields.typeToClass(i) + "]:" + setProcessors[i] + " "));
            }
        }
        if (isRequired()) {
            buf.append("  required");
        }
        if (isUnique()) {
            buf.append("  unique");
        }
        if (isFinished()) {
            buf.append(".");
        }
        return buf.toString();
    }


    /**
     * @inheritDoc
     * This method is final, override {@link #clone(String)} in stead.
     */
    public final Object clone() {
        return clone(getName() + ".clone");
    }

    /**
     * @inheritDoc
     */
    public Object clone(String name) {
        try {
            BasicDataType clone = (BasicDataType)super.clone(name);
            // reset owner if it was set, so this datatype can be changed
            clone.owner = null;
            // properly inherit from this datatype (this also clones properties and processor arrays)
            clone.inherit(this);
            log.debug("Cloned " + this + " -> " + clone);
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
     * @inheritDoc
     */
    public boolean isRequired() {
        return requiredConstraint == null ? CONSTRAINT_REQUIRED_DEFAULT.booleanValue() : Boolean.TRUE.equals(requiredConstraint.getValue());
    }

    /**
     * @inheritDoc
     */
    public DataType.ValueConstraint getRequiredConstraint() {
        if (requiredConstraint == null) requiredConstraint = new AbstractValueConstraint(CONSTRAINT_REQUIRED, CONSTRAINT_REQUIRED_DEFAULT);
        return requiredConstraint;
    }

    /**
     * @inheritDoc
     */
    public DataType.ValueConstraint setRequired(boolean required) {
        return getRequiredConstraint().setValue(Boolean.valueOf(required));
    }

    /**
     * @inheritDoc
     */
    public boolean isUnique() {
        return uniqueConstraint == null ? CONSTRAINT_UNIQUE_DEFAULT.booleanValue() : Boolean.TRUE.equals(uniqueConstraint.getValue());
    }

    /**
     * @inheritDoc
     */
    public DataType.ValueConstraint getUniqueConstraint() {
        if (uniqueConstraint == null) uniqueConstraint = new AbstractValueConstraint(CONSTRAINT_UNIQUE, CONSTRAINT_UNIQUE_DEFAULT);
        return uniqueConstraint;
    }

    /**
     * @inheritDoc
     */
    public DataType.ValueConstraint setUnique(boolean unique) {
        return getUniqueConstraint().setValue(Boolean.valueOf(unique));
    }


    /**
     * @inheritDoc
     */
    public Collection getEnumerationValues(Locale locale, Cloud cloud, Node node, Field field) {
        if (enumerationValues == null || enumerationValues.size() == 0) return null;
        return enumerationValues.get(locale);
    }

    /**
     * @inheritDoc
     */
    public LocalizedEntryListFactory getEnumerationFactory() {
        if(enumerationValues == null) {
            enumerationValues = new LocalizedEntryListFactory();
        }
        return enumerationValues;
    }

    /**
     * @inheritDoc
     */
    public Object process(int action, Node node, Field field, Object value) {
        return process(action, node, field, value, Field.TYPE_UNKNOWN);
    }

    /**
     * @inheritDoc
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
     * @inheritDoc
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
     * @inheritDoc
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
     * @inheritDoc
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
     * @inheritDoc
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


    /**
     * 
     */
    protected class AbstractValueConstraint extends StaticAbstractValueConstraint {
        protected AbstractValueConstraint(DataType.ValueConstraint source) {
            super(BasicDataType.this, source);
        }
        protected AbstractValueConstraint(String name, Object value) {
            super(BasicDataType.this, name, value);
        }
    }
    /**
     * A Constraint is represented by these kind of objects.
     * When you override this class, take care of cloning of outer class!
     * This class itself is not cloneable. Cloning is hard when you have inner classes.
     *
     * See <a href="http://www.adtmag.com/java/articleold.asp?id=364">article of inner classes,
     * cloning in java</a>
     */
    protected static class StaticAbstractValueConstraint implements DataType.ValueConstraint {
        private final String name;
        protected final BasicDataType parent;
        private LocalizedString errorDescription;
        private Object value;
        private boolean fixed = false;

        protected StaticAbstractValueConstraint(BasicDataType parent, DataType.ValueConstraint source) {
            this.name = source.getName();
            this.parent = parent;
            inherit(source);
        }

        protected StaticAbstractValueConstraint(BasicDataType parent, String name, Object value) {
            this.name = name;
            this.parent = parent;
            String key = parent.getBaseTypeIdentifier() + "." + name + ".error";
            errorDescription = new LocalizedString(key);
            errorDescription.setBundle(DATATYPE_BUNDLE);
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public Object getValue() {
            return value;
        }

        public ValueConstraint setValue(Object value) {
            log.debug("Settign constraint " + name + " on " + parent);
            parent.edit();
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
                return parent.addError(errors, this, value);
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


        protected void inherit(DataType.ValueConstraint source) {
            value = source.getValue();
            // perhaps this value must be cloned?, but how?? Cloneable has no public methods....
            errorDescription = (LocalizedString) source.getErrorDescription().clone();
        }


        public String toString() {
            return name + " : " + value + ( fixed ? "." : "");
        }

    }

}
