package org.mmbase.storage.search.implementation;

import org.junit.*;
import org.mmbase.storage.search.*;

import static org.junit.Assert.*;
/**
 * JUnit tests.
 *
 * @author Rob van Maris
 * @version $Revision: 1.2 $
 */
public class BasicConstraintTest  {

    /** Test instance. */
    private BasicConstraint instance = null;

    /**
     * Sets up before each test.
     */
    @Before
    public void setUp() throws Exception {
        instance = new BasicConstraint();
    }


    /** Test of setInverse method, of class org.mmbase.storage.search.implementation.BasicConstraint. */
    @Test
    public void testSetInverse() {
        // Default is false.
        assertTrue(!instance.isInverse());

        BasicConstraint result = instance.setInverse(true);
        assertTrue(instance.isInverse());
        assertTrue(result == instance);
    }

    /** Test of isInverse method, of class org.mmbase.storage.search.implementation.BasicConstraint. */
    //@Test
    public void testIsInverse() {
        // Same as:
        testSetInverse();
    }

    /** Test of equals method, of class org.mmbase.storage.search.implementation.BasicConstraint. */
    //@Test
    public void testEquals() {
        // TODO: implement test
    }

    /** Test of hashCode method, of class org.mmbase.storage.search.implementation.BasicConstraint. */
    //    @Test
    public void testHashCode() {
        // TODO: implement test
    }

    /** Test of getBasicSupportLevel method. */
    @Test
    public void testGetBasicSupportLevel() {
        // Returns SUPPORT_OPTIMAL.
        assertTrue(instance.getBasicSupportLevel() == SearchQueryHandler.SUPPORT_OPTIMAL);
    }
}
