/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package org.mmbase.util.functions;

import java.util.*;

/**
 * A function provider maintains a set of {@link Function} objects.
 *
 * @since MMBase-1.8
 * @author Pierre van Rooden
 * @version $Id: FunctionProvider.java,v 1.6 2005-05-10 23:00:44 michiel Exp $
 */
public class FunctionProvider {

    /**
     * Every Function Provider provides least the 'getFunctions' function, which returns a Set of all functions which it provides.
     */
    protected Function getFunctions = new AbstractFunction("getFunctions", Parameter.EMPTY, ReturnType.SET) {
            {
                setDescription("The 'getFunctions' returns the collections of al Function object which are available on this FunctionProvider");
            }
            public Object getFunctionValue(Parameters arguments) {
                return getFunctions();
            }
        };

    protected Map functions = Collections.synchronizedMap(new HashMap());

    /**
     * The constructor of an FunctionProvider two things. It adds the 'getFunction' function, and it
     * guesses other function using reflection.
     * @todo Should this last thing not only be done on MMObjectBuilders?
     */
    public FunctionProvider() {
        // determine parameters through reflection
        Map parameterDefinitions =  Functions.getParameterDefinitonsByReflection(this.getClass(), new HashMap());
        for (Iterator i = parameterDefinitions.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry entry = (Map.Entry) i.next();
            Function fun = newFunctionInstance((String)entry.getKey(), (Parameter[])entry.getValue(), ReturnType.UNKNOWN);
            fun.setDescription("Function automaticly found by reflection on public Parameter[] members");
            addFunction(fun);
        }
        addFunction(getFunctions);
    }

    /**
     * @javadoc
     */
    protected Function newFunctionInstance(String name, Parameter[] parameters, ReturnType returnType) {
        return new ProviderFunction(name, parameters, returnType, this);
    }

    /**
     * Adds a function to the FunctionProvider. So, you can implement any function and add it to the
     * provider, to make it provide this function too.
     */
    public void addFunction(Function function) {
        functions.put(function.getName(), function);
    }

    /**
     * Creates a new empty Parameters object for given function.
     * @return A new empty Parameters object, or <code>null</code> if no such function.
     */
    public Parameters createParameters(String functionName) {
        Function function = getFunction(functionName);
        if (function != null) {
            return function.createParameters();
        } else {
            return null;
        }
    }

    /**
     * Executes a function, and returns the function value.
     * @return The function value or <code>null</code> if no such function.
     */
    public Object getFunctionValue(String functionName, List parameters) {
        Function function = getFunction(functionName);
        if (function != null) {
            return function.getFunctionValueWithList(parameters);
        } else {
            return null;
        }
    }

    /**
     * What's this??
     * @javadoc
     */
    protected Object executeFunction(String functionName, Parameters parameters) {
        return null;
    }

    /**
     * Returns the Function object with given name.
     * @return Function object or <code>null</code> if no such function is provided.
     */
    public Function getFunction(String functionName) {
        return (Function)functions.get(functionName);
    }

    /**
     * Return a Set of all functions currently provided by the FunctionProvider.
     */
    public Set getFunctions() {
        // return new UnmodifiableSet(functions.values());
        Set set = new HashSet(functions.values());
        set.remove(null); // remove null values --> WHY?
        return set;
    }
}
