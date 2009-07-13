/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

import java.util.*;
import java.util.regex.Pattern;
import org.mmbase.util.functions.*;
import org.mmbase.tests.*;
import org.mmbase.bridge.util.CollectionNodeList;

/**
 *
 * @author Simon Groenewolt (simon@submarine.nl)
 * @author Michiel Meeuwissen
 * @since $Id: FunctionsTest.java,v 1.16 2008-11-18 23:33:31 michiel Exp $
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

    public void testNodeManagerFunction2() {
        Cloud cloud = getCloud();
        NodeManager nm = cloud.getNodeManager("news");
        Node newsNode = nm.createNode();
        newsNode.commit();
        Function function = nm.getFunction("latest");
        Parameters params = function.createParameters();
        // remote clouds are not serializable. It is not needed anyway. Cloud parameters are
        // implicit, when using bridge.
        // params.set(Parameter.CLOUD, cloud);
        params.set("max", new Integer(1));
        NodeList nl = (NodeList) function.getFunctionValue(params);
        assertTrue(nl.getNode(0).getNumber() == newsNode.getNumber());
    }

    public void testNodeManagerFunction3() {
        Cloud cloud = getCloud();
        NodeManager nm = cloud.getNodeManager("mmservers");
        Function function = nm.getFunction("uptime");
        Parameters params = function.createParameters();
        Long value = (Long) function.getFunctionValue(params);
        assertTrue(value.longValue() >= 0);
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

    public void testNodeFunctionWithNodeResult1() {
        Cloud cloud = getCloud();
        NodeManager nm = cloud.getNodeManager("datatypes");
        Node node1 = nm.createNode();
        node1.commit();
        Function function = node1.getFunction("nodeFunction1");
        Parameters params = function.createParameters();
        params.set("parameter1", "hoi");
        Node n = node1.getFunctionValue("nodeFunction1", params).toNode();
        assertTrue(n.getStringValue("bloe").equals("hoi"));
        n = (Node) function.getFunctionValue(params);
        assertTrue(n.getStringValue("bloe").equals("hoi"));
    }

    public void testNodeFunctionWithNodeResult2() {
        Cloud cloud = getCloud();
        NodeManager nm = cloud.getNodeManager("datatypes");
        Node node1 = nm.createNode();
        node1.commit();
        Function function = node1.getFunction("nodeFunction2");
        Parameters params = function.createParameters();
        params.set("parameter1", "hoi");
        Node n = node1.getFunctionValue("nodeFunction2", params).toNode();
        assertTrue(n.getStringValue("bloe").equals("hoi"));
        n = (Node) function.getFunctionValue(params);
        assertTrue(n.getStringValue("bloe").equals("hoi"));
    }

    public void testNodeFunctionWithNodeListResult() {
        Cloud cloud = getCloud();
        NodeManager nm = cloud.getNodeManager("datatypes");
        Node node1 = nm.createNode();
        node1.commit();
        Function function = node1.getFunction("nodeListFunction");
        Parameters params = function.createParameters();
        params.set("parameter1", "hoi");
        NodeList nl = new CollectionNodeList((Collection) node1.getFunctionValue("nodeListFunction", params).get(), cloud);
        NodeIterator i = nl.nodeIterator();
        while (i.hasNext()) {
            Node n =  i.nextNode();
            assertTrue(n.getStringValue("bloe").equals("hoi"));
        }
        nl = new CollectionNodeList((Collection) function.getFunctionValue(params), cloud);
        i = nl.nodeIterator();
        while (i.hasNext()) {
            Node n =  i.nextNode();
            assertTrue(n.getStringValue("bloe").equals("hoi"));
        }
    }
    public void testNodeFunctionWithNodeListResult1() {
        Cloud cloud = getCloud();
        NodeManager nm = cloud.getNodeManager("datatypes");
        Node node1 = nm.createNode();
        node1.commit();
        Function function = node1.getFunction("nodeListFunction1");
        Parameters params = function.createParameters();
        params.set("parameter1", "hoi");
        NodeList nl = (NodeList) node1.getFunctionValue("nodeListFunction1", params).get();
        NodeIterator i = nl.nodeIterator();
        while (i.hasNext()) {
            Node n = i.nextNode();
            assertTrue("" + nl + " contains nulls", n != null);
            assertTrue(n.getStringValue("bloe").equals("hoi"));
        }
        nl = (NodeList) function.getFunctionValue(params);
        i = nl.nodeIterator();
        while (i.hasNext()) {
            Node n = i.nextNode();
            assertTrue(n.getStringValue("bloe").equals("hoi"));
        }
    }

    /**
     * test a variety of functionset possibilities
     * XXX really not complete yet
     */
    public void testFunctionSets() {
        if (getCloudContext().getUri().equals(ContextProvider.DEFAULT_CLOUD_CONTEXT_NAME)) {
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
        } else {
            System.out.println("Functionsets can only be used on local cloud");
        }
    }

    private static final Pattern NO_FUNCTION = Pattern.compile("(?i).*function.*nonexistingfunction.*");

    public void testNonExistingFunctionOnRealNode() {
        Cloud cloud = getCloud();
        NodeList nl = cloud.getNodeManager("object").getList(null);
        Node n = nl.get(0);

        assertTrue(n.getNumber() > 0);


        try {
            n.getFunction("nonexistingfunction");
            fail("Getting non-existing function should throw exception");
        } catch (NotFoundException e) {
            assertTrue(e.getMessage() + " does not match " + NO_FUNCTION, NO_FUNCTION.matcher(e.getMessage()).matches());
        }


        try {
            n.getValue("nonexistingfunction()");
            fail("Using non existing function should raise an exeption"); // this fails!
        } catch (IllegalArgumentException e) {
            // The exception should also be clear
            assertTrue(e.getMessage() + " does not match " + NO_FUNCTION, NO_FUNCTION.matcher(e.getMessage()).matches());
        }

    }


    public void testFunctionOnClusterNode() {
        Cloud cloud = getCloud();
        NodeList nl = cloud.getList("", "object", "",
                                    "", "", "",
                                    "", false);
        Node n = nl.get(0);
        assertTrue(n.getNumber() < 0);

        // info implemented in MMObjectBuilder  itself, so even virtual nodes must  have it, and give something non empty
        assertTrue(!"".equals(n.getFunctionValue("info", null).toString()));

        // TODO: FAILS!!!
        //assertTrue(!"".equals(n.getStringValue("info()")));

        // could also test gui of virtual nodes themselves, they seem to give empty now.

        // should be possible to call functions on elements too.
        assertTrue(!"".equals(n.getNodeValue("object").getFunctionValue("gui", null).toString()));

        // TODO: FAILS:
        //assertTrue(!"".equals(n.getStringValue("object.gui()")));
    }

    public void testNonExistingFunctionOnClusterNode() { //MMB-1208
        Cloud cloud = getCloud();
        NodeList nl = cloud.getList("", "object", "",
                                    "", "", "",
                                    "", false);
        Node n = nl.get(0);
        assertTrue(n.getNumber() < 0);
        try {
            n.getValue("nonexistingfunction(object.number)");
            fail("Using non existing function should raise an exeption"); // this fails!
        } catch (IllegalArgumentException e) {
            // The exception should also be clear
            assertTrue(e.getMessage() + " does not match " + NO_FUNCTION, NO_FUNCTION.matcher(e.getMessage()).matches());
        }
        try {
            n.getValue("object.nonexistingfunction()");
            fail("Using non existing function should raise an exeption"); // this fails!
        } catch (IllegalArgumentException e) {
            // The exception should also be clear
            assertTrue(e.getMessage() + " does not match " + NO_FUNCTION, NO_FUNCTION.matcher(e.getMessage()).matches());
        }

    }

    protected void testThisNode(String function) {
        Cloud cloud = getCloud();
        NodeManager nm = cloud.getNodeManager("datatypes");
        Node node1 = nm.createNode();
        node1.commit();

        Function fun = node1.getFunction(function);
        assertNotNull(fun);

        FieldValue result = node1.getFunctionValue(function, null);
        assertNotNull(result);

        Node resultNode = result.toNode();

        assertNotNull(resultNode);

        assertEquals(node1, resultNode);

        // calling it in a a 'legacy' way
        Node resultNode2 = node1.getNodeValue(function + "()");

        assertEquals(node1, resultNode2);
    }

    public void testThisNode() {
        testThisNode("thisnode");
        testThisNode("thisnode2");
    }
}
