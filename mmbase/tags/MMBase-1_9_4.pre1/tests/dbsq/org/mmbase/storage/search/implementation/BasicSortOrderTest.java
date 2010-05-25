package org.mmbase.storage.search.implementation;

import junit.framework.*;
import org.mmbase.module.core.*;
 import org.mmbase.core.CoreField;
import org.mmbase.storage.search.*;

/**
 * JUnit tests.
 *
 * @author Rob van Maris
 * @version $Revision: 1.3 $
 */
public class BasicSortOrderTest extends TestCase {

    /** Test instance. */
    private BasicSortOrder instance = null;

    /** Associated field. */
    private BasicStepField field = null;

    /** MMBase instance. */
    private MMBase mmbase = null;

    public BasicSortOrderTest(java.lang.String testName) {
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
        MMObjectBuilder builder = mmbase.getBuilder("images");
        CoreField CoreField = builder.getField("title");
        Step step = new BasicStep(builder);
        field = new BasicStepField(step, CoreField);
        instance = new BasicSortOrder(field);
    }

    /**
     * Tears down after each test.
     */
    public void tearDown() throws Exception {}

    /** Test of setDirection method, of class org.mmbase.storage.search.implementation.BasicSortOrder. */
    public void testSetDirection() {
        // Default is SortOrder.ASCENDING.
        assertTrue(instance.getDirection() == SortOrder.ORDER_ASCENDING);

        // Invalid value, should throw IllegalArgumentException.
        try {
            instance.setDirection(-1);
            fail("Invalid value, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}

        BasicSortOrder result
            = instance.setDirection(SortOrder.ORDER_DESCENDING);
        assertTrue(instance.getDirection() == SortOrder.ORDER_DESCENDING);
        assertTrue(result == instance);
    }

    /** Test of getField method, of class org.mmbase.storage.search.implementation.BasicSortOrder. */
    public void testGetField() {
        assertTrue(instance.getField() == field);
    }

    /** Test of getDirection method, of class org.mmbase.storage.search.implementation.BasicSortOrder. */
    public void testGetDirection() {
        // Same as:
        testSetDirection();
    }

    /** Test of equals method, of class org.mmbase.storage.search.implementation.BasicSortOrder. */
    public void testEquals() {
        // TODO: implement test
    }

    /** Test of hashCode method, of class org.mmbase.storage.search.implementation.BasicSortOrder. */
    public void testHashCode() {
        // TODO: implement test
    }


    public static Test suite() {
        TestSuite suite = new TestSuite(BasicSortOrderTest.class);

        return suite;
    }

}
