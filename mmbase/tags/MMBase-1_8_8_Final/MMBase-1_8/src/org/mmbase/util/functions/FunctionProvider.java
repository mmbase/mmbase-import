/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package org.mmbase.util.functions;

import java.util.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * A function provider maintains a set of {@link Function} objects.
 *
 * @since MMBase-1.8
 * @author Pierre van Rooden
 * @version $Id$
 */
public abstract class FunctionProvider {
    private static final Logger log = Logging.getLoggerInstance(FunctionProvider.class);

    protected Map functions = Collections.synchronizedMap(new HashMap());
    /**
     * Every Function Provider provides least the 'getFunctions' function, which returns a Set of all functions which it provides.
     */
    protected Function getFunctions = new AbstractFunction("getFunctions", Parameter.EMPTY, ReturnType.COLLECTION) {
            {
                setDescription("The 'getFunctions' returns the collections of al Function object which are available on this FunctionProvider");
            }
            public Object getFunctionValue(Parameters arguments) {
                return getFunctions();
            }
        };
    {
        addFunction(getFunctions);
    }


    /**
     * The constructor of an FunctionProvider two things. It adds the 'getFunction' function, and it
     * guesses other function using reflection.
     * @todo Should this last thing not only be done on MMObjectBuilders?
     */
    public FunctionProvider() {
        // determine parameters through reflection
        Map parameterDefinitions =  Functions.getParameterDefinitonsByReflection(this.getClass(), new HashMap());
        try {
            for (Iterator i = parameterDefinitions.entrySet().iterator(); i.hasNext(); ) {
                Map.Entry entry = (Map.Entry) i.next();
                Function fun = newFunctionInstance((String)entry.getKey(), (Parameter[])entry.getValue(), ReturnType.UNKNOWN);
                fun.setDescription("Function automaticly found by reflection on public Parameter[] members");
                addFunction(fun);
            }
        } catch (UnsupportedOperationException uoo) {
            log.warn("Found parameter definition array in " + this.getClass() + " but newFunctionInstance was not implemented for that");
        }
    }

    protected  Function newFunctionInstance(String name, Parameter[] parameters, ReturnType returnType) {
        throw new UnsupportedOperationException("This class is not a fully implemented function-provider");
    }


    /**
     * Adds a function to the FunctionProvider. So, you can implement any function and add it to the
     * provider, to make it provide this function too.
     */
    public void addFunction(Function function) {
        Object oldValue = functions.put(function.getName(), function);
        if (oldValue != null) {
            log.debug("Replaced " + oldValue + " by " + function);
        }
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
     * Returns the Function object with given name.
     * @return Function object or <code>null</code> if no such function is provided.
     */
    public Function getFunction(String functionName) {
        return (Function)functions.get(functionName);
    }

    /**
     * Returns a Collection of all functions currently provided by the FunctionProvider.
     */
    public Collection getFunctions() {
        return Collections.unmodifiableCollection(functions.values());
    }

}
