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
 * @version $Revision: 1.5 $
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
        System.exit(0);
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
    
    /** Test of getNode(int,boolean) method, of class org.mmbase.module.core.MMObjectBuilder. */
    public void testGetNode() {
        Iterator iTestNodes = testNodes.iterator();
        while (iTestNodes.hasNext()) {
            MMObjectNode testNode = (MMObjectNode) iTestNodes.next();
            int nodeNumber = testNode.getNumber();
            MMObjectNode node = instance.getNode(nodeNumber, false);
            assertTrue(areEqual(node, testNode));
        }
    }
    
    /** Test of getNodeType(int) method, of class org.mmbase.module.core.MMObjectBuilder. */
    public void testGetNodeType() {
        Iterator iTestNodes = testNodes.iterator();
        while (iTestNodes.hasNext()) {
            MMObjectNode testNode = (MMObjectNode) iTestNodes.next();
            int nodeNumber = testNode.getNumber();
            int typedef = instance.getNodeType(nodeNumber);
            assertTrue(typedef == testNode.getOType());
        }
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
            = new BasicCompositeConstraint(CompositeConstraint.LOGICAL_AND)
                .addChild(constraint1)
                .addChild(constraint2);
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
            = new BasicCompositeConstraint(CompositeConstraint.LOGICAL_AND)
                .addChild(constraint1)
                .addChild(constraint2);
        query.setConstraint(constraint);
        for (int i = 0; i < (NR_TEST_NODES + 1); i++) {
            value = TEST_NAME + i;
            constraint1.setValue(value);
            assertTrue(instance.getNodes(query).size() == i);
        }
        BasicSortOrder nameOrder = query.addSortOrder(nameField)
            .setDirection(SortOrder.ORDER_DESCENDING);
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
                + "' AND name LIKE '" + TEST_NAME + "%'", "name", "DOWN");
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
    
    /** Test of searchVector(String) method, of class org.mmbase.module.core.MMObjectBuilder. */
    public void testSearchVector() throws Exception {
        assertTrue(instance.searchVector("").size() >= NR_TEST_NODES);
  
        List results = null;
        for (int i = 0; i < (NR_TEST_NODES + 1); i++) {
            String value = TEST_NAME + i;
            results = instance.searchVector("WHERE name<'" + value 
                + "' AND name LIKE '" + TEST_NAME + "%'", "name",  "DOWN");
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
        
        // AltaVista format.
        query = instance.getSearchQuery("name=N'te*?t'");
        assertTrue(query.getBuilder() == instance);
        FieldValueConstraint constraint1b
            = (FieldValueConstraint) query.getConstraint();
        assertTrue(constraint1b != null);
        assertTrue(constraint1b.getField() == query.getField(poolsName));
        assertTrue(constraint1b.getOperator() 
            == FieldCompareConstraint.NOT_EQUAL);
        assertTrue(constraint1b.getValue().equals("te%_t"));
        assertTrue(!constraint1b.isInverse());
        
        // Other
        query = instance.getSearchQuery("WHERE ldlf kjd kjidji");
        assertTrue(query.getBuilder() == instance);
        LegacyConstraint constraint2 = (LegacyConstraint) query.getConstraint();
        assertTrue(constraint2 != null);
        assertTrue(constraint2.getConstraint().equals("ldlf kjd kjidji"));
    }

    /** Test of getSearchQuery(String,String,String) method, of class org.mmbase.module.core.MMObjectBuilder. */
    public void testGetSearchQuery2() throws Exception {
        // Empty constraint (without sortorders).
        NodeSearchQuery query = instance.getSearchQuery(null, "", "");
        assertTrue(query.getBuilder() == instance);
        assertTrue(query.getConstraint() == null);
        
        // MMNODE expression (without sortorders).
        query = instance.getSearchQuery("MMNODE name=Nv", "", "");
        assertTrue(query.getBuilder() == instance);
        FieldValueConstraint constraint1 
            = (FieldValueConstraint) query.getConstraint();
        assertTrue(constraint1 != null);
        assertTrue(constraint1.getField() == query.getField(poolsName));
        assertTrue(constraint1.getOperator() == FieldCompareConstraint.NOT_EQUAL);
        assertTrue(constraint1.getValue().equals("v"));
        assertTrue(!constraint1.isInverse());
        
        // AltaVista format (without sortorders).
        query = instance.getSearchQuery("name=N'te*?t'");
        assertTrue(query.getBuilder() == instance);
        FieldValueConstraint constraint1b
            = (FieldValueConstraint) query.getConstraint();
        assertTrue(constraint1b != null);
        assertTrue(constraint1b.getField() == query.getField(poolsName));
        assertTrue(constraint1b.getOperator() 
            == FieldCompareConstraint.NOT_EQUAL);
        assertTrue(constraint1b.getValue().equals("te%_t"));
        assertTrue(!constraint1b.isInverse());
        
        // Other (without sortorders).
        query = instance.getSearchQuery("WHERE ldlf kjd kjidji", "", "");
        assertTrue(query.getBuilder() == instance);
        LegacyConstraint constraint2 = (LegacyConstraint) query.getConstraint();
        assertTrue(constraint2 != null);
        assertTrue(constraint2.getConstraint().equals("ldlf kjd kjidji"));
        
        // Test sortorders.
        List sortOrders;
        SortOrder so;
        
        query = instance.getSearchQuery(null, "number, name", "");
        sortOrders = query.getSortOrders();
        assertTrue(sortOrders.size() == 2);
        so = (SortOrder) sortOrders.get(0);
        assertTrue(so.getField() == query.getField(poolsNumber));
        assertTrue(so.getDirection() == SortOrder.ORDER_ASCENDING);
        so = (SortOrder) sortOrders.get(1);
        assertTrue(so.getField() == query.getField(poolsName));
        assertTrue(so.getDirection() == SortOrder.ORDER_ASCENDING);
        
        query = instance.getSearchQuery(null, "number, name", "UP");
        sortOrders = query.getSortOrders();
        assertTrue(sortOrders.size() == 2);
        so = (SortOrder) sortOrders.get(0);
        assertTrue(so.getField() == query.getField(poolsNumber));
        assertTrue(so.getDirection() == SortOrder.ORDER_ASCENDING);
        so = (SortOrder) sortOrders.get(1);
        assertTrue(so.getField() == query.getField(poolsName));
        assertTrue(so.getDirection() == SortOrder.ORDER_ASCENDING);
        
        query = instance.getSearchQuery(null, "number, name", "DOWN");
        sortOrders = query.getSortOrders();
        assertTrue(sortOrders.size() == 2);
        so = (SortOrder) sortOrders.get(0);
        assertTrue(so.getField() == query.getField(poolsNumber));
        assertTrue(so.getDirection() == SortOrder.ORDER_DESCENDING);
        so = (SortOrder) sortOrders.get(1);
        assertTrue(so.getField() == query.getField(poolsName));
        assertTrue(so.getDirection() == SortOrder.ORDER_DESCENDING);
        
        query = instance.getSearchQuery(null, "number, name", "UP, DOWN");
        sortOrders = query.getSortOrders();
        assertTrue(sortOrders.size() == 2);
        so = (SortOrder) sortOrders.get(0);
        assertTrue(so.getField() == query.getField(poolsNumber));
        assertTrue(so.getDirection() == SortOrder.ORDER_ASCENDING);
        so = (SortOrder) sortOrders.get(1);
        assertTrue(so.getField() == query.getField(poolsName));
        assertTrue(so.getDirection() == SortOrder.ORDER_DESCENDING);
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
    
    /** Test of searchNumbers(String) method, of class org.mmbase.module.core.MMObjectBuilder. */
    public void testSearchNumbers() throws Exception {

        assertTrue(instance.searchNumbers("").size() >= NR_TEST_NODES);
        
        for (int i = 0; i < (NR_TEST_NODES + 1); i++) {
            // Numbers retrieved by the method.
            String value = TEST_NAME + i;
            Vector numbers = instance.searchNumbers("WHERE name < '" + value + "'");
            
            // Numbers retrieved by query.
            NodeSearchQuery query = new NodeSearchQuery(instance);
            StepField field = query.getField(poolsName);
            BasicFieldValueConstraint constraint 
                = new BasicFieldValueConstraint(field, value);
            constraint.setOperator(FieldValueConstraint.LESS);
            query.setConstraint(constraint);
            Iterator iResultNodes = mmbase.getDatabase()
                .getNodes(query, new ResultBuilder(mmbase, query)).iterator();
            List resultNumbers = new ArrayList();
            while (iResultNodes.hasNext()) {
                ResultNode resultNode = (ResultNode) iResultNodes.next();
                resultNumbers.add(resultNode.getIntegerValue("number"));
            }
            
            // Should contain same elements.
            assertTrue(new HashSet(resultNumbers).
                equals(new HashSet(numbers)));
        }
    }
    
    /** Test of searchVectorIn(String) method, of class org.mmbase.module.core.MMObjectBuilder. */
    public void testSearchVectorIn() throws Exception {
        for (int i = 0; i < (NR_TEST_NODES + 1); i++) {
            // Numbers retrieved by query.
            String value = TEST_NAME + i;
            Vector numbers = instance.searchNumbers("WHERE name < '" + value + "'");
            
            // Numbers retrieved by method.
            Vector resultNumbers = new Vector();
            Iterator iNodes 
                = instance.searchVectorIn(toCSV(numbers)).iterator();
            while (iNodes.hasNext()) {
                MMObjectNode node = (MMObjectNode) iNodes.next();
                resultNumbers.add(node.getIntegerValue("number"));
            }
            
            // Should be equal.
            assertTrue(resultNumbers.equals(numbers));
        }
    }
  
    /** Test of searchVectorIn(String,String) method, of class org.mmbase.module.core.MMObjectBuilder. */
    public void testSearchVectorIn2() throws Exception {
        for (int i = 0; i < (NR_TEST_NODES + 1); i++) {
            // Numbers retrieved by query.
            String value = TEST_NAME + i;
            Vector numbers = instance.searchNumbers("WHERE name < '" + value + "'");
            
            // Numbers retrieved by method.
            Vector resultNumbers = new Vector();
            Iterator iNodes = instance.searchVectorIn(
                "WHERE name < '" + value + "'", toCSV(numbers)).iterator();
            while (iNodes.hasNext()) {
                MMObjectNode node = (MMObjectNode) iNodes.next();
                resultNumbers.add(node.getIntegerValue("number"));
            }
            
            // Should be equal.
            assertTrue(resultNumbers.equals(numbers));

            List nodes = instance.searchVectorIn(
                "WHERE name >= '" + value + "'", toCSV(numbers));
            assertTrue(nodes.size() == 0);
        }
    }
  
    /** Test of searchVectorIn(String,String,String) method, of class org.mmbase.module.core.MMObjectBuilder. */
    public void testSearchVectorIn3() throws Exception {
        for (int i = 0; i < (NR_TEST_NODES + 1); i++) {
            String value = TEST_NAME + i;

            // Numbers retrieved by query.
            Vector numbers = instance.searchNumbers("WHERE name < '" + value + "'");
            
            // Numbers retrieved by method.
            List nodes = instance.searchVectorIn(
                "WHERE name < '" + value + "'", "number", toCSV(numbers));
            ListIterator iNodes = nodes.listIterator();
            int indexNumbers = 0;
            while (iNodes.hasNext()) {
                MMObjectNode node = (MMObjectNode) iNodes.next();
                Integer resultNumber = node.getIntegerValue("number");
                iNodes.set(resultNumber);
            }
            
            // Must contain same numbers.
            assertTrue(nodes.containsAll(numbers));
            assertTrue(numbers.containsAll(nodes));
            
            nodes = instance.searchVectorIn(
                "WHERE name >= '" + value + "'", "number", toCSV(numbers));
            assertTrue(nodes.size() == 0);
        }
    }
  
    /** Test of searchVectorIn(String,String,boolean,String) method, of class org.mmbase.module.core.MMObjectBuilder. */
    public void testSearchVectorIn4() throws Exception {
        for (int i = 0; i < (NR_TEST_NODES + 1); i++) {
            // Numbers retrieved by query.
            String value = TEST_NAME + i;
            Vector numbers = instance.searchNumbers("WHERE name < '" + value + "'");
            // Sort ascending.
            Collections.sort(numbers);
            
            // Numbers retrieved by method.
            Iterator iNodes;
            iNodes = instance.searchVectorIn(
                "WHERE name < '" + value + "'", "number", true, toCSV(numbers))
                .iterator();
            int indexNumbers = 0;
            while (iNodes.hasNext()) {
                MMObjectNode node = (MMObjectNode) iNodes.next();
                Integer resultNumber = node.getIntegerValue("number");
                assertTrue(resultNumber.equals(numbers.get(indexNumbers++)));
            }
            
            iNodes = instance.searchVectorIn(
                "WHERE name < '" + value + "'", "number", false, toCSV(numbers))
                .iterator();
            indexNumbers = numbers.size() - 1;
            while (iNodes.hasNext()) {
                MMObjectNode node = (MMObjectNode) iNodes.next();
                Integer resultNumber = node.getIntegerValue("number");
                assertTrue(resultNumber.equals(numbers.get(indexNumbers--)));
            }
            
            List nodes = instance.searchVectorIn(
                "WHERE name >= '" + value + "'", "number", true, toCSV(numbers));
            assertTrue(nodes.size() == 0);
        }
    }
  
    /** Test of search(String) method, of class org.mmbase.module.core.MMObjectBuilder. */
    public void testSearch() throws Exception {
        List results = toList(instance.search(""));
        assertTrue(results.size() >= NR_TEST_NODES);
  
        for (int i = 0; i < (NR_TEST_NODES + 1); i++) {
            String value = TEST_NAME + i;
            results = toList(instance.search("WHERE name<'" + value 
                + "' AND name LIKE '" + TEST_NAME + "%'", "name", false));
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
  
    /** Test of addNodesToQuery(NodeSearchQuery,String) method, of class org.mmbase.module.core.MMObjectBuilder. */
    public void testAddNodesToQuery() throws Exception {
        NodeSearchQuery query = new NodeSearchQuery(instance);
        Step step = (Step) query.getSteps().get(0);
        SortedSet numbers = new TreeSet();
        for (int i = 0; i < 10; i++) {
            instance.addNodesToQuery(query, toCSV(numbers));
            SortedSet nodeNumbers = step.getNodes();
            assertTrue(nodeNumbers.equals(numbers));
            numbers.add(new Integer(i));
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
            Object value1 = node1.getValue(fieldName);
            Object value2 = node2.getValue(fieldName);
            if (!(value1 == null? value2 == null: value1.equals(value2))) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Creates comma-separated list of number values in collection.
     */
    private String toCSV(Collection values) {
        StringBuffer sb = new StringBuffer();
        Iterator iValues = values.iterator();
        while (iValues.hasNext()) {
            Number value = (Number) iValues.next();
            sb.append(value);
            if (iValues.hasNext()) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
    
    /**
     * Creates list based on enumeration.
     */
    private List toList(Enumeration e) {
        List result = new ArrayList();
        while (e.hasMoreElements()) {
            result.add(e.nextElement());
        }
        return result;
    }
}