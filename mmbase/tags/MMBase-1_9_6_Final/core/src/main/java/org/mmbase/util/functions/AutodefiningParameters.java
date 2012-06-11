/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.functions;

import java.util.*;

/**
 * If there is no Parameter definition array available you could try it with this specialization, which does not need one.
 * You loose al checking on type and availability. It should only be used as a last fall back and accompanied by warnings.
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.7
 * @version $Id$
 * @see Parameter
 */

public class AutodefiningParameters extends Parameters {
    private static final long serialVersionUID = 0L;

    private final Set<String> myParams = new TreeSet<String>();
    //private static Logger log = Logging.getLoggerInstance(AutodefiningParameters.class);

    public AutodefiningParameters(Parameter<?>[] base) {
        super(base);
    }

    public AutodefiningParameters() {
        super(new Parameter[0]);
    }
    @Override
    public boolean containsParameter(Parameter<?> param) {
        return true;
    }

    protected int define(Parameter param) {
        if (myParams.size() > 0) {
            String prevParam = myParams.iterator().next();
            myParams.remove(prevParam);
            int i = indexOfParameter(prevParam);
            definition[i] = param;
            return i;
        } else {
            Parameter<Object>[] newDef = new Parameter[definition.length + 1];
            for (int i = 0; i < definition.length; i++) {
                newDef[i] = definition[i];
            }
            newDef[newDef.length - 1] = param;
            definition = newDef;
            toIndex++;
            patternLimit++;
            return definition.length - 1;
        }
    }

    protected int define(String param) {
        return define(new Parameter<Object>(param, Object.class));
    }

    @Override
    public int indexOfParameter(Parameter parameter) {
        int index = super.indexOfParameter(parameter);
        if (index == -1) {
            return define(parameter);
        } else {
            return index;
        }
    }

    @Override
    public int indexOfParameter(String parameterName) {
        int index = super.indexOfParameter(parameterName);
        if (index == -1) {
            return define(parameterName);
        } else {
            return index;
        }
    }

    @Override
    public Object set(int i, Object value) {
        Set<String> newParams = new TreeSet<String>();
        while (definition.length <= i) {
            String argName = "arg" + definition.length;
            while (! newParams.contains(argName) && super.indexOfParameter(argName) != -1) {
                argName += "_";
            }
            define(argName);
            if (definition.length <= i) {
                newParams.add(argName);
            }
        }
        myParams.addAll(newParams);
        return super.set(i, value);
    }

    public static void main(String[] args) {
        AutodefiningParameters pars = new AutodefiningParameters();
        for (String arg : args) {
            pars.setIfDefined(arg, "");
        }

        System.out.println("" + pars);
        for (String arg : args) {
            System.out.println(" " + arg + ": " + pars.indexOfParameter(arg));
        }
        for (String arg : args) {
            pars.set(arg, "X");
        }

        for (String arg : args) {
            System.out.println(" " + arg + ": " + pars.indexOfParameter(arg));
        }

    }

}
