/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.functions;

import java.util.*;
import org.mmbase.util.Casting;

/**
 * Entry for Parameters. A (function) argument is specified by a name and type.
 *
 * @author Michiel Meeuwissen
 * @author Daniel Ockeloen (MMFunctionParam)
 * @since  MMBase-1.7
 * @version $Id: Parameter.java,v 1.5 2004-02-02 14:25:22 michiel Exp $
 * @see Parameters
 */

public class Parameter {

    /**
     * Parameter which might be needed in lots of Parameter definitions.
     */
    public static final Parameter LANGUAGE = new Parameter("language", String.class);
    public static final Parameter USER     = new Parameter("user",     org.mmbase.bridge.User.class);
    public static final Parameter RESPONSE = new Parameter("response", javax.servlet.http.HttpServletResponse.class);
    public static final Parameter REQUEST  = new Parameter("request",  javax.servlet.http.HttpServletRequest.class);

    // package for Parameters (direct access avoids function calls)
    String key;
    Class type;
    String description  = "";
    Object defaultValue = null;
    boolean required  = false;

    protected Parameter() {}

    /**
     * Create a Parameter object
     * @param name the name of the parameter
     * @param type the class of the parameter's possible value
     */
    public Parameter(String name, Class type) {
        this.key = name;
        this.type = type;
    }

    /**
     * Create a Parameter object
     * @param name the name of the parameter
     * @param type the class of the parameter's possible value
     * @param required whether the parameter requires a value (default is <code>false</code>)
     */
    public Parameter(String name, Class type, boolean required) {
        this.key = name;
        this.type = type;
        this.required = required;
    }

    /**
     * Create a Parameter object
     * @param name the name of the parameter
     * @param type the class of the parameter's possible value
     * @param defaultValue the value to use if the parameter has no value set (default is <code>null</code>)
     */
    public Parameter(String name, Class type, Object defaultValue) {
        this.key = name;
        this.type = type;
        this.required = required;
        setDefaultValue(defaultValue);
    }

    /**
     * Returns the name or 'key' of this parameter.
     * @return the name as a String
     */
    public String getName () {
        return key;
    }

    /**
     * Returns the description of this parameter.
     * @return the description as a String
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of this parameter.
     * @param description the description as a String
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the default value of this parameter.
     * @return the default value
     */
    public Object getDefaultValue() {
        return defaultValue;
    }

    /**
     * Sets the default value of this parameter.
     * @param def the default value
     */
    public void setDefaultValue(Object def) {
        defaultValue = def;
    }

    /**
     * Returns the type of values that this parameter accepts.
     * @return the type as a Class
     */
    public Class getType() {
        return type;
    }

    /**
     * Returns whether the parameter requires a value.
     * @return <code>true</code> if a value is required
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * Checks if the passed object is of the correct class (compatible with the type of this Parameter),
     * and throws an IllegalArgumentException if it doesn't.
     * @param value teh value whose type (class) to check
     * @throws IllegalArgumentException if the type is not compatible
     */
    public void checkType(Object value) {
        if (type.isPrimitive()) {
            // sigh
            if (type.equals(Boolean.TYPE) && value instanceof Boolean) return;
            if (type.equals(Byte.TYPE)   && value instanceof Byte) return;
            if (type.equals(Character.TYPE) && value instanceof Character) return;
            if (type.equals(Short.TYPE) && value instanceof Short) return;
            if (type.equals(Integer.TYPE) && value instanceof Integer) return;
            if (type.equals(Long.TYPE) && value instanceof Long) return;
            if (type.equals(Float.TYPE) && value instanceof Float) return;
            if (type.equals(Double.TYPE) && value instanceof Double) return;
        }
        if (! type.isInstance(value)) {
            throw new IllegalArgumentException("Parameter '" + value + "' must be of type " + type + " (but is " + (value == null ? value : value.getClass()) + ")");
        }
    }


    /**
     * Tries to 'cast' an object for use with this parameter. E.g. if value is a String, but this
     * parameter is of type Integer, then the string can be parsed to Integer.
     * @param value The value to be filled in in this Parameter.     
     */
    protected Object autoCast(Object value) {        
        if (type.equals(Integer.class)) {
            return Casting.toInteger(value);
        } else if (type.equals(int.class)) {
            return Casting.toInteger(value);
        } else if (type.equals(String.class)) {
            return Casting.toString(value);
        } else if (type.equals(List.class)) {
            return Casting.toList(value);
        } else {
            // don't know
            return value;
        }
    }


    public boolean equals(Object o) {
        if (o instanceof Parameter) {
            Parameter a = (Parameter) o;
            return a.key.equals(key) && a.type.equals(type);
        }
        return false;
    }

    public String toString() {
        return type.getName() + " " + key;
    }

    /**
     * A Parameter.Wrapper wraps one Parameter around a Parameter[]
     * (then you can put it in a Parameter[]).  Parameters will
     * recognize this.
     */
    public static class Wrapper extends Parameter {
        Parameter[] arguments;

        public Wrapper(Parameter[] arg) {
            key = "[ARRAYWRAPPER]";
            arguments = arg;
            type = Parameter[].class; // should not remain null, (equals of Parameter depends on that)
        }

        public String toString() {
            return "WRAPPED" + Arrays.asList(arguments).toString();
        }
    }

}
