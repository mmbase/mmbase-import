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
public class ParametersTest {

    static {
        DataTypes.initialize();
    }

    private static final Parameter<String> A = new Parameter<String>("a", String.class, "A");
    private static final Parameter<Integer> B = new Parameter<Integer>("b", Integer.class, true);
    private static final Parameter<String> C = new Parameter<String>("c", String.class, "C");
    private static final Parameter<String> D = new Parameter<String>("d", String.class, "D");
    private static final Parameter<String> E = new Parameter<String>("e", String.class, "E");
    private static final Parameter<String> COLORS = new Parameter<String>("color", DataTypes.getDataType("colors"));

    private static final Parameter G = new Parameter("g", DataTypes.getDataType("currency_enforcescale"));

    private static final Parameter<String>  PA = new PatternParameter<String>(Pattern.compile("pa+"), String.class);
    private static final Parameter<Integer> PB = new PatternParameter<Integer>(Pattern.compile("b+"), Integer.class);
    private static final Parameter<Integer> PC = new PatternParameter<Integer>(Pattern.compile("c+"), Integer.class);
    private static final Parameter<String> PD = new PatternParameter<String>(Pattern.compile("d+"), DataTypes.getDataType("colors"));

    @Test
    public void simple() {
        Parameters params = new Parameters(A, B);
        assertEquals(2, params.size());
        assertEquals(2, params.getDefinition().length);
        assertEquals(2, params.patternLimit);

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
    @Test
    public void illegalPatterns() {
        try {
            Parameters params = new Parameters(PB, A);
            fail("parameterpattern should be given last. Should have give exception about that");
        } catch (IllegalArgumentException iae) {
        }
    }
    @Test
    public void patterns() {
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

    @Test
    public void wrapper() {
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

    @Test
    public void wrapperPatterns() {
        Parameter[] params1 = new Parameter[] {A, PB};
        Parameter[] params2 = new Parameter[]{ B, PC};
        Parameters params = new Parameters(new Parameter.Wrapper(params1), new Parameter.Wrapper(params2));
        // should have been no exceptions until now;

        params.set(B, 5);




    }
    @Test
    public void subList() {
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

    @Test
    public void testAutodefiningParameters() {
        Parameters auto = new AutodefiningParameters();
        auto.setIfDefined("a", "A");
        auto.set("b", "B");

        Parameters sub = auto.subList(0, 1);

    }



    @Test
    public void autoCast() {

        Parameters params = new Parameters(A, B, G, COLORS);
        params.set(B, 5); // OK
        params.set(G, new java.math.BigDecimal(5));
        try {
            params.set("b", "5");
            fail("Should have given IllegalArgumentException");
        } catch (IllegalArgumentException ia) {
            // ok this is expected
        }
        try {
            params.set("g", "5");
            fail("Should have given IllegalArgumentException");
        } catch (IllegalArgumentException ia) {
            // ok this is expected
        }
        params.setAutoCasting(true);
        params.set("b", "5");
        params.set("g", "5");

        try {
            params.set("b", "a");
            fail("Should have given IllegalArgumentException since 'a' is not a valid integer " + params);
        } catch (IllegalArgumentException ia) {
            // ok this is expected

        }

        try {
            params.set("g", "a");
            fail("Should have given IllegalArgumentException since 'a' is not a valid decimal");
        } catch (IllegalArgumentException ia) {
            // ok this is expected

        }

        params.set("g", new String[] {"6"});
        assertEquals("6", params.get(G).toString());

        try {
            params.set("g", new String[] {"list"});
            fail("Should have given IllegalArgumentException since 'list' is not a valid decimal");
        } catch (IllegalArgumentException ia) {
            // ok this is expected

        }

        params.set("g", new String[] {"6.12345"});
        assertEquals("6.12345", params.get(G).toString());

        params.set("g", new String[] {"123.1"});
        assertEquals("123.1", params.get(G).toString());

        try {
            params.set("g", "7.123456789");
            params.check();
            fail("Should have given IllegalArgumentException since '7.123456789' has too many digits");
        } catch (IllegalArgumentException ia) {
            // ok this is expected
            //System.out.println(ia);

        }
        params.set("g", new String[] {"1.1"});

        params.set("a", new String[0]);
        params.check();
        params.set("a", new String[] {"foo"});
        params.check();
        params.set("a", new Object[] {"foo"});
        params.check();


        params.set("color", new String[] {"green"});
        params.check();
        params.set("color", new Object[] {"green"});
        params.check();

        params.set("color", new Object[] {"not a color"});
        assertTrue(params.validate().size() > 0);

    }

    @Test
    public void autoCastPatterns() {
        Parameters params = new Parameters(PA, PD);
        params.setAutoCasting(true);

        params.set("pa", new String[0]);
        params.check();
        params.set("pa", new String[] {"foo"});
        params.check();
        params.set("pa", new Object[] {"foo"});
        params.check();


        params.set("dd", new String[] {"green"});
        params.check();
        params.set("dd", new Object[] {"green"});
        params.check();

        params.set("dd", new Object[] {"not a color"});
        try {
            params.check();
            fail("Should have been illegal");
        } catch (IllegalArgumentException ia) {
            //
        }

    }


    @Test
    public void patternParameters() {
        Parameters params = new Parameters(PB, PD);
        params.set("dd", "a");
        assertTrue(params.validate().size() > 0);

        try {
            params.set("bb", "a");
            fail("Should have given IllegalArgumentException since 'a' cannot be casted to integer");
        } catch (IllegalArgumentException ia) {
            // ok this is expected
            //System.out.println(ia);

        }
    }
    @Test
    public void patternParametersAutoCast() {
        Parameters params = new Parameters(PB, PD);
        params.setAutoCasting(true);

        params.set("dd", "a");
        assertTrue(params.validate().size() > 0);

        try {
            params.set("bb", "a");
            fail("Should have given IllegalArgumentException since 'a' cannot be casted to integer");
        } catch (IllegalArgumentException ia) {
            // ok this is expected
            //System.out.println(ia);

        }
    }

    @Test
    public void patternLimit1() {
        Parameters params = new Parameters(A);
        assertEquals(1, params.patternLimit);
        assertEquals(1, params.definition.length);
    }

    @Test
    public void patternLimit2() {
        Parameters params = new Parameters(A, PD);
        assertEquals(1, params.patternLimit);
        assertEquals(2, params.definition.length);
    }

    @Test
    public void patternLimit3() {
        Parameters params = new Parameters(A, PB, PC, PD);
        assertEquals(1, params.patternLimit);
        assertEquals(4, params.definition.length);
    }

    @Test
    public void definition() {
        Parameters params = new Parameters(A, PD);
        params.setAutoCasting(true);
        params.set("dd", "red");
        params.set("ddd", "blue");
        params.set("dddd", "green");
        params.set("ddddd", "yellow");
        params.set("ddddd", "blue");
        assertEquals(5, params.size());

        Parameters sublist = params.subList(0, 3);
        assertEquals("red", sublist.get(1));
        assertEquals(3, sublist.size());

        assertEquals(2, params.getDefinition().length);
        assertEquals(2, sublist.getDefinition().length);
        assertEquals("a", sublist.getDefinition()[0].getName());
        assertEquals("d+", sublist.getDefinition()[1].getName());

        Parameters sublist2 = params.subList(1, 3);
        assertEquals(2, sublist2.size());
        assertEquals("red", sublist2.get(0));
        assertEquals(1, sublist2.getDefinition().length);
        assertEquals("d+", sublist2.getDefinition()[0].getName());

    }



}
