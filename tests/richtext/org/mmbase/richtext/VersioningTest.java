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
 * @verion $Id: VersioningTest.java,v 1.1 2008-11-18 12:58:16 michiel Exp $
 */
public class VersioningTest extends BridgeTest {



    protected void testVersionField(Cloud c) {
        NodeManager nm = c.getNodeManager("wiki_news");
        Node node = nm.createNode();
        assertEquals(0, node.getIntValue("version"));
        node.commit();
        int nn = node.getNumber();
        node = c.getNode(nn);
        node.setStringValue("title", "" + System.currentTimeMillis());
        node.commit();
        assertEquals(1, node.getIntValue("version"));

    }

    public void testVersionFieldCloud() {
        testVersionField(getCloud());
    }

    public void testVersionFieldTransaction() {
        testVersionField(getCloud().createTransaction("testtest"));
    }

}

