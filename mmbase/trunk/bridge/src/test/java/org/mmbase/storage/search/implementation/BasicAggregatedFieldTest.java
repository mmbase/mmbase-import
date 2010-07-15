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
public class BasicAggregatedFieldTest  {

    private final static String BUILDER = "news";
    private final static String TITLE = "title";

    /** Test instance. */
    private BasicAggregatedField instance;

    /** Associated step. */
    private BasicStep step;

    /** CoreField. */
    private Field field = null;


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
        NodeManager images = MockCloudContext.getInstance().getCloud("mmbase").getNodeManager(BUILDER);
        field = images.getField(TITLE);
        step = new BasicStep(BUILDER);
        instance = new BasicAggregatedField(step, field,
                                            AggregatedField.AGGREGATION_TYPE_MAX);
    }
    /** Test of setAggregationType method, of class org.mmbase.storage.search.implementation.BasicAggregatedField. */
    @Test
    public void testSetAggregationType() {
        assertTrue(instance.getAggregationType()
        == AggregatedField.AGGREGATION_TYPE_MAX);
        BasicAggregatedField result =
            instance.setAggregationType(
                AggregatedField.AGGREGATION_TYPE_GROUP_BY);
        assertTrue(instance.getAggregationType()
            == AggregatedField.AGGREGATION_TYPE_GROUP_BY);
        assertTrue(result == instance);

        try {
            // Invalid aggregation type, should throw IllegalArgumentException.
            instance.setAggregationType(0);
            fail("Invalid aggregation type, should throw IllegalArgumentException.");
            instance.setAggregationType(6);
            fail("Invalid aggregation type, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
    }

    /** Test of getAggregationType method, of class org.mmbase.storage.search.implementation.BasicAggregatedField. */
    //@Test
    public void testGetAggregationType() {
        // Same as:
        testSetAggregationType();
    }

    /** Test of equals method, of class org.mmbase.storage.search.implementation.BasicAggregatedField. */
    //@Test
    public void testEquals() {
        // TODO: implement
    }

    /** Test of hashCode method, of class org.mmbase.storage.search.implementation.BasicAggregatedField. */
    //@Test
    public void testHashCode() {
        // TODO: implement
    }


}
