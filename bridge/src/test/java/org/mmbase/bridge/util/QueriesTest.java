/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.util;

import org.mmbase.bridge.*;
import org.mmbase.bridge.mock.*;
import org.mmbase.datatypes.*;
import org.mmbase.storage.search.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;


/**
   *
 * @author Michiel Meeuwissen
 * @version $Id$
 */
public class QueriesTest  {

    private static final String REMOTE_URI = "rmi://127.0.0.1:1111/exampleremotecontext";
    private static Cloud remoteCloud;

    public MockCloudContext getCloudContext() {
        return MockCloudContext.getInstance();
    }

    @BeforeClass
    public static void setup() throws Exception {
        MockCloudContext.getInstance().addCore();
        MockCloudContext.getInstance().addNodeManagers(MockBuilderReader.getBuilderLoader().getChildResourceLoader("mynews"));
        try {
            CloudContext c =  ContextProvider.getCloudContext(REMOTE_URI);
            remoteCloud = c.getCloud("mmbase", "class", null);
            System.out.println("Found remote cloud " + remoteCloud);
        } catch (Exception e) {
            System.err.println("Cannot get RemoteCloud. (" + e.getMessage() + "). Some tests will be skipped. (but reported as succes: see http://jira.codehaus.org/browse/SUREFIRE-542)");
            System.err.println("You can start up a test-environment for remote tests: trunk/example-webapp$ mvn jetty:run");
            remoteCloud = null;
        }
    }




    @Test
    public void constants() {

        assertTrue(Queries.getRelationStepDirection("destination") == RelationStep.DIRECTIONS_DESTINATION);
        assertTrue(Queries.getRelationStepDirection("SOURCE") == RelationStep.DIRECTIONS_SOURCE);
        try {
            Queries.getRelationStepDirection("bla");
            fail("Should have thrown exception");
        } catch (BridgeException be) {}

    }


    @Test
    public void nodeQuery1() {
        Cloud cloud = getCloudContext().getCloud("mmbase");
        {
            NodeManager object  = cloud.getNodeManager("object");
            NodeQuery q = object.createQuery();
            assertEquals("" + q.getFields(), object.getFields(NodeManager.ORDER_CREATE).size(), q.getFields().size());
            assertEquals("" + object.getFields(NodeManager.ORDER_CREATE), 3, q.getFields().size());
            StepField f = q.getStepField(object.getField("number"));
            assertNotNull(f);
        }
        {
            // making sure there is a virtual field (and it is virtual)
            NodeManager news = cloud.getNodeManager("news");
            assertTrue(news.getField("security_context").isVirtual());
        }

    }

    @Test
    public void nodeNodeQuery() {
        Cloud cloud = getCloudContext().getCloud("mmbase");
        NodeQuery q = cloud.createNodeQuery();
        NodeManager object  = cloud.getNodeManager("object");
        Step s = q.addStep(object);
        q.setNodeStep(s);
        assertEquals("" + q.getFields(), object.getFields(NodeManager.ORDER_CREATE).size(), q.getFields().size());
    }

    @Test
    public void nodeQuery2() {
        Cloud cloud = getCloudContext().getCloud("mmbase");
        Node node = cloud.getNodeManager("object").createNode();
        node.commit();
        Queries.createNodeQuery(node);
    }

    @Test
    public void createConstraint() {
        Cloud cloud = getCloudContext().getCloud("mmbase");
        NodeQuery q = cloud.getNodeManager("object").createQuery();
        Queries.createConstraint(q, "number", Queries.getOperator("LT"), 1);
    }

    @Test
    public void getSortOrderFieldValueSimple() {
        Cloud cloud = getCloudContext().getCloud("mmbase");
        Node node = cloud.getNodeManager("object").createNode();
        node.commit();

        NodeQuery q = cloud.getNodeManager("object").createQuery();
        SortOrder so = Queries.addSortOrders(q, "number", "UP").get(0);
        assertEquals("" + node.getNumber(), Queries.getSortOrderFieldValue(node, so).toString());
    }

    @Test
    public void createNodeQuery() {
        Cloud cloud = getCloudContext().getCloud("mmbase");
        Node node = cloud.getNodeManager("news").createNode();
        node.setStringValue("title", "foo");
        {
            NodeQuery q = Queries.createNodeQuery(node);
            assertEquals(1, q.getSteps().size());
            assertEquals("news", q.getSteps().get(0).getTableName());

            // createNodeQuery on a new node will leave the query unconstraint
            assertEquals(1, q.getSteps().get(0).getNodes().size());
            assertTrue(q.getSteps().get(0).getNodes().contains(node.getNumber()));
        }
        node.commit();
        {
            NodeQuery q = Queries.createNodeQuery(node);
            assertEquals(1, q.getSteps().size());
            assertEquals("news", q.getSteps().get(0).getTableName());
            assertEquals(1, q.getSteps().get(0).getNodes().size());
            assertTrue(q.getSteps().get(0).getNodes().contains(node.getNumber()));
        }

    }

    @Test
    public void createNodeQueryAfterSetNodeManager() {
        Cloud cloud = getCloudContext().getCloud("mmbase");
        Node node = cloud.getNodeManager("news").createNode();
        node.setStringValue("title", "foo");
        node.commit();

        node.setNodeManager(cloud.getNodeManager("mags"));
        assertEquals("mags", node.getNodeManager().getName());
        {
            NodeQuery q = Queries.createNodeQuery(node);
            assertEquals(1, q.getSteps().size());
            assertEquals("news", q.getSteps().get(0).getTableName());
            assertEquals(1, q.getSteps().get(0).getNodes().size());
            assertTrue(q.getSteps().get(0).getNodes().contains(node.getNumber()));
        }
        node.commit();
        assertEquals("mags", node.getNodeManager().getName());
        assertEquals("mags", cloud.getNode(node.getNumber()).getNodeManager().getName());

        {
            NodeQuery q = Queries.createNodeQuery(node);
            assertEquals(1, q.getSteps().size());
            assertEquals("mags", q.getSteps().get(0).getTableName());
            assertEquals(1, q.getSteps().get(0).getNodes().size());
            assertTrue(q.getSteps().get(0).getNodes().contains(node.getNumber()));
        }


    }


    // ================================================================================
    // Tests below this assume an RMMCI connection
    // ================================================================================

    @Test
    public void addToResult() {
        //Cloud cloud = getCloudContext().getCloud("mmbase");
        assumeNotNull(remoteCloud);
        Cloud cloud = remoteCloud;
        Node node = cloud.getNodeManager("news").createNode();
        node.setStringValue("title", "foo");
        node.commit();
        //NodeQuery q = Queries.createRelatedNodesQuery(node, otherNodeManager, role, direction);
        NodeQuery  q = Queries.createRelatedNodesQuery(node, cloud.getNodeManager("news"), "posrel", "destination");
        StepField pos = q.createStepField(q.getSteps().get(1), "pos");
        Constraint c = q.createConstraint(pos, 1);
        System.out.println("Query " + q.toSql());
    }



    @Test
    public void nodeComparationsAndSorting() {
        assumeNotNull(remoteCloud);
        Cloud cloud = remoteCloud;
        Node node = cloud.getNode("default.mags");
        NodeQuery q = Queries.createRelatedNodesQuery(node, cloud.getNodeManager("news"), "posrel", "destination");
        String before = q.toSql();
        List<SortOrder> sos = Queries.addSortOrders(q, "posrel.pos,number", "UP");
        Queries.sortUniquely(q);
        assertEquals(q.toSql() + sos, 2, sos.size());
        assertEquals("" + node.getNumber(), Queries.getSortOrderFieldValue(node, sos.get(1)).toString());


        NodeQuery clone = (NodeQuery) q.clone();
        Queries.addSortedFields(clone);

        assertEquals(clone.getNodeManager().getList(clone), q.getNodeManager().getList(q));

        NodeList nl = cloud.getList(clone);

        assumeTrue(nl.size() >= 2);

        Node multi1 = nl.get(0);
        assertEquals("" + multi1.getStringValue("posrel.pos"), Queries.getSortOrderFieldValue(multi1, sos.get(0)).toString());

        Node multi2 = nl.get(1);

        List<Node> multis = new ArrayList<Node>();
        multis.add(multi2);
        multis.add(multi1);

        Comparator<Node> comp = Queries.getComparator(q);

        Collections.sort(multis, comp);

        assertEquals(multi1, multis.get(0));
        assertEquals(multi2, multis.get(1));

    }


    @Test
    public void reorderResults() {
        assumeNotNull(remoteCloud);
        Cloud cloud = remoteCloud;
        Node node = cloud.getNode("default.mags");

        NodeQuery q = Queries.createRelatedNodesQuery(node, cloud.getNodeManager("news"), "posrel", "destination");
        Queries.addRelationFields(q, "posrel", "pos", "UP");

        List<Integer> nodeNumbers = new ArrayList<Integer>();

        for (Node n : q.getNodeManager().getList(q)) {
            nodeNumbers.add(n.getNumber());
        }

        assumeTrue(nodeNumbers.size() > 1);

        Collections.reverse(nodeNumbers);

        Queries.reorderResult(q, nodeNumbers);


        List<Integer> nodeNumbers2 = new ArrayList<Integer>();
        for (Node n : q.getNodeManager().getList(q)) {
            nodeNumbers2.add(n.getNumber());
        }

        assertEquals(nodeNumbers, nodeNumbers2);

    }


    String toString(List<Node> list, String pref) {
        StringBuilder b = new StringBuilder();
        for (Node n : list) {
            if (b.length() > 0) b.append(",");
            int i = n.getNumber();
            if (i != -1) {
                b.append("").append(i);
            } else {
                b.append(n.getStringValue(pref + ".number"));
            }
        }
        return b.toString();
    }

    void assertListEqual(List<Node> list1, String pref1, List<Node> list2, String pref2) {
        assertEquals(list1.size(), list2.size());

        for (int i = 0; i < list1.size(); i++) {
            Node n1 = list1.get(i);
            Node n2 = list2.get(i);
            if (pref1 != null) n1 = n1.getNodeValue(pref1 + ".number");
            if (pref2 != null) n2 = n2.getNodeValue(pref2 + ".number");

            assertEquals(n1.getNumber(), n2.getNumber());
        }
    }

    @Test
    public void getRelatedNodes() {
        assumeNotNull(remoteCloud);
        Cloud cloud = remoteCloud;
        Node node = cloud.getNode("default.mags");

        // must basic implementation used by e.g. mm:relatednodes
        NodeList relatedNodes =  Queries.getRelatedNodes(node, cloud.getNodeManager("news"), "posrel", "destination", "pos", "DOWN");


        // implementation based on NodeQuery
        NodeQuery q = Queries.createRelatedNodesQuery(node, cloud.getNodeManager("news"), "posrel", "destination");
        Queries.addSortOrders(q, "posrel.pos", "DOWN");
        Queries.sortUniquely(q);
        List<Node> relatedNodes2 = Queries.getRelatedNodesInTransaction(node, q); // outside a transaction it works too

        System.out.println(toString(relatedNodes, "news") + " =? " + toString(relatedNodes2, null));
        assertListEqual(relatedNodes, "news", relatedNodes2, null);

        int sizeBefore = relatedNodes2.size();

        // Now for the really insteresting stuff.
        {
            // Adding a node

            Transaction t = cloud.getTransaction("relatednodes1");
            Node magNode = t.getNode("default.mags");
            Node newNode = t.getNodeManager("news").createNode();
            newNode.setStringValue("title", "Test node of " + QueriesTest.class.getName());

            Queries.addToResult(q, newNode);

            //
            List<Node> relatedNodesInTransaction = Queries.getRelatedNodesInTransaction(magNode, q);

            assertEquals(sizeBefore + 1, relatedNodesInTransaction.size());

            assertTrue(relatedNodesInTransaction.contains(newNode));

            // order of posrel was DOWN, so this newNode should even be the first one in this list:

            assertEquals(newNode, relatedNodesInTransaction.get(0));
            t.cancel();
        }

        {
            // Deleting a node
            Transaction t = cloud.getTransaction("relatednodes2");
            Node magNode = t.getNode("default.mags");
            NodeList newsList = magNode.getRelatedNodes("news", "posrel", "destination");
            assumeTrue(newsList.size() > 0);

            Node news = newsList.get(0);
            news.delete(true);

            List<Node> relatedNodesInTransaction = Queries.getRelatedNodesInTransaction(magNode, q);

            assertEquals(sizeBefore - 1, relatedNodesInTransaction.size());

            assertFalse(relatedNodesInTransaction.contains(news));

            t.cancel();

        }
        // TODO test-case for relating a node to the source node of a different type. Should not appear in the result.
        // It doesn't now, btw. but it used to be a bug..


    }

    @Test
    public void getRelationNodes() {
        assumeNotNull(remoteCloud);
        Cloud cloud = remoteCloud;
        Node node = cloud.getNode("default.mags");
        // implementation based on NodeQuery
        NodeQuery q = Queries.createRelationNodesQuery(node, cloud.getNodeManager("news"), "posrel", "destination");
        Queries.addSortOrders(q, "pos,number", "DOWN");

        // must basic implementation used by e.g. mm:relatednodes
        NodeList relatedNodes = q.getNodeManager().getList(q);


        List<Node> relatedNodes2 = Queries.getRelatedNodesInTransaction(node, q); // outside a transaction it works too

        System.out.println(toString(relatedNodes, null) + " =? " + toString(relatedNodes2, null));
        assertListEqual(relatedNodes, null, relatedNodes2, null);

        int sizeBefore = relatedNodes2.size();

        // Now for the really insteresting stuff.
        {
            // Adding a node

            Transaction t = cloud.getTransaction("relationnodes1");
            Node magNode = t.getNode("default.mags");
            Node newNode = t.getNodeManager("news").createNode();
            newNode.setStringValue("title", "Test node of " + QueriesTest.class.getName());

            Node newRelation = Queries.addToResult(q, newNode).get(0);

            //
            List<Node> relatedNodesInTransaction = Queries.getRelatedNodesInTransaction(magNode, q);

            assertEquals(sizeBefore + 1, relatedNodesInTransaction.size());

            assertTrue(relatedNodesInTransaction.contains(newRelation)); // It should be the relation
            assertFalse(relatedNodesInTransaction.contains(newNode)); // Not the related node


            // order of posrel was DOWN, so this newNode should even be the first one in this list:

            assertEquals(newRelation, relatedNodesInTransaction.get(0));
            t.cancel();
        }


    }
}
