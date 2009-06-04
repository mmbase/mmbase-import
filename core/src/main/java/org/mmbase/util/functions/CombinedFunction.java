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
 * A combined function combines other function objects. Depending on the provided filled paramters
 * it calls the right function. So, it uses the function for which the provided parameters object
 * matched best.
 *
 * The best match is determined by a kind of scoring mechanism. Every missing required parameter
 * makes the function score very bad. Otherwise the rule is that the more parameters of the function
 * are provided, the better it is.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9
 */
public class CombinedFunction<R> implements Function<R> {

    private static final Logger log = Logging.getLoggerInstance(CombinedFunction.class);

    private final List<Function<R>> functions = new ArrayList<Function<R>>();

    private Parameter<?>[] parameterDefinition = null;
    private ReturnType<R> returnType = null;
    private final String name;
    private String description;

    public CombinedFunction(String name) {
        this.name = name;
    }

    public void addFunction(Function<R> func) {
        parameterDefinition = null;
        if (returnType == null) {
            returnType = func.getReturnType();
        } else {
            ReturnType<R> funcType = func.getReturnType();
            if (returnType.getTypeAsClass().isAssignableFrom(funcType.getTypeAsClass())) {
                //
            } else if (funcType.getTypeAsClass().isAssignableFrom(returnType.getTypeAsClass())) {
                returnType = funcType;
            } else {
                throw new IllegalStateException("" + func + " is not compatible. The return type " + funcType + " does not match " + returnType + " (defined by " + functions + ")");
            }
        }
        functions.add(func);
    }

    public Parameters createParameters() {
        if (parameterDefinition == null) determinDefinition();
        return new Parameters(parameterDefinition);
    }
    public R getFunctionValue(Parameters parameters) {
        if (parameterDefinition == null) determinDefinition();
        float maxscore = -1;
        Function<R> function = null;
        for (Function<R> f : functions) {
            // determin score here
            int scoreCounter = 0;
            for (Parameter<?> p : f.getParameterDefinition()) {
                if (p.isRequired() && parameters.get(p) == null) {
                    // required parameter missing, that is baaad!
                    scoreCounter = 0;
                    break;
                }
                Object v = parameters.get(p);
                if (v != null && ! "".equals(v)) {
                    log.debug("Scoring with parameter " + p);
                    scoreCounter++;
                }
            }
            if (scoreCounter > maxscore) {
                function = f;
                maxscore = scoreCounter;
                log.debug("???Using function " + function + " (with score " + maxscore + ") and parameters " + parameters);
            }
        }
        log.debug("Using function " + function + " (with score " + maxscore + ") and parameters " + parameters);
        R r = function.getFunctionValue(parameters);
        log.debug(" ==> '" + r + "'");
        return r;
    }

    /**
     * Combines the parameter definitions of the wrapped function to one new parameter definition
     */
    protected void determinDefinition() {
        if (functions.size() == 0) throw new IllegalStateException("No functions added");
        for (Function<R> f : functions) {
            Parameter<?>[] fd = f.getParameterDefinition();
            if (parameterDefinition == null) {
                parameterDefinition = fd;
                continue;
            }
            List<Parameter<?>> existing = new ArrayList<Parameter<?>>(Arrays.asList(parameterDefinition));
            for (Parameter<?> extra : fd) {
                if (! existing.contains(extra)) {
                    existing.add(extra);
                }

            }
            parameterDefinition = existing.toArray(Parameter.emptyArray());
        }
    }

    public R getFunctionValueWithList(List<?> parameters) {
        Parameters params = createParameters();
        params.setAll(parameters);
        return getFunctionValue(params);
    }
    public R getFunctionValue(Object... parameters) {
        Parameters params = createParameters();
        params.setAll(parameters);
        return getFunctionValue(params);
    }


    public void setDescription(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public Parameter<?>[] getParameterDefinition(){
        if (parameterDefinition == null) determinDefinition();
        return parameterDefinition;
    }

    public void setParameterDefinition(Parameter<?>[] params) {
        throw new UnsupportedOperationException();
    }

    public ReturnType<R> getReturnType() {
        return returnType;
    }

    public void setReturnType(ReturnType<R> type) {
        throw new UnsupportedOperationException();
    }

    public String toString() {
        if (parameterDefinition == null && functions.size() > 0) determinDefinition();
        return "" + returnType + " " +
            "Combined(" + getName() + ", " + functions.size() + " entries)" +
            (parameterDefinition == null ?  "EMPTY" : "" + Arrays.asList(parameterDefinition));

                                                                                      }

}
