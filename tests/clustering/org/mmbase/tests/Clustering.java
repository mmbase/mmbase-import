/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.tests;
import junit.framework.TestCase;
import java.util.*;
import org.mmbase.bridge.*;

/**

 * @author Michiel Meeuwissen
 */
public class Clustering extends TestCase {

    int tryCount = 0;
    protected Cloud cloud1;
    protected Cloud cloud2;
    public void setUp() {
        while(true) {
            try {
                cloud1 =   ContextProvider.getCloudContext("rmi://127.0.0.1:1221/remotecontext").getCloud("mmbase", "class", null);
                cloud2 =   ContextProvider.getCloudContext("rmi://127.0.0.1:1222/remotecontext").getCloud("mmbase", "class", null);
                break;
            } catch (BridgeException be) {
                System.out.println(be.getMessage() + ". Perhaps mmbase not yet running, retrying in 5 seconds");
                try {
                    tryCount ++;
                    if (tryCount > 25) break;
                    Thread.sleep(5000);
                } catch (Exception ie) {}
            }
        }
    }

    public void fieldEquals(Node n1, Node n2) {
        FieldIterator fi = n1.getNodeManager().getFields(NodeManager.ORDER_CREATE).fieldIterator();
        while (fi.hasNext()) {
            Field f = fi.nextField();
            Object value1 = n1.getValue(f.getName());
            Object value2 = n2.getValue(f.getName());
            assertTrue("" + value1 + " != " + value2 + " (value of " + n1.getNumber() + "/" + f.getName() + ")", value1 == null ? value2 == null : value1.equals(value2));
        }
    }

    public void testCreateNode() {
        NodeManager aa1 = cloud1.getNodeManager("aa");
        NodeManager aa2 = cloud2.getNodeManager("aa");
        NodeList    aa2list = aa2.getList(null, null, null); // cache list result

        Node nodea1 = aa1.createNode();
        nodea1.commit();
        Node nodea2 = cloud2.getNode(nodea1.getNumber());
        fieldEquals(nodea1, nodea2);

        NodeList aa2list2 = aa2.getList(null, null, null);
        assertTrue("Check wheter node-list got invalidated failed", aa2list2.size() == aa2list.size() + 1);
    }

    public void testList() {
    }
}
