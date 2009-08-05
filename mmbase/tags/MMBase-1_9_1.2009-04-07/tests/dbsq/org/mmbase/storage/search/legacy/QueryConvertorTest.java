package org.mmbase.storage.search.legacy;

import junit.framework.*;
import java.util.*;

import org.mmbase.core.CoreField;
import org.mmbase.module.core.*;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;

/**
 * JUnit tests.
 *
 * @author Rob van Maris
 * @version $Revision: 1.1 $
 */
public class QueryConvertorTest extends TestCase {

    private final static String IMAGES = "images";

    private MMBase mmbase = null;
    private MMObjectBuilder images = null;
    private CoreField imagesNumber = null;
    private CoreField imagesTitle = null;
    private ClusterBuilder clusterBuilder = null;

    public QueryConvertorTest(java.lang.String testName) {
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
        images = mmbase.getBuilder(IMAGES);
        imagesNumber = images.getField("number");
        imagesTitle = images.getField("title");
        clusterBuilder = mmbase.getClusterBuilder();
    }

    /**
     * Tears down after each test.
     */
    public void tearDown() throws Exception {}

    /** Test of setConstraint() method, of class org.mmbase.util.QueryConvertor. */
    public void testSetConstraint() {
        List<String> tables = Arrays.asList(
            new String[] {"news", "related", "images", "insrel", "news0"});
        List<String> empty = new ArrayList<String>(0);
        BasicSearchQuery query = clusterBuilder.getMultiLevelSearchQuery(
            empty, empty, null, tables, null,
            empty, empty, RelationStep.DIRECTIONS_ALL);

        // Altavista format.
        altaVistaSearchQueryTests();
        QueryConvertor.setConstraint(query,
            "news.title=E'test1'+related.number=E10+images.number=E10+news0.title=E'test2'");
        CompositeConstraint composite
            = (CompositeConstraint) query.getConstraint();
        assertTrue(composite.getLogicalOperator()
            == CompositeConstraint.LOGICAL_AND);
        List<Constraint> constraints = composite.getChilds();
        assertTrue(constraints.size() == 4);
        FieldValueConstraint constraint0
            = (FieldValueConstraint) constraints.get(0);
        assertTrue(constraint0.toString(),
            constraint0.getField().getStep().getTableName().equals("news"));
        assertTrue(constraint0.toString(),
            constraint0.getField().getStep().getAlias().equals("news"));
        assertTrue(constraint0.toString(),
            constraint0.getField().getFieldName().equals("title"));
        assertTrue(constraint0.getField().toString(),
            constraint0.getField().getAlias() == null);
        assertTrue(constraint0.toString(),
            constraint0.getOperator() == FieldCompareConstraint.LIKE);
        assertTrue(constraint0.toString(),
            !constraint0.isCaseSensitive());
        assertTrue(constraint0.toString(),
            constraint0.getValue().equals("test1"));
        FieldValueConstraint constraint1
            = (FieldValueConstraint) constraints.get(1);
        assertTrue(constraint1.toString(),
            constraint1.getField().getStep().getTableName().equals("insrel"));
        assertTrue(constraint1.toString(),
            constraint1.getField().getStep().getAlias().equals("related"));
        assertTrue(constraint1.toString(),
            constraint1.getField().getFieldName().equals("number"));
        assertTrue(constraint1.getField().toString(),
            constraint1.getField().getAlias() == null);
        assertTrue(constraint1.toString(),
            constraint1.getOperator() == FieldCompareConstraint.EQUAL);
        assertTrue(constraint1.toString(),
            constraint1.getValue().equals(new Double(10)));
        FieldValueConstraint constraint2
            = (FieldValueConstraint) constraints.get(2);
        assertTrue(constraint2.toString(),
            constraint2.getField().getStep().getTableName().equals("images"));
        assertTrue(constraint2.toString(),
            constraint2.getField().getStep().getAlias().equals("images"));
        assertTrue(constraint2.toString(),
            constraint2.getField().getFieldName().equals("number"));
        assertTrue(constraint2.getField().toString(),
            constraint2.getField().getAlias() == null);
        assertTrue(constraint2.toString(),
            constraint2.getOperator() == FieldCompareConstraint.EQUAL);
        assertTrue(constraint2.toString(),
            constraint2.getValue().equals(new Double(10)));
        FieldValueConstraint constraint3
            = (FieldValueConstraint) constraints.get(3);
        assertTrue(constraint3.toString(),
            constraint3.getField().getStep().getTableName().equals("news"));
        assertTrue(constraint3.toString(),
            constraint3.getField().getStep().getAlias().equals("news0"));
        assertTrue(constraint3.toString(),
            constraint3.getField().getFieldName().equals("title"));
        assertTrue(constraint3.getField().toString(),
            constraint3.getField().getAlias() == null);
        assertTrue(constraint0.getOperator() == FieldCompareConstraint.LIKE);
        assertTrue(constraint3.toString(),
            !constraint3.isCaseSensitive());
        assertTrue(constraint3.toString(),
            constraint3.getValue().equals("test2"));

        // Where string.
        QueryConvertor.setConstraint(query,
            "WHERE LOWER(news.title) LIKE 'test1' AND related.number=10"
                + " AND images.number=10 AND LOWER(news0.title) LIKE 'test2'");
        composite = (CompositeConstraint) query.getConstraint();
        assertTrue(composite.getLogicalOperator()
            == CompositeConstraint.LOGICAL_AND);
        constraints = composite.getChilds();
        assertTrue(constraints.size() == 4);
        constraint0 = (FieldValueConstraint) constraints.get(0);
        assertTrue(constraint0.toString(),
            constraint0.getField().getStep().getTableName().equals("news"));
        assertTrue(constraint0.toString(),
            constraint0.getField().getStep().getAlias().equals("news"));
        assertTrue(constraint0.toString(),
            constraint0.getField().getFieldName().equals("title"));
        assertTrue(constraint0.getField().toString(),
            constraint0.getField().getAlias() == null);
        assertTrue(constraint0.toString(),
            constraint0.getOperator() == FieldCompareConstraint.LIKE);
        assertTrue(constraint0.toString(),
            !constraint0.isCaseSensitive());
        assertTrue(constraint0.toString(),
            constraint0.getValue().equals("test1"));
        constraint1 = (FieldValueConstraint) constraints.get(1);
        assertTrue(constraint1.toString(),
            constraint1.getField().getStep().getTableName().equals("insrel"));
        assertTrue(constraint1.toString(),
            constraint1.getField().getStep().getAlias().equals("related"));
        assertTrue(constraint1.toString(),
            constraint1.getField().getFieldName().equals("number"));
        assertTrue(constraint1.getField().toString(),
            constraint1.getField().getAlias() == null);
        assertTrue(constraint1.toString(),
            constraint1.getOperator() == FieldCompareConstraint.EQUAL);
        assertTrue(constraint1.toString(),
            constraint1.getValue().equals(new Double(10)));
        constraint2 = (FieldValueConstraint) constraints.get(2);
        assertTrue(constraint2.toString(),
            constraint2.getField().getStep().getTableName().equals("images"));
        assertTrue(constraint2.toString(),
            constraint2.getField().getStep().getAlias().equals("images"));
        assertTrue(constraint2.toString(),
            constraint2.getField().getFieldName().equals("number"));
        assertTrue(constraint2.getField().toString(),
            constraint2.getField().getAlias() == null);
        assertTrue(constraint2.toString(),
            constraint2.getOperator() == FieldCompareConstraint.EQUAL);
        assertTrue(constraint2.toString(),
            constraint2.getValue().equals(new Double(10)));
        constraint3 = (FieldValueConstraint) constraints.get(3);
        assertTrue(constraint3.toString(),
            constraint3.getField().getStep().getTableName().equals("news"));
        assertTrue(constraint3.toString(),
            constraint3.getField().getStep().getAlias().equals("news0"));
        assertTrue(constraint3.toString(),
            constraint3.getField().getFieldName().equals("title"));
        assertTrue(constraint3.getField().toString(),
            constraint3.getField().getAlias() == null);
        assertTrue(constraint0.getOperator() == FieldCompareConstraint.LIKE);
        assertTrue(constraint3.toString(),
            !constraint3.isCaseSensitive());
        assertTrue(constraint3.toString(),
            constraint3.getValue().equals("test2"));

        // "where(", without space following "where".
        QueryConvertor.setConstraint(query,
            "WHERE(LOWER(news.title) LIKE 'test1' AND related.number=10"
                + " AND images.number=10 AND LOWER(news0.title) LIKE 'test2'");
        composite = (CompositeConstraint) query.getConstraint();
        assertTrue(composite.getLogicalOperator()
            == CompositeConstraint.LOGICAL_AND);
        constraints = composite.getChilds();
        assertTrue(constraints.size() == 4);
        constraint0 = (FieldValueConstraint) constraints.get(0);
        assertTrue(constraint0.toString(),
            constraint0.getField().getStep().getTableName().equals("news"));
        assertTrue(constraint0.toString(),
            constraint0.getField().getStep().getAlias().equals("news"));
        assertTrue(constraint0.toString(),
            constraint0.getField().getFieldName().equals("title"));
        assertTrue(constraint0.getField().toString(),
            constraint0.getField().getAlias() == null);
        assertTrue(constraint0.toString(),
            constraint0.getOperator() == FieldCompareConstraint.LIKE);
        assertTrue(constraint0.toString(),
            !constraint0.isCaseSensitive());
        assertTrue(constraint0.toString(),
            constraint0.getValue().equals("test1"));
        constraint1 = (FieldValueConstraint) constraints.get(1);
        assertTrue(constraint1.toString(),
            constraint1.getField().getStep().getTableName().equals("insrel"));
        assertTrue(constraint1.toString(),
            constraint1.getField().getStep().getAlias().equals("related"));
        assertTrue(constraint1.toString(),
            constraint1.getField().getFieldName().equals("number"));
        assertTrue(constraint1.getField().toString(),
            constraint1.getField().getAlias() == null);
        assertTrue(constraint1.toString(),
            constraint1.getOperator() == FieldCompareConstraint.EQUAL);
        assertTrue(constraint1.toString(),
            constraint1.getValue().equals(new Double(10)));
        constraint2 = (FieldValueConstraint) constraints.get(2);
        assertTrue(constraint2.toString(),
            constraint2.getField().getStep().getTableName().equals("images"));
        assertTrue(constraint2.toString(),
            constraint2.getField().getStep().getAlias().equals("images"));
        assertTrue(constraint2.toString(),
            constraint2.getField().getFieldName().equals("number"));
        assertTrue(constraint2.getField().toString(),
            constraint2.getField().getAlias() == null);
        assertTrue(constraint2.toString(),
            constraint2.getOperator() == FieldCompareConstraint.EQUAL);
        assertTrue(constraint2.toString(),
            constraint2.getValue().equals(new Double(10)));
        constraint3 = (FieldValueConstraint) constraints.get(3);
        assertTrue(constraint3.toString(),
            constraint3.getField().getStep().getTableName().equals("news"));
        assertTrue(constraint3.toString(),
            constraint3.getField().getStep().getAlias().equals("news0"));
        assertTrue(constraint3.toString(),
            constraint3.getField().getFieldName().equals("title"));
        assertTrue(constraint3.getField().toString(),
            constraint3.getField().getAlias() == null);
        assertTrue(constraint0.getOperator() == FieldCompareConstraint.LIKE);
        assertTrue(constraint3.toString(),
            !constraint3.isCaseSensitive());
        assertTrue(constraint3.toString(),
            constraint3.getValue().equals("test2"));
    }

    /**
     * Tests setConstraint() method, of class org.mmbase.util.QueryConvertor,
     * specifically for constraints in altavista format, with search queries
     * involving a single step.
     */
    private void altaVistaSearchQueryTests() {
        NodeSearchQuery query1 = new NodeSearchQuery(images);
        NodeSearchQuery query2 = new NodeSearchQuery(images);

        // Empty constraint.
        QueryConvertor.setConstraint(query2, "");
        assertTrue("\n1:" + query1.toString() + "\n2:" + query2.toString(),
            query1.equals(query2));

        // Single constraint on textfield with wildcards.
        BasicFieldValueConstraint constraint1
            = (BasicFieldValueConstraint)
            new BasicFieldValueConstraint(query1.getField(imagesTitle), "t_st%")
                .setOperator(FieldCompareConstraint.LIKE)
                .setCaseSensitive(false);
        query1.setConstraint(constraint1);
        QueryConvertor.setConstraint(query2, "title=E't?st*'");
        assertTrue("\n1:" + query1.toString() + "\n2:" + query2.toString(),
            query1.equals(query2));

        // Combined constraints (using AND), field prefixed by step alias.
        BasicCompositeConstraint composite1 = new BasicCompositeConstraint(
            CompositeConstraint.LOGICAL_AND);
        query1.setConstraint(composite1);
        composite1.addChild(constraint1);
        BasicFieldValueConstraint constraint2
            = (BasicFieldValueConstraint)
            new BasicFieldValueConstraint(
                    query1.getField(imagesNumber), new Double("1.11"))
                .setOperator(FieldCompareConstraint.LESS_EQUAL);
        composite1.addChild(constraint2);
        query1.getField(imagesNumber);
        QueryConvertor.setConstraint(query2,
            "title=E't?st*'+images.number=s1.11");
        assertTrue("\n1:" + query1.toString() + "\n2:" + query2.toString(),
            query1.equals(query2));

        // Using AND NOT.
        constraint2.setInverse(true);
        QueryConvertor.setConstraint(query2,
            "title=E't?st*'-images.number=s1.11");
        assertTrue("\n1:" + query1.toString() + "\n2:" + query2.toString(),
            query1.equals(query2));

        // Additional subconstraint.
        BasicFieldValueConstraint constraint3
            = (BasicFieldValueConstraint)
            new BasicFieldValueConstraint(query1.getField(imagesNumber), new Double("100"))
                .setOperator(FieldCompareConstraint.NOT_EQUAL);
        composite1.addChild(constraint3);
        QueryConvertor.setConstraint(query2,
            "title=E't?st*'-images.number=s1.11+number=N100");
        assertTrue("\n1:" + query1.toString() + "\n2:" + query2.toString(),
            query1.equals(query2));

        // Additional subconstraint (using OR).
        BasicCompositeConstraint composite2 = new BasicCompositeConstraint(
            CompositeConstraint.LOGICAL_OR);
        composite2.addChild(composite1);
        query1.setConstraint(composite2);
        BasicFieldValueConstraint constraint4
            = (BasicFieldValueConstraint)
            new BasicFieldValueConstraint(query1.getField(imagesNumber), new Double("200"))
                .setOperator(FieldCompareConstraint.EQUAL);
        composite2.addChild(constraint4);
        QueryConvertor.setConstraint(query2,
            "title=E't?st*'-images.number=s1.11+number=N100|number=E200");
        assertTrue("\n1:" + query1.toString() + "\n2:" + query2.toString(),
            query1.equals(query2));

        // Additional subconstraint.
        BasicCompositeConstraint composite3 = new BasicCompositeConstraint(
            CompositeConstraint.LOGICAL_AND);
        composite3.addChild(composite2);
        query1.setConstraint(composite3);
        BasicFieldValueConstraint constraint5
            = (BasicFieldValueConstraint)
            new BasicFieldValueConstraint(query1.getField(imagesNumber), new Double("0"))
                .setOperator(FieldCompareConstraint.GREATER)
                .setInverse(true);
        composite3.addChild(constraint5);
        QueryConvertor.setConstraint(query2,
            "title=E't?st*'-images.number=s1.11+number=N100|number=E200-number=G0");
        assertTrue("\n1:" + query1.toString() + "\n2:" + query2.toString(),
            query1.equals(query2));

        // Inverse subconstraint.
        constraint5.setInverse(false);
        QueryConvertor.setConstraint(query2,
            "title=E't?st*'-images.number=s1.11+number=N100|number=E200+number=G0");
        assertTrue("\n1:" + query1.toString() + "\n2:" + query2.toString(),
            query1.equals(query2));
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(QueryConvertorTest.class);

        return suite;
    }

}
