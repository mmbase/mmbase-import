package org.mmbase.storage.search.implementation.database;

import junit.framework.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.database.support.MMJdbc2NodeInterface;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.util.logging.*;
import org.mmbase.module.database.MultiConnection;
import java.sql.*;
import java.util.*;

/**
 * JUnit tests.
 *
 * @author Rob van Maris
 * @version $Revision: 1.1 $
 */
public class BasicSqlHandlerTest extends TestCase {
    
    /** Test instance. */
    private BasicSqlHandler instance;
    
    /** Disallowed values map. */
    private Map disallowedValues = null;
    
    /** Prefix applied to buildernames to create tablenames. */
    private String prefix = null;
    
    /** MMBase query. */
    private MMBase mmbase = null;
    
    /** Images builder, used as builder example. */
    private MMObjectBuilder images = null;
    
    /** Pools builder, used as builder example. */
    private MMObjectBuilder pools = null;
    
    /** Insrel builder, used as relation builder example. */
    private InsRel insrel = null;
    
    public BasicSqlHandlerTest(java.lang.String testName) {
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
        instance = new BasicSqlHandler(disallowedValues);
        
        prefix = mmbase.getBaseName() + "_";
    }
    
    /**
     * Tears down after each test.
     */
    public void tearDown() throws Exception {}
    
    /** Test of toSqlString method, of class org.mmbase.storage.search.implementation.database.BasicSqlHandler. */
    public void testToSqlString() throws Exception {
        assert(BasicSqlHandler.toSqlString(null) == null);
        assert(BasicSqlHandler.toSqlString("'").equals("''"));
        assert(BasicSqlHandler.toSqlString("'''''").equals("''''''''''"));
        assert(BasicSqlHandler.toSqlString("asdf'").equals("asdf''"));
        assert(BasicSqlHandler.toSqlString("asdf'jkl").equals("asdf''jkl"));
        assert(BasicSqlHandler.toSqlString("asdf'jkl'").equals("asdf''jkl''"));
        assert(BasicSqlHandler.toSqlString("'asdf'jkl").equals("''asdf''jkl"));
        assert(BasicSqlHandler.toSqlString("qwerty").equals("qwerty"));
    }
    
    /** Test of toSql method, of class org.mmbase.storage.search.implementation.database.BasicSqlHandler. */
    public void testToSql() throws Exception{
        BasicSearchQuery query = new BasicSearchQuery();
        
       // Query without step, should throw IllegalStateException.
        try {
            instance.toSql(query, instance);
            fail("Query without step, should throw IllegalStateException.");
        } catch (IllegalStateException e) {};
        
        BasicStep step1 = query.addStep(images);
        
        // Query without field, should throw IllegalStateException.
        try {
            instance.toSql(query, instance);
            fail("Query without field, should throw IllegalStateException.");
        } catch (IllegalStateException e) {};
        
        FieldDefs imagesTitle = images.getField("title");
        FieldDefs insrelRNumber = insrel.getField("rnumber");
        FieldDefs poolsName = pools.getField("name");
        
        // Query with one step (default alias) and one field (default alias).
        BasicStepField field1a = query.addField(step1, imagesTitle);
        String strSql = instance.toSql(query, instance);
        assert(strSql, strSql.equals(
        "SELECT m_images.m_title AS m_title "
        + "FROM " + prefix + "m_images m_images"));
        
        // Set step alias.
        step1.setAlias("i");
        strSql = instance.toSql(query, instance);
        assert(strSql, strSql.equals(
        "SELECT m_i.m_title AS m_title "
        + "FROM " + prefix + "m_images m_i"));
        
        // Set field alias.
        field1a.setAlias("imageTitle");
        strSql = instance.toSql(query, instance);
        assert(strSql, strSql.equals(
        "SELECT m_i.m_title AS m_imageTitle "
        + "FROM " + prefix + "m_images m_i"));
        
        // Add second field (default alias).
        FieldDefs imagesNumber = images.getField("number");
        BasicStepField field1b = query.addField(step1, imagesNumber);
        strSql = instance.toSql(query, instance);
        assert(strSql, strSql.equals(
        "SELECT m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS m_number "
        + "FROM " + prefix + "m_images m_i"));
        
        // Set alias for second field.
        field1b.setAlias("imageNumber");
        strSql = instance.toSql(query, instance);
        assert(strSql, strSql.equals(
        "SELECT m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber "
        + "FROM " + prefix + "m_images m_i"));
        
        // Set distinct true.
        query.setDistinct(true);
        strSql = instance.toSql(query, instance);
        assert(strSql, strSql.equals(
        "SELECT DISTINCT m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber "
        + "FROM " + prefix + "m_images m_i"));
        
        // Add sortorder (default direction).
        BasicSortOrder sortOrder1a = query.addSortOrder(field1a);
        strSql = instance.toSql(query, instance);
        assert(strSql, strSql.equals(
        "SELECT DISTINCT m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber "
        + "FROM " + prefix + "m_images m_i "
        + "ORDER BY m_imageTitle ASC"));
        
        // Set constraint.
        Constraint constraint1 = new BasicFieldValueConstraint(field1a, "abd");
        query.setConstraint(constraint1);
        strSql = instance.toSql(query, instance);
        assert(strSql, strSql.equals(
        "SELECT DISTINCT m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber "
        + "FROM " + prefix + "m_images m_i "
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
        strSql = instance.toSql(query, instance);
        assert(strSql, strSql.equals(
        "SELECT DISTINCT m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber "
        + "FROM " + prefix + "m_images m_i "
        + "WHERE m_i.m_title='abd' AND m_i.m_number=123 "
        + "ORDER BY m_imageTitle ASC"));
        
        // Set sortorder direction.
        sortOrder1a.setDirection(SortOrder.ORDER_DESCENDING);
        strSql = instance.toSql(query, instance);
        assert(strSql, strSql.equals(
        "SELECT DISTINCT m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber "
        + "FROM " + prefix + "m_images m_i "
        + "WHERE m_i.m_title='abd' AND m_i.m_number=123 "
        + "ORDER BY m_imageTitle DESC"));
        
        // Set sortorder direction.
        sortOrder1a.setDirection(SortOrder.ORDER_ASCENDING);
        strSql = instance.toSql(query, instance);
        assert(strSql, strSql.equals(
        "SELECT DISTINCT m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber "
        + "FROM " + prefix + "m_images m_i "
        + "WHERE m_i.m_title='abd' AND m_i.m_number=123 "
        + "ORDER BY m_imageTitle ASC"));
        
        // Set distinct false.
        query.setDistinct(false);
        strSql = instance.toSql(query, instance);
        assert(strSql, strSql.equals(
        "SELECT m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber "
        + "FROM " + prefix + "m_images m_i "
        + "WHERE m_i.m_title='abd' AND m_i.m_number=123 "
        + "ORDER BY m_imageTitle ASC"));
        
        // Add node constraint for first step (one node).
        step1.addNode(123);
        strSql = instance.toSql(query, instance);
        assert(strSql, strSql.equals(
        "SELECT m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber "
        + "FROM " + prefix + "m_images m_i "
        + "WHERE m_i.m_number IN (123) "
        + "AND (m_i.m_title='abd' AND m_i.m_number=123) "
        + "ORDER BY m_imageTitle ASC"));
        
        // Add second node to node constraint.
        step1.addNode(456);
        strSql = instance.toSql(query, instance);
        assert(strSql, strSql.equals(
        "SELECT m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber "
        + "FROM " + prefix + "m_images m_i "
        + "WHERE m_i.m_number IN (123,456) "
        + "AND (m_i.m_title='abd' AND m_i.m_number=123) "
        + "ORDER BY m_imageTitle ASC"));
        
        // Add relationstep (default directionality).
        BasicRelationStep step2 = query.addRelationStep(insrel,pools);
        BasicStep step3 = (BasicStep) step2.getNext();
        strSql = instance.toSql(query, instance);
        assert(strSql, strSql.equals(
        "SELECT m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber "
        + "FROM " + prefix + "m_images m_i," + prefix + "insrel insrel," + prefix + "pools pools "
        + "WHERE m_i.m_number IN (123,456) "
        + "AND ((m_i.m_number=insrel.m_dnumber AND pools.m_number=insrel.m_snumber) "
        + "OR (m_i.m_number=insrel.m_snumber AND pools.m_number=insrel.m_dnumber)) "
        + "AND (m_i.m_title='abd' AND m_i.m_number=123) "
        + "ORDER BY m_imageTitle ASC"));
        
        // Set directionality for relationstep to DESTINATION.
        step2.setDirectionality(RelationStep.DIRECTIONS_DESTINATION);
        strSql = instance.toSql(query, instance);
        assert(strSql, strSql.equals(
        "SELECT m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber "
        + "FROM " + prefix + "m_images m_i," + prefix + "insrel insrel," + prefix + "pools pools "
        + "WHERE m_i.m_number IN (123,456) "
        + "AND (m_i.m_number=insrel.m_snumber AND pools.m_number=insrel.m_dnumber) "
        + "AND (m_i.m_title='abd' AND m_i.m_number=123) "
        + "ORDER BY m_imageTitle ASC"));
        
        // Set directionality for relationstep to SOURCE.
        step2.setDirectionality(RelationStep.DIRECTIONS_SOURCE);
        strSql = instance.toSql(query, instance);
        assert(strSql, strSql.equals(
        "SELECT m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber "
        + "FROM " + prefix + "m_images m_i," + prefix + "insrel insrel," + prefix + "pools pools "
        + "WHERE m_i.m_number IN (123,456) "
        + "AND (m_i.m_number=insrel.m_dnumber AND pools.m_number=insrel.m_snumber) "
        + "AND (m_i.m_title='abd' AND m_i.m_number=123) "
        + "ORDER BY m_imageTitle ASC"));
        
        // Set directionality for relationstep to BOTH.
        step2.setDirectionality(RelationStep.DIRECTIONS_BOTH);
        strSql = instance.toSql(query, instance);
        assert(strSql, strSql.equals(
        "SELECT m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber "
        + "FROM " + prefix + "m_images m_i," + prefix + "insrel insrel," + prefix + "pools pools "
        + "WHERE m_i.m_number IN (123,456) "
        + "AND ((m_i.m_number=insrel.m_dnumber AND pools.m_number=insrel.m_snumber) "
        + "OR (m_i.m_number=insrel.m_snumber AND pools.m_number=insrel.m_dnumber)) "
        + "AND (m_i.m_title='abd' AND m_i.m_number=123) "
        + "ORDER BY m_imageTitle ASC"));
        
        // Add field for relationstep.
        StepField field2a = query.addField(step2, insrelRNumber);
        strSql = instance.toSql(query, instance);
        assert(strSql, strSql.equals(
        "SELECT m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber,"
        + "insrel.rnumber AS rnumber "
        + "FROM " + prefix + "m_images m_i," + prefix + "insrel insrel," + prefix + "pools pools "
        + "WHERE m_i.m_number IN (123,456) "
        + "AND ((m_i.m_number=insrel.m_dnumber AND pools.m_number=insrel.m_snumber) "
        + "OR (m_i.m_number=insrel.m_snumber AND pools.m_number=insrel.m_dnumber)) "
        + "AND (m_i.m_title='abd' AND m_i.m_number=123) "
        + "ORDER BY m_imageTitle ASC"));
        
        // Add field for third step.
        StepField field3a = query.addField(step3, poolsName);
        strSql = instance.toSql(query, instance);
        assert(strSql, strSql.equals(
        "SELECT m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber,"
        + "insrel.rnumber AS rnumber,"
        + "pools.name AS name "
        + "FROM " + prefix + "m_images m_i," + prefix + "insrel insrel," + prefix + "pools pools "
        + "WHERE m_i.m_number IN (123,456) "
        + "AND ((m_i.m_number=insrel.m_dnumber AND pools.m_number=insrel.m_snumber) "
        + "OR (m_i.m_number=insrel.m_snumber AND pools.m_number=insrel.m_dnumber)) "
        + "AND (m_i.m_title='abd' AND m_i.m_number=123) "
        + "ORDER BY m_imageTitle ASC"));
        
        // Add second sortorder
        BasicSortOrder sortOrder3a = query.addSortOrder(field3a);
        strSql = instance.toSql(query, instance);
        assert(strSql, strSql.equals(
        "SELECT m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber,"
        + "insrel.rnumber AS rnumber,"
        + "pools.name AS name "
        + "FROM " + prefix + "m_images m_i," + prefix + "insrel insrel," + prefix + "pools pools "
        + "WHERE m_i.m_number IN (123,456) "
        + "AND ((m_i.m_number=insrel.m_dnumber AND pools.m_number=insrel.m_snumber) "
        + "OR (m_i.m_number=insrel.m_snumber AND pools.m_number=insrel.m_dnumber)) "
        + "AND (m_i.m_title='abd' AND m_i.m_number=123) "
        + "ORDER BY m_imageTitle ASC,name ASC"));
        
        // Add third sortorder.
        BasicSortOrder sortOrder2a = query.addSortOrder(field2a);
        strSql = instance.toSql(query, instance);
        assert(strSql, strSql.equals(
        "SELECT m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber,"
        + "insrel.rnumber AS rnumber,"
        + "pools.name AS name "
        + "FROM " + prefix + "m_images m_i," + prefix + "insrel insrel," + prefix + "pools pools "
        + "WHERE m_i.m_number IN (123,456) "
        + "AND ((m_i.m_number=insrel.m_dnumber AND pools.m_number=insrel.m_snumber) "
        + "OR (m_i.m_number=insrel.m_snumber AND pools.m_number=insrel.m_dnumber)) "
        + "AND (m_i.m_title='abd' AND m_i.m_number=123) "
        + "ORDER BY m_imageTitle ASC,name ASC,rnumber ASC"));
        
        // Add node constraint for second step (relation step).
        step2.addNode(789);
        strSql = instance.toSql(query, instance);
        assert(strSql, strSql.equals(
        "SELECT m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber,"
        + "insrel.rnumber AS rnumber,"
        + "pools.name AS name "
        + "FROM " + prefix + "m_images m_i," + prefix + "insrel insrel," + prefix + "pools pools "
        + "WHERE m_i.m_number IN (123,456) AND insrel.m_number IN (789) "
        + "AND ((m_i.m_number=insrel.m_dnumber AND pools.m_number=insrel.m_snumber) "
        + "OR (m_i.m_number=insrel.m_snumber AND pools.m_number=insrel.m_dnumber)) "
        + "AND (m_i.m_title='abd' AND m_i.m_number=123) "
        + "ORDER BY m_imageTitle ASC,name ASC,rnumber ASC"));
    }
    
    
    /** Test of appendQueryBodyToSql method, of class org.mmbase.storage.search.implementation.database.BasicSqlHandler. */
    public void testAppendQueryBodyToSql() throws Exception {
        BasicSearchQuery query = new BasicSearchQuery();
        
        FieldDefs imagesTitle = images.getField("title");
        FieldDefs insrelRNumber = insrel.getField("rnumber");
        FieldDefs poolsName = pools.getField("name");
        StringBuffer sb = new StringBuffer();
        BasicStep step1 = query.addStep(images);
        
        // Query with one step (default alias) and one field (default alias).
        BasicStepField field1a = query.addField(step1, imagesTitle);
        instance.appendQueryBodyToSql(sb, query, instance);
        String strSql = sb.toString();
        assert(strSql, strSql.equals(
        "m_images.m_title AS m_title "
        + "FROM " + prefix + "m_images m_images"));
        
        // Set step alias.
        step1.setAlias("i");
        sb.setLength(0);
        instance.appendQueryBodyToSql(sb, query, instance);
        strSql = sb.toString();
        assert(strSql, strSql.equals(
        "m_i.m_title AS m_title "
        + "FROM " + prefix + "m_images m_i"));
        
        // Set field alias.
        field1a.setAlias("imageTitle");
        sb.setLength(0);
        instance.appendQueryBodyToSql(sb, query, instance);
        strSql = sb.toString();
        assert(strSql, strSql.equals(
        "m_i.m_title AS m_imageTitle "
        + "FROM " + prefix + "m_images m_i"));
        
        // Add second field (default alias).
        FieldDefs imagesNumber = images.getField("number");
        BasicStepField field1b = query.addField(step1, imagesNumber);
        sb.setLength(0);
        instance.appendQueryBodyToSql(sb, query, instance);
        strSql = sb.toString();
        assert(strSql, strSql.equals(
        "m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS m_number "
        + "FROM " + prefix + "m_images m_i"));
        
        // Set alias for second field.
        field1b.setAlias("imageNumber");
        sb.setLength(0);
        instance.appendQueryBodyToSql(sb, query, instance);
        strSql = sb.toString();
        assert(strSql, strSql.equals(
        "m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber "
        + "FROM " + prefix + "m_images m_i"));
        
        // Set distinct true.
        query.setDistinct(true);
        sb.setLength(0);
        instance.appendQueryBodyToSql(sb, query, instance);
        strSql = sb.toString();
        assert(strSql, strSql.equals(
        "m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber "
        + "FROM " + prefix + "m_images m_i"));
        
        // Add sortorder (default direction).
        BasicSortOrder sortOrder1a = query.addSortOrder(field1a);
        sb.setLength(0);
        instance.appendQueryBodyToSql(sb, query, instance);
        strSql = sb.toString();
        assert(strSql, strSql.equals(
        "m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber "
        + "FROM " + prefix + "m_images m_i "
        + "ORDER BY m_imageTitle ASC"));
        
        // Set constraint.
        Constraint constraint1 = new BasicFieldValueConstraint(field1a, "abd");
        query.setConstraint(constraint1);
        sb.setLength(0);
        instance.appendQueryBodyToSql(sb, query, instance);
        strSql = sb.toString();
        assert(strSql, strSql.equals(
        "m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber "
        + "FROM " + prefix + "m_images m_i "
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
        sb.setLength(0);
        instance.appendQueryBodyToSql(sb, query, instance);
        strSql = sb.toString();
        assert(strSql, strSql.equals(
        "m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber "
        + "FROM " + prefix + "m_images m_i "
        + "WHERE m_i.m_title='abd' AND m_i.m_number=123 "
        + "ORDER BY m_imageTitle ASC"));
        
        // Set sortorder direction.
        sortOrder1a.setDirection(SortOrder.ORDER_DESCENDING);
        sb.setLength(0);
        instance.appendQueryBodyToSql(sb, query, instance);
        strSql = sb.toString();
        assert(strSql, strSql.equals(
        "m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber "
        + "FROM " + prefix + "m_images m_i "
        + "WHERE m_i.m_title='abd' AND m_i.m_number=123 "
        + "ORDER BY m_imageTitle DESC"));
        
        // Set sortorder direction.
        sortOrder1a.setDirection(SortOrder.ORDER_ASCENDING);
        sb.setLength(0);
        instance.appendQueryBodyToSql(sb, query, instance);
        strSql = sb.toString();
        assert(strSql, strSql.equals(
        "m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber "
        + "FROM " + prefix + "m_images m_i "
        + "WHERE m_i.m_title='abd' AND m_i.m_number=123 "
        + "ORDER BY m_imageTitle ASC"));
        
        // Set distinct false.
        query.setDistinct(false);
        sb.setLength(0);
        instance.appendQueryBodyToSql(sb, query, instance);
        strSql = sb.toString();
        assert(strSql, strSql.equals(
        "m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber "
        + "FROM " + prefix + "m_images m_i "
        + "WHERE m_i.m_title='abd' AND m_i.m_number=123 "
        + "ORDER BY m_imageTitle ASC"));
        
        // Add node constraint for first step (one node).
        step1.addNode(123);
        sb.setLength(0);
        instance.appendQueryBodyToSql(sb, query, instance);
        strSql = sb.toString();
        assert(strSql, strSql.equals(
        "m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber "
        + "FROM " + prefix + "m_images m_i "
        + "WHERE m_i.m_number IN (123) "
        + "AND (m_i.m_title='abd' AND m_i.m_number=123) "
        + "ORDER BY m_imageTitle ASC"));
        
        // Add second node to node constraint.
        step1.addNode(456);
        sb.setLength(0);
        instance.appendQueryBodyToSql(sb, query, instance);
        strSql = sb.toString();
        assert(strSql, strSql.equals(
        "m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber "
        + "FROM " + prefix + "m_images m_i "
        + "WHERE m_i.m_number IN (123,456) "
        + "AND (m_i.m_title='abd' AND m_i.m_number=123) "
        + "ORDER BY m_imageTitle ASC"));
        
        // Add relationstep (default directionality).
        BasicRelationStep step2 = query.addRelationStep(insrel,pools);
        BasicStep step3 = (BasicStep) step2.getNext();
        sb.setLength(0);
        instance.appendQueryBodyToSql(sb, query, instance);
        strSql = sb.toString();
        assert(strSql, strSql.equals(
        "m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber "
        + "FROM " + prefix + "m_images m_i," + prefix + "insrel insrel," + prefix + "pools pools "
        + "WHERE m_i.m_number IN (123,456) "
        + "AND ((m_i.m_number=insrel.m_dnumber AND pools.m_number=insrel.m_snumber) "
        + "OR (m_i.m_number=insrel.m_snumber AND pools.m_number=insrel.m_dnumber)) "
        + "AND (m_i.m_title='abd' AND m_i.m_number=123) "
        + "ORDER BY m_imageTitle ASC"));
        
        // Set directionality for relationstep to DESTINATION.
        step2.setDirectionality(RelationStep.DIRECTIONS_DESTINATION);
        sb.setLength(0);
        instance.appendQueryBodyToSql(sb, query, instance);
        strSql = sb.toString();
        assert(strSql, strSql.equals(
        "m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber "
        + "FROM " + prefix + "m_images m_i," + prefix + "insrel insrel," + prefix + "pools pools "
        + "WHERE m_i.m_number IN (123,456) "
        + "AND (m_i.m_number=insrel.m_snumber AND pools.m_number=insrel.m_dnumber) "
        + "AND (m_i.m_title='abd' AND m_i.m_number=123) "
        + "ORDER BY m_imageTitle ASC"));
        
        // Set directionality for relationstep to SOURCE.
        step2.setDirectionality(RelationStep.DIRECTIONS_SOURCE);
        sb.setLength(0);
        instance.appendQueryBodyToSql(sb, query, instance);
        strSql = sb.toString();
        assert(strSql, strSql.equals(
        "m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber "
        + "FROM " + prefix + "m_images m_i," + prefix + "insrel insrel," + prefix + "pools pools "
        + "WHERE m_i.m_number IN (123,456) "
        + "AND (m_i.m_number=insrel.m_dnumber AND pools.m_number=insrel.m_snumber) "
        + "AND (m_i.m_title='abd' AND m_i.m_number=123) "
        + "ORDER BY m_imageTitle ASC"));
        
        // Set directionality for relationstep to BOTH.
        step2.setDirectionality(RelationStep.DIRECTIONS_BOTH);
        sb.setLength(0);
        instance.appendQueryBodyToSql(sb, query, instance);
        strSql = sb.toString();
        assert(strSql, strSql.equals(
        "m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber "
        + "FROM " + prefix + "m_images m_i," + prefix + "insrel insrel," + prefix + "pools pools "
        + "WHERE m_i.m_number IN (123,456) "
        + "AND ((m_i.m_number=insrel.m_dnumber AND pools.m_number=insrel.m_snumber) "
        + "OR (m_i.m_number=insrel.m_snumber AND pools.m_number=insrel.m_dnumber)) "
        + "AND (m_i.m_title='abd' AND m_i.m_number=123) "
        + "ORDER BY m_imageTitle ASC"));
        
        // Add field for relationstep.
        StepField field2a = query.addField(step2, insrelRNumber);
        sb.setLength(0);
        instance.appendQueryBodyToSql(sb, query, instance);
        strSql = sb.toString();
        assert(strSql, strSql.equals(
        "m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber,"
        + "insrel.rnumber AS rnumber "
        + "FROM " + prefix + "m_images m_i," + prefix + "insrel insrel," + prefix + "pools pools "
        + "WHERE m_i.m_number IN (123,456) "
        + "AND ((m_i.m_number=insrel.m_dnumber AND pools.m_number=insrel.m_snumber) "
        + "OR (m_i.m_number=insrel.m_snumber AND pools.m_number=insrel.m_dnumber)) "
        + "AND (m_i.m_title='abd' AND m_i.m_number=123) "
        + "ORDER BY m_imageTitle ASC"));
        
        // Add field for third step.
        StepField field3a = query.addField(step3, poolsName);
        sb.setLength(0);
        instance.appendQueryBodyToSql(sb, query, instance);
        strSql = sb.toString();
        assert(strSql, strSql.equals(
        "m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber,"
        + "insrel.rnumber AS rnumber,"
        + "pools.name AS name "
        + "FROM " + prefix + "m_images m_i," + prefix + "insrel insrel," + prefix + "pools pools "
        + "WHERE m_i.m_number IN (123,456) "
        + "AND ((m_i.m_number=insrel.m_dnumber AND pools.m_number=insrel.m_snumber) "
        + "OR (m_i.m_number=insrel.m_snumber AND pools.m_number=insrel.m_dnumber)) "
        + "AND (m_i.m_title='abd' AND m_i.m_number=123) "
        + "ORDER BY m_imageTitle ASC"));
        
        // Add second sortorder
        BasicSortOrder sortOrder3a = query.addSortOrder(field3a);
        sb.setLength(0);
        instance.appendQueryBodyToSql(sb, query, instance);
        strSql = sb.toString();
        assert(strSql, strSql.equals(
        "m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber,"
        + "insrel.rnumber AS rnumber,"
        + "pools.name AS name "
        + "FROM " + prefix + "m_images m_i," + prefix + "insrel insrel," + prefix + "pools pools "
        + "WHERE m_i.m_number IN (123,456) "
        + "AND ((m_i.m_number=insrel.m_dnumber AND pools.m_number=insrel.m_snumber) "
        + "OR (m_i.m_number=insrel.m_snumber AND pools.m_number=insrel.m_dnumber)) "
        + "AND (m_i.m_title='abd' AND m_i.m_number=123) "
        + "ORDER BY m_imageTitle ASC,name ASC"));
        
        // Add third sortorder.
        BasicSortOrder sortOrder2a = query.addSortOrder(field2a);
        sb.setLength(0);
        instance.appendQueryBodyToSql(sb, query, instance);
        strSql = sb.toString();
        assert(strSql, strSql.equals(
        "m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber,"
        + "insrel.rnumber AS rnumber,"
        + "pools.name AS name "
        + "FROM " + prefix + "m_images m_i," + prefix + "insrel insrel," + prefix + "pools pools "
        + "WHERE m_i.m_number IN (123,456) "
        + "AND ((m_i.m_number=insrel.m_dnumber AND pools.m_number=insrel.m_snumber) "
        + "OR (m_i.m_number=insrel.m_snumber AND pools.m_number=insrel.m_dnumber)) "
        + "AND (m_i.m_title='abd' AND m_i.m_number=123) "
        + "ORDER BY m_imageTitle ASC,name ASC,rnumber ASC"));
        
        // Add node constraint for second step (relation step).
        step2.addNode(789);
        sb.setLength(0);
        instance.appendQueryBodyToSql(sb, query, instance);
        strSql = sb.toString();
        assert(strSql, strSql.equals(
        "m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber,"
        + "insrel.rnumber AS rnumber,"
        + "pools.name AS name "
        + "FROM " + prefix + "m_images m_i," + prefix + "insrel insrel," + prefix + "pools pools "
        + "WHERE m_i.m_number IN (123,456) AND insrel.m_number IN (789) "
        + "AND ((m_i.m_number=insrel.m_dnumber AND pools.m_number=insrel.m_snumber) "
        + "OR (m_i.m_number=insrel.m_snumber AND pools.m_number=insrel.m_dnumber)) "
        + "AND (m_i.m_title='abd' AND m_i.m_number=123) "
        + "ORDER BY m_imageTitle ASC,name ASC,rnumber ASC"));
    }
    
    /** Test of appendConstraintToSql method, of class org.mmbase.storage.search.implementation.database.BasicSqlHandler. */
    public void testAppendConstraintToSql() {
        
        BasicSearchQuery query = new BasicSearchQuery();
        StringBuffer sb = new StringBuffer();
        Step step1 = query.addStep(images);
        FieldDefs imagesTitle = images.getField("title");
        StepField field1 = query.addField(step1, imagesTitle);
        FieldDefs imagesNumber = images.getField("number");
        StepField field2 = query.addField(step1, imagesNumber);
        Step step2 = query.addStep(pools);
        FieldDefs poolsNumber = pools.getField("number");
        StepField field3 = query.addField(step2, poolsNumber);
        
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
            // Composite constraint, should throw IllegalArgumentException.
            instance.appendConstraintToSql(sb, constraint9, query, false, false);
            fail("Composite constraint, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
    }
    
    /** Test of getSupportLevel(int,SearchQuery), of class org.mmbase.storage.search.implementation.database.BasicSqlHandler. */
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
    
    /** Test of getSupportLevel(Constraint,SearchQuery), of class org.mmbase.storage.search.implementation.database.BasicSqlHandler. */
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
    
    /** Test of getAllowedValue method, of class org.mmbase.storage.search.implementation.database.BasicSqlHandler. */
    public void testGetAllowedValue() {
        Set entries = disallowedValues.entrySet();
        Iterator iEntries = entries.iterator();
        while (iEntries.hasNext()) {
            Map.Entry entry = (Map.Entry) iEntries.next();
            String disallowedValue = (String) entry.getKey();
            String allowedValue = (String) entry.getValue();
            
            // Disallowed value.
            assert(instance.getAllowedValue(disallowedValue).equals(allowedValue));
            
            // Allowed values.
            assert(instance.getAllowedValue(allowedValue).equals(allowedValue));
            allowedValue += "_must_be_allowed_as_well";
            assert(instance.getAllowedValue(allowedValue).equals(allowedValue));
        }
        
        try {
            // Null value, shoul throw IllegalArgumentException.
            instance.getAllowedValue(null);
            fail("Null value, shoul throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
    }
    
    /** Test of appendCompositeConstraintToSql method, of class org.mmbase.storage.search.implementation.database.BasicSqlHandler. */
    public void testAppendCompositeConstraintToSql() throws Exception {
        BasicSearchQuery query = new BasicSearchQuery();
        StringBuffer sb = new StringBuffer();
        Step step1 = query.addStep(images);
        FieldDefs imagesNumber = images.getField("number");
        StepField field1 = query.addField(step1, imagesNumber);
        Step step2 = query.addStep(pools);
        FieldDefs poolsNumber = pools.getField("number");
        StepField field2 = query.addField(step2, poolsNumber);
        
        BasicFieldValueConstraint constraint1 
        = new BasicFieldValueConstraint(field1, new Integer(9876));
        constraint1.setOperator(FieldCompareConstraint.LESS);
        constraint1.setOperator(FieldCompareConstraint.GREATER);
        constraint1.setInverse(true); // set inverse
        
        BasicCompareFieldsConstraint constraint2
        = new BasicCompareFieldsConstraint(field1, field2);
        constraint2.setOperator(FieldCompareConstraint.GREATER);
        constraint2.setInverse(true); // set inverse

        // Test for BasicCompareFieldsConstraint
        BasicCompositeConstraint compositeConstraint =
        new BasicCompositeConstraint(CompositeConstraint.LOGICAL_AND);
        
        try {
            // Empty composite constraint, should throw IllegalStateException.
            instance.appendCompositeConstraintToSql(sb, (CompositeConstraint) compositeConstraint, 
            query, false, false, instance);
            fail("Empty composite constraint, should throw IllegalStateException.");
        } catch (IllegalStateException e) {}
        
        sb.setLength(0);
        compositeConstraint.addChild(constraint2); // Add first child constraint.
        instance.appendCompositeConstraintToSql(sb, (CompositeConstraint) compositeConstraint, 
        query, false, false, instance);
        assert(sb.toString(), sb.toString().equals(
        "NOT m_images.m_number>pools.m_number"));
        
        sb.setLength(0);
        instance.appendCompositeConstraintToSql(sb, (CompositeConstraint) compositeConstraint, 
        query, true, false, instance);
        assert(sb.toString(), sb.toString().equals(
        "m_images.m_number>pools.m_number"));
        
        sb.setLength(0);
        instance.appendCompositeConstraintToSql(sb, (CompositeConstraint) compositeConstraint, 
        query, true, true, instance);
        assert(sb.toString(), sb.toString().equals(
        "m_images.m_number>pools.m_number"));
        
        sb.setLength(0);
        compositeConstraint.addChild(constraint1); // Add second child constraint.
        instance.appendCompositeConstraintToSql(sb, (CompositeConstraint) compositeConstraint, 
        query, false, false, instance);
        assert(sb.toString(), sb.toString().equals(
        "NOT m_images.m_number>pools.m_number AND NOT m_images.m_number>9876"));
        
        sb.setLength(0);
        instance.appendCompositeConstraintToSql(sb, (CompositeConstraint) compositeConstraint, 
        query, true, false, instance);
        assert(sb.toString(), sb.toString().equals(
        "m_images.m_number>pools.m_number OR m_images.m_number>9876"));
        
        sb.setLength(0);
        instance.appendCompositeConstraintToSql(sb, (CompositeConstraint) compositeConstraint, 
        query, true, true, instance);
        assert(sb.toString(), sb.toString().equals(
        "(m_images.m_number>pools.m_number OR m_images.m_number>9876)"));
        
        sb.setLength(0);
        instance.appendCompositeConstraintToSql(sb, (CompositeConstraint) compositeConstraint, 
        query, false, true, instance);
         assert(sb.toString(), sb.toString().equals(
        "(NOT m_images.m_number>pools.m_number AND NOT m_images.m_number>9876)"));
        
        sb.setLength(0);
        constraint1.setInverse(false); // Set second child not inverse.
        instance.appendCompositeConstraintToSql(sb, (CompositeConstraint) compositeConstraint, 
        query, false, false, instance);
         assert(sb.toString(), sb.toString().equals(
        "NOT m_images.m_number>pools.m_number AND m_images.m_number>9876"));
        
        sb.setLength(0);
        instance.appendCompositeConstraintToSql(sb, (CompositeConstraint) compositeConstraint, 
        query, true, false, instance);
         assert(sb.toString(), sb.toString().equals(
        "m_images.m_number>pools.m_number OR NOT m_images.m_number>9876"));
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(BasicSqlHandlerTest.class);
        
        return suite;
    }
    
}
