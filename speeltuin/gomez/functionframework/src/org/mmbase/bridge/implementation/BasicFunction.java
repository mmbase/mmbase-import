/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.implementation;

import java.util.List;
import org.mmbase.bridge.*;
import org.mmbase.util.functions.*;

/**
 * @javadoc
 * @since MMBase-1.7
 * @author Pierre van Rooden
 * @version $Id: BasicFunction.java,v 1.2 2004-11-29 14:21:10 pierre Exp $
 */
public class BasicFunction extends WrappedFunction {

    protected Cloud cloud = null;
    protected Node node = null;

    /**
     * Constructor for Basic Function
     * @param cloud The user's cloud
     * @param function The function to wrap
     */
    public BasicFunction(Function function) {
         super(function);
    }

    /**
     * Constructor for Basic Function
     * @param cloud The user's cloud
     * @param function The function to wrap
     */
    public BasicFunction(Cloud cloud, Function function) {
         this(function);
         this.cloud = cloud;
    }

    /**
     * Constructor for Basic Function
     * @param cloud The user's cloud
     * @param function The function to wrap
     */
    public BasicFunction(Node node, Function function) {
         this(node.getCloud(), function);
         this.node = node;
    }

    public Object getFunctionValue(Parameters parameters) {
        if (node != null) {
            return new BasicFunctionValue(node, super.getFunctionValue(parameters));
        } else {
            return new BasicFunctionValue(cloud, super.getFunctionValue(parameters));
        }
    }
}
