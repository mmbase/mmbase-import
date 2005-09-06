/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.implementation;

import org.mmbase.bridge.*;
import org.mmbase.util.functions.*;

/**
 * This wraps the function to return 'FunctionValue' objects - rather then the more basic types of
 * the function itself - as required by bridge.
 *
 * @since MMBase-1.7
 * @author Pierre van Rooden
 * @version $Id: BasicFunction.java,v 1.8 2005-09-06 21:14:44 michiel Exp $
 */
public class BasicFunction extends WrappedFunction {

    protected Cloud cloud = null;
    protected BasicNode node = null;

    /**
     * Constructor for Basic Function
     * @param cloud The user's cloud
     * @param function The function to wrap
     */
    BasicFunction(Function function) {
         super(function);
    }

    /**
     * Constructor for Basic Function
     * @param cloud The user's cloud
     * @param function The function to wrap
     */
    BasicFunction(Cloud cloud, Function function) {
         this(function);
         this.cloud = cloud;
    }

    /**
     * Constructor for Basic Function
     * @param cloud The user's cloud
     * @param function The function to wrap
     */
    BasicFunction(BasicNode node, Function function) {
         this(node.cloud, function);
         this.node = node;
    }

    public Object getFunctionValue(Parameters parameters) {
        if (node != null) {
            return new BasicFunctionValue(node, wrappedFunction.getFunctionValue(parameters));
        } else {
            if (cloud == null) {
                cloud = (Cloud) parameters.get("cloud");
            }
            return new BasicFunctionValue(cloud, wrappedFunction.getFunctionValue(parameters));
        }
    }
    public Parameters createParameters() {
        Parameters params = super.createParameters();
        if (node != null) {
            params.setIfDefined(Parameter.NODE, node.noderef);
        }
        return params;
    }

    public String toString() {
        return "BASICFUNCTION " + (node != null ? " on node " + node.getNumber() + ": " : "") + wrappedFunction;
    }
}
