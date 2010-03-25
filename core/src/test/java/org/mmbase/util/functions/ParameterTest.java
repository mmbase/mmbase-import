/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.functions;
import org.mmbase.datatypes.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.mock.*;
import java.util.*;
import java.util.regex.*;
import org.junit.*;
import static org.junit.Assert.*;
import org.mmbase.util.logging.Logging;

/**
 *
 * @author Michiel Meeuwissen
 * @verion $Id$
 */
public class ParameterTest {

    @BeforeClass
    public static void setup() throws Exception {
        MockCloudContext.getInstance().addCore();
        DataTypes.initialize();
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
        param.autoCast("a2"); // and can also be casted.

        assertEquals("red", param.autoCast("red"));
    }

    @Test
    public void autoCastNodeEnumeration() throws Exception {
        Parameter<Node> param = new Parameter<Node>("a", DataTypes.getDataType("typedef"));
        org.mmbase.bridge.Node typedef =  MockCloudContext.getInstance().getCloud("mmbase").getNodeManager("typedef").getList(null).getNode(0);
        org.mmbase.bridge.Node news =  MockCloudContext.getInstance().getCloud("mmbase").getNodeManager("news").createNode();
        news.setStringValue("title", "bla");
        news.commit();

        param.checkType(typedef);
        param.checkType(news); // it _is_ of the correct type (namely a node)

        param.autoCast(typedef);
        assertTrue(param.getDataType().validate(news, null, null).size() > 0);
    }

}
