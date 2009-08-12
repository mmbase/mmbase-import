/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.virtual;

import org.mmbase.bridge.*;
import org.mmbase.datatypes.*;
import java.util.*;
import junit.framework.*;


/**
 * Test class <code>CloudContext</code> from the bridge package.
 *
 * @author Michiel Meeuwissen
 * @version $Id: CloudContextTest.java 37077 2009-07-21 17:28:12Z michiel $
 */
public class VirtualTest extends TestCase {

    public VirtualTest(String name) {
        super(name);
    }
    public CloudContext getCloudContext() {
        return VirtualCloudContext.getCloudContext();
    }

    public void testListClouds() {
        CloudContext cloudContext = getCloudContext();
        boolean defaultCloudFound = false;
        StringList stringList = cloudContext.getCloudNames();
        for (int i = 0; i < stringList.size(); i++) {
            Cloud cloud = cloudContext.getCloud(stringList.getString(i));
            if (cloud.getName().equals("mmbase")) {
                defaultCloudFound = true;
            }
        }
        assertTrue(defaultCloudFound);
    }
    public void testUri() {

        CloudContext cloudContext = getCloudContext();
        Cloud cloud = cloudContext.getCloud("mmbase");
        assertEquals("" + cloud.getCloudContext().getClass() + " " + cloudContext.getClass(),
                     cloudContext.getUri(),
                     cloud.getCloudContext().getUri());

    }

    public void testNodeManager() {
        Cloud cloud = getCloudContext().getCloud("mmbase");
        Map<String, DataType> map = new HashMap<String, DataType>();
        map.put("number", Constants.DATATYPE_INTEGER);
        map.put("title", Constants.DATATYPE_STRING);
        VirtualCloudContext.addNodeManager("aa", map);

        NodeManager aa = cloud.getNodeManager("aa");
        Node a = aa.createNode();
        a.setStringValue("title", "bloe");
        a.commit();

        assertTrue(a.getNumber() > 0);

        int number = a.getNumber();

        Node a2 = cloud.getNode(number);
        assertEquals("bloe", a2.getStringValue("title"));

    }

}
