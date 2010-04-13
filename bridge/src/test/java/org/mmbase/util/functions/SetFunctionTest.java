/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.functions;
import org.mmbase.datatypes.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.mock.MockCloudContext;
import org.mmbase.bridge.mock.MockBuilderReader;
import org.mmbase.util.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Michiel Meeuwissen
 * @verion $Id$
 */
public class SetFunctionTest {

    @BeforeClass
    public static void setup() throws Exception {
        DataTypes.initialize();
        MockCloudContext.getInstance().addCore();
        MockCloudContext.getInstance().addNodeManagers(MockBuilderReader.getBuilderLoader().getChildResourceLoader("mynews"));
    }

    public static String testFunction() {
        return "aa";
    }

    public static String testFunction(int a) {
        return "aa" + a;
    }
    public static String testFunction(int a, int b, String c, String d) {
        return "aa" + a;
    }


    public static String testFunction(Cloud cloud, int nodeNumber) {
        return "aa" + cloud.getNode(nodeNumber).getNumber();
    }

    @Test
    public void getMethod() {
        assertNotNull(SetFunction.getMethod(SetFunctionTest.class, "testFunction", new Parameters()));
        assertNotNull(SetFunction.getMethod(SetFunctionTest.class, "testFunction", new Parameters(new Parameter<Integer>("A", int.class, 7))));
        assertNotNull(SetFunction.getMethod(SetFunctionTest.class, "testFunction", new Parameters(new Parameter<Integer>("A", Integer.class, 8))));
        assertNotNull(SetFunction.getMethod(SetFunctionTest.class, "testFunction", new Parameters(new Parameter<Node>("A", Node.class))));
        assertNotNull(SetFunction.getMethod(SetFunctionTest.class, "testFunction", new Parameters(Parameter.CLOUD, new Parameter<Node>("A", Node.class))));
    }


    @Test
    public void noParameters() {
        SetFunction function = new SetFunction("test", new Parameter[] {}, null, SetFunctionTest.class, "testFunction", SetFunction.Type.CLASS);
        Parameters params = function.createParameters();
        assertEquals("aa", function.getFunctionValue(params));
    }

    @Test
    public void parametersMatch() {
        SetFunction function = new SetFunction("test", new Parameter[] {new Parameter<Integer>("A", int.class, 5)}, null, SetFunctionTest.class, "testFunction", SetFunction.Type.CLASS);
        Parameters params = function.createParameters();
        assertEquals("aa5", function.getFunctionValue(params));
        params.set("A", 6);
        assertEquals("aa6", function.getFunctionValue(params));
    }


    @Test
    public void parametersDontMatch() {
        // int.class != Integer.class but are trivially casted
        SetFunction function = new SetFunction("test", new Parameter[] {new Parameter<Integer>("A", Integer.class, 5)}, null, SetFunctionTest.class, "testFunction", SetFunction.Type.CLASS);
        Parameters params = function.createParameters();
        assertEquals("aa5", function.getFunctionValue(params));
        params.set("A", 6);
        assertEquals("aa6", function.getFunctionValue(params));
    }

    @Test
    public void typedef() {
        SetFunction function = new SetFunction("test", new Parameter[] {Parameter.CLOUD, new Parameter<Node>("A", DataTypes.getDataType("typedef"))}, null, SetFunctionTest.class, "testFunction", SetFunction.Type.CLASS);
        Parameters params = function.createParameters();
        params.setAutoCasting(true);
        Cloud cloud = MockCloudContext.getInstance().getCloud("mmbase");
        params.set(Parameter.CLOUD, cloud);

        {
            Node typedef = cloud.getNodeManager("typedef").getList(null).getNode(0);
            params.set("A", "" + typedef.getNumber());
            Collection<LocalizedString> errors = params.validate();
            assertEquals("" + errors, 0, errors.size());
            assertEquals("aa" + typedef.getNumber(), function.getFunctionValue(params));
        }
        {
            Node news = cloud.getNodeManager("news").createNode();
            news.setStringValue("title", "foobar");
            news.commit();

            try {
                params.set("A", "" + news.getNumber());
                params.check();
                fail("Node " + news + " should not have been valid for " + DataTypes.getDataType("typedef"));
            } catch (IllegalArgumentException  iae) {
                //ok
            }

        }
    }

    @Test
    public void getValues() {
        SetFunction function = new SetFunction("test", new Parameter[] {
                new Parameter<Integer>("A", Integer.class, 5),
                new Parameter<Integer>("B", Integer.class, null),
                new Parameter<String>("C", String.class, "vijf"),
                new Parameter<String>("D", String.class)
            }, null, SetFunctionTest.class, "testFunction", SetFunction.Type.CLASS);
        Parameters params = function.createParameters();
        assertEquals(5, function.getValues(params)[0]);
        assertNull(function.getValues(params)[1]);
        assertEquals("vijf", function.getValues(params)[2]);
        assertNull(function.getValues(params)[3]);
    }


}
