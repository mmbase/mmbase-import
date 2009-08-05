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
 * @author Kees Jongenburger
 */
public class NodeManagerTest extends BridgeTest {
    Cloud cloud;
    NodeList nodes;
    int nrOfTestNodes;
    public final static String TEST_STRING_VALUE = "C05353E04zz HAVO, Cultuur/Maatschappij, Z'zee, 04-05";

    public NodeManagerTest(String name) {
        super(name);
    }

    public void setUp() {
        // Create some test nodes
        cloud = getCloud();
        nodes = cloud.createNodeList();
        Node node = cloud.getNodeManager("aa").createNode();
        node.setByteValue("binaryfield", "100".getBytes());
        node.setDoubleValue("doublefield", 200);
        node.setFloatValue("floatfield", 300);
        node.setIntValue("intfield", 400);
        node.setLongValue("longfield", 500);
        node.setStringValue("stringfield", "600");
        node.commit();
        nodes.add(node);

        node = cloud.getNodeManager("aa").createNode();
        node.setByteValue("binaryfield", "100".getBytes());
        node.setDoubleValue("doublefield", 200);
        node.setFloatValue("floatfield", 300);
        node.setIntValue("intfield", 400);
        node.setLongValue("longfield", 500);
        node.setStringValue("stringfield", TEST_STRING_VALUE );
        node.commit();

        nodes.add(node);

    }

    public void tearDown() {
        // Remove test node.

        for (NodeIterator i  = nodes.nodeIterator() ; i.hasNext() ; ){
            i.nextNode().delete();
        }
    }

    public void testGetList() {
        NodeManager nodeManager = cloud.getNodeManager("aa");
        NodeList nodeList;
        nodeList = nodeManager.getList(null, null, null);
        assertTrue(nodeList.size() == nodes.size());
        nodeList = nodeManager.getList("", "", "");
        assertTrue(nodeList.size() == nodes.size());
    }

    /**
     * Test if it is possible to search for a node that contains single quotes
     */
    public void testGetListWithQuotes() {
        NodeManager nodeManager = cloud.getNodeManager("aa");
        NodeList nodeList = nodeManager.getList("stringfield ='"+ org.mmbase.util.Encode.encode("ESCAPE_SINGLE_QUOTE", TEST_STRING_VALUE) + "'", "", "");
        assertTrue("Size of result list with constraint on string field is not 1 but " + nodeList.size() + " : " + nodeList, nodeList.size() == 1);
    }
    // Add some more list test.

    // Add some tests with wrong formatted parameters.

}
