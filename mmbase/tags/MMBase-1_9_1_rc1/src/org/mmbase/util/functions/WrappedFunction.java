/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.functions;

import java.util.List;

/**
 * A wrapped function is a base class for function objects based on an other function object.
 *
 * @since MMBase-1.8
 * @author Pierre van Rooden
 * @version $Id$
 */
public abstract class WrappedFunction<R> implements Function<R> {

    protected Function<R> wrappedFunction;

    /**
     * Constructor for Basic Function
     * @param function The function to wrap
     */
    public WrappedFunction(Function<R> function) {
         wrappedFunction = function;
    }

    public Parameters createParameters() {
        return wrappedFunction.createParameters();
    }

    public R getFunctionValue(Parameters parameters) {
         return wrappedFunction.getFunctionValue(parameters);
    }

    public R getFunctionValueWithList(List<?> parameters) {
         if (parameters instanceof Parameters) {
             return getFunctionValue((Parameters)parameters);
         } else {
             Parameters params = wrappedFunction.createParameters();
             params.setAutoCasting(true);
             params.setAll(parameters);
	     return getFunctionValue(params);
         }
    }
    public R getFunctionValue(Object... parameters) {
        Parameters params = wrappedFunction.createParameters();
        params.setAutoCasting(true);
        params.setAll(parameters);
        return getFunctionValue(params);
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

    public Parameter<?>[] getParameterDefinition() {
        return wrappedFunction.getParameterDefinition();
    }

    public void setParameterDefinition(Parameter<?>[] params) {
        wrappedFunction.setParameterDefinition(params);
    }

    public ReturnType<R> getReturnType() {
        return wrappedFunction.getReturnType();
    }

    public void setReturnType(ReturnType<R> type) {
        wrappedFunction.setReturnType(type);
    }

    public int hashCode() {
        return getName().hashCode();
    }
    public String toString() {
        return "WRAPPED " + wrappedFunction.toString();
    }


}
