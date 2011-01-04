/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.functions;
import org.mmbase.datatypes.*;
import static org.junit.Assert.*;
import org.junit.*;

/**
 *
 * @author Michiel Meeuwissen
 * @verion $Id$
 */
public class AutodefiningParametersTest {

    @BeforeClass
    public static void setup() throws Exception {
        DataTypes.initialize();
    }

    @Test
    public void basic() {
        Parameters params = new AutodefiningParameters();
        params.set("a", "A");
        params.set("b", new Integer(2));

        assertEquals("A", params.get("a"));
        assertEquals(new Integer(2), params.get("b"));
    }

    @Test
    public void index() {
        Parameters params = new AutodefiningParameters();
        params.set(1, "A");
        params.set(2, new Integer(2));
        assertEquals(3, params.size());

        params.set("b", "B");
        assertEquals(3, params.size());

        assertEquals("B", params.get(0));
        assertEquals("A", params.get(1));
        assertEquals(new Integer(2), params.get(2));
    }



}
