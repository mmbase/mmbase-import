/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.functions;

import org.mmbase.util.Casting;
import java.util.*;
//import org.mmbase.util.logging.*;

/**
 * Arguments for functions, a way to make variable arguments in Java. In fact this class does
 * nothing more then providing a convenient way to create a List. This List is backed by a HashMap.
 *
 * This List is modifiable but not resizeable. It is always the size of the definition array.
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.7
 * @version $Id: Parameters.java,v 1.1 2003-11-21 14:27:29 michiel Exp $
 * @see Parameter
 */

public class Parameters extends AbstractList implements List  {
    //private static Logger log = Logging.getLoggerInstance(Parameters.class);



    /**
     * Converts a certain List to an Parameters if it is not already one.
     */
    public static Parameters get(Parameter[] def, List args) {
        Parameters a;
        if (args instanceof Parameters) {
            a = (Parameters) args;
            if (a.definition != def) throw new IllegalArgumentException("Given arguments has other difinition.");
        } else {
            a = new Parameters(def, args);
        }
        return a;
    }

    /**
     * The contents of this List are stored in this HashMap.
     */
    private Map backing = new HashMap();

    /**
     * This array maps integers (position in array) to map keys, making it possible to implement
     * List.
     */
    private   Parameter[] definition;

    /**
     * Constructor, taking an Parameter[] array argument. The Parameter may also be Parameter.Wrapper
     * (to impelmente overriding of functions).  The idea is that these array arguments are defined
     * as constants in the classes which define a function with variable arguments.
     */

    public Parameters(Parameter [] def) {
        definition = (Parameter []) define(def, new ArrayList()).toArray(new Parameter[0]);
        // fill with nulls, and check for non-unique keys.
        for (int i = 0; i < definition.length; i++) {
            if (backing.put(definition[i].key, definition[i].defaultValue) != null) {
                throw new IllegalArgumentException("Parameter keys not unique");
            } 
            
        }
    }
    /**
     * If you happen to have a List of arguments, then you can wrap it into an Parameters with this constructor
     */
    public Parameters(Parameter [] def, List values) {
        this(def);
        if (values.size() > definition.length) throw new IllegalArgumentException("Given too many values. " + values + " does not match " + Arrays.asList(definition).toString());
        for (int i = 0; i < values.size(); i++) {
            set(i, values.get(i));
        }
    }
    

    /**
     * Adds the definitions to a List. Resolves the Attribute.Wrapper's (recursively).
     * @return List with only simple Parameter's.
     */
    protected List define(Parameter[] def, List list) {        
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
    public int size() {
        return definition.length;
    }

    // implementation of List
    public Object get(int i) {
        return backing.get(definition[i].key);
    }

    // implementation of (modifiable) List
    public Object set(int i, Object value) {
        Parameter a = definition[i];
        checkType(a, value);
        return backing.put(a.key, value);
    }


    protected void checkType(Parameter a, Object value) {
        if (! a.type.isInstance(value)) {
            throw new IllegalArgumentException("Parameter '" + value + "' must be of type " + a.type + " (but is " + (value == null ? value : value.getClass()) + ")");
        }
    }


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
     */
    public Parameters set(String arg, Object value) {
        for (int i = 0; i < definition.length; i++) {
            Parameter a = definition[i];
            if (a.key.equals(arg)) {
                checkType(a, value);
                backing.put(arg, value);
                return this;
            }
        }
        throw new IllegalArgumentException("The argument '" + arg + "' is not defined");
    }

    /**
     * Sets the value of an argument, if the argument is defined, otherwise do nothing.
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
