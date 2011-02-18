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
public class BasicFieldValueConstraintTest {

    private final static String BUILDER_NAME = "news";
    private final static String FIELD_NAME1 = "title";
    private final static String FIELD_NAME2 = "number";
    private final static String STRING_VALUE1 = "value1";
    private final static String STRING_VALUE2 = "aValue2";
    private final static Double DOUBLE_VALUE1 = (double) 12345;
    private final static Double DOUBLE_VALUE2 = (double) 34589;

    /** Test instance 1 (string field). */
    private BasicFieldValueConstraint instance1 = null;

    /** Test instance 2 (integer field). */
    private BasicFieldValueConstraint instance2 = null;

    /** Field instance 1 (string field). */
    private BasicStepField field1 = null;


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
        instance1 = new BasicFieldValueConstraint(field1, STRING_VALUE1);

        // Create instance 2 (integer field).
        field = builder.getField(FIELD_NAME2);
        BasicStepField field2 = new BasicStepField(step, field);
        instance2 = new BasicFieldValueConstraint(field2, DOUBLE_VALUE1);
    }

    /** Test of setValue method, of class org.mmbase.storage.search.implementation.BasicFieldValueConstraint. */
    @Test
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
        instance2.setValue(DOUBLE_VALUE2.intValue());
        assertTrue(
            ((Number) instance2.getValue()).doubleValue()
                == DOUBLE_VALUE2.doubleValue());

        // Insert as Double.
        BasicFieldValueConstraint result = instance2.setValue(DOUBLE_VALUE2);
        assertTrue(instance2.getValue().equals(DOUBLE_VALUE2));
        assertTrue(result == instance2);
    }

    /** Test of getValue method, of class org.mmbase.storage.search.implementation.BasicFieldValueConstraint. */
    //@Test
    public void testGetValue() {
        // Same as:
        testSetValue();
    }

    /** Test of getBasicSupportLevel method. */
    @Test
    public void testGetBasicSupportLevel() {
        // Returns SUPPORT_OPTIMAL.
        assertTrue(instance1.getBasicSupportLevel() == SearchQueryHandler.SUPPORT_OPTIMAL);
    }

    /** Test of equals method, of class org.mmbase.storage.search.implementation.BasicFieldValueConstraint. */
    //@Test
    public void testEquals() {
        // TODO: implement test
    }

    /** Test of hashCode method, of class org.mmbase.storage.search.implementation.BasicFieldValueConstraint. */
    //@Test
    public void testHashCode() {
        // TODO: implement test
    }

}
