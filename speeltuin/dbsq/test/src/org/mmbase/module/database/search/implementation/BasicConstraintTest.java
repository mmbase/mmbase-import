package org.mmbase.module.database.search.implementation;

import junit.framework.*;
import org.mmbase.module.core.*;
import org.mmbase.module.database.search.*;

/**
 * JUnit tests.
 *
 * @author Rob van Maris
 * @version $Revision: 1.1 $
 */
public class BasicConstraintTest extends TestCase {
    
    /** Test instance. */
    private BasicConstraint instance = null;
    
    /** MMBase instance. */
    private MMBase mmbase;
    
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
        mmbase = MMBase.getMMBase();
        instance = new BasicConstraint();
    }
    
    /**
     * Tears down after each test.
     */
    public void tearDown() throws Exception {}
    
    /** Test of setInverse method, of class org.mmbase.module.database.search.implementation.BasicConstraint. */
    public void testSetInverse() {
        // Default is false.
        assert(!instance.isInverse());
        
        instance.setInverse(true);
        assert(instance.isInverse());
    }
    
    /** Test of isInverse method, of class org.mmbase.module.database.search.implementation.BasicConstraint. */
    public void testIsInverse() {
        // Same as:
        testSetInverse();
    }
    
    /** Test of equals method, of class org.mmbase.module.database.search.implementation.BasicConstraint. */
    public void testEquals() {
        // TODO: implement test
    }
    
    /** Test of hashCode method, of class org.mmbase.module.database.search.implementation.BasicConstraint. */
    public void testHashCode() {
        // TODO: implement test
    }
    
    /** Test of getBasicSupportLevel method. */
    public void testGetBasicSupportLevel() {
        // Returns SUPPORT_OPTIMAL.
        assert(instance.getBasicSupportLevel() == SearchQueryHandler.SUPPORT_OPTIMAL);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(BasicConstraintTest.class);
        
        return suite;
    }
    
}
