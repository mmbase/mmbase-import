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
 * A combined function combines other function object. Depending on the provided filled paramters it calls the right function.
 *
 * @author Michiel Meeuwissen
 * @version $Id: CombinedFunction.java,v 1.4 2006-09-25 14:00:01 michiel Exp $
 * @since MMBase-1.9
 */
public class CombinedFunction<R> implements Function<R> {

    private static final Logger log = Logging.getLoggerInstance(CombinedFunction.class);

    private final List<Function<R>> functions = new ArrayList<Function<R>>();

    private Parameter<?>[] parameterDefinition = null;
    private ReturnType returnType = null;
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
            ReturnType funcType = func.getReturnType();
            if (returnType.getTypeAsClass().isAssignableFrom(funcType.getTypeAsClass())) {
                // 
            } else if (funcType.getTypeAsClass().isAssignableFrom(returnType.getTypeAsClass())) {
                returnType = funcType;
            } else {
                throw new IllegalStateException("" + func + " is not compatible");
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
            for (Parameter p : f.getParameterDefinition()) {
                if (p.isRequired() && parameters.get(p) == null) {
                    // required parameter missing, that is baaad!
                    scoreCounter = 0;
                    break;
                }
                Object v = parameters.get(p);
                if (v != null && ! "".equals(v)) {
                    log.info("Scoring with parameter " + p);
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

    protected void determinDefinition() {
        if (functions.size() == 0) throw new IllegalStateException("No functions added");
        for (Function f : functions) {
            Parameter[] fd = f.getParameterDefinition();
            if (parameterDefinition == null) {
                parameterDefinition = fd;
                continue;
            }
            List<Parameter> existing = new ArrayList(Arrays.asList(parameterDefinition));
            for (Parameter extra : fd) {
                if (! existing.contains(extra)) {
                    existing.add(extra);
                }

            }
            parameterDefinition = existing.toArray(Parameter.EMPTY);
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

    public ReturnType getReturnType() {
        return returnType;
    }

    public void setReturnType(ReturnType type) {
        throw new UnsupportedOperationException();
    }

    public String toString() {
        if (parameterDefinition == null && functions.size() > 0) determinDefinition();
        return "" + returnType + " " + 
            "Combined(" + getName() + ", " + functions.size() + " entries)" +
            (parameterDefinition == null ?  "EMPTY" : "" + Arrays.asList(parameterDefinition));

                                                                                      }

}
