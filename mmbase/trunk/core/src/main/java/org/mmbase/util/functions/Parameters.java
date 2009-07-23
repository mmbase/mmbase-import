/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.functions;

import org.mmbase.util.*;
import java.util.*;
import org.mmbase.util.logging.*;

/**
 * This class implemements the Parameters interface.
 * It provides a convenient way to create a List that allows the use of 'named parameters'.
 * This List is therefore backed by a HashMap, but it behaves as a list. So if you set
 * a parameter with a certain name, it always appears in the same location of the List.
 * This List is modifiable but not resizeable. It is always the size of the definition array.
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.7
 * @version $Id$
 * @see Parameter
 * @see #Parameters(Parameter[])
 */

public class Parameters extends AbstractList<Object> implements java.io.Serializable {
    private static final Logger log = Logging.getLoggerInstance(Parameters.class);

    private static final long serialVersionUID = 1L;

    /**
     * No need to bother for the functions with no parameters. This is a constant you could supply.
     */
    public static final Parameters VOID = new Parameters(Parameter.emptyArray());


    /**
     * The contents of this List are stored in this HashMap.
     */
    protected final Map<String, Object> backing;

    protected final List<Map.Entry<String, Object>> patternBacking;

    // Index of the first PatternParameter
    protected int patternLimit = -1;

    /**
     * This array maps integers (position in array) to map keys, making it possible to implement
     * List.
     */
    protected Parameter<Object>[] definition;

    /**
     * If <code>true</code>, values are automatically cast to the right type (if possible) when set.
     */
    protected boolean autoCasting = false;

    private int fromIndex = 0;
    protected int toIndex;

    /**
     * Constructor, taking an Parameter[] array argument.
     * The Parameter may also be Parameter.Wrapper
     * (to implement overriding of functions).  The idea is that these array arguments are defined
     * as constants in the classes which define a function with variable arguments.
     * <br />
     * The Parameter[] array could e.g. be somewhere defined as a constant, like this:
     * <pre>
     *   <code>
     *     public final static Parameter[] MYFUNCTION_PARAMETERS = {
     *         new Parameter("type", Integer.class),
     *         new Parameter("text", String.class),
     *         Parameter.CLOUD,                                 // a predefined parameter
     *         new Parameter.Wrapper(OTHERFUNCTION_PARAMETERS)  // a way to include another definition in this one
     *     };
     *   </code>
     * </pre>
     */
    public Parameters(Parameter<?>... def) {
        definition = Functions.define(def, new ArrayList<Parameter<?>>()).toArray(Parameter.emptyArray());
        if (log.isDebugEnabled()) {
            log.debug("Found definition " + Arrays.asList(definition));
        }
        backing = new HashMap<String, Object>();
        List<Map.Entry<String, Object>> pb = null;
        // fill with default values, and check for non-unique keys.
        int i = fromIndex;
        for (; i < definition.length; i++) {

            if (definition[i]  instanceof PatternParameter) {
                pb = new ArrayList<Map.Entry<String, Object>>();
                break;
            }
            if (backing.put(definition[i].getName(), definition[i].getDefaultValue()) != null) {
                throw new IllegalArgumentException("Parameter keys not unique");
            }

        }
        patternLimit = i + (pb == null ? 1 : 0);
        toIndex = i;
        patternBacking = pb;

    }

    /**
     * If you happen to have a List of parameters, then you can wrap it into an Parameters with this constructor.
     *
     * @param values Collection with values. This Collection should have a predictable iteration order.
     * @throws NullPointerException if definition is null
     * @see #Parameters(Parameter[])
     */
    public Parameters(Parameter<?>[] def, Collection<?> values) {
        this(def);
        setAll(values);
    }
    /**
     * @since MMBase-1.9
     */
    public Parameters(Parameter<?>[] def, Object... values) {
        this(def);
        setAll(values);
    }

    /**
     * @since MMBase-1.9
     */
    public Parameters(Map<String, Object> backing) {
        this.backing = backing;
        toIndex = backing.size() - 1;
        definition = null;
        patternBacking = null;
    }

    /**
     * @since MMBase-1.9
     */
    public Parameters(final List<Map.Entry<String, Object>> list) {
        backing = new HashMap<String, Object>();
        Set<String> myCollections = null;
        for (Map.Entry<String, Object> entry : list) {
            String key = entry.getKey(); Object value = entry.getValue();
            Object prevValue = backing.put(key, value);
            if (prevValue != null) {
                List<Object> newValue;
                if (myCollections == null) {
                    myCollections = new HashSet<String>();
                }
                if (myCollections.contains(key)) {
                    newValue = (ArrayList<Object>) prevValue;
                } else {
                    myCollections.add(key);
                    newValue = new ArrayList<Object>();
                    if (prevValue instanceof Collection) {
                        newValue.addAll((Collection<?>) prevValue);
                    } else {
                        newValue.add(prevValue);
                    }
                }
                if (value instanceof Collection) {
                    newValue.addAll((Collection<?>) value);
                } else {
                    newValue.add(value);
                }
                backing.put(key, newValue);
            }
        }
        toIndex = backing.size() - 1;
        definition = null;
        patternBacking = null;
    }

    /**
     * Copy-constructor
     * @since MMBase-1.9.1
     */
    public  Parameters(Parameters params) {
        backing = new HashMap<String, Object>();
        backing.putAll(params.backing);
        definition = params.definition;
        if (params.patternBacking != null) {
            patternBacking = new ArrayList<Map.Entry<String, Object>>();
            patternBacking.addAll(params.patternBacking);
        } else {
            patternBacking = null;
        }
        patternLimit   = params.patternLimit;
        fromIndex = params.fromIndex;
        toIndex   = params.toIndex;
    }


    /**
     * Used for nicer implemenation  of subList (which we want to also be instanceof Parameters).
     */
    protected Parameters(Parameters  params, int from, int to) {
        backing = params.backing;
        definition = params.definition;
        patternBacking = params.patternBacking;
        patternLimit   = params.patternLimit;
        fromIndex = from + params.fromIndex;
        toIndex   = to   + params.fromIndex;
        if (fromIndex < 0) throw new IndexOutOfBoundsException("fromIndex < 0");
        if (toIndex > definition.length) throw new IndexOutOfBoundsException("toIndex greater than length of list");
        if (fromIndex > toIndex) throw new IndexOutOfBoundsException("fromIndex > toIndex");

    }

    /**
     * @since MMBase-1.9
     */
    public boolean isHavingPatterns() {
        return patternBacking != null;
    }

    protected final void checkDef() {
        if (definition == null) {
            definition = new Parameter[backing.size()];
            int i = 0;
            for (Map.Entry<String, Object> entry : backing.entrySet()) {
                definition[i++] = new Parameter<Object>(entry);
            }
        }
    }


    public String toString() {
        StringBuilder buf = new StringBuilder("[");
        checkDef();
        int i = fromIndex;
        for (i = fromIndex; i < toIndex && i < patternLimit; i++) {
            if (buf.length() > 1) buf.append(", ");
            buf.append(definition[i]).append('=').append(get(i));
        }
        if (patternBacking != null) {
            for (Map.Entry<String, Object> entry : patternBacking) {
                if (buf.length() > 1) buf.append(", ");
                buf.append(entry.getKey()).append('=').append(entry.getValue());
            }
        }
        buf.append("]");
        return buf.toString();
    }

    public Class<?>[] toClassArray() {
        Class<?>[] array = new Class[toIndex - fromIndex];
        checkDef();
        for (int i = fromIndex; i < toIndex; i++) {
            array[i - fromIndex] = definition[i].getDataType().getTypeAsClass();
        }
        return array;
    }

    /**
     * Sets the 'auto casting' property (which on default is false)
     * @param autocast the new value for autocast
     * @see #isAutoCasting
     */
    public void setAutoCasting(boolean autocast) {
        autoCasting = autocast;
    }

    public Parameter<?>[] getDefinition() {
        checkDef();
        if (fromIndex > 0 || toIndex != definition.length - 1) {
            return Arrays.asList(definition).subList(fromIndex, toIndex).toArray(Parameter.emptyArray());
        } else {
            return definition;
        }
    }

    /**
     * Whether this Parameters object is 'automaticly casting'. If it is, that means that you can set e.g.
     * an Integer by a String.
     * @return <code>true</code> if autocasting is on
     */
    public boolean isAutoCasting() {
        return autoCasting;
    }

    // implementation of List
    public int size() {
        return toIndex - fromIndex;
    }

    // implementation of List
    public Object get(int i) {
        checkDef();
        int j = i + fromIndex;
        if (j < patternLimit) {
            return backing.get(definition[j].getName());
        } else {
            if (patternBacking == null) throw new IndexOutOfBoundsException();
            return patternBacking.get(j - patternLimit).getValue();
        }
    }

    // implementation of (modifiable) List
    // @throws NullPointerException if definition not set
    public Object set(int i, Object value) {
        checkDef();
        int j = i + fromIndex;
        if (j < patternLimit) {
            Parameter<?> a = definition[j];
            if (autoCasting) value = a.autoCast(value);
            a.checkType(value);
            return backing.put(a.getName(), value);
        } else {
            if (patternBacking == null) throw new IndexOutOfBoundsException("No index " + i + " (" + j + "). Patternlimit " + patternLimit + " " + this);
            return patternBacking.get(j - patternLimit).setValue(value);
        }
    }


    /**
     * Throws an IllegalArgumentException if one of the required parameters was not entered.
     * @see #validate() For complete datatype validation
     */
    public void checkRequiredParameters() {
        checkDef();
        for (int i = fromIndex; i < toIndex && i < patternLimit; i++) {
            Parameter<?> a = definition[i];
            if (a.isRequired() && (get(a.getName()) == null)) {
                throw new IllegalArgumentException("Required parameter '" + a.getName() + "' is null (of (" + toString() + ")");
            }
        }
    }
    /**
     * Validates all values in the Parameters object with their {@link Parameter#getDataType()}. You should
     * call this method if you ready to pass it into some function or so, if you want
     * validation. If the returned Collection is not empty, something is wrong, and you may want to
     * not proceed.
     *
     * @return A collection with errors.
     * @since MMBase-1.9.2
     */
    public Collection<LocalizedString> validate() {
        checkDef();
        Collection<LocalizedString> errors = new ArrayList<LocalizedString>();
        for (int i = fromIndex; i < toIndex && i < patternLimit; i++) {
            Parameter<?> a = definition[i];
            errors.addAll(a.getDataType().castAndValidate(get(a), null, null));
        }
        return errors;
    }

    /**
     * Returns the position of a parameter in the parameters list, using the Parameter as a qualifier.
     * you can then acecss that paramter with {@link #get(int)}.
     * @param parameter the parameter
     * @return the index of the parameter, or -1 if it doesn't exist
     */

    public int indexOfParameter(Parameter<?> parameter) {
        checkDef();
        int index = -1;
        for (int i = fromIndex; i < toIndex && i < patternLimit; i++) {
            if (definition[i].equals(parameter)) {
                index = i - fromIndex;
                break;
            }
        }
        return index;
    }


    /**
     * Returns the position of a parameter in the parameters list, using the parameter name as a qualifier.
     * you can then acecss that paramter with {@link #get(int)}.
     * @param parameterName the name of the parameter
     * @return the index of the parameter, or -1 if it doesn't exist
     */
    public int indexOfParameter(String parameterName) {
        checkDef();
        for (int i = fromIndex; i < toIndex && i < patternLimit; i++) {
            if (definition[i].getName().equals(parameterName)) {
                return i - fromIndex;
            }
        }
        if (patternBacking != null) {
            for (int i = 0; i < toIndex - patternLimit; i++) {
                Map.Entry<String, Object> entry = patternBacking.get(i);
                if (entry.getKey().equals(parameterName)) {
                    return patternLimit + i - fromIndex;
                }
            }
        }
        return -1;
    }


    /**
     * Checks wether a certain parameter is available, using the Parameter as a qualifier.
     * @param parameter the parameter
     * @return <code>true</code> if a parameter exists.
     */
    public boolean containsParameter(Parameter<?> parameter) {
        return indexOfParameter(parameter) != -1;
    }

    /**
     * Checks wether a certain parameter is available, using the parameter name as a qualifier.
     * @param parameterName the name of the parameter
     * @return <code>true</code> if a parameter exists.
     */
    public boolean containsParameter(String parameterName) {
        return indexOfParameter(parameterName) != -1;
    }

    /**
     * Sets the value of a parameter.
     * @param parameter the Parameter describing the parameter to set
     * @param value the object value to set
     * @throws IllegalArgumentException if either the argument name is unknown to this Parameters, or the value is of the wrong type.
     */
    public <F> Parameters set(Parameter<F> parameter, F value) {
        int index = indexOfParameter(parameter);
        if (index > -1) {
            set(index, value);
            return this;
        } else {
            throw new IllegalArgumentException("The parameter '" + parameter + "' is not defined (defined are " + toString() + ")");
        }
    }

    /**
     * Sets the value of a parameter.
     * @param parameterName the name of the parameter to set
     * @param value the object value to set
     * @throws IllegalArgumentException if either the argument name is unknown to this Parameters, or the value is of the wrong type.
     */
    public Parameters set(String parameterName, Object value) {
        int index = indexOfParameter(parameterName);
        if (index > -1) {
            set(index, value);
            return this;
        } else {
            for (int i = patternLimit; i < definition.length; i++) {
                if (definition[i].matches(parameterName)) {
                    patternBacking.add(new Entry<String, Object>(parameterName, value));
                    toIndex++;
                    return this;
                }
            }
            throw new IllegalArgumentException("The parameter '" + parameterName + "' is not defined (defined are " + toString() + ")");
        }
    }

    /**
     * Copies all values of a map to the corresponding values of this Parameters Object.
     */
    public Parameters  setAll(Map<String, ?> map) {
        if (map != null) {
            for (Map.Entry<String, ?> entry : map.entrySet()) {
                set(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }

    /**
     * Copies all values of a collection to the corresponding values of this Parameters Object.
     */
    public Parameters setAll(Collection<?> values) {
        if (values != null) {
            if (log.isDebugEnabled()) {
                checkDef();
                if (values.size() > definition.length) {
                    log.debug("Given too many values. " + values + " does not match " + Arrays.asList(definition));
                }
            }
            Iterator<?> valueIterator = values.iterator();
            int i = 0;
            while (valueIterator.hasNext()) {
                set(i++, valueIterator.next());
            }
        }
        return this;
    }
    /**
     * @since MMBase-1.9
     */
    public Parameters setAll(Object... values) {
        int i = 0;
        for(Object value : values) {
            set(i++, value);
        }
        return this;
    }
    /**
     * @since MMBase-1.9
     */
    public Parameters setAllIfDefined(Parameters params) {
        for (Parameter param : params.getDefinition()) {
            setIfDefined(param, params.get(param));
        }
        return this;
    }

    /**
     * @since MMBase-1.8.7
     */
    public Parameters setAll(Parameters params) {
        for (Parameter param : params.getDefinition()) {
            set(param, params.get(param));
        }
        return this;
    }

    public Parameters subList(int fromIndex, int toIndex) {
        return new Parameters(this, fromIndex, toIndex);
    }


    /**
     * Sets the value of an argument, if the argument is defined, otherwise do nothing.
     * @param parameter the parameter to set
     * @param value the object value to set
     */
    public <F> Parameters setIfDefined(Parameter<F> parameter, F value) {
        int index = indexOfParameter(parameter);
        if (index > -1) {
            set(index, value);
        }
        return this;
    }


    /**
     * Sets the value of an argument, if the argument is defined, otherwise do nothing.
     * @param parameterName the name of the parameter to set
     * @param value the object value to set
     */
    public Parameters setIfDefined(String parameterName, Object value) {
        int index = indexOfParameter(parameterName);
        if (index > -1) {
            set(index, value);
        }
        return this;
    }

    /**
     * Gets the value of a parameter.
     * @param parameter the parameter to get
     * @return value the parameter value
     */
    public <F> F get(Parameter<F> parameter) {
        return (F) get(parameter.getName());
    }

    /**
     * Gets the value of a parameter.
     * @param parameterName the name of the parameter to get
     * @return value the parameter value
     */
    public Object get(String parameterName) {
        Object o = backing.get(parameterName);
        if (o == null) {
            if (backing.containsKey(parameterName)) return null;
            if (patternBacking != null) {
                for (Map.Entry<String, Object> entry : patternBacking) {
                    if (entry.getKey().equals(parameterName)) return entry.getValue();
                }
            }
            return null;
        } else {
            return o;
        }
    }


    /**
     * Gets the value of a parameter, cast to a String.
     * @param parameter the parameter to get
     * @return value the parameter value as a <code>STring</code>
     */

    public String getString(Parameter<?> parameter) {
        return getString(parameter.getName());
    }


    /**
     * Gets the value of a parameter, cast to a String.
     * @param parameterName the name of the parameter to get
     * @return value the parameter value as a <code>String</code>
     */
    public String getString(String parameterName) {
        return Casting.toString(get(parameterName));
    }


    /**
     * Returns a view on the backing where every value wich is the default value is set to
     * <code>null</code>. If the default is not <code>null</code> itself, <em>but the value is</em>,
     * than the value will be returned as an empty string.
     *
     * This can be used to generated keys and such, which are not polluted with all kind of default values.
     * @since MMBase-1.9.1
     */
    protected Map<String, Object> undefaultBacking() {
        return new AbstractMap<String, Object>() {
            public Set<Map.Entry<String, Object>> entrySet() {
                return new AbstractSet<Map.Entry<String, Object>>() {
                    public Iterator<Map.Entry<String, Object>> iterator() {
                        final Iterator<Map.Entry<String, Object>> iterator = Parameters.this.backing.entrySet().iterator();

                        return new Iterator<Map.Entry<String, Object>>() {
                            public boolean hasNext() {
                                return iterator.hasNext();
                            }
                            public Map.Entry<String, Object> next() {
                                Map.Entry<String, Object> entry = iterator.next();
                                Parameter<?> def = Parameters.this.definition[Parameters.this.indexOfParameter(entry.getKey())];
                                Object defaultValue = def.getDefaultValue();
                                if (defaultValue == null) {
                                    return entry;
                                }
                                if (defaultValue.equals(entry.getValue())) {
                                    return new org.mmbase.util.Entry<String, Object>(entry.getKey(), null);
                                } else {
                                    if (entry.getValue() == null) {
                                        return new org.mmbase.util.Entry<String, Object>(entry.getKey(), "");
                                    } else {
                                        return entry;
                                    }
                                }
                            }
                            public void remove() {
                                throw new UnsupportedOperationException();
                            }

                        };
                    }
                    public int size() {
                        return Parameters.this.backing.size();
                    }
                };
            }
        };
    }

    private Map<String, Object> toMap(final Map<String, Object> b) {
        return new AbstractMap<String, Object>() {
            public Set<Map.Entry<String, Object>> entrySet() {
                return new AbstractSet<Map.Entry<String, Object>>() {
                    public Iterator<Map.Entry<String, Object>> iterator() {
                        return patternBacking != null ?
                            new org.mmbase.util.ChainedIterator<Map.Entry<String, Object>>(b.entrySet().iterator(), patternBacking.iterator())
                            :
                            b.entrySet().iterator();
                    }
                    public int size() {
                        return Parameters.this.size();
                    }
                };
            }
        };
    }

    /**
     * Gives the arguments back as a (unmodifiable) map.
     */
    public Map<String, Object> toMap() {
        return toMap(backing);
    }
    /**
     * Returns this parameters object as a (unmodifiable)  Map, but all values which only have the
     * default value are <code>null</code>
     * @since MMBase-1.9.1
     */
    public Map<String, Object> toUndefaultMap() {
        return toMap(undefaultBacking());
    }

    private List<Map.Entry<String, Object>> toEntryList(final Map<String, Object> b) {
        return new AbstractList<Map.Entry<String, Object>>() {
            public int size() {
                return Parameters.this.size();
            }
            public Map.Entry<String, Object> get(final int i) {

                return new Map.Entry<String, Object>() {
                    final Parameter<?> a = Parameters.this.definition[i + Parameters.this.fromIndex];
                    // see Map.Entry
                    public String getKey() {
                        return a.getName();
                    }

                    // see Map.Entry
                    public Object getValue() {
                        return b.get(a.getName());
                    }

                    // see Map.Entry
                    public Object setValue(Object v) {
                        return b.put(a.getName(), v);
                    }

                    public int hashCode() {
                        Object value = getValue();
                        return a.getName().hashCode() ^ (value == null ? 0 : value.hashCode());
                    }
                    public boolean equals(Object o) {
                        if (o instanceof Map.Entry) {
                            Map.Entry<String,Object> entry = (Map.Entry<String,Object>) o;
                            Object value = getValue();
                            return
                                a.getName().equals(entry.getKey()) &&
                                (value == null ? entry.getValue() == null : value.equals(entry.getValue()));
                        } else {
                            return false;
                        }
                    }
                };
            }
        };
    }

    /**
     * Returns the Parameters as an unmodifiable List of Map.Entrys with predictable iteration order
     * (the same order of this Parameters, which is a List of the values only, itself)
     * @since MMBase-1.9
     */
    public  List<Map.Entry<String, Object>> toEntryList() {
        return toEntryList(backing);
    }
    /**
     * Returns the Parameters as an unmodifiable List of Map.Entrys with predictable iteration order
     * (the same order of this Parameters, which is a List of the values only, itself)
     * Values which are the same as the default value are returned as <code>null</code>. If the
     * default is not <code>null</code> but the value is, then it is returned as an empty string.
     * @since MMBase-1.9.1
     */
    public  List<Map.Entry<String, Object>> toUndefaultEntryList() {
        return toEntryList(undefaultBacking());
    }
}
