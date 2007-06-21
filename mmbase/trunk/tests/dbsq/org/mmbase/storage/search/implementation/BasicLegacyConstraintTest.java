package org.mmbase.storage.search.implementation;

import junit.framework.*;

/**
 * JUnit tests.
 *
 * @author Rob van Maris
 * @version $Revision: 1.4 $
 */
public class BasicLegacyConstraintTest extends TestCase {
    
    private final static String[] TEST_CONSTRAINTS = new String[] {
        "a=1 and b=2", "kjdfd", "c between d and e"
    };
    
    private BasicLegacyConstraint instance = null;
    
    public BasicLegacyConstraintTest(java.lang.String testName) {
        super(testName);
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    /**
     * Sets up before each test.
     */
    public void setUp() throws Exception {
        instance = new BasicLegacyConstraint("a=1 and b=2");
    }
    
    /**
     * Tears down after each test.
     */
    public void tearDown() throws Exception {}
    
    /** Test of setConstraint method, of class org.mmbase.storage.search.implementation.BasicLegacyConstraint. */
    public void testSetConstraint() {
        try {
            // Null constraint, should throw InvalidArgumentException.
            new BasicLegacyConstraint(null);
            fail("Null constraint, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
        
        try {
            // Empty constraint, should throw InvalidArgumentException.
            new BasicLegacyConstraint("");
            fail("Null constraint, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
        
        BasicLegacyConstraint instance = new BasicLegacyConstraint("xxx");
        for (String constraint : TEST_CONSTRAINTS) {
            BasicLegacyConstraint result = instance.setConstraint(constraint);
            assertTrue(instance.getConstraint().equals(constraint));
            assertTrue(result == instance);
        }
    }
    
    /** Test of getConstraint method, of class org.mmbase.storage.search.implementation.BasicLegacyConstraint. */
    public void testGetConstraint() {
        // Same as:
        testSetConstraint();
    }
    
    /** Test of toString method, of class org.mmbase.storage.search.implementation.BasicLegacyConstraint. */
    public void testToString() {
        assertTrue(instance.toString(),
        instance.toString().equals("LegacyConstraint(inverse:"
        + instance.isInverse() + ", constraint:"
        + instance.getConstraint() + ")"));
        
        instance.setInverse(true);
        assertTrue(instance.toString(),
        instance.toString().equals("LegacyConstraint(inverse:"
        + instance.isInverse() + ", constraint:"
        + instance.getConstraint() + ")"));
    }
    
    /** Test of equals method, of class org.mmbase.storage.search.implementation.BasicLegacyConstraint. */
    public void testEquals() {
        // TODO: implement test
    }
    
    /** Test of hashCode method, of class org.mmbase.storage.search.implementation.BasicLegacyConstraint. */
    public void testHashCode() {
        // TODO: implement test
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(BasicLegacyConstraintTest.class);
        
        return suite;
    }
    
}
