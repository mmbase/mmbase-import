/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.functions;


import org.mmbase.util.logging.*;
import org.mmbase.bridge.*;

/**
 * Describing a function on a NodeManager.
 *
 * @author Michiel Meeuwissen
 * @version $Id: NodeManagerFunction.java,v 1.3 2003-11-21 22:01:50 michiel Exp $
 * @since MMBase-1.7
 */
public class NodeManagerFunction extends NodeFunction {

    private static final Logger log = Logging.getLoggerInstance(NodeManagerFunction.class);

    private NodeManager nodeManager;
    public NodeManagerFunction(String name, Parameter[] def, ReturnType returnType, NodeManager nodeManager) {
        super(name, def, returnType, nodeManager);
        this.nodeManager = nodeManager;
    }

    /**
     * NodeManager actually has two function like methods now (accepting 'command')
     */

    public Object getFunctionValue(Parameters arguments) {
        if (NodeList.class.isAssignableFrom(returnType.getType())) {
            return nodeManager.getList(name, arguments.toMap());
        } else if (String.class.isAssignableFrom(returnType.getType())) {
            return nodeManager. getInfo(name);
        } else {
            return super.getFunctionValue(arguments);
        }
    }

}
