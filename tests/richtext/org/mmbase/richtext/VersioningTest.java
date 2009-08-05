/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.richtext;

import org.mmbase.bridge.*;
import org.mmbase.tests.*;

/**
 *
 * @author Michiel Meeuwissen
 * @verion $Id: VersioningTest.java,v 1.2 2008-11-18 13:07:00 michiel Exp $
 */
public class VersioningTest extends BridgeTest {



    protected Transaction getTransaction(Cloud c) {
        return c.createTransaction("testtest");
    }
    protected Node testVersionField1(Cloud c) {
        NodeManager nm = c.getNodeManager("wiki_news");
        Node node = nm.createNode();
        assertEquals(0, node.getIntValue("version"));
        node.commit();
        return node;
    }
    protected void testVersionField2(Cloud c, int nn) {
        Node node = c.getNode(nn);
        node.setStringValue("title", "" + System.currentTimeMillis());
        node.commit();
        assertEquals(1, node.getIntValue("version"));

    }



    public void testVersionFieldCloud() {
        Cloud c = getCloud();
        Node n = testVersionField1(c);
        n.commit();
        int nn = n.getNumber();
        assertTrue(nn > 0);
        testVersionField2(c, nn);
    }

    public void testVersionFieldTransaction() {
        Transaction t = getTransaction(getCloud());
        Node n = testVersionField1(t);
        n.commit();
        t.commit();
        int nn = n.getNumber();
        assertTrue(nn > 0);
        t = getTransaction(getCloud());
        testVersionField2(t, nn);

    }

}

