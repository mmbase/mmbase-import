/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.functions;
import org.mmbase.datatypes.*;
import java.util.*;
import java.util.regex.*;
import org.junit.*;
import static org.junit.Assert.*;
import org.mmbase.util.logging.Logging;

/**
 *
 * @author Michiel Meeuwissen
 * @verion $Id: ParameterTest.java 39211 2009-10-19 12:11:28Z michiel $
 */
public class SetFunctionTest {

    static {
        DataTypes.initialize();
    }

    public static String testFunction() {
        return "aa";
    }

    public static String testFunction(int a) {
        return "aa" + a;
    }


    @Test
    public void noParameters() {
        SetFunction function = new SetFunction("test", new Parameter[] {}, null, SetFunctionTest.class, "testFunction", SetFunction.Type.CLASS);
        Parameters params = function.createParameters();
        assertEquals("aa", function.getFunctionValue(params));
    }

    @Test
    public void parameters() {
        SetFunction function = new SetFunction("test", new Parameter[] {new Parameter<Integer>("A", int.class, 5)}, null, SetFunctionTest.class, "testFunction", SetFunction.Type.CLASS);
        Parameters params = function.createParameters();
        assertEquals("aa5", function.getFunctionValue(params));
        params.set("A", 6);
        assertEquals("aa6", function.getFunctionValue(params));
    }


}
