/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

import org.mmbase.tests.*;

/**
 * Test class <code>NodeManager</code> from the bridge package.
 *
 * @author Jaco de Groot
 */
public class NodeManagerTest extends BridgeTest {
    Cloud cloud;
    Node node;
    int nrOfTestNodes;

    public NodeManagerTest(String name) {
        super(name);
    }

    public void setUp() {
        // Create a test node.
        cloud = getCloud();
        node = cloud.getNodeManager("aa").createNode();
        byte[] bytes = {72,101,108,108,111,32,119,111,114,108,100,33};
        node.setByteValue("bytefield", "100".getBytes());
        node.setDoubleValue("doublefield", 200);
        node.setFloatValue("floatfield", 300);
        node.setIntValue("intfield", 400);
        node.setLongValue("longfield", 500);
        node.setStringValue("stringfield", "600");
        node.commit();
        nrOfTestNodes = 1;
    }

    public void tearDown() {
        // Remove test node.
        node.delete();
    }

    public void testGetList() {
        NodeManager nodeManager = cloud.getNodeManager("aa");
        NodeList nodeList;
        nodeList = nodeManager.getList(null, null, null);
        assertTrue(nodeList.size() == nrOfTestNodes);
        nodeList = nodeManager.getList("", "", "");
        assertTrue(nodeList.size() == nrOfTestNodes);
    }
    
    // Add some more list test.

    // Add some tests with wrong formatted parameters.

}
