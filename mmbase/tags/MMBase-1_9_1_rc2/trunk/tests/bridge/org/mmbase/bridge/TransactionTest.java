/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

import org.mmbase.tests.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * Test class <code>Transaction</code> from the bridge package.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8.6
  */
public class TransactionTest extends BridgeTest {
    private static final Logger log = Logging.getLoggerInstance(TransactionTest.class);

    public TransactionTest(String name) {
        super(name);
    }

    static int seq = 0;
    int newNode;
    int newNode2;


    public void setUp() {
        seq++;
        // Create some test nodes
        Cloud cloud = getCloud();
        {
            Node node = cloud.getNodeManager("news").createNode();
            node.setStringValue("title", "foo");
            node.commit();
            newNode = node.getNumber();
        }
        {
            Node node = cloud.getNodeManager("news").createNode();
            node.setStringValue("title", "foo");
            node.createAlias("test.news." + seq);
            node.setContext("default");
            node.commit();
            newNode2 = node.getNumber();
        }
    }

    public void testCancel() {
        Cloud cloud = getCloud();
        Transaction t = cloud.getTransaction("cancel1");
        Node node = t.getNode(newNode);
        node.setStringValue("title", "xxxxx");
        node.commit();
        t.cancel();

        node = cloud.getNode(newNode);

        assertEquals("foo", node.getStringValue("title"));
    }


    public void testCancel2() {
        Cloud cloud = getCloud();
        {
            Transaction t = cloud.getTransaction("cancel2");
            Node node = t.getNode(newNode);
            node.setStringValue("title", "xxxxx");
            node.commit();
            t.cancel();
        }
        {
            Transaction t = cloud.getTransaction("cancel2");
            Node node = t.getNode(newNode);
            assertEquals("foo", node.getStringValue("title"));
            t.cancel();
        }
    }

    public void testGetTransaction() {
        Cloud cloud = getCloud();

        {
            Transaction t = cloud.getTransaction("gettransactiontest");
            Node node = t.getNode(newNode);
            node.setStringValue("title", "xxxxx");
        }
        {
            Transaction t = cloud.getTransaction("gettransactiontest");
            Node node = t.getNode(newNode);
            assertEquals("xxxxx", node.getStringValue("title"));
            t.cancel();
        }
    }

    public void testCommit() {
        Cloud cloud = getCloud();
        Transaction t = cloud.getTransaction("bar3");
        Node node = t.getNode(newNode);
        node.setStringValue("title", "yyyyy");
        node.commit();
        t.commit();

        node = cloud.getNode(newNode);

        assertEquals("yyyyy", node.getStringValue("title"));
    }

    public void testMMB1546() {
        Cloud cloud = getCloud();
        Transaction t = cloud.getTransaction("test0");
        Node nt = t.getNode(newNode);
        nt.setValue("title", "bla");
        //t.cancel(); _DONT_ cancel
        Node nc = cloud.getNode(newNode);
        nc.setValue("title", "bloe");
        nc.commit();
        assertEquals("bloe", nc.getStringValue("title"));
        assertEquals("bloe", cloud.getNode(newNode).getStringValue("title"));
        t.cancel();
        assertEquals("bloe", cloud.getNode(newNode).getStringValue("title"));

    }


    //Test for http://www.mmbase.org/jira/browse/MMB-1621
    public void testGetValue() {
        Cloud cloud = getCloud();
        String value = cloud.getNode(newNode).getStringValue("title");

        Transaction t = cloud.getTransaction("bar4");
        Node node = t.getNode(newNode);

        node.setStringValue("title", "zzzzz");
        node.commit(); // committing inside transaction

        assertEquals(value, cloud.getNode(newNode).getStringValue("title"));

        t.commit();
        assertEquals("zzzzz", cloud.getNode(newNode).getStringValue("title"));

    }
    public void testReuseTransaction() {
        Cloud cloud = getCloud();
        Transaction t = cloud.getTransaction("bar4");
        Node node = t.getNode(newNode);
        node.setStringValue("title", "wwwwww");
        node.commit();
        t.cancel();

    }

    public void testCancelDelete() {
        Cloud cloud = getCloud();
        Transaction t = cloud.getTransaction("bar5");
        Node node = t.getNode(newNode);
        node.delete();
        t.cancel();
        assertTrue(cloud.hasNode(newNode));

    }

    public void testCommitDelete() {
        Cloud cloud = getCloud();
        Transaction t = cloud.getTransaction("bar6");
        Node node = t.getNode(newNode);
        node.delete();
        t.commit();
        assertFalse(cloud.hasNode(newNode));

    }

    public void testSetContext() {
        Cloud cloud = getCloud();
        Transaction t = cloud.getTransaction("bar7");
        Node n = t.getNodeManager("news").createNode();
        n.setContext("non_default");
        assertEquals("non_default", n.getStringValue("owner"));
        assertEquals("non_default", n.getContext());

        t.commit();

        Node n2 = cloud.getNode(n.getNumber());
        assertEquals("non_default", n2.getStringValue("owner"));
        assertEquals("non_default", n2.getContext());

    }

    public void testSetContextSubTransaction() {
        Cloud cloud = getCloud();

        Transaction ot = cloud.getTransaction("bar8");
        Transaction t = ot.getTransaction("bar9");
        Node n = t.getNodeManager("news").createNode();
        n.setContext("non_default");
        assertEquals("non_default", n.getContext());
        t.commit();

        Node n2 = ot.getNode(n.getNumber());
        assertEquals("non_default", n2.getContext());

        ot.commit();
        Node n3 = cloud.getNode(n.getNumber());
        assertEquals("non_default", n3.getContext());
    }

    public void testEditNodeOutsideTransaction() {
        Cloud cloud = getCloud();
        Transaction t = cloud.getTransaction("bar10");
        Node nodeInTransaction = t.getNode(newNode2);
        nodeInTransaction.setStringValue("title", "foo2");
        Node nodeOutTransaction = cloud.getNode(newNode2);
        nodeOutTransaction.setStringValue("title", "bar2");

        nodeOutTransaction.commit();
        t.commit();

        // transaction was committed _later_ so its commit of the node must have won
        assertEquals("foo2", cloud.getNode(newNode2).getStringValue("title"));
        assertEquals("foo2", nodeInTransaction.getStringValue("title"));
        //assertEquals("foo2", nodeOutTransaction.getStringValue("title")); // not sure what this should have done, but anyhow, it now fails

    }
    public void testEditNodeOutsideTransaction2() {
        Cloud cloud = getCloud();
        Transaction t = cloud.getTransaction("bar10");
        Node nodeInTransaction = t.getNode(newNode2);
        nodeInTransaction.setStringValue("title", "foo2");
        Node nodeOutTransaction = cloud.getNode(newNode2);
        nodeOutTransaction.setStringValue("title", "bar2");

        t.commit();
        nodeOutTransaction.commit();

        // transaction was committed _earlier_ so the commit of the node must have won
        assertEquals("bar2", cloud.getNode(newNode2).getStringValue("title"));
        //assertEquals("bar2", nodeInTransaction.getStringValue("title"));// not sure what this should have done, but anyhow, it now fails
        assertEquals("bar2", nodeOutTransaction.getStringValue("title"));

    }

    public void testDeleteNodeOutsideTransaction() {
        Cloud cloud = getCloud();
        Transaction t = cloud.getTransaction("bar11");
        Node nodeInTransaction = t.getNode(newNode2);
        nodeInTransaction.setStringValue("title", "foo2");
        {
            // now delete the node
            Node nodeOutTransaction = cloud.getNode(newNode2);
            nodeOutTransaction.delete();
            assertFalse(cloud.hasNode(newNode2));
        }

        try {
            t.commit();
        } catch (Exception e) {
            // should not give exception. MMB-1680
            log.error(e.getMessage(), e);
            fail(e.getMessage());
        }

        assertTrue(cloud.hasNode(newNode2));
    }

    // same case as above, only no changes are made to the node.
    public void testDeleteNodeOutsideTransactionNodeInTransactionButNotChanged() {
        Cloud cloud = getCloud();
        Transaction t = cloud.getTransaction("bar11");
        Node nodeInTransaction = t.getNode(newNode2);
        //nodeInTransaction.setStringValue("title", "foo2");
        {
            // now delete the node
            Node nodeOutTransaction = cloud.getNode(newNode2);
            nodeOutTransaction.delete();
            assertFalse(cloud.hasNode(newNode2));
        }

        try {
            // make a relation to the (deleted) node, but in the transaction, where the node still
            // exists.
            // This demonstrate that there is an actual problem if the node ends up non-existing now.
            Node url = t.getNodeManager("urls").createNode();
            RelationManager rm = t.getRelationManager("news", "urls", "posrel");
            Relation r = nodeInTransaction.createRelation(url, rm);
            t.commit();
        } catch (Exception e) {
            // should not give exception. MMB-1680
            log.error(e.getMessage(), e);
            fail(e.getMessage());
        }

        assertTrue(cloud.hasNode(newNode2));
        assertEquals(1, cloud.getNode(newNode2).countRelations());
    }



    public void testAlias() {
        Cloud cloud = getCloud();
        {
            Node node = cloud.getNode(newNode2);
            node.setStringValue("title", "abcdef");
            node.commit();
            assertEquals("abcdef", node.getStringValue("title"));
            node = cloud.getNode("test.news." + seq);
            assertEquals("abcdef", node.getStringValue("title"));
        }

        {
            Transaction t = cloud.getTransaction("bar12");

            Node node = t.getNode(newNode2);
            node.setStringValue("title", "abcdefg");
            node.commit();
            assertEquals("abcdefg", node.getStringValue("title"));
            t.commit();
            assertEquals("abcdefg", node.getStringValue("title"));

            node = cloud.getNode("test.news." + seq);
            assertEquals("abcdefg", node.getStringValue("title"));

            node = cloud.getNode(newNode2);
            assertEquals("abcdefg", node.getStringValue("title"));
        }



    }

    public void testGetNodeTwiceWhileChanged() {
        Cloud cloud1 = getCloud();
        {
            Node node = cloud1.getNode(newNode2);
            String title1 = node.getStringValue("title");
            node.setStringValue("title", "bla bla");
            // don't commit
        }
        // now change something by someone else
        {
            Cloud cloud2 = getCloud("foo");
            assertTrue(cloud1 != cloud2);
            Node node = cloud2.getNode(newNode2);
            node.setStringValue("title", "new title value");
            node.commit();
        }
        // now look to the original cloud
        {
            Node node = cloud1.getNode(newNode2);
            assertEquals("new title value", node.getStringValue("title"));
        }

    }

    public void testCreateRelationBetweenNewNodes() {
        Cloud cloud = getCloud();
        Transaction t = cloud.getTransaction("createrelationtrans");
        NodeManager news = t.getNodeManager("news");
        Node n = news.createNode();
        NodeManager urls = t.getNodeManager("urls");
        Node url = urls.createNode();
        RelationManager rm = t.getRelationManager("news", "urls", "posrel");
        Relation r = n.createRelation(url, rm);
        t.commit();
    }

    // new node as argument
    public void testCreateRelationToNewNode() {
        Cloud cloud = getCloud();
        Transaction t = cloud.getTransaction("createrelationtrans");
        Node n = t.getNode(newNode);
        NodeManager urls = t.getNodeManager("urls");
        Node url = urls.createNode();
        RelationManager rm = t.getRelationManager("news", "urls", "posrel");
        Relation r = n.createRelation(url, rm);
        t.commit();
    }

    // old node as argument
    public void testCreateRelationToNewNode2() {
        Cloud cloud = getCloud();
        Transaction t = cloud.getTransaction("createrelationtrans");
        Node n = t.getNode(newNode);
        NodeManager urls = t.getNodeManager("urls");
        Node url = urls.createNode();
        RelationManager rm = t.getRelationManager("urls", "news", "posrel");
        Relation r = url.createRelation(n, rm);
        t.commit();
    }


    public void testTransactionsAreEqual() {
        Cloud cloud = getCloud();
        Transaction t1 = cloud.getTransaction("testequals");
        Transaction t2 = cloud.getTransaction("testequals");
        assertEquals(t1, t2);
        Node n = t1.getNode(newNode);
        NodeManager urls = t2.getNodeManager("urls");
        Node url = urls.createNode();
        RelationManager rm = t2.getRelationManager("urls", "news", "posrel");
        Relation r = url.createRelation(n, rm);
        t2.commit();
        assertTrue(t2.isCommitted());
        assertTrue(t1.isCommitted());
        // assertTrue(t1 == t2); // FAILS IN RMMCI. Perhaps we should simply implement .equals on transactions
    }


}
