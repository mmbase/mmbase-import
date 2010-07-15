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
public class BasicFieldConstraintTest {

    private final static String BUILDER_NAME = "news";
    private final static String FIELD_NAME = "title";

    /** Test instance. */
    private BasicFieldConstraint instance = null;

    /** Field instance. */
    private StepField field = null;

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
        Field f = builder.getField(FIELD_NAME);
        Step step = new BasicStep(builder.getName());
        field = new BasicStepField(step, f);
        instance = new BasicFieldConstraint(field) {}; // Class is abstract.
    }

    /** Test of getField method, of class org.mmbase.storage.search.implementation.BasicFieldConstraint. */
    @Test
    public void testGetField() {
        assertTrue(instance.getField() != null);
        assertTrue(instance.getField() == field);
    }

    /** Tests constructor. */
    @Test
    public void testConstructor() {
        try {
            // Null field, should throw IllegalArgumentException.
            new BasicFieldConstraint(null);
            fail("Null field, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
    }

    /** Test of setCaseSensitive method, of class org.mmbase.storage.search.implementation.BasicFieldConstraint. */
    @Test
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
    //@Test
    public void testIsCaseSensitive() {
        // Same as:
        testSetCaseSensitive();
    }

    /** Test of getBasicSupportLevel method. */
    @Test
    public void testGetBasicSupportLevel() {
        // Returns SUPPORT_OPTIMAL.
        assertTrue(instance.getBasicSupportLevel() == SearchQueryHandler.SUPPORT_OPTIMAL);
    }

    /** Test of equals method, of class org.mmbase.storage.search.implementation.BasicFieldConstraint. */
    //@Test
    public void testEquals() {
        // TODO: implement test
    }

    /** Test of hashCode method, of class org.mmbase.storage.search.implementation.BasicFieldConstraint. */
    //@Test
    public void testHashCode() {
        // TODO: implement test
    }


}
