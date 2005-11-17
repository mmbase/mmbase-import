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
import org.mmbase.datatypes.processors.*;
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
 * NumberDataType}). In other words, this arrangement is like this, because java does not support
 * Multipible inheritance.
 *
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @since  MMBase-1.8
 * @version $Id: BasicDataType.java,v 1.28 2005-11-17 18:10:21 michiel Exp $
 */

public class BasicDataType extends AbstractDescriptor implements DataType, Cloneable, Comparable, Descriptor {
    /**
     * The bundle used by datatype to determine default prompts for error messages when a
     * validation fails.
     */
    public static final String DATATYPE_BUNDLE = "org.mmbase.datatypes.resources.datatypes";
    private static final Logger log = Logging.getLoggerInstance(BasicDataType.class);

    protected RequiredRestriction requiredRestriction        = new RequiredRestriction(false);
    protected UniqueRestriction   uniqueRestriction          = new UniqueRestriction(false);
    protected TypeRestriction     typeRestriction            = new TypeRestriction();
    protected EnumerationRestriction enumerationRestriction  = new EnumerationRestriction((LocalizedEntryListFactory) null);

    /**
     * The datatype from which this datatype originally inherited it's properties.
     */
    protected DataType origin = null;

    private Object owner;
    private Class classType;
    private Object defaultValue;

    private CommitProcessor commitProcessor = EmptyCommitProcessor.getInstance();
    private Processor[]     getProcessors;
    private Processor[]     setProcessors;

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

    private static final int serialVersionUID = 1; // increase this if object serialization changes (which we shouldn't do!)

    // implementation of serializable
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(requiredRestriction);
        out.writeObject(uniqueRestriction);
        //out.writeObject(enumerationRestriction.value);
        if (owner instanceof Serializable) {
            out.writeObject(owner);
        } else {
            out.writeObject(owner == null ? null : "OWNER");
        }
        out.writeObject(classType);
        if (defaultValue instanceof Serializable || defaultValue == null) {
            out.writeObject(defaultValue);
        } else {
            log.warn("Default value " + defaultValue.getClass() + " '" + defaultValue + "' is not serializable, taking it null, which may not be correct.");
            out.writeObject(null);
        }
        out.writeObject(commitProcessor);
        out.writeObject(getProcessors);
        out.writeObject(setProcessors);
    }
    // implementation of serializable
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        requiredRestriction    = (RequiredRestriction) in.readObject();
        uniqueRestriction      = (UniqueRestriction) in.readObject();
        //enumerationRestriction = new EnumerationRestriction((LocalizedEntryListFactory) in.readObject());
        typeRestriction        = new TypeRestriction(); // its always the same, so no need actually persisting it.
        owner                 = in.readObject();
        classType             =  (Class) in.readObject();
        defaultValue          = in.readObject();
        commitProcessor       = (CommitProcessor) in.readObject();
        getProcessors         = (Processor[]) in.readObject();
        setProcessors         = (Processor[]) in.readObject();
    }



    protected String getBaseTypeIdentifier() {
        return Fields.getTypeDescription(Fields.classToType(classType)).toLowerCase();
    }

    /**
     * {@inheritDoc}
     */
    public void inherit(BasicDataType origin) {
        edit();
        this.origin = origin;
        defaultValue = origin.getDefaultValue();
        commitProcessor = origin.commitProcessor;
        enumerationRestriction = new EnumerationRestriction(origin.enumerationRestriction);
        requiredRestriction = new RequiredRestriction(origin.requiredRestriction);
        uniqueRestriction   = new UniqueRestriction(origin.uniqueRestriction);

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
     * {@inheritDoc}
     */
    public DataType getOrigin() {
        return origin;
    }

    /**
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    public void checkType(Object value) {
        if (!isCorrectType(value)) {
            // customize this?
            throw new IllegalArgumentException("DataType of '" + value + "' for '" + getName() + "' must be of type " + classType + " (but is " + (value == null ? value : value.getClass()) + ")");
        }
    }


    /**
     * {@inheritDoc}
     *
     * Tries to determin  cloud by node and field if possible and wraps {@link preCast(Object, Cloud, Node, Field}.
     */
    public final Object preCast(Object value, Node node, Field field) {
        return preCast(value, getCloud(node, field), node, field);
    }

    /**
     * This method is as yet unused, but can be anticipated
     */
    public final Object preCast(Object value, Cloud cloud) {
        return preCast(value, cloud, null, null);
    }

    /**
     * This method implements 'precasting', which can be seen as a kind of datatype specific
     * casting.  It should anticipated that every argument can be <code>null</code>. It should not
     * change the actual type of the value.
     */
    protected Object preCast(Object value, Cloud cloud, Node node, Field field) {
        return enumerationRestriction.preCast(value, cloud);
    }


    /**
     * {@inheritDoc}
     *
     * No need to override this. It is garantueed by javadoc that cast should work out of preCast
     * using Casting.toType. So that is what this final implementation is doing.
     *
     * Override {@link preCast(Object, Cloud, Node, Field)}
     */
    public final Object cast(Object value, Node node, Field field) {
        return cast(value, getCloud(node, field), node, field);
    }

    /**
     * Utility to avoid repitive calling of getCloud
     */
    protected final Object cast(Object value, Cloud cloud, Node node, Field field) {
        return Casting.toType(classType, cloud, preCast(value, cloud, node, field));
    }

    protected final Cloud getCloud(Node node, Field field) {
        if (node != null) return node.getCloud();
        if (field != null) return field.getNodeManager().getCloud();
        return null;
    }

    /**
     * Before validating the value, the value will be 'casted', on default this will be to the
     * 'correct' type, but it can be a more generic type sometimes. E.g. for numbers this wil simply
     * cast to Number.
     */
    protected Object castToValidate(Object value, Node node, Field field) {
        return cast(value, node, field);
    }

    /**
     * {@inheritDoc}
     */
    public Object getDefaultValue() {
        return defaultValue;
    }

    /**
     * {@inheritDoc}
     */
    public DataType setDefaultValue(Object def) {
        edit();
        defaultValue = cast(def, null, null);
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
        if (this.owner != null) {
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
     * {@inheritDoc}
     */
    public final Collection /*<LocalizedString>*/ validate(Object value) {
        return validate(value, null, null);
    }

    /**
     * {@inheritDoc}
     */
    public final Collection /*<LocalizedString> */ validate(Object value, Node node, Field field) {
        Collection errors = VALID;
        errors = typeRestriction.validate(errors, value, node, field);
        if (errors.size() != 0) {
            // no need continuing, restrictions will probably not know how to handle this value any way.
            return errors;
        }
        Object castedValue = castToValidate(value, node, field);
        errors = requiredRestriction.validate(errors, castedValue, node, field);
        if (value == null) return errors; // null is valid, unless required.
        errors = enumerationRestriction.validate(errors, value, node, field);
        errors = uniqueRestriction.validate(errors, castedValue, node, field);
        errors = validateCastedValue(errors, castedValue, node, field);
        return errors;
    }

    protected Collection validateCastedValue(Collection errors, Object castedValue, Node node, Field field) {
        return errors;
    }

    protected StringBuffer toStringBuffer() {
        StringBuffer buf = new StringBuffer();
        buf.append(getName() + " (" + getTypeAsClass() + (defaultValue != null ? ":" + defaultValue : "") + ")");
        buf.append(commitProcessor == null ? "" : " commit: " + commitProcessor + "");
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
        if (enumerationRestriction.getValue() != null) {
            buf.append(" " + enumerationRestriction);
        }
        return buf;

    }
    public final String toString() {
        StringBuffer buf = toStringBuffer();
        if (isFinished()) {
            buf.append(".");
        }
        return buf.toString();
    }


    /**
     * {@inheritDoc}
     * This method is final, override {@link #clone(String)} in stead.
     */
    public final Object clone() {
//        return clone(getName() + "_clone");
        return clone(getName());
    }

    /**
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    public boolean isRequired() {
        return requiredRestriction.isRequired();
    }

    /**
     * {@inheritDoc}
     */
    public DataType.Restriction getRequiredRestriction() {
        return requiredRestriction;
    }

    /**
     * {@inheritDoc}
     */
    public DataType.Restriction setRequired(boolean required) {
        return getRequiredRestriction().setValue(Boolean.valueOf(required));
    }

    /**
     * {@inheritDoc}
     */
    public boolean isUnique() {
        return uniqueRestriction.isUnique();
    }

    /**
     * {@inheritDoc}
     */
    public DataType.Restriction getUniqueRestriction() {
        return uniqueRestriction;
    }

    /**
     * {@inheritDoc}
     */
    public DataType.Restriction setUnique(boolean unique) {
        return getUniqueRestriction().setValue(Boolean.valueOf(unique));
    }


    /**
     * {@inheritDoc}
     */
    public Iterator getEnumerationValues(Locale locale, Cloud cloud, Node node, Field field) {
        Iterator i = new RestrictedEnumerationIterator(locale, cloud, node, field);
        return i.hasNext() ? i : null;
    }

    /**
     * {@inheritDoc}
     */
    public LocalizedEntryListFactory getEnumerationFactory() {
        return enumerationRestriction.getEnumerationFactory();
    }

    /**
     * {@inheritDoc}
     */
    public DataType.Restriction getEnumerationRestriction() {
        return enumerationRestriction;
    }



    public CommitProcessor getCommitProcessor() {
        return commitProcessor == null ? EmptyCommitProcessor.getInstance() : commitProcessor;
    }
    public void setCommitProcessor(CommitProcessor cp) {
        commitProcessor = cp;
    }

    /**
     * {@inheritDoc}
     */
    public Processor getProcessor(int action) {
        Processor processor;
        if (action == PROCESS_GET) {
            processor =  getProcessors == null ? null : getProcessors[0];
        } else {
            processor =  setProcessors == null ? null : setProcessors[0];
        }
        return processor == null ? CopyProcessor.getInstance() : processor;
    }

    /**
     * {@inheritDoc}
     */
    public Processor getProcessor(int action, int processingType) {
        if (processingType == Field.TYPE_UNKNOWN) {
            return getProcessor(action);
        } else {
            Processor processor;
            if (action == PROCESS_GET) {
                processor =  getProcessors == null ? null : getProcessors[processingType];
            } else {
                processor =  setProcessors == null ? null : setProcessors[processingType];
            }
            return processor == null ? getProcessor(action) : processor;
        }
    }

    /**
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    public void setProcessor(int action, Processor processor, int processingType) {
        if (processingType == Field.TYPE_UNKNOWN) {
            processingType = 0;
        }
        if (action == PROCESS_GET) {
            if (getProcessors == null) getProcessors = newProcessorsArray();
            getProcessors[processingType] = processor;
        } else {
            if (setProcessors == null) setProcessors = newProcessorsArray();
            setProcessors[processingType] = processor;
        }
    }


    /**
     * Abstract inner class Restriction. Based on static StaticAbstractRestriction
     */
    protected abstract class AbstractRestriction extends StaticAbstractRestriction {
        protected AbstractRestriction(DataType.Restriction source) {
            super(BasicDataType.this, source);
        }
        protected AbstractRestriction(String name, Serializable value) {
            super(BasicDataType.this, name, value);
        }
    }
    /**
     * A Restriction is represented by these kind of objects.
     * When you override this class, take care of cloning of outer class!
     * This class itself is not cloneable. Cloning is hard when you have inner classes.
     *
     * See <a href="http://www.adtmag.com/java/articleold.asp?id=364">article about inner classes,
     * cloning in java</a>
     */
    protected static abstract class StaticAbstractRestriction implements DataType.Restriction {
        protected final String name;
        protected final BasicDataType parent;
        protected LocalizedString errorDescription;
        protected Serializable value;
        protected boolean fixed = false;
        protected int    enforceStrength = DataType.ENFORCE_ALWAYS;

        protected StaticAbstractRestriction(BasicDataType parent, DataType.Restriction source) {
            this.name = source.getName();
            this.parent = parent;
            inherit(source);
        }

        protected StaticAbstractRestriction(BasicDataType parent, String name, Serializable value) {
            this.name = name;
            this.parent = parent;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public Object getValue() {
            return value;
        }

        public Restriction setValue(Serializable value) {
            log.debug("Setting restriction " + name + " on " + parent);
            parent.edit();
            if (fixed) {
                throw new IllegalStateException("Restriction '" + name + "' is fixed, cannot be changed");
            }
            this.value = value;
            return this;
        }

        public LocalizedString getErrorDescription() {
            if (errorDescription == null) {
                // this is postponsed to first use, because otherwis 'getBaesTypeIdentifier' give correct value only after constructor of parent.
                String key = parent.getBaseTypeIdentifier() + "." + name + ".error";
                errorDescription = new LocalizedString(key);
                errorDescription.setBundle(DATATYPE_BUNDLE);
            }
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
                throw new IllegalStateException("Restriction '" + name + "' is fixed, cannot be changed");
            }
            this.fixed = fixed;
        }



        /**
         * Utility method to add a new error message to the errors collection, based on this
         * Restriction. If this error-collection is unmodifiable (VALID), it is replaced with a new
         * empty one first.
         */
        protected final Collection addError(Collection errors, Object v, Node node, Field field) {
            if (errors == VALID) errors = new ArrayList();
            ReplacingLocalizedString error = new ReplacingLocalizedString(getErrorDescription());
            error.replaceAll("\\$\\{NAME\\}",       error.makeLiteral(getName()));
            error.replaceAll("\\$\\{CONSTRAINT\\}", error.makeLiteral(toString(node, field)));
            error.replaceAll("\\$\\{VALUE\\}",      error.makeLiteral("" + v));
            errors.add(error);
            return errors;
        }

        /**
         * If toString a restriction depends on node, field, then you can override this
         */
        protected String toString(Node node, Field field) {
            return toString();
        }

        /**
         * Whether {@link #validate} must enforce this condition
         */
        protected final boolean enforce(Node node, Field field) {
            switch(enforceStrength) {
            case DataType.ENFORCE_ALWAYS:   return true;
            case DataType.ENFORCE_ONCHANGE: if (node == null || field == null || node.isChanged(field.getName())) return true;
            case DataType.ENFORCE_ONCREATE: if (node == null || node.isNew()) return true;
            case DataType.ENFORCE_NEVER:    return false;
            default:                        return true;
            }
        }
        /**
         * This method is called by {@link BasicDataType#validate(Object, Node, Field)} for each of its conditions.
         */
        protected Collection validate(Collection errors, Object v, Node node, Field field) {
            if ((! enforce(node, field)) ||  valid(v, node, field) || v == null) {
                // no new error to add.
                return errors;
            } else {
                return addError(errors, v, node, field);
            }
        }

        public abstract boolean valid(Object value, Node node, Field field);


        protected void inherit(DataType.Restriction source) {
            value = (Serializable) source.getValue();
            // perhaps this value must be cloned?, but how?? Cloneable has no public methods....
            errorDescription = (LocalizedString) source.getErrorDescription().clone();
            enforceStrength = source.getEnforceStrength();
        }

        public int getEnforceStrength() {
            return enforceStrength;
        }
        public void setEnforceStrength(int e) {
            enforceStrength = e;
        }

        public String toString() {
            return name + ": " +
                (enforceStrength == DataType.ENFORCE_NEVER ? "*" : "") +
                value + ( fixed ? "." : "");
        }

    }

    protected class RequiredRestriction extends AbstractRestriction {
        RequiredRestriction(RequiredRestriction source) {
            super(source);
        }
        RequiredRestriction(boolean b) {
            super("required", Boolean.valueOf(b));
        }
        final boolean isRequired() {
            return value.equals(Boolean.TRUE);
        }
        public boolean valid(Object v, Node node, Field field) {
            if(!isRequired()) return true;
            return v != null || BasicDataType.this.commitProcessor != null;
        }
    }

    protected class UniqueRestriction extends AbstractRestriction {
        UniqueRestriction(UniqueRestriction source) {
            super(source);
        }
        UniqueRestriction(boolean b) {
            super("unique", Boolean.valueOf(b));
        }
        final boolean isUnique() {
            return value.equals(Boolean.TRUE);
        }
        public boolean valid(Object v, Node node, Field field) {
            if (! isUnique()) return true;
            if (node != null && field != null && value != null) {
                // create a query and query for the value
                // XXX This will test for uniquness using the cloud, so you'll miss objects you can't
                // see (and database doesn't know that!)
                NodeQuery query = field.getNodeManager().createQuery();
                Constraint constraint = Queries.createConstraint(query, field.getName(), FieldCompareConstraint.EQUAL, v);
                Queries.addConstraint(query, constraint);
                if (!node.isNew()) {
                    constraint = Queries.createConstraint(query, "number", FieldCompareConstraint.NOT_EQUAL, new Integer(node.getNumber()));
                    Queries.addConstraint(query, constraint);
                }
                log.debug(query);
                return Queries.count(query) == 0;
            } else {
                // TODO needs to work without Node too.
                return true;
            }
        }
    }

    protected class TypeRestriction extends AbstractRestriction {
        TypeRestriction(TypeRestriction source) {
            super(source);
        }
        TypeRestriction() {
            super("type", BasicDataType.this.getClass());
        }
        public boolean valid(Object v, Node node, Field field) {
            try {
                BasicDataType.this.cast(v, node, field);
                return true;
            } catch (Throwable e) {
                return false;
            }
        }
    }

    protected class EnumerationRestriction extends AbstractRestriction {
        EnumerationRestriction(EnumerationRestriction source) {
            super(source);
        }
        EnumerationRestriction(LocalizedEntryListFactory entries) {
            super("enumeration", entries);
        }
        final LocalizedEntryListFactory getEnumerationFactory() {
            if(value == null) {
                value = new LocalizedEntryListFactory();
            }
            return (LocalizedEntryListFactory) value;
        }
        public Collection getEnumeration(Locale locale, Cloud cloud, Node node, Field field) {
            if (value == null) return null;
            LocalizedEntryListFactory ef = (LocalizedEntryListFactory) value;
            if (cloud == null) {
                if (node != null) {
                    cloud = node.getCloud();
                } else if (field != null) {
                    cloud = field.getNodeManager().getCloud();
                }
            }
            if (ef.size(cloud) == 0) return null;
            return ef.get(locale, cloud);
        }

        /**
         * @see BasicDataType#preCast
         */
        protected Object preCast(Object v, Cloud cloud) {
            if (getValue() == null) return v;
            Object res =  ((LocalizedEntryListFactory) value).castKey(v);
            return v != null ? Casting.toType(v.getClass(), cloud, res) : res;
        }

        public boolean valid(Object v, Node node, Field field) {
            Cloud cloud = BasicDataType.this.getCloud(node, field);
            Collection validValues = getEnumeration(null, cloud, node, field);
            if (validValues == null) return true;
            Object candidate = BasicDataType.this.cast(v, cloud, node, field);            
            Iterator i = validValues.iterator();
            while (i.hasNext()) {
                Map.Entry e = (Map.Entry) i.next();
                Object valid = BasicDataType.this.cast(e.getKey(), cloud, node, field);
                if (valid.equals(candidate)) {
                    return true;
                }
            }
            return false;
        }

        protected String toString(Node node, Field field) {
            return getEnumeration(null, null, node, field).toString();
        }

    }


    /**
     * Iterates over the collection provided by the EnumerationRestriction, but skips the values
     * which are invalid because of the other restrictions on this DataType. 
     */
    //Also, it 'preCasts' the * keys to the right type.

    protected class RestrictedEnumerationIterator implements Iterator {
        private final Iterator baseIterator;
        private final Node node;
        private final Field field;
        private Map.Entry next = null;

        RestrictedEnumerationIterator(Locale locale, Cloud cloud, Node node, Field field) {
            Collection col = enumerationRestriction.getEnumeration(locale, cloud, node, field);
            baseIterator =  col != null ? col.iterator() : Collections.EMPTY_LIST.iterator();
            this.node = node;
            this.field = field;
            determineNext();
        }

        protected void determineNext() {
            next = null;
            while (baseIterator.hasNext()) {
                final Map.Entry entry = (Map.Entry) baseIterator.next();
                Object value = entry.getKey();
                Collection validationResult = BasicDataType.this.validate(value, node, field);
                if (validationResult == VALID) {
                    next = entry;
                    /*
                    new Map.Entry() {
                            public Object getKey() {
                                return BasicDataType.this.preCast(entry.getKey(), node, field);
                            }
                            public Object getValue() {
                                return entry.getValue();
                            }
                            public Object setValue(Object v) {
                                return entry.setValue(v);
                            }
                        };
                    */
                    break;
                } else if (log.isDebugEnabled()) {
                    String errors = "";
                    for (Iterator i = validationResult.iterator(); i.hasNext();) {
                        errors += ((LocalizedString)i.next()).get(null);
                    }
                    log.debug("Value " + value + " does not validate : " + errors);
                }
            }
        }

        public boolean hasNext() {
            return next != null;
        }
        public Object next() {
            if (next == null) {
                throw new NoSuchElementException();
            }
            Object n = next;
            determineNext();
            return n;
        }
        public void remove() {
            throw new UnsupportedOperationException("Cannot remove entries from " + getClass());
        }
        public String toString() {
            return "restricted iterator(" + enumerationRestriction + ")";
        }
    }

}
