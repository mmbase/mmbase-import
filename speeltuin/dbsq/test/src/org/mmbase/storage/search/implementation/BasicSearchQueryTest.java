package org.mmbase.storage.search.implementation;

import junit.framework.*;
import java.util.*;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.storage.search.*;

/**
 * JUnit tests.
 *
 * @author Rob van Maris
 * @version $Revision: 1.2 $
 */
public class BasicSearchQueryTest extends TestCase {
    
    /** Test instance. */
    private BasicSearchQuery instance;
    
    /** MMBase instance. */
    private MMBase mmbase = null;
    
    /** Images builder, used as builder example. */
    private MMObjectBuilder images = null;
    
    /** Pools builder, used as builder example. */
    private MMObjectBuilder pools = null;
    
    /** Insrel builder, used as relation builder example. */
    private InsRel insrel = null;
    
    public BasicSearchQueryTest(java.lang.String testName) {
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
        insrel = mmbase.getInsRel();
        pools = mmbase.getBuilder("pools");
        instance = new BasicSearchQuery();
    }
    
    /**
     * Tears down after each test.
     */
    public void tearDown() throws Exception {}
    
    /** Test of setDistinct method, of class org.mmbase.storage.search.implementation.BasicSearchQuery. */
    public void testSetDistinct() {
        // Default is false.
        assert(!instance.isDistinct());
        
        instance.setDistinct(true);
        assert(instance.isDistinct());
    }
    
    /** Test of setMaxNumber method, of class org.mmbase.storage.search.implementation.BasicSearchQuery. */
    public void testSetMaxNumber() {
        // Default is max integer value.
        assert(instance.getMaxNumber() == -1);
        
        instance.setMaxNumber(12345);
        assert(instance.getMaxNumber() == 12345);
        
        // Value less than -1, should throw IllegalArgumentException.
        try {
            instance.setMaxNumber(-2);
            fail("Value less than -1, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
    }
    
    /** Test of setOffset method, of class org.mmbase.storage.search.implementation.BasicSearchQuery. */
    public void testSetOffset() {
        // Default is zero.
        assert(instance.getOffset() == 0);
        
        // Invalid offset, should throw IllegalArgumentException.
        try {
            instance.setOffset(-789);
            fail("Invalid offset, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
        
        instance.setOffset(123456);
        assert(instance.getOffset() == 123456);
    }
    
    /** Test of addStep method, of class org.mmbase.storage.search.implementation.BasicSearchQuery. */
    public void testAddStep() {
        // Null argument, should throw IllegalArgumentException
        try {
            instance.addStep(null);
            fail("Null argument, should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
        
        List steps = instance.getSteps();
        assert(steps.size() == 0);
        Step step0 = instance.addStep(images);
        steps = instance.getSteps();
        assert(steps.size() == 1);
        assert(steps.get(0) == step0);
        Step step1 = instance.addStep(images);
        steps = instance.getSteps();
        assert(steps.size() == 2);
        assert(steps.get(0) == step0);
        assert(steps.get(1) == step1);
    }
    
    /** Test of addRelationStep method, of class org.mmbase.storage.search.implementation.BasicSearchQuery. */
    public void testAddRelationStep() {
        // No previous step, should throw IllegalStateException
        try {
            instance.addRelationStep(insrel, images);
            fail("No previous step, should throw IllegalStateException");
        } catch (IllegalStateException e) {}
        
        instance.addStep(images);
        
        // Null builder argument, should throw IllegalArgumentException
        try {
            instance.addRelationStep(null, images);
            fail("Null builder argument, should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
        
        // Null nextBuilder argument, should throw IllegalArgumentException
        try {
            instance.addRelationStep(insrel, null);
            fail("Null nextBuilder argument, should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
        
        List steps = instance.getSteps();
        assert(steps.size() == 1);
        RelationStep relationStep = instance.addRelationStep(insrel, images);
        steps = instance.getSteps();
        assert(steps.size() == 3);
        assert(steps.get(1) == relationStep);
        Step next = relationStep.getNext();
        assert(steps.get(2) == next);
    }
    
    /** Test of addField method, of class org.mmbase.storage.search.implementation.BasicSearchQuery. */
    public void testAddField() {
        Step step = instance.addStep(images);
        FieldDefs fieldDefs0 = images.getField("title");
        FieldDefs fieldDefs1 = images.getField("description");
        
        // Null step argument, should throw IllegalArgumentException
        try {
            instance.addField(null, fieldDefs0);
            fail("Null step argument, should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
        
        // Null fieldDefs argument, should throw IllegalArgumentException
        try {
            instance.addField(step, null);
            fail("Null fieldDefs argument, should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
        
        List fields = instance.getFields();
        assert(fields.size() == 0);
        StepField field0 = instance.addField(step, fieldDefs0);
        fields = instance.getFields();
        assert(fields.size() == 1);
        assert(fields.indexOf(field0) == 0);
        StepField field1 = instance.addField(step,fieldDefs1);
        fields = instance.getFields();
        assert(fields.size() == 2);
        assert(fields.indexOf(field0) == 0);
        assert(fields.indexOf(field1) == 1);
    }
    
    /** Test of addSortOrder method, of class org.mmbase.storage.search.implementation.BasicSearchQuery. */
    public void testAddSortOrder() {
        // Null step argument, should throw IllegalArgumentException
        try {
            instance.addSortOrder(null);
            fail("Null step argument, should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
        
        Step step = instance.addStep(images);
        FieldDefs fieldDefs0 = images.getField("title");
        FieldDefs fieldDefs1 = images.getField("description");
        StepField field0 = instance.addField(step, fieldDefs0);
        StepField field1 = instance.addField(step, fieldDefs1);
        
        List sortOrders = instance.getSortOrders();
        assert(sortOrders.size() == 0);
        SortOrder sortOrder0 = instance.addSortOrder(field0);
        sortOrders = instance.getSortOrders();
        assert(sortOrders.size() == 1);
        assert(sortOrders.indexOf(sortOrder0) == 0);
        SortOrder sortOrder1 = instance.addSortOrder(field1);
        sortOrders = instance.getSortOrders();
        assert(sortOrders.size() == 2);
        assert(sortOrders.indexOf(sortOrder0) == 0);
        assert(sortOrders.indexOf(sortOrder1) == 1);
    }
    
    /** Test of setConstraint method, of class org.mmbase.storage.search.implementation.BasicSearchQuery. */
    public void testSetConstraint() {
        // Default is null.
        assert(instance.getConstraint() == null);
        
        BasicConstraint constraint = new BasicConstraint();
        instance.setConstraint(constraint);
        assert(instance.getConstraint() == constraint);
        
        // Null value allowed.
        instance.setConstraint(null);
        assert(instance.getConstraint() == null);
   }
    
    /** Test of isDistinct method, of class org.mmbase.storage.search.implementation.BasicSearchQuery. */
    public void testIsDistinct() {
        // Same as:
        testSetDistinct();
    }
    
    /** Test of getSortOrders method, of class org.mmbase.storage.search.implementation.BasicSearchQuery. */
    public void testGetSortOrders() {
        // See:
        testAddSortOrder();
        
        List sortOrders = instance.getSortOrders();
        Object item = sortOrders.get(0);
        
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
    public void testGetSteps() {
        // See:
        testAddStep();
        
        List steps = instance.getSteps();
        Object item = steps.get(0);
        
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
    public void testGetFields() {
        // See:
        testAddField();
        
        List fields = instance.getFields();
        Object item = fields.get(0);
        
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
    public void testGetConstraint() {
        // Same as:
        testSetConstraint();
    }
    
    /** Test of getMaxNumber method, of class org.mmbase.storage.search.implementation.BasicSearchQuery. */
    public void testGetMaxNumber() {
        // Same as:
        testSetMaxNumber();
    }
    
    /** Test of getOffset method, of class org.mmbase.storage.search.implementation.BasicSearchQuery. */
    public void testGetOffset() {
        // Same as:
        testSetOffset();
    }
    
    /** Test of equals method, of class org.mmbase.storage.search.implementation.BasicSearchQuery. */
    public void testEquals() {
        // TODO: implement test
    }
    
    /** Test of hashCode method, of class org.mmbase.storage.search.implementation.BasicSearchQuery. */
    public void testHashCode() {
        // TODO: implement test
    }
    
    /** Test of toString method, of class org.mmbase.storage.search.implementation.BasicSearchQuery. */
    public void testToString() {
        
        BasicStep step1 = instance.addStep(images);
        BasicStepField field1a = instance.addField(step1, images.getField("title"));
        instance.setConstraint(new BasicFieldNullConstraint(field1a));
        instance.addSortOrder(field1a);

        assert(instance.toString(),
        instance.toString().equals(
        "SearchQuery(distinct:" + instance.isDistinct()
        + ", steps:" + instance.getSteps()
        + ", fields:" + instance.getFields()
        + ", constraint:" + instance.getConstraint()
        + ", sortorders:" + instance.getSortOrders()
        + ", max:" + instance.getMaxNumber()
        + ", offset:" + instance.getOffset() + ")"));

        instance.setDistinct(true);
        assert(instance.toString(),
        instance.toString().equals(
        "SearchQuery(distinct:" + instance.isDistinct()
        + ", steps:" + instance.getSteps()
        + ", fields:" + instance.getFields()
        + ", constraint:" + instance.getConstraint()
        + ", sortorders:" + instance.getSortOrders()
        + ", max:" + instance.getMaxNumber()
        + ", offset:" + instance.getOffset() + ")"));

        instance.setMaxNumber(100);
        instance.setOffset(50);
        assert(instance.toString(),
        instance.toString().equals(
        "SearchQuery(distinct:" + instance.isDistinct()
        + ", steps:" + instance.getSteps()
        + ", fields:" + instance.getFields()
        + ", constraint:" + instance.getConstraint()
        + ", sortorders:" + instance.getSortOrders()
        + ", max:" + instance.getMaxNumber()
        + ", offset:" + instance.getOffset() + ")"));
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(BasicSearchQueryTest.class);
        
        return suite;
    }
    
}
