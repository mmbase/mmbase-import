/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.datatypes;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import org.mmbase.datatypes.handlers.Handler;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.bridge.util.AbstractDescriptor;
import org.mmbase.bridge.Fields;
import org.mmbase.datatypes.processors.*;
import org.mmbase.security.Rank;
import org.mmbase.storage.search.Constraint;
import org.mmbase.storage.search.FieldCompareConstraint;
import org.mmbase.util.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.xml.DocumentReader;
import org.w3c.dom.Element;


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
 * @version $Id$
 */

public class BasicDataType<C> extends AbstractDescriptor implements DataType<C>, Comparable<DataType<C>>, Descriptor {
    /**
     * The bundle used by datatype to determine default prompts for error messages when a
     * validation fails.
     */
    public static final String DATATYPE_BUNDLE = "org.mmbase.datatypes.resources.datatypes";
    private static final Logger log = Logging.getLoggerInstance(BasicDataType.class);

    private Collection<Restriction<?>> restrictions = new ArrayList<Restriction<?>>();
    private Collection<Restriction<?>> unmodifiableRestrictions = Collections.unmodifiableCollection(restrictions);

    protected RequiredRestriction requiredRestriction        = new RequiredRestriction(false);
    protected UniqueRestriction   uniqueRestriction          = new UniqueRestriction(false);
    protected TypeRestriction     typeRestriction            = new TypeRestriction();
    protected EnumerationRestriction enumerationRestriction  = new EnumerationRestriction((LocalizedEntryListFactory<C>) null);

    /**
     * The datatype from which this datatype originally inherited it's properties.
     */
    protected BasicDataType<?> origin = null;

    private Object owner;
    private Class<C> classType;
    protected Object defaultValue;

    private CommitProcessor commitProcessor = EmptyCommitProcessor.getInstance();
    private CommitProcessor deleteProcessor = EmptyCommitProcessor.getInstance();
    private Processor[]     getProcessors;
    private Processor[]     setProcessors;

    private Processor defaultProcessor;

    private Map<String, Handler<?>> handlers = new ConcurrentHashMap<String, Handler<?>>();

    private Element xml = null;

    private String[] styleClasses;

    /**
     * Create a data type object of unspecified class type
     * @param name the name of the data types
     */
    @SuppressWarnings("unchecked")
    public BasicDataType(String name) {
        this(name, (Class<C>) Object.class);
    }

    /**
     * Create a data type object
     * @param name the name of the data type
     * @param classType the class of the data type's possible value
     */
    protected BasicDataType(String name, Class<C> classType) {
        super(name);
        this.classType = classType;
        owner = null;
    }

    private static final long serialVersionUID = 1L; // increase this if object serialization changes (which we shouldn't do!)

    // implementation of serializable
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeUTF(key);
        out.writeObject(description);
        out.writeObject(guiName);
        out.writeObject(requiredRestriction);
        out.writeObject(uniqueRestriction);
        out.writeObject(enumerationRestriction.getEnumerationFactory());
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
        out.writeObject(handlers);
        out.writeObject(restrictions);
        out.writeObject(defaultProcessor);
    }
    // implementation of serializable
    @SuppressWarnings("unchecked")
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        key                    = in.readUTF();
        description            = (LocalizedString) in.readObject();
        guiName                = (LocalizedString) in.readObject();
        requiredRestriction    = (RequiredRestriction) in.readObject();
        uniqueRestriction      = (UniqueRestriction) in.readObject();
        enumerationRestriction = new EnumerationRestriction((LocalizedEntryListFactory<C>) in.readObject());
        typeRestriction        = new TypeRestriction(); // its always the same, so no need actually persisting it.
        owner                  = in.readObject();
        try {
            classType          =  (Class<C>) in.readObject();
        } catch (Throwable t) {
            // if some unknown class, simply fall back
            classType         = (Class<C>) Object.class;
        }
        defaultValue          = (C) in.readObject();
        commitProcessor       = (CommitProcessor) in.readObject();
        getProcessors         = (Processor[]) in.readObject();
        setProcessors         = (Processor[]) in.readObject();
        handlers              = (Map<String, Handler<?>>) in.readObject();
        restrictions          = (Collection<Restriction<?>>) in.readObject();
        unmodifiableRestrictions = Collections.unmodifiableCollection(restrictions);
        try {
            defaultProcessor      = (Processor) in.readObject();
        } catch (OptionalDataException ode) {
            log.service(ode.getClass() + " " + ode.getMessage() +  " (remote version probably not supporting defaultProcessor yet (< 1.9.3))");
        }
    }

    @Override
    public String getBaseTypeIdentifier() {
        return Fields.getTypeDescription(getBaseType()).toLowerCase();
    }

    @Override
    public int getBaseType() {
        return Fields.classToType(classType);
    }

    /**
     * Calls both {@link #inheritProperties} and {@link #inheritRestrictions}.
     * @param origin inherit properties and restrictions from this DataType
     */
    public final void inherit(BasicDataType<C> origin) {
        edit();
        inheritProperties(origin);
        inheritRestrictions(origin);
        handlers.putAll(origin.handlers);
    }

    /**
     * Properties are members of the datatype that can easily be copied/clones.
     */
    protected void inheritProperties(BasicDataType<C> origin) {
        this.origin     = origin;

        defaultValue    = origin.getDefaultValue();

        commitProcessor = (CommitProcessor) ( origin.commitProcessor instanceof PublicCloneable ? ((PublicCloneable) origin.commitProcessor).clone()  : origin.commitProcessor);
        deleteProcessor = (CommitProcessor) ( origin.deleteProcessor instanceof PublicCloneable ? ((PublicCloneable) origin.deleteProcessor).clone()  : origin.deleteProcessor);
        if (origin.getProcessors == null) {
            getProcessors = null;
        } else {
            getProcessors = origin.getProcessors.clone();
        }
        if (origin.setProcessors == null) {
            setProcessors = null;
        } else {
            setProcessors = origin.setProcessors.clone();
        }
        if (origin.defaultProcessor == null) {
            defaultProcessor = null;
        } else {
            defaultProcessor = origin.defaultProcessor;
        }

        styleClasses = origin.styleClasses;

    }

    /**
     * If a datatype is cloned, the restrictions of it (normally implemented as inner classes), must be reinstantiated.
     */
    protected void cloneRestrictions(BasicDataType<C> origin) {
        enumerationRestriction = new EnumerationRestriction(origin.enumerationRestriction);
        requiredRestriction    = new RequiredRestriction(origin.requiredRestriction);
        uniqueRestriction      = new UniqueRestriction(origin.uniqueRestriction);
    }

    /**
     * If a datatype inherits from another datatype all its restrictions inherit too.
     */
    protected void inheritRestrictions(BasicDataType<C> origin) {
        if (! origin.getEnumerationFactory().isEmpty()) {
            enumerationRestriction.inherit(origin.enumerationRestriction);
            if (enumerationRestriction.value != null) {
                LocalizedEntryListFactory<C> fact = enumerationRestriction.getEnumerationFactory();
                if (! origin.getTypeAsClass().equals(getTypeAsClass())) {
                    // Reevaluate XML configuration, because it was done with a 'wrong' suggestion for the wrapper class.
                    Element elm = fact.toXml();
                    if (elm == null) {
                        log.warn("Did not get XML from Factory " + fact);
                    } else {
                        // need to clone the actual factory,
                        // since it will otherwise change the original restrictions.
                        fact = new LocalizedEntryListFactory<C>();
                        fact.fillFromXml(elm, getTypeAsClass());
                        enumerationRestriction.setValue(fact);
                    }
                }
            }
        }

        requiredRestriction.inherit(origin.requiredRestriction);
        uniqueRestriction.inherit(origin.uniqueRestriction);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BasicDataType<?> getOrigin() {
        return origin;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<C> getTypeAsClass() {
        return classType;
    }

   /**
     * Checks if the passed object is of the correct class (compatible with the type of this DataType),
     * and throws an IllegalArgumentException if it doesn't.
     * @param value teh value whose type (class) to check
     * @throws IllegalArgumentException if the type is not compatible
     */
    protected boolean isCorrectType(Object value) {
        return (value == null && ! isRequired()) || Casting.isType(classType, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkType(Object value) {
        if (!isCorrectType(value)) {
            // customize this?
            throw new IllegalArgumentException("DataType of '" + Casting.toString(value) + "' for '" + getName() + "' must be of type " + classType + " (but is " + (value == null ? value : value.getClass()) + ")");
        }
    }


    /**
     * {@inheritDoc}
     *
     * Tries to determin  cloud by node and field if possible and wraps {@link #preCast(Object, Cloud, Node, Field)}.
     */
    @Override
    public final <D> D preCast(D value, Node node, Field field) {
        //public final Object preCast(Object value, Node node, Field field) {
        try {
            return preCast(value, getCloud(node, field), node, field);
        } catch (Exception e) {
            return value;
        }
    }

    /**
     * This method is as yet unused, but can be anticipated
     */
    //public final <D> D preCast(D value, Cloud cloud) {
    public final Object preCast(Object value, Cloud cloud) {
        return preCast(value, cloud, null, null);
    }

    /**
     * This method implements 'precasting', which can be seen as a kind of datatype specific
     * casting.  It should anticipate that every argument can be <code>null</code>. It should not
     * change the actual type of the value.
     */
    protected <D> D preCast(D value, Cloud cloud, Node node, Field field) {
        if (value == null) return null;
        D preCast =  enumerationRestriction.preCast(value, cloud);
        //System.out.println("Enumeration casted " + value + " to " + preCast);
        return preCast;
    }


    /**
     * {@inheritDoc}
     *
     * No need to override this. It is garantueed by javadoc that cast should work out of preCast
     * using Casting.toType. So that is what this final implementation is doing.
     *
     * Override {@link #cast(Object, Cloud, Node, Field)}
     */
    @Override
    public final C cast(Object value, final Node node, final Field field) {
        try {
            return castOrException(value, node, field);
        } catch (CastException ce) {
            log.service(ce.getMessage(), ce);
            Cloud cloud = getCloud(getCloud(node, field));
            return Casting.toType(classType, cloud, preCast(value, cloud, node, field));
        }
    }

    /**
     * @since MMBase-2.0
     */
    @Override
    public final C castOrException(Object value, final Node node, final Field field) throws CastException {
        if (origin != null && (! origin.getClass().isAssignableFrom(getClass()))) {
            // if inherited from incompatible type, then first try to cast in the way of origin.
            // e.g. if origin is Date, but actual type is integer, then casting of 'today' works now.
            value = origin.cast(value, node, field);
        }
        if (value == null) {
            return null;
        }
        Cloud cloud = getCloud(getCloud(node, field));
        return cast(value, cloud, node, field);
    }

    /**
     * Utility to avoid repetitive calling of getCloud
     */
    protected C cast(Object value, Cloud cloud, Node node, Field field) throws CastException {
        Object preCast = preCast(value, cloud, node, field);
        if (preCast == null) return null;
        C cast = Casting.toType(classType, cloud, preCast);
        return cast;
    }

    protected final Cloud getCloud(Node node, Field field) {
        if (node != null) {
            log.trace("Using cloud of node");
            return node.getCloud();
        }
        if (field != null) {
            try {
                return field.getNodeManager().getCloud();
            } catch (UnsupportedOperationException uoe) {
                log.debug(uoe.getMessage());
                // Corefield does not support getNodeManager
            }
        }
        Cloud cloud = org.mmbase.bridge.util.CloudThreadLocal.currentCloud();
        if (cloud != null) {
            return cloud;
        }
        try {
            return ContextProvider.getDefaultCloudContext().getCloud("mmbase", "class", null);
        } catch (NotFoundException nfe) {
            return null;
        }
    }

    private static Cloud classCloud = null;
    /**
     * Returns a cloud object if argument is <code>null</code>. Otherwise the argument.
     * @since MMBase-1.8.6
     */
    protected Cloud getCloud(Cloud cloud) {
        if (cloud == null) {
            log.trace("No cloud found");
            cloud = org.mmbase.bridge.util.CloudThreadLocal.currentCloud();
        }
        if (cloud == null) {
            try {
                CloudContext context = ContextProvider.getDefaultCloudContext();
                if (! context.isUp()) {
                    return null;
                }
                // class security can be a bit expensive, and this method can in certain cases be called very often.
                if (classCloud == null || ! classCloud.getUser().isValid()) {
                    classCloud = context.getCloud("mmbase", "class", null);
                }
                cloud  = classCloud;
            } catch (Throwable e) {
                log.warn(e.getMessage(), e);
                return null;
            }
        }
        return cloud;
    }

    /**
     * Before validating the value, the value will be 'cast', on default this will be to the
     * 'correct' type, but it can be a more generic type sometimes. E.g. for numbers this wil simply
     * cast to Number.
     */
    protected Object castToValidate(Object value, Node node, Field field) throws CastException {
        return cast(value, getCloud(getCloud(node, field)), node, field);
    }

    /**
     * If the value must be shown, e.g. in error message, it passed through this method.
     * @since MMBase-1.9.1
     */
    protected String castToPresent(Object value, Node node, Field field) {
        return Casting.toString(value);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public final C getDefaultValue() {
        return getDefaultValue(null, null, null);
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public C getDefaultValue(Locale locale, Cloud cloud, Field field) {
        C res;
        if (defaultValue != null) {
            res =  cast(defaultValue, null, null);
            if (res == null) {
                try {
                    res =  cast(defaultValue, getCloud(cloud), null, null);
                } catch (CastException ce) {
                    log.error(ce);
                    res = Casting.toType(classType, cloud, preCast(defaultValue, cloud, null, field));
                }
            }
        } else {
            res = null;
        }
        if (defaultProcessor != null) {
            log.debug("Found " + defaultProcessor);
            res = Casting.toType(classType, cloud, defaultProcessor.process(null, field, res));
        } else {
            //log.debug("No default processor found for " + this);
        }
        return res;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setDefaultValue(Object def) {
        edit();
        defaultValue = def;
    }


    protected Element getElement(Element parent, String name, String path) {
        return getElement(parent, name, name, path);
    }
    protected Element getElement(Element parent, String pattern, String name, String path) {
        java.util.regex.Pattern p = java.util.regex.Pattern.compile("\\A" + pattern + "\\z");
        org.w3c.dom.NodeList nl  = parent.getChildNodes();
        Element el = null;
        for (int i = 0; i < nl.getLength(); i++) {
            org.w3c.dom.Node child = nl.item(i);
            if (child instanceof Element) {
                if (p.matcher(child.getLocalName()).matches()) {
                    el = (Element) child;
                    break;
                }
            }
        }
        if (el == null) {
            el = parent.getOwnerDocument().createElementNS(XMLNS, name);
            DocumentReader.appendChild(parent, el, path);
        } else if (! el.getLocalName().equals(name)) {
            Element newChild = parent.getOwnerDocument().createElementNS(XMLNS, name);
            parent.replaceChild(newChild, el);
            el = newChild;
        }
        return el;
    }

    protected String getEnforceString(int enforce) {
        switch(enforce) {
        case DataType.ENFORCE_ABSOLUTE: return "absolute";
        case DataType.ENFORCE_ALWAYS:   return "always";
        case DataType.ENFORCE_ONCHANGE: return "onchange";
        case DataType.ENFORCE_ONCREATE: return "oncreate";
        case DataType.ENFORCE_ONVALIDATE: return "onvalidate";
        case DataType.ENFORCE_NEVER:    return "never";
        default:                        return "???";
        }
    }

    protected  Element addRestriction(Element parent,  String name, String path, Restriction<?> restriction) {
        return addRestriction(parent, name, name, path, restriction);
    }
    protected  Element addRestriction(Element parent, String pattern, String name, String path, Restriction<?> restriction) {
        Element el = addErrorDescription(getElement(parent, pattern, name,   path), restriction);
        xmlValue(el, restriction.getValue());
        el.setAttribute("enforce", getEnforceString(restriction.getEnforceStrength()));
        return el;
    }


    protected Element addErrorDescription(Element el, Restriction<?> r)  {
        r.getErrorDescription().toXml("description", DataType.XMLNS, el, "");
        return el;
    }


    @Override
    public boolean isFinished() {
        return owner != null;
    }

    /**
     * @see BasicDataType#finish()
     */
    public void finish() {
        finish(new Object());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void finish(Object owner) {
        if (! isFinished()) {
            handlers = Collections.unmodifiableMap(handlers);
            description  = description.getReadonlyLocalizedString();
            guiName      = guiName.getReadonlyLocalizedString();
        }
        this.owner = owner;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataType<C> rewrite(Object owner) {
        if (this.owner != null) {
            if (this.owner != owner) {
                throw new IllegalArgumentException("Cannot rewrite this datatype - specified owner is not correct");
            }
            handlers     = new ConcurrentHashMap<String, Handler<?>>(handlers);
            guiName     = guiName.clone();
            description = description.clone();
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
    @Override
    public final Collection<LocalizedString> validate(C value) {
        return validate(value, null, null);
    }


    @Override
    public final Collection<LocalizedString>  validate(final C value, final Node node, final Field field) {
        return validate(value, node, field, true);
    }
    /**
     * {@inheritDoc}
     */
    private Collection<LocalizedString> validate(final Object value, final Node node, final Field field, boolean testEnum) {
        if (log.isDebugEnabled()) {
            log.debug("Validating " + value);
        }
        Collection<LocalizedString> errors = VALID;
        Object castValue;
        try {
            castValue = castToValidate(value, node, field);
            errors = typeRestriction.validate(errors, castValue, node, field);
        } catch (CastException ce) {
            log.debug(ce);
            errors = typeRestriction.addError(errors, value, node, field);
            castValue = value;
        } catch (IllegalArgumentException iae) {
            log.debug(iae);
            errors = typeRestriction.addError(errors, value, node, field);
            castValue = value;

        }
        if (log.isDebugEnabled()) {
            log.debug("Validating cast value " + castValue);
        }
        //System.out.println("" + value + " -> " + castValue + " (" + errors);

        if (errors.size() > 0) {
            log.debug("Invalid");
            // no need continuing, restrictions will probably not know how to handle this value any way.
            return errors;
        }

        errors = validateRequired(errors, castValue, value, node, field);

        errors = validateCastValueOrNull(errors, castValue, value, node, field);

        if (castValue == null) {
            log.debug("cast value is null");
            return errors; // null is valid, unless required.
        }
        if (testEnum) {
            errors = enumerationRestriction.validate(errors, castValue, node, field);
        }
        errors = uniqueRestriction.validate(errors, castValue, node, field);
        errors = validateCastValue(errors, castValue, value, node, field);
        return errors;
    }

    @Override
    public final Collection<LocalizedString> castAndValidate(final Object value, final Node node, final Field field) {
        return validate(value, node, field, true);
    }

    @Override
    public int getEnforceStrength() {
        int enforceStrength = Math.max(typeRestriction.getEnforceStrength(), requiredRestriction.getEnforceStrength());
        enforceStrength = Math.max(enforceStrength, enumerationRestriction.getEnforceStrength());
        return Math.max(enforceStrength, uniqueRestriction.getEnforceStrength());
    }

    protected Collection<LocalizedString> validateCastValue(Collection<LocalizedString> errors, Object castValue, Object value, Node  node, Field field) {
        return errors;
    }
    /**
     * @since MMBase-1.8.4
     */
    protected Collection<LocalizedString> validateCastValueOrNull(Collection<LocalizedString> errors, Object castValue, Object value, Node  node, Field field) {
        return errors;
    }
    /**
     * @since MMBase-1.9.1
     */
    protected Collection<LocalizedString> validateRequired(Collection<LocalizedString> errors, Object castValue, Object value, Node  node, Field field) {
        return requiredRestriction.validate(errors, value, node, field);
    }

    /**
     * @since MMBase-1.9.1
     */
    @Override
    public Object castForSearch(final Object value, final Node node, final Field field) {
        return cast(value, node, field);
    }

    protected StringBuilder toStringBuilder() {
        StringBuilder buf = new StringBuilder();
        buf.append(getName()).append(" (").append(getTypeAsClass()).append(defaultValue != null ? ":" + defaultValue : "").append(")");
        buf.append(commitProcessor == null || EmptyCommitProcessor.getInstance() == commitProcessor ? "" : " commit: " + commitProcessor + "");
        buf.append(deleteProcessor == null || EmptyCommitProcessor.getInstance() == deleteProcessor ? "" : " delete: " + deleteProcessor + "");
        buf.append(defaultProcessor == null || CopyProcessor.getInstance() == defaultProcessor ? "" : " default: " + defaultProcessor + "");
        if (getProcessors != null) {
            for (int i = 0; i < Fields.TYPE_MAXVALUE; i++) {
                buf.append(getProcessors[i] == null ? "" : ("; get [" + Fields.typeToClass(i) + "]:" + getProcessors[i] + " "));
            }
        }
        if (setProcessors != null) {
            for (int i = 0; i < Fields.TYPE_MAXVALUE; i++) {
                buf.append(setProcessors[i] == null ? "" : ("; set [" + Fields.typeToClass(i) + "]:" + setProcessors[i] + " "));
            }
        }
        if (isRequired()) {
            buf.append("  required");
        }
        if (isUnique()) {
            buf.append("  unique");
        }
        if (enumerationRestriction.getValue() != null && ! enumerationRestriction.getEnumerationFactory().isEmpty()) {
            try {
                buf.append(" ").append(enumerationRestriction);
            } catch (Throwable t) {
                // never mind
            }
        }
        return buf;

    }
    @Override
    public final String toString() {
        StringBuilder buf = toStringBuilder();
        if (isFinished()) {
            buf.append(".");
        }
        return buf.toString();
    }


    /**
     * {@inheritDoc}
     *
     * This method is final, override {@link #clone(String)} in stead.
     */
    @Override
    public final BasicDataType<C> clone() {
        return clone(null);
    }

    /**
     * {@inheritDoc}
     *
     * Besides super.clone, it calls {@link #inheritProperties(BasicDataType)} and {@link
     * #cloneRestrictions(BasicDataType)}. A clone is not finished. See {@link #isFinished()}.
     */
    @Override
    public BasicDataType<C> clone(String name) {
        @SuppressWarnings("unchecked")
        BasicDataType<C> clone = (BasicDataType<C>) super.clone(name);
        clone.restrictions.clear();
        // reset owner if it was set, so this datatype can be changed
        clone.rewrite(clone.owner);
        // properly inherit from this datatype (this also clones properties and processor arrays)
        clone.inheritProperties(this);
        clone.cloneRestrictions(this);
        if (log.isTraceEnabled()) {
            log.trace("Cloned " + this + " -> " + clone);
        }
        return clone;
    }

    @Override
    public Element toXml() {
         if (xml == null) {
            xml = DocumentReader.getDocumentBuilder().newDocument().createElementNS(XMLNS, "datatype");
            xml.getOwnerDocument().appendChild(xml);
            toXml(xml);
        }
        return xml;
    }

    @SuppressWarnings("fallthrough")
    public void setXml(Element element) {
        if (element == null) {
            xml = null;
            return;
        }
        xml = DocumentReader.toDocument(element).getDocumentElement();
        if (origin != null) {
            xml.setAttribute("base", origin.getName());
        }
        // remove 'specialization' childs (they don't say anything about this datatype itself)
        org.w3c.dom.Node child = xml.getFirstChild();
        while(child != null) {
            org.w3c.dom.Node next = child.getNextSibling();
            switch(child.getNodeType()) {
            case org.w3c.dom.Node.ELEMENT_NODE:
                if (child.getLocalName().equals("specialization")
                    ||child.getLocalName().equals("datatype")
                    ) {
                    // fall through and remove
                } else {
                    break;
                }
            case org.w3c.dom.Node.TEXT_NODE:
                xml.removeChild(child);
            }
            child = next;

        }
    }
    protected void xmlValue(Element el, Object value) {
        el.setAttribute("value", Casting.toString(value));
    }


    /**

     */
    @Override
    public void toXml(Element parent) {
        parent.setAttribute("id", getName());

        guiName.toXml("name", XMLNS, parent, "name");
        description.toXml("description", XMLNS, parent, "name,description");

        {
            Element classElement = getElement(parent, "class",    "name,description,class");
            classElement.setAttribute("name", getClass().getName());

            StringBuilder extend = new StringBuilder();
            Class<?> sup = getClass().getSuperclass();
            while(DataType.class.isAssignableFrom(sup)) {
                if (extend.length() > 0) extend.append(',');
                extend.append(sup.getName());
                sup = sup.getSuperclass();
            }
            for (Class<?> c : getClass().getInterfaces()) {
                if (DataType.class.isAssignableFrom(c)) {
                    if (extend.length() > 0) extend.append(',');
                    extend.append(c.getName());
                }
            }
            classElement.setAttribute("extends", extend.toString());
        }



        xmlValue(getElement(parent, "default",  "name,description,class,property,default"), defaultValue);

        addRestriction(parent, "unique",   "name,description,class,property,default,unique", uniqueRestriction);
        addRestriction(parent, "required",   "name.description,class,property,default,unique,required", requiredRestriction);
        {
            Element factoryXml  = getEnumerationFactory().toXml();
            if (factoryXml != null) {
                Element enumElement = getElement(parent, "enumeration", "name,description,class,property,default,unique,required,(maxInclusive|maxExclusive),precision,scale,minLength,maxLength,length,pattern,password,enumeration");
                if (enumElement.getChildNodes().getLength() == 0) {
                    Element imported = (Element) enumElement.getOwnerDocument().importNode(factoryXml, true);
                    org.w3c.dom.NodeList childs = imported.getChildNodes();
                    for (int i = 0; i < childs.getLength(); i++) {
                        enumElement.appendChild(childs.item(i));
                    }
                }
            }
        }

        /**
           End up in the wrong place, and not needed for javascript, so commented out for the moment.

        if (getCommitProcessor() != EmptyCommitProcessor.getInstance()) {
            org.w3c.dom.NodeList nl  = parent.getElementsByTagName("commitprocessor");
            Element element;
            if (nl.getLength() == 0) {
                element = parent.getOwnerDocument().createElementNS(XMLNS, "commitprocessor");
                Element clazz = parent.getOwnerDocument().createElementNS(XMLNS, "class");
                clazz.setAttribute("name", getCommitProcessor().getClass().getName());
                DocumentReader.appendChild(parent, element, "description,class,property");
                element.appendChild(clazz);
            } else {
                element = (Element) nl.item(0);
            }

            //element.setAttribute("value", Casting.toString(defaultValue));
        }
        */

    }

    @Override
    public Handler<?> getHandler(String mimeType) {
        return handlers.get(mimeType);
    }

    @Override
    public Map<String, Handler<?>> getHandlers() {
        return handlers;
    }
    public Collection<Restriction<?>> getRestrictions() {
        return unmodifiableRestrictions;
    }
    @Override
    public int compareTo(DataType<C> a) {
        int compared = getName().compareTo(a.getName());
        if (compared == 0) compared = getTypeAsClass().getName().compareTo(a.getTypeAsClass().getName());
        return compared;
    }

    /**
     * Whether data type equals to other data type. Only key and type are consided. DefaultValue and
     * required properties are only 'utilities'.
     * @return true if o is a DataType of which key and type equal to this' key and type.
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof DataType<?>) {
            DataType<?> a = (DataType<?>) o;
            return getName().equals(a.getName()) && getTypeAsClass().equals(a.getTypeAsClass());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getName().hashCode() * 13 + getTypeAsClass().hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRequired() {
        return requiredRestriction.isRequired();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataType.Restriction<Boolean> getRequiredRestriction() {
        return requiredRestriction;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRequired(boolean required) {
        getRequiredRestriction().setValue(Boolean.valueOf(required));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUnique() {
        return uniqueRestriction.isUnique();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataType.Restriction<Boolean> getUniqueRestriction() {
        return uniqueRestriction;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUnique(boolean unique) {
        getUniqueRestriction().setValue(Boolean.valueOf(unique));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEnumerationValue(Locale locale, Cloud cloud, Node node, Field field, Object key) {
        String value = null;
        if (key != null && ! getEnumerationFactory().isEmpty()) {
            // cast to the appropriate datatype value.
            // Note that for now it is assumed that the keys are of the same type.
            // I'm not 100% sure that this is always the case.
            C keyValue = cast(key, node, field);
            log.debug("Is enumeration. value " + keyValue);

            if (keyValue != null) {
                for (Iterator<Map.Entry<C, String>> i = new RestrictedEnumerationIterator(locale, cloud, node, field); value == null && i.hasNext(); ) {
                    Map.Entry<C, String> entry = i.next();
                    log.debug("Check with " + entry);
                    if (keyValue.equals(entry.getKey()) ) {
                        value = entry.getValue();
                        log.debug("Found " + value);
                    }
                }
            }
        } else {
            log.debug("key" + key + " " + getEnumerationFactory());
        }
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<Map.Entry<C, String>> getEnumerationValues(Locale locale, Cloud cloud, Node node, Field field) {
        Iterator<Map.Entry<C, String>> i = new RestrictedEnumerationIterator(locale, cloud, node, field);
        return i.hasNext() ? i : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalizedEntryListFactory<C> getEnumerationFactory() {
        return enumerationRestriction.getEnumerationFactory();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataType.Restriction<LocalizedEntryListFactory<C>> getEnumerationRestriction() {
        return enumerationRestriction;
    }


    public Processor getDefaultProcessor() {
        return defaultProcessor == null ? CopyProcessor.getInstance() : defaultProcessor;
    }
    public void setDefaultProcessor(Processor dp) {
        defaultProcessor = dp;
    }

    @Override
    public CommitProcessor getCommitProcessor() {
        return commitProcessor == null ? EmptyCommitProcessor.getInstance() : commitProcessor;
    }
    @Override
    public void setCommitProcessor(CommitProcessor cp) {
        commitProcessor = cp;
    }
    @Override
    public CommitProcessor getDeleteProcessor() {
        return deleteProcessor == null ? EmptyCommitProcessor.getInstance() : deleteProcessor;
    }
    public void setDeleteProcessor(CommitProcessor cp) {
        deleteProcessor = cp;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Processor getProcessor(int action) {
        Processor processor;
        switch(action) {
        case PROCESS_GET: {
            processor =  getProcessors == null ? null : getProcessors[0];
            break;
        }
        case PROCESS_SET: {
            processor =  setProcessors == null ? null : setProcessors[0];
            break;
        }
        default: throw new IllegalArgumentException();
        }
        return processor == null ? CopyProcessor.getInstance() : processor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Processor getProcessor(int action, int processingType) {
        if (processingType == Field.TYPE_UNKNOWN) {
            return getProcessor(action);
        } else {
            Processor processor;
            switch(action) {
            case PROCESS_GET: processor =  getProcessors == null ? null : getProcessors[processingType]; break;
            case PROCESS_SET: processor =  setProcessors == null ? null : setProcessors[processingType]; break;
            default: throw new IllegalArgumentException();
            }
            return processor == null ? getProcessor(action) : processor;
        }
    }

    public Processor getProcessorWithoutDefault(int action, int processingType) {
        if (processingType == -1) {
            processingType = 0;
        }
        switch(action) {
        case PROCESS_GET: return getProcessors == null ? null : getProcessors[processingType];
        case PROCESS_SET: return setProcessors == null ? null : setProcessors[processingType];
        default: throw new IllegalArgumentException();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setProcessor(int action, Processor processor) {
        setProcessor(action, processor, Field.TYPE_UNKNOWN);
    }

    private Processor[] newProcessorsArray() {
        return new Processor[Fields.TYPE_MAXVALUE + 1];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setProcessor(int action, Processor processor, int processingType) {
        if (processingType == Field.TYPE_UNKNOWN) {
            processingType = 0;
        }
        switch (action) {
        case PROCESS_GET: {
            if (getProcessors == null) getProcessors = newProcessorsArray();
            getProcessors[processingType] = processor;
            break;
        }
        case PROCESS_SET: {
            if (setProcessors == null) setProcessors = newProcessorsArray();
            setProcessors[processingType] = processor;
            break;
        }
        default: throw new IllegalArgumentException();
        }
    }


    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    @Override
    public String[] getStyleClasses() {
        if (styleClasses != null) {
            return styleClasses;
        } else {
            return EMPTY_STRING_ARRAY;
        }
    }

    public void addStyleClass(String styleClass) {
        edit();
        List<String> list = new ArrayList<String>(Arrays.asList(getStyleClasses()));
        list.add(styleClass);
        styleClasses = list.toArray(EMPTY_STRING_ARRAY);
    }



    /**
     * @since MMBase-1.9.2
     */
    public Comparator<C> getComparator() {
        return new DataTypeComparator<C>(this) {
            @Override
            public int compare(C o1, C o2) {
                if (o1 == null) {
                    if (o2 == null) {
                        return 0;
                    } else {
                        return o2.hashCode();
                    }
                } else {
                    return o1.hashCode() - o2.hashCode();
                }
            }
        };
    }


    // ================================================================================
    // Follow implementations of the basic restrictions.


    // ABSTRACT

    /**
     * Abstract inner class Restriction. Based on static StaticAbstractRestriction
     */
    protected abstract class AbstractRestriction<D extends Serializable>  extends StaticAbstractRestriction<D> {
        protected AbstractRestriction(AbstractRestriction<?> source) {
            super(BasicDataType.this, source);
        }
        protected AbstractRestriction(String name, D value) {
            super(BasicDataType.this, name, value);
        }
    }
    /**
     * A Restriction is represented by these kind of objects.
     * When you override this class, take care of cloning of outer class!
     * This class itself is not cloneable. Cloning is hard when you have inner classes.
     *
     * All restrictions extend from this.
     *
     * See <a href="http://www.adtmag.com/java/articleold.asp?id=364">article about inner classes,
     * cloning in java</a>
     */
    protected static abstract class StaticAbstractRestriction<D extends Serializable>  implements DataType.Restriction<D> {
        private static final long serialVersionUID = -1921261633989010854L;
        protected final String name;
        protected final BasicDataType<?> parent;
        protected LocalizedString errorDescription;
        protected D value;
        protected boolean fixed = false;
        protected int enforceStrength = DataType.ENFORCE_ONCHANGE;

        /**
         * If a restriction has an 'absolute' parent restriction, then also that restriction must be
         * valid (because it was 'absolute'). A restriction gets an absolute parent if its
         * surrounding DataType is clone of DataType in which the same restriction is marked with
         * {@link DataType#ENFORCE_ABSOLUTE}.
         */
        protected StaticAbstractRestriction<?> absoluteParent = null;

        /**
         * Instantaties new restriction for a clone of the parent DataType. If the source
         * restriction is 'absolute' it will remain to be enforced even if the clone gets a new
         * value.
         */
        protected StaticAbstractRestriction(BasicDataType<?> parent, StaticAbstractRestriction<?> source) {
            this.name = source.getName();
            this.parent = parent;
            if (source.enforceStrength == DataType.ENFORCE_ABSOLUTE) {
                absoluteParent = source;
            } else {
                absoluteParent = source.absoluteParent;
            }
            inherit(source);
            if (source.enforceStrength == DataType.ENFORCE_ABSOLUTE) {
                enforceStrength = DataType.ENFORCE_ALWAYS;
            }
            if (parent != null && parent.restrictions != null) { // could happen during deserialization
                parent.restrictions.add(this);
            }

        }

        protected StaticAbstractRestriction(BasicDataType<?> parent, String name, D value) {
            this.name = name;
            this.parent = parent;
            this.value = value;
            if (parent != null && parent.restrictions != null) { // could happen during deserialization
                parent.restrictions.add(this);
            }
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public D getValue() {
            return value;
        }

        @Override
        public void setValue(D v) {
            parent.edit();
            if (fixed) {
                throw new IllegalStateException("Restriction '" + name + "' is fixed, cannot be changed");
            }

            this.value = v;
        }

        /**
         * @since MMBase-1.9.3
         */
        protected String getErrorDescriptionBundle() {
            return DATATYPE_BUNDLE;
        }

        @Override
        public LocalizedString getErrorDescription() {
            if (errorDescription == null) {
                // this is postponsed to first use, because otherwise 'getBaseTypeIdentifier' give correct value only after constructor of parent.
                String key = parent.getBaseTypeIdentifier() + "." + name + ".error";
                errorDescription = new LocalizedString(key);
                errorDescription.setBundle(getErrorDescriptionBundle());
            }
            return errorDescription;
        }

        @Override
        public void setErrorDescription(LocalizedString errorDescription) {
            this.errorDescription = errorDescription;
        }

        public boolean isFixed() {
            return fixed;
        }

        @Override
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
        protected final Collection<LocalizedString> addError(Collection<LocalizedString> errors, Object v, Node node, Field field) {
            if (errors == VALID) errors = new ArrayList<LocalizedString>();
            ReplacingLocalizedString error = new ReplacingLocalizedString(getErrorDescription());
            error.replaceAll("\\$\\{NAME\\}",       ReplacingLocalizedString.makeLiteral(getName()));
            error.replaceAll("\\$\\{CONSTRAINT\\}", ReplacingLocalizedString.makeLiteral(toString(node, field)));
            error.replaceAll("\\$\\{CONSTRAINTVALUE\\}", ReplacingLocalizedString.makeLiteral(valueString(node, field)));
            error.replaceAll("\\$\\{VALUE\\}",      ReplacingLocalizedString.makeLiteral(parent.castToPresent(v, node, field)));
            errors.add(error);
            return errors;
        }

        /**
         * If value of a a restriction depends on node, field, then you can override this
         */
        protected String valueString(Node node, Field field) {
            return Casting.toString(value);
        }

        /**
         * Whether {@link #validate} must enforce this condition
         */
        @SuppressWarnings("fallthrough")
        protected final boolean enforce(Object v, Node node, Field field) {
            switch(enforceStrength) {
            case DataType.ENFORCE_ABSOLUTE:
            case DataType.ENFORCE_ALWAYS:   return true;
            case DataType.ENFORCE_ONCHANGE: if (node == null || field == null || node.isChanged(field.getName())) return true;
            case DataType.ENFORCE_ONCREATE: return (node == null || node.isNew());
            case DataType.ENFORCE_ONVALIDATE:
                if (node == null) return true;
                if (node != null) {
                    Object committing = node.getCloud().getProperty(Node.CLOUD_COMMITNODE_KEY);
                    if (Integer.valueOf(node.getNumber()).equals(committing)) {
                        return false;
                    }
                }
                return true;
            case DataType.ENFORCE_NEVER:    return false;
            default:                        return true;
            }
        }

        /**
         * This method is called by {@link BasicDataType#validate(Object, Node, Field)} for each of its conditions.
         */
        protected Collection<LocalizedString> validate(Collection<LocalizedString> errors, Object v, Node node, Field field) {
            if (absoluteParent != null && ! absoluteParent.valid(v, node, field)) {
                int sizeBefore = errors.size();
                Collection<LocalizedString> res = absoluteParent.addError(errors, v,  node, field);
                if (res.size() > sizeBefore) {
                    return res;
                }
            }
            boolean enforce = enforce(v, node, field);
            if (! enforce) {
                if (log.isDebugEnabled()) {
                    log.debug("Ignoring " + v + " because not currently enforced by " + this);
                }
                return errors;
            } else if (valid(v, node, field)) {
                if (log.isDebugEnabled()) {
                    log.debug("Valid " + v + " for " + this);
                }
                return errors;
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Invalid  " + v + " for " + this);
                }
                return addError(errors, v, node, field);
            }
        }

        @Override
        public final boolean valid(Object v, Node node, Field field) {
            try {
                if (absoluteParent != null) {
                    if (! absoluteParent.valid(v, node, field)) return false;
                }
                return simpleValid(parent.castToValidate(v, node, field), node, field);
            } catch (Throwable t) {
                if (log.isServiceEnabled()) {
                    log.service("Not valid because cast-to-validate threw exception " + t.getClass(), t);
                }
                return false;
            }
        }

        protected abstract boolean simpleValid(Object v, Node node, Field field);

        @SuppressWarnings("unchecked")
        protected final void inherit(StaticAbstractRestriction<?> source, boolean cast) {
            // perhaps this value must be cloned?, but how?? Cloneable has no public methods....
            Object inheritedValue = source.getValue();
            D correctedValue;
            if (cast) {
                correctedValue = (D) parent.cast(inheritedValue, null, null);
            } else {
                correctedValue = (D) inheritedValue;
            }
            setValue(correctedValue);
            enforceStrength = source.getEnforceStrength();
            errorDescription = source.getErrorDescription().clone();
        }

        protected final void inherit(StaticAbstractRestriction<?> source) {
            inherit(source, false);
        }

        @Override
        public int getEnforceStrength() {
            return enforceStrength;
        }

        @Override
        public void setEnforceStrength(int e) {
            enforceStrength = e;
        }

        @Override
        public final String toString() {
            return toString(null, null);
        }

        public final String toString(Node node, Field field) {
            return name + ": " +
                (enforceStrength == DataType.ENFORCE_NEVER ? "*" : "") +
                valueString(node, field) + ( fixed ? "." : "");
        }

    }


    protected static abstract class DataTypeComparator<D> implements Comparator<D> {
        protected final BasicDataType<D> parent;
        DataTypeComparator(BasicDataType<D> p) {
            parent = p;
        }

        @Override
        public abstract int compare(D o1, D o2);

        @Override
        public boolean equals(Object o) {
            if (o instanceof DataTypeComparator) {
                DataTypeComparator comp = (DataTypeComparator) o;
                return parent.equals(comp.parent);
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 37 * hash + (this.parent != null ? this.parent.hashCode() : 0);
            return hash;
        }
    }



    // ================================================================================
    // Restriction implementation

    // REQUIRED
    protected class RequiredRestriction extends AbstractRestriction<Boolean> {
        private static final long serialVersionUID = 1L;

        RequiredRestriction(RequiredRestriction source) {
            super(source);
        }

        RequiredRestriction(boolean b) {
            super("required", Boolean.valueOf(b));
        }

        final boolean isRequired() {
            return Boolean.TRUE.equals(value);
        }

        @Override
        protected boolean simpleValid(Object v, Node node, Field field) {
            if(!isRequired()) return true;
            return v != null;
        }

    }

    // UNIQUE
    protected class UniqueRestriction extends AbstractRestriction<Boolean> {
        private static final long serialVersionUID = 1L;
        UniqueRestriction(UniqueRestriction source) {
            super(source);
        }

        UniqueRestriction(boolean b) {
            super("unique", Boolean.valueOf(b));
        }

        final boolean isUnique() {
            return Boolean.TRUE.equals(value);
        }

        @Override
        protected boolean simpleValid(final Object v, final Node node, final Field field) {
            if (! isUnique()) {
                log.debug("Not unique");
                return true;
            }
            if (field != null && v != null && value != null ) {

                if (field.isVirtual()) {
                    log.warn("Cannot check uniqueness on field " + field + " because it is virtual");
                    return true; // e.g. if the field was defined in XML but not present in DB (after upgrade?)
                }

                if (node != null && !node.isNew()) {
                    if (field.getName().equals("number")) {
                        // on 'number' there is a unique constraint, if it is checked for a non-new node
                        // we can simply avoid all quering because it will result in a query number == <number> and number <> <number>
                        if (Casting.toInt(v) == node.getNumber()) {
                            return true;
                        } else {
                            // changing
                            log.warn("Odd, changing number of node " + node + " ?!", new Exception());
                        }
                    }
                }

                if (log.isDebugEnabled()) {
                    log.debug("Checking '" + value + "'");
                }
                NodeManager nodeManager = field.getNodeManager();
                Cloud cloud = nodeManager.getCloud();
                if (cloud.getUser().getRank().getInt() < Rank.ADMIN_INT) {
                    // This will test for uniqueness using bridge, so you'll miss objects you can't
                    // see (and database doesn't know that!)
                    // So try using an administrator for that! That would probably work ok.
                    Cloud adminCloud = cloud.getCloudContext().getCloud("mmbase", "class", null);
                    if (adminCloud.getUser().getRank().getInt() > cloud.getUser().getRank().getInt()) {
                        cloud = adminCloud;
                        nodeManager = adminCloud.getNodeManager(nodeManager.getName());
                    }
                }
                // create a query and query for the value
                NodeQuery query = nodeManager.createQuery();
                Constraint constraint = Queries.createConstraint(query, field.getName(), FieldCompareConstraint.EQUAL, v);
                Queries.addConstraint(query, constraint);
                if (node != null && !node.isNew()) {
                    constraint = Queries.createConstraint(query, "number", FieldCompareConstraint.NOT_EQUAL, node.getNumber());
                    Queries.addConstraint(query, constraint);
                }
                int c = Queries.count(query);
                if (log.isDebugEnabled()) {
                    log.debug(query.toSql() + " -> " + c);
                }
                return c == 0;
            } else {
                if (field == null) log.warn("Cannot check uniqueness  without field");
                return true;
            }
        }
    }


    // TYPE

    protected class TypeRestriction extends AbstractRestriction<Class<?>> {
        private static final long serialVersionUID = 1L;
        TypeRestriction(TypeRestriction source) {
            super(source);
        }

        TypeRestriction() {
            super("type", BasicDataType.this.getClass());
        }

        @Override
        protected boolean simpleValid(Object v, Node node, Field field) {
            try {
                BasicDataType.this.cast(v, node, field);
                return true;
            } catch (Throwable e) {
                log.error(e);
                return false;
            }
        }
    }

    // ENUMERATION
    protected class EnumerationRestriction extends AbstractRestriction<LocalizedEntryListFactory<C>> {
        private static final long serialVersionUID = 1L;

        EnumerationRestriction(EnumerationRestriction source) {
            super(source);
            value = value != null ? (LocalizedEntryListFactory<C>) value.clone() : null;
        }

        EnumerationRestriction(LocalizedEntryListFactory<C> entries) {
            super("enumeration", entries);
        }

        final LocalizedEntryListFactory<C> getEnumerationFactory() {
            if(value == null) {
                value = new LocalizedEntryListFactory<C>();
            }
            return value;
        }

        /**
         * @since MMBase-1.9.6
         */
        public Collection<C> getEnumerationKeys(Locale locale, Cloud cloud, Node node, Field field) {
            if (value == null) {
                return Collections.emptyList();
            }
            return value.getKeys(locale, cloud, node, field);
        }
        public Collection<Map.Entry<C, String>> getEnumeration(Locale locale, Cloud cloud, Node node, Field field) {
            if (value == null) {
                return Collections.emptyList();
            }
            return value.get(locale, cloud, node, field);
        }

        /**
         * @see BasicDataType#preCast
         */
        protected <D> D preCast(D v, Cloud cloud) {
            if (getValue() == null) {
                return v;
            }
            try {
                if (v == null) {
                    return null;
                }
                Object res;
                if (!value.isEmpty()) {
                    res = value.castKey(v, cloud);
                } else {
                    res = v;
                }
                // type may have changed (to some value wrapper). Undo that:
                return (D) Casting.unWrap(res);

                // Used to be this, but that give CCE if type unrecognized
                //return (D) Casting.toType(v.getClass(), cloud, res);
            } catch (NoClassDefFoundError ncdfe) {
                log.error("Could not find class " + ncdfe.getMessage() + " while casting " + v.getClass() + " " + v, ncdfe);
                return v;
            }

        }

        @Override
        protected boolean simpleValid(Object v, Node node, Field field) {
            if (value == null || value.isEmpty()) {
                return true;
            }

            Cloud cloud = BasicDataType.this.getCloud(node, field);
            Collection<C> validValues = getEnumerationKeys(null, cloud, node, field);
            if (validValues.isEmpty()) {
                return true;
            }
            C candidate;
            try {
                candidate = BasicDataType.this.cast(v, cloud, node, field);
            } catch (CastException ce) {
                log.info(ce);
                return false;
            }
            for (C valid : validValues) {
                if (BasicDataType.this.getComparator().compare(valid, candidate) == 0) {
                    return true;
                }
            }
            return false;
        }

        @Override
        protected String valueString(Node node, Field field) {
            Collection<C> col = getEnumerationKeys(null, null, node, field);
            //System.out.println("Making value string gof " + col);
            if (col.isEmpty()) {
                return "";
            }
            StringBuilder buf = new StringBuilder();
            Iterator<C> it = col.iterator();
            int i = 0;
            while (it.hasNext() && i < 10) {
                i++;
                C v = it.next();
                buf.append(Casting.toString(v));
                if (it.hasNext()) {
                    buf.append(", ");
                }
            }
            if (i < col.size()) {
                buf.append(".(").append(col.size() - i).append(" more ..)");
            }
            return buf.toString();
        }

    }



    /**
     * Iterates over the collection provided by the EnumerationRestriction, but skips the values
     * which are invalid because of the other restrictions on this DataType.
     */
    //Also, it 'preCasts' the * keys to the right type.

    protected class RestrictedEnumerationIterator implements Iterator<Map.Entry<C, String>> {
        private final Iterator<Map.Entry<C, String>> baseIterator;
        private final Node node;
        private final Field field;
        private Map.Entry<C, String> next = null;

        RestrictedEnumerationIterator(Locale locale, Cloud cloud, Node node, Field field) {
            Collection<Map.Entry<C, String>> col = enumerationRestriction.getEnumeration(locale, cloud, node, field);
            if (log.isDebugEnabled()) {
                log.debug("Restricted iterator on " + col);
            }
            baseIterator =  col.iterator();
            this.node = node;
            this.field = field;
            determineNext();
        }

        protected final void determineNext() {
            next = null;
            while (baseIterator.hasNext()) {
                final Map.Entry<C, String> entry = baseIterator.next();
                C value = entry.getKey();
                Collection<LocalizedString> validationResult = BasicDataType.this.validate(value, node, field, false);
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
                    for (LocalizedString localizedString : validationResult) {
                        errors += localizedString.get(null);
                    }
                    log.debug("Value " + value.getClass() + " " + value + " does not validate : " + errors);
                }
            }
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public Map.Entry<C, String> next() {
            if (next == null) {
                throw new NoSuchElementException();
            }
            Map.Entry<C, String> n = next;
            determineNext();
            return n;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Cannot remove entries from " + getClass());
        }

        @Override
        public String toString() {
            return "restricted iterator(" + enumerationRestriction + ")";
        }
    }

}
