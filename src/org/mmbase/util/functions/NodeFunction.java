/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.functions;

import org.mmbase.module.core.MMObjectNode;
import org.mmbase.bridge.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * A NodeFunction represents a function on a node instances of this builder. This means
 * that it always has one implicit node argument. This node-argument needs not be mentioned in
 * the Parameter array of the constructor.
 *
 * @author Michiel Meeuwissen
 * @version $Id: NodeFunction.java,v 1.7 2005-06-27 12:29:06 michiel Exp $
 * @see org.mmbase.module.core.MMObjectBuilder#executeFunction
 * @see org.mmbase.bridge.Node#getFunctionValue
 * @see org.mmbase.util.function.BeanFunction
 * @since MMBase-1.8
 */

public abstract class NodeFunction extends AbstractFunction {

    private static final Logger log = Logging.getLoggerInstance(NodeFunction.class);


    /**
     * Utility function, for easy call of function on node by one string.
     */
    public static FieldValue getFunctionValue(Node node, String function) {
        java.util.List args = null;
        String functionName = function;
        int pos1 = function.indexOf('(');
        if (pos1 != -1) {
            int pos2 = function.lastIndexOf(')');
            if (pos2 != -1) {
                functionName = function.substring(0, pos1);
                args = org.mmbase.util.StringSplitter.splitFunctions(function.subSequence(pos1 + 1, pos2));
            }
        }
        return node.getFunctionValue(functionName, args);
    }



    public NodeFunction(String name, DataType[] def, DataType returnType) {
        super(name, new Parameter[] { new Parameter.Wrapper(def), Parameter.NODE}, returnType);
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
     * bridge version has two advantages. It's easier, and mmbase security will be honoured.
     * 
     */
    protected Object getFunctionValue(final MMObjectNode coreNode, final Parameters parameters) {
        if (coreNode == null) throw new RuntimeException("No node argument given for " + this + "(" + parameters + ")!");
        final Cloud cloud   = (Cloud)  parameters.get(Parameter.CLOUD);
        if (cloud == null) throw new RuntimeException("No cloud argument given"  + this + "(" + parameters + ")!" + Logging.stackTrace());
        final Node node     = cloud.getNode(coreNode.getNumber());
        return getFunctionValue(node, parameters);
            
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
     * To implement a NodeFunction, you must override {@link #getFunctionValue(MMObjectNode, Parameters)}. 
     * This one can be overriden if the same function must <em>also</em> be a builder function.
     */
    public Object getFunctionValue(Parameters parameters) {
        if (! parameters.containsParameter(Parameter.NODE)) {
            throw new IllegalArgumentException("The function " + toString() + " requires a node argument");
        }
        MMObjectNode node = (MMObjectNode) parameters.get(Parameter.NODE);
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


