package org.mmbase.storage.search.implementation;

import junit.framework.*;
import java.util.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.storage.search.*;
import org.mmbase.util.*;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.util.logging.*;

/**
 * JUnit tests.
 *
 * @author Rob van Maris
 * @version $Revision: 1.1 $
 */
public class BasicSqlGeneratorTest extends TestCase {
    
    /** Test query. */
    private BasicSqlGenerator instance;
    
    /** Disallowed values map. */
    private Map disallowedValues = null;
    
    /** MMBase query. */
    private MMBase mmbase = null;
    
    /** Images builder, used as builder example. */
    private MMObjectBuilder images = null;
    
    /** Pools builder, used as builder example. */
    private MMObjectBuilder pools = null;
    
    /** Insrel builder, used as relation builder example. */
    private InsRel insrel = null;
    
    public BasicSqlGeneratorTest(java.lang.String testName) {
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
        images = mmbase.getBuilder("images");
        insrel = mmbase.getInsRel();
        pools = mmbase.getBuilder("pools");
        
        // Disallowed fields map.
        disallowedValues = new HashMap();
        disallowedValues.put("number", "m_number");
        disallowedValues.put("snumber", "m_snumber");
        disallowedValues.put("dnumber", "m_dnumber");
        disallowedValues.put("title", "m_title");
        disallowedValues.put("images", "m_images");
        disallowedValues.put("imageTitle", "m_imageTitle");
        disallowedValues.put("i", "m_i");
        instance = new BasicSqlGenerator(disallowedValues, "base_");
    }
    
    /**
     * Tears down after each test.
     */
    public void tearDown() throws Exception {}
    
    /** Test of getAllowedValue method, of class org.mmbase.storage.search.implementation.BasicQueryHandler. */
    public void testGetAllowedValue() {
        Set entries = disallowedValues.entrySet();
        Iterator iEntries = entries.iterator();
        while (iEntries.hasNext()) {
            Map.Entry entry = (Map.Entry) iEntries.next();
            String disallowedValue = (String) entry.getKey();
            String allowedValue = (String) entry.getValue();
            
            // Disallowed value.
            assert(instance.getAllowedValue(disallowedValue).equals(allowedValue));
            allowedValue = "allowedvalue_" + allowedValue;
            
            // Allowed value.
            assert(instance.getAllowedValue(allowedValue).equals(allowedValue));
        }
        
        try {
            // Null value, shoul throw IllegalArgumentException.
            instance.getAllowedValue(null);
            fail("Null value, shoul throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
    }
    
    /** Test of appendConstraintToSql method, of class org.mmbase.storage.search.implementation.BasicQueryHandler. */
    public void testAppendConstraintToSql() {
        
        SearchQuery query = null;
        StringBuffer sb = new StringBuffer();
        Step step1 = new BasicStep(images);
        FieldDefs imagesTitle = images.getField("title");
        StepField field1 = new BasicStepField(step1, imagesTitle);
        FieldDefs imagesNumber = images.getField("number");
        StepField field2 = new BasicStepField(step1, imagesNumber);
        Step step2 = new BasicStep(pools);
        FieldDefs poolsNumber = pools.getField("number");
        StepField field3 = new BasicStepField(step2, poolsNumber);
        
        // Test for BasicFieldNullConstraint
        BasicFieldNullConstraint constraint1 
        = new BasicFieldNullConstraint(field1);

        sb.setLength(0);
        instance.appendConstraintToSql(sb, constraint1, query, false, false);
        assert(sb.toString(), sb.toString().equals(
        "m_images.m_title IS NULL"));
        
        sb.setLength(0);
        instance.appendConstraintToSql(sb, constraint1, query, false, true);
        assert(sb.toString(), sb.toString().equals(
        "m_images.m_title IS NULL"));

        sb.setLength(0);
        instance.appendConstraintToSql(sb, constraint1, query, true, false);
        assert(sb.toString(), sb.toString().equals(
        "m_images.m_title IS NOT NULL"));
        
        sb.setLength(0);
        constraint1.setInverse(true); // Set inverse.
        instance.appendConstraintToSql(sb, constraint1, query, true, false);
        assert(sb.toString(), sb.toString().equals(
        "m_images.m_title IS NULL"));
        
        sb.setLength(0);
        instance.appendConstraintToSql(sb, constraint1, query, false, false);
        assert(sb.toString(), sb.toString().equals(
        "m_images.m_title IS NOT NULL"));
        
        // Test for BasicFieldValueInConstraint (String).
        BasicFieldValueInConstraint constraint2 
        = new BasicFieldValueInConstraint(field1);
        
        // Empty values list, should throw IllegalStateException.
        sb.setLength(0);
        try {
            instance.appendConstraintToSql(sb, constraint2, query, false, false);
            fail("Empty values list, should throw IllegalStateException.");
        } catch (IllegalStateException e) {}
        
        sb.setLength(0);
        constraint2.addValue("asdf");   // Add first value.
        instance.appendConstraintToSql(sb, constraint2, query, false, false);
        assert(sb.toString(), sb.toString().equals(
        "m_images.m_title IN ('asdf')"));

        sb.setLength(0);
        constraint2.addValue("qwer");   // Add second value.
        instance.appendConstraintToSql(sb, constraint2, query, false, false);
        assert(sb.toString(), sb.toString().equals(
        "m_images.m_title IN ('asdf','qwer')"));

        sb.setLength(0);
        instance.appendConstraintToSql(sb, constraint2, query, false, true);
        assert(sb.toString(), sb.toString().equals(
        "m_images.m_title IN ('asdf','qwer')"));

        sb.setLength(0);
        instance.appendConstraintToSql(sb, constraint2, query, true, false);
        assert(sb.toString(), sb.toString().equals(
        "m_images.m_title NOT IN ('asdf','qwer')"));

        sb.setLength(0);
        constraint2.setInverse(true);   // Set inverse.
        instance.appendConstraintToSql(sb, constraint2, query, true, false);
        assert(sb.toString(), sb.toString().equals(
        "m_images.m_title IN ('asdf','qwer')"));

        sb.setLength(0);
        instance.appendConstraintToSql(sb, constraint2, query, false, false);
        assert(sb.toString(), sb.toString().equals(
        "m_images.m_title NOT IN ('asdf','qwer')"));

        // Test for BasicFieldValueInConstraint (integer).
        BasicFieldValueInConstraint constraint3 
        = new BasicFieldValueInConstraint(field2);
        
        // Empty values list, should throw IllegalStateException.
        sb.setLength(0);
        try {
            instance.appendConstraintToSql(sb, constraint3, query, false, false);
            fail("Empty values list, should throw IllegalStateException.");
        } catch (IllegalStateException e) {}
        
        sb.setLength(0);
        constraint3.addValue(new Integer(1234));   // Add first value.
        instance.appendConstraintToSql(sb, constraint3, query, false, false);
        assert(sb.toString(), sb.toString().equals(
        "m_images.m_number IN (1234)"));

        sb.setLength(0);
        constraint3.addValue(new Integer(5678));   // Add second value.
        instance.appendConstraintToSql(sb, constraint3, query, false, false);
        assert(sb.toString(), sb.toString().equals(
        "m_images.m_number IN (1234,5678)"));

        sb.setLength(0);
        instance.appendConstraintToSql(sb, constraint3, query, false, true);
        assert(sb.toString(), sb.toString().equals(
        "m_images.m_number IN (1234,5678)"));

        sb.setLength(0);
        instance.appendConstraintToSql(sb, constraint3, query, true, false);
        assert(sb.toString(), sb.toString().equals(
        "m_images.m_number NOT IN (1234,5678)"));

        sb.setLength(0);
        constraint3.setInverse(true);   // Set inverse.
        instance.appendConstraintToSql(sb, constraint3, query, true, false);
        assert(sb.toString(), sb.toString().equals(
        "m_images.m_number IN (1234,5678)"));

        sb.setLength(0);
        instance.appendConstraintToSql(sb, constraint3, query, false, false);
        assert(sb.toString(), sb.toString().equals(
        "m_images.m_number NOT IN (1234,5678)"));
        
        // Test for BasicFieldValueConstraint (string).
        BasicFieldValueConstraint constraint6 
        = new BasicFieldValueConstraint(field1, "qwerty");

        sb.setLength(0);
        instance.appendConstraintToSql(sb, constraint6, query, false, false);
        assert(sb.toString(), sb.toString().equals(
        "m_images.m_title='qwerty'"));

        sb.setLength(0);
        constraint6.setOperator(FieldCompareConstraint.LESS);
        instance.appendConstraintToSql(sb, constraint6, query, false, false);
        assert(sb.toString(), sb.toString().equals(
        "m_images.m_title<'qwerty'"));

        sb.setLength(0);
        constraint6.setOperator(FieldCompareConstraint.GREATER);
        instance.appendConstraintToSql(sb, constraint6, query, false, false);
        assert(sb.toString(), sb.toString().equals(
        "m_images.m_title>'qwerty'"));

        sb.setLength(0);
        constraint6.setOperator(FieldCompareConstraint.LIKE);
        instance.appendConstraintToSql(sb, constraint6, query, false, false);
        assert(sb.toString(), sb.toString().equals(
        "m_images.m_title LIKE 'qwerty'"));

        sb.setLength(0);
        constraint6.setInverse(true); // set inverse
        instance.appendConstraintToSql(sb, constraint6, query, false, false);
        assert(sb.toString(), sb.toString().equals(
        "NOT m_images.m_title LIKE 'qwerty'"));

        sb.setLength(0);
        instance.appendConstraintToSql(sb, constraint6, query, true, false);
        assert(sb.toString(), sb.toString().equals(
        "m_images.m_title LIKE 'qwerty'"));

        // Test for BasicFieldValueConstraint (integer).
        BasicFieldValueConstraint constraint7 
        = new BasicFieldValueConstraint(field2, new Integer(9876));

        sb.setLength(0);
        instance.appendConstraintToSql(sb, constraint7, query, false, false);
        assert(sb.toString(), sb.toString().equals(
        "m_images.m_number=9876"));

        sb.setLength(0);
        constraint7.setOperator(FieldCompareConstraint.LESS);
        instance.appendConstraintToSql(sb, constraint7, query, false, false);
        assert(sb.toString(), sb.toString().equals(
        "m_images.m_number<9876"));

        sb.setLength(0);
        constraint7.setOperator(FieldCompareConstraint.GREATER);
        instance.appendConstraintToSql(sb, constraint7, query, false, false);
        assert(sb.toString(), sb.toString().equals(
        "m_images.m_number>9876"));

        sb.setLength(0);
        constraint7.setInverse(true); // set inverse
        instance.appendConstraintToSql(sb, constraint7, query, false, false);
        assert(sb.toString(), sb.toString().equals(
        "NOT m_images.m_number>9876"));

        sb.setLength(0);
        instance.appendConstraintToSql(sb, constraint7, query, true, false);
        assert(sb.toString(), sb.toString().equals(
        "m_images.m_number>9876"));
        
        // Test for BasicCompareFieldsConstraint
        BasicCompareFieldsConstraint constraint8
        = new BasicCompareFieldsConstraint(field2, field3);
        
        sb.setLength(0);
        instance.appendConstraintToSql(sb, constraint8, query, false, false);
        assert(sb.toString(), sb.toString().equals(
        "m_images.m_number=pools.m_number"));

        sb.setLength(0);
        constraint8.setOperator(FieldCompareConstraint.LESS);
        instance.appendConstraintToSql(sb, constraint8, query, false, false);
        assert(sb.toString(), sb.toString().equals(
        "m_images.m_number<pools.m_number"));

        sb.setLength(0);
        constraint8.setOperator(FieldCompareConstraint.GREATER);
        instance.appendConstraintToSql(sb, constraint8, query, false, false);
        assert(sb.toString(), sb.toString().equals(
        "m_images.m_number>pools.m_number"));

        sb.setLength(0);
        constraint8.setInverse(true); // set inverse
        instance.appendConstraintToSql(sb, constraint8, query, false, false);
        assert(sb.toString(), sb.toString().equals(
        "NOT m_images.m_number>pools.m_number"));

        sb.setLength(0);
        instance.appendConstraintToSql(sb, constraint8, query, true, false);
        assert(sb.toString(), sb.toString().equals(
        "m_images.m_number>pools.m_number"));
        

        // Test for BasicCompareFieldsConstraint
        BasicCompositeConstraint constraint9 =
        new BasicCompositeConstraint(CompositeConstraint.LOGICAL_AND);
        
        sb.setLength(0);
        try {
            // Empty composite constraint, should throw IllegalStateException.
            instance.appendConstraintToSql(sb, constraint9, query, false, false);
            fail("Empty composite constraint, should throw IllegalStateException.");
        } catch (IllegalStateException e) {}
        
        sb.setLength(0);
        constraint9.addChild(constraint8); // Add first child constraint.
        instance.appendConstraintToSql(sb, constraint9, query, false, false);
        assert(sb.toString(), sb.toString().equals(
        "NOT m_images.m_number>pools.m_number"));
        
        sb.setLength(0);
        instance.appendConstraintToSql(sb, constraint9, query, true, false);
        assert(sb.toString(), sb.toString().equals(
        "m_images.m_number>pools.m_number"));
        
        sb.setLength(0);
        instance.appendConstraintToSql(sb, constraint9, query, true, true);
        assert(sb.toString(), sb.toString().equals(
        "m_images.m_number>pools.m_number"));
        
        sb.setLength(0);
        constraint9.addChild(constraint7); // Add second child constraint.
        instance.appendConstraintToSql(sb, constraint9, query, false, false);
        assert(sb.toString(), sb.toString().equals(
        "NOT m_images.m_number>pools.m_number AND NOT m_images.m_number>9876"));
        
        sb.setLength(0);
        instance.appendConstraintToSql(sb, constraint9, query, true, false);
        assert(sb.toString(), sb.toString().equals(
        "m_images.m_number>pools.m_number OR m_images.m_number>9876"));
        
        sb.setLength(0);
        instance.appendConstraintToSql(sb, constraint9, query, true, true);
        assert(sb.toString(), sb.toString().equals(
        "(m_images.m_number>pools.m_number OR m_images.m_number>9876)"));
        
        sb.setLength(0);
        instance.appendConstraintToSql(sb, constraint9, query, false, true);
        assert(sb.toString(), sb.toString().equals(
        "(NOT m_images.m_number>pools.m_number AND NOT m_images.m_number>9876)"));
        
        sb.setLength(0);
        constraint7.setInverse(false); // Set second child not inverse.
        instance.appendConstraintToSql(sb, constraint9, query, false, false);
        assert(sb.toString(), sb.toString().equals(
        "NOT m_images.m_number>pools.m_number AND m_images.m_number>9876"));
        
        sb.setLength(0);
        instance.appendConstraintToSql(sb, constraint9, query, true, false);
        assert(sb.toString(), sb.toString().equals(
        "m_images.m_number>pools.m_number OR NOT m_images.m_number>9876"));
    }
    
    /** Test of toSql method, of class org.mmbase.storage.search.implementation.BasicQueryHandler. */
    public void testToSql() throws Exception{
        BasicSearchQuery query = new BasicSearchQuery();
        
       // Query without step, should throw IllegalStateException.
        try {
            instance.toSql(query);
            fail("Query without step, should throw IllegalStateException.");
        } catch (IllegalStateException e) {};
        
        BasicStep step1 = query.addStep(images);
        
        // Query without field, should throw IllegalStateException.
        try {
            instance.toSql(query);
            fail("Query without field, should throw IllegalStateException.");
        } catch (IllegalStateException e) {};
        
        FieldDefs imagesTitle = images.getField("title");
        FieldDefs insrelRNumber = insrel.getField("rnumber");
        FieldDefs poolsName = pools.getField("name");
        
        // Query with one stap (default alias) and one field (default alias).
        BasicStepField field1a = query.addField(step1, imagesTitle);
        assert(instance.toSql(query), instance.toSql(query).equals(
        "SELECT m_images.m_title AS m_title "
        + "FROM base_m_images m_images"));
        
        // Set step alias.
        step1.setAlias("i");
        assert(instance.toSql(query), instance.toSql(query).equals(
        "SELECT m_i.m_title AS m_title "
        + "FROM base_m_images m_i"));
        
        // Set field alias.
        field1a.setAlias("imageTitle");
        assert(instance.toSql(query), instance.toSql(query).equals(
        "SELECT m_i.m_title AS m_imageTitle "
        + "FROM base_m_images m_i"));
        
        // Add second field (default alias).
        FieldDefs imagesNumber = images.getField("number");
        BasicStepField field1b = query.addField(step1, imagesNumber);
        assert(instance.toSql(query), instance.toSql(query).equals(
        "SELECT m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS m_number "
        + "FROM base_m_images m_i"));
        
        // Set alias for second field.
        field1b.setAlias("imageNumber");
        assert(instance.toSql(query), instance.toSql(query).equals(
        "SELECT m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber "
        + "FROM base_m_images m_i"));
        
        // Set distinct true.
        query.setDistinct(true);
        assert(instance.toSql(query), instance.toSql(query).equals(
        "SELECT DISTINCT m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber "
        + "FROM base_m_images m_i"));
        
        // Add sortorder (default direction).
        BasicSortOrder sortOrder1a = query.addSortOrder(field1a);
        assert(instance.toSql(query), instance.toSql(query).equals(
        "SELECT DISTINCT m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber "
        + "FROM base_m_images m_i "
        + "ORDER BY m_imageTitle ASC"));
        
        // Set constraint.
        Constraint constraint1 = new BasicFieldValueConstraint(field1a, "abd");
        query.setConstraint(constraint1);
        assert(instance.toSql(query), instance.toSql(query).equals(
        "SELECT DISTINCT m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber "
        + "FROM base_m_images m_i "
        + "WHERE m_i.m_title='abd' "
        + "ORDER BY m_imageTitle ASC"));
        
        // Set composite constraint.
        Constraint constraint2 
        = new BasicFieldValueConstraint(field1b, new Integer(123));
        BasicCompositeConstraint constraint3 
        = new BasicCompositeConstraint(CompositeConstraint.LOGICAL_AND);
        constraint3.addChild(constraint1);
        constraint3.addChild(constraint2);
        query.setConstraint(constraint3);
        assert(instance.toSql(query), instance.toSql(query).equals(
        "SELECT DISTINCT m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber "
        + "FROM base_m_images m_i "
        + "WHERE m_i.m_title='abd' AND m_i.m_number=123 "
        + "ORDER BY m_imageTitle ASC"));
        
        // Set sortorder direction.
        sortOrder1a.setDirection(SortOrder.ORDER_DESCENDING);
        assert(instance.toSql(query), instance.toSql(query).equals(
        "SELECT DISTINCT m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber "
        + "FROM base_m_images m_i "
        + "WHERE m_i.m_title='abd' AND m_i.m_number=123 "
        + "ORDER BY m_imageTitle DESC"));
        
        // Set sortorder direction.
        sortOrder1a.setDirection(SortOrder.ORDER_ASCENDING);
        assert(instance.toSql(query), instance.toSql(query).equals(
        "SELECT DISTINCT m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber "
        + "FROM base_m_images m_i "
        + "WHERE m_i.m_title='abd' AND m_i.m_number=123 "
        + "ORDER BY m_imageTitle ASC"));
        
        // Set distinct false.
        query.setDistinct(false);
        assert(instance.toSql(query), instance.toSql(query).equals(
        "SELECT m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber "
        + "FROM base_m_images m_i "
        + "WHERE m_i.m_title='abd' AND m_i.m_number=123 "
        + "ORDER BY m_imageTitle ASC"));
        
        // Add node constraint for first step (one node).
        step1.addNode(123);
        assert(instance.toSql(query), instance.toSql(query).equals(
        "SELECT m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber "
        + "FROM base_m_images m_i "
        + "WHERE m_i.m_number IN (123) "
        + "AND (m_i.m_title='abd' AND m_i.m_number=123) "
        + "ORDER BY m_imageTitle ASC"));
        
        // Add second node to node constraint.
        step1.addNode(456);
        assert(instance.toSql(query), instance.toSql(query).equals(
        "SELECT m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber "
        + "FROM base_m_images m_i "
        + "WHERE m_i.m_number IN (123,456) "
        + "AND (m_i.m_title='abd' AND m_i.m_number=123) "
        + "ORDER BY m_imageTitle ASC"));
        
        // Add relationstep (default directionality).
        BasicRelationStep step2 = query.addRelationStep(insrel,pools);
        BasicStep step3 = (BasicStep) step2.getNext();
        assert(instance.toSql(query), instance.toSql(query).equals(
        "SELECT m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber "
        + "FROM base_m_images m_i,base_insrel insrel,base_pools pools "
        + "WHERE m_i.m_number IN (123,456) "
        + "AND ((m_i.m_number=insrel.m_dnumber AND pools.m_number=insrel.m_snumber) "
        + "OR (m_i.m_number=insrel.m_snumber AND pools.m_number=insrel.m_dnumber)) "
        + "AND (m_i.m_title='abd' AND m_i.m_number=123) "
        + "ORDER BY m_imageTitle ASC"));
        
        // Set directionality for relationstep to DESTINATION.
        step2.setDirectionality(RelationStep.DIRECTIONS_DESTINATION);
        assert(instance.toSql(query), instance.toSql(query).equals(
        "SELECT m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber "
        + "FROM base_m_images m_i,base_insrel insrel,base_pools pools "
        + "WHERE m_i.m_number IN (123,456) "
        + "AND (m_i.m_number=insrel.m_snumber AND pools.m_number=insrel.m_dnumber) "
        + "AND (m_i.m_title='abd' AND m_i.m_number=123) "
        + "ORDER BY m_imageTitle ASC"));
        
        // Set directionality for relationstep to SOURCE.
        step2.setDirectionality(RelationStep.DIRECTIONS_SOURCE);
        assert(instance.toSql(query), instance.toSql(query).equals(
        "SELECT m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber "
        + "FROM base_m_images m_i,base_insrel insrel,base_pools pools "
        + "WHERE m_i.m_number IN (123,456) "
        + "AND (m_i.m_number=insrel.m_dnumber AND pools.m_number=insrel.m_snumber) "
        + "AND (m_i.m_title='abd' AND m_i.m_number=123) "
        + "ORDER BY m_imageTitle ASC"));
        
        // Set directionality for relationstep to BOTH.
        step2.setDirectionality(RelationStep.DIRECTIONS_BOTH);
        assert(instance.toSql(query), instance.toSql(query).equals(
        "SELECT m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber "
        + "FROM base_m_images m_i,base_insrel insrel,base_pools pools "
        + "WHERE m_i.m_number IN (123,456) "
        + "AND ((m_i.m_number=insrel.m_dnumber AND pools.m_number=insrel.m_snumber) "
        + "OR (m_i.m_number=insrel.m_snumber AND pools.m_number=insrel.m_dnumber)) "
        + "AND (m_i.m_title='abd' AND m_i.m_number=123) "
        + "ORDER BY m_imageTitle ASC"));
        
        // Add field for relationstep.
        StepField field2a = query.addField(step2, insrelRNumber);
        assert(instance.toSql(query), instance.toSql(query).equals(
        "SELECT m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber,"
        + "insrel.rnumber AS rnumber "
        + "FROM base_m_images m_i,base_insrel insrel,base_pools pools "
        + "WHERE m_i.m_number IN (123,456) "
        + "AND ((m_i.m_number=insrel.m_dnumber AND pools.m_number=insrel.m_snumber) "
        + "OR (m_i.m_number=insrel.m_snumber AND pools.m_number=insrel.m_dnumber)) "
        + "AND (m_i.m_title='abd' AND m_i.m_number=123) "
        + "ORDER BY m_imageTitle ASC"));
        
        // Add field for third step.
        StepField field3a = query.addField(step3, poolsName);
        assert(instance.toSql(query), instance.toSql(query).equals(
        "SELECT m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber,"
        + "insrel.rnumber AS rnumber,"
        + "pools.name AS name "
        + "FROM base_m_images m_i,base_insrel insrel,base_pools pools "
        + "WHERE m_i.m_number IN (123,456) "
        + "AND ((m_i.m_number=insrel.m_dnumber AND pools.m_number=insrel.m_snumber) "
        + "OR (m_i.m_number=insrel.m_snumber AND pools.m_number=insrel.m_dnumber)) "
        + "AND (m_i.m_title='abd' AND m_i.m_number=123) "
        + "ORDER BY m_imageTitle ASC"));
        
        // Add second sortorder
        BasicSortOrder sortOrder3a = query.addSortOrder(field3a);
        assert(instance.toSql(query), instance.toSql(query).equals(
        "SELECT m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber,"
        + "insrel.rnumber AS rnumber,"
        + "pools.name AS name "
        + "FROM base_m_images m_i,base_insrel insrel,base_pools pools "
        + "WHERE m_i.m_number IN (123,456) "
        + "AND ((m_i.m_number=insrel.m_dnumber AND pools.m_number=insrel.m_snumber) "
        + "OR (m_i.m_number=insrel.m_snumber AND pools.m_number=insrel.m_dnumber)) "
        + "AND (m_i.m_title='abd' AND m_i.m_number=123) "
        + "ORDER BY m_imageTitle ASC,name ASC"));
        
        // Add third sortorder.
        BasicSortOrder sortOrder2a = query.addSortOrder(field2a);
        assert(instance.toSql(query), instance.toSql(query).equals(
        "SELECT m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber,"
        + "insrel.rnumber AS rnumber,"
        + "pools.name AS name "
        + "FROM base_m_images m_i,base_insrel insrel,base_pools pools "
        + "WHERE m_i.m_number IN (123,456) "
        + "AND ((m_i.m_number=insrel.m_dnumber AND pools.m_number=insrel.m_snumber) "
        + "OR (m_i.m_number=insrel.m_snumber AND pools.m_number=insrel.m_dnumber)) "
        + "AND (m_i.m_title='abd' AND m_i.m_number=123) "
        + "ORDER BY m_imageTitle ASC,name ASC,rnumber ASC"));
        
        // Add node constraint for second step (relation step).
        step2.addNode(789);
        assert(instance.toSql(query), instance.toSql(query).equals(
        "SELECT m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber,"
        + "insrel.rnumber AS rnumber,"
        + "pools.name AS name "
        + "FROM base_m_images m_i,base_insrel insrel,base_pools pools "
        + "WHERE m_i.m_number IN (123,456) AND insrel.m_number IN (789) "
        + "AND ((m_i.m_number=insrel.m_dnumber AND pools.m_number=insrel.m_snumber) "
        + "OR (m_i.m_number=insrel.m_snumber AND pools.m_number=insrel.m_dnumber)) "
        + "AND (m_i.m_title='abd' AND m_i.m_number=123) "
        + "ORDER BY m_imageTitle ASC,name ASC,rnumber ASC"));
    }
    
    /** Test of appendQueryBodyToSql method, of class org.mmbase.storage.search.implementation.BasicSqlGenerator. */
    public void testAppendQueryBodyToSql() {
        // TODO: move BasicSqlGenerater code somewhere else - see TODO
        // in top of BasicSqlGenerater source.
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(BasicSqlGeneratorTest.class);
        
        return suite;
    }
    
}
