/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.functions;


import org.mmbase.util.logging.*;


/**
 * An abstract representation of a piece of functionality.
 *
 * @author Daniel Ockeloen
 * @author Michiel Meeuwissen
 * @version $Id: MMFunction.java
 */
abstract public class Function {

    private static final Logger log = Logging.getLoggerInstance(Function.class);

    protected String      name;
    protected ReturnType  returnType;

    private Parameter[] parameterDefinition;
    private String     description;


    protected Function(String name, Parameter[] def, ReturnType returnType) {
        this.name = name;
        this.parameterDefinition = def;
        this.returnType = returnType;
    }
    /**
     * Creates an empty 'Parameters'  object for you, which you have to fill and feed back to getFunctionValue
     * @see #getFunctionValue
     */

    public Parameters getNewParameters() {
        return new Parameters(parameterDefinition);
    }

    /**
     * Executes the defined function supplying the given arguments.
     * @see #getNewParameters
     */

    abstract public Object getFunctionValue(Parameters arguments);

    public void setDescription(String description)   { 
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }
    public Parameter[] getParameterDefinition() {
        return parameterDefinition;
    }
    public ReturnType getReturnType() {
        return returnType;
    }
    

}
