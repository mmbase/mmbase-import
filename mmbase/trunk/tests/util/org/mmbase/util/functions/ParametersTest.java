/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.functions;
import java.util.*;
import java.util.regex.*;
import junit.framework.TestCase;

/**
 *
 * @author Michiel Meeuwissen
 * @verion $Id: ParametersTest.java,v 1.1 2008-08-20 08:00:01 michiel Exp $
 */
public class ParametersTest extends TestCase {

    private static final Parameter<String> A = new Parameter<String>("a", String.class, "A");
    private static final Parameter<Integer> B = new Parameter<Integer>("b", Integer.class, true);
    private static final Parameter<Integer> PB = new PatternParameter<Integer>(Pattern.compile("b+"), Integer.class);
    /**
     */
    public void testSimple() {
        Parameters params = new Parameters(A, B);
        assertEquals(2, params.size());
        assertEquals(2, params.getDefinition().length);
        assertEquals(3, params.patternLimit);

        assertEquals("A", params.get(0));
        assertEquals("A", params.get("a"));
        assertEquals("A", params.get(A));
        params.set(0, "AA");
        assertEquals("AA", params.get(0));
        assertEquals("AA", params.get("a"));
        assertEquals("AA", params.get(A));
        params.set("a", "AAA");
        assertEquals("AAA", params.get(0));
        assertEquals("AAA", params.get("a"));
        assertEquals("AAA", params.get(A));
        params.set(A, "AAAA");
        assertEquals("AAAA", params.get(0));
        assertEquals("AAAA", params.get("a"));
        assertEquals("AAAA", params.get(A));
        try {
            params.checkRequiredParameters();
            fail("Required parameter 'b' was not filled, but no exception was thrown by that");
        } catch (IllegalArgumentException iae) {
        }
        params.set("b", 5);
        params.checkRequiredParameters();

        assertEquals(5, params.get(1));
        assertEquals(5, params.get("b"));
        assertTrue(5 == params.get(B));

    }
    public void testIllegalPatterns() {
        try {
            Parameters params = new Parameters(PB, A);
            fail("parameterpattern should be given last. Should have give exception about that");
        } catch (IllegalArgumentException iae) {
        }
    }
    public void testPatterns() {
        Parameters params = new Parameters(A, PB);
        assertEquals(1, params.size());
        assertEquals(2, params.getDefinition().length);
        assertEquals(1, params.patternLimit);

        assertEquals("A", params.get(0));
        assertEquals("A", params.get("a"));
        assertEquals("A", params.get(A));


        params.checkRequiredParameters();
        params.set("b", 5);
        assertEquals(2, params.size());
        params.set("bb", 6);
        assertEquals(3, params.size());

        assertEquals(5, params.get(1));
        assertEquals(6, params.get(2));
    }
}
