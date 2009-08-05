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
 * @verion $Id: ParametersTest.java,v 1.5 2008-08-20 21:56:24 michiel Exp $
 */
public class ParametersTest extends TestCase {

    private static final Parameter<String> A = new Parameter<String>("a", String.class, "A");
    private static final Parameter<Integer> B = new Parameter<Integer>("b", Integer.class, true);
    private static final Parameter<String> C = new Parameter<String>("c", String.class, "C");
    private static final Parameter<String> D = new Parameter<String>("d", String.class, "D");
    private static final Parameter<String> E = new Parameter<String>("e", String.class, "E");
    private static final Parameter<String> F = new Parameter<String>("f", String.class, "F");

    private static final Parameter<Integer> PB = new PatternParameter<Integer>(Pattern.compile("b+"), Integer.class);
    private static final Parameter<Integer> PC = new PatternParameter<Integer>(Pattern.compile("c+"), Integer.class);
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
        assertEquals(2, params.toMap().size());
        assertEquals(2, params.toEntryList().size());
        params.set("b", 6);
        assertEquals(2, params.size());
        assertEquals(2, params.toMap().size());
        assertEquals(2, params.toEntryList().size());
        params.set("bb", 7);
        assertEquals(3, params.size());
        assertEquals(3, params.toMap().size());
        assertEquals(3, params.toEntryList().size());

        assertTrue(1 == params.patternLimit);
        assertTrue(-1 == params.indexOfParameter(PB));
        try {
            params.set(PB, 7);
            fail("You should not be able to set a value by a pattern parameter");
        } catch (IllegalArgumentException iae) {
        }


        assertEquals(6, params.get(1));
        assertEquals(6, params.get("b"));
        assertEquals(7, params.get(2));
        assertEquals(7, params.get("bb"));
    }

    public void testWrapper() {
        Parameters params = new Parameters(A, B, new Parameter.Wrapper(C, D));
        assertEquals(4, params.size());
        params.set(B, 5);

        assertEquals("A", params.get(A));
        assertTrue(5 == params.get(B));
        assertEquals("C", params.get(C));
        assertEquals("D", params.get(D));

        assertEquals("A", params.get("a"));
        assertEquals(5, params.get("b"));
        assertEquals("C", params.get("c"));
        assertEquals("D", params.get("d"));

        assertEquals("A", params.get(0));
        assertEquals(5, params.get(1));
        assertEquals("C", params.get(2));
        assertEquals("D", params.get(3));
    }

    public void testWrapperPatterns() {
        Parameter[] params1 = new Parameter[] {A, PB};
        Parameter[] params2 = new Parameter[]{ B, PC};
        Parameters params = new Parameters(new Parameter.Wrapper(params1), new Parameter.Wrapper(params2));
        // should have been no exceptions until now;

        params.set(B, 5);




    }

    public void testSubList() {
        Parameters params = new Parameters(A, B, new Parameter.Wrapper(C, D));
        params.set(B, 5);
        Parameters subParams = params.subList(1, 3);
        assertEquals(2, subParams.size());

        assertTrue(5 == subParams.get(B));
        assertEquals(5, subParams.get(0));
        assertEquals(5, subParams.get("b"));

        assertEquals("C", subParams.get(C));
        assertEquals("C", subParams.get(1));
        assertEquals("C", subParams.get("c"));
    }
}
