package org.mmbase.module.database.search.implementation;

import junit.framework.*;
import java.util.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.FieldDefs;
import org.mmbase.module.database.search.*;
import org.mmbase.util.logging.*;

/**
 * JUnit tests.
 *
 * @author Rob van Maris
 * @version $Revision: 1.1 $
 */
public class InformixSqlHandlerTest extends TestCase {
    
    /** Test instance. */
    private InformixSqlHandler instance;
    
    /** Disallowed values map. */
    private Map disallowedValues = null;
    
    /** Prefix applied to buildernames to create tablenames. */
    private String prefix = null;
    
    /** MMBase query. */
    private MMBase mmbase = null;
    
    /** Images builder, used as builder example. */
    private MMObjectBuilder images = null;
    
    /** Test query. */
    private BasicSearchQuery query = null;
    
    public InformixSqlHandlerTest(java.lang.String testName) {
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
        
        // Disallowed fields map.
        disallowedValues = new HashMap();
        disallowedValues.put("number", "m_number");
        instance = new InformixSqlHandler(disallowedValues);
        
        prefix = mmbase.getBaseName() + "_";
        
        query = new BasicSearchQuery();
        BasicStep imageStep = query.addStep(images);
        FieldDefs imageNumber = images.getField("number");
        BasicStepField imageNumberField = query.addField(imageStep, imageNumber);
        Constraint constraint = new BasicFieldNullConstraint(imageNumberField);
        query.setConstraint(constraint);
    }
    
    /**
     * Tears down after each test.
     */
    public void tearDown() throws Exception {}
    
    /** Test of getSupportLevel(int,SearchQuery) method, of class org.mmbase.module.database.search.implementation.InformixSqlHandler. */
    public void testGetSupportLevel() throws Exception {
        // Support max number.
        assert(instance.getSupportLevel(SearchQueryHandler.FEATURE_MAX_NUMBER, query)
        == SearchQueryHandler.SUPPORT_OPTIMAL);
        query.setMaxNumber(100);
        assert(instance.getSupportLevel(SearchQueryHandler.FEATURE_MAX_NUMBER, query)
        == SearchQueryHandler.SUPPORT_OPTIMAL);
        query.setMaxNumber(-1);
        assert(instance.getSupportLevel(SearchQueryHandler.FEATURE_MAX_NUMBER, query)
        == SearchQueryHandler.SUPPORT_OPTIMAL);
        
        // Support offset only when set to default (= 0).
        assert(instance.getSupportLevel(SearchQueryHandler.FEATURE_OFFSET, query)
        == SearchQueryHandler.SUPPORT_OPTIMAL);
        query.setOffset(100);
        assert(instance.getSupportLevel(SearchQueryHandler.FEATURE_OFFSET, query)
        == SearchQueryHandler.SUPPORT_NONE);
        query.setOffset(0);
        assert(instance.getSupportLevel(SearchQueryHandler.FEATURE_OFFSET, query)
        == SearchQueryHandler.SUPPORT_OPTIMAL);
    }
    
    /** Test of getSupportLevel(Constraint,SearchQuery) method, of class org.mmbase.module.database.search.implementation.InformixSqlHandler. */
    public void testGetSupportLevel2() throws Exception {
        // Should return basic support level of constraint.
        SearchQuery query = new BasicSearchQuery();
        Constraint constraint = new TestConstraint(SearchQueryHandler.SUPPORT_NONE);
        assert(instance.getSupportLevel(constraint, query) == SearchQueryHandler.SUPPORT_NONE);
        constraint = new TestConstraint(SearchQueryHandler.SUPPORT_WEAK);
        assert(instance.getSupportLevel(constraint, query) == SearchQueryHandler.SUPPORT_WEAK);
        constraint = new TestConstraint(SearchQueryHandler.SUPPORT_NORMAL);
        assert(instance.getSupportLevel(constraint, query) == SearchQueryHandler.SUPPORT_NORMAL);
        constraint = new TestConstraint(SearchQueryHandler.SUPPORT_OPTIMAL);
        assert(instance.getSupportLevel(constraint, query) == SearchQueryHandler.SUPPORT_OPTIMAL);
    }
    
    /** Test of toSql method, of class org.mmbase.module.database.search.implementation.InformixSqlHandler. */
    public void testToSql() throws Exception {
        // Test use of "FIRST" construct.
        assert(instance.toSql(query, instance), 
        instance.toSql(query, instance).equals(
        "SELECT images.m_number AS m_number FROM " 
        + prefix + "images images WHERE images.m_number IS NULL"));
        
        query.setMaxNumber(100);
        assert(instance.toSql(query, instance), 
        instance.toSql(query, instance).equals(
        "SELECT FIRST 100 images.m_number AS m_number FROM " 
        + prefix + "images images WHERE images.m_number IS NULL"));
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(InformixSqlHandlerTest.class);
        
        return suite;
    }
    
}
