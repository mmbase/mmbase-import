/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.functions;

import org.mmbase.bridge.DataType;
import java.util.List;

/**
 * A wrapped function is a base class for function objects based on an other function object.
 *
 * @since MMBase-1.8
 * @author Pierre van Rooden
 * @version $Id: WrappedFunction.java,v 1.6 2005-06-09 21:27:56 michiel Exp $
 */
public abstract class WrappedFunction implements Function {

    protected Function wrappedFunction;

    /**
     * Constructor for Basic Function
     * @param cloud The user's cloud
     * @param function The function to wrap
     */
    public WrappedFunction(Function function) {
         wrappedFunction = function;
    }

    public Parameters createParameters() {
        return wrappedFunction.createParameters();
    }

    public Object getFunctionValue(Parameters parameters) {
         return wrappedFunction.getFunctionValue(parameters);
    }

    public Object getFunctionValueWithList(List parameters) {
         if (parameters instanceof Parameters) {
             return getFunctionValue((Parameters)parameters);
         } else {
             Parameters params = wrappedFunction.createParameters().setAll(parameters);
             return getFunctionValue(params);
         }
    }

    public void setDescription(String description) {
        wrappedFunction.setDescription(description);
    }

    public String getDescription() {
        return wrappedFunction.getDescription();
    }

    public String getName() {
        return wrappedFunction.getName();
    }

    public DataType[] getParameterDefinition() {
        return wrappedFunction.getParameterDefinition();
    }

    public void setParameterDefinition(DataType[] params) {
        wrappedFunction.setParameterDefinition(params);
    }

    public DataType getReturnType() {
        return wrappedFunction.getReturnType();
    }

    public void setReturnType(DataType type) {
        wrappedFunction.setReturnType(type);
    }

    public int hashCode() {
        return getName().hashCode();
    }
    public String toString() {
        return "WRAPPED" + getReturnType() + " " + getName() + java.util.Arrays.asList(getParameterDefinition());
    }


}
