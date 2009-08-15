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
 * @version $Id: DummyTest.java 37837 2009-08-14 21:45:04Z michiel $
 */
public class QueriesTest  {

    public CloudContext getCloudContext() {
        return DummyCloudContext.getInstance();
    }

    @BeforeClass
    public static void setup() throws Exception {
        DummyCloudContext.getInstance().clear();
        DummyCloudContext.getInstance().addCore();
    }



    @Test
    public void nodeQuery1() {
        Cloud cloud = getCloudContext().getCloud("mmbase");
        NodeManager object  = cloud.getNodeManager("object");
        NodeQuery q = object.createQuery();
        assertEquals(object.getFields().size(), q.getFields().size());
        StepField f = q.getStepField(object.getField("number"));
        assertNotNull(f);

    }

    @Test
    public void nodeQuery2() {
        Cloud cloud = getCloudContext().getCloud("mmbase");
        NodeQuery q = cloud.createNodeQuery();
        q.addStep(cloud.getNodeManager("object"));
    }

    @Test
    public void nodeQuery() {
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
