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
public class BasicCompareFieldsConstraintTest  {

    private final static String BUILDER_NAME = "mags";
    private final static String STRING_FIELD_NAME = "owner";
    private final static String INTEGER_FIELD_NAME = "number";
    private final static String BUILDER_NAME2 = "news";
    private final static String STRING_FIELD_NAME2 = "owner";
    private final static String INTEGER_FIELD_NAME2 = "number";

    /** Test instance. */
    private BasicCompareFieldsConstraint instance = null;

    /** String type Field instance. */
    private BasicStepField stringField = null;

    /** Integer type Field instance. */
    private StepField integerField = null;

    /** CoreField example (string type). */
    private Field stringCoreField = null;

    /** CoreField example (integer type). */
    private Field integerCoreField = null;

    /** Second string type Field instance. */
    private BasicStepField stringField2 = null;

    /** Second string CoreField example. */
    private Field stringCoreField2 = null;

    /** Second  integer CoreField example. */
    private Field integerCoreField2 = null;


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
        Step step = new BasicStep(BUILDER_NAME);
        stringCoreField = builder.getField(STRING_FIELD_NAME);
        stringField = new BasicStepField(step, stringCoreField);
        integerCoreField = builder.getField(INTEGER_FIELD_NAME);
        integerField = new BasicStepField(step, integerCoreField);
        Step step2 = new BasicStep(BUILDER_NAME2);
        NodeManager builder2 = MockCloudContext.getInstance().getCloud("mmbase").getNodeManager(BUILDER_NAME2);
        stringCoreField2 = builder2.getField(STRING_FIELD_NAME2);
        stringField2 = new BasicStepField(step2, stringCoreField2);
        integerCoreField2 = builder2.getField(INTEGER_FIELD_NAME2);
        integerField = new BasicStepField(step2, integerCoreField2);
        instance = new BasicCompareFieldsConstraint(stringField, stringField2);
    }


    /** Tests constructor. */
    @Test
    public void testConstructor() {
        try {
            // Null field2, should throw IllegalArgumentException.
            new BasicCompareFieldsConstraint(stringField, null);
            fail("Null field2, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}

        try {
            // Different field types, should throw IllegalArgumentException.
            new BasicCompareFieldsConstraint(stringField, integerField);
            fail("Different field types, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
        try {
            // Different field types, should throw IllegalArgumentException.
            new BasicCompareFieldsConstraint(integerField, stringField2);
            fail("Different field types, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}

    }

    /** Test of getField2 method, of class org.mmbase.storage.search.implementation.BasicCompareFieldsConstraint. */
    @Test
    public void testGetField2() {
        assertTrue(instance.getField2() == stringField2);
    }

    /** Test of getBasicSupportLevel method. */
    @Test
    public void testGetBasicSupportLevel() {
        // Returns SUPPORT_OPTIMAL.
        assertTrue(instance.getBasicSupportLevel() == SearchQueryHandler.SUPPORT_OPTIMAL);
    }

    /** Test of equals method, of class org.mmbase.storage.search.implementation.BasicCompareFieldsConstraint. */
    //@Test
    public void testEquals() {
        // TODO: implement test
    }

    /** Test of hashCode method, of class org.mmbase.storage.search.implementation.BasicCompareFieldsConstraint. */
    //@Test
    public void testHashCode() {
        // TODO: implement test
    }


}
