package org.mmbase.storage.search;

import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.mock.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.storage.search.implementation.*;


public class ClusterQueriesTest {

    private final String NEWS = "news";
    private static int    RELATED;

    @BeforeClass
    public static void setup() throws java.io.IOException {
        MockCloudContext.getInstance().addCore();
        MockCloudContext.getInstance().addCoreModel();
        MockCloudContext.getInstance().addNodeManagers(MockBuilderReader.getBuilderLoader().getChildResourceLoader("mynews"));
        RELATED =  MockCloudContext.getInstance().getCloud("mmbase").getRelationManager("related").getNumber();
    }

    ClusterQueries instance = new BridgeClusterQueries(new QueryContext.Bridge(MockCloudContext.getInstance().getCloud("mmbase")));

    @Test
    public void getUniqueTableAlias() {
        List<String> originalAliases = Arrays.asList(new String[] {"test1", "test2"});
        Set<String> tableAliases = new HashSet<String>();
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

        // Alias containing white space.
        alias = instance.getUniqueTableAlias(
            "white space", tableAliases, originalAliases);
        assertTrue(alias, alias.equals("white space"));
        assertTrue(tableAliases.contains("white space"));
    }

    @Test
    public void getBuilder() {
        Map<String,Integer> nodes = new HashMap<String,Integer>();
        assertTrue(MockCloudContext.getInstance().getCloud("mmbase").hasNodeManager(NEWS));

        assertEquals(NEWS, instance.getBuilder("news", nodes));
        assertTrue(nodes.size() == 0);

        assertTrue(instance.getBuilder("news0", nodes).equals(NEWS));
        assertTrue(nodes.size() == 0);

        assertTrue(instance.getBuilder("news1", nodes).equals(NEWS));
        assertTrue(nodes.size() == 0);

        assertEquals("insrel", instance.getBuilder("related", nodes));
        assertTrue("Nodes is " + nodes + " should be size 1", nodes.size() == 1);
        assertEquals(Integer.valueOf(RELATED), nodes.get("related"));

        assertEquals("insrel", instance.getBuilder("related0", nodes));

        assertEquals(2, nodes.size());
        assertEquals(Integer.valueOf(RELATED), nodes.get("related0"));

        try {
            // Unknown builder or role, should throw IllegalArgumentException.
            instance.getBuilder("xxxx", nodes);
            fail("Unknown builder or role, "
                + "should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
    }


    @Test
    public void addFields() {
        BasicSearchQuery query = new BasicSearchQuery();
        Map<String,Integer> roles = new HashMap<String,Integer>();
        Map<String, BasicStepField> fieldsByName = new HashMap<String, BasicStepField>();
        List<String> tables = Arrays.asList(
            new String[] {"news", "related", "people", "news1"});
        Map<String, BasicStep> stepsByAlias = instance.addSteps(query, tables, roles, true, fieldsByName);
        assertTrue(query.getFields().size() == 4);
        assertTrue(fieldsByName.size() == 4);

        instance.addFields(query, "news.title", stepsByAlias, fieldsByName);

        StepField stepField = getField(query, "news", "title");
        assertTrue(stepField != null);
        assertTrue(stepField.equals(fieldsByName.get("news.title")));
        assertTrue(stepField.getAlias() == null);
        assertTrue(query.getFields().size() == 5);
        assertTrue(fieldsByName.size() == 5);

        instance.addFields(query, "related.number", stepsByAlias, fieldsByName);
        stepField = getField(query, "related", "number");
        assertTrue(stepField != null);
        assertTrue(stepField.equals(fieldsByName.get("related.number")));
        assertTrue(stepField.getAlias() == null);
        // Not added twice.
        assertTrue(query.getFields().size() == 5);
        assertTrue(fieldsByName.size() == 5);

        instance.addFields(query, "people.firstname", stepsByAlias, fieldsByName);
        stepField = getField(query, "people", "firstname");
        assertTrue(stepField != null);
        assertTrue(stepField.equals(fieldsByName.get("people.firstname")));
        assertTrue(stepField.getAlias() == null);
        assertTrue(query.getFields().size() == 6);
        assertTrue(fieldsByName.size() == 6);

        instance.addFields(query, "news1.title", stepsByAlias, fieldsByName);
        stepField = getField(query, "news1", "title");
        assertTrue(stepField != null);
        assertTrue(stepField.equals(fieldsByName.get("news1.title")));
        assertTrue(stepField.getAlias() == null);
        assertTrue(query.getFields().size() == 7);
        assertTrue(fieldsByName.size() == 7);

        try {
            // Invalid expression, should throw IllegalArgumentException.
            instance.addFields(query, "newsnumber", stepsByAlias, fieldsByName);
            fail("Invalid expression, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}

        try {
            // Invalid expression, should throw IllegalArgumentException.
            instance.addFields(query, ".newsnumber", stepsByAlias, fieldsByName);
            fail("Invalid expression, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}

        try {
            // Invalid expression, should throw IllegalArgumentException.
            instance.addFields(query, "newsnumber.", stepsByAlias, fieldsByName);
            fail("Invalid expression, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}

        try {
            // Unknown table alias, should throw IllegalArgumentException.
            instance.addFields(query, "xxx.number", stepsByAlias, fieldsByName);
            fail("Unknown table alias, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}

        try {
            // Unknown field name, should throw IllegalArgumentException.
            instance.addFields(query, "news.xxx", stepsByAlias, fieldsByName);
            fail("Unknown table alias, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}

        instance.addFields(query, "f1(news.body,news1.body)", stepsByAlias, fieldsByName);
        stepField = getField(query, "news", "body");
        assertTrue(stepField != null);
        assertTrue(stepField.equals(fieldsByName.get("news.body")));
        assertTrue(stepField.getAlias() == null);
        stepField = getField(query, "news1", "body");
        assertTrue(stepField != null);
        assertTrue(stepField.equals(fieldsByName.get("news1.body")));
        assertTrue(query.getFields().size() == 9);
        assertTrue(fieldsByName.size() == 9);
    }

    @Test
    public void isRelation() {
        assertTrue(instance.isRelation("insrel"));
        assertFalse(instance.isRelation("news"));
    }



    /** Test of addSteps() method, of class org.mmbase.module.core.ClusterBuilder. */
    @Test
    public void addSteps() {
        // --- requires role "related" to be defined: ---
        BasicSearchQuery query = new BasicSearchQuery();
        Map<String,Integer> roles = new HashMap<String,Integer>();
        Map<String, BasicStepField> fieldsByName = new HashMap<String, BasicStepField>();
        List<String> tables = Arrays.asList(new String[] {"news", "news1"});
        Map<String, BasicStep> stepsByAlias = instance.addSteps(query, tables, roles, true, fieldsByName);
        // Test steps and returned map.
        assertTrue(stepsByAlias.size() == 3);
        List<Step> steps = query.getSteps();
        assertTrue(steps.size() == 3);
        Step step0 = steps.get(0);
        assertTrue(step0.getTableName().equals("news"));
        assertTrue(step0.getAlias().equals("news"));
        assertTrue(stepsByAlias.get("news") == step0);
        Step step1 = steps.get(1);
        assertTrue(step1.getTableName().equals("insrel"));
        assertTrue(step1.getAlias().startsWith("insrel"));
        assertTrue(stepsByAlias.get("insrel") == step1);
        Step step2 = steps.get(2);
        assertTrue(step2.getTableName().equals("news"));
        assertTrue(step2.getAlias().equals("news1"));
        assertTrue(stepsByAlias.get("news1") == step2);
        // Test (number)fields and fields map.
        assertTrue(fieldsByName.toString(), fieldsByName.size() == 2);
        List<StepField> fields = query.getFields();
        assertTrue(fields.size() == 2);
        StepField field0 = fields.get(0);
        assertTrue(field0.getStep() == step0);
        assertTrue(field0.getFieldName().equals("number"));
        assertTrue(fieldsByName.get("news.number") == field0);
        StepField field1 = fields.get(1);
        assertTrue(field1.getStep() == step2);
        assertTrue(field1.getFieldName().equals("number"));
        assertTrue(fieldsByName.get("news1.number") == field1);
        // Test roles.
        assertTrue("Roles is " + roles + " not{}", roles.size() == 0);

        query = new BasicSearchQuery();
        roles.clear();
        fieldsByName.clear();
        tables = Arrays.asList(new String[] {"news", "news1"});
        stepsByAlias = instance.addSteps(query, tables, roles, false, fieldsByName);
        // Test steps and returned map.
        assertTrue(stepsByAlias.size() == 3);
        steps = query.getSteps();
        assertTrue(steps.size() == 3);
        step0 = steps.get(0);
        assertTrue(step0.getTableName().equals("news"));
        assertTrue(step0.getAlias().equals("news"));
        assertTrue(stepsByAlias.get("news") == step0);
        step1 = steps.get(1);
        assertTrue(step1.getTableName().equals("insrel"));
        assertTrue(step1.getAlias().startsWith("insrel"));
        assertTrue(stepsByAlias.get("insrel") == step1);
        step2 = steps.get(2);
        assertTrue(step2.getTableName().equals("news"));
        assertTrue(step2.getAlias().equals("news1"));
        assertTrue(stepsByAlias.get("news1") == step2);
        // Test (number)fields and fields map.
        assertTrue(fieldsByName.size() == 0);
        fields = query.getFields();
        assertTrue(fields.size() == 0);
        // Test roles.
        assertTrue(roles.size() == 0);

        query = new BasicSearchQuery();
        roles.clear();
        fieldsByName.clear();
        tables = Arrays.asList(new String[] {"news", "related", "news1"});
        stepsByAlias = instance.addSteps(query, tables, roles, true, fieldsByName);
        // Test steps and returned map.
        assertEquals("" + stepsByAlias, 3, stepsByAlias.size());
        steps = query.getSteps();
        assertEquals(3, steps.size());
        step0 = steps.get(0);
        assertTrue(step0.getTableName().equals("news"));
        assertTrue(step0.getAlias().equals("news"));
        assertTrue(stepsByAlias.get("news") == step0);
        step1 = steps.get(1);
        assertTrue(step1.getTableName().equals("insrel"));
        assertTrue(step1.getAlias().equals("related"));
        assertTrue(stepsByAlias.get("related") == step1);
        step2 = steps.get(2);
        assertTrue(step2.getTableName().equals("news"));
        assertTrue(step2.getAlias().equals("news1"));
        assertTrue(stepsByAlias.get("news1") == step2);

        // Test (number)fields and fields map.
        assertTrue(fieldsByName.size() == 3);
        fields = query.getFields();
        assertTrue(fields.size() == 3);
        field0 = fields.get(0);
        assertTrue(field0.getStep() == step0);
        assertTrue(field0.getFieldName().equals("number"));
        assertTrue(fieldsByName.get("news.number") == field0);
        field1 = fields.get(1);
        assertTrue(field1.getStep() == step1);
        assertTrue(field1.getFieldName().equals("number"));
        assertTrue(fieldsByName.get("related.number") == field1);
        StepField field2 = fields.get(2);
        assertTrue(field2.getStep() == step2);
        assertTrue(field2.getFieldName().equals("number"));
        assertTrue(fieldsByName.get("news1.number") == field2);
        // Test roles.
        assertTrue("Roles is " + roles + " wich has not size 1", roles.size() == 1);
        Integer number = roles.get("related");
        assertTrue("" + number.intValue() + " != " + RELATED, number.intValue() == RELATED);

        query = new BasicSearchQuery();
        roles.clear();
        fieldsByName.clear();
        tables = Arrays.asList(new String[] {"news", "related", "news1", "related", "news"});
        stepsByAlias = instance.addSteps(query, tables, roles, true, fieldsByName);
        // Test steps and returned map.
        assertTrue(stepsByAlias.size() == 5);
        steps = query.getSteps();
        assertTrue(steps.size() == 5);
        step0 = steps.get(0);
        assertTrue(step0.getTableName().equals("news"));
        assertTrue(step0.getAlias().equals("news"));
        assertTrue(stepsByAlias.get("news") == step0);
        step1 = steps.get(1);
        assertTrue(step1.getTableName().equals("insrel"));
        assertTrue(step1.getAlias().equals("related"));
        assertTrue(stepsByAlias.get("related") == step1);
        step2 = steps.get(2);
        assertTrue(step2.getTableName().equals("news"));
        assertTrue(step2.getAlias().equals("news1"));
        assertTrue(stepsByAlias.get("news1") == step2);
        Step step3 = steps.get(3);
        assertTrue(step3.getTableName().equals("insrel"));
        assertTrue(step3.getAlias().equals("related0"));
        assertTrue(stepsByAlias.get("related0") == step3);
        Step step4 = steps.get(4);
        assertTrue(step4.getTableName().equals("news"));
        assertTrue(step4.getAlias().equals("news0"));
        assertTrue(stepsByAlias.get("news0") == step4);
        // Test (number)fields and field map.
        assertTrue(fieldsByName.size() == 3);
        fields = query.getFields();
        assertTrue(fields.toString(), fields.size() == 3);
        for (int i = 0; i < 3; i++) {
            StepField field = fields.get(i);
            assertTrue(field.getStep() == steps.get(i));
            assertTrue(field.getFieldName().equals("number"));
            assertTrue(fieldsByName.get(tables.get(i) + ".number") == field);
        }
        // Test roles.
        assertTrue("Roles is " + roles + " which is not size 2. There are to roles, so related and related0 must be availab.e", roles.size() == 2);
        number = roles.get("related");
        assertTrue(number.intValue() == RELATED);
    }

    /**
     * Gets field in query.
     *
     * @param query The query.
     * @param stepAlias The alias of the step.
     * @param fieldName The name of the field.
     * @return The field if the field is present in the query, null otherwise.
     */
    private StepField getField(
            SearchQuery query, String stepAlias, String fieldName) {
        StepField result = null;
        Iterator<StepField> iFields = query.getFields().iterator();
        while (iFields.hasNext()) {
            StepField field = iFields.next();
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
