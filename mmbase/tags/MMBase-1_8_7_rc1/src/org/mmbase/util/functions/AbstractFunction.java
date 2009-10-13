/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.functions;

import java.util.*;

/**
 * An abstract representation of a piece of functionality (a 'function'). A function has a name, a
 * return type, and a parameter-definition (which is a {@link Parameter} array).
 *
 * The goal of a Function object is to call its {@link #getFunctionValue(Parameters)} method, which
 * executes it, given the specified parameters.
 *
 *
 * @author Daniel Ockeloen
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8
 * @see Parameter
 * @see Parameters
 */
abstract public class AbstractFunction implements Function, Comparable, java.io.Serializable {

    protected String    name;
    protected ReturnType  returnType;

    private Parameter[] parameterDefinition;
    private String     description;

    /**
     * Constructor for Function objects.
     * @param name Every function must have a name
     * @param def  Every function must have a parameter definition. It can be left <code>null</code> and then filled later by {@link #setParameterDefinition}
     * @param returnType Every function must also specify its return type. It can be left <code>null</code> and then filled later by {@link #setReturnType}
     */
    public AbstractFunction(String name, Parameter[] def, ReturnType returnType) {
        this.name = name;
        if (def != null){
            this.parameterDefinition = (Parameter[]) Functions.define(def, new ArrayList()).toArray(Parameter.EMPTY);
        }
        this.returnType = returnType;
    }

    /**
     * Creates an empty 'Parameters'  object for you, which you have to fill and feed back to getFunctionValue
     * @see #getFunctionValue(Parameters)
     */
    public Parameters createParameters() {
        if (parameterDefinition == null) {
            throw new IllegalStateException("Definition is not set yet");
        }
        return new Parameters(parameterDefinition);
    }

    /**
     * Executes the defined function supplying the given arguments.
     * @see #createParameters
     * @param parameters The parameters for the function. To specify an empty parameter list use {@link Parameters#VOID}.
     *                   Implementors are encouraged to support <code>null</code> too.
     * @return The function value, which can be of any type compatible to {@link #getReturnType}
     */
    abstract public Object getFunctionValue(Parameters parameters);

    /**
     * Executes the defined function supplying the given List of arguments.
     * This is a convenience method, as the List is mapped to a Parameters type and passed to {@link  #getFunctionValue(Parameters)}.
     * @param parameters The parameters for the function. To specify an empty parameter list use {@link Parameters#VOID}.
     *
     * @return The function value, which can be of any type compatible to {@link #getReturnType}
     */
     public final Object getFunctionValueWithList(List parameters) {
         if (parameters instanceof Parameters) {
             return getFunctionValue((Parameters)parameters);
         } else {
             return getFunctionValue(new Parameters(parameterDefinition, parameters));
         }
     }

    /**
     * For documentational  purposes a function object needs a description too.
     */
    public void setDescription(String description)   {
        this.description = description;
    }

    /**
     * @see #setDescription(String)
     */
    public String getDescription() {
        return description;
    }

    /**
     * A function <em>must</em> have a name. This is the name which was used to aquire the function object.
     * @return The function's name, never <code>null</code>
     */
    public String getName() {
        return name;
    }

    /**
     * @return The currently set Parameter definition array, or <code>null</code> if not set already.
     */
    public Parameter[] getParameterDefinition() {
        return parameterDefinition;
    }

    /**
     * A function object is of no use, as long as it lacks a definition.
     * @param params An array of Parameter objects.
     * @throws IllegalStateException if there was already set a parameter defintion for this function object.
     */
    public void setParameterDefinition(Parameter[] params) {
        if (parameterDefinition != null) {
            throw new IllegalStateException("Definition is set already");
        }
        parameterDefinition =  (Parameter[]) Functions.define(params, new ArrayList()).toArray(Parameter.EMPTY);
    }


    /**
     * @return The currently set ReturnType, or <code>null</code> if not set already.
     */
    public ReturnType getReturnType() {
        return returnType;
    }
    /**
     * Sets the ReturnType for this function if not set already.
     * @param type A ReturnType object. For void functions that could be {@link ReturnType#VOID}.
     * @throws IllegalStateException if there was already set a return type for this function object.
     */
    public void setReturnType(ReturnType type) {
        if (returnType != null) {
            throw new IllegalStateException("Returntype is set already");
        }
        returnType = type;
    }

    public int compareTo(Object o) {
        Function fun = (Function) o;
        return name.compareTo(fun.getName());
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null) return false;
        return (o instanceof Function) && ((Function)o).getName().equals(name);
    }


    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return name.hashCode();
    }

    public String toString() {
        return "" + returnType + " " + getName() + Arrays.asList(parameterDefinition);
    }

}
