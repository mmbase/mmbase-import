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
 * @version $Id: WrappedFunction.java,v 1.11 2006-09-08 18:34:12 michiel Exp $
 */
public abstract class WrappedFunction<R, E> implements Function<R, E> {

    protected Function<R, E> wrappedFunction;

    /**
     * Constructor for Basic Function
     * @param function The function to wrap
     */
    public WrappedFunction(Function<R, E> function) {
         wrappedFunction = function;
    }

    public Parameters<E> createParameters() {
        return wrappedFunction.createParameters();
    }

    public R getFunctionValue(Parameters<E> parameters) {
         return wrappedFunction.getFunctionValue(parameters);
    }

    public R getFunctionValueWithList(List<E> parameters) {
         if (parameters instanceof Parameters) {
             return getFunctionValue((Parameters<E>)parameters);
         } else {
             Parameters<E> params = wrappedFunction.createParameters().setAll(parameters);
             return getFunctionValue(params);
         }
    }
    public R getFunctionValue(E... parameters) {
        Parameters params = wrappedFunction.createParameters().setAll(parameters);
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

    public Parameter<E>[] getParameterDefinition() {
        return wrappedFunction.getParameterDefinition();
    }

    public void setParameterDefinition(Parameter<E>[] params) {
        wrappedFunction.setParameterDefinition(params);
    }

    public ReturnType getReturnType() {
        return wrappedFunction.getReturnType();
    }

    public void setReturnType(ReturnType type) {
        wrappedFunction.setReturnType(type);
    }

    public int hashCode() {
        return getName().hashCode();
    }
    public String toString() {
        return "WRAPPED " + getReturnType() + " " + getName() + java.util.Arrays.asList(getParameterDefinition());
    }


}
