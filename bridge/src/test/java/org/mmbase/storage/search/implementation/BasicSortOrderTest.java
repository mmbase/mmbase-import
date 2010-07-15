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
public class BasicSortOrderTest  {

    /** Test instance. */
    private BasicSortOrder instance = null;

    /** Associated field. */
    private BasicStepField field = null;



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
        NodeManager builder = MockCloudContext.getInstance().getCloud("mmbase").getNodeManager("news");
        Field f = builder.getField("title");
        Step step = new BasicStep(builder.getName());
        field = new BasicStepField(step, f);
        instance = new BasicSortOrder(field);
    }

    /** Test of setDirection method, of class org.mmbase.storage.search.implementation.BasicSortOrder. */
    @Test
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
    @Test
    public void testGetField() {
        assertTrue(instance.getField() == field);
    }

    /** Test of getDirection method, of class org.mmbase.storage.search.implementation.BasicSortOrder. */
    //@Test
    public void testGetDirection() {
        // Same as:
        testSetDirection();
    }

    /** Test of equals method, of class org.mmbase.storage.search.implementation.BasicSortOrder. */
    //@Test
    public void testEquals() {
        // TODO: implement test
    }

    /** Test of hashCode method, of class org.mmbase.storage.search.implementation.BasicSortOrder. */
    //@Test
    public void testHashCode() {
        // TODO: implement test
    }

}
