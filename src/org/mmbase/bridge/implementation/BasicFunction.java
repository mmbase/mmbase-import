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
 * This wraps the function to set the node and cloud arguments in createParameters only.
 *
 * @since MMBase-1.8
 * @author Pierre van Rooden
 * @version $Id: BasicFunction.java,v 1.11 2005-10-18 18:21:20 michiel Exp $
 */
public class BasicFunction extends WrappedFunction {

    protected Cloud cloud = null;
    protected Node node = null;


    /**
     * Constructor for Basic Function
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
         super(function);
         this.cloud = cloud;
    }

    /**
     * Constructor for Basic Function
     * @param node     
     * @param function The function to wrap
     */
    BasicFunction(Node node, Function function) {
         this(node.getCloud(), function);
         this.node = node;
    }

    public Parameters createParameters() {
        Parameters params = super.createParameters();
        if (node != null) {
            params.setIfDefined(Parameter.NODE, node);
        }
        params.setIfDefined(Parameter.CLOUD, cloud);
        return params;
    }

    public String toString() {
        return "BASICFUNCTION " + (node != null ? " on node " + node.getNumber() + ": " : "") + wrappedFunction;
    }
}
