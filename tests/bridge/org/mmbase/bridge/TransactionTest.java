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
 * @version $Id: TransactionTest.java,v 1.3 2008-03-17 09:30:31 michiel Exp $
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

    public void testGetValue() {
        Cloud cloud = getCloud();
        Transaction t = cloud.getTransaction("bar4");
        Node node = t.getNode(newNode);
        node.setStringValue("title", "zzzzz");
        node.commit(); // committing inside transaction

        assertEquals("yyyyy", cloud.getNode(newNode).getStringValue("title")); // TODO TODO, I think
                                                                               // this is failing.

        t.commit();
        assertEquals("zzzzz", cloud.getNode(newNode).getStringValue("title"));

    }

}
