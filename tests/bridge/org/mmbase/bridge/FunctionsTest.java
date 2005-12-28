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
    
    /**
     * test a variety of functionset possibilities
     * XXX really not complete yet
     * @author Simon Groenwolt (simon@submarine.nl)
     */
    public void testFunctionSets() {

        Function testfunc1 = FunctionSets.getFunction("utils", "randomLong");
        assertTrue("function 'randomLong' not found (functionset 'utils')", testfunc1 != null);
        // long randomLong = testfunc1.getFunctionValue(null);
        Function testfunc2 = FunctionSets.getFunction("testfunctions", "testBoolean");
        assertTrue("function 'testBoolean' not found (functionset 'testfunctions')", testfunc2 != null);
        
        Parameters params2 = testfunc2.createParameters();
        params2.set("inBoolean", Boolean.valueOf(true));
        
        
        Object result = testfunc2.getFunctionValue(params2);
        assertTrue("Expected return value of type 'Boolean', but got: '" + result + "'", result instanceof java.lang.Boolean);
        assertTrue("function 'testBoolean' didn't return true as was expected", ((Boolean) result).booleanValue());
    }
}
