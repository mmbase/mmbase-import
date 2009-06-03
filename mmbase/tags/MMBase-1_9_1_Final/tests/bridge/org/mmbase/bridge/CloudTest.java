/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

import org.mmbase.tests.*;
/**
 * Test class <code>Cloud</code> from the bridge package.
 *
 * @author Jaco de Groot
 */
public class CloudTest extends BridgeTest {
    Cloud cloud;
    Node aaNode1;
    Node aaNode2;
    Node bbNode;
    Node[] bbNodes;
    int nrOfBBNodes;

    public CloudTest(String name) {
        super(name);
    }

    public void setUp() {
        // Create a test node.
        cloud = getCloud();
        aaNode1 = cloud.getNodeManager("aa").createNode();
        aaNode1.setStringValue("stringfield", "startnode1");
        aaNode1.commit();
        aaNode2 = cloud.getNodeManager("aa").createNode();
        aaNode2.setStringValue("stringfield", "startnode2");
        aaNode2.commit();
        bbNode = cloud.getNodeManager("bb").createNode();
        bbNode.setStringValue("stringfield", "bbNode");
        bbNode.commit();
        RelationManager relationManager;
        relationManager = cloud.getRelationManager("aa", "bb", "related");
        Relation relation;
        relation = relationManager.createRelation(aaNode2, bbNode);
        relation.commit();
        bbNodes = new Node[11];
        nrOfBBNodes = 0;
        for (int i = -5; i < 6; i++) {
            String s = new Integer(i).toString();
            Node node;
            node = cloud.getNodeManager("bb").createNode();
            node.setByteValue("binaryfield", s.getBytes());
            node.setDoubleValue("doublefield", i);
            node.setFloatValue("floatfield", i);
            node.setIntValue("intfield", i);
            node.setLongValue("longfield", i);
            node.setStringValue("stringfield", s);
            node.commit();
            bbNodes[nrOfBBNodes] = node;
            relation = relationManager.createRelation(aaNode1, node);
            relation.commit();
            nrOfBBNodes++;
        }
    }

    public void tearDown() {
        // Remove test nodes.
        aaNode1.delete(true);
        aaNode2.delete(true);
        bbNode.delete(true);
        for (int i = 0; i < nrOfBBNodes; i++) {
            bbNodes[i].delete(true);
        }
    }

    public void testClassSecurity() {
        assertEquals("admin", cloud.getUser().getIdentifier());
        assertEquals("foo", getCloud("foo").getUser().getIdentifier());
        assertEquals("admin", getCloud("admin").getUser().getIdentifier());
    }

    public void testGetList() {
        NodeList nodeList;
        nodeList = cloud.getList("" + aaNode1.getNumber(), "aa,bb", "aa.binaryfield", "", "", "", "", false);
        assertTrue(nodeList.size() == nrOfBBNodes);
    }

    public void testGetListWithNullParameters() {
        try {
            cloud.getList(null, null, null, null, null, null, null, false);
            fail("Should raise a BridgeException");
        } catch (BridgeException e) {}
    }

    public void testGetListWithEmptyParameters() {
        try {
            cloud.getList("", "", "", "", "", "", "", false);
            fail("Should raise a BridgeException");
        } catch (BridgeException e) {}
    }

    public void testGetListWithNullParameterStartNodes() {
        NodeList nodeList;
        nodeList = cloud.getList(null, "aa,bb", "aa.binaryfield", "", "", "", "", false);
        assertTrue(nodeList.size() == nrOfBBNodes + 1);
    }

    public void testGetListWithEmptyParameterStartNodes() {
        NodeList nodeList;
        nodeList = cloud.getList("", "aa,bb", "aa.binaryfield", "", "", "", "", false);
        assertTrue(nodeList.size() == nrOfBBNodes + 1);
    }

    /*
    This test is obsolete, the new API allows these queries

    public void testGetListWithInvalidParameterStartNodes() {
        try {
            NodeList nodeList;
            nodeList = cloud.getList("" + bbNode.getNumber(), "aa,bb", "aa.binaryfield", "", "", "", "", false);
            fail("Should raise a BridgeException, but gave following list: " + nodeList);
        } catch (BridgeException e) {}
    }
    */

    public void testGetListWithNullParameterNodePath() {
        try {
            cloud.getList(null, null, "binaryfield", "", "", "", "", false);
            fail("Should raise a BridgeException");
        } catch (BridgeException e) {}
    }

    public void testGetListWithEmptyParameterNodePath() {
        try {
            cloud.getList(null, "", "binaryfield", "", "", "", "", false);
            fail("Should raise a BridgeException");
        } catch (BridgeException e) {}
    }

    public void testGetListWithInvalidParameterNodePath() {
        try {
            cloud.getList(null, "x", "binaryfield", "", "", "", "", false);
            fail("Should raise a BridgeException");
        } catch (BridgeException e) {}
    }

    /*
    This test is now obsolete, the new API allows these queries
    public void testGetListWithNullParameterFields() {
        try {
            NodeList nodeList;
            nodeList = cloud.getList(null, "aa,bb", null, "", "", "", "", false);
            fail("Should raise a BridgeException, but returned: " + nodeList);
        } catch (BridgeException e) {}
    }

    */

    /*
    This test is now obsolete, the new API allows these queries
    public void testGetListWithEmptyParameterFields() {
        try {
            NodeList nodeList;
            nodeList = cloud.getList(null, "aa,bb", "", "", "", "", "", false);
            fail("Should raise a BridgeException, but returned: " + nodeList);
        } catch (BridgeException e) {}
    }
    */

    public void testGetListWithInvalidParameterFields() {
        try {
            NodeList nodeList;
            nodeList = cloud.getList(null, "aa,bb", "x", "", "", "", "", false);
            fail("Should raise a BridgeException, but returned: " + nodeList);
        } catch (BridgeException e) {}
    }

    public void testGetListWithConstraint() {
        cloud.getList(null, //java.lang.String startNodes,
        "aa,bb,aa", //java.lang.String nodePath,
        "bb.number,bb.owner", //java.lang.String fields,
        "bb.owner ='test'", //java.lang.String constraints,
        "bb.number", //java.lang.String orderby,
        "UP", //java.lang.String directions,
        "destination", //java.lang.String searchDir,
        false //boolean distinct
        );
    };

    public void testGetListWithConstraint2() {
        cloud.getList(null, //java.lang.String startNodes,
        "aa,bb,aa", //java.lang.String nodePath,
        "bb.number,bb.owner", //java.lang.String fields,
        "bb.number <>1234", //java.lang.String constraints,
        "bb.number", //java.lang.String orderby,
        "UP", //java.lang.String directions,
        "destination", //java.lang.String searchDir,
        false //boolean distinct
        );
    }
    /*
    public void testGetListWithQuery() {

        org.mmbase.cache.Cache cache = org.mmbase.cache.MultilevelCache.getCache();
        NodeList nodeList;
        Query query = new BasicSearchQuery();
        NodeManager a = cloud.getNodeManager("aa");
        Step stepa = query.addStep(a);
        RelationManager relationManager = cloud.getRelationManager("aa", "bb", "related");
        query.addRelationStep(relationManager, cloud.getNodeManager("bb"));
        query.addField(stepa, a.getField("stringfield"));
        nodeList = cloud.getList(query);
        assertTrue(cache.size() == 1);

    }
    */

    // Add some more list test.

}
