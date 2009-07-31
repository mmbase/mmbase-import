package org.mmbase.storage.search.implementation;

import junit.framework.*;
import org.mmbase.module.core.*;
import org.mmbase.storage.search.*;

/**
 * JUnit tests.
 *
 * @author Rob van Maris
 * @version $Revision: 1.2 $
 */
public class BasicConstraintTest extends TestCase {
    
    /** Test instance. */
    private BasicConstraint instance = null;
    
    public BasicConstraintTest(java.lang.String testName) {
        super(testName);
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    /**
     * Sets up before each test.
     */
    public void setUp() throws Exception {
        MMBaseContext.init();
        MMBase.getMMBase();
        instance = new BasicConstraint();
    }
    
    /**
     * Tears down after each test.
     */
    public void tearDown() throws Exception {}
    
    /** Test of setInverse method, of class org.mmbase.storage.search.implementation.BasicConstraint. */
    public void testSetInverse() {
        // Default is false.
        assertTrue(!instance.isInverse());
        
        BasicConstraint result = instance.setInverse(true);
        assertTrue(instance.isInverse());
        assertTrue(result == instance);
    }
    
    /** Test of isInverse method, of class org.mmbase.storage.search.implementation.BasicConstraint. */
    public void testIsInverse() {
        // Same as:
        testSetInverse();
    }
    
    /** Test of equals method, of class org.mmbase.storage.search.implementation.BasicConstraint. */
    public void testEquals() {
        // TODO: implement test
    }
    
    /** Test of hashCode method, of class org.mmbase.storage.search.implementation.BasicConstraint. */
    public void testHashCode() {
        // TODO: implement test
    }
    
    /** Test of getBasicSupportLevel method. */
    public void testGetBasicSupportLevel() {
        // Returns SUPPORT_OPTIMAL.
        assertTrue(instance.getBasicSupportLevel() == SearchQueryHandler.SUPPORT_OPTIMAL);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(BasicConstraintTest.class);
        
        return suite;
    }
    
}
