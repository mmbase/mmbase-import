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
public class BasicSearchQueryTest  {

    /** Test instance (non-aggregating). */
    private BasicSearchQuery instance1;

    /** Test instance (aggregating). */
    private BasicSearchQuery instance2;


    /** Images builder, used as builder example. */
    private NodeManager images = null;

    /** Insrel builder, used as relation builder example. */
    //private InsRel insrel = null;


    @BeforeClass
    public static void setUpClass() throws Exception {
        MockCloudContext.getInstance().addCore();
        MockCloudContext.getInstance().addCoreModel();
        MockCloudContext.getInstance().addNodeManagers(MockBuilderReader.getBuilderLoader().getChildResourceLoader("mynews"));
        MockCloudContext.getInstance().addNodeManagers(MockBuilderReader.getBuilderLoader().getChildResourceLoader("resources"));
    }

    /**
     * Sets up before each test.
     */
    @Before
    public void setUp() throws Exception {
        Cloud cloud = MockCloudContext.getInstance().getCloud("mmbase");
        images = cloud.getNodeManager("images");
        instance1 = new BasicSearchQuery();
        instance2 = new BasicSearchQuery(true);
    }

    /** Test of setDistinct method, of class org.mmbase.storage.search.implementation.BasicSearchQuery. */
    @Test
    public void testSetDistinct() {
        // Default is false.
        assertTrue(!instance1.isDistinct());

        BasicSearchQuery result = instance1.setDistinct(true);
        assertTrue(instance1.isDistinct());
        assertTrue(result == instance1);
    }

    @Test
    public void testIsAggregating() {
        assertTrue(!instance1.isAggregating());
        assertTrue(instance2.isAggregating());
    }

    /** Test of setMaxNumber method, of class org.mmbase.storage.search.implementation.BasicSearchQuery. */
    @Test
    public void testSetMaxNumber() {
        // Default is max integer value.
        assertTrue(instance1.getMaxNumber() == -1);

        BasicSearchQuery result = instance1.setMaxNumber(12345);
        assertTrue(instance1.getMaxNumber() == 12345);
        assertTrue(result == instance1);

        // Value less than -1, should throw IllegalArgumentException.
        try {
            instance1.setMaxNumber(-2);
            fail("Value less than -1, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
    }

    /** Test of setOffset method, of class org.mmbase.storage.search.implementation.BasicSearchQuery. */
    @Test
    public void testSetOffset() {
        // Default is zero.
        assertTrue(instance1.getOffset() == 0);

        // Invalid offset, should throw IllegalArgumentException.
        try {
            instance1.setOffset(-789);
            fail("Invalid offset, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}

        BasicSearchQuery result = instance1.setOffset(123456);
        assertTrue(instance1.getOffset() == 123456);
        assertTrue(result == instance1);
    }

    /** Test of addStep method, of class org.mmbase.storage.search.implementation.BasicSearchQuery. */
    @Test
    public void testAddStep() {
        // Null argument, should throw IllegalArgumentException
        try {
            instance1.addStep((String) null);
            fail("Null argument, should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {}

        List<Step> steps = instance1.getSteps();
        assertTrue(steps.size() == 0);
        Step step0 = instance1.addStep(images.getName());
        steps = instance1.getSteps();
        assertTrue(steps.size() == 1);
        assertTrue(steps.get(0) == step0);
        Step step1 = instance1.addStep(images.getName());
        steps = instance1.getSteps();
        assertTrue(steps.size() == 2);
        assertTrue(steps.get(0) == step0);
        assertTrue(steps.get(1) == step1);
    }

    /** Test of addRelationStep method, of class org.mmbase.storage.search.implementation.BasicSearchQuery. */
    @Test
    public void testAddRelationStep() {
        // No previous step, should throw IllegalStateException
        try {
            instance1.addRelationStep("insrel", images.getName());
            fail("No previous step, should throw IllegalStateException");
        } catch (IllegalStateException e) {}

        instance1.addStep(images.getName());

        // Null builder argument, should throw IllegalArgumentException
        try {
            instance1.addRelationStep(null, images.getName());
            fail("Null builder argument, should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {}

        // Null nextBuilder argument, should throw IllegalArgumentException
        try {
            instance1.addRelationStep("insrel", null);
            fail("Null nextBuilder argument, should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {}

        List<Step> steps = instance1.getSteps();
        assertTrue(steps.size() == 1);
        RelationStep relationStep = instance1.addRelationStep("insrel", images.getName());
        steps = instance1.getSteps();
        assertTrue(steps.size() == 3);
        assertTrue(steps.get(1) == relationStep);
        Step next = relationStep.getNext();
        assertTrue(steps.get(2) == next);
    }

    /** Test of addField method, of class org.mmbase.storage.search.implementation.BasicSearchQuery. */
    @Test
    public void testAddField() {
        Step step = instance1.addStep(images.getName());
        Field imagesTitle = images.getField("title");
        Field imagesDescription = images.getField("description");

        // Null step argument, should throw IllegalArgumentException
        try {
            instance1.addField(null, imagesTitle);
            fail("Null step argument, should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {}

        // Null CoreField argument, should throw IllegalArgumentException
        try {
            instance1.addField(step, null);
            fail("Null CoreField argument, should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {}

        List<StepField> fields = instance1.getFields();
        assertTrue(fields.size() == 0);
        StepField field0 = instance1.addField(step, imagesTitle);
        fields = instance1.getFields();
        assertTrue(fields.size() == 1);
        assertTrue(fields.indexOf(field0) == 0);
        StepField field1 = instance1.addField(step,imagesDescription);
        fields = instance1.getFields();
        assertTrue(fields.size() == 2);
        assertTrue(fields.indexOf(field0) == 0);
        assertTrue(fields.indexOf(field1) == 1);

        // Aggregating query:
        step = instance2.addStep(images.getName());
        try {
            // Adding non-aggregatedg step to aggregating query, should throw UnsupportedOperationException.
            instance2.addField(step, imagesTitle);
            fail("Adding non-aggregated step to aggregating query, should throw UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {}
    }

    /** Test of addAggregatedField method, of class org.mmbase.storage.search.implementation.BasicSearchQuery. */
    @Test
    public void testAddAggregatedField() {
        Step step = instance2.addStep(images.getName());
        Field imagesTitle = images.getField("title");
        Field imagesDescription = images.getField("description");

        // Null step argument, should throw IllegalArgumentException
        try {
            instance2.addAggregatedField(
            null, imagesTitle, AggregatedField.AGGREGATION_TYPE_COUNT);
            fail("Null step argument, should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {}

        // Null CoreField argument, should throw IllegalArgumentException
        try {
            instance2.addAggregatedField(
            step, null, AggregatedField.AGGREGATION_TYPE_COUNT);
            fail("Null CoreField argument, should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {}

        // Invalid aggregation type argument, should throw IllegalArgumentException
        try {
            instance2.addAggregatedField(
            step, imagesTitle, 0);
            fail("Invalid aggregation type argument, should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {}

        List<StepField> fields = instance2.getFields();
        assertTrue(fields.size() == 0);
        StepField field0 = instance2.addAggregatedField(
        step, imagesTitle, AggregatedField.AGGREGATION_TYPE_COUNT);
        fields = instance2.getFields();
        assertTrue(fields.size() == 1);
        assertTrue(fields.indexOf(field0) == 0);
        StepField field1 = instance2.addAggregatedField(
        step,imagesDescription, AggregatedField.AGGREGATION_TYPE_COUNT);
        fields = instance2.getFields();
        assertTrue(fields.size() == 2);
        assertTrue(fields.indexOf(field0) == 0);
        assertTrue(fields.indexOf(field1) == 1);

        // Non-aggregating query:
        step = instance1.addStep(images.getName());
        try {
            // Adding aggregated step to non-aggregating query, should throw UnsupportedOperationException.
            instance1.addAggregatedField(
            step, imagesTitle, AggregatedField.AGGREGATION_TYPE_COUNT);
            fail("Adding aggregated step to non-aggregating query, should throw UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {}
    }

    /** Test of addSortOrder method, of class org.mmbase.storage.search.implementation.BasicSearchQuery. */
    @Test
    public void testAddSortOrder() {
        // Null step argument, should throw IllegalArgumentException
        try {
            instance1.addSortOrder(null);
            fail("Null step argument, should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {}

        Step step = instance1.addStep(images.getName());
        Field imagesTitle = images.getField("title");
        Field imagesDescription = images.getField("description");
        StepField field0 = instance1.addField(step, imagesTitle);
        StepField field1 = instance1.addField(step, imagesDescription);

        List<SortOrder> sortOrders = instance1.getSortOrders();
        assertTrue(sortOrders.size() == 0);
        SortOrder sortOrder0 = instance1.addSortOrder(field0);
        sortOrders = instance1.getSortOrders();
        assertTrue(sortOrders.size() == 1);
        assertTrue(sortOrders.indexOf(sortOrder0) == 0);
        SortOrder sortOrder1 = instance1.addSortOrder(field1);
        sortOrders = instance1.getSortOrders();
        assertTrue(sortOrders.size() == 2);
        assertTrue(sortOrders.indexOf(sortOrder0) == 0);
        assertTrue(sortOrders.indexOf(sortOrder1) == 1);
    }

    /** Test of setConstraint method, of class org.mmbase.storage.search.implementation.BasicSearchQuery. */
    @Test
    public void testSetConstraint() {
        // Default is null.
        assertTrue(instance1.getConstraint() == null);

        BasicConstraint constraint = new BasicConstraint();
        instance1.setConstraint(constraint);
        assertTrue(instance1.getConstraint() == constraint);

        // Null value allowed.
        instance1.setConstraint(null);
        assertTrue(instance1.getConstraint() == null);
   }

    /** Test of isDistinct method, of class org.mmbase.storage.search.implementation.BasicSearchQuery. */
    //@Test
    public void testIsDistinct() {
        // Same as:
        testSetDistinct();
    }

    /** Test of getSortOrders method, of class org.mmbase.storage.search.implementation.BasicSearchQuery. */
    @Test
    public void testGetSortOrders() {
        // See:
        testAddSortOrder();

        List<SortOrder> sortOrders = instance1.getSortOrders();
        SortOrder item = sortOrders.get(0);

        // List returned must be unmodifiable.
        try {
            sortOrders.add(item);
            fail("Attempt to modify list, must throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {}
        try {
            sortOrders.clear();
            fail("Attempt to modify list, must throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {}
    }

    /** Test of getSteps method, of class org.mmbase.storage.search.implementation.BasicSearchQuery. */
    @Test
    public void testGetSteps() {
        // See:
        testAddStep();

        List<Step> steps = instance1.getSteps();
        Step item = steps.get(0);

        // List returned must be unmodifiable.
        try {
            steps.add(item);
            fail("Attempt to modify list, must throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {}
        try {
            steps.clear();
            fail("Attempt to modify list, must throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {}
    }

    /** Test of getFields method, of class org.mmbase.storage.search.implementation.BasicSearchQuery. */
    @Test
    public void testGetFields() {
        // See:
        testAddField();

        List<StepField> fields = instance1.getFields();
        StepField item = fields.get(0);

        // List returned must be unmodifiable.
        try {
            fields.add(item);
            fail("Attempt to modify list, must throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {}
        try {
            fields.clear();
            fail("Attempt to modify list, must throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {}
    }

    /** Test of getConstraint method, of class org.mmbase.storage.search.implementation.BasicSearchQuery. */
    //@Test
    public void testGetConstraint() {
        // Same as:
        testSetConstraint();
    }

    /** Test of getMaxNumber method, of class org.mmbase.storage.search.implementation.BasicSearchQuery. */
    //@Test
    public void testGetMaxNumber() {
        // Same as:
        testSetMaxNumber();
    }

    /** Test of getOffset method, of class org.mmbase.storage.search.implementation.BasicSearchQuery. */
    //@Test
    public void testGetOffset() {
        // Same as:
        testSetOffset();
    }

    /** Test of equals method, of class org.mmbase.storage.search.implementation.BasicSearchQuery. */
    //@Test
    public void testEquals() {
        // TODO: implement test
    }

    /** Test of hashCode method, of class org.mmbase.storage.search.implementation.BasicSearchQuery. */
    //@Test
    public void testHashCode() {
        // TODO: implement test
    }

    /** Test of toString method, of class org.mmbase.storage.search.implementation.BasicSearchQuery. */
    @Test
    public void testToString() {

        BasicStep step1 = instance1.addStep(images.getName());
        BasicStepField field1a = instance1.addField(step1, images.getField("title"));
        instance1.setConstraint(new BasicFieldNullConstraint(field1a));
        instance1.addSortOrder(field1a);

        assertTrue(instance1.toString(),
        instance1.toString().equals(
        "SearchQuery(distinct:" + instance1.isDistinct()
        + ", steps:" + instance1.getSteps()
        + ", fields:" + instance1.getFields()
        + ", constraint:" + instance1.getConstraint()
        + ", sortorders:" + instance1.getSortOrders()
        + ", max:" + instance1.getMaxNumber()
        + ", offset:" + instance1.getOffset() + ")"));

        instance1.setDistinct(true);
        assertTrue(instance1.toString(),
        instance1.toString().equals(
        "SearchQuery(distinct:" + instance1.isDistinct()
        + ", steps:" + instance1.getSteps()
        + ", fields:" + instance1.getFields()
        + ", constraint:" + instance1.getConstraint()
        + ", sortorders:" + instance1.getSortOrders()
        + ", max:" + instance1.getMaxNumber()
        + ", offset:" + instance1.getOffset() + ")"));

        instance1.setMaxNumber(100)
            .setOffset(50);
        assertTrue(instance1.toString(),
        instance1.toString().equals(
        "SearchQuery(distinct:" + instance1.isDistinct()
        + ", steps:" + instance1.getSteps()
        + ", fields:" + instance1.getFields()
        + ", constraint:" + instance1.getConstraint()
        + ", sortorders:" + instance1.getSortOrders()
        + ", max:" + instance1.getMaxNumber()
        + ", offset:" + instance1.getOffset() + ")"));
    }


}
