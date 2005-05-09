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
 * @version $Id: BasicFunction.java,v 1.5 2005-05-09 21:48:44 michiel Exp $
 */
public class BasicFunction extends WrappedFunction {

    protected Cloud cloud = null;
    protected Node node = null;

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
    BasicFunction(Node node, Function function) {
         this(node.getCloud(), function);
         this.node = node;
    }

    public Object getFunctionValue(Parameters parameters) {
        if (node != null) {
            return new BasicFunctionValue(node, wrappedFunction.getFunctionValue(parameters));
        } else {
            if (cloud == null) {
                cloud = (Cloud)parameters.get("cloud");
            }
            return new BasicFunctionValue(cloud, wrappedFunction.getFunctionValue(parameters));
        }
    }

    public String toString() {
        return "BASICFUNCTION " + wrappedFunction;
    }
}
