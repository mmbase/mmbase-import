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
 * @version $Revision: 1.2 $
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
        System.exit(0);
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
        TestSuite suite = new TestSuite(ClusterBuilderTest.class);
        
        return suite;
    }
    
    /** Test of testGetClusterNodes method, of class org.mmbase.module.core.ClusterBuilder. */
    public void testGetClusterNodes() throws Exception {
        BasicSearchQuery query = new BasicSearchQuery();
        BasicStep poolsStep = query.addStep(pools)
            .setAlias("pools1");
        FieldDefs poolsName = pools.getField("name");
        BasicStepField poolsNameField = query.addField(poolsStep, poolsName)
            .setAlias("a_name"); // should not affect result node fieldnames!
        BasicSortOrder sortOrder = query.addSortOrder(poolsNameField)
            .setDirection(SortOrder.ORDER_ASCENDING);
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
    
    /** Test of getUniqueTableAlias() method, of class org.mmbase.module.core.ClusterBuilder. */
    public void testGetUniqueTableAlias() {
        List originalAliases = Arrays.asList(new Object[] {"test1", "test2"});
        Set tableAliases = new HashSet();
        String alias = instance.getUniqueTableAlias("test", tableAliases, originalAliases);
        assertTrue(alias.equals("test"));
        assertTrue(tableAliases.size() == 1);
        assertTrue(tableAliases.contains("test"));
        assertTrue(originalAliases.size() == 2);
        
        alias = instance.getUniqueTableAlias("test", tableAliases, originalAliases);
        assertTrue(alias.equals("test0"));
        assertTrue(tableAliases.size() == 2);
        assertTrue(tableAliases.contains("test0"));
        assertTrue(originalAliases.size() == 2);
        
        alias = instance.getUniqueTableAlias(
            "test1", tableAliases, originalAliases);
        assertTrue(alias.equals("test1"));
        assertTrue(tableAliases.size() == 3);
        assertTrue(tableAliases.contains("test1"));
        assertTrue(originalAliases.size() == 2);
        
        alias = instance.getUniqueTableAlias(
            "test", tableAliases, originalAliases);
        assertTrue(alias.equals("test3"));
        assertTrue(tableAliases.size() == 4);
        assertTrue(tableAliases.contains("test1"));
        assertTrue(originalAliases.size() == 2);
        
        for (int i = 4; i < 10; i++) {
            alias = instance.getUniqueTableAlias(
                "test" + (i - 1), tableAliases, originalAliases);
            assertTrue(alias, alias.equals("test" + i));
            assertTrue(tableAliases.size() == i + 1);
            assertTrue(tableAliases.contains("test" + i));
            assertTrue(originalAliases.size() == 2);
        }
        
        try {
            // Can't generate another unique value for this string, 
            // should throw IndexOutOfBoundsException.
            instance.getUniqueTableAlias("test", tableAliases, originalAliases);
            fail("Can't generate another unique value for this string, "
                + "should throw IndexOutOfBoundsException.");
        } catch (IndexOutOfBoundsException e) {}
        
        assertTrue(tableAliases.size() == 10);
        for (int i = 0; i < 10; i++) {
            if (i == 2) {
                assertTrue(!tableAliases.contains("test" + i));
            } else {
                assertTrue(tableAliases.contains("test" + i));
            }
        }
        assertTrue(tableAliases.contains("test"));
    }
    
    /** Test of getBuilder() method, of class org.mmbase.module.core.ClusterBuilder. */
    public void testGetBuilder() {
        // --- requires role "related" to be defined: ---
        Integer related = new Integer(
            mmbase.getRelDef().getNumberByName("related"));
        assertTrue("Role 'related' must be defined to run this test.", 
            related.intValue() != -1);

        Map nodes = new HashMap();
        assertTrue(instance.getBuilder("pools", nodes).equals(pools));
        assertTrue(nodes.size() == 0);
        
        assertTrue(instance.getBuilder("pools0", nodes).equals(pools));
        assertTrue(nodes.size() == 0);
        
        assertTrue(instance.getBuilder("pools1", nodes).equals(pools));
        assertTrue(nodes.size() == 0);
        
        assertTrue(instance.getBuilder("related", nodes).
            equals(mmbase.getInsRel()));
        assertTrue(nodes.size() == 1);
        assertTrue(nodes.get("related").equals(related));
        
        assertTrue(instance.getBuilder("related0", nodes).
            equals(mmbase.getInsRel()));
        assertTrue(nodes.size() == 2);
        assertTrue(nodes.get("related0").equals(related));
        
        try {
            // Unknown builder or role, should throw IllegalArgumentException.
            instance.getBuilder("xxxx", nodes);
            fail("Unknown builder or role, "
                + "should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
    }
    
    /** Test of addSteps() method, of class org.mmbase.module.core.ClusterBuilder. */
    public void testAddSteps() {
        // --- requires role "related" to be defined: ---
        int related = mmbase.getRelDef().getNumberByName("related");
        assertTrue("Role 'related' must be defined to run this test.", 
            related != -1);
        
        BasicSearchQuery query = new BasicSearchQuery();
        Map roles = new HashMap();
        Map fieldsByName = new HashMap();
        List tables = Arrays.asList(new Object[] {"pools", "pools1"});
        Map stepsByAlias = instance.addSteps(query, tables, roles, true, fieldsByName);
        // Test steps and returned map.
        assertTrue(stepsByAlias.size() == 3);
        List steps = query.getSteps();
        assertTrue(steps.size() == 3);
        Step step0 = (Step) steps.get(0);
        assertTrue(step0.getTableName().equals("pools"));
        assertTrue(step0.getAlias().equals("pools"));
        assertTrue(stepsByAlias.get("pools") == step0);
        Step step1 = (RelationStep) steps.get(1);
        assertTrue(step1.getTableName().equals("insrel"));
        assertTrue(step1.getAlias().startsWith("insrel"));
        assertTrue(stepsByAlias.get("insrel") == step1);
        Step step2 = (Step) steps.get(2);
        assertTrue(step2.getTableName().equals("pools"));
        assertTrue(step2.getAlias().equals("pools1"));
        assertTrue(stepsByAlias.get("pools1") == step2);
        // Test (number)fields and fields map.
        assertTrue(fieldsByName.toString(), fieldsByName.size() == 2);
        List fields = query.getFields();
        assertTrue(fields.size() == 2);
        StepField field0 = (StepField) fields.get(0);
        assertTrue(field0.getStep() == step0);
        assertTrue(field0.getFieldName().equals("number"));
        assertTrue(fieldsByName.get("pools.number") == field0);
        StepField field1 = (StepField) fields.get(1);
        assertTrue(field1.getStep() == step2);
        assertTrue(field1.getFieldName().equals("number"));
        assertTrue(fieldsByName.get("pools1.number") == field1);
        // Test roles.
        assertTrue(roles.size() == 0);
        
        query = new BasicSearchQuery();
        roles.clear();
        fieldsByName.clear();
        tables = Arrays.asList(new Object[] {"pools", "pools1"});
        stepsByAlias = instance.addSteps(query, tables, roles, false, fieldsByName);
        // Test steps and returned map.
        assertTrue(stepsByAlias.size() == 3);
        steps = query.getSteps();
        assertTrue(steps.size() == 3);
        step0 = (Step) steps.get(0);
        assertTrue(step0.getTableName().equals("pools"));
        assertTrue(step0.getAlias().equals("pools"));
        assertTrue(stepsByAlias.get("pools") == step0);
        step1 = (RelationStep) steps.get(1);
        assertTrue(step1.getTableName().equals("insrel"));
        assertTrue(step1.getAlias().startsWith("insrel"));
        assertTrue(stepsByAlias.get("insrel") == step1);
        step2 = (Step) steps.get(2);
        assertTrue(step2.getTableName().equals("pools"));
        assertTrue(step2.getAlias().equals("pools1"));
        assertTrue(stepsByAlias.get("pools1") == step2);
        // Test (number)fields and fields map.
        assertTrue(fieldsByName.size() == 0);
        fields = query.getFields();
        assertTrue(fields.size() == 0);
        // Test roles.
        assertTrue(roles.size() == 0);
        
        query = new BasicSearchQuery();
        roles.clear();
        fieldsByName.clear();
        tables = Arrays.asList(new Object[] {"pools", "related", "pools1"});
        stepsByAlias = instance.addSteps(query, tables, roles, true, fieldsByName);
        // Test steps and returned map.
        assertTrue(stepsByAlias.size() == 3);
        steps = query.getSteps();
        assertTrue(steps.size() == 3);
        step0 = (Step) steps.get(0);
        assertTrue(step0.getTableName().equals("pools"));
        assertTrue(step0.getAlias().equals("pools"));
        assertTrue(stepsByAlias.get("pools") == step0);
        step1 = (RelationStep) steps.get(1);
        assertTrue(step1.getTableName().equals("insrel"));
        assertTrue(step1.getAlias().equals("related"));
        assertTrue(stepsByAlias.get("related") == step1);
        step2 = (Step) steps.get(2);
        assertTrue(step2.getTableName().equals("pools"));
        assertTrue(step2.getAlias().equals("pools1"));
        assertTrue(stepsByAlias.get("pools1") == step2);
        // Test (number)fields and fields map.
        assertTrue(fieldsByName.size() == 3);
        fields = query.getFields();
        assertTrue(fields.size() == 3);
        field0 = (StepField) fields.get(0);
        assertTrue(field0.getStep() == step0);
        assertTrue(field0.getFieldName().equals("number"));
        assertTrue(fieldsByName.get("pools.number") == field0);
        field1 = (StepField) fields.get(1);
        assertTrue(field1.getStep() == step1);
        assertTrue(field1.getFieldName().equals("number"));
        assertTrue(fieldsByName.get("related.number") == field1);
        StepField field2 = (StepField) fields.get(2);
        assertTrue(field2.getStep() == step2);
        assertTrue(field2.getFieldName().equals("number"));
        assertTrue(fieldsByName.get("pools1.number") == field2);
        // Test roles.
        assertTrue(roles.size() == 1);
        Integer number = (Integer) roles.get("related");
        assertTrue(number.intValue() == related);
        
        query = new BasicSearchQuery();
        roles.clear();
        fieldsByName.clear();
        tables = Arrays.asList(
            new Object[] {"pools", "related", "pools1", "related", "pools"});
        stepsByAlias 
            = instance.addSteps(query, tables, roles, true, fieldsByName);
        // Test steps and returned map.
        assertTrue(stepsByAlias.size() == 5);
        steps = query.getSteps();
        assertTrue(steps.size() == 5);
        step0 = (Step) steps.get(0);
        assertTrue(step0.getTableName().equals("pools"));
        assertTrue(step0.getAlias().equals("pools"));
        assertTrue(stepsByAlias.get("pools") == step0);
        step1 = (RelationStep) steps.get(1);
        assertTrue(step1.getTableName().equals("insrel"));
        assertTrue(step1.getAlias().equals("related"));
        assertTrue(stepsByAlias.get("related") == step1);
        step2 = (Step) steps.get(2);
        assertTrue(step2.getTableName().equals("pools"));
        assertTrue(step2.getAlias().equals("pools1"));
        assertTrue(stepsByAlias.get("pools1") == step2);
        Step step3 = (Step) steps.get(3);
        assertTrue(step3.getTableName().equals("insrel"));
        assertTrue(step3.getAlias().equals("related0"));
        assertTrue(stepsByAlias.get("related0") == step3);
        Step step4 = (Step) steps.get(4);
        assertTrue(step4.getTableName().equals("pools"));
        assertTrue(step4.getAlias().equals("pools0"));
        assertTrue(stepsByAlias.get("pools0") == step4);
        // Test (number)fields and field map.
        assertTrue(fieldsByName.size() == 3);
        fields = query.getFields();
        assertTrue(fields.toString(), fields.size() == 3);
        for (int i = 0; i < 3; i++) {
            StepField field = (StepField) fields.get(i);
            assertTrue(field.getStep() == steps.get(i));
            assertTrue(field.getFieldName().equals("number"));
            assertTrue(fieldsByName.get(tables.get(i) + ".number") == field);
        }
        // Test roles.
        assertTrue(roles.size() == 1);
        number = (Integer) roles.get("related");
        assertTrue(number.intValue() == related);
    }

    public void testGetSelectString() {
        System.out.println("testGetSelectString()");
        Vector tables 
            = new Vector(
                Arrays.asList(new String[] {"pools","insrel","images"}));

        Vector fields = new Vector();
        System.out.println(instance.getSelectString(tables, fields));

        fields.add("f3(pools.number)");
        System.out.println(instance.getSelectString(tables, fields));

        fields.add("f2(f3(pools.number), pools.number, insrel.number)");
        System.out.println(instance.getSelectString(tables, fields));

        fields.add("f1(f2(f3(pools.number), pools.number, insrel.number), "
                    + "pools.number,insrel.number, images.number)");
        System.out.println(instance.getSelectString(tables, fields));

        fields.add("pools.number");
        System.out.println(instance.getSelectString(tables, fields));

        fields.add("insrel.number");
        System.out.println(instance.getSelectString(tables, fields));

        fields.add("images.number");
        System.out.println(instance.getSelectString(tables, fields));
    }
    
    public void testAddFields() {
        BasicSearchQuery query = new BasicSearchQuery();
        Map roles = new HashMap();
        Map fieldsByName = new HashMap();
        List tables = Arrays.asList(
            new Object[] {"pools", "related", "images", "pools1"});
        Map stepsByAlias = instance.addSteps(query, tables, roles, true, fieldsByName);
        assertTrue(query.getFields().size() == 4);
        assertTrue(fieldsByName.size() == 4);

        instance.addFields(query, "pools.name", stepsByAlias, fieldsByName);
        
        StepField stepField = getField(query, "pools", "name");
        assertTrue(stepField != null);
        assertTrue(stepField.equals(fieldsByName.get("pools.name")));
        assertTrue(query.getFields().size() == 5);
        assertTrue(fieldsByName.size() == 5);

        instance.addFields(query, "related.number", stepsByAlias, fieldsByName);
        stepField = getField(query, "related", "number");
        assertTrue(stepField != null);
        assertTrue(stepField.equals(fieldsByName.get("related.number")));
        // Not added twice.
        assertTrue(query.getFields().size() == 5);
        assertTrue(fieldsByName.size() == 5);

        instance.addFields(query, "images.title", stepsByAlias, fieldsByName);
        stepField = getField(query, "images", "title");
        assertTrue(stepField != null);
        assertTrue(stepField.equals(fieldsByName.get("images.title")));
        assertTrue(query.getFields().size() == 6);
        assertTrue(fieldsByName.size() == 6);

        instance.addFields(query, "pools1.name", stepsByAlias, fieldsByName);
        stepField = getField(query, "pools1", "name");
        assertTrue(stepField != null);
        assertTrue(stepField.equals(fieldsByName.get("pools1.name")));
        assertTrue(query.getFields().size() == 7);
        assertTrue(fieldsByName.size() == 7);
        
        try {
            // Invalid expression, should throw IllegalArgumentException.
            instance.addFields(query, "poolsnumber", stepsByAlias, fieldsByName);
            fail("Invalid expression, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
        
        try {
            // Invalid expression, should throw IllegalArgumentException.
            instance.addFields(query, ".poolsnumber", stepsByAlias, fieldsByName);
            fail("Invalid expression, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
        
        try {
            // Invalid expression, should throw IllegalArgumentException.
            instance.addFields(query, "poolsnumber.", stepsByAlias, fieldsByName);
            fail("Invalid expression, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
        
        try {
            // Unknown table alias, should throw IllegalArgumentException.
            instance.addFields(query, "pols.number", stepsByAlias, fieldsByName);
            fail("Unknown table alias, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
        
        try {
            // Unknown field name, should throw IllegalArgumentException.
            instance.addFields(query, "pools.nmber", stepsByAlias, fieldsByName);
            fail("Unknown table alias, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
        
        instance.addFields(query, "f1(pools.description,pools1.description)", stepsByAlias, fieldsByName);
        stepField = getField(query, "pools", "description");
        assertTrue(stepField != null);
        assertTrue(stepField.equals(fieldsByName.get("pools.description")));
        stepField = getField(query, "pools1", "description");
        assertTrue(stepField != null);
        assertTrue(stepField.equals(fieldsByName.get("pools1.description")));
        assertTrue(query.getFields().size() == 9);
        assertTrue(fieldsByName.size() == 9);
    }
    
    public void testAddSortOrders() {
        fail("n.i.y");
    }
    
    /**
     * Gets field in query.
     *
     * @param query The query.
     * @param stepAlias The alias of the step.
     * @param fieldName The name of the field.
     * @return The field if the field is present in the query, null otherwise.
     */
    private BasicStepField getField(
            SearchQuery query, String stepAlias, String fieldName) {
        BasicStepField result = null;
        Iterator iFields = query.getFields().iterator();
        while (iFields.hasNext()) {
            BasicStepField field = (BasicStepField) iFields.next();
            if (field.getStep().getAlias().equals(stepAlias)
            && field.getFieldName().equals(fieldName)) {
                // Found.
                result = field;
                break;
            }
        }
        return result;
    }

}
