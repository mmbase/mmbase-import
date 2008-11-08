package org.mmbase.storage.search.implementation.database;

import junit.textui.TestRunner;
import org.mmbase.storage.search.*;
import org.mmbase.module.core.*;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.storage.search.implementation.database.MySqlSqlHandler;
import junit.framework.*;
 import org.mmbase.core.CoreField;

/**
 * JUnit tests.
 *
 * @author Rob van Maris
 * @version $Revision: 1.7 $
 */
public class MySqlSqlHandlerTest extends TestCase {

    /** Test instance. */
    private MySqlSqlHandler instance;

    /** Prefix applied to buildernames to create tablenames. */
    private String prefix = null;

    /** MMBase query. */
    private MMBase mmbase = null;

    /** Images builder, used as builder example. */
    private MMObjectBuilder images = null;

    /** Test query. */
    private BasicSearchQuery query = null;

    public MySqlSqlHandlerTest(java.lang.String testName) {
        super(testName);
    }

    public static void main(java.lang.String[] args) {
        TestRunner.run(suite());
    }

    /**
     * Sets up before each test.
     */
    public void setUp() throws Exception {
        MMBaseContext.init();
        mmbase = MMBase.getMMBase();
        images = mmbase.getBuilder("images");

        instance = new MySqlSqlHandler();

        prefix = mmbase.getBaseName() + "_";

        query = new BasicSearchQuery();
        BasicStep imageStep = query.addStep(images);
        CoreField imageNumber = images.getField("number");
        BasicStepField imageNumberField = query.addField(imageStep, imageNumber);
        Constraint constraint = new BasicFieldNullConstraint(imageNumberField);
        query.setConstraint(constraint);
    }

    /**
     * Tears down after each test.
     */
    public void tearDown() throws Exception {}

    /** Test of getSupportLevel(int,SearchQuery) method, of class org.mmbase.storage.search.implementation.database.MySqlSqlHandler. */
    public void testGetSupportLevel() throws Exception {
        // Support max number.
        assertTrue(instance.getSupportLevel(SearchQueryHandler.FEATURE_MAX_NUMBER, query)
        == SearchQueryHandler.SUPPORT_OPTIMAL);
        query.setMaxNumber(100);
        assertTrue(instance.getSupportLevel(SearchQueryHandler.FEATURE_MAX_NUMBER, query)
        == SearchQueryHandler.SUPPORT_OPTIMAL);
        query.setMaxNumber(-1);
        assertTrue(instance.getSupportLevel(SearchQueryHandler.FEATURE_MAX_NUMBER, query)
        == SearchQueryHandler.SUPPORT_OPTIMAL);

        // Support offset.
        assertTrue(instance.getSupportLevel(SearchQueryHandler.FEATURE_OFFSET, query)
        == SearchQueryHandler.SUPPORT_OPTIMAL);
        query.setOffset(100);
        assertTrue(instance.getSupportLevel(SearchQueryHandler.FEATURE_OFFSET, query)
        == SearchQueryHandler.SUPPORT_OPTIMAL);
        query.setOffset(0);
        assertTrue(instance.getSupportLevel(SearchQueryHandler.FEATURE_OFFSET, query)
        == SearchQueryHandler.SUPPORT_OPTIMAL);
    }

    /** Test of getSupportLevel2(Constraint,SearchQuery) method, of class org.mmbase.storage.search.implementation.database.MySqlSqlHandler. */
    public void testGetSupportLevel2() throws Exception {
        // Should return basic support level of constraint.
        SearchQuery query = new BasicSearchQuery();
        Constraint constraint = new TestConstraint(SearchQueryHandler.SUPPORT_NONE);
        assertTrue(instance.getSupportLevel(constraint, query) == SearchQueryHandler.SUPPORT_NONE);
        constraint = new TestConstraint(SearchQueryHandler.SUPPORT_WEAK);
        assertTrue(instance.getSupportLevel(constraint, query) == SearchQueryHandler.SUPPORT_WEAK);
        constraint = new TestConstraint(SearchQueryHandler.SUPPORT_NORMAL);
        assertTrue(instance.getSupportLevel(constraint, query) == SearchQueryHandler.SUPPORT_NORMAL);
        constraint = new TestConstraint(SearchQueryHandler.SUPPORT_OPTIMAL);
        assertTrue(instance.getSupportLevel(constraint, query) == SearchQueryHandler.SUPPORT_OPTIMAL);
    }

    /** Test of toSql method, of class org.mmbase.storage.search.implementation.database.MySqlSqlHandler. */
    public void testToSql() throws Exception {
        // Test use of "LIMIT" construct.
        assertTrue(instance.toSql(query, instance),
        instance.toSql(query, instance).equalsIgnoreCase("SELECT number FROM " + prefix + "images IMAGES WHERE number IS NULL"));

        query.setMaxNumber(100);
        assertTrue(instance.toSql(query, instance),
        instance.toSql(query, instance).equalsIgnoreCase("SELECT number FROM " + prefix + "images IMAGES WHERE number IS NULL LIMIT 100"));

        query.setOffset(50);
        assertTrue(instance.toSql(query, instance),
        instance.toSql(query, instance).equalsIgnoreCase("SELECT number FROM " + prefix + "images IMAGES WHERE number IS NULL LIMIT 50,100"));

        query.setMaxNumber(-1);
        assertTrue(instance.toSql(query, instance),
        instance.toSql(query, instance).equalsIgnoreCase("SELECT number FROM " + prefix + "images IMAGES WHERE number IS NULL LIMIT 50," + Integer.MAX_VALUE));
    }


    public void testToSqlString() {

        assertEquals("abc", instance.toSqlString("abc"));
        assertEquals("a''bc", instance.toSqlString("a'bc"));
        assertEquals("a''''bc", instance.toSqlString("a''bc"));
        assertEquals("a\\\\bc", instance.toSqlString("a\\bc"));
        assertEquals("a\\\\''bc", instance.toSqlString("a\\'bc"));
        assertEquals("a\\tbc", instance.toSqlString("a\tbc"));
        assertEquals("a\\0bc", instance.toSqlString("a\0bc"));
        assertEquals("a\\\\0bc", instance.toSqlString("a\\0bc"));


        assertEquals("a%bc", instance.toSqlString("a%bc"));
        assertEquals("a_bc", instance.toSqlString("a_bc"));

        // Should it be possible to escape % and _ ?
        // These two cases could apply (now failing)
        //assertEquals("a\\%bc", instance.toSqlString("a\\%bc"));
        //assertEquals("a\\_bc", instance.toSqlString("a\\_bc"));
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(MySqlSqlHandlerTest.class);

        return suite;
    }

}
