/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.functions;

import org.mmbase.util.Casting;
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

public class Parameters extends AbstractList implements java.io.Serializable {
    private static final Logger log = Logging.getLoggerInstance(Parameters.class);

    private static final int serialVersionUID = 1;

    /**
     * No need to bother for the functions with no parameters. This is a constant you could supply.
     */
    public static final Parameters VOID = new Parameters(Parameter.EMPTY);


    /**
     * The contents of this List are stored in this HashMap.
     */
    protected Map backing; /* String -> Value */

    /**
     * This array maps integers (position in array) to map keys, making it possible to implement
     * List.
     */
    protected Parameter[] definition = null;

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
    public Parameters(Parameter[] def) {
        definition = (Parameter[]) Functions.define(def, new ArrayList()).toArray(Parameter.EMPTY);
        toIndex = definition.length;
        if (log.isDebugEnabled()) {
            log.debug("Found definition " + Arrays.asList(definition));
        }
        backing = new HashMap();
        // fill with default values, and check for non-unique keys.
        for (int i = fromIndex; i < toIndex; i++) {
            if (backing.put(definition[i].getName(), definition[i].getDefaultValue()) != null) {
                throw new IllegalArgumentException("Parameter keys not unique");
            }

        }

    }

    /**
     * If you happen to have a List of parameters, then you can wrap it into an Parameters with this constructor.
     *
     * @param values Collection with values. This Collection should have a predictable iteration order.
     * @throws NullPointerException if definition is null
     * @see #Parameters(Parameter[])
     */
    public Parameters(Parameter[] def, Collection values) {
        this(def);
        setAll(values);
    }

    /**
     * Used for nicer implemenation  of subList (which we want to also be instanceof Parameters).
     */
    protected Parameters(Parameters params, int from, int to) {
        backing = params.backing;
        definition = params.definition;
        fromIndex = from + params.fromIndex;
        toIndex   = to   + params.fromIndex;
        if (fromIndex < 0) throw new IndexOutOfBoundsException("fromIndex < 0");
        if (toIndex > definition.length) throw new IndexOutOfBoundsException("toIndex greater then length of list");
        if (fromIndex > toIndex) throw new IndexOutOfBoundsException("fromIndex > toIndex");

    }

    public String toString() {
        StringBuffer buf = new StringBuffer("[");
        for (int i = fromIndex; i < toIndex; i++) {
            if (i > fromIndex) buf.append(", ");
            buf.append(definition[i]).append('=').append(get(i));
        }
        buf.append("]");
        return buf.toString();
    }

    public Class[] toClassArray() {
        Class[] array = new Class[toIndex - fromIndex];
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

    public Parameter[] getDefinition() {
        if (fromIndex > 0 || toIndex != definition.length - 1) {
            return (Parameter[]) Arrays.asList(definition).subList(fromIndex, toIndex).toArray(Parameter.EMPTY);
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
    // @throws NullPointerException if definition not set
    public int size() {
        return toIndex - fromIndex;
    }

    // implementation of List
    // @throws NullPointerException if definition not set
    public Object get(int i) {
        return backing.get(definition[i + fromIndex].getName());
    }

    // implementation of (modifiable) List
    // @throws NullPointerException if definition not set
    public Object set(int i, Object value) {
        Parameter a = definition[i + fromIndex];
        if (autoCasting) value = a.autoCast(value);
        a.checkType(value);
        return backing.put(a.getName(), value);
    }


    /**
     * Throws an IllegalArgumentException if one of the required parameters was not entered.
     */
    public void checkRequiredParameters() {
        for (int i = fromIndex; i < toIndex; i++) {
            Parameter a = definition[i];
            if (a.isRequired() && (get(a.getName()) == null)) {
                throw new IllegalArgumentException("Required parameter '" + a.getName() + "' is null (of (" + toString() + ")");
            }
        }
    }

    /**
     * Returns the position of a parameter in the parameters list, using the Parameter as a qualifier.
     * you can tehn acecss that paramter with {@link #get(int)}.
     * @param parameter the parameter
     * @return the index of the parameter, or -1 if it doesn't exist
     */

    public int indexOfParameter(Parameter parameter) {
        int index = -1;
        for (int i = fromIndex; i < toIndex; i++) {
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
        int index = -1;
        for (int i = fromIndex; i < toIndex; i++) {
            if (definition[i].getName().equals(parameterName)) {
                index = i - fromIndex;
                break;
            }
        }
        return index;
    }


    /**
     * Checks wether a certain parameter is available, using the Parameter as a qualifier.
     * @param parameter the parameter
     * @return <code>true</code> if a parameter exists.
     */
    public boolean containsParameter(Parameter parameter) {
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
    public Parameters set(Parameter parameter, Object value) {
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
            throw new IllegalArgumentException("The parameter '" + parameterName + "' is not defined (defined are " + toString() + ")");
        }
    }

    /**
     * Copies all values of a map to the corresponding values of this Parameters Object.
     */
    public Parameters setAll(Map map) {
        if (map != null) {
            Iterator i = map.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry entry = (Map.Entry) i.next();
                set((String) entry.getKey(), entry.getValue());
            }
        }
        return this;
    }

    /**
     * Copies all values of a collection to the corresponding values of this Parameters Object.
     */
    public Parameters setAll(Collection values) {
        if (values != null) {
            if (log.isDebugEnabled()) {
                if (values.size() > definition.length) {
                    log.debug("Given too many values. " + values + " does not match " + Arrays.asList(definition));
                }
            }
            Iterator valueIterator = values.iterator();
            int i = 0;
            while (valueIterator.hasNext()) {
                set(i++, valueIterator.next());
            }
        }
        return this;
    }

    /**
     * @since MMBase-1.8.7
     */
    public Parameters setAll(Parameters params) {
        Parameter[] def = params.getDefinition();
        for (int i = 0; i < def.length; i++) {
            Parameter param = def[i];
            set(param, params.get(param));
        }
        return this;
    }
    public List subList(int fromIndex, int toIndex) {
        return new Parameters(this, fromIndex, toIndex);
    }


    /**
     * Sets the value of an argument, if the argument is defined, otherwise do nothing.
     * @param parameter the parameter to set
     * @param value the object value to set
     */
    public Parameters setIfDefined(Parameter parameter, Object value) {
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
    public Object get(Parameter parameter) {
        return get(parameter.getName());
    }


    /**
     * Gets the value of a parameter.
     * @param parameterName the name of the parameter to get
     * @return value the parameter value
     */
    public Object get(String parameterName) {
        return backing.get(parameterName);
    }


    /**
     * Gets the value of a parameter, cast to a String.
     * @param parameter the parameter to get
     * @return value the parameter value as a <code>STring</code>
     */

    public String getString(Parameter parameter) {
        return getString(parameter.getName());
    }


    /**
     * Gets the value of a parameter, cast to a String.
     * @param parameterName the name of the parameter to get
     * @return value the parameter value as a <code>STring</code>
     */
    public String getString(String parameterName) {
        return Casting.toString(get(parameterName));
    }

    /**
     * Gives the arguments back as a (unmodifiable) map.
     */
    public Map toMap() {
        return Collections.unmodifiableMap(backing);
    }

}
