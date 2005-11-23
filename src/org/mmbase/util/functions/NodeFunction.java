/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.functions;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.bridge.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * A NodeFunction represents a function on a node instances of this builder. This means
 * that it always has one implicit node argument. This node-argument needs not be mentioned in
 * the Parameter array of the constructor.
 *
 * @author Michiel Meeuwissen
 * @version $Id: NodeFunction.java,v 1.16 2005-11-23 12:19:26 michiel Exp $
 * @see org.mmbase.module.core.MMObjectBuilder#executeFunction
 * @see org.mmbase.bridge.Node#getFunctionValue
 * @see org.mmbase.util.functions.BeanFunction
 * @since MMBase-1.8
 */

public abstract class NodeFunction extends AbstractFunction {

    private static final Logger log = Logging.getLoggerInstance(NodeFunction.class);


    /**
     * Utility function, for easy call of function on node by one string.
     */
    public static FieldValue getFunctionValue(Node node, String function) {
        if(node == null) {
            log.warn("Tried to execute node-function on null!");
            return null;
        }
        List args = new ArrayList();
        String functionName = getFunctionNameAndFillArgs(function, args);
        if (log.isDebugEnabled()) {
            log.debug("Executing " + functionName + " " + args + " on " + node.getNumber());
        }

        return node.getFunctionValue(functionName, args);
    }

    public static String getFunctionNameAndFillArgs(String function, java.util.List args) {
        String functionName = function;
        int pos1 = function.indexOf('(');
        if (pos1 != -1) {
            int pos2 = function.lastIndexOf(')');
            if (pos2 != -1) {
                functionName = function.substring(0, pos1);
                java.util.List args2 = org.mmbase.util.StringSplitter.splitFunctions(function.subSequence(pos1 + 1, pos2));
                args.addAll(args2);
            }
        }
        return functionName;
    }



    public NodeFunction(String name, Parameter[] def, ReturnType returnType) {
        super(name, getNodeParameterDef(def), returnType);
    }
    protected static Parameter[] getNodeParameterDef(Parameter[] def) {
        List defList = Arrays.asList(def);
        if (defList.contains(Parameter.NODE) && defList.contains(Parameter.CLOUD)) {
            return def;
        } else if (defList.contains(Parameter.NODE)) {
            return new Parameter[] { new Parameter.Wrapper(def), Parameter.CLOUD};
        } else if (defList.contains(Parameter.CLOUD)) {
            return new Parameter[] { new Parameter.Wrapper(def), Parameter.NODE};
        } else {
            return new Parameter[] { new Parameter.Wrapper(def), Parameter.NODE, Parameter.CLOUD};
        }
    }

    /**
     * Returns a new instance of NodeInstanceFunction, which represents an actual Function.
     */
    final public Function newInstance(MMObjectNode node) {
        return new NodeInstanceFunction(node);
    }

    /**
     * Implements the function on a certain node. Override this method <em>or</em> it's bridge
     * counter-part {@link #getFunctionValue(org.mmbase.bridge.Node, Parameters)}.  Overriding the
     * bridge version has two advantages. It's easier, and mmbase security will be honoured. That
     * last thing is of course not necesary if you are not going to use other nodes.
     *
     * XXX: made final because it does not work well if you don't implement a bridge version
     */
    protected final Object getFunctionValue(final MMObjectNode coreNode, final Parameters parameters) {
        if (coreNode == null) throw new RuntimeException("No node argument given for " + this + "(" + parameters + ")!");
        Cloud cloud   = (Cloud)  parameters.get(Parameter.CLOUD);
        if (cloud == null) {
            // lets try this
            cloud = org.mmbase.bridge.ContextProvider.getDefaultCloudContext().getCloud("mmbase", "class", null);
            if (cloud == null) {
                throw new RuntimeException("No cloud argument given"  + this + "(" + parameters + ")!" + Logging.stackTrace());
            }
        }         
        Node node;
        if (coreNode instanceof org.mmbase.module.core.VirtualNode) {
            node = new org.mmbase.bridge.implementation.VirtualNode((org.mmbase.module.core.VirtualNode) coreNode, cloud); 
        } else {
            int number = coreNode.getNumber();
            if (number == -1) {
                // must be in transaction or uncommited node
                String tmpNumber = coreNode.getStringValue(MMObjectBuilder.TMP_FIELD_NUMBER);
                node = cloud.getNode(tmpNumber);
            } else {
                node = cloud.getNode(number);
            }
        }
        return getFunctionValue(node, parameters);

    }

    /**
     * Utility method to convert a {@link org.mmbase.bridge.Node} to a a {@link org.mmbase.module.core.MMObjectNode}.
     */
    protected final MMObjectNode getCoreNode(final MMObjectBuilder builder, final Node node) {
        if (node instanceof org.mmbase.bridge.implementation.VirtualNode) {
            return ((org.mmbase.bridge.implementation.VirtualNode) node).getNodeRef();
        } else {
            return builder.getNode(node.getNumber());
        }

    }

    /**
     * This function will never be called, only by the default implemention of {@link
     * #getFunctionValue(MMObjectNode, Parameters)}. So, you must override either that funciton, or
     * this one. This will probably not work if you don't call {@link Node#getFunctionValue} (but
     * something like getStringValue("function(arguments)"), which is deprecated).
     *
     * @throws UnssupportedOoperationException
     */
    protected Object getFunctionValue(Node node, Parameters parameters) {
        throw new UnsupportedOperationException("Not supported");
    }

    /**
     * To implement a NodeFunction, you must override {@link #getFunctionValue(Node, Parameters)}.
     * This one can be overriden if the same function must <em>also</em> be a builder function.
     */
    public Object getFunctionValue(Parameters parameters) {
        if (! parameters.containsParameter(Parameter.NODE)) {
            throw new IllegalArgumentException("The function " + toString() + " requires a node argument");
        }
        Node node = (Node) parameters.get(Parameter.NODE);
        if (node == null) {
            throw new IllegalArgumentException("The node argument of  " + getClass() + " " + toString() + " must not be null ");
        }
        Object o = getFunctionValue(node, parameters);
        if (log.isDebugEnabled()) {
            log.debug("" + this + " " + parameters + " --> " + o);
        }
        return o;
    }

    /**
     * This represents the function on one specific Node. This is instantiated when new Istance
     * if called on a NodeFunction.
     */
    private class NodeInstanceFunction extends WrappedFunction {

        protected MMObjectNode node;

        public NodeInstanceFunction(MMObjectNode node) {
            super(NodeFunction.this);
            this.node = node;
        }
        //javadoc inherited
        public final Object getFunctionValue(Parameters parameters) {
            return NodeFunction.this.getFunctionValue(node, parameters);

        }

        public String toString() {
            return NodeFunction.this.toString() + " for node " + node.getNumber();
        }
    }

}


