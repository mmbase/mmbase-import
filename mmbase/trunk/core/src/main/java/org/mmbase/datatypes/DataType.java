/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.datatypes;

import java.util.*;
import java.io.Serializable;

import org.mmbase.bridge.*;
import org.mmbase.datatypes.processors.*;
import org.mmbase.datatypes.handlers.Handler;
import org.mmbase.util.*;

/**
 * A value in MMBase (such as the value of a field, or function parameter) is associated with a
 * 'datatype'.  A DataType is actually an elaborated wrapper around a Class object, but besides
 * this basic type of the value, it also defines restrictions on the values, a default value,
 * Processors, and perhaps other properties (e.g. properties which describe indications for edit
 * tool implementations).
 *
 * There are several extensions of DataType which normally add other kinds of restrictions which are
 * specific for certain classes of values. All implementations of DataType extend from {@link
 * BasicDataType}, but they can sometimes implement different extensions of DataType at the same time
 * ('multiple inheritance').
 *
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @since  MMBase-1.8
 * @version $Id$
 * @param <C> Class this DataType
 */

public interface DataType<C> extends Descriptor, Comparable<DataType<C>>, Serializable {

    /**
     * The XML Namespace to be used for creating datatype XML
     */
    public static final String XMLNS = org.mmbase.datatypes.util.xml.DataTypeReader.NAMESPACE_DATATYPES_1_0;

    // XXXX MM: I think 'action' must be gone; it is silly.
    static final int PROCESS_GET    = 1;
    static final int PROCESS_SET    = 2;

    /**
     * Return value for {@link DataType.Restriction#getEnforceStrength}. This means that the value
     * restriction must be enforced always, and furthermore, that extensions (based on clone) cannot
     * loosen it. For example, the absolute maximum for any datatype backed by a integer is
     * Integer.MAX_VALUE, there is no way you can even store a bigger value in this, so this
     * restriction is 'absolute'.
     */
    static final int ENFORCE_ABSOLUTE  = Integer.MAX_VALUE;

    /**
     * Return value for {@link DataType.Restriction#getEnforceStrength}. This means that a
     * restriction on a value must be enforced always.
     */
    static final int ENFORCE_ALWAYS   = 100000;

    /**
     * Return value for {@link DataType.Restriction#getEnforceStrength}. This means that a
     * restriction on a value must be enforced only if it was changed.
     */
    static final int ENFORCE_ONCHANGE = 10000;

    /**
     * Return value for {@link DataType.Restriction#getEnforceStrength}. This means that ta
     * restriction on a value must be enforced only on creation.
     */
    static final int ENFORCE_ONCREATE = 1000;

    /**
     * Return value for {@link DataType.Restriction#getEnforceStrength}. This means that ta
     * restriction on a value must be enforced only on validation of the value. This means that it
     * has no influence of the validity of the <em>node</em>.
     * @todo No difference made between always/change/create for this. We could also add
     *       ENfORCE_ONVALIDATE_CHANGE, ENFORMCE_ONVALIDATE_CREATE
     * @since MMBase-1.9.1
     */

    static final int ENFORCE_ONVALIDATE = 500;

    /**
     * Return value for {@link DataType.Restriction#getEnforceStrength}. This means that the
     * restriction on a value must be enforced never, so the restriction serves only as UI indication.
     */
    static final int ENFORCE_NEVER    = 0;

    /**
     * Returned by {@link #validate(Object, Node, Field)} if no errors: an empty (nonmodifiable)
     * Collection containing no error messages.
     */
    public static final Collection<LocalizedString> VALID = Collections.emptyList();

    /**
     * Return the DataType from which this one inherited, or <code>null</code>
     * @return inherited DataType
     */
    public DataType<?> getOrigin();

    /**
     * Return an identifier for the basic type (i.e., 'string', 'int', 'datetime') supported by this datatype.
     * @return identifier for the basic type
     */
    public String getBaseTypeIdentifier();

    /**
     * Return the datatype's basic (MMBase) type (i.e., STRING, INTEGER, DATETIME) as definied in the Field interface
     * Note that in some cases (i.e. with older clouds) this may differ from the basic type of the datatype's field,
     * which defines in what format the data is stored.
     * @return identifier of the basic type
     * @see Field#getType
     */
    public int getBaseType();

    /**
     * Returns the type of values that this data type accepts.
     * @return the type as a Class
     */
    public Class<C> getTypeAsClass();

    /**
     * Checks if the passed object is of the correct class (compatible with the type of this data type),
     * and throws an IllegalArgumentException if it doesn't.
     * @param value the value whose type (class) to check
     * @throws IllegalArgumentException if the type is not compatible
     */
    public void checkType(Object value);

    /**
     * Tries to 'cast' an object for use with this parameter. E.g. if value is a String, but this
     * parameter is of type Integer, then the string can be parsed to Integer.
     *
     *
     * @param value The value to be filled in a value with this DataType.
     * @param node  Sometimes a node might be needed.
     * @param field Sometimes a (or 'the') field might be needed.
     * @return casted object of Class of this DataType
     */
    public C cast(Object value, Node node, Field field);

    /**
     * Before actually 'cast' an object to the right type, it may undergo some conversion by the
     * datatype, e.g. enumerations may get resolved (enumerations have the feature that they can
     * e.g. resolve java-constants to their values).
     *
     * This does not guarantee that the value has the 'proper' type, but only that it now can be
     * cast to the right type without further problems. ({@link org.mmbase.util.Casting#toType(Class, Object)} should do).
     *
     * preCast should not change the actual type of value. It is e.g. used in the
     * Node#setStringValue, and the processor may expect a String there.
     * @param value The value to be filled in a value with this DataType.
     * @param node  Sometimes a node might be needed.
     * @param field Sometimes a (or 'the') field might be needed.
     * @param <D>
     * @return converted value to be able to cast to the DataType of the field
     */
    public <D> D preCast(D value, Node node, Field field);
    //public Object preCast(Object value, Node node, Field field);


    /**
     * Sometimes the the representation of the value is a bit different in the database, or has a
     * different type. So when constraining a search on the value, casting is done by this. This may
     * default to {@link cast(Object, Node, Field)}.
     * @since MMBase-1.9.1
     */
    public Object castForSearch(Object value, Node node, Field field);

    /**
     * Returns the default value of this data type.
     * @return the default value
     */
    public C getDefaultValue();

    /**
     * Returns the (locale dependent) default value of this data type,
     * @since MMBase-1.8.6
     */
    public C getDefaultValue(Locale locale, Cloud cloud, Field field);

    /**
     * Set the default value for this DataType
     * @param def default value
     */
    public void setDefaultValue(C def);

    /**
     * Unlock a DataType so it can be changed or altered.
     * @param owner the object to finish datatypes with
     * @return unlocked DataType
     */
    public DataType<C> rewrite(Object owner);

    /**
     * Is datatype locked
     * @return <code>true</code> when datatype is locked
     */
    public boolean isFinished();

    /**
     * Lock a dataType so it can be changed or altered.
     * @param owner the object to finish datatypes with
     */
    public void finish(Object owner);

    /**
     * The maximum enforce strength of all restrictions on this datatype.
     * See {@link DataType#ENFORCE_ALWAYS}, {@link DataType#ENFORCE_ONCHANGE}, {@link DataType#ENFORCE_NEVER}.
     * @return maximum enforce strength
     */
    public int getEnforceStrength();

    /**
     * @see #validate(Object, Node, Field)
     * @return The error message(s) if the value is not compatible. An empty collection if valid.
     * @param value the value to be validated
     */
    public Collection<LocalizedString> validate(C value);

    /**
     * Checks if the passed object obeys the restrictions defined for this type.
     * @param value the value to validate
     * @param node the node for which the datatype is checked. If not <code>null</code>, and the
     *        datatype is determined as unique, than uniquness is checked for this value using the passed field.
     * @param field the field for which the datatype is checked.
     *
     * @return The error message(s) if the value is not compatible. An empty collection ({@link
     * DataType#VALID})if the value is valid.
     */
    public Collection<LocalizedString> validate(C value, Node node, Field field);

    /**
     * Validates a value without knowing its type yet.
     *
     * @since MMBase-1.9.1
     */
    public Collection<LocalizedString> castAndValidate(Object value, Node node, Field field);

    /**
     * Returns whether this field is required (may not be <code>null</code>, or otherwise empty).
     *
     * @return  <code>true</code> if the field is required
     */
    public boolean isRequired();

    /**
     * Returns the 'required' restriction, containing the value, error messages, and fixed status of this attribute.
     * @return the restriction as a {@link DataType.Restriction}
     */
    public DataType.Restriction<Boolean> getRequiredRestriction();

    /**
     * Sets whether the data type requires a value, which means that it may not remain unfilled.
     * @param required <code>true</code> if a value is required
     * @throws IllegalStateException if the datatype was finished (and thus can no longer be changed)
     */
    public void setRequired(boolean required);

    /**
     * Returns whether this field has a unique restriction.
     * Uniqueness is generally achieved through association of the datatype with one or more sets of fields.
     * This is notably different from other datatype properties.
     *
     * Note that the MMBase core does not generally enforce uniqueness, but the storage layer might.
     *
     * @return  <code>true</code> if the field is unique
     */
    public boolean isUnique();

    /**
     * Returns the 'unique' restriction, containing the value, error messages, and fixed status of this attribute.
     * @return the restriction as a {@link DataType.Restriction}
     */
    public DataType.Restriction<Boolean> getUniqueRestriction();

    /**
     * Sets whether the data type requires a value.
     * @param unique <code>true</code> if a value is unique
     * @throws IllegalStateException if the datatype was finished (and thus can no longer be changed)
     */
    public void setUnique(boolean unique);

    /**
     * Returns an iterator over all possible values for this datatype, as {@link java.util.Map.Entry}s, or
     * <code>null</code> if no enumeration restrictions apply. Every Map entry contains as key the
     * 'value' for this datatype and as value it contains the description for this value in the
     * given locale.
     *
     * This Iterator skips all entries which are impossible because of other restrictions on this datatype.
     *
     * @param locale for which to produce
     * @param cloud  Possibly the possible values depend on a cloud (security)
     * @param node   Possibly the possible values depend on an actual node (this may be, and in the default implementation is, ignored)
     * @param field   Possibly the possible values depend on an actual field (this may be, and in the default implementation is, ignored)
     * @return iterator over all possible values for this datatype
     *
     */
    public Iterator<Map.Entry<C, String>> getEnumerationValues(Locale locale, Cloud cloud, Node node, Field field);

    /**
     * Returns a (gui) value from a list of restricted enumerated values, or
     * <code>null</code> if no enumeration restrictions apply or the value cannot be found.
     *
     * @param locale for which to produce
     * @param cloud  Possibly the possible values depend on a cloud (security)
     * @param node   Possibly the possible values depend on an actual node (this may be, and in the default implementation is, ignored)
     * @param field  Possibly the possible values depend on an actual field (this may be, and in the default implementation is, ignored)
     * @param key    the key for which to look up the (gui) value
     * @return a (gui) value from a list of restricted enumerated values
     */
    public String getEnumerationValue(Locale locale, Cloud cloud, Node node, Field field, Object key);

    /**
     * @return the LocalizedEntryListFactory which will be used to produce the result of {@link
     * #getEnumerationValues}. Never <code>null</code>. This can be used to add more possible values.
     */
    public LocalizedEntryListFactory<C> getEnumerationFactory();

    /**
     * The enumeration for this datatype as a {@link Restriction}.
     * @return enumeration for this datatype
     */
    public DataType.Restriction<LocalizedEntryListFactory<C>> getEnumerationRestriction();

    /**
     * Return the Commit processor of this datatype
     * @return Commit processor
     */
    public CommitProcessor getCommitProcessor();

    /**
     * Set the Commit processor of this datatype
     * @param cp Commit processor
     */
    public void setCommitProcessor(CommitProcessor cp);


    /**
     * Return the Delete processor of this datatype
     * @return A commitprocessor that will be called if the Node is deleted.
     * @since MMBase-1.9.1
     */
    public CommitProcessor getDeleteProcessor();

    /**
     * Returns the default processor for this action
     * @param action either {@link #PROCESS_GET}, or {@link #PROCESS_SET}
     * XXX What exactly would be against getGetProcesor(), getSetProcessor() ?
     * @return the default processor for this action
     */
    public Processor getProcessor(int action);

    /**
     * Returns the processor for this action and processing type
     * @param action either {@link #PROCESS_GET}, or {@link #PROCESS_SET}
     * @param processingType the MMBase type defining the type of value to process
     * @return the processor for this action and processing type
     */
    public Processor getProcessor(int action, int processingType);

    /**
     * Sets the processor for this action
     * @param action either {@link #PROCESS_GET}, or {@link #PROCESS_SET}
     * @param processor the processor for this action
     */
    public void setProcessor(int action, Processor processor);

    /**
     * Sets the processor for this action
     * @param action either {@link #PROCESS_GET}, or {@link #PROCESS_SET}
     * @param processor the processor for this action and processing type
     * @param processingType the MMBase type defining the type of value to process
     */
    public void setProcessor(int action, Processor processor, int processingType);

    /**
     * Returns a cloned instance of this datatype, inheriting all validation rules.
     * Unlike the original datatype though, the cloned copy is declared unfinished even if the original
     * was finished. This means that the cloned datatype can be changed.
     * @return cloned instance
     */
    public DataType<C> clone();

    /**
     * Returns a cloned instance of this datatype, inheriting all validation rules.
     * Similar to calling clone(), but changes the data type name if one is provided.
     * @param name the new name of the copied datatype (can be <code>null</code>, in which case the name is not changed).
     * @return cloned DataType
     */
    public DataType<C> clone(String name);


    /**
     * Returns a DOM element describing this DataType.
     * @return a DOM element describing this DataType.
     * @todo EXPERIMENTAL.
     */
    public org.w3c.dom.Element toXml();

    /**
     * Fills this datatype in another XML (for example in the xml of {@link #getOrigin}, to make one
     * XML, fully describing the DataType).  The implementation of this method is
     * <em>unfinished</em>!
     * @todo EXPERIMENTAL
     * @param element a 'datatype' element.
     */
    public void toXml(org.w3c.dom.Element element);

    /**
     * Returns a handler for given mimetype for this DataType. The handler can be used to produce UI
     * for values of this datatype.
     * @todo EXPERIMENTAL
     * @since MMBase-1.9.1
     */
    public Handler<?> getHandler(String mimeType);
    /**
     * @since MMBase-1.9.1
     */
    public Map<String, Handler<?>> getHandlers();

    /**
     * @since MMBase-1.9
     */
    //public Collection<Restriction<?>> getRestrictions();


    /**
     * @since MMBase-1.9.1
     */
    public String[] getStyleClasses();


    /**
     * A restriction controls (one aspect of) the acceptable values of a DataType. A DataType
     * generally has several restrictions.
     * @param <D> Type of Value describing the restriction
     */
    public interface Restriction<D extends Serializable> extends Serializable {

        /**
         * @return Name of datatype
         */
        public String getName();

        /**
         * A Value describing the restriction, so depending on the semantics of this restriction, it
         * can have virtually every type (as long as it is Serializable)
         * @return A Value describing the restriction
         */
        public D getValue();

        /**
         * Set the Value describing the restriction
         * @param value The instanc for the Value
         */
        public void setValue(D value);

        /**
         * If the restriction does not hold, the following error description can be used. On default
         * these descriptions are searched in a resource bundle based on the name of this
         * restriction.
         * @return error description
         */
        public LocalizedString getErrorDescription();

        /**
         * Set error description for this restriction
         * @param errorDescription description of error
         */
        public void setErrorDescription(LocalizedString errorDescription);

        /**
         * This function should contain the actual logic of the restriction. This does not consider
         * the 'enforceStrength' (that is only used in the containing DataType implementation).
         *
         * @param value The value to check the restriction for
         * @param node  Some constrainst may need the Node.
         * @param field Some constrainst may need the Field.
         * @return Whether the supplied value is a valid value for this restriction.
         */
        public boolean valid(Object value, Node node, Field field);

        /**
         * If a restriction is 'fixed', the value and error-description cannot be changed any more.
         * @param fixed
         */
        public void setFixed(boolean fixed);

        /**
         * See {@link DataType#ENFORCE_ALWAYS}, {@link DataType#ENFORCE_ONCHANGE}, {@link DataType#ENFORCE_NEVER}.
         * @return enforce strength
         */
        public int getEnforceStrength();

        /**
         * Set enforce strength
         * @param v value of {@link DataType#ENFORCE_ALWAYS}, {@link DataType#ENFORCE_ONCHANGE}, {@link DataType#ENFORCE_NEVER}.
         */
        public void setEnforceStrength(int v);



    }

}
