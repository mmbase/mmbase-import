package org.mmbase.storage.search.legacy;

import junit.framework.*;
import java.util.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;

/**
 * JUnit tests.
 *
 * @author Rob van Maris
 * @version $Revision: 1.7 $
 */
public class ConstraintParserTest extends TestCase {

    /** Test instance. */
    private ConstraintParser instance = null;

    /** Test constraint. */
    private BasicSearchQuery query = null;

    private MMBase mmbase = null;
    private MMObjectBuilder images = null;
    private InsRel insrel = null;
    private MMObjectBuilder news = null;

    public ConstraintParserTest(java.lang.String testName) {
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
        images = mmbase.getBuilder("images");
        insrel = mmbase.getInsRel();
        news = mmbase.getBuilder("news");

        query = new BasicSearchQuery();
        instance = new ConstraintParser(query);
    }

    /**
     * Tears down after each test.
     */
    public void tearDown() throws Exception {}

    /** Test of parseValue method, of class org.mmbase.storage.search.legacy.ConstraintParser. */
    public void testParseValue() {
        query.addStep(news);
        StepField numericalField = instance.getField("number");
        StepField stringField = instance.getField("title");
        Iterator<String> iTokens = Arrays.asList(new String[] {
            "12345.6",
            "NotANumber",
            "'", "12345.6", "'",
            "'", "NotANumber", "'",
            "'", "value1", "'",
            "-10",
            "'", "value2", "'",
            "'", "value3", "123"}).iterator();
        Object value = ConstraintParser.parseValue(iTokens, numericalField);
        assertTrue(value.toString(), value.equals(new Double("12345.6")));
        try {
            // Invalid value for numerical field, must throw NumberFormatException.
            value = ConstraintParser.parseValue(iTokens, numericalField);
            fail("Invalid value for numerical field, must throw NumberFormatException.");
        } catch (NumberFormatException e) {}
        value = ConstraintParser.parseValue(iTokens, numericalField);
        assertTrue(value.toString(), value.equals(new Double("12345.6")));
        try {
            // Invalid value for numerical field, must throw NumberFormatException.
            value = ConstraintParser.parseValue(iTokens, numericalField);
            fail("Invalid value for numerical field, must throw NumberFormatException.");
        } catch (NumberFormatException e) {}
        value = ConstraintParser.parseValue(iTokens, stringField);
        assertTrue(value.toString(), value.equals("value1"));
        value = ConstraintParser.parseValue(iTokens, numericalField);
        assertTrue(value.toString(), value.equals(new Double("-10")));
        value = ConstraintParser.parseValue(iTokens, stringField);
        assertTrue(value.toString(), value.equals("value2"));
        try {
            // Missing end delimiter, must throw IllegalArgumentException.
            ConstraintParser.parseValue(iTokens, stringField);
            fail("Missing end delimiter, must throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
    }

    /** Test of tokenize method, of class org.mmbase.storage.search.legacy.ConstraintParser. */
    public void testTokenize() {
        List<String> tokens = ConstraintParser.tokenize("qwe '' and '123''456' or \"\"\"789\"");
        assertTrue(tokens.toString(), tokens.equals(
            Arrays.asList(new String[] {
                "qwe", "'", "", "'", "and", "'", "123'456", "'", "or", "'",
                "\"789", "'"})));

        tokens = ConstraintParser.tokenize("'''' and \"\"\"\" and '\"\"' and \"''\"");
        assertTrue(tokens.toString(), tokens.equals(
            Arrays.asList(new String[] {
                "'", "'", "'", "and", "'", "\"", "'", "and", "'", "\"\"", "'",
                "and", "'", "''", "'"})));
    }


    /** Test of getField(String) method, of class org.mmbase.storage.search.legacy.ConstraintParser. */
    public void testGetField() {
        Step step1 = query.addStep(images).setAlias("step1");
        StepField field = instance.getField("number");
        assertTrue(field.toString(), field.getStep() == step1);
        assertTrue(field.toString(), field.getFieldName().equals("number"));
        assertTrue(field.toString(), field.getAlias() == null);

        field = instance.getField("step1.number");
        assertTrue(field.toString(), field.getStep() == step1);
        assertTrue(field.toString(), field.getFieldName().equals("number"));
        assertTrue(field.toString(), field.getAlias() == null);

        try {
            // Field does not exist, should throw IllegalArgumentException.
            instance.getField("step1.abcdef");
            fail("Field does not exist, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}

        RelationStep step2 = query.addRelationStep(insrel, news);
        Step step3 = step2.getNext();
        try {
            // Field not prefixed, should throw IllegalArgumentException.
            instance.getField("number");
            fail("Field not prefixed, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}

        field = instance.getField("step1.title");
        assertTrue(field.toString(), field.getStep() == step1);
        assertTrue(field.toString(), field.getFieldName().equals("title"));
        assertTrue(field.toString(), field.getAlias() == null);

        field = instance.getField("news.title");
        assertTrue(field.toString(), field.getStep() == step3);
        assertTrue(field.toString(), field.getFieldName().equals("title"));
        assertTrue(field.toString(), field.getAlias() == null);
    }

    /** Test of getField(String, List) method, of class org.mmbase.storage.search.legacy.ConstraintParser. */
    public void testGetField2() {
        List<Step> steps = query.getSteps();
        Step step1 = query.addStep(images).setAlias("step1");
        StepField field = ConstraintParser.getField("number", steps);
        assertTrue(field.toString(), field.getStep() == step1);
        assertTrue(field.toString(), field.getFieldName().equals("number"));
        assertTrue(field.toString(), field.getAlias() == null);

        field = ConstraintParser.getField("step1.number", steps);
        assertTrue(field.toString(), field.getStep() == step1);
        assertTrue(field.toString(), field.getFieldName().equals("number"));
        assertTrue(field.toString(), field.getAlias() == null);

        try {
            // Field does not exist, should throw IllegalArgumentException.
            ConstraintParser.getField("step1.abcdef", steps);
            fail("Field does not exist, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}

        RelationStep step2 = query.addRelationStep(insrel, news);
        Step step3 = step2.getNext();
        try {
            // Field not prefixed, should throw IllegalArgumentException.
            ConstraintParser.getField("number", steps);
            fail("Field not prefixed, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}

        field = ConstraintParser.getField("step1.title", steps);
        assertTrue(field.toString(), field.getStep() == step1);
        assertTrue(field.toString(), field.getFieldName().equals("title"));
        assertTrue(field.toString(), field.getAlias() == null);

        field = ConstraintParser.getField("news.title", steps);
        assertTrue(field.toString(), field.getStep() == step3);
        assertTrue(field.toString(), field.getFieldName().equals("title"));
        assertTrue(field.toString(), field.getAlias() == null);
    }

    /** Test of parseSimpleCondition method, of class org.mmbase.storage.search.legacy.ConstraintParser. */
    public void testParseSimpleCondition() {
        query.addStep(images).setAlias("step1");
        RelationStep step2 = query.addRelationStep(insrel, news);
        step2.getNext();
        StepField field1 = instance.getField("step1.title");
        StepField field2 = instance.getField("step1.description");
        StepField field3 = instance.getField("step1.number");

        // LIKE
        BasicFieldValueConstraint constraint1
            = (BasicFieldValueConstraint)
                new BasicFieldValueConstraint(field1, "%abc def%")
                    .setOperator(FieldCompareConstraint.LIKE);
        Constraint constraint = instance.parseSimpleCondition(
            ConstraintParser.tokenize("step1.title like '%abc def%'").listIterator());
        assertTrue(constraint.toString(), constraint.equals(constraint1));

        // LIKE case insensitive with LOWER
        constraint1.setCaseSensitive(false);
        constraint = instance.parseSimpleCondition(
            ConstraintParser.tokenize("lower(step1.title) like '%abc def%'").listIterator());
        assertTrue(constraint.toString(), constraint.equals(constraint1));

        // LIKE case sensitive with LOWER
        constraint1.setValue("%ABC DEF%").setCaseSensitive(true);
        constraint = instance.parseSimpleCondition(
            ConstraintParser.tokenize("lower(step1.title) like '%ABC DEF%'").listIterator());
        assertTrue(constraint.toString(), constraint.equals(constraint1));

        // LIKE case insensitive with UPPER
        constraint1.setCaseSensitive(false);
        constraint = instance.parseSimpleCondition(
            ConstraintParser.tokenize("UPPER(step1.title) like '%ABC DEF%'").listIterator());
        assertTrue(constraint.toString(), constraint.equals(constraint1));

        // LIKE case sensitive with UPPER
        constraint1.setValue("%abc def%").setCaseSensitive(true);
        constraint = instance.parseSimpleCondition(
            ConstraintParser.tokenize("UPPER(step1.title) like '%abc def%'").listIterator());
        assertTrue(constraint.toString(), constraint.equals(constraint1));

        try {
            // LIKE applied to numerical field, should throw
            // IllegalArgumentException.
            instance.parseSimpleCondition(
                ConstraintParser.tokenize("step1.number like '%abc def%'")
                    .listIterator());
            fail("LIKE applied to numerical field, should throw "
                + "IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}

        // NOT LIKE
        constraint1.setInverse(true);
        constraint = instance.parseSimpleCondition(
            ConstraintParser.tokenize("step1.title not like '%abc def%'")
                .listIterator());
        assertTrue(constraint.toString(), constraint.equals(constraint1));

        // IS NULL
        BasicFieldNullConstraint constraint2
            = new BasicFieldNullConstraint(field1);
        constraint = instance.parseSimpleCondition(
            ConstraintParser.tokenize("step1.title is null").listIterator());
        assertTrue(constraint.toString(), constraint.equals(constraint2));

        // IS NOT NULL
        constraint2.setInverse(true);
        constraint = instance.parseSimpleCondition(
            ConstraintParser.tokenize("step1.title is not null").listIterator());
        assertTrue(constraint.toString(), constraint.equals(constraint2));

        // = value
        BasicFieldValueConstraint constraint3
            = (BasicFieldValueConstraint)
                new BasicFieldValueConstraint(field1, "abc def")
                    .setOperator(FieldCompareConstraint.EQUAL);
        constraint = instance.parseSimpleCondition(
            ConstraintParser.tokenize("step1.title = 'abc def'").listIterator());
        assertTrue(constraint.toString(), constraint.equals(constraint3));

        // == value
        constraint = instance.parseSimpleCondition(
            ConstraintParser.tokenize("step1.title == 'abc def'").listIterator());
        assertTrue(constraint.toString(), constraint.equals(constraint3));

        // = value case insensitive with LOWER
        constraint3.setCaseSensitive(false);
        constraint = instance.parseSimpleCondition(
            ConstraintParser.tokenize("LOWER(step1.title) = 'abc def'").listIterator());
        assertTrue(constraint.toString(), constraint.equals(constraint3));

        // = value case sensitive with LOWER
        constraint3.setValue("ABC DEF").setCaseSensitive(true);
        constraint = instance.parseSimpleCondition(
            ConstraintParser.tokenize("LOWER(step1.title) = 'ABC DEF'").listIterator());
        assertTrue(constraint.toString(), constraint.equals(constraint3));

        // = value case insensitive with UPPER
        constraint3.setCaseSensitive(false);
        constraint = instance.parseSimpleCondition(
            ConstraintParser.tokenize("upper(step1.title) = 'ABC DEF'").listIterator());
        assertTrue(constraint.toString(), constraint.equals(constraint3));

        // = value case sensitive with UPPER
        constraint3.setValue("abc def").setCaseSensitive(true);
        constraint = instance.parseSimpleCondition(
            ConstraintParser.tokenize("upper(step1.title) = 'abc def'").listIterator());
        assertTrue(constraint.toString(), constraint.equals(constraint3));

        // = field2
        BasicCompareFieldsConstraint constraint3a
            = (BasicCompareFieldsConstraint)
                new BasicCompareFieldsConstraint(field1, field2)
                    .setOperator(FieldCompareConstraint.EQUAL);
        constraint = instance.parseSimpleCondition(
            ConstraintParser.tokenize("step1.title = step1.description").listIterator());
        assertTrue(constraint.toString(), constraint.equals(constraint3a));

        // == field2
        constraint = instance.parseSimpleCondition(
            ConstraintParser.tokenize("step1.title == step1.description").listIterator());
        assertTrue(constraint.toString(), constraint.equals(constraint3a));

        // != value
        BasicFieldValueConstraint constraint4
            = (BasicFieldValueConstraint)
                new BasicFieldValueConstraint(field1, "abc def")
                    .setOperator(FieldCompareConstraint.NOT_EQUAL);
        constraint = instance.parseSimpleCondition(
            ConstraintParser.tokenize("step1.title != 'abc def'").listIterator());
        assertTrue(constraint.toString(), constraint.equals(constraint4));

        // <> value
        constraint = instance.parseSimpleCondition(
            ConstraintParser.tokenize("step1.title <> 'abc def'").listIterator());
        assertTrue(constraint.toString(), constraint.equals(constraint4));

        // != field2
        BasicCompareFieldsConstraint constraint4a
            = (BasicCompareFieldsConstraint)
                new BasicCompareFieldsConstraint(field1, field2)
                    .setOperator(FieldCompareConstraint.NOT_EQUAL);
        constraint = instance.parseSimpleCondition(
            ConstraintParser.tokenize("step1.title != step1.description").listIterator());
        assertTrue(constraint.toString(), constraint.equals(constraint4a));

        // <> field2
        constraint = instance.parseSimpleCondition(
            ConstraintParser.tokenize("step1.title <> step1.description").listIterator());
        assertTrue(constraint.toString(), constraint.equals(constraint4a));

        // > value
        BasicFieldValueConstraint constraint5
            = (BasicFieldValueConstraint)
                new BasicFieldValueConstraint(field1, "abc def")
                    .setOperator(FieldCompareConstraint.GREATER);
        constraint = instance.parseSimpleCondition(
            ConstraintParser.tokenize("step1.title>'abc def'").listIterator());
        assertTrue(constraint.toString(), constraint.equals(constraint5));

        // > field2
        BasicCompareFieldsConstraint constraint5a
            = (BasicCompareFieldsConstraint)
                new BasicCompareFieldsConstraint(field1, field2)
                    .setOperator(FieldCompareConstraint.GREATER);
        constraint = instance.parseSimpleCondition(
            ConstraintParser.tokenize("step1.title>step1.description").listIterator());
        assertTrue(constraint.toString(), constraint.equals(constraint5a));

        // >= value
        BasicFieldValueConstraint constraint6
            = (BasicFieldValueConstraint)
                new BasicFieldValueConstraint(field1, "abc def")
                    .setOperator(FieldCompareConstraint.GREATER_EQUAL);
        constraint = instance.parseSimpleCondition(
            ConstraintParser.tokenize("step1.title>='abc def'").listIterator());
        assertTrue(constraint.toString(), constraint.equals(constraint6));

        // >= field2
        BasicCompareFieldsConstraint constraint6a
            = (BasicCompareFieldsConstraint)
                new BasicCompareFieldsConstraint(field1, field2)
                    .setOperator(FieldCompareConstraint.GREATER_EQUAL);
        constraint = instance.parseSimpleCondition(
            ConstraintParser.tokenize("step1.title>=step1.description").listIterator());
        assertTrue(constraint.toString(), constraint.equals(constraint6a));

        // < value
        BasicFieldValueConstraint constraint7
            = (BasicFieldValueConstraint)
                new BasicFieldValueConstraint(field1, "abc def")
                    .setOperator(FieldCompareConstraint.LESS);
        constraint = instance.parseSimpleCondition(
            ConstraintParser.tokenize("step1.title<'abc def'").listIterator());
        assertTrue(constraint.toString(), constraint.equals(constraint7));

        // < field2
        BasicCompareFieldsConstraint constraint7a
            = (BasicCompareFieldsConstraint)
                new BasicCompareFieldsConstraint(field1, field2)
                    .setOperator(FieldCompareConstraint.LESS);
        constraint = instance.parseSimpleCondition(
            ConstraintParser.tokenize("step1.title<step1.description").listIterator());
        assertTrue(constraint.toString(), constraint.equals(constraint7a));

        // <= value
        BasicFieldValueConstraint constraint8
            = (BasicFieldValueConstraint)
                new BasicFieldValueConstraint(field1, "abc def")
                    .setOperator(FieldCompareConstraint.LESS_EQUAL);
        constraint = instance.parseSimpleCondition(
            ConstraintParser.tokenize("step1.title<='abc def'").listIterator());
        assertTrue(constraint.toString(), constraint.equals(constraint8));

        // <= field2
        BasicCompareFieldsConstraint constraint8a
            = (BasicCompareFieldsConstraint)
                new BasicCompareFieldsConstraint(field1, field2)
                    .setOperator(FieldCompareConstraint.LESS_EQUAL);
        constraint = instance.parseSimpleCondition(
            ConstraintParser.tokenize("step1.title<=step1.description").listIterator());
        assertTrue(constraint.toString(), constraint.equals(constraint8a));

        // Comparing numerical field with string representing a numerical value:
        // step1.number <= '123'
        BasicFieldValueConstraint constraint8b
            = (BasicFieldValueConstraint)
                new BasicFieldValueConstraint(field3, new Double("123"))
                    .setOperator(FieldCompareConstraint.LESS_EQUAL);
        constraint = instance.parseSimpleCondition(
            ConstraintParser.tokenize("step1.number<='123'").listIterator());
        assertTrue(constraint.toString(), constraint.equals(constraint8b));

        try {
            // Comparing numerical field with string (not representing a numerical value),
            // should throw IllegalArgumentException.
            instance.parseSimpleCondition(
                ConstraintParser.tokenize("step1.number<='abc def'").listIterator());
            fail("Comparing numerical field with string, should throw "
                + "IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}

        try {
            // Comparing string field with number, should throw
            // IllegalArgumentException.
            instance.parseSimpleCondition(
                ConstraintParser.tokenize("step1.title<=123").listIterator());
            fail("Comparing string field with number, should throw "
                + "IllegalArgumentException");
        } catch (IllegalArgumentException e) {}

        // IN ()
        BasicFieldValueInConstraint constraint9
            = new BasicFieldValueInConstraint(field1);
        constraint = instance.parseSimpleCondition(
            ConstraintParser.tokenize("step1.title IN ()").listIterator());
        assertTrue(constraint.toString(), constraint.equals(constraint9));

        // IN ('abc def')
        constraint9.addValue("abc def");
        constraint = instance.parseSimpleCondition(
            ConstraintParser.tokenize("step1.title IN ('abc def')").listIterator());
        assertTrue(constraint.toString(), constraint.equals(constraint9));

        // IN ('abc def', "hijk lm")
        constraint9.addValue("hijk lm");
        constraint = instance.parseSimpleCondition(
            ConstraintParser.tokenize("step1.title IN ('abc def', \"hijk lm\")")
                .listIterator());
        assertTrue(constraint.toString(), constraint.equals(constraint9));

        // NOT IN ('abc def', "hijk lm")
        constraint9.setInverse(true);
        constraint = instance.parseSimpleCondition(
            ConstraintParser.tokenize("step1.title NOT IN ('abc def', \"hijk lm\")")
                .listIterator());
        assertTrue(constraint.toString(), constraint.equals(constraint9));
    }

    /** Test of parseCondition method, of class org.mmbase.storage.search.legacy.ConstraintParser. */
    public void testParseCondition() {
        query.addStep(images).setAlias("step1");
        RelationStep step2 = query.addRelationStep(insrel, news);
        step2.getNext();
        StepField field1 = instance.getField("step1.title");
        StepField field2 = instance.getField("news.number");

        // Empty constraint
        Constraint constraint = instance.parseCondition(
            ConstraintParser.tokenize("").listIterator());
        assertTrue(constraint == null);

        // Simple constraint
        BasicFieldValueConstraint constraint1 =
            new BasicFieldValueConstraint(field1, "abc def");
        constraint = instance.parseCondition(
            ConstraintParser.tokenize("step1.title = 'abc def'").listIterator());
        assertTrue(constraint.toString(), constraint.equals(constraint1));

        // simple constraint with NOT
        constraint1.setInverse(true);
        constraint = instance.parseCondition(
            ConstraintParser.tokenize("NOT step1.title = 'abc def'").listIterator());
        assertTrue(constraint.toString(), constraint.equals(constraint1));

        // Parenthesis with NOT
        constraint = instance.parseCondition(
            ConstraintParser.tokenize("NOT (step1.title = 'abc def')").listIterator());
        assertTrue(constraint.toString(), constraint.equals(constraint1));

        // Parenthesis without NOT
        constraint1.setInverse(false);
        constraint = instance.parseCondition(
            ConstraintParser.tokenize("(step1.title = 'abc def')").listIterator());
        assertTrue(constraint.toString(), constraint.equals(constraint1));

        // Composite with AND
        BasicFieldCompareConstraint constraint2
            = new BasicFieldValueConstraint(field2, new Integer(123))
                .setOperator(FieldCompareConstraint.GREATER);
        BasicCompositeConstraint constraint3
            = new BasicCompositeConstraint(CompositeConstraint.LOGICAL_AND)
                .addChild(constraint1)
                .addChild(constraint2);
        constraint = instance.parseCondition(
            ConstraintParser.tokenize(
                "step1.title = 'abc def' AND news.number > 123")
                    .listIterator());
        assertTrue(constraint.toString(), constraint.equals(constraint3));

        // Composite with OR
        constraint3
            = new BasicCompositeConstraint(CompositeConstraint.LOGICAL_OR)
                .addChild(constraint1)
                .addChild(constraint2);
        constraint = instance.parseCondition(
            ConstraintParser.tokenize(
                "step1.title = 'abc def' OR news.number > 123")
                    .listIterator());
        assertTrue(constraint.toString(), constraint.equals(constraint3));

        // Composite with AND and OR mixed
        BasicFieldCompareConstraint constraint4 =
            new BasicFieldValueConstraint(field2, new Integer(200))
                .setOperator(FieldCompareConstraint.LESS);
        BasicCompositeConstraint constraint5 =
            new BasicCompositeConstraint(CompositeConstraint.LOGICAL_AND)
                .addChild(constraint3)
                .addChild(constraint4);
        constraint = instance.parseCondition(
            ConstraintParser.tokenize(
                "step1.title = 'abc def' OR news.number > 123"
                    + " AND news.number < 200")
                    .listIterator());
        assertTrue(constraint.toString(), constraint.equals(constraint5));
    }

    /** Test of toConstraint method, of class org.mmbase.storage.search.legacy.ConstraintParser. */
    public void testToConstraint() {
        query.addStep(images).setAlias("step1");
        RelationStep step2 = query.addRelationStep(insrel, news);
        step2.getNext();
        StepField field1 = instance.getField("step1.title");
        instance.getField("news.number");

        // Empty constraint
        Constraint constraint = instance.toConstraint("");
        assertTrue(constraint == null);

        // Simple constraint
        BasicFieldValueConstraint constraint1 =
            new BasicFieldValueConstraint(field1, "abc def");
        constraint = instance.toConstraint("step1.title = 'abc def'");
        assertTrue(constraint.toString(), constraint.equals(constraint1));

        // NOT()
        String con = "NOT(step1.title='hoi')";
        constraint = instance.toConstraint(con);
        assertTrue(constraint instanceof BasicFieldValueConstraint);
        assertTrue(constraint.isInverse());
    }

    public void testUnsupported() {
        // Legacy constraint
        BasicLegacyConstraint constraint2 =
            new BasicLegacyConstraint("abc DEF ghi");
        Constraint constraint = instance.toConstraint("abc DEF ghi");
        assertTrue(constraint.toString(), constraint.equals(constraint2));

        // Legacy constraint - format "WHERE ...." not supported
        BasicLegacyConstraint constraint3 =
            new BasicLegacyConstraint("WHERE step1.title = 'abc def'");
        constraint = instance.toConstraint("WHERE step1.title = 'abc def'");
        assertTrue(constraint.toString(), constraint.equals(constraint3));


    }

    public static Test suite() {
        TestSuite suite = new TestSuite(ConstraintParserTest.class);

        return suite;
    }

}
