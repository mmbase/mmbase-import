/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;
import org.mmbase.bridge.*;
import org.mmbase.util.Casting;
import org.mmbase.module.core.MMObjectNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.util.*;

/**
 * This implementation of the Field Value interface is used by getFunctionValue of Node. This
 * represents the result of a `function' on a node and it (therefore) is a unmodifiable.
 *
 * @author  Michiel Meeuwissen
 * @version $Id: BasicFunctionValue.java,v 1.17 2005-12-27 22:14:46 michiel Exp $
 * @since   MMBase-1.6
 */
public class BasicFunctionValue extends org.mmbase.bridge.util.AbstractFieldValue {

    private final Object value;

    /**
     * Constructor for a function value returned by a Node.
     * @since MMBase-1.8
     * @param node the node that called the function
     * @param value the function value
     */
    BasicFunctionValue(Node node, Object value) {
        super(node, null);
        this.value  = value;
    }

    /**
     * Constructor for a function value returned by a Module or NodeManager.
     * @since MMBase-1.8
     * @param cloud the cloud under which the call was run, used to instantiate NodeList values
     * @param value the function value
     */
    BasicFunctionValue(Cloud cloud, Object value) {
        super(null, cloud);
        Object v = value;
        if (v instanceof List) { // might be a collection of MMObjectNodes
            List list  = (List) v;
            if (list.size() > 0) {
                if (list.get(0) instanceof MMObjectNode) { // if List of MMObjectNodes, make NodeList
                    if (cloud == null) {
                        throw new IllegalStateException("Cloud is unknown, cannot convert MMObjectNode to Node");
                    }
                    NodeList l = cloud.createNodeList();
                    v = l;
                    l.addAll(list);
                }
            }
        }
        this.value = v;
    }


    public Object get() {
        return value;
    }


}
