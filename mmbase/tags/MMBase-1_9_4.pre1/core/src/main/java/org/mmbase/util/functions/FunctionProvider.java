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

    protected Map<String, Function<?>> functions = Collections.synchronizedMap(new HashMap<String, Function<?>>());
    /**
     * Every Function Provider provides least the 'getFunctions' function, which returns a Set of all functions which it provides.
     */
    protected Function<Collection<Function<?>>> getFunctions = new AbstractFunction<Collection<Function<?>>>("getFunctions") {
            {
                setDescription("The 'getFunctions' returns the collections of al Function object which are available on this FunctionProvider");
            }
            public Collection<Function<?>> getFunctionValue(Parameters arguments) {
                return getFunctions();
            }
        };
    {
        addFunction(getFunctions);
    }


    /**
     * The constructor of an FunctionProvider  guesses the functions using reflection.
     * @todo Should this last thing not only be done on MMObjectBuilders?
     */
    public FunctionProvider() {
        // determine parameters through reflection
        Map<String, Parameter<?>[]> parameterDefinitions =  Functions.getParameterDefinitonsByReflection(this.getClass(), new HashMap<String, Parameter<?>[]>());
        try {
            for (Map.Entry<String, Parameter<?>[]> entry : parameterDefinitions.entrySet()) {
                Function<?> fun = newFunctionInstance(entry.getKey(), entry.getValue(), ReturnType.UNKNOWN);
                fun.setDescription("Function automaticly found by reflection on public Parameter[] members");
                addFunction(fun);
            }
        } catch (UnsupportedOperationException uoo) {
            log.warn("Found parameter definition array in " + this.getClass() + " but newFunctionInstance was not implemented for that");
        }
    }

    protected  Function<?> newFunctionInstance(String name, Parameter<?>[] parameters, ReturnType returnType) {
        throw new UnsupportedOperationException("This class is not a fully implemented function-provider");
    }


    /**
     * Adds a function to the FunctionProvider. So, you can implement any function and add it to the
     * provider, to make it provide this function too.
     * @return The function previously assigned with this name or <code>null</code> if no such function.
     */
    public Function<?> addFunction(Function<?> function) {
        Function<?> oldValue = functions.put(function.getName(), function);
        if (oldValue != null) {
            log.debug("Replaced " + oldValue + " by " + function);
        }
        return oldValue;
    }

    /**
     * Creates a new empty Parameters object for given function.
     * @return A new empty Parameters object, or <code>null</code> if no such function.
     */
    public Parameters createParameters(String functionName) {
        Function<?> function = getFunction(functionName);
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
    public Object getFunctionValue(String functionName, List<?> parameters) {
        Function<?> function = getFunction(functionName);
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
    public Function<?> getFunction(String functionName) {
        return functions.get(functionName);
    }

    /**
     * Returns a Collection of all functions currently provided by the FunctionProvider.
     */
    public Collection<Function<?>> getFunctions() {
        return Collections.unmodifiableCollection(functions.values());
    }

}
