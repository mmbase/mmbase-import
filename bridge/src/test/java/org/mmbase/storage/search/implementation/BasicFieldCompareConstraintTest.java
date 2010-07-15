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
public class BasicFieldCompareConstraintTest  {

    private final static String BUILDER_NAME = "news";
    private final static String FIELD_NAME1 = "title";
    private final static String FIELD_NAME2 = "number";

    /** Test instance 1 (string field). */
    private BasicFieldCompareConstraint instance1 = null;

    /** Test instance 2 (integer field). */
    private BasicFieldCompareConstraint instance2 = null;


    /** Field instance 1 (string field). */
    private StepField field1 = null;

    /** Field instance 2 (integer field). */
    private StepField field2 = null;


    @BeforeClass
    public static void setUpClass() throws Exception {
        MockCloudContext.getInstance().addCore();
        MockCloudContext.getInstance().addCoreModel();
        MockCloudContext.getInstance().addNodeManagers(MockBuilderReader.getBuilderLoader().getChildResourceLoader("mynews"));
    }


    /**
     * Sets up before each test.
     */
    @Before
    public void setUp() throws Exception {
        NodeManager builder = MockCloudContext.getInstance().getCloud("mmbase").getNodeManager(BUILDER_NAME);
        Step step = new BasicStep(builder.getName());
        // Create instance 1 (string field).
        Field field = builder.getField(FIELD_NAME1);
        field1 = new BasicStepField(step, field);
        instance1 = new BasicFieldCompareConstraint(field1);

        // Create instance 2 (integer field).
        field = builder.getField(FIELD_NAME2);
        field2 = new BasicStepField(step, field);
        instance2 = new BasicFieldCompareConstraint(field2);
    }

    /** Test of setOperator method, of class org.mmbase.storage.search.implementation.BasicFieldCompareConstraint. */
    @Test
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
    //@Test
    public void testGetOperator() {
        // Same as:
        testSetOperator();
    }

    /** Test of getBasicSupportLevel method. */
    @Test
    public void testGetBasicSupportLevel() {
        // Returns SUPPORT_OPTIMAL.
        assertTrue(instance1.getBasicSupportLevel() == SearchQueryHandler.SUPPORT_OPTIMAL);
    }

    /** Test of equals method, of class org.mmbase.storage.search.implementation.BasicFieldCompareConstraint. */
    //@Test
    public void testEquals() {
        // TODO: implement test
    }

    /** Test of hashCode method, of class org.mmbase.storage.search.implementation.BasicFieldCompareConstraint. */
    //@Test
    public void testHashCode() {
        // TODO: implement test
    }
}
