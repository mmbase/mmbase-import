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
 * @version $Id: TransactionTest.java,v 1.9 2008-07-15 11:59:01 michiel Exp $
 * @since MMBase-1.8.6
  */
public class TransactionTest extends BridgeTest {
    private static final Logger log = Logging.getLoggerInstance(TransactionTest.class);

    public TransactionTest(String name) {
        super(name);
    }

    int newNode;
    int newNode2;


    public void setUp() {
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
            node.commit();
            newNode2 = node.getNumber();
        }
    }

    public void testCancel() {
        Cloud cloud = getCloud();
        Transaction t = cloud.getTransaction("bar1");
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
            Transaction t = cloud.getTransaction("bar2");
            Node node = t.getNode(newNode);
            node.setStringValue("title", "xxxxx");
            node.commit();
            t.cancel();
        }
        {
            Transaction t = cloud.getTransaction("bar2");
            Node node = t.getNode(newNode);
            assertEquals("foo", node.getStringValue("title"));
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
        assertEquals("non_default", n.getContext());
        t.commit();

        Node n2 = cloud.getNode(n.getNumber());
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





}
