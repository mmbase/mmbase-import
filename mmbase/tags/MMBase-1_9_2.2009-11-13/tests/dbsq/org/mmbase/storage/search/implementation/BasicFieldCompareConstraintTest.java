package org.mmbase.storage.search.implementation;

import junit.framework.*;

import org.mmbase.core.CoreField;
import org.mmbase.module.core.*;
import org.mmbase.storage.search.*;

/**
 * JUnit tests.
 *
 * @author Rob van Maris
 * @version $Revision: 1.4 $
 */
public class BasicFieldCompareConstraintTest extends TestCase {
    
    private final static String BUILDER_NAME = "images";
    private final static String FIELD_NAME1 = "title";
    private final static String FIELD_NAME2 = "number";

    /** Test instance 1 (string field). */
    private BasicFieldCompareConstraint instance1 = null;
    
    /** Test instance 2 (integer field). */
    private BasicFieldCompareConstraint instance2 = null;
    
    /** MMBase instance. */
    private MMBase mmbase = null;
    
    /** Field instance 1 (string field). */
    private StepField field1 = null;
    
    /** Field instance 2 (integer field). */
    private StepField field2 = null;
    
    /** Builder example. */
    private MMObjectBuilder builder = null;
    
    /** CoreField example. */
    private CoreField CoreField = null;
    
    public BasicFieldCompareConstraintTest(java.lang.String testName) {
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
        Step step = new BasicStep(builder);
        
        // Create instance 1 (string field).
        CoreField = builder.getField(FIELD_NAME1);
        field1 = new BasicStepField(step, CoreField);
        instance1 = new BasicFieldCompareConstraint(field1);
        
        // Create instance 2 (integer field).
        CoreField = builder.getField(FIELD_NAME2);
        field2 = new BasicStepField(step, CoreField);
        instance2 = new BasicFieldCompareConstraint(field2);        
    }
    
    /**
     * Tears down after each test.
     */
    public void tearDown() throws Exception {}
    
    /** Test of setOperator method, of class org.mmbase.storage.search.implementation.BasicFieldCompareConstraint. */
    public void testSetOperator() {
        // Default is EQUAL.
        assertTrue(instance1.getOperator() == FieldCompareConstraint.EQUAL);
        assertTrue(instance2.getOperator() == FieldCompareConstraint.EQUAL);
        
        // Invalid operator value, should throw IllegalArgumentException.
        try {
            instance1.setOperator(-123);
            fail("Invalid operator value, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
        try {
            instance2.setOperator(-123);
            fail("Invalid operator value, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
        try {
            instance1.setOperator(0);
            fail("Invalid operator value, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
        try {
            instance2.setOperator(0);
            fail("Invalid operator value, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
        try {
            instance1.setOperator(100);
            fail("Invalid operator value, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
        try {
            instance2.setOperator(100);
            fail("Invalid operator value, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
        
        instance1.setOperator(FieldCompareConstraint.LESS);
        assertTrue(instance1.getOperator() == FieldCompareConstraint.LESS);
        instance2.setOperator(FieldCompareConstraint.LESS);
        assertTrue(instance2.getOperator() == FieldCompareConstraint.LESS);
        instance1.setOperator(FieldCompareConstraint.LESS_EQUAL);
        assertTrue(instance1.getOperator() == FieldCompareConstraint.LESS_EQUAL);
        instance2.setOperator(FieldCompareConstraint.LESS_EQUAL);
        assertTrue(instance2.getOperator() == FieldCompareConstraint.LESS_EQUAL);
        instance1.setOperator(FieldCompareConstraint.EQUAL);
        assertTrue(instance1.getOperator() == FieldCompareConstraint.EQUAL);
        instance2.setOperator(FieldCompareConstraint.EQUAL);
        assertTrue(instance2.getOperator() == FieldCompareConstraint.EQUAL);
        instance1.setOperator(FieldCompareConstraint.NOT_EQUAL);
        assertTrue(instance1.getOperator() == FieldCompareConstraint.NOT_EQUAL);
        instance2.setOperator(FieldCompareConstraint.NOT_EQUAL);
        assertTrue(instance2.getOperator() == FieldCompareConstraint.NOT_EQUAL);
        instance1.setOperator(FieldCompareConstraint.GREATER);
        assertTrue(instance1.getOperator() == FieldCompareConstraint.GREATER);
        instance2.setOperator(FieldCompareConstraint.GREATER);
        assertTrue(instance2.getOperator() == FieldCompareConstraint.GREATER);
        instance1.setOperator(FieldCompareConstraint.GREATER_EQUAL);
        assertTrue(instance1.getOperator() == FieldCompareConstraint.GREATER_EQUAL);
        instance2.setOperator(FieldCompareConstraint.GREATER_EQUAL);
        assertTrue(instance2.getOperator() == FieldCompareConstraint.GREATER_EQUAL);
        BasicFieldCompareConstraint result 
            = instance1.setOperator(FieldCompareConstraint.LIKE);
        assertTrue(instance1.getOperator() == FieldCompareConstraint.LIKE);
        assertTrue(result == instance1);
        try {
            // Like operator for integer type field, should throw IllegalArgumentException.
            instance2.setOperator(FieldCompareConstraint.LIKE);
            fail("Like operator for integer type field, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
    }
    
    /** Test of getOperator method, of class org.mmbase.storage.search.implementation.BasicFieldCompareConstraint. */
    public void testGetOperator() {
        // Same as:
        testSetOperator();
    }
    
    /** Test of getBasicSupportLevel method. */
    public void testGetBasicSupportLevel() {
        // Returns SUPPORT_OPTIMAL.
        assertTrue(instance1.getBasicSupportLevel() == SearchQueryHandler.SUPPORT_OPTIMAL);
    }
    
    /** Test of equals method, of class org.mmbase.storage.search.implementation.BasicFieldCompareConstraint. */
    public void testEquals() {
        // TODO: implement test
    }
    
    /** Test of hashCode method, of class org.mmbase.storage.search.implementation.BasicFieldCompareConstraint. */
    public void testHashCode() {
        // TODO: implement test
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(BasicFieldCompareConstraintTest.class);
        
        return suite;
    }
    
}
