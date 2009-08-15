/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.dummy;

import org.mmbase.bridge.*;
import org.mmbase.datatypes.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;


/**
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */
public class DummyTest  {

    public CloudContext getCloudContext() {
        return DummyCloudContext.getInstance();
    }
    @BeforeClass()
    public static void setUp() {
        DummyCloudContext.getInstance().clear();
    }


    @Test
    public void listClouds() {
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
    @Test
    public void uri() {

        CloudContext cloudContext = getCloudContext();
        Cloud cloud = cloudContext.getCloud("mmbase");
        assertEquals("" + cloud.getCloudContext().getClass() + " " + cloudContext.getClass(),
                     cloudContext.getUri(),
                     cloud.getCloudContext().getUri());

    }

    @Test
    public void nodeManager() {
        Cloud cloud = getCloudContext().getCloud("mmbase");
        Map<String, DataType> map = new HashMap<String, DataType>();
        map.put("number", Constants.DATATYPE_INTEGER);
        map.put("title", Constants.DATATYPE_STRING);
        DummyCloudContext.getInstance().addNodeManager("aa", map);

        NodeManager aa = cloud.getNodeManager("aa");
        Node a = aa.createNode();
        a.setStringValue("title", "bloe");
        a.commit();

        assertTrue(a.getNumber() > 0);

        int number = a.getNumber();

        Node a2 = cloud.getNode(number);
        assertEquals("bloe", a2.getStringValue("title"));


        assertEquals(1, cloud.getNodeManagers().size());

    }

    @Test
    public void builderReader() throws Exception {
        DummyCloudContext.getInstance().addCore();
        Cloud cloud = getCloudContext().getCloud("mmbase");

        assertTrue(cloud.hasNodeManager("object"));

        NodeManager object = cloud.getNodeManager("object");
        assertTrue("" + DummyCloudContext.getInstance().nodeManagers.get("object"), object.hasField("number"));
        assertTrue(object.hasField("otype"));
        assertTrue(object.hasField("owner"));
        assertTrue(object.hasField("_number"));
    }

    @Test
    public void addNodeManagers() throws Exception {
        DummyCloudContext cc = new DummyCloudContext();
        cc.addNodeManagers(DummyBuilderReader.getBuilderLoader().getChildResourceLoader("core"));

        Cloud cloud = cc.getCloud("mmbase");
        assertTrue("" + cc.nodeManagers + " " + cc.builders, cloud.hasNodeManager("object"));

        NodeManager object = cloud.getNodeManager("object");
        assertTrue(object.hasField("number"));
        assertTrue(object.hasField("otype"));
        assertTrue(object.hasField("owner"));

        assertEquals("number", object.getField("number").getName());
    }


}
