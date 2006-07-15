package org.mmbase.storage.search.implementation;

import junit.framework.*;
import java.util.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.storage.search.*;

/**
 * JUnit tests.
 *
 * @author Rob van Maris
 * @version $Revision: 1.2 $
 */
public class NodeSearchQueryTest extends TestCase {
    
    /** Test instance. */
    private NodeSearchQuery instance = null;
    
    /** MMBase instance. */
    private MMBase mmbase = null;
    
    /** Exampler builders. */
    private MMObjectBuilder images = null;
    private MMObjectBuilder news = null;
    private InsRel insrel = null;
    
    /** Example fields. */
    private FieldDefs imagesTitle = null;
    private FieldDefs newsTitle = null;
    
    public NodeSearchQueryTest(java.lang.String testName) {
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
        images = mmbase.getBuilder("images");
        news = mmbase.getBuilder("news");
        insrel = mmbase.getInsRel();
        imagesTitle = images.getField("title");
        newsTitle = news.getField("title");
        instance = new NodeSearchQuery(images);
    }
    
    /**
     * Tears down after each test.
     */
    public void tearDown() throws Exception {}
    
    /** Test of constructor. */
    public void testConstructor() {
        try {
            // Null builder, should throw IllegalArgumentException.
            new NodeSearchQuery(null);
            fail("Null builder, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
            
        try {
            // Virtual builder, should throw IllegalArgumentException.
            new NodeSearchQuery(new ClusterBuilder(mmbase));
            fail("Virtual builder, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
            
        List fields = images.getFields();
        List stepFields = instance.getFields();
        Iterator iStepFields = stepFields.iterator();
        // Test all elements in stepFields are persistent fields from images.
        while (iStepFields.hasNext()) {
            StepField stepField = (StepField) iStepFields.next();
            FieldDefs field = images.getField(stepField.getFieldName());
            assertTrue(fields.contains(field));
            assertTrue(field.getDBType() != FieldDefs.TYPE_BYTE &&
                (field.getDBState() == FieldDefs.DBSTATE_PERSISTENT
                    || field.getDBState() == FieldDefs.DBSTATE_SYSTEM));
        }
        // Test all persistent fields from images are in query.
        Iterator iFields = fields.iterator();
        while (iFields.hasNext()) {
            FieldDefs field = (FieldDefs) iFields.next();
            if (field.getDBType() != FieldDefs.TYPE_BYTE &&
                (field.getDBState() == FieldDefs.DBSTATE_PERSISTENT
                    || field.getDBState() == FieldDefs.DBSTATE_SYSTEM)) {
                assertTrue(instance.getField(field) != null);
            }
        }
    }
    
    /** Test of getField method, of class org.mmbase.storage.search.implementation.NodeSearchQuery. */
    public void testGetField() {
        Step step = (Step) instance.getSteps().get(0);
        List fields = images.getFields();
        Iterator iFields = fields.iterator();
        while (iFields.hasNext()) {
            FieldDefs field = (FieldDefs) iFields.next();
            if (field.getDBType() != FieldDefs.TYPE_BYTE &&
                (field.getDBState() == FieldDefs.DBSTATE_PERSISTENT
                    || field.getDBState() == FieldDefs.DBSTATE_SYSTEM)) {
                StepField stepField = instance.getField(field);
                assertTrue(stepField != null);
                assertTrue(stepField.getFieldName().equals(field.getDBName()));
                assertTrue(stepField.getAlias() == null);
                assertTrue(stepField.getType() == field.getDBType());
                assertTrue(stepField.getStep().equals(step));
            } else {
                // Non-persistent field: should throw IllegalArgumentException.
                try {
                    instance.getField(field);
                    fail("Non-persistent field: should throw IllegalArgumentException.");
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
    public void testAddStep() {
        // Adding step, should throw UnsupportedOperationException.
        try {
            instance.addStep(news);
            fail("Adding step, should throw UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {}
    }
    
    /** Test of addRelationStep method, of class org.mmbase.storage.search.implementation.NodeSearchQuery. */
    public void testAddRelationStep() {
        // Adding relation step, should throw UnsupportedOperationException.
        try {
            instance.addRelationStep(insrel, news);
            fail("Adding relation step, should throw UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {}
    }
    
    /** Test of addField method, of class org.mmbase.storage.search.implementation.NodeSearchQuery. */
    public void testAddField() {
        Step step = (Step) instance.getSteps().get(0);

        // Adding field, should throw UnsupportedOperationException.
        try {
            instance.addField(step, imagesTitle);
            fail("Adding field, should throw UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {}
    }
    
    /** Test of addAggregatedField method, of class org.mmbase.storage.search.implementation.NodeSearchQuery. */
    public void testAddAggregatedField() {
        Step step = (Step) instance.getSteps().get(0);

        // Adding field, should throw UnsupportedOperationException.
        try {
            instance.addAggregatedField(step, imagesTitle, 
                AggregatedField.AGGREGATION_TYPE_MIN);
            fail("Adding field, should throw UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {}
    }
    
    /** Test of getBuilder method, of class org.mmbase.storage.search.implementation.NodeSearchQuery. */
    public void testGetBuilder() {
        assertTrue(new NodeSearchQuery(images).getBuilder() == images);
        assertTrue(new NodeSearchQuery(news).getBuilder() == news);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(NodeSearchQueryTest.class);
        
        return suite;
    }
    
}
