package org.mmbase.module.core;

import junit.framework.*;
import java.util.*;
import java.util.Date;
import java.sql.*;
import java.io.File;
import java.text.NumberFormat;
import java.text.DateFormat;
import java.net.URLEncoder;
import org.mmbase.util.*;
import org.mmbase.module.ParseException;
import org.mmbase.storage.StorageException;
import org.mmbase.module.database.MultiConnection;
import org.mmbase.module.database.support.MMJdbc2NodeInterface;
import org.mmbase.module.builders.DayMarkers;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.gui.html.EditState;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.util.logging.*;

/**
 * JUnit tests.
 *
 * @author Rob van Maris
 * @version $Revision: 1.1 $
 */
public class MMObjectBuilderTest extends TestCase {
    
    /**
     * JUnit test user.
     * Nodes created by this user must be removed in the tearDown.
     */
    private final static String JUNIT_USER = "JUnitTester";
    
    private final static int NR_TEST_NODES = 5;
    private final static String TEST_NAME = "_TE$T12_";
    
    /** Test instance. */
    private MMObjectBuilder instance = null;
    
    /** Test nodes. */
    private List testNodes = new ArrayList(NR_TEST_NODES);
    
    private MMBase mmbase = null;
    private MMObjectBuilder images = null;
    private FieldDefs poolsName = null;
    private FieldDefs poolsNumber = null;
    
    public MMObjectBuilderTest(java.lang.String testName) {
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
        instance = mmbase.getBuilder("pools");
        images = mmbase.getBuilder("images");
        poolsName = instance.getField("name");
        poolsNumber = instance.getField("number");
        
        // Add testnodes.
        for (int i = 0; i < NR_TEST_NODES; i++) {
            MMObjectNode node = instance.getNewNode(JUNIT_USER);
            node.setValue("name", TEST_NAME + (NR_TEST_NODES - 1 - i));
            node.setValue("description", "Pool created for testing only.");
            instance.insert(JUNIT_USER, node);
            testNodes.add(node);
        }
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
    
    public static Test suite() {
        TestSuite suite = new TestSuite(MMObjectBuilderTest.class);
        
        return suite;
    }
    
    /** Test of count(String) method, of class org.mmbase.module.core.MMObjectBuilder. */
    public void testCount() throws Exception {
        assertTrue(instance.count((String) null) >= NR_TEST_NODES);
        assertTrue(instance.count("") >= NR_TEST_NODES);
        
        for (int i = 0; i < (NR_TEST_NODES + 1); i++) {
            int result = instance.count(
                "MMNODE name=S" + TEST_NAME + i + "+name==" + TEST_NAME);
            assertTrue(Integer.toString(result), result == i);
        }
    }
    
    /** Test of count(NodeSearchQuery) method, of class org.mmbase.module.core.MMObjectBuilder. */
    public void testCount2() throws Exception {
        NodeSearchQuery query = new NodeSearchQuery(instance);

        try {
            // Wrong builder, should throw IllegalArgumentException.
            images.count(query);
            fail("Wrong builder, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
        
        assertTrue(instance.count(query) >= NR_TEST_NODES);
        StepField nameField = query.getField(poolsName);
        String value = "";
        
        BasicFieldValueConstraint constraint1 
            = new BasicFieldValueConstraint(nameField, value);
        constraint1.setOperator(FieldValueConstraint.LESS);
        BasicFieldValueConstraint constraint2
            = new BasicFieldValueConstraint(nameField, TEST_NAME + '%');
        constraint2.setOperator(FieldValueConstraint.LIKE);
        BasicCompositeConstraint constraint 
            = new BasicCompositeConstraint(CompositeConstraint.LOGICAL_AND);
        constraint.addChild(constraint1);
        constraint.addChild(constraint2);
        query.setConstraint(constraint);
        for (int i = 0; i < (NR_TEST_NODES + 1); i++) {
            value = TEST_NAME + i;
            constraint1.setValue(value);
            assertTrue(instance.count(query) == i);
        }
    }
    
    /** Test of getNodes method, of class org.mmbase.module.core.MMObjectBuilder. */
    public void testGetNodes() throws Exception {
        NodeSearchQuery query = new NodeSearchQuery(instance);

        try {
            // Wrong builder, should throw IllegalArgumentException.
            images.getNodes(query);
            fail("Wrong builder, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
        
        assertTrue(instance.getNodes(query).size() >= NR_TEST_NODES);
        StepField nameField = query.getField(poolsName);
        String value = "";
        
        BasicFieldValueConstraint constraint1 
            = new BasicFieldValueConstraint(nameField, value);
        constraint1.setOperator(FieldValueConstraint.LESS);
        BasicFieldValueConstraint constraint2
            = new BasicFieldValueConstraint(nameField, TEST_NAME + '%');
        constraint2.setOperator(FieldValueConstraint.LIKE);
        BasicCompositeConstraint constraint 
            = new BasicCompositeConstraint(CompositeConstraint.LOGICAL_AND);
        constraint.addChild(constraint1);
        constraint.addChild(constraint2);
        query.setConstraint(constraint);
        for (int i = 0; i < (NR_TEST_NODES + 1); i++) {
            value = TEST_NAME + i;
            constraint1.setValue(value);
            assertTrue(instance.getNodes(query).size() == i);
        }
        BasicSortOrder nameOrder = query.addSortOrder(nameField);
        nameOrder.setDirection(SortOrder.ORDER_DESCENDING);
        Iterator iResults = instance.getNodes(query).iterator();
        Iterator iTestNodes = testNodes.iterator();
        while (iTestNodes.hasNext()) {
            MMObjectNode testNode = (MMObjectNode) iTestNodes.next();
            MMObjectNode result = (MMObjectNode) iResults.next();
            // In cache, so must be same instance.
            assertTrue(areEqual(result, testNode));
        }
        assertTrue(!iResults.hasNext());
    }
    
    /** Test of searchList method, of class org.mmbase.module.core.MMObjectBuilder. */
    public void testSearchList() throws Exception {
        assertTrue(instance.searchList("").size() >= NR_TEST_NODES);
  
        List results = null;
        for (int i = 0; i < (NR_TEST_NODES + 1); i++) {
            String value = TEST_NAME + i;
            results = instance.searchList("WHERE name<'" + value 
                + "' AND name LIKE '" + TEST_NAME + "%' ORDER BY name DESC");
            assertTrue(results.size() == i);
        }
        Iterator iResults = results.iterator();
        Iterator iTestNodes = testNodes.iterator();
        while (iTestNodes.hasNext()) {
            MMObjectNode testNode = (MMObjectNode) iTestNodes.next();
            MMObjectNode result = (MMObjectNode) iResults.next();
            // In cache, so must be same instance.
            assertTrue(areEqual(result, testNode));
        }
        assertTrue(!iResults.hasNext());
    }
    
    /** Test of convertMMNodeSearch2Query method, of class org.mmbase.module.core.MMObjectBuilder. */
    public void testConvertMMNodeSearch2Query() throws Exception {

        try {
            // Invalid fieldname, should throw IllegalArgumentException.
            instance.convertMMNodeSearch2Query("MMNODE naamNNvalue");
            fail("Invalid fieldname, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
        
        try {
            // Invalid expression, should throw IllegalArgumentException.
            instance.convertMMNodeSearch2Query("MMNODE nameNNvalue");
            fail("Invalid expression, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
        
        try {
            // Invalid expression, should throw IllegalArgumentException.
            instance.convertMMNodeSearch2Query("MMNODE name=");
            fail("Invalid expression, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
        
        try {
            // Invalid expression, should throw IllegalArgumentException.
            instance.convertMMNodeSearch2Query("MMNODE name=N");
            fail("Invalid expression, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
        
        NodeSearchQuery query = instance.convertMMNodeSearch2Query("MMNODE ");
        assertTrue(query.getBuilder() == instance);
        FieldValueConstraint constraint 
            = (FieldValueConstraint) query.getConstraint();
        assertTrue(constraint == null);
        
        query = instance.convertMMNodeSearch2Query("MMNODE name=Nv");
        assertTrue(query.getBuilder() == instance);
        constraint = (FieldValueConstraint) query.getConstraint();
        assertTrue(constraint != null);
        assertTrue(constraint.getField() == query.getField(poolsName));
        assertTrue(constraint.getOperator() == FieldCompareConstraint.NOT_EQUAL);
        assertTrue(constraint.getValue().equals("v"));
        assertTrue(!constraint.isInverse());
        
        query = instance.convertMMNodeSearch2Query("MMNODE builder.name=Nvalue");
        assertTrue(query.getBuilder() == instance);
        constraint = (FieldValueConstraint) query.getConstraint();
        assertTrue(constraint != null);
        assertTrue(constraint.getField() == query.getField(poolsName));
        assertTrue(constraint.getOperator() == FieldCompareConstraint.NOT_EQUAL);
        assertTrue(constraint.getValue().equals("value"));
        assertTrue(!constraint.isInverse());
        
        query = instance.convertMMNodeSearch2Query("MMNODE name=Nvalue+number=E123");
        assertTrue(query.getBuilder() == instance);
        CompositeConstraint composite 
            = (CompositeConstraint) query.getConstraint();
        List constraints = composite.getChilds();
        assertTrue(constraints.size() == 2);
        constraint = (FieldValueConstraint) constraints.get(0);
        assertTrue(constraint != null);
        assertTrue(constraint.getField() == query.getField(poolsName));
        assertTrue(constraint.getOperator() == FieldCompareConstraint.NOT_EQUAL);
        assertTrue(constraint.getValue().equals("value"));
        assertTrue(!constraint.isInverse());
        constraint = (FieldValueConstraint) constraints.get(1);
        assertTrue(constraint != null);
        assertTrue(constraint.getField() == query.getField(poolsNumber));
        assertTrue(constraint.getOperator() == FieldCompareConstraint.EQUAL);
        assertTrue(constraint.getValue().equals(new Double(123)));
        assertTrue(!constraint.isInverse());
        
        query = instance.convertMMNodeSearch2Query("MMNODE name=Nvalue-number=E123");
        assertTrue(query.getBuilder() == instance);
        composite = (CompositeConstraint) query.getConstraint();
        constraints = composite.getChilds();
        assertTrue(constraints.size() == 2);
        constraint = (FieldValueConstraint) constraints.get(0);
        assertTrue(constraint != null);
        assertTrue(constraint.getField() == query.getField(poolsName));
        assertTrue(constraint.getOperator() == FieldCompareConstraint.NOT_EQUAL);
        assertTrue(constraint.getValue().equals("value"));
        assertTrue(!constraint.isInverse());
        constraint = (FieldValueConstraint) constraints.get(1);
        assertTrue(constraint != null);
        assertTrue(constraint.getField() == query.getField(poolsNumber));
        assertTrue(constraint.getOperator() == FieldCompareConstraint.EQUAL);
        assertTrue(constraint.getValue().equals(new Double(123)));
        assertTrue(constraint.isInverse());
        
    }

    /** Test of getSearchQuery(String) method, of class org.mmbase.module.core.MMObjectBuilder. */
    public void testGetSearchQuery() throws Exception {
        // Empty constraint.
        NodeSearchQuery query = instance.getSearchQuery(null);
        assertTrue(query.getBuilder() == instance);
        assertTrue(query.getConstraint() == null);
        
        // MMNODE expression.
        query = instance.getSearchQuery("MMNODE name=Nv");
        assertTrue(query.getBuilder() == instance);
        FieldValueConstraint constraint1 
            = (FieldValueConstraint) query.getConstraint();
        assertTrue(constraint1 != null);
        assertTrue(constraint1.getField() == query.getField(poolsName));
        assertTrue(constraint1.getOperator() == FieldCompareConstraint.NOT_EQUAL);
        assertTrue(constraint1.getValue().equals("v"));
        assertTrue(!constraint1.isInverse());
        
        // Other
        query = instance.getSearchQuery("WHERE ldlf kjd kjidji");
        assertTrue(query.getBuilder() == instance);
        LegacyConstraint constraint2 = (LegacyConstraint) query.getConstraint();
        assertTrue(constraint2 != null);
        assertTrue(constraint2.getConstraint().equals("ldlf kjd kjidji"));
    }

    /** Test of parseFieldPart method, of class org.mmbase.module.core.MMObjectBuilder. */
    public void testParseFieldPart() throws Exception {
        NodeSearchQuery query = new NodeSearchQuery(instance);
        
        // String field
        StepField stringField = query.getField(poolsName);
        
        FieldValueConstraint constraint 
            = instance.parseFieldPart(stringField, '=', "value");
        assertTrue(constraint.getField() == stringField);
        assertTrue(!constraint.isCaseSensitive());
        assertTrue(constraint.getOperator() == FieldCompareConstraint.LIKE);
        assertTrue(constraint.getValue().equals("%value%"));
        
        constraint = instance.parseFieldPart(stringField, '=', "*alu*");
        assertTrue(constraint.getField() == stringField);
        assertTrue(!constraint.isCaseSensitive());
        assertTrue(constraint.getOperator() == FieldCompareConstraint.LIKE);
        assertTrue(constraint.getValue().equals("%alu%"));
        
        constraint = instance.parseFieldPart(stringField, 'E', "value");
        assertTrue(constraint.getField() == stringField);
        assertTrue(!constraint.isCaseSensitive());
        assertTrue(constraint.getOperator() == FieldCompareConstraint.LIKE);
        assertTrue(constraint.getValue().equals("%value%"));
        
        constraint = instance.parseFieldPart(stringField, 'E', "*alu*");
        assertTrue(constraint.getField() == stringField);
        assertTrue(!constraint.isCaseSensitive());
        assertTrue(constraint.getOperator() == FieldCompareConstraint.LIKE);
        assertTrue(constraint.getValue().equals("%alu%"));
        
        constraint = instance.parseFieldPart(stringField, 'S', "value");
        assertTrue(constraint.getField() == stringField);
        assertTrue(constraint.isCaseSensitive());
        assertTrue(constraint.getOperator() == FieldCompareConstraint.LESS);
        assertTrue(constraint.getValue().equals("value"));
        
        constraint = instance.parseFieldPart(stringField, 's', "value");
        assertTrue(constraint.getField() == stringField);
        assertTrue(constraint.isCaseSensitive());
        assertTrue(constraint.getOperator() == FieldCompareConstraint.LESS_EQUAL);
        assertTrue(constraint.getValue().equals("value"));
        
        constraint = instance.parseFieldPart(stringField, 'N', "value");
        assertTrue(constraint.getField() == stringField);
        assertTrue(constraint.isCaseSensitive());
        assertTrue(constraint.getOperator() == FieldCompareConstraint.NOT_EQUAL);
        assertTrue(constraint.getValue().equals("value"));
        
        constraint = instance.parseFieldPart(stringField, 'G', "value");
        assertTrue(constraint.getField() == stringField);
        assertTrue(constraint.isCaseSensitive());
        assertTrue(constraint.getOperator() == FieldCompareConstraint.GREATER);
        assertTrue(constraint.getValue().equals("value"));
        
        constraint = instance.parseFieldPart(stringField, 'g', "value");
        assertTrue(constraint.getField() == stringField);
        assertTrue(constraint.isCaseSensitive());
        assertTrue(constraint.getOperator() == FieldCompareConstraint.GREATER_EQUAL);
        assertTrue(constraint.getValue().equals("value"));
        
        // Numerical field.
        StepField numberField = query.getField(poolsNumber);
        Double doubleValue = new Double("123.456");
        constraint = instance.parseFieldPart(numberField, '=', "123.456");
        assertTrue(constraint.getField() == numberField);
        assertTrue(constraint.getOperator() == FieldCompareConstraint.EQUAL);
        assertTrue(constraint.getValue().equals(doubleValue));
        
        constraint = instance.parseFieldPart(numberField, 'E', "123.456");
        assertTrue(constraint.getField() == numberField);
        assertTrue(constraint.getOperator() == FieldCompareConstraint.EQUAL);
        assertTrue(constraint.getValue().equals(doubleValue));
        
        constraint = instance.parseFieldPart(numberField, 'S', "123.456");
        assertTrue(constraint.getField() == numberField);
        assertTrue(constraint.getOperator() == FieldCompareConstraint.LESS);
        assertTrue(constraint.getValue().equals(doubleValue));
        
        constraint = instance.parseFieldPart(numberField, 's', "123.456");
        assertTrue(constraint.getField() == numberField);
        assertTrue(constraint.getOperator() == FieldCompareConstraint.LESS_EQUAL);
        assertTrue(constraint.getValue().equals(doubleValue));
        
        constraint = instance.parseFieldPart(numberField, 'N', "123.456");
        assertTrue(constraint.getField() == numberField);
        assertTrue(constraint.getOperator() == FieldCompareConstraint.NOT_EQUAL);
        assertTrue(constraint.getValue().equals(doubleValue));
        
        constraint = instance.parseFieldPart(numberField, 'G', "123.456");
        assertTrue(constraint.getField() == numberField);
        assertTrue(constraint.getOperator() == FieldCompareConstraint.GREATER);
        assertTrue(constraint.getValue().equals(doubleValue));
        
        constraint = instance.parseFieldPart(numberField, 'g', "123.456");
        assertTrue(constraint.getField() == numberField);
        assertTrue(constraint.getOperator() == FieldCompareConstraint.GREATER_EQUAL);
        assertTrue(constraint.getValue().equals(doubleValue));
        
        try {
            // Not a number, should throw NumberFormatException.
            instance.parseFieldPart(numberField, '=', "abcde");
            fail("Not a number, should throw NumberFormatException.");
        } catch (NumberFormatException e) {
        }
    }
    
    /**
     * Compares two nodes for equality, by comparing their type and all 
     * their fields.
     */
    private boolean areEqual(MMObjectNode node1, MMObjectNode node2) {
        int type = node1.getOType();
        if (node2.getOType() != type) {
            return false;
        }
        Iterator iFields = node1.getBuilder().getFields().iterator();
        while (iFields.hasNext()) {
            FieldDefs field = (FieldDefs) iFields.next();
            String fieldName = field.getDBName();
            if (!node1.getValue(fieldName).equals(node2.getValue(fieldName))) {
                return false;
            }
        }
        return true;
    }
}