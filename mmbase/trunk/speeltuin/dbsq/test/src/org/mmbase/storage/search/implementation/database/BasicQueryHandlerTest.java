package org.mmbase.storage.search.implementation.database;

import org.mmbase.module.corebuilders.FieldDefs;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;
import junit.framework.*;
import junit.textui.TestRunner;
import java.util.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.database.support.MMJdbc2NodeInterface;
import org.mmbase.util.logging.*;
import org.mmbase.module.database.MultiConnection;
import java.sql.*;


/**
 * JUnit tests.
 *
 * @author Rob van Maris
 * @version $Revision: 1.1 $
 */
public class BasicQueryHandlerTest extends TestCase {
    
    /**
     * JUnit test user.
     * Nodes created by this user must be removed in the tearDown.
     */
    private final static String JUNIT_USER = "JUnitTester";
    
    /** Test instance. */
    private BasicQueryHandler instance;
    
    /** Test query. */
    private BasicSearchQuery query = null;
    
    /** Disallowed values map. */
    private Map disallowedValues = null;
    
    /** MMBase query. */
    private MMBase mmbase = null;
    
    /** Images builder, used as builder example. */
    
    /** Pools builder, used as builder example. */
    private MMObjectBuilder pools = null;
    
    /** Insrel builder, used as relation builder example. */
    /** Test nodes, created in setUp, deleted in tearDown. */
    private List testNodes = new ArrayList();
    
    public BasicQueryHandlerTest(java.lang.String testName) {
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
        pools = mmbase.getBuilder("pools");
        
        // Disallowed fields map.
        disallowedValues = new HashMap();
        disallowedValues.put("number", "m_number");
        disallowedValues.put("snumber", "m_snumber");
        disallowedValues.put("dnumber", "m_dnumber");
        disallowedValues.put("title", "m_title");
        disallowedValues.put("i", "m_i");
        
        SqlHandler sqlHandler = new BasicSqlHandler(disallowedValues);
        instance = new BasicQueryHandler(sqlHandler);
        query = new BasicSearchQuery();
        
        // Add testnodes.
        MMObjectNode pool1 = pools.getNewNode(JUNIT_USER);
        pool1.setValue("name", "_TE$T_1");
        pool1.setValue("description", "Pool created for testing only.");
        pools.insert(JUNIT_USER, pool1);
        testNodes.add(pool1);
        
        MMObjectNode pool2 = pools.getNewNode(JUNIT_USER);
        pool2.setValue("name", "_TE$T_2");
        pool2.setValue("description", "Pool created for testing only.");
        pools.insert(JUNIT_USER, pool2);
        testNodes.add(pool2);
    }
    
    /**
     * Tears down after each test.
     */
    public void tearDown() throws Exception {
        // Remove all testnodes.
        Iterator iTestNodes = testNodes.iterator();
        while (iTestNodes.hasNext()) {
            MMObjectNode testNode = (MMObjectNode) iTestNodes.next();
            MMObjectBuilder builder = testNode.getBuilder();
            builder.removeRelations(testNode);
            builder.removeNode(testNode);
        }
    }
    
    /** Test of getNodes method, of class org.mmbase.storage.search.implementation.database.BasicQueryHandler. */
    public void testGetNodes() throws Exception {
        // Test for real nodes.
        // TODO: add number field as well, once the queryhandler can be
        // properly initialized with the disallowed2allowed map specified
        // for the database that is actually used (instead of a dummy map
        // as is now the case).
        {
            BasicSearchQuery query = new BasicSearchQuery();
            BasicStep poolsStep = query.addStep(pools);
            poolsStep.setAlias("pools1");
            FieldDefs poolsName = pools.getField("name");
            BasicStepField poolsNameField = query.addField(poolsStep, poolsName);
            poolsNameField.setAlias("a_name"); // should not affect result node fieldnames!
            BasicSortOrder sortOrder = query.addSortOrder(poolsNameField);
            sortOrder.setDirection(SortOrder.ORDER_ASCENDING);
            FieldDefs poolsDescription = pools.getField("description");
            query.addField(poolsStep, poolsDescription);
            FieldDefs otypeDescription = pools.getField("otype");
            query.addField(poolsStep, otypeDescription);
            FieldDefs poolsOwner = pools.getField("owner");
            BasicStepField poolsOwnerField = query.addField(poolsStep, poolsOwner);
            BasicFieldValueConstraint constraint
            = new BasicFieldValueConstraint(poolsOwnerField, JUNIT_USER);
            query.setConstraint(constraint);
            List resultNodes = instance.getNodes(query, mmbase.getBuilder("pools"));
            Iterator iResultNodes = resultNodes.iterator();
            Iterator iTestNodes = testNodes.iterator();
            while (iTestNodes.hasNext()) {
                MMObjectNode testNode = (MMObjectNode) iTestNodes.next();
                assert(iResultNodes.hasNext());
                MMObjectNode resultNode = (MMObjectNode) iResultNodes.next();
                assert(resultNode.getBuilder() == mmbase.getBuilder("pools"));
                assert(resultNode.getStringValue("name") != null
                && resultNode.getStringValue("name").length() > 0);
                assert(resultNode.getStringValue("name").equals(testNode.getStringValue("name")));
                assert(resultNode.getStringValue("description") != null
                && resultNode.getStringValue("description").length() > 0);
                assert(resultNode.getStringValue("description").equals(testNode.getStringValue("description")));
                assert(resultNode.getStringValue("owner") != null
                && resultNode.getStringValue("owner").length() > 0);
                assert(resultNode.getStringValue("owner").equals(testNode.getStringValue("owner")));
            }
            assert(!iResultNodes.hasNext());
        }
        
        // Test for clusternodes.
        {
            BasicSearchQuery query = new BasicSearchQuery();
            BasicStep poolsStep = query.addStep(pools);
            poolsStep.setAlias("pools1");
            FieldDefs poolsName = pools.getField("name");
            BasicStepField poolsNameField = query.addField(poolsStep, poolsName);
            poolsNameField.setAlias("a_name"); // should not affect result node fieldnames!
            BasicSortOrder sortOrder = query.addSortOrder(poolsNameField);
            sortOrder.setDirection(SortOrder.ORDER_ASCENDING);
            FieldDefs poolsDescription = pools.getField("description");
            query.addField(poolsStep, poolsDescription);
            FieldDefs poolsOwner = pools.getField("owner");
            BasicStepField poolsOwnerField = query.addField(poolsStep, poolsOwner);
            BasicFieldValueConstraint constraint
            = new BasicFieldValueConstraint(poolsOwnerField, JUNIT_USER);
            query.setConstraint(constraint);
            List resultNodes = instance.getNodes(query, mmbase.getClusterBuilder());
            Iterator iResultNodes = resultNodes.iterator();
            Iterator iTestNodes = testNodes.iterator();
            while (iTestNodes.hasNext()) {
                MMObjectNode testNode = (MMObjectNode) iTestNodes.next();
                assert(iResultNodes.hasNext());
                MMObjectNode resultNode = (MMObjectNode) iResultNodes.next();
                assert(resultNode instanceof ClusterNode);
                assert(resultNode.getBuilder() == mmbase.getClusterBuilder());
                assert(resultNode.getStringValue("pools1.name") != null
                && resultNode.getStringValue("pools1.name").length() > 0);
                assert(resultNode.getStringValue("pools1.name").equals(testNode.getStringValue("name")));
                assert(resultNode.getStringValue("pools1.description") != null
                && resultNode.getStringValue("pools1.description").length() > 0);
                assert(resultNode.getStringValue("pools1.description").equals(testNode.getStringValue("description")));
                assert(resultNode.getStringValue("pools1.owner") != null
                && resultNode.getStringValue("pools1.owner").length() > 0);
                assert(resultNode.getStringValue("pools1.owner").equals(testNode.getStringValue("owner")));
            }
            assert(!iResultNodes.hasNext());
        }
    }
    
    /** Test of getSupportLevel(int,SearchQuery) method, of class org.mmbase.storage.search.implementation.database.BasicQueryHandler. */
    public void testGetSupportLevel() throws Exception {
        BasicSearchQuery query = new BasicSearchQuery();
        
        // Support max number only when set to default (= -1).
        assert(instance.getSupportLevel(SearchQueryHandler.FEATURE_MAX_NUMBER, query)
        == SearchQueryHandler.SUPPORT_OPTIMAL);
        query.setMaxNumber(100);
        assert(instance.getSupportLevel(SearchQueryHandler.FEATURE_MAX_NUMBER, query)
        == SearchQueryHandler.SUPPORT_NONE);
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
    
    /** Test of second getSupportLevel(Constraint,SearchQuery) method, of class org.mmbase.storage.search.implementation.database.BasicQueryHandler. */
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
    
    public static Test suite() {
        TestSuite suite = new TestSuite(BasicQueryHandlerTest.class);
        
        return suite;
    }
    
}
