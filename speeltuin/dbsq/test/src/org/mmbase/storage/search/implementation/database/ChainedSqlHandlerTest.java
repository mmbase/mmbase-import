package org.mmbase.storage.search.implementation.database;

import junit.textui.TestRunner;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;
import junit.framework.*;


/**
 * JUnit tests.
 *
 * @author Rob van Maris
 * @version $Revision: 1.1 $
 */
public class ChainedSqlHandlerTest extends TestCase {
    private final static int TEST_SUPPORT_LEVEL = 123;
    private final static String TEST = "abcd";
    
    /** Test instance. */
    private ChainedSqlHandler instance = null;
    
    /** TestSqlHandler instance, used as successor for test instance. */
    private TestSqlHandler testSuccessor = null;
    
    /** Test searchquery instance. */
    private SearchQuery query = null;
    
    public ChainedSqlHandlerTest(java.lang.String testName) {
        super(testName);
    }
    
    public static void main(java.lang.String[] args) {
        TestRunner.run(suite());
    }
    
    /**
     * Sets up before each test.
     */
    public void setUp() throws Exception {
        testSuccessor = new TestSqlHandler(TEST_SUPPORT_LEVEL);
        instance = new ChainedSqlHandler(testSuccessor);
        query = new BasicSearchQuery();
    }
    
    /**
     * Tears down after each test.
     */
    public void tearDown() throws Exception {}
    
    /** Test of toSql method, of class org.mmbase.storage.search.implementation.database.ChainedSqlHandler. */
    public void testToSql() throws Exception {
        String sql = instance.toSql(query, instance); 
        assert(sql != null);
        assert(sql.equals(testSuccessor.toSql(query, instance)));
    }
    
    /** Test of appendQueryBodyToSql method, of class org.mmbase.storage.search.implementation.database.ChainedSqlHandler. */
    public void testAppendQueryBodyToSql() throws Exception {
        StringBuffer sb = new StringBuffer();
        instance.appendQueryBodyToSql(sb, query, instance);
        String queryBody = sb.toString();
        sb.setLength(0);
        testSuccessor.appendQueryBodyToSql(sb, query, instance);
        String queryBody2 = sb.toString();
        assert(queryBody.length() > 0);
        assert(queryBody.equals(queryBody2));
    }
    
    /** Test of appendConstraintToSql method, of class org.mmbase.storage.search.implementation.database.ChainedSqlHandler. */
    public void testAppendConstraintToSql() throws Exception {
        StringBuffer sb = new StringBuffer();
        Constraint constraint 
        = new BasicCompositeConstraint(CompositeConstraint.LOGICAL_AND);
        instance.appendConstraintToSql(sb, constraint, query, false, false);
        String strConstraint = sb.toString();
        sb.setLength(0);
        testSuccessor.appendConstraintToSql(sb, constraint, query, false, false);
        String queryBody2 = sb.toString();
        assert(strConstraint.length() > 0);
        assert(strConstraint.equals(queryBody2));
    }
    
    /** Test of both getSupportLevel methods, of class org.mmbase.storage.search.implementation.database.ChainedSqlHandler. */
    public void testGetSupportLevel() throws Exception {
        // test method getSupport(int,SearchQuery)
        assert(instance.getSupportLevel(
        SearchQueryHandler.FEATURE_MAX_NUMBER, query)
        == TEST_SUPPORT_LEVEL);
        
        // test method getSupport(Constraint,SearchQuery)
        Constraint constraint 
        = new BasicCompositeConstraint(CompositeConstraint.LOGICAL_AND);
        assert(instance.getSupportLevel(constraint, query)
        == TEST_SUPPORT_LEVEL);
    }
    
    /** Test of getAllowedValue method, of class org.mmbase.storage.search.implementation.database.ChainedSqlHandler. */
    public void testGetAllowedValue() {
        String result = instance.getAllowedValue(TEST);
        assert(result.length() > 0);
        assert(result.equals(testSuccessor.getAllowedValue(TEST)));
    }
    
    /** Test of getSuccessor method, of class org.mmbase.storage.search.implementation.database.ChainedSqlHandler. */
    public void testGetSuccessor() {
        assert(instance.getSuccessor() == testSuccessor);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(ChainedSqlHandlerTest.class);
        
        return suite;
    }
    
}
