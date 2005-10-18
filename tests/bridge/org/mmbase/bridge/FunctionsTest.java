/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

import java.util.*;
import org.mmbase.util.*;
import org.mmbase.util.functions.*;
import org.mmbase.tests.*;

/**
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.8
 */
public class FunctionsTest extends BridgeTest {


    public FunctionsTest(String name) {
        super(name);
    }

    public void testPatternFunction() {
        Cloud cloud = getCloud();
        NodeManager nm = cloud.getNodeManager("datatypes");
        Node node = nm.createNode();
        node.commit();
        assertTrue(node.getFunctionValue("test", null).toString().equals("[" + node.getNumber() + "]"));
    }


    public void testNodeManagerFunction() {
        Cloud cloud = getCloud();
        NodeManager nm = cloud.getNodeManager("datatypes");
        Function function = nm.getFunction("aaa");
        Parameters params = function.createParameters();
        System.out.println("Found " + params);
        params.set("parameter2", new Integer(5));
        Object result = function.getFunctionValue(params);
        assertTrue("No instance of Integer but " + result.getClass(), result instanceof Integer);
        Integer i = (Integer) result;
        assertTrue(i.intValue() == 15);
        // can also be called on a node.
        Node node = nm.createNode();
        node.commit();
        assertTrue(node.getFunctionValue("aaa", params).toInt() == 15);
    }

    public void testNodeFunctionWithNodeResult() {
        Cloud cloud = getCloud();
        NodeManager nm = cloud.getNodeManager("datatypes");
        Node node1 = nm.createNode();
        node1.commit();
        Node node2 = nm.createNode();
        node2.commit();
        Node successorOfNode1 = node1.getFunctionValue("successor", null).toNode();
        assertTrue(successorOfNode1.equals(node2));
        
    }
}
