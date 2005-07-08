/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.functions;

import java.util.*;


/**
 * Parameters for functions, a way to make variable arguments in Java.
 * A Parameters object presents the actual parameters in a function call, and its most important
 * use is in {@link Function#getFunctionValue(Parameters)}.
 *
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @since  MMBase-1.8
 * @version $Id: Parameters.java,v 1.17 2005-07-08 12:23:46 pierre Exp $
 */

public interface Parameters extends List {
    /**
     * No need to bother for the functions with no parameters. This is a constant you could supply.
     */
    public static final Parameters VOID = new ParametersImpl(Parameter.EMPTY);

    /**
     * When using reflection, you might need the Parameters as a Class[]. This function provides it.
     */
    public Class[] toClassArray();

    public Parameter[] getDefinition();

    /**
     * Sets the 'auto casting' property (which on default is false)
     * @param autocast the new value for autocast
     * @see #isAutoCasting
     */
    public void setAutoCasting(boolean autocast);

    /**
     * Whether this Parameters object is 'automaticly casting'. If it is, that means that you can set e.g.
     * an Integer by a String.
     * @return <code>true</code> if autocasting is on
     */
    public boolean isAutoCasting();

    /**
     * Throws an IllegalArgumentException if one of the required parameters was not entered.
     */
    public void checkRequiredParameters();

    /**
     * Returns the position of a parameter in the parameters list, using the Parameter as a qualifier.
     * you can tehn acecss that paramter with {@link #get(int)}.
     * @param parameter the parameter
     * @return the index of the parameter, or -1 if it doesn't exist
     */
    public int indexOfParameter(Parameter parameter);

    /**
     * Returns the position of a parameter in the parameters list, using the parameter name as a qualifier.
     * you can tehn acecss that paramter with {@link #get(int)}.
     * @param parameterName the name of the parameter
     * @return the index of the parameter, or -1 if it doesn't exist
     */
    public int indexOfParameter(String parameterName);

    /**
     * Checks wether a certain parameter is available, using the Parameter as a qualifier.
     * @param parameter the parameter
     * @return <code>true</code> if a parameter exists.
     */
    public boolean containsParameter(Parameter parameter);

    /**
     * Checks wether a certain parameter is available, using the parameter name as a qualifier.
     * @param parameterName the name of the parameter
     * @return <code>true</code> if a parameter exists.
     */
    public boolean containsParameter(String parameterName);

    /**
     * Sets the value of a parameter.
     * @param parameter the Parameter describing the parameter to set
     * @param value the object value to set
     * @throws IllegalArgumentException if either the argument name is unknown to this Parameters, or the value is of the wrong type.
     */
    public Parameters set(Parameter parameter, Object value);

    /**
     * Sets the value of a parameter.
     * @param parameterName the name of the parameter to set
     * @param value the object value to set
     * @throws IllegalArgumentException if either the argument name is unknown to this Parameters, or the value is of the wrong type.
     */
    public Parameters set(String parameterName, Object value);

    /**
     * Copies all values of a map to the corresponding values of this Parameters Object.
     */
    public Parameters setAll(Map map);
    /**
     * Copies all values of a collection to the corresponding values of this Parameters Object.
     */
    public Parameters setAll(Collection list);

    /**
     * Sets the value of an argument, if the argument is defined, otherwise do nothing.
     * @param parameter the parameter to set
     * @param value the object value to set
     */
    public Parameters setIfDefined(Parameter parameter, Object value);

    /**
     * Sets the value of an argument, if the argument is defined, otherwise do nothing.
     * @param parameterName the name of the parameter to set
     * @param value the object value to set
     */
    public Parameters setIfDefined(String parameterName, Object value);

    /**
     * Gets the value of a parameter.
     * @param parameter the parameter to get
     * @return value the parameter value
     */
    public Object get(Parameter parameter);

    /**
     * Gets the value of a parameter.
     * @param parameterName the name of the parameter to get
     * @return value the parameter value
     */
    public Object get(String parameterName);

    /**
     * Gets the value of a parameter, cast to a String.
     * @param parameter the parameter to get
     * @return value the parameter value as a <code>STring</code>
     */
    public String getString(Parameter parameter);

    /**
     * Gets the value of a parameter, cast to a String.
     * @param parameterName the name of the parameter to get
     * @return value the parameter value as a <code>STring</code>
     */
    public String getString(String parameterName);

    /**
     * Gives the arguments back as a (unmodifiable) map.
     */
    public Map toMap();

}
