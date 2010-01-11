/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

import java.util.*;
import org.mmbase.bridge.*;

import org.mmbase.util.functions.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Sometimes you may want to implement a function on a node based on an {@link MMObjectNode} instance.
 * This provides {@link #getCoreNode}, which can be used to produce an instance of that.
 *
 * @author Michiel Meeuwissen
 * @version $Id: NodeFunction.java 38475 2009-09-07 14:10:55Z michiel $
 * @see org.mmbase.module.core.MMObjectBuilder#executeFunction
 * @see org.mmbase.bridge.Node#getFunctionValue
 * @see org.mmbase.util.functions.BeanFunction
 * @since MMBase-1.9.2
 */

public abstract class MMObjectNodeFunction<R> extends NodeFunction<R> {

    private static final Logger log = Logging.getLoggerInstance(MMObjectNodeFunction.class);


    public static <Q> Function<Q> newInstance(NodeFunction<Q> function, MMObjectNode node) {
        return new MMObjectNodeInstanceFunction(function, node);
    }

    public MMObjectNodeFunction(String name, Parameter<?>[] def, ReturnType<R> returnType) {
        super(name, getNodeParameterDef(def), returnType);
    }
    /**
     * @since MMBase-1.9
     */
    public MMObjectNodeFunction(String name, Parameter... def) {
        super(name, getNodeParameterDef(def));
    }

    /**
     * Returns a new instance of NodeInstanceFunction, which represents an actual Function.
     */
    final public Function<R> newInstance(MMObjectNode node) {
        return new MMObjectNodeInstanceFunction(this, node);
    }


    protected static Node getNode(final MMObjectNode coreNode, final Parameters parameters) {
        if (coreNode == null) throw new RuntimeException("No node argument given for (" + parameters + ")!");
        Node node = parameters.get(Parameter.NODE);
        if (node == null) {
            Cloud cloud   = parameters.get(Parameter.CLOUD);
            if (cloud == null) {
                // lets try this
                try {
                    cloud = org.mmbase.bridge.ContextProvider.getDefaultCloudContext().getCloud("mmbase", "class", null);
                } catch (org.mmbase.security.SecurityException se) {
                    // perhaps class-security not implemented by security implementation.
                    log.warn("" + se.getMessage());
                    cloud = org.mmbase.bridge.ContextProvider.getDefaultCloudContext().getCloud("mmbase");
                }
                if (cloud == null) {
                    throw new RuntimeException("No cloud argument given (" + parameters + ")!" + Logging.stackTrace());
                }
            }
            if (coreNode instanceof org.mmbase.module.core.VirtualNode) {
                node = new org.mmbase.bridge.implementation.VirtualNode((org.mmbase.module.core.VirtualNode) coreNode, cloud);
                log.debug("Core node is virtual, taking bridge node " + node);
            } else {
                int number = coreNode.getNumber();
                if (number == -1) {
                    // must be in transaction or uncommited node
                    String tmpNumber = coreNode.getStringValue(MMObjectBuilder.TMP_FIELD_NUMBER);
                    if (cloud.hasNode(tmpNumber)) {
                        node = cloud.getNode(tmpNumber);
                        log.debug("Found transactional (?) node " + node + " from "  + cloud);
                    } else {
                        // last resort..., we're really desperate now.
                        // This happens when calling gui() in transaction.
                        // Perhaps we need something like a public new BasicNode(MMobjectNode, Cloud). Abusing VirtualNode for similar purpose now.
                        org.mmbase.module.core.VirtualNode virtual = new org.mmbase.module.core.VirtualNode(coreNode.getBuilder());
                        for (Map.Entry<String, Object> entry : coreNode.getValues().entrySet()) {
                            virtual.storeValue(entry.getKey(), entry.getValue());
                        }
                        node = new org.mmbase.bridge.implementation.VirtualNode(virtual, cloud);
                        log.debug("Found transaction (?) node " + node + ". Not in cloud " + cloud + " taking"  + node);
                    }
                } else {
                    if (cloud.mayRead(number)) {
                        node = cloud.getNode(number);
                        log.debug("Node exists, taking " + node + " from " + cloud);
                    } else {
                        log.warn("Could not produce Bridge Node for '" + number + "', cannot execute node function.");
                        return null;
                    }
                }
            }
            parameters.set(Parameter.NODE, node);
        } else {
            log.debug("node as param: " + node);
        }
        return node;
    }


    /**
     * Implements the function on a certain node. Override this method <em>or</em> it's bridge
     * counter-part {@link #getFunctionValue(org.mmbase.bridge.Node, Parameters)}.  Overriding the
     * bridge version has two advantages. It's easier, and mmbase security will be honoured. That
     * last thing is of course not necesary if you are not going to use other nodes.
     *
     * XXX: made final because it does not work well if you don't implement a bridge version
     */
    protected final R getFunctionValue(final MMObjectNode coreNode, final Parameters parameters) {
        Node node = getNode(coreNode, parameters);
        if (log.isDebugEnabled()) {
            log.debug("Now calling on " + this + " " + parameters);
        }
        return getFunctionValue(node, parameters);
    }



    /**
     * Utility method to convert a {@link org.mmbase.bridge.Node} to a {@link org.mmbase.module.core.MMObjectNode}.
     */
    protected final  MMObjectNode getCoreNode(final MMObjectBuilder builder, final Node node) {
        if (node instanceof org.mmbase.bridge.implementation.VirtualNode) {
            MMObjectNode n = ((org.mmbase.bridge.implementation.VirtualNode) node).getNodeRef();
            if (log.isDebugEnabled()) {
                log.debug("" + node + " -> " + n);
            }
            return n;
        } else {
            MMObjectNode n = builder.getNode(node.getNumber());
            if (log.isDebugEnabled()) {
                log.debug("" + node + " -> " + n);
            }
            return n;
        }

    }

    /**
     * This represents the function on one specific Node. This is instantiated when new Istance
     * if called on a NodeFunction.
     */
    private static class MMObjectNodeInstanceFunction<Q> extends WrappedFunction<Q> {

        protected MMObjectNode node;


        public MMObjectNodeInstanceFunction(NodeFunction<Q> function, MMObjectNode node) {
            super(function);
            this.node = node;
        }
        @Override
        public final Q getFunctionValue(Parameters parameters) {
            if (wrappedFunction instanceof MMObjectNodeFunction) {
                parameters.set(Parameter.CORENODE, node);
                return ((MMObjectNodeFunction<Q>) wrappedFunction).getFunctionValue(node, parameters);
            } else {
                Node n = MMObjectNodeFunction.getNode(node, parameters);
                if (log.isDebugEnabled()) {
                    log.debug("Now calling on " + wrappedFunction.getClass() + " " + wrappedFunction + " " + parameters);
                }
                parameters.set(Parameter.CORENODE, node); // hmm
                return ((NodeFunction<Q>) wrappedFunction).getFunctionValueForNode(n, parameters);
            }

        }

        @Override
        public String toString() {
            return wrappedFunction.toString() + " for node " + node.getNumber();
        }
    }

}


