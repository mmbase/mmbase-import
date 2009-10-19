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
 * @verion $Id$
 */
public class ParameterTest {

    static {
        DataTypes.initialize();
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

}
