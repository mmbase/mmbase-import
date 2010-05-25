/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/ 
package org.mmbase.applications.vprowizards.spring.util;

import org.mmbase.bridge.Node;

/**
 * Becouse the node number is not known before the nodes are committed,
 * it is not possible for the create node action to set the node number
 * of the 'new' node as extra param for the return url.
 * This simple wrapper fixes that
 * @author ebunders
 *
 */
public class NodeNumberResolver implements ParamValueResolver {
    private Node node;

    public NodeNumberResolver(Node node) {
        this.node = node;
    }

    public String getValue() {
        return ""+node.getNumber();
    }
}
