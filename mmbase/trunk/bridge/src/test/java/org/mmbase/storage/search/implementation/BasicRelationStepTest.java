package org.mmbase.storage.search.implementation;

import org.junit.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.mock.*;
import org.mmbase.storage.search.*;

import static org.junit.Assert.*;

/**
 * JUnit tests.
 *
 * @author Rob van Maris
 * @version $Id$
 */
public class BasicRelationStepTest  {

    private final static String TEST_BUILDER1 = "mags";
    private final static String TEST_BUILDER2 = "news";
    private final static String INSREL = "posrel";
    private final static Integer TEST_ROLE = new Integer(123456);

    /** Test instance. */
    private BasicRelationStep instance = null;

    /** Previous step of test instance. */
    private Step previous = null;

    /** Next step of test instance. */
    private Step next = null;


    /**
     * Sets up before each test.
     */
    @Before
    public void setUp() throws Exception {
        previous = new BasicStep(TEST_BUILDER1);
        next = new BasicStep(TEST_BUILDER2);
        instance = new BasicRelationStep(INSREL, previous, next);
     }


    /** Test of setCheckedDirectionality method, of class org.mmbase.storage.search.implementation.BasicRelationStep. */
    @Test
    public void testSetCheckedDirectionality() {
        // Default is false.
        assertTrue(!instance.getCheckedDirectionality());

        instance.setCheckedDirectionality(true);
        assertTrue(instance.getCheckedDirectionality());

        BasicRelationStep result = instance.setCheckedDirectionality(false);
        assertTrue(!instance.getCheckedDirectionality());
        assertTrue(result == instance);
    }

    /** Test of setDirectionality method, of class org.mmbase.storage.search.implementation.BasicRelationStep. */
    @Test
    public void testSetDirectionality() {
       // Default is RelationStep.DIRECTIONS_BOTH.
       assertTrue(instance.getDirectionality() == RelationStep.DIRECTIONS_BOTH);

        // Invalid value, should throw IllegalArgumentException.
       try {
            instance.setDirectionality(-1);
            fail("Invalid value, should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {}

       instance.setDirectionality(RelationStep.DIRECTIONS_SOURCE);
       assertTrue(instance.getDirectionality() == RelationStep.DIRECTIONS_SOURCE);

       BasicRelationStep result
            = instance.setDirectionality(RelationStep.DIRECTIONS_DESTINATION);
       assertTrue(instance.getDirectionality() == RelationStep.DIRECTIONS_DESTINATION);
       assertTrue(result == instance);
    }

    /** Test of getCheckedDirectionality method, of class org.mmbase.storage.search.implementation.BasicRelationStep. */
    //@Test
    public void testGetCheckedDirectionality() {
        // Same as:
        testSetCheckedDirectionality();
    }

    /** Test of getDirectionality method, of class org.mmbase.storage.search.implementation.BasicRelationStep. */
    //@Test
    public void testGetDirectionality() {
        // Same as:
        testSetDirectionality();
    }

    /** Test of setRole method, of class org.mmbase.storage.search.implementation.BasicRelationStep. */
    @Test
    public void testSetRole() {
        // Defaults to null.
        assertTrue(instance.getRole() == null);

        instance.setRole(TEST_ROLE);
        assertTrue(instance.getRole().equals(TEST_ROLE));

        BasicRelationStep result = instance.setRole(null);
        assertTrue(instance.getRole() == null);
        assertTrue(result == instance);

    }

    /** Test of getRole method, of class org.mmbase.storage.search.implementation.BasicRelationStep. */
    //@Test
    public void testGetRole() {
        // Same as:
        testSetRole();
    }

    /** Test of getPrevious method, of class org.mmbase.storage.search.implementation.BasicRelationStep. */
    @Test
    public void testGetPrevious() {
        Step step1 = instance.getPrevious();
        assertTrue(step1 != null);
        assertTrue(step1.equals(previous));
    }

    /** Test of getNext method, of class org.mmbase.storage.search.implementation.BasicRelationStep. */
    @Test
    public void testGetNext() {
        Step step2 = instance.getNext();
        assertTrue(next != null);
        assertTrue(step2.equals(next));
    }

    /** Test of equals method, of class org.mmbase.storage.search.implementation.BasicRelationStep. */
    //@Test
    public void testEquals() {
        // TODO: implement test
    }

    /** Test of hashCode method, of class org.mmbase.storage.search.implementation.BasicRelationStep. */
    //@Test
    public void testHashCode() {
        // TODO: implement test
    }



}
