package org.mmbase.storage.search.implementation.database;

 import org.mmbase.core.CoreField;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;
import junit.framework.*;
import junit.textui.TestRunner;
import java.util.*;
import org.mmbase.module.core.*;

/**
 * JUnit tests.
 *
 * @author Rob van Maris
 * @version $Id$
 */
public class BasicQueryHandlerTest extends TestCase {

    /**
     * JUnit test user.
     * Nodes created by this user must be removed in the tearDown.
     */
    private final static String JUNIT_USER = "JUnitTester";

    /** Test instance. */
    private BasicQueryHandler instance;

    /** MMBase query. */
    private MMBase mmbase = null;

    /** News builder, used as builder example. */
    private MMObjectBuilder news = null;

    /**
     * Typedef builder, used as builder example where we need a number
     * of nodes (there are > 5 typedef nodes for the core nodetypes).
     */
    private MMObjectBuilder typedef = null;

    /** Insrel builder, used as relation builder example. */
    /** Test nodes, created in setUp, deleted in tearDown. */
    private List<MMObjectNode> testNodes = new ArrayList<MMObjectNode>();

    public BasicQueryHandlerTest(java.lang.String testName) {
        super(testName);
    }

    public static void main(java.lang.String[] args) {
        TestRunner.run(suite());
        System.exit(0);
    }

    /**
     * Sets up before each test.
     */
    public void setUp() throws Exception {
        MMBaseContext.init();
        mmbase = MMBase.getMMBase();
        news = mmbase.getBuilder("news");
        typedef = mmbase.getBuilder("typedef");

        // Disallowed fields map.
//        disallowedValues = new HashMap();
//        disallowedValues.put("number", "m_number");
//        disallowedValues.put("snumber", "m_snumber");
//        disallowedValues.put("dnumber", "m_dnumber");
//        disallowedValues.put("title", "m_title");
//        disallowedValues.put("i", "m_i");

        SqlHandler sqlHandler = new BasicSqlHandler();
        instance = new BasicQueryHandler(sqlHandler);

        // Add testnodes.
        MMObjectNode news1 = news.getNewNode(JUNIT_USER);
        news1.setValue("title", "_TE$T_1");
        news1.setValue("body", "News created for testing only.");
        news.insert(JUNIT_USER, news1);
        testNodes.add(news1);

        MMObjectNode news2 = news.getNewNode(JUNIT_USER);
        news2.setValue("title", "_TE$T_2");
        news2.setValue("body", "News created for testing only.");
        news.insert(JUNIT_USER, news2);
        testNodes.add(news2);
    }

    /**
     * Tears down after each test.
     */
    public void tearDown() throws Exception {
        // Remove all testnodes.
        Iterator<MMObjectNode> iTestNodes = testNodes.iterator();
        while (iTestNodes.hasNext()) {
            MMObjectNode testNode = iTestNodes.next();
            MMObjectBuilder builder = testNode.getBuilder();
            builder.removeRelations(testNode);
            builder.removeNode(testNode);
        }
    }

    /** Test of getNodes method, of class org.mmbase.storage.search.implementation.database.BasicQueryHandler. */
    public void testGetNodes() throws Exception {
        BasicSearchQuery query = null;
        // Test for real nodes.
        // TODO: add number field as well, once the queryhandler can be
        // properly initialized with the disallowed2allowed map specified
        // for the database that is actually used (instead of a dummy map
        // as is now the case).
        {
            query = new BasicSearchQuery();
            BasicStep newsStep = query.addStep(news) .setAlias("news1");
            CoreField newsTitle = news.getField("title");
            BasicStepField newsTitleField = query.addField(newsStep, newsTitle) .setAlias("a_title"); // should not affect result node fieldnames!
            query.addSortOrder(newsTitleField) .setDirection(SortOrder.ORDER_ASCENDING);
            CoreField newsDescription = news.getField("body");
            query.addField(newsStep, newsDescription);
            CoreField otypeDescription = news.getField("otype");
            query.addField(newsStep, otypeDescription);
            CoreField newsOwner = news.getField("owner");
            BasicStepField newsOwnerField = query.addField(newsStep, newsOwner);
            BasicFieldValueConstraint constraint = new BasicFieldValueConstraint(newsOwnerField, JUNIT_USER);
            query.setConstraint(constraint);
            List<MMObjectNode> resultNodes = instance.getNodes(query, mmbase.getBuilder("news"));
            Iterator<MMObjectNode> iResultNodes = resultNodes.iterator();
            Iterator<MMObjectNode> iTestNodes = testNodes.iterator();
            while (iTestNodes.hasNext()) {
                MMObjectNode testNode = iTestNodes.next();
                assertTrue(iResultNodes.hasNext());
                MMObjectNode resultNode = iResultNodes.next();
                assertTrue(resultNode.getBuilder() == news);
                assertTrue(resultNode.getStringValue("title") != null
                && resultNode.getStringValue("title").length() > 0);
                assertTrue("" + resultNode.getStringValue("title") + "!=" + testNode.getStringValue("title"), resultNode.getStringValue("title").equals(testNode.getStringValue("title")));
                assertTrue(resultNode.getStringValue("body") != null
                && resultNode.getStringValue("body").length() > 0);
                assertTrue(resultNode.getStringValue("body").equals(testNode.getStringValue("body")));
                assertTrue(resultNode.getStringValue("owner") != null
                && resultNode.getStringValue("owner").length() > 0);
                assertTrue(resultNode.getStringValue("owner").equals(testNode.getStringValue("owner")));
            }
            assertTrue(!iResultNodes.hasNext());
        }

        // Test for clusternodes.
        {
            query = new BasicSearchQuery();
            BasicStep newsStep = query.addStep(news)
                .setAlias("news1");
            CoreField newsTitle = news.getField("title");
            BasicStepField newsTitleField = query.addField(newsStep, newsTitle)
                .setAlias("a_title"); // should not affect result node fieldnames!
            query.addSortOrder(newsTitleField)
                .setDirection(SortOrder.ORDER_ASCENDING);
            CoreField newsDescription = news.getField("body");
            query.addField(newsStep, newsDescription);
            CoreField newsOwner = news.getField("owner");
            BasicStepField newsOwnerField = query.addField(newsStep, newsOwner);
            BasicFieldValueConstraint constraint
            = new BasicFieldValueConstraint(newsOwnerField, JUNIT_USER);
            query.setConstraint(constraint);
            List<MMObjectNode> resultNodes = instance.getNodes(query, mmbase.getClusterBuilder());
            Iterator<MMObjectNode> iResultNodes = resultNodes.iterator();
            Iterator<MMObjectNode> iTestNodes = testNodes.iterator();
            while (iTestNodes.hasNext()) {
                MMObjectNode testNode = iTestNodes.next();
                assertTrue(iResultNodes.hasNext());
                MMObjectNode resultNode = iResultNodes.next();
                assertTrue(resultNode instanceof ClusterNode);
                assertTrue(resultNode.getBuilder() == mmbase.getClusterBuilder());
                assertTrue(resultNode.toString(),
                    resultNode.getStringValue("news1.title") != null
                    && resultNode.getStringValue("news1.title").length() > 0);
                assertTrue(resultNode.getStringValue("news1.title").equals(testNode.getStringValue("title")));
                assertTrue(resultNode.getStringValue("news1.body") != null
                && resultNode.getStringValue("news1.body").length() > 0);
                assertTrue(resultNode.getStringValue("news1.body").equals(testNode.getStringValue("body")));
                assertTrue(resultNode.getStringValue("news1.owner") != null
                && resultNode.getStringValue("news1.owner").length() > 0);
                assertTrue(resultNode.getStringValue("news1.owner").equals(testNode.getStringValue("owner")));
            }
            assertTrue(!iResultNodes.hasNext());
        }

        // Test for clusternodes using NodeSearchQuery, should still return clusternodes
        {
            NodeSearchQuery nodeQuery = new NodeSearchQuery(news);
            CoreField newsTitle = news.getField("title");
            BasicStepField newsTitleField = nodeQuery.getField(newsTitle);
            nodeQuery.addSortOrder(newsTitleField)
                .setDirection(SortOrder.ORDER_ASCENDING);
            CoreField newsOwner = news.getField("owner");
            BasicStepField newsOwnerField = nodeQuery.getField(newsOwner);
            BasicFieldValueConstraint constraint
            = new BasicFieldValueConstraint(newsOwnerField, JUNIT_USER);
            nodeQuery.setConstraint(constraint);
            List<MMObjectNode> resultNodes = instance.getNodes(nodeQuery, mmbase.getClusterBuilder());
            Iterator<MMObjectNode> iResultNodes = resultNodes.iterator();
            Iterator<MMObjectNode> iTestNodes = testNodes.iterator();
            while (iTestNodes.hasNext()) {
                MMObjectNode testNode = iTestNodes.next();
                assertTrue(iResultNodes.hasNext());
                MMObjectNode resultNode = iResultNodes.next();
                assertTrue(resultNode instanceof ClusterNode);
                assertTrue(resultNode.getBuilder() == mmbase.getClusterBuilder());
                assertTrue(resultNode.toString(),
                    resultNode.getStringValue("news.title") != null
                    && resultNode.getStringValue("news.title").length() > 0);
                assertTrue(resultNode.getStringValue("news.title").equals(testNode.getStringValue("title")));
                assertTrue(resultNode.getStringValue("news.body") != null
                && resultNode.getStringValue("news.body").length() > 0);
                assertTrue(resultNode.getStringValue("news.body").equals(testNode.getStringValue("body")));
                assertTrue(resultNode.getStringValue("news.owner") != null
                && resultNode.getStringValue("news.owner").length() > 0);
                assertTrue(resultNode.getStringValue("news.owner").equals(testNode.getStringValue("owner")));
            }
            assertTrue(!iResultNodes.hasNext());
        }

        // Test for result nodes.
        {
            query = new BasicSearchQuery();
            BasicStep newsStep = query.addStep(news)
                .setAlias("news1");
            CoreField newsTitle = news.getField("title");
            BasicStepField newsTitleField = query.addField(newsStep, newsTitle)
                .setAlias("a_title");
            query.addSortOrder(newsTitleField)
                .setDirection(SortOrder.ORDER_ASCENDING);
            CoreField newsDescription = news.getField("body");
            query.addField(newsStep, newsDescription);
            CoreField newsOwner = news.getField("owner");
            BasicStepField newsOwnerField = query.addField(newsStep, newsOwner);
            BasicFieldValueConstraint constraint
            = new BasicFieldValueConstraint(newsOwnerField, JUNIT_USER);
            query.setConstraint(constraint);
            List<MMObjectNode> resultNodes = instance.getNodes(query, new ResultBuilder(mmbase, query));
            Iterator<MMObjectNode> iResultNodes = resultNodes.iterator();
            Iterator<MMObjectNode> iTestNodes = testNodes.iterator();
            while (iTestNodes.hasNext()) {
                MMObjectNode testNode = iTestNodes.next();
                assertTrue(iResultNodes.hasNext());
                MMObjectNode resultNode = iResultNodes.next();
                assertTrue(resultNode instanceof ResultNode);
                assertTrue(resultNode.getBuilder() instanceof ResultBuilder);
                assertTrue(resultNode.getStringValue("a_title") != null
                && resultNode.getStringValue("a_title").length() > 0);
                assertTrue(resultNode.getStringValue("a_title").equals(testNode.getStringValue("title")));
                assertTrue(resultNode.getStringValue("body") != null
                && resultNode.getStringValue("body").length() > 0);
                assertTrue(resultNode.getStringValue("body").equals(testNode.getStringValue("body")));
                assertTrue(resultNode.getStringValue("owner") != null
                && resultNode.getStringValue("owner").length() > 0);
                assertTrue(resultNode.getStringValue("owner").equals(testNode.getStringValue("owner")));
            }
            assertTrue(!iResultNodes.hasNext());
        }

        // Test for result nodes with aggregated fields.
        {
            query = new BasicSearchQuery(true);
            BasicStep newsStep = query.addStep(news)
                .setAlias("news1");
            CoreField newsTitle = news.getField("title");
            query.addAggregatedField(
                newsStep, newsTitle, AggregatedField.AGGREGATION_TYPE_MIN).
                setAlias("minName");
            query.addAggregatedField(
                newsStep, newsTitle, AggregatedField.AGGREGATION_TYPE_MAX).
                setAlias("maxName");

            CoreField newsOwner = news.getField("owner");
            BasicStepField newsOwnerField = new BasicStepField(newsStep, newsOwner);
            BasicFieldValueConstraint constraint
                = new BasicFieldValueConstraint(newsOwnerField, JUNIT_USER);
            query.setConstraint(constraint);
            List<MMObjectNode> resultNodes = instance.getNodes(query, new ResultBuilder(mmbase, query));
            assertTrue(resultNodes.size() == 1);

            // Determine min/max title from testnodes.
            Iterator<MMObjectNode> iTestNodes = testNodes.iterator();
            String minName =
                testNodes.get(0).getStringValue("title");
            String maxName =
                testNodes.get(0).getStringValue("title");
            while (iTestNodes.hasNext()) {
                MMObjectNode testNode = iTestNodes.next();
                String title = testNode.getStringValue("title");
                if (title.compareTo(minName) < 0) {
                    minName = title;
                } else if (title.compareTo(maxName) > 0) {
                    maxName = title;
                }
            }

            // Compare with resultnodes.
            ResultNode result = (ResultNode) resultNodes.get(0);
            assertTrue(result.getStringValue("minName").equals(minName));
            assertTrue(result.getStringValue("maxName").equals(maxName));
        }

        // Test weak offset support.
        query = new NodeSearchQuery(typedef);
        List<MMObjectNode> typedefNodes = instance.getNodes(query, typedef);
        assertTrue(
            "In order to run this test, more than 5 typedef nodes are required.",
            typedefNodes.size() > 5);

        query.setOffset(2);
        List<MMObjectNode> resultNodes = instance.getNodes(query, typedef);
        assertTrue(resultNodes.size() == typedefNodes.size() - 2);

        query.setMaxNumber(3);
        resultNodes = instance.getNodes(query, typedef);
        assertTrue(resultNodes.size() == 3);

    }

    /** Test of getSupportLevel(int,SearchQuery) method, of class org.mmbase.storage.search.implementation.database.BasicQueryHandler. */
    public void testGetSupportLevel() throws Exception {
        BasicSearchQuery query = new BasicSearchQuery();

        // Support for max number optimal only when set to default (= -1),
        // weak otherwise.
        assertTrue(instance.getSupportLevel(SearchQueryHandler.FEATURE_MAX_NUMBER, query)
        == SearchQueryHandler.SUPPORT_OPTIMAL);
        query.setMaxNumber(100);
        assertTrue(instance.getSupportLevel(SearchQueryHandler.FEATURE_MAX_NUMBER, query)
        == SearchQueryHandler.SUPPORT_NONE);
        query.setMaxNumber(-1);
        assertTrue(instance.getSupportLevel(SearchQueryHandler.FEATURE_MAX_NUMBER, query)
        == SearchQueryHandler.SUPPORT_OPTIMAL);

        // Support for offset optimal only when set to default (= 0),
        // weak otherwise.
        assertTrue(instance.getSupportLevel(SearchQueryHandler.FEATURE_OFFSET, query)
        == SearchQueryHandler.SUPPORT_OPTIMAL);
        query.setOffset(100);
        assertTrue(instance.getSupportLevel(SearchQueryHandler.FEATURE_OFFSET, query)
        == SearchQueryHandler.SUPPORT_NONE);
        query.setOffset(0);
        assertTrue(instance.getSupportLevel(SearchQueryHandler.FEATURE_OFFSET, query)
        == SearchQueryHandler.SUPPORT_OPTIMAL);

        // TODO: (later) test whith partial/full support for offset/maxNumber
    }

    /** Test of second getSupportLevel(Constraint,SearchQuery) method, of class org.mmbase.storage.search.implementation.database.BasicQueryHandler. */
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

    public static Test suite() {
        TestSuite suite = new TestSuite(BasicQueryHandlerTest.class);

        return suite;
    }

}
