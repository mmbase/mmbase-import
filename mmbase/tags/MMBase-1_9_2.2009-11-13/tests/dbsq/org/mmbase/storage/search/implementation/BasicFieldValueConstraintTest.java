package org.mmbase.storage.search.implementation;

import junit.framework.*;

import org.mmbase.module.core.*;
import org.mmbase.storage.search.*;

/**
 * JUnit tests.
 *
 * @author Rob van Maris
 * @version $Revision: 1.4 $
 */
public class BasicFieldValueConstraintTest extends TestCase {

    private final static String BUILDER_NAME = "images";
    private final static String FIELD_NAME1 = "title";
    private final static String FIELD_NAME2 = "number";
    private final static String STRING_VALUE1 = "value1";
    private final static String STRING_VALUE2 = "aValue2";
    private final static Double DOUBLE_VALUE1 = new Double(12345);
    private final static Double DOUBLE_VALUE2 = new Double(34589);

    /** Test instance 1 (string field). */
    private BasicFieldValueConstraint instance1 = null;

    /** Test instance 2 (integer field). */
    private BasicFieldValueConstraint instance2 = null;

    /** MMBase instance. */
    private MMBase mmbase = null;

    /** Field instance 1 (string field). */
    private BasicStepField field1 = null;

    /** Builder example. */
    private MMObjectBuilder builder = null;

    public BasicFieldValueConstraintTest(java.lang.String testName) {
        super(testName);
    }

    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
        System.exit(0);
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
        org.mmbase.core.CoreField field = builder.getField(FIELD_NAME1);
        field1 = new BasicStepField(step, field);
        instance1 = new BasicFieldValueConstraint(field1, STRING_VALUE1);

        // Create instance 2 (integer field).
        field = builder.getField(FIELD_NAME2);
        BasicStepField field2 = new BasicStepField(step, field);
        instance2 = new BasicFieldValueConstraint(field2, DOUBLE_VALUE1);
    }

    /**
     * Tears down after each test.
     */
    public void tearDown() throws Exception {}

    /** Test of setValue method, of class org.mmbase.storage.search.implementation.BasicFieldValueConstraint. */
    public void testSetValue() {
        assertTrue(instance1.getValue().equals(STRING_VALUE1));
        assertTrue(instance2.getValue().equals(DOUBLE_VALUE1));

        try {
            // Null value, should throw IllegalArgumentException.
            instance1.setValue(null);
            fail("Null value, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
        try {
            // Null value, should throw IllegalArgumentException.
            instance2.setValue(null);
            fail("Null value, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}

        try {
            // Double value for string type, should throw IllegalArgumentException.
            instance1.setValue(DOUBLE_VALUE1);
            fail("Double value for string type, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
        try {
            // String value for integer type, should throw IllegalArgumentException.
            instance2.setValue(STRING_VALUE1);
            fail("String value for integer type, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}

        assertTrue(!STRING_VALUE1.equals(STRING_VALUE2)); // Different test values!
        instance1.setValue(STRING_VALUE2);
        assertTrue(instance1.getValue().equals(STRING_VALUE2));
        assertTrue(!DOUBLE_VALUE1.equals(DOUBLE_VALUE2)); // Different test values!

        // Insert as Integer value instead of Double.
        instance2.setValue(new Integer(DOUBLE_VALUE2.intValue()));
        assertTrue(
            ((Number) instance2.getValue()).doubleValue()
                == DOUBLE_VALUE2.doubleValue());

        // Insert as Double.
        BasicFieldValueConstraint result = instance2.setValue(DOUBLE_VALUE2);
        assertTrue(instance2.getValue().equals(DOUBLE_VALUE2));
        assertTrue(result == instance2);
    }

    /** Test of getValue method, of class org.mmbase.storage.search.implementation.BasicFieldValueConstraint. */
    public void testGetValue() {
        // Same as:
        testSetValue();
    }

    /** Test of getBasicSupportLevel method. */
    public void testGetBasicSupportLevel() {
        // Returns SUPPORT_OPTIMAL.
        assertTrue(instance1.getBasicSupportLevel() == SearchQueryHandler.SUPPORT_OPTIMAL);
    }

    /** Test of equals method, of class org.mmbase.storage.search.implementation.BasicFieldValueConstraint. */
    public void testEquals() {
        // TODO: implement test
    }

    /** Test of hashCode method, of class org.mmbase.storage.search.implementation.BasicFieldValueConstraint. */
    public void testHashCode() {
        // TODO: implement test
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(BasicFieldValueConstraintTest.class);

        return suite;
    }

}
