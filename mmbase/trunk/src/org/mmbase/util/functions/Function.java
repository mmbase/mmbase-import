/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.functions;


//import org.mmbase.util.logging.*;


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
 * @version $Id: Function.java,v 1.3 2004-11-02 18:35:31 michiel Exp $
 * @since MMBase-1.7
 * @see Parameter
 * @see Parameters
 */
abstract public class Function {

    //private static final Logger log = Logging.getLoggerInstance(Function.class);
    
    protected String      name;
    protected ReturnType  returnType;

    private Parameter[] parameterDefinition;
    private String     description;


    /**
     * Constructor for Function objects.
     * @param name Every function must have a name
     * @param def  Every function must have a parameter definition. It can be left <code>null</code> and then filled later by {@link #setParameterDefinition}
     * @param returnType Every function must also specify its return type. It can be left <code>null</code> and then filled later by {@link #setReturnType}
     */
    protected Function(String name, Parameter[] def, ReturnType returnType) {
        this.name = name;
        this.parameterDefinition = def;
        this.returnType = returnType;
    }
    /**
     * Creates an empty 'Parameters'  object for you, which you have to fill and feed back to getFunctionValue
     * @see #getFunctionValue(Parameters)
     */
    public Parameters getNewParameters() {
        if (parameterDefinition == null) {
            throw new IllegalStateException("Definition is not set yet");
        }
        return new Parameters(parameterDefinition);
    }

    /**
     * Executes the defined function supplying the given arguments.
     * @see #getNewParameters
     * @param parameters Of course, a function needs parameters when it is executed. To safely
     *                   specify no-parameters you're encouraged to supply {@link Parameters#VOID}. On the contrary
     *                  implementors are encouraged to support <code>null</code> too.
     *
     * @return The function value, which can be of any type (predictable though, by {@link #getReturnType})
     */

    abstract public Object getFunctionValue(Parameters parameters); 
    /*
    {
        throw new UnsupportedOperationException("This is only an abstract representation of a function with name and cannot be actually executed. Use an extension of this class if you want that.");
    }
    */


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
        parameterDefinition = params;
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
    

}
