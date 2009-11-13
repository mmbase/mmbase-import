package org.mmbase.storage.search.implementation.database;

import junit.framework.*;
import org.mmbase.module.core.*;
 import org.mmbase.core.CoreField;
import org.mmbase.storage.search.implementation.database.InformixSqlHandler;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;

/**
 * JUnit tests.
 *
 * @author Rob van Maris
 * @version $Revision: 1.7 $
 */
public class InformixSqlHandlerTest extends TestCase {

    /** Test instance. */
    private InformixSqlHandler instance;

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

        instance = new InformixSqlHandler();

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

    /** Test of getSupportLevel(int,SearchQuery) method, of class org.mmbase.storage.search.implementation.database.InformixSqlHandler. */
    public void testGetSupportLevel() throws Exception {
        // Support max number.
        assertTrue(instance.getSupportLevel(SearchQueryHandler.FEATURE_MAX_NUMBER, query) == SearchQueryHandler.SUPPORT_OPTIMAL);
        query.setMaxNumber(100);
        assertTrue(instance.getSupportLevel(SearchQueryHandler.FEATURE_MAX_NUMBER, query) == SearchQueryHandler.SUPPORT_OPTIMAL);
        query.setMaxNumber(-1);
        assertTrue(instance.getSupportLevel(SearchQueryHandler.FEATURE_MAX_NUMBER, query) == SearchQueryHandler.SUPPORT_OPTIMAL);

        // Support offset only when set to default (= 0).
        assertTrue(instance.getSupportLevel(SearchQueryHandler.FEATURE_OFFSET, query) == SearchQueryHandler.SUPPORT_OPTIMAL);
        query.setOffset(100);
        assertTrue(instance.getSupportLevel(SearchQueryHandler.FEATURE_OFFSET, query) == SearchQueryHandler.SUPPORT_NONE);
        query.setOffset(0);
        assertTrue(instance.getSupportLevel(SearchQueryHandler.FEATURE_OFFSET, query) == SearchQueryHandler.SUPPORT_OPTIMAL);
    }

    /** Test of getSupportLevel(Constraint,SearchQuery) method, of class org.mmbase.storage.search.implementation.database.InformixSqlHandler. */
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

    /** Test of toSql method, of class org.mmbase.storage.search.implementation.database.InformixSqlHandler. */
    public void testToSql() throws Exception {
        // Test use of "FIRST" construct.
        assertTrue(instance.toSql(query, instance),
        instance.toSql(query, instance).equalsIgnoreCase("SELECT number FROM " + prefix + "images IMAGES WHERE number IS NULL"));

        query.setMaxNumber(100);
        assertTrue(instance.toSql(query, instance),
        instance.toSql(query, instance).equalsIgnoreCase("SELECT FIRST 100 number FROM " + prefix + "images IMAGES WHERE number IS NULL"));

        // Distinct keyword avoided in aggregating query.
        query = new BasicSearchQuery(true);
        BasicStep step1 = query.addStep(images).setAlias(null);
        CoreField imagesTitle = images.getField("title");
        query.addAggregatedField(step1, imagesTitle, AggregatedField.AGGREGATION_TYPE_COUNT).setAlias(null);
        query.setDistinct(true);
        String strSql = instance.toSql(query, instance);
        assertTrue(strSql, strSql.equalsIgnoreCase("SELECT COUNT(TITLE) FROM " + prefix + "images IMAGES"));
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(InformixSqlHandlerTest.class);

        return suite;
    }

}
