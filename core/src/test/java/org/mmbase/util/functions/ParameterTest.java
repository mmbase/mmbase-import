/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.functions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.mock.MockBuilderReader;
import org.mmbase.bridge.mock.MockCloudContext;
import org.mmbase.bridge.util.CloudThreadLocal;
import org.mmbase.datatypes.CastException;
import org.mmbase.datatypes.DataTypes;

/**
 *
 * @author Michiel Meeuwissen
 * @verion $Id$
 */
public class ParameterTest {

    @BeforeClass
    public static void setup() throws Exception {
        DataTypes.initialize();
        MockCloudContext.getInstance().addCore();
        MockCloudContext.getInstance().addNodeManagers(MockBuilderReader.getBuilderLoader().getChildResourceLoader("mynews"));
        MockCloudContext.getInstance().addNodeManagers(MockBuilderReader.getBuilderLoader().getChildResourceLoader("tests"));
    }

    @Test
    public void autoCastInteger() throws Exception {
        Parameter<Integer> param = new Parameter<Integer>("a", Integer.class);
        try {
            param.checkType("a1");
            fail();
        } catch (IllegalArgumentException ie) {
        }
        try {
            param.autoCast("a2");
            fail();
        } catch (CastException ie) {
        }
        assertEquals(Integer.valueOf(1), param.autoCast("1"));
    }

    @Test
    public void autoCastEnumeration() throws Exception {
        Parameter<String> param = new Parameter<String>("a", DataTypes.getDataType("colors"));

        param.checkType("just a string"); // it _is_ of the correct type

        try {
            param.autoCast("a2"); // it cannot be casted though
            fail();
        } catch (CastException ie) {
        }
        assertEquals("red", param.autoCast("red"));
    }

    @Test
    public void autoCastNodeEnumeration() throws Exception {
        Parameter<Node> param = new Parameter<Node>("a", DataTypes.getDataType("typedef"));
        Cloud cloud = MockCloudContext.getInstance().getCloud("mmbase");
        CloudThreadLocal.bind(cloud);
        org.mmbase.bridge.Node typedef =  cloud.getNodeManager("typedef").getList(null).getNode(0);
        org.mmbase.bridge.Node news =  MockCloudContext.getInstance().getCloud("mmbase").getNodeManager("news").createNode();
        news.setStringValue("title", "bla");
        news.commit();

        param.checkType(typedef);
        param.checkType(news); // it _is_ of the correct type (namely a node)

        param.autoCast(typedef);
        try {
            param.autoCast(news);
            fail("Node " + news + " should not have been valid for " + DataTypes.getDataType("typedef"));
        } catch(CastException ce) {
            // but it cannot be casted.
        }
    }

}
