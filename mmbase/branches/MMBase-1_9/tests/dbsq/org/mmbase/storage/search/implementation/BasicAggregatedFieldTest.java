package org.mmbase.storage.search.implementation;

import junit.framework.*;

import org.mmbase.core.CoreField;
import org.mmbase.module.core.*;
import org.mmbase.storage.search.*;

/**
 * JUnit tests.
 *
 * @author Rob van Maris
 * @version $Revision: 1.3 $
 */
public class BasicAggregatedFieldTest extends TestCase {

    private final static String IMAGES = "images";
    private final static String TITLE = "title";

    /** Test instance. */
    private BasicAggregatedField instance;

    /** Associated step. */
    private BasicStep step;

    /** MMBase instance. */
    private MMBase mmbase = null;

    /** Builder. */
    private MMObjectBuilder images = null;

    /** CoreField. */
    private CoreField CoreField = null;

    public BasicAggregatedFieldTest(java.lang.String testName) {
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
        images = mmbase.getBuilder(IMAGES);
        CoreField = images.getField(TITLE);
        step = new BasicStep(images);
        instance = new BasicAggregatedField(step, CoreField,
        AggregatedField.AGGREGATION_TYPE_MAX);
    }

    /**
     * Tears down after each test.
     */
    public void tearDown() throws Exception {}

    /** Test of setAggregationType method, of class org.mmbase.storage.search.implementation.BasicAggregatedField. */
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
    public void testGetAggregationType() {
        // Same as:
        testSetAggregationType();
    }

    /** Test of equals method, of class org.mmbase.storage.search.implementation.BasicAggregatedField. */
    public void testEquals() {
        // TODO: implement
    }

    /** Test of hashCode method, of class org.mmbase.storage.search.implementation.BasicAggregatedField. */
    public void testHashCode() {
        // TODO: implement
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(BasicAggregatedFieldTest.class);

        return suite;
    }

}
