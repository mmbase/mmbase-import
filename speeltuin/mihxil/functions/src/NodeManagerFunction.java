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
 * @version $Id: MMFunction.java
 */
public class NodeManagerFunction extends NodeFunction {

    private static final Logger log = Logging.getLoggerInstance(NodeManagerFunction.class);

    private NodeManager nodeManager;
    public NodeManagerFunction(String name, Parameter[] def, Class returnType, NodeManager nodeManager) {
        super(name, def, returnType, nodeManager);
        this.nodeManager = nodeManager;
    }

    public Object getFunctionValue(Parameters arguments) {
        if (NodeList.class.isAssignableFrom(returnType)) {
            return nodeManager.getList(name, arguments.toMap());
        } else if (String.class.isAssignableFrom(returnType)) {
            return nodeManager. getInfo(name);
        } else {
            return super.getFunctionValue(arguments);
        }
    }

}
