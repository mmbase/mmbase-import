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
public class NodeSearchQueryTest {

    /** Test instance. */
    private NodeSearchQuery instance = null;

    /** Exampler builders. */
    private NodeManager images = null;
    private NodeManager news = null;
    private NodeManager insrel = null;

    /** Example fields. */
    private Field imagesTitle = null;
    private Field newsTitle = null;

    private Cloud cloud;

    @BeforeClass
    public static void setUpClass() throws Exception {
        MockCloudContext.getInstance().addCore();
        MockCloudContext.getInstance().addCoreModel();
        MockCloudContext.getInstance().addNodeManagers(MockBuilderReader.getBuilderLoader().getChildResourceLoader("mynews"));
        MockCloudContext.getInstance().addNodeManagers(MockBuilderReader.getBuilderLoader().getChildResourceLoader("resources"));
    }
    /*
     * Sets up before each test.
     */
    @Before
    public void setUp() throws Exception {
        cloud = MockCloudContext.getInstance().getCloud("mmbase");
        images = cloud.getNodeManager("images");
        news = cloud.getNodeManager("news");
        //insrel = mmbase.getInsRel();
        imagesTitle = images.getField("title");
        newsTitle = news.getField("title");
        instance = new NodeSearchQuery(images);
    }


    /** Test of constructor. */
    @Test
    public void testConstructor() {
        try {
            // Null builder, should throw IllegalArgumentException.
            new NodeSearchQuery(null);
            fail("Null builder, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}

        /* TODO
        try {
            // Virtual builder, should throw IllegalArgumentException.
            new NodeSearchQuery(new ClusterBuilder(mmbase));
            fail("Virtual builder, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}

        */
        Collection<Field> fields = images.getFields();
        List<StepField> stepFields = instance.getFields();
        // Test all elements in stepFields are persistent fields from images.
        for (StepField stepField1 : stepFields) {
            StepField stepField = stepField1;
            Field field = images.getField(stepField.getFieldName());
            //assertTrue("" + fields + " does not contain " + field, fields.contains(field));
            //assertTrue(field.getType() != Field.TYPE_BINARY); // NodeSearchQuery is not in 'database', so it should not whine!
            assertTrue(!field.isVirtual());
        }
        // Test all persistent fields from images are in query.
        for (Field field1 : fields) {
            Field field = field1;
            if (field.getType() != Field.TYPE_BINARY && !field.isVirtual()) {
                assertTrue(instance.getField(field) != null);
            }
        }
    }

    /** Test of getField method, of class org.mmbase.storage.search.implementation.NodeSearchQuery. */
    @Test
    public void testGetField() {
        Step step = instance.getSteps().get(0);
        Collection<Field> fields = images.getFields();
        for (Field field : fields) {
            if (! field.isVirtual() && field.getType() != Field.TYPE_BINARY) {
                StepField stepField = instance.getField(field);
                assertTrue(stepField != null);
                assertTrue(stepField.getFieldName().equals(field.getName()));
                assertTrue(stepField.getAlias() == null);
                assertTrue(stepField.getType() == field.getType());
                assertTrue(stepField.getStep().equals(step));
            } else {
                // Non-persistent field or binary fields: should throw IllegalArgumentException.
                try {
                    instance.getField(field);
                    fail("Non-persistent field: '" + field + "' should throw IllegalArgumentException.");
                } catch (IllegalArgumentException e) {}
            }
        }

        // Field not belonging to images: should throw IllegalArgumentException.
        try {
            instance.getField(newsTitle);
            fail("Field not belonging to images: should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
    }

    /** Test of addStep method, of class org.mmbase.storage.search.implementation.NodeSearchQuery. */
    @Test
    public void testAddStep() {
        // Adding step, should throw UnsupportedOperationException.
        try {
            instance.addStep(news.getName());
            fail("Adding step, should throw UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {}
    }

    /** Test of addRelationStep method, of class org.mmbase.storage.search.implementation.NodeSearchQuery. */
    @Test
    public void testAddRelationStep() {
        // Adding relation step, should throw UnsupportedOperationException.
        try {
            instance.addRelationStep("insrel", news.getName());
            fail("Adding relation step, should throw UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {}
    }

    /** Test of addField method, of class org.mmbase.storage.search.implementation.NodeSearchQuery. */
    @Test
    public void testAddField() {
        Step step = instance.getSteps().get(0);

        // Adding field, should throw UnsupportedOperationException.
        try {
            instance.addField(step, imagesTitle);
            fail("Adding field, should throw UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {}
    }

    /** Test of addAggregatedField method, of class org.mmbase.storage.search.implementation.NodeSearchQuery. */
    @Test
    public void testAddAggregatedField() {
        Step step = instance.getSteps().get(0);

        // Adding field, should throw UnsupportedOperationException.
        try {
            instance.addAggregatedField(step, imagesTitle,
                AggregatedField.AGGREGATION_TYPE_MIN);
            fail("Adding field, should throw UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {}
    }

    /** Test of getBuilder method, of class org.mmbase.storage.search.implementation.NodeSearchQuery. */
    @Test
    public void testGetTablename() {
        assertEquals(images.getName(), new NodeSearchQuery(images).getTableName());
        assertEquals(news.getName(), new NodeSearchQuery(news).getTableName());
    }

}
