package org.mmbase.storage.search.implementation;

import junit.framework.*;

import org.mmbase.core.CoreField;
import org.mmbase.module.core.*;
import org.mmbase.storage.search.*;

/**
 * JUnit tests.
 *
 * @author Rob van Maris
 * @version $Revision: 1.2 $
 */
public class BasicFieldConstraintTest extends TestCase {
    
    private final static String BUILDER_NAME = "images";
    private final static String FIELD_NAME = "title";
    
    /** Test instance. */
    private BasicFieldConstraint instance = null;
    
    /** MMBase instance. */
    private MMBase mmbase = null;
    
    /** Field instance. */
    private StepField field = null;
    
    /** Builder example. */
    private MMObjectBuilder builder = null;
    
    /** CoreField example. */
    private CoreField CoreField = null;
    
    public BasicFieldConstraintTest(java.lang.String testName) {
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
        builder = mmbase.getBuilder(BUILDER_NAME);
        CoreField = builder.getField(FIELD_NAME);
        Step step = new BasicStep(builder);
        field = new BasicStepField(step, CoreField);
        instance = new BasicFieldConstraint(field) {}; // Class is abstract.
    }
    
    /**
     * Tears down after each test.
     */
    public void tearDown() throws Exception {}
    
    /** Test of getField method, of class org.mmbase.storage.search.implementation.BasicFieldConstraint. */
    public void testGetField() {
        assertTrue(instance.getField() != null);
        assertTrue(instance.getField() == field);
    }
    
    /** Tests constructor. */
    public void testConstructor() {
        try {
            // Null field, should throw IllegalArgumentException.
            new BasicFieldConstraint(null);
            fail("Null field, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
    }
    
    /** Test of setCaseSensitive method, of class org.mmbase.storage.search.implementation.BasicFieldConstraint. */
    public void testSetCaseSensitive() {
        // Defaults to true.
        assertTrue(instance.isCaseSensitive());
        
        instance.setCaseSensitive(false);
        assertTrue(!instance.isCaseSensitive());
        BasicFieldConstraint result = instance.setCaseSensitive(true);
        assertTrue(instance.isCaseSensitive());
        assertTrue(result == instance);
    }
    
    /** Test of isCaseSensitive method, of class org.mmbase.storage.search.implementation.BasicFieldConstraint. */
    public void testIsCaseSensitive() {
        // Same as:
        testSetCaseSensitive();
    }
    
    /** Test of getBasicSupportLevel method. */
    public void testGetBasicSupportLevel() {
        // Returns SUPPORT_OPTIMAL.
        assertTrue(instance.getBasicSupportLevel() == SearchQueryHandler.SUPPORT_OPTIMAL);
    }
    
    /** Test of equals method, of class org.mmbase.storage.search.implementation.BasicFieldConstraint. */
    public void testEquals() {
        // TODO: implement test
    }
    
    /** Test of hashCode method, of class org.mmbase.storage.search.implementation.BasicFieldConstraint. */
    public void testHashCode() {
        // TODO: implement test
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(BasicFieldConstraintTest.class);
        
        return suite;
    }
    
}
