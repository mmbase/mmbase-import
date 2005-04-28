package org.mmbase.storage.search.implementation;

import junit.framework.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.FieldDefs;
import org.mmbase.storage.search.*;

/**
 * JUnit tests.
 *
 * @author Rob van Maris
 * @version $Revision: 1.1 $
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
    
    /** FieldDefs. */
    private FieldDefs fieldDefs = null;
    
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
        fieldDefs = images.getField(TITLE);
        step = new BasicStep(images);
        instance = new BasicAggregatedField(step, fieldDefs, 
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
    
    /** Test of toString method, of class org.mmbase.storage.search.implementation.BasicAggregatedField. */
    public void testToString() {
        assertTrue(instance.toString(),
        instance.toString().equals(
        "AggregatedField(step:" + instance.getStep().getTableName()
        + ", fieldname:" + instance.getFieldName()
        + ", alias:" + instance.getAlias()
        + ", aggregationtype:" + instance.getAggregationType() + ")"));
        
        step.setAlias("images1");
        assertTrue(instance.toString(),
        instance.toString().equals(
        "AggregatedField(step:" + instance.getStep().getAlias()
        + ", fieldname:" + instance.getFieldName()
        + ", alias:" + instance.getAlias()
        + ", aggregationtype:" + instance.getAggregationType() + ")"));
        
        instance.setAlias("kjeiejk");
        assertTrue(instance.toString(),
        instance.toString().equals(
        "AggregatedField(step:" + instance.getStep().getAlias()
        + ", fieldname:" + instance.getFieldName()
        + ", alias:" + instance.getAlias()
        + ", aggregationtype:" + instance.getAggregationType() + ")"));
        
        instance.setAggregationType(AggregatedField.AGGREGATION_TYPE_GROUP_BY);
        assertTrue(instance.toString(),
        instance.toString().equals(
        "AggregatedField(step:" + instance.getStep().getAlias()
        + ", fieldname:" + instance.getFieldName()
        + ", alias:" + instance.getAlias()
        + ", aggregationtype:" + instance.getAggregationType() + ")"));
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(BasicAggregatedFieldTest.class);
        
        return suite;
    }
    
}
