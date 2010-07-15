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
public class ModifiableQueryTest  {

    private final static String IMAGES = "mags";
    private final static String TITLE = "title";
    private final static String DESCRIPTION = "description";
    private final static String NEWS = "news";

    private final static int MAX_NUMBER1 = 123;
    private final static int MAX_NUMBER2 = 456;
    private final static int OFFSET1 = 987;
    private final static int OFFSET2 = 654;

    /** Test instance. */
    private ModifiableQuery instance = null;

    /** Original query. */
    private BasicSearchQuery query = null;

    private NodeManager images = null;
    private NodeManager news = null;
    private Field imagesTitle = null;
    private Field imagesDescription = null;
    private Step step1 = null;
    private StepField field1 = null;




    @BeforeClass
    public static void setUpClass() throws Exception {
        MockCloudContext.getInstance().addCore();
        MockCloudContext.getInstance().addCoreModel();
        MockCloudContext.getInstance().addNodeManagers(MockBuilderReader.getBuilderLoader().getChildResourceLoader("mynews"));
    }

    /*
     * Sets up before each test.
     */
    @Before
    public void setUp() throws Exception {
        images = MockCloudContext.getInstance().getCloud("mmbase").getNodeManager(IMAGES);
        imagesTitle = images.getField(TITLE);
        imagesDescription = images.getField(DESCRIPTION);
        news = MockCloudContext.getInstance().getCloud("mmbase").getNodeManager(NEWS);
        news.getField(TITLE);

        // Construct query.
        query = new BasicSearchQuery();
        step1 = query.addStep(images.getName());
        field1 = query.addField(step1, imagesTitle);
        instance = new ModifiableQuery(query);
    }


    /** Test of setMaxNumber method, of class org.mmbase.storage.search.implementation.ModifiableQuery. */
    @Test
    public void testSetMaxNumber() {
        query.setMaxNumber(MAX_NUMBER1);
        assertTrue(instance.getMaxNumber() == MAX_NUMBER1);
        instance.setMaxNumber(MAX_NUMBER2);
        assertTrue(instance.getMaxNumber() == MAX_NUMBER2);
        ModifiableQuery result = instance.setMaxNumber(-1);
        assertTrue(instance.getMaxNumber() == MAX_NUMBER1);
        assertTrue(result == instance);
    }

    /** Test of setOffset method, of class org.mmbase.storage.search.implementation.ModifiableQuery. */
    @Test
    public void testSetOffset() {
        query.setOffset(OFFSET1);
        assertTrue(instance.getOffset() == OFFSET1);
        instance.setOffset(OFFSET2);
        assertTrue(instance.getOffset() == OFFSET2);
        ModifiableQuery result = instance.setOffset(-1);
        assertTrue(instance.getOffset() == OFFSET1);
        assertTrue(result == instance);
    }

    /** Test of getMaxNumber method, of class org.mmbase.storage.search.implementation.ModifiableQuery. */
    //@Test
    public void testGetMaxNumber() {
        // Same as:
        testSetMaxNumber();
    }

    /** Test of getOffset method, of class org.mmbase.storage.search.implementation.ModifiableQuery. */
    //@Test
    public void testGetOffset() {
        // Same as:
        testSetOffset();
    }

    /** Test of setConstraint method, of class org.mmbase.storage.search.implementation.ModifiableQuery. */
    @Test
    public void testSetConstraint() {
        Constraint c1 = new BasicFieldNullConstraint(field1);
        Constraint c2 = new BasicFieldValueConstraint(field1, "jjfkljfd");
        query.setConstraint(c1);
        assertTrue(instance.getConstraint().equals(c1));
        instance.setConstraint(c2);
        assertTrue(instance.getConstraint().equals(c2));
        ModifiableQuery result = instance.setConstraint(null);
        assertTrue(instance.getConstraint().equals(c1));
        assertTrue(result == instance);
    }

    /** Test of getConstraint method, of class org.mmbase.storage.search.implementation.ModifiableQuery. */
    //@Test
    public void testGetConstraint() {
        // Same as:
        testSetConstraint();
    }

    /** Test of setFields method, of class org.mmbase.storage.search.implementation.ModifiableQuery. */
    @Test
    public void testSetFields() {
        StepField field2 = new BasicStepField(step1, imagesDescription);
        List<StepField> fields1 = new ArrayList<StepField>();
        fields1.add(field1);
        List<StepField> fields2 = new ArrayList<StepField>();
        fields2.add(field2);

        assertTrue(instance.getFields().equals(fields1));
        instance.setFields(fields2);
        assertTrue(instance.getFields().equals(fields2));
        ModifiableQuery result = instance.setFields(null);
        assertTrue(instance.getFields().equals(fields1));
        assertTrue(result == instance);
    }

    /** Test of getFields method, of class org.mmbase.storage.search.implementation.ModifiableQuery. */
    //@Test
    public void testGetFields() {
        // Same as:
        testSetFields();
    }

    /** Test of setSortOrders method, of class org.mmbase.storage.search.implementation.ModifiableQuery. */
    @Test
    public void testSetSortOrders() {
        BasicSortOrder so1 = query.addSortOrder(field1)
            .setDirection(SortOrder.ORDER_ASCENDING);
        List<BasicSortOrder> sortOrders1 = new ArrayList<BasicSortOrder>();
        sortOrders1.add(so1);
        BasicSortOrder so2 = new BasicSortOrder(field1)
            .setDirection(SortOrder.ORDER_DESCENDING);
        List<SortOrder> sortOrders2 = new ArrayList<SortOrder>();
        sortOrders2.add(so2);

        assertTrue(instance.getSortOrders().equals(sortOrders1));
        instance.setSortOrders(sortOrders2);
        assertTrue(instance.getSortOrders().equals(sortOrders2));
        ModifiableQuery result = instance.setSortOrders(null);
        assertTrue(instance.getSortOrders().equals(sortOrders1));
        assertTrue(result == instance);
    }

    /** Test of getSortOrders method, of class org.mmbase.storage.search.implementation.ModifiableQuery. */
    //@Test
    public void testGetSortOrders() {
        // Same as:
        testSetSortOrders();
    }

    /** Test of setSteps method, of class org.mmbase.storage.search.implementation.ModifiableQuery. */
    @Test
    public void testSetSteps() {
        Step step2 = query.addStep(news.getName());
        List<Step> steps1 = new ArrayList<Step>();
        steps1.add(step1);
        steps1.add(step2);
        List<Step> steps2 = new ArrayList<Step>();
        steps2.add(step1);

        assertTrue(instance.getSteps().equals(steps1));
        instance.setSteps(steps2);
        assertTrue(instance.getSteps().equals(steps2));
        ModifiableQuery result = instance.setSteps(null);
        assertTrue(instance.getSteps().equals(steps1));
        assertTrue(result == instance);
    }

    /** Test of getSteps method, of class org.mmbase.storage.search.implementation.ModifiableQuery. */
    //@Test
    public void testGetSteps() {
        // Same as:
        testSetSteps();
    }

    /** Test of setDistinct method, of class org.mmbase.storage.search.implementation.ModifiableQuery. */
    @Test
    public void testSetDistinct() {
        assertTrue(!instance.isDistinct());
        instance.setDistinct(Boolean.TRUE);
        assertTrue(instance.isDistinct());
        ModifiableQuery result = instance.setDistinct(null);
        assertTrue(!instance.isDistinct());
        assertTrue(result == instance);
    }

    /** Test of isDistinct method, of class org.mmbase.storage.search.implementation.ModifiableQuery. */
    //@Test
    public void testIsDistinct() {
        //  Same as:
        testSetDistinct();
    }

    @Test
    public void testSetAggregating() {
        assertTrue(!instance.isAggregating());
        instance.setAggregating(Boolean.TRUE);
        assertTrue(instance.isAggregating());
        ModifiableQuery result = instance.setAggregating(null);
        assertTrue(!instance.isAggregating());
        assertTrue(result == instance);
    }

    //@Test
    public void testIsAggregating() {
        // Same as:
        testSetAggregating();
    }
}
