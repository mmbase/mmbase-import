package org.mmbase.module.core;

import junit.framework.*;
import java.util.*;
import java.sql.*;
import org.mmbase.module.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.corebuilders.InsRel;
import org.mmbase.module.database.*;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * JUnit tests.
 *
 * @author Rob van Maris
 * @version $Revision: 1.1 $
 */
public class ClusterBuilderTest extends TestCase {
    
    /**
     * JUnit test user.
     * Nodes created by this user must be removed in the tearDown.
     */
    private final static String JUNIT_USER = "JUnitTester";
    
    /** Test instance. */
    private ClusterBuilder instance;
    
    /** Disallowed values map. */
    private Map disallowedValues = null;
    
    /** MMBase query. */
    private MMBase mmbase = null;
    
    /** Pools builder, used as builder example. */
    private MMObjectBuilder pools = null;
    
    /** Insrel builder, used as relation builder example. */
    /** Test nodes, created in setUp, deleted in tearDown. */
    private List testNodes = new ArrayList();
    
    public ClusterBuilderTest(java.lang.String testName) {
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
        pools = mmbase.getBuilder("pools");
        
        instance = mmbase.getClusterBuilder();
        
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
    public void tearDown() throws Exception {}
    
    public static Test suite() {
        TestSuite suite = new TestSuite(ClusterBuilderTest.class);
        
        return suite;
    }
    
    /** Test of testGetClusterNodes method, of class org.mmbase.module.core.ClusterBuilder. */
    public void testGetClusterNodes() throws Exception {
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
        List resultNodes = instance.getClusterNodes(query);
        Iterator iResultNodes = resultNodes.iterator();
        Iterator iTestNodes = testNodes.iterator();
        while (iTestNodes.hasNext()) {
            MMObjectNode testNode = (MMObjectNode) iTestNodes.next();
            assertTrue(iResultNodes.hasNext());
            MMObjectNode resultNode = (MMObjectNode) iResultNodes.next();
            assertTrue(resultNode instanceof ClusterNode);
            assertTrue(resultNode.getBuilder() == mmbase.getClusterBuilder());
            assertTrue(resultNode.getStringValue("pools1.name") != null
            && resultNode.getStringValue("pools1.name").length() > 0);
            assertTrue(resultNode.getStringValue("pools1.name").equals(testNode.getStringValue("name")));
            assertTrue(resultNode.getStringValue("pools1.description") != null
            && resultNode.getStringValue("pools1.description").length() > 0);
            assertTrue(resultNode.getStringValue("pools1.description").equals(testNode.getStringValue("description")));
            assertTrue(resultNode.getStringValue("pools1.owner") != null
            && resultNode.getStringValue("pools1.owner").length() > 0);
            assertTrue(resultNode.getStringValue("pools1.owner").equals(testNode.getStringValue("owner")));
        }
        assertTrue(!iResultNodes.hasNext());
    }
    
}
