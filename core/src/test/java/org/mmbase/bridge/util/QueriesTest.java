/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.util;

import org.mmbase.bridge.*;
import org.mmbase.bridge.dummy.*;
import org.mmbase.datatypes.*;
import org.mmbase.storage.search.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;


/**
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */
public class QueriesTest  {

    public CloudContext getCloudContext() {
        return DummyCloudContext.getInstance();
    }

    @BeforeClass
    public static void setup() throws Exception {
        DummyCloudContext.getInstance().clear();
        DummyCloudContext.getInstance().addCore();
        DummyCloudContext.getInstance().addNodeManagers(DummyBuilderReader.getBuilderLoader().getChildResourceLoader("mynews"));
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
        Queries.createConstraint(q, "number", Queries.getOperator("LT"), new Integer(1));
    }

}
