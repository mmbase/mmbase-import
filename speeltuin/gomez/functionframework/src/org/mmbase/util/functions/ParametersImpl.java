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
 * @version $Id: ParametersImpl.java,v 1.1 2004-11-24 13:23:03 pierre Exp $
 * @see Parameter
 * @see #Parameters(Parameter[])
 */

public class ParametersImpl extends AbstractList implements Parameters {
    private static final Logger log = Logging.getLoggerInstance(ParametersImpl.class);

    /**
     * The contents of this List are stored in this HashMap.
     */
    protected Map backing = new HashMap();

    /**
     * This array maps integers (position in array) to map keys, making it possible to implement
     * List.
     */
    protected DataType[] definition = null;

    /**
     * If <code>true</code>, values are automatically cast to the right type (if possible) when set.
     */
    protected boolean autoCasting = false;

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
    public ParametersImpl(DataType[] def) {
        definition = (DataType[]) Functions.define(def, new ArrayList()).toArray(new Parameter[0]);
        // fill with default values, and check for non-unique keys.
        for (int i = 0; i < definition.length; i++) {
            if (backing.put(definition[i].getName(), definition[i].getDefaultValue()) != null) {
                throw new IllegalArgumentException("Parameter keys not unique");
            }

        }
    }

    /**
     * If you happen to have a List of parameters, then you can wrap it into an Parameters with this constructor.
     *
     * @throws NullPointerException if definition is null
     * @see #Parameters(Parameter[])
     */
    public ParametersImpl(DataType[] def, List values) {
        this(def);
        if (log.isDebugEnabled()) {
            if (values.size() > definition.length) {
                log.debug("Given too many values. " + values + " does not match " + Arrays.asList(definition));
            }
        }
        for (int i = 0; i < values.size(); i++) {
            set(i, values.get(i));
        }
    }

    public String toString() {
        StringBuffer buf = new StringBuffer("[");
        for (int i = 0; i < definition.length; i++) {
            if (i > 0) buf.append(", ");
            buf.append(definition[i]).append('=').append(get(i));
        }
        buf.append("]");
        return buf.toString();
    }

    public Class[] toClassArray() {
        Class[] array = new Class[definition.length];
        for (int i = 0; i < definition.length; i++) {
            array[i] = definition[i].getType();
        }
        return array;
    }

    /**
     * Sets the 'auto casting' property (which on default is false)
     * @see #isAutoCasting
     */
    public void setAutoCasting(boolean autocast) {
        autoCasting = autocast;
    }

    /**
     * Whether this Parameters object is 'automaticly casting'. If it is, that means that you can set e.g.
     * an Integer by a String.
     */
    public boolean isAutoCasting() {
        return autoCasting;
    }

    // implementation of List
    // @throws NullPointerException if definition not set
    public int size() {
        return definition.length;
    }

    // implementation of List
    // @throws NullPointerException if definition not set
    public Object get(int i) {
        return backing.get(definition[i].getName());
    }

    // implementation of (modifiable) List
    // @throws NullPointerException if definition not set
    public Object set(int i, Object value) {
        DataType a = definition[i];
        if (autoCasting) value = a.autoCast(value);
        a.checkType(value);
        return backing.put(a.getName(), value);
    }

    public void checkRequiredParameters() {
        for (int i = 0; i < definition.length; i++) {
            DataType a = definition[i];
            if (a.isRequired() && (get(a.getName()) == null)) {
                throw new IllegalArgumentException("Required parameter '" + a.getName() + "' is null");
            }
        }
    }

    // what about indexOf and contains ???
    // what should that check? value? or DataType/parametername?

    public int indexOfParameter(DataType parameter) {
        return Arrays.binarySearch(definition, parameter);
    }

    public int indexOfParameter(String parameterName) {
        int index = -1;
        for (int i = 0; index == -1 && i < definition.length; i++) {
            if (definition[i].getName().equals(parameterName)) {
                index = i;
            }
        }
        return index;
    }

    public boolean containsParameter(DataType parameter) {
        return indexOfParameter(parameter) != -1;
    }

    public boolean containsParameter(String parameterName) {
        return indexOfParameter(parameterName) != -1;
    }

    public Parameters set(DataType parameter, Object value) {
        int index = indexOfParameter(parameter);
        if (index > -1) {
            set(index,value);
            return this;
        } else {
            throw new IllegalArgumentException("The parameter '" + parameter + "' is not defined (defined are " + toString() + ")");
        }
    }

    public Parameters set(String parameterName, Object value) {
        int index = indexOfParameter(parameterName);
        if (index > -1) {
            set(index,value);
            return this;
        } else {
            throw new IllegalArgumentException("The parameter '" + parameterName + "' is not defined (defined are " + toString() + ")");
        }
    }

    public Parameters setAll(Map map) {
        Iterator i = map.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry entry = (Map.Entry) i.next();
            set((String) entry.getKey(), entry.getValue());
        }
        return this;
    }

    public Parameters setIfDefined(DataType parameter, Object value) {
        int index = indexOfParameter(parameter);
        if (index > -1) {
            set(index,value);
        }
        return this;
    }

    public Parameters setIfDefined(String parameterName, Object value) {
        int index = indexOfParameter(parameterName);
        if (index > -1) {
            set(index,value);
        }
        return this;
    }

    public Object get(DataType parameter) {
        return get(parameter.getName());
    }

    public Object get(String parameterName) {
        return backing.get(parameterName);
    }

    public String getString(DataType parameter) {
        return getString(parameter.getName());
    }

    public String getString(String parameterName) {
        return Casting.toString(get(parameterName));
    }

    public Map toMap() {
        return Collections.unmodifiableMap(backing);
    }

}
