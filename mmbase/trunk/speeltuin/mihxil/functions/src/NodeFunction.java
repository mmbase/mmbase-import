/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.functions;


import org.mmbase.bridge.*;

/**
 * Describing a function on a bridge Node, giving access to the underlying executeFunction of the MMObjectBuilder.
 *
 * @author Michiel Meeuwissen
 * @version $Id: MMFunction.java
 * @see org.mmbase.module.core.MMObjectBuilder#executeFunction
 * @see org.mmbase.bridge.Node#getFunctionValue
 * @since MMBase-1.7
 */
public class NodeFunction extends Function {

    private Node node;
    public NodeFunction(String name, Parameter[] def, ReturnType returnType, Node node) {
        super(name, def, returnType);
        this.node = node;
    }

    public Object getFunctionValue(Parameters arguments) {
        return node.getFunctionValue(name, arguments);
    }

}
