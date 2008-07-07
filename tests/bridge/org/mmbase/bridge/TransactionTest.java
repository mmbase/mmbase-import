/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

import org.mmbase.tests.*;

/**
 * Test class <code>Transaction</code> from the bridge package.
 *
 * @author Michiel Meeuwissen
 * @version $Id: TransactionTest.java,v 1.6 2008-07-07 15:03:49 michiel Exp $
 * @since MMBase-1.8.6
  */
public class TransactionTest extends BridgeTest {

    public TransactionTest(String name) {
        super(name);
    }

    int newNode;


    public void setUp() {
        // Create some test nodes
        Cloud cloud = getCloud();
        Node node = cloud.getNodeManager("news").createNode();
        node.setStringValue("title", "foo");
        node.commit();
        newNode = node.getNumber();
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
        n.setContext("default");
        assertEquals("default", n.getContext());
        t.commit();

        Node n2 = cloud.getNode(n.getNumber());
        assertEquals("default", n2.getContext());
    }



}
