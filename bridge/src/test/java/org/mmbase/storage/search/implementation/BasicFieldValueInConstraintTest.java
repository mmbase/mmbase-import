package org.mmbase.storage.search.implementation;

import org.junit.*;
import java.util.*;

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
public class BasicFieldValueInConstraintTest  {

    private final static String BUILDER_NAME = "news";
    private final static String STRING_FIELD_NAME = "title";
    private final static String INTEGER_FIELD_NAME = "number";

    private final static String STRING_TEST_VALUE1 = "kjjdf kjjkl";
    private final static String STRING_TEST_VALUE2 = " KLkljklj KJKLJ ERwe ";
    private final static Integer INTEGER_TEST_VALUE = new Integer(12345);

    /** Test instance. */
    private BasicFieldValueInConstraint instance = null;

    /** Field instances. */
    private BasicStepField stringField = null;
    private StepField integerField = null;


    /** CoreField examples. */
    private Field stringCoreField = null;
    private Field integerCoreField = null;


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
        stringCoreField = builder.getField(STRING_FIELD_NAME);
        integerCoreField = builder.getField(INTEGER_FIELD_NAME);
        Step step = new BasicStep(builder.getName());
        stringField = new BasicStepField(step, stringCoreField);
        integerField = new BasicStepField(step, integerCoreField);
        instance = new BasicFieldValueInConstraint(stringField);
    }


    /** Test of addValue method, of class org.mmbase.storage.search.implementation.BasicFieldValueInConstraint. */
    @Test
    public void testAddValue() {
        // Null value, should throw IllegalArgumentException
        try {
            instance.addValue(null);
            fail("Null value, should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {}

        // Trying to add integer value to string field, should throw IllegalArgumentException.
        try {
            instance.addValue(INTEGER_TEST_VALUE);
            fail("Trying to add integer value to string field, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}

        List<Object> values = new ArrayList<Object>(instance.getValues());
        assertTrue(values.size() == 0);
        instance.addValue(STRING_TEST_VALUE1);
        values = new ArrayList<Object>(instance.getValues());
        assertTrue(values.size() == 1);
        assertTrue(values.indexOf(STRING_TEST_VALUE1) == 0);
        instance.addValue(STRING_TEST_VALUE2);
        values = new ArrayList<Object>(instance.getValues());
        assertTrue(values.size() == 2);
        // Lexicographically ordering:
        assertTrue(values.indexOf(STRING_TEST_VALUE1) == 1);
        assertTrue(values.indexOf(STRING_TEST_VALUE2) == 0);

        // Trying to add string value to integer field, should throw IllegalArgumentException.
        BasicFieldValueInConstraint instance2 = new BasicFieldValueInConstraint(integerField);
        try {
            instance2.addValue("skdlw");
            fail("Trying to add string value to integer field, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}

        // Add integer value to integer field.
        values = new ArrayList<Object>(instance2.getValues());
        assertTrue(values.size() == 0);
        instance2.addValue(INTEGER_TEST_VALUE);
        values = new ArrayList<Object>(instance2.getValues());
        assertTrue(values.size() == 1);
        assertTrue(values.indexOf(INTEGER_TEST_VALUE) == 0);
    }

    /** Test of getValues method, of class org.mmbase.storage.search.implementation.BasicFieldValueInConstraint. */
    @Test
    public void testGetValues() {
        // See:
        testAddValue();

        Set<Object> values = instance.getValues();

        // List returned must be unmodifiable.
        try {
            values.add("ekowkdk kkj");
            fail("Attempt to modify list, must throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {}
        try {
            values.clear();
            fail("Attempt to modify list, must throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {}
   }

    /** Test of getBasicSupportLevel method. */
    @Test
    public void testGetBasicSupportLevel() {
        // Returns SUPPORT_OPTIMAL.
        assertTrue(instance.getBasicSupportLevel() == 3); // TODO SearchQueryHandler.SUPPORT_OPTIMAL);
    }

    /** Test of equals method, of class org.mmbase.storage.search.implementation.BasicFieldValueInConstraint. */
    //@Test
    public void testEquals() {
        // TODO: implement test
    }

    /** Test of hashCode method, of class org.mmbase.storage.search.implementation.BasicFieldValueInConstraint. */
    //@Test
    public void testHashCode() {
        // TODO: implement test
    }

}
