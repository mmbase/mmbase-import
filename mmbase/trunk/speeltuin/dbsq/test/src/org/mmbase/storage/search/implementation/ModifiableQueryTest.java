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
 * @version $Revision: 1.1 $
 */
public class ModifiableQueryTest extends TestCase {
    
    private final static String IMAGES = "images";
    private final static String TITLE = "title";
    private final static String DESCRIPTION = "description";
    private final static String NAME = "name";
    private final static String POOLS = "pools";
    
    private final static int MAX_NUMBER1 = 123;
    private final static int MAX_NUMBER2 = 456;
    private final static int OFFSET1 = 987;
    private final static int OFFSET2 = 654;
    
    /** Test instance. */
    private ModifiableQuery instance = null;
    
    /** Original query. */
    private BasicSearchQuery query = null;
    
    private MMBase mmbase = null;
    private MMObjectBuilder images = null;
    private MMObjectBuilder pools = null;
    private FieldDefs imagesTitle = null;
    private FieldDefs imagesDescription = null;
    private FieldDefs poolsName = null;
    private Step step1 = null;
    private StepField field1 = null;
    
    public ModifiableQueryTest(java.lang.String testName) {
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
        images = mmbase.getBuilder(IMAGES);
        imagesTitle = images.getField(TITLE);
        imagesDescription = images.getField(DESCRIPTION);
        pools = mmbase.getBuilder(POOLS);
        poolsName = pools.getField(NAME);
        
        // Construct query.
        query = new BasicSearchQuery();
        step1 = query.addStep(images);
        field1 = query.addField(step1, imagesTitle);
        instance = new ModifiableQuery(query);
    }
    
    /**
     * Tears down after each test.
     */
    public void tearDown() throws Exception {}
    
    /** Test of setMaxNumber method, of class org.mmbase.storage.search.implementation.ModifiableQuery. */
    public void testSetMaxNumber() {
        query.setMaxNumber(MAX_NUMBER1);
        assertTrue(instance.getMaxNumber() == MAX_NUMBER1);
        instance.setMaxNumber(MAX_NUMBER2);
        assertTrue(instance.getMaxNumber() == MAX_NUMBER2);
        instance.setMaxNumber(-1);
        assertTrue(instance.getMaxNumber() == MAX_NUMBER1);
    }
    
    /** Test of setOffset method, of class org.mmbase.storage.search.implementation.ModifiableQuery. */
    public void testSetOffset() {
        query.setOffset(OFFSET1);
        assertTrue(instance.getOffset() == OFFSET1);
        instance.setOffset(OFFSET2);
        assertTrue(instance.getOffset() == OFFSET2);
        instance.setOffset(-1);
        assertTrue(instance.getOffset() == OFFSET1);        
    }
    
    /** Test of getMaxNumber method, of class org.mmbase.storage.search.implementation.ModifiableQuery. */
    public void testGetMaxNumber() {
        // Same as:
        testSetMaxNumber();
    }
    
    /** Test of getOffset method, of class org.mmbase.storage.search.implementation.ModifiableQuery. */
    public void testGetOffset() {
        // Same as:
        testSetOffset();
    }
    
    /** Test of getConstraint method, of class org.mmbase.storage.search.implementation.ModifiableQuery. */
    public void testGetConstraint() {
        Constraint c1 = new BasicFieldNullConstraint(field1);
        Constraint c2 = new BasicFieldValueConstraint(field1, "jjfkljfd");
        query.setConstraint(c1);
        assertTrue(instance.getConstraint().equals(c1));
        instance.setConstraint(c2);
        assertTrue(instance.getConstraint().equals(c2));
        instance.setConstraint(null);
        assertTrue(instance.getConstraint().equals(c1));
    }
    
    /** Test of getFields method, of class org.mmbase.storage.search.implementation.ModifiableQuery. */
    public void testGetFields() {
        StepField field2 = new BasicStepField(step1, imagesDescription);
        List fields1 = new ArrayList();
        fields1.add(field1);
        List fields2 = new ArrayList();
        fields2.add(field2);
        
        assertTrue(instance.getFields().equals(fields1));
        instance.setFields(fields2);
        assertTrue(instance.getFields().equals(fields2));
        instance.setFields(null);
        assertTrue(instance.getFields().equals(fields1));
    }
    
    /** Test of getSortOrders method, of class org.mmbase.storage.search.implementation.ModifiableQuery. */
    public void testGetSortOrders() {
        BasicSortOrder so1 = query.addSortOrder(field1);
        so1.setDirection(SortOrder.ORDER_ASCENDING);
        List sortOrders1 = new ArrayList();
        sortOrders1.add(so1);
        BasicSortOrder so2 = new BasicSortOrder(field1);
        so2.setDirection(SortOrder.ORDER_DESCENDING);
        List sortOrders2 = new ArrayList();
        sortOrders2.add(so2);
        
        assertTrue(instance.getSortOrders().equals(sortOrders1));
        instance.setSortOrders(sortOrders2);
        assertTrue(instance.getSortOrders().equals(sortOrders2));
        instance.setSortOrders(null);
        assertTrue(instance.getSortOrders().equals(sortOrders1));
    }
    
    /** Test of getSteps method, of class org.mmbase.storage.search.implementation.ModifiableQuery. */
    public void testGetSteps() {
        Step step2 = query.addStep(pools);
        List steps1 = new ArrayList();
        steps1.add(step1);
        steps1.add(step2);
        List steps2 = new ArrayList();
        steps2.add(step1);
        
        assertTrue(instance.getSteps().equals(steps1));
        instance.setSteps(steps2);
        assertTrue(instance.getSteps().equals(steps2));
        instance.setSteps(null);
        assertTrue(instance.getSteps().equals(steps1));
    }
    
    /** Test of isDistinct method, of class org.mmbase.storage.search.implementation.ModifiableQuery. */
    public void testIsDistinct() {
        assertTrue(!instance.isDistinct());
        instance.setDistinct(Boolean.TRUE);
        assertTrue(instance.isDistinct());
        instance.setDistinct(null);
        assertTrue(!instance.isDistinct());
    }
    
    /** Test of setConstraint method, of class org.mmbase.storage.search.implementation.ModifiableQuery. */
    public void testSetConstraint() {
        // Same as:
        testGetConstraint();
    }
    
    /** Test of setFields method, of class org.mmbase.storage.search.implementation.ModifiableQuery. */
    public void testSetFields() {
        // Same as:
        testGetFields();
    }
    
    /** Test of setSortOrders method, of class org.mmbase.storage.search.implementation.ModifiableQuery. */
    public void testSetSortOrders() {
        // Same as:
        testGetSortOrders();
    }
    
    /** Test of setSteps method, of class org.mmbase.storage.search.implementation.ModifiableQuery. */
    public void testSetSteps() {
        // Same as:
        testGetSteps();
    }
    
    /** Test of setDistinct method, of class org.mmbase.storage.search.implementation.ModifiableQuery. */
    public void testSetDistinct() {
        //  Same as:
        testIsDistinct();
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(ModifiableQueryTest.class);
        
        return suite;
    }
    
}
