package org.mmbase.util;

import junit.framework.*;
import java.util.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.database.support.*;
import org.mmbase.util.logging.*;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.storage.search.legacy.ConstraintParser;

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
    private FieldDefs imagesNumber = null;
    private FieldDefs imagesTitle = null;
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
    
    /** Test of altaVista2SearchQuery method, of class org.mmbase.util.QueryConvertor. */
    public void testAltaVista2SearchQuery() {
        NodeSearchQuery query1 = new NodeSearchQuery(images);
        NodeSearchQuery query2 = QueryConvertor.altaVista2SearchQuery(
            "", images);
        assertTrue("\n1:" + query1.toString() + "\n2:" + query2.toString(), 
            query1.equals(query2));

        BasicFieldValueConstraint constraint1 
            = (BasicFieldValueConstraint)
            new BasicFieldValueConstraint(query1.getField(imagesTitle), "t_st%")
                .setOperator(FieldValueConstraint.LIKE)
                .setCaseSensitive(false);
        query1.setConstraint(constraint1);
        query2 = QueryConvertor.altaVista2SearchQuery(
            "title=E't?st*'", images);
        assertTrue("\n1:" + query1.toString() + "\n2:" + query2.toString(), 
            query1.equals(query2));

        BasicCompositeConstraint composite1 = new BasicCompositeConstraint(
            CompositeConstraint.LOGICAL_AND);
        query1.setConstraint(composite1);
        composite1.addChild(constraint1);
        BasicFieldValueConstraint constraint2 
            = (BasicFieldValueConstraint)
            new BasicFieldValueConstraint(query1.getField(imagesNumber), new Double("1.11"))
                .setOperator(FieldValueConstraint.LESS_EQUAL);
        composite1.addChild(constraint2);
        query1.getField(imagesNumber).setAlias("images.number");
        query2 = QueryConvertor.altaVista2SearchQuery(
            "title=E't?st*'+images.number=s1.11", images);
        assertTrue("\n1:" + query1.toString() + "\n2:" + query2.toString(), 
            query1.equals(query2));
        
        constraint2.setInverse(true);
        query2 = QueryConvertor.altaVista2SearchQuery(
            "title=E't?st*'-images.number=s1.11", images);
        assertTrue("\n1:" + query1.toString() + "\n2:" + query2.toString(), 
            query1.equals(query2));
        
        BasicFieldValueConstraint constraint3 
            = (BasicFieldValueConstraint)
            new BasicFieldValueConstraint(query1.getField(imagesNumber), new Double("100"))
                .setOperator(FieldValueConstraint.NOT_EQUAL);
        composite1.addChild(constraint3);
        query2 = QueryConvertor.altaVista2SearchQuery(
            "title=E't?st*'-images.number=s1.11+number=N100", images);
        assertTrue("\n1:" + query1.toString() + "\n2:" + query2.toString(), 
            query1.equals(query2));
        
        BasicCompositeConstraint composite2 = new BasicCompositeConstraint(
            CompositeConstraint.LOGICAL_OR);
        composite2.addChild(composite1);
        query1.setConstraint(composite2);
        BasicFieldValueConstraint constraint4
            = (BasicFieldValueConstraint)
            new BasicFieldValueConstraint(query1.getField(imagesNumber), new Double("200"))
                .setOperator(FieldValueConstraint.EQUAL);
        composite2.addChild(constraint4);
        query2 = QueryConvertor.altaVista2SearchQuery(
            "title=E't?st*'-images.number=s1.11+number=N100|number=E200", images);
        assertTrue("\n1:" + query1.toString() + "\n2:" + query2.toString(), 
            query1.equals(query2));
        
        BasicCompositeConstraint composite3 = new BasicCompositeConstraint(
            CompositeConstraint.LOGICAL_AND);
        composite3.addChild(composite2);
        query1.setConstraint(composite3);
        BasicFieldValueConstraint constraint5
            = (BasicFieldValueConstraint)
            new BasicFieldValueConstraint(query1.getField(imagesNumber), new Double("0"))
                .setOperator(FieldValueConstraint.GREATER)
                .setInverse(true);
        composite3.addChild(constraint5);
        query2 = QueryConvertor.altaVista2SearchQuery(
            "title=E't?st*'-images.number=s1.11+number=N100|number=E200-number=G0", images);
        assertTrue("\n1:" + query1.toString() + "\n2:" + query2.toString(), 
            query1.equals(query2));

        constraint5.setInverse(false);
        query2 = QueryConvertor.altaVista2SearchQuery(
            "title=E't?st*'-images.number=s1.11+number=N100|number=E200+number=G0", images);
        assertTrue("\n1:" + query1.toString() + "\n2:" + query2.toString(), 
            query1.equals(query2));
    }
    
    /** Test of setConstraint() method, of class org.mmbase.util.QueryConvertor. */
    public void testSetConstraint() {
        List tables = Arrays.asList(
            new Object[] {"pools", "related", "images", "insrel", "pools0"});
        List empty = new ArrayList(0);
        BasicSearchQuery query = clusterBuilder.getMultiLevelSearchQuery(
            empty, empty, null, tables, null, 
            empty, empty, ClusterBuilder.SEARCH_ALL);
        
        // Altavista format.
        QueryConvertor.setConstraint(query, 
            "pools.name=E'test1'+related.number=E10+images.number=E10+pools0.name=E'test2'");
        CompositeConstraint composite 
            = (CompositeConstraint) query.getConstraint();
        assertTrue(composite.getLogicalOperator() 
            == CompositeConstraint.LOGICAL_AND);
        List constraints = composite.getChilds();
        assertTrue(constraints.size() == 4);
        FieldValueConstraint constraint0 
            = (FieldValueConstraint) constraints.get(0);
        assertTrue(constraint0.toString(), 
            constraint0.getField().getStep().getTableName().equals("pools"));
        assertTrue(constraint0.toString(), 
            constraint0.getField().getStep().getAlias().equals("pools"));
        assertTrue(constraint0.toString(), 
            constraint0.getField().getFieldName().equals("name"));
        assertTrue(constraint0.getField().toString(), 
            constraint0.getField().getAlias().equals("pools.name"));
        assertTrue(constraint0.toString(), 
            constraint0.getOperator() == FieldValueConstraint.LIKE);
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
            constraint1.getField().getAlias().equals("related.number"));
        assertTrue(constraint1.toString(), 
            constraint1.getOperator() == FieldValueConstraint.EQUAL);
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
            constraint2.getField().getAlias().equals("images.number"));
        assertTrue(constraint2.toString(), 
            constraint2.getOperator() == FieldValueConstraint.EQUAL);
        assertTrue(constraint2.toString(), 
            constraint2.getValue().equals(new Double(10)));
        FieldValueConstraint constraint3 
            = (FieldValueConstraint) constraints.get(3);
        assertTrue(constraint3.toString(), 
            constraint3.getField().getStep().getTableName().equals("pools"));
        assertTrue(constraint3.toString(), 
            constraint3.getField().getStep().getAlias().equals("pools0"));
        assertTrue(constraint3.toString(), 
            constraint3.getField().getFieldName().equals("name"));
        assertTrue(constraint3.getField().toString(), 
            constraint3.getField().getAlias().equals("pools0.name"));
        assertTrue(constraint0.getOperator() == FieldValueConstraint.LIKE);
        assertTrue(constraint3.toString(), 
            !constraint3.isCaseSensitive());
        assertTrue(constraint3.toString(), 
            constraint3.getValue().equals("test2"));
        
        // Where string.
        QueryConvertor.setConstraint(query, 
            "WHERE LOWER(pools.name) LIKE 'test1' AND related.number=10"
                + " AND images.number=10 AND LOWER(pools0.name) LIKE 'test2'");
        composite = (CompositeConstraint) query.getConstraint();
        assertTrue(composite.getLogicalOperator() 
            == CompositeConstraint.LOGICAL_AND);
        constraints = composite.getChilds();
        assertTrue(constraints.size() == 4);
        constraint0 = (FieldValueConstraint) constraints.get(0);
        assertTrue(constraint0.toString(), 
            constraint0.getField().getStep().getTableName().equals("pools"));
        assertTrue(constraint0.toString(), 
            constraint0.getField().getStep().getAlias().equals("pools"));
        assertTrue(constraint0.toString(), 
            constraint0.getField().getFieldName().equals("name"));
        assertTrue(constraint0.getField().toString(), 
            constraint0.getField().getAlias().equals("pools.name"));
        assertTrue(constraint0.toString(), 
            constraint0.getOperator() == FieldValueConstraint.LIKE);
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
            constraint1.getField().getAlias().equals("related.number"));
        assertTrue(constraint1.toString(), 
            constraint1.getOperator() == FieldValueConstraint.EQUAL);
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
            constraint2.getField().getAlias().equals("images.number"));
        assertTrue(constraint2.toString(), 
            constraint2.getOperator() == FieldValueConstraint.EQUAL);
        assertTrue(constraint2.toString(), 
            constraint2.getValue().equals(new Double(10)));
        constraint3 = (FieldValueConstraint) constraints.get(3);
        assertTrue(constraint3.toString(), 
            constraint3.getField().getStep().getTableName().equals("pools"));
        assertTrue(constraint3.toString(), 
            constraint3.getField().getStep().getAlias().equals("pools0"));
        assertTrue(constraint3.toString(), 
            constraint3.getField().getFieldName().equals("name"));
        assertTrue(constraint3.getField().toString(), 
            constraint3.getField().getAlias().equals("pools0.name"));
        assertTrue(constraint0.getOperator() == FieldValueConstraint.LIKE);
        assertTrue(constraint3.toString(), 
            !constraint3.isCaseSensitive());
        assertTrue(constraint3.toString(), 
            constraint3.getValue().equals("test2"));
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(QueryConvertorTest.class);
        
        return suite;
    }
    
}
