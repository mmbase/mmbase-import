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
 * Parameters for functions, a way to make variable arguments in Java. In fact this class does
 * nothing more then providing a convenient way to create a List, by the use of 'named
 * parameters'. This List is therefore backed by a HashMap, but it behaves as a list. So if you set
 * a parameter with a certain name, it always appears in the same location of the List.
 *
 * This List is modifiable but not resizeable. It is always the size of the definition array.
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.7
 * @version $Id: Parameters.java,v 1.3 2004-01-26 09:25:05 pierre Exp $
 * @see Parameter
 */

public class Parameters extends AbstractList implements List  {
    private static final Logger log = Logging.getLoggerInstance(Parameters.class);


    public static final Parameters VOID = new Parameters(new Parameter[0]);


    /**
     * Converts a certain List to an Parameters if it is not already one.
     */
    public static Parameters get(Parameter[] def, List args) {
        Parameters a;
        if (args instanceof Parameters) {
            a = (Parameters) args;
            if ( ! Arrays.equals(a.definition, def))  throw new IllegalArgumentException("Given parameters '" + args + "' has other definition. ('" + Arrays.asList(a.definition) + "')' incompatible with '" + Arrays.asList(def) + "')");
        } else {
            a = new Parameters(def, args);
        }
        return a;
    }

    /**
     * The contents of this List are stored in this HashMap.
     */
    protected Map backing = new HashMap();

    /**
     * This array maps integers (position in array) to map keys, making it possible to implement
     * List.
     */
    protected  Parameter[] definition = null;


    Parameters() {
    }

    /**
     * Constructor, taking an Parameter[] array argument. The Parameter may also be Parameter.Wrapper
     * (to impelmente overriding of functions).  The idea is that these array arguments are defined
     * as constants in the classes which define a function with variable arguments.
     */

    public Parameters(Parameter [] def) {
        definition = (Parameter []) define(def, new ArrayList()).toArray(new Parameter[0]);
        // fill with default values, and check for non-unique keys.
        for (int i = 0; i < definition.length; i++) {
            if (backing.put(definition[i].key, definition[i].defaultValue) != null) {
                throw new IllegalArgumentException("Parameter keys not unique");
            }

        }
    }
    /**
     * If you happen to have a List of parameters, then you can wrap it into an Parameters with this constructor
     * @throws NullPointerException if definition is null
     */
    public Parameters(Parameter [] def, List values) {
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


    /**
     * When using reflection, you might need the Parameters as a Class[]. This function provides it.
     * @throws NullPointerException if definition is null
     */
    public Class[] toClassArray() {
        Class[] array = new Class[definition.length];
        for (int i = 0; i < definition.length; i++) {
            array[i] = definition[i].getType();
        }
        return array;
    }

    /**
     * Adds the definitions to a List. Resolves the Attribute.Wrapper's (recursively).
     * @return List with only simple Parameter's.
     */
    protected static List define(Parameter[] def, List list) {
        for (int i = 0; i < def.length; i++) {
            if (def[i] instanceof Parameter.Wrapper) {
                define(((Parameter.Wrapper) def[i]).arguments, list);
            } else {
                list.add(def[i]);
            }
        }
        return list;
    }


    // implementation of List
    // @throws NullPointerException if definition not set
    public int size() {
        return definition.length;
    }

    // implementation of List
    // @throws NullPointerException if definition not set
    public Object get(int i) {
        return backing.get(definition[i].key);
    }

    // implementation of (modifiable) List
    // @throws NullPointerException if definition not set
    public Object set(int i, Object value) {
        Parameter a = definition[i];
        a.checkType(value);
        return backing.put(a.key, value);
    }

    /**
     * Throws IllegalArgumentException if one of the required parameters is null.
     */
    public void checkRequiredParameters() {
        for (int i = 0; i < definition.length; i++) {
            Parameter a = definition[i];
            if (a.isRequired() && (get(a.getName()) == null)) {
                throw new IllegalArgumentException("Required parameter '" + a.getName() + "' is null");
            }
        }
    }

    /**
     * Checks wether a certain parameter is available.
     */
    public boolean hasParameter(Parameter arg) {
        for (int i = 0; i < definition.length; i++) {
            Parameter a = definition[i];
            if (a.equals(arg)) return true;
        }
        return false;
    }


    /**
     * Sets the value of an argument.
     * @throws IllegalArgumentException if either the argument name is unknown to this Parameters, or the value is of the wrong type.
     * @throws NullPointerException if definition not set
     */
    public Parameters set(String arg, Object value) {
        for (int i = 0; i < definition.length; i++) {
            Parameter a = definition[i];
            if (a.key.equals(arg)) {
                if (a.isType(value)) {
                    backing.put(arg, value);
                } else {
                    if (value instanceof String) {
                        if (a.getType().equals(Integer.class)) {
                            try {
                                backing.put(arg, new Integer(Integer.parseInt((String)value)));
                            } catch(Exception e) {
                                throw new IllegalArgumentException("Parameter '" + value + "' must be of type a real number to autocast from String to Integer");
                            }
                        } else if (a.getType().equals(int.class)) {
                            try {
                                backing.put(arg, new Integer(Integer.parseInt((String)value)));
                            } catch(Exception e) {
                                throw new IllegalArgumentException("Parameter '" + value + "' must be of type a real number to autocast from String to int");
                            }
                        }
                    } else {
                        throw new IllegalArgumentException("Parameter '" + value + "' must be of type " + a.getType() + " (but is " + (value == null ? value : value.getClass()) + ")");
                    }
                }
                return this;
            }
        }
        throw new IllegalArgumentException("The parameter '" + arg + "' is not defined");
    }

    public Parameters set(Parameter arg, Object value) {
        return set(arg.getName(), value);
    }

    public Parameters setAll(Map map) {
        Iterator i = map.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry entry = (Map.Entry) i.next();
            set((String) entry.getKey(), entry.getValue());
        }
        return this;
    }

    /**
     * Sets the value of an argument, if the argument is defined, otherwise do nothing.
     * @throws NullPointerException if definition not set
     */

    public Parameters setIfDefined(String arg, Object value) {
        for (int i = 0; i < definition.length; i++) {
            Parameter a = definition[i];
            if (a.key.equals(arg)) {
                if (! a.type.isInstance(value)) break;
                backing.put(arg, value);
                return this;
            }
        }
        return this;
    }

    public Object get(String arg) {
        return backing.get(arg);
    }
    public String getString(String arg) {
        return Casting.toString(get(arg));
    }

    /**
     * Gives the arguments back as a (unmodifiable) map.  Parameters
     * cannot implement Map itself because 'remove' of List and Map
     * exclude.
     *
     * Remark: Could return a modifiable map, but then need to make a
     * wrapper implementation.
     */

    public Map toMap() {
        return Collections.unmodifiableMap(backing);
    }


}
