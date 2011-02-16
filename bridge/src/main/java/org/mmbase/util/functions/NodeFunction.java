/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.functions;

import java.util.*;
import org.mmbase.bridge.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * A NodeFunction represents a function on a node instances of this builder. This means
 * that it always has one implicit node argument. This node-argument needs not be mentioned in
 * the Parameter array of the constructor.
 *
 * If you need to impelment this and like to use MMObjectNodes for the implementation (which would probably make the function unusable in RMMCI), then you could
 * extend {@link org.mmbase.module.core.MMObjectNodeFunction}.
 *
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @see org.mmbase.bridge.Node#getFunctionValue
 * @see org.mmbase.util.functions.BeanFunction
 * @since MMBase-1.8
 */

public abstract class NodeFunction<R> extends AbstractFunction<R> {

    private static final Logger log = Logging.getLoggerInstance(NodeFunction.class);

    /**
     * @return The currently set ReturnType, or <code>null</code> if not set already.
     */
    @Override
    public ReturnType<R> getReturnType() {
        if (returnType == null && autoReturnType) {
            try {
                returnType = (ReturnType<R>) ReturnType.getReturnType(getClass().getDeclaredMethod("getFunctionValue", Node.class, Parameters.class).getReturnType());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return returnType;
    }

    /**
     * Utility function, for easy call of function on node by one string.
     */
    public static FieldValue getFunctionValue(Node node, String function) {
        if(node == null) {
            log.warn("Tried to execute node-function on null!");
            return null;
        }
        List<String> args = new ArrayList<String>();
        String functionName = getFunctionNameAndFillArgs(function, args);
        if (log.isDebugEnabled()) {
            log.debug("Executing " + functionName + " " + args + " on " + node.getNumber());
        }

        return node.getFunctionValue(functionName, args);
    }

    public static String getFunctionNameAndFillArgs(String function, java.util.List<String> args) {
        String functionName = function;
        int pos1 = function.indexOf('(');
        if (pos1 != -1) {
            int pos2 = function.lastIndexOf(')');
            if (pos2 != -1) {
                functionName = function.substring(0, pos1);
                java.util.List<String> args2 = org.mmbase.util.StringSplitter.splitFunctions(function.subSequence(pos1 + 1, pos2));
                args.addAll(args2);
            }
        }
        return functionName;
    }

    public NodeFunction(String name, Parameter<?>[] def, ReturnType<R> returnType) {
        super(name, getNodeParameterDef(def), returnType);
    }
    /**
     * @since MMBase-1.9
     */
    public NodeFunction(String name, Parameter... def) {
        super(name, getNodeParameterDef(def));
    }

    protected static Parameter[] getNodeParameterDef(Parameter... def) {
        List<Parameter> defList = new ArrayList(Arrays.asList(def));
        if (! defList.contains(Parameter.NODE)) defList.add(Parameter.NODE);
        if (! defList.contains(Parameter.CLOUD)) defList.add(Parameter.CLOUD);
        if (! defList.contains(Parameter.CORENODE)) defList.add(Parameter.CORENODE); // I bit silly, but added for backwards compatibility
        return defList.toArray(Parameter.emptyArray());
    }


    /**
     * Returns a new instance of NodeInstanceFunction, which represents an actual Function.
     */
    final public Function<R> newInstance(Node node) {
        return new NodeInstanceFunction(node);
    }

    /**
     */
    protected abstract R getFunctionValue(Node node, Parameters parameters);


    /**
     * Just a public wrapper around {@link #getFunctionValue(Node, Parameters)} (of which we don't want to loosen the scope, because it may be overriden protected).
     */
    public final R getFunctionValueForNode(Node node, Parameters parameters) {
        return getFunctionValue(node, parameters);
    }

    protected Node getNode(Parameters parameters) {
        if (! parameters.containsParameter(Parameter.NODE)) {
            throw new IllegalArgumentException("The function " + toString() + " requires a node argument");
        }
        Node node = parameters.get(Parameter.NODE);
        if (node == null) {
            throw new IllegalArgumentException("The '" + Parameter.NODE + "' argument of  " + getClass() + " " + toString() + " must not be null ");
        }
        return node;
    }

    /**
     * To implement a NodeFunction, you must override {@link #getFunctionValue(Node, Parameters)}.
     * This one can be overridden if the same function must <em>also</em> be a builder function.
     */
    @Override
    public  R getFunctionValue(Parameters parameters) {
        log.debug("Getting for " + this + " " + parameters);
        return  getFunctionValue(getNode(parameters), parameters);
    }

    /**
     * Tries to convert a certain Function object into a NodeFunction object.
     * @return <code>function</code> if that was already a NodeFunction, <code>null</code> if it
     * could not be wrapped (No {@link Parameter#NODE} parameter), or a new NodeFunction object
     * wrapping <code>function</code>
     *
     * @since MMBase-1.8.5
     */
    public static <S> NodeFunction<S> wrap(Function<S> function) {
        if (function instanceof NodeFunction) {
            return (NodeFunction<S>) function;
        } else {
            // if it contains a 'node' parameter, it can be wrapped into a node-function,
            // and be available on nodes of this builder.
            Parameters test = function.createParameters();
            if (test.containsParameter(Parameter.NODE)) {
                final Function<S> f = function;
                return new NodeFunction<S>(function.getName(), function.getParameterDefinition(), function.getReturnType()) {
                    @Override
                    protected S getFunctionValue(org.mmbase.bridge.Node node, Parameters parameters) {
                        if (parameters == null) {
                            parameters = createParameters();
                        }
                        parameters.set(Parameter.NODE, node);
                        return f.getFunctionValue(parameters);
                    }
                    @Override
                    public S getFunctionValue(Parameters parameters) {
                        return f.getFunctionValue(parameters);
                    }
                };
            } else {
                return null;
            }
        }
    }


    /**
     * This represents the function on one specific Node. This is instantiated when new Istance
     * if called on a NodeFunction.
     */
    private class NodeInstanceFunction extends WrappedFunction<R> {

        protected Node node;

        public NodeInstanceFunction(Node node) {
            super(NodeFunction.this);
            this.node = node;
        }
        @Override
        public final R getFunctionValue(Parameters parameters) {
            parameters.set(Parameter.NODE, node);
            return NodeFunction.this.getFunctionValue(node, parameters);

        }

        @Override
        public String toString() {
            return NodeFunction.this.toString() + " for node " + node.getNumber();
        }
    }



}


