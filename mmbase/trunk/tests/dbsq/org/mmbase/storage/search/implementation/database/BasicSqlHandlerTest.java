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
 * @version $Revision: 1.4 $
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
    
    /** News builder, used as builder example. */
    private MMObjectBuilder news = null;
    
    /** Insrel builder, used as relation builder example. */
    private InsRel insrel = null;
    
    public BasicSqlHandlerTest(java.lang.String testName) {
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
    
    /** Test of init method, of class org.mmbase.storage.search.implementation.database.BasicSqlHandler. */
    public void testInit() throws Exception {
        // Same as:
        testToSqlString();
        testToSql();
    }
    
    /** Test of toSqlString method, of class org.mmbase.storage.search.implementation.database.BasicSqlHandler. */
    public void testToSqlString() throws Exception {
        assertTrue(BasicSqlHandler.toSqlString(null) == null);
        assertTrue(BasicSqlHandler.toSqlString("'").equals("''"));
        assertTrue(BasicSqlHandler.toSqlString("'''''").equals("''''''''''"));
        assertTrue(BasicSqlHandler.toSqlString("AsDf'").equals("AsDf''"));
        assertTrue(BasicSqlHandler.toSqlString("AsDf'jkl").equals("AsDf''jkl"));
        assertTrue(BasicSqlHandler.toSqlString("AsDf'jkl'").equals("AsDf''jkl''"));
        assertTrue(BasicSqlHandler.toSqlString("'AsDf'jkl").equals("''AsDf''jkl"));
        assertTrue(BasicSqlHandler.toSqlString("qwerty").equals("qwerty"));
    }
    
    /** Test of toSql method, of class org.mmbase.storage.search.implementation.database.BasicSqlHandler. */
    public void testToSql() throws Exception {
        BasicSearchQuery query = new BasicSearchQuery();
        
       // Query without step, should throw IllegalStateException.
        try {
            instance.toSql(query, instance);
            fail("Query without step, should throw IllegalStateException.");
        } catch (IllegalStateException e) {};
        
        BasicStep step1 = query.addStep(images).setAlias(null);
        
        // Query without field, should throw IllegalStateException.
        try {
            instance.toSql(query, instance);
            fail("Query without field, should throw IllegalStateException.");
        } catch (IllegalStateException e) {};
        
        FieldDefs imagesTitle = images.getField("title");
        FieldDefs insrelRNumber = insrel.getField("rnumber");
        FieldDefs newsTitle = news.getField("title");
        
        // Query with one step (default alias) and one field (default alias).
        BasicStepField field1a 
            = query.addField(step1, imagesTitle).setAlias(null);
        String strSql = instance.toSql(query, instance);
        assertTrue(strSql, strSql.equals(
        "SELECT m_title "
        + "FROM " + prefix + "images m_images"));
        
        // Set step alias.
        step1.setAlias("i");
        strSql = instance.toSql(query, instance);
        assertTrue(strSql, strSql.equals(
        "SELECT m_title "
        + "FROM " + prefix + "images m_i"));
        
        // Set field alias.
        field1a.setAlias("imageTitle");
        strSql = instance.toSql(query, instance);
        assertTrue(strSql, strSql.equals(
        "SELECT m_title AS m_imageTitle "
        + "FROM " + prefix + "images m_i"));
        
        // Add second field (null alias).
        FieldDefs imagesNumber = images.getField("number");
        BasicStepField field1b 
            = query.addField(step1, imagesNumber).setAlias(null);
        strSql = instance.toSql(query, instance);
        assertTrue(strSql, strSql.equals(
        "SELECT m_title AS m_imageTitle,"
        + "m_number "
        + "FROM " + prefix + "images m_i"));
        
        // Set alias for second field.
        field1b.setAlias("imageNumber");
        strSql = instance.toSql(query, instance);
        assertTrue(strSql, strSql.equals(
        "SELECT m_title AS m_imageTitle,"
        + "m_number AS imageNumber "
        + "FROM " + prefix + "images m_i"));
        
        // Set distinct true.
        query.setDistinct(true);
        strSql = instance.toSql(query, instance);
        assertTrue(strSql, strSql.equals(
        "SELECT DISTINCT m_title AS m_imageTitle,"
        + "m_number AS imageNumber "
        + "FROM " + prefix + "images m_i"));
        
        // Add sortorder (default direction).
        BasicSortOrder sortOrder1a = query.addSortOrder(field1a);
        strSql = instance.toSql(query, instance);
        assertTrue(strSql, strSql.equals(
        "SELECT DISTINCT m_title AS m_imageTitle,"
        + "m_number AS imageNumber "
        + "FROM " + prefix + "images m_i "
        + "ORDER BY m_title ASC"));
        
        // Set constraint.
        Constraint constraint1 = new BasicFieldValueConstraint(field1a, "abd");
        query.setConstraint(constraint1);
        strSql = instance.toSql(query, instance);
        assertTrue(strSql, strSql.equals(
        "SELECT DISTINCT m_title AS m_imageTitle,"
        + "m_number AS imageNumber "
        + "FROM " + prefix + "images m_i "
        + "WHERE m_title='abd' "
        + "ORDER BY m_title ASC"));
        
        // Set composite constraint.
        Constraint constraint2 
        = new BasicFieldValueConstraint(field1b, new Integer(123));
        BasicCompositeConstraint constraint3 
            = new BasicCompositeConstraint(CompositeConstraint.LOGICAL_AND)
                .addChild(constraint1)
                .addChild(constraint2);
        query.setConstraint(constraint3);
        strSql = instance.toSql(query, instance);
        assertTrue(strSql, strSql.equals(
        "SELECT DISTINCT m_title AS m_imageTitle,"
        + "m_number AS imageNumber "
        + "FROM " + prefix + "images m_i "
        + "WHERE m_title='abd' AND m_number=123 "
        + "ORDER BY m_title ASC"));
        
        // Set sortorder direction.
        sortOrder1a.setDirection(SortOrder.ORDER_DESCENDING);
        strSql = instance.toSql(query, instance);
        assertTrue(strSql, strSql.equals(
        "SELECT DISTINCT m_title AS m_imageTitle,"
        + "m_number AS imageNumber "
        + "FROM " + prefix + "images m_i "
        + "WHERE m_title='abd' AND m_number=123 "
        + "ORDER BY m_title DESC"));
        
        // Set sortorder direction.
        sortOrder1a.setDirection(SortOrder.ORDER_ASCENDING);
        strSql = instance.toSql(query, instance);
        assertTrue(strSql, strSql.equals(
        "SELECT DISTINCT m_title AS m_imageTitle,"
        + "m_number AS imageNumber "
        + "FROM " + prefix + "images m_i "
        + "WHERE m_title='abd' AND m_number=123 "
        + "ORDER BY m_title ASC"));
        
        // Set distinct false.
        query.setDistinct(false);
        strSql = instance.toSql(query, instance);
        assertTrue(strSql, strSql.equals(
        "SELECT m_title AS m_imageTitle,"
        + "m_number AS imageNumber "
        + "FROM " + prefix + "images m_i "
        + "WHERE m_title='abd' AND m_number=123 "
        + "ORDER BY m_title ASC"));
        
        // Add node constraint for first step (one node).
        step1.addNode(123);
        strSql = instance.toSql(query, instance);
        assertTrue(strSql, strSql.equals(
        "SELECT m_title AS m_imageTitle,"
        + "m_number AS imageNumber "
        + "FROM " + prefix + "images m_i "
        + "WHERE m_number IN (123) "
        + "AND (m_title='abd' AND m_number=123) "
        + "ORDER BY m_title ASC"));
        
        // Add second node to node constraint.
        step1.addNode(456);
        strSql = instance.toSql(query, instance);
        assertTrue(strSql, strSql.equals(
        "SELECT m_title AS m_imageTitle,"
        + "m_number AS imageNumber "
        + "FROM " + prefix + "images m_i "
        + "WHERE m_number IN (123,456) "
        + "AND (m_title='abd' AND m_number=123) "
        + "ORDER BY m_title ASC"));
        
        // Add relationstep (default directionality).
        BasicRelationStep step2 = query.addRelationStep(insrel,news);
        BasicStep step3 = (BasicStep) step2.getNext();
        strSql = instance.toSql(query, instance);
        assertTrue(strSql, strSql.equals(
        "SELECT m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber "
        + "FROM " + prefix + "images m_i," + prefix + "insrel insrel," 
        + prefix + "news news "
        + "WHERE m_i.m_number IN (123,456) "
        + "AND ((m_i.m_number=insrel.m_dnumber AND news.m_number=insrel.m_snumber) "
        + "OR (m_i.m_number=insrel.m_snumber AND news.m_number=insrel.m_dnumber)) "
        + "AND (m_i.m_title='abd' AND m_i.m_number=123) "
        + "ORDER BY m_i.m_title ASC"));
        
        // Set aliases on step2 and stap3.
        step2.setAlias("insrel");
        step3.setAlias("news");
        
        // Set role.
        step2.setRole(new Integer(890));
        strSql = instance.toSql(query, instance);
        assertTrue(strSql, strSql.equals(
        "SELECT m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber "
        + "FROM " + prefix + "images m_i," + prefix + "insrel insrel," + prefix + "news news "
        + "WHERE m_i.m_number IN (123,456) "
        + "AND (((m_i.m_number=insrel.m_dnumber AND news.m_number=insrel.m_snumber) "
        + "OR (m_i.m_number=insrel.m_snumber AND news.m_number=insrel.m_dnumber)) "
        + "AND insrel.rnumber=890) "
        + "AND (m_i.m_title='abd' AND m_i.m_number=123) "
        + "ORDER BY m_i.m_title ASC"));
        
        // Set directionality for relationstep to DESTINATION.
        step2.setDirectionality(RelationStep.DIRECTIONS_DESTINATION);
        strSql = instance.toSql(query, instance);
        assertTrue(strSql, strSql.equals(
        "SELECT m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber "
        + "FROM " + prefix + "images m_i," + prefix + "insrel insrel," + prefix + "news news "
        + "WHERE m_i.m_number IN (123,456) "
        + "AND (m_i.m_number=insrel.m_snumber AND news.m_number=insrel.m_dnumber "
        + "AND insrel.rnumber=890) "
        + "AND (m_i.m_title='abd' AND m_i.m_number=123) "
        + "ORDER BY m_i.m_title ASC"));
        
        // Set checkedDirectionality to true.
        step2.setCheckedDirectionality(true);
        strSql = instance.toSql(query, instance);
        assertTrue(strSql, strSql.equals(
        "SELECT m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber "
        + "FROM " + prefix + "images m_i," + prefix + "insrel insrel," + prefix + "news news "
        + "WHERE m_i.m_number IN (123,456) "
        + "AND (m_i.m_number=insrel.m_snumber AND news.m_number=insrel.m_dnumber "
        + "AND insrel.rnumber=890) "
        + "AND (m_i.m_title='abd' AND m_i.m_number=123) "
        + "ORDER BY m_i.m_title ASC"));
        
        // Set directionality for relationstep to SOURCE,
        // set checkedDirectionality to false.
        step2.setDirectionality(RelationStep.DIRECTIONS_SOURCE);
        step2.setCheckedDirectionality(false);
        strSql = instance.toSql(query, instance);
        assertTrue(strSql, strSql.equals(
        "SELECT m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber "
        + "FROM " + prefix + "images m_i," + prefix + "insrel insrel," + prefix + "news news "
        + "WHERE m_i.m_number IN (123,456) "
        + "AND (m_i.m_number=insrel.m_dnumber AND news.m_number=insrel.m_snumber "
        + "AND insrel.rnumber=890) "
        + "AND (m_i.m_title='abd' AND m_i.m_number=123) "
        + "ORDER BY m_i.m_title ASC"));
        
        // Set checkedDirectionality to true.
        step2.setCheckedDirectionality(true);
        strSql = instance.toSql(query, instance);
        assertTrue(strSql, strSql.equals(
        "SELECT m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber "
        + "FROM " + prefix + "images m_i," + prefix + "insrel insrel," + prefix + "news news "
        + "WHERE m_i.m_number IN (123,456) "
        + "AND (m_i.m_number=insrel.m_dnumber AND news.m_number=insrel.m_snumber "
        + "AND insrel.dir<>1 "
        + "AND insrel.rnumber=890) "
        + "AND (m_i.m_title='abd' AND m_i.m_number=123) "
        + "ORDER BY m_i.m_title ASC"));
        
        // Set directionality for relationstep to BOTH, 
        // set checkedDirectionality to false.
        step2.setDirectionality(RelationStep.DIRECTIONS_BOTH);
        step2.setCheckedDirectionality(false);
        strSql = instance.toSql(query, instance);
        assertTrue(strSql, strSql.equals(
        "SELECT m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber "
        + "FROM " + prefix + "images m_i," + prefix + "insrel insrel," + prefix + "news news "
        + "WHERE m_i.m_number IN (123,456) "
        + "AND (((m_i.m_number=insrel.m_dnumber AND news.m_number=insrel.m_snumber) "
        + "OR (m_i.m_number=insrel.m_snumber AND news.m_number=insrel.m_dnumber)) "
        + "AND insrel.rnumber=890) "
        + "AND (m_i.m_title='abd' AND m_i.m_number=123) "
        + "ORDER BY m_i.m_title ASC"));
        
        // Set checkedDirectionality to true.
        step2.setCheckedDirectionality(true);
        strSql = instance.toSql(query, instance);
        assertTrue(strSql, strSql.equals(
        "SELECT m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber "
        + "FROM " + prefix + "images m_i," + prefix + "insrel insrel," + prefix + "news news "
        + "WHERE m_i.m_number IN (123,456) "
        + "AND (((m_i.m_number=insrel.m_dnumber AND news.m_number=insrel.m_snumber "
        + "AND insrel.dir<>1) "
        + "OR (m_i.m_number=insrel.m_snumber AND news.m_number=insrel.m_dnumber)) "
        + "AND insrel.rnumber=890) "
        + "AND (m_i.m_title='abd' AND m_i.m_number=123) "
        + "ORDER BY m_i.m_title ASC"));
        
        // Reset role and checkedDirectionality.
        step2.setRole(null);
        step2.setCheckedDirectionality(false);
        strSql = instance.toSql(query, instance);
        assertTrue(strSql, strSql.equals(
        "SELECT m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber "
        + "FROM " + prefix + "images m_i," + prefix + "insrel insrel," + prefix + "news news "
        + "WHERE m_i.m_number IN (123,456) "
        + "AND ((m_i.m_number=insrel.m_dnumber AND news.m_number=insrel.m_snumber) "
        + "OR (m_i.m_number=insrel.m_snumber AND news.m_number=insrel.m_dnumber)) "
        + "AND (m_i.m_title='abd' AND m_i.m_number=123) "
        + "ORDER BY m_i.m_title ASC"));
        
        // Add field for relationstep.
        StepField field2a = query.addField(step2, insrelRNumber).setAlias(null);
        strSql = instance.toSql(query, instance);
        assertTrue(strSql, strSql.equals(
        "SELECT m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber,"
        + "insrel.rnumber "
        + "FROM " + prefix + "images m_i," + prefix + "insrel insrel," + prefix + "news news "
        + "WHERE m_i.m_number IN (123,456) "
        + "AND ((m_i.m_number=insrel.m_dnumber AND news.m_number=insrel.m_snumber) "
        + "OR (m_i.m_number=insrel.m_snumber AND news.m_number=insrel.m_dnumber)) "
        + "AND (m_i.m_title='abd' AND m_i.m_number=123) "
        + "ORDER BY m_i.m_title ASC"));
        
        // Add field for third step.
        StepField field3a = query.addField(step3, newsTitle).setAlias(null);
        strSql = instance.toSql(query, instance);
        assertTrue(strSql, strSql.equals(
        "SELECT m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber,"
        + "insrel.rnumber,"
        + "news.m_title "
        + "FROM " + prefix + "images m_i," + prefix + "insrel insrel," + prefix + "news news "
        + "WHERE m_i.m_number IN (123,456) "
        + "AND ((m_i.m_number=insrel.m_dnumber AND news.m_number=insrel.m_snumber) "
        + "OR (m_i.m_number=insrel.m_snumber AND news.m_number=insrel.m_dnumber)) "
        + "AND (m_i.m_title='abd' AND m_i.m_number=123) "
        + "ORDER BY m_i.m_title ASC"));
        
        // Add second sortorder
        BasicSortOrder sortOrder3a = query.addSortOrder(field3a);
        strSql = instance.toSql(query, instance);
        assertTrue(strSql, strSql.equals(
        "SELECT m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber,"
        + "insrel.rnumber,"
        + "news.m_title "
        + "FROM " + prefix + "images m_i," + prefix + "insrel insrel," + prefix + "news news "
        + "WHERE m_i.m_number IN (123,456) "
        + "AND ((m_i.m_number=insrel.m_dnumber AND news.m_number=insrel.m_snumber) "
        + "OR (m_i.m_number=insrel.m_snumber AND news.m_number=insrel.m_dnumber)) "
        + "AND (m_i.m_title='abd' AND m_i.m_number=123) "
        + "ORDER BY m_i.m_title ASC,news.m_title ASC"));
        
        // Add third sortorder.
        BasicSortOrder sortOrder2a = query.addSortOrder(field2a);
        strSql = instance.toSql(query, instance);
        assertTrue(strSql, strSql.equals(
        "SELECT m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber,"
        + "insrel.rnumber,"
        + "news.m_title "
        + "FROM " + prefix + "images m_i," + prefix + "insrel insrel," + prefix + "news news "
        + "WHERE m_i.m_number IN (123,456) "
        + "AND ((m_i.m_number=insrel.m_dnumber AND news.m_number=insrel.m_snumber) "
        + "OR (m_i.m_number=insrel.m_snumber AND news.m_number=insrel.m_dnumber)) "
        + "AND (m_i.m_title='abd' AND m_i.m_number=123) "
        + "ORDER BY m_i.m_title ASC,news.m_title ASC,insrel.rnumber ASC"));
        
        // Add node constraint for second step (relation step).
        step2.addNode(789);
        strSql = instance.toSql(query, instance);
        assertTrue(strSql, strSql.equals(
        "SELECT m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber,"
        + "insrel.rnumber,"
        + "news.m_title "
        + "FROM " + prefix + "images m_i," + prefix + "insrel insrel," + prefix + "news news "
        + "WHERE m_i.m_number IN (123,456) AND insrel.m_number IN (789) "
        + "AND ((m_i.m_number=insrel.m_dnumber AND news.m_number=insrel.m_snumber) "
        + "OR (m_i.m_number=insrel.m_snumber AND news.m_number=insrel.m_dnumber)) "
        + "AND (m_i.m_title='abd' AND m_i.m_number=123) "
        + "ORDER BY m_i.m_title ASC,news.m_title ASC,insrel.rnumber ASC"));
        
        // Aggregated query.
        query = new BasicSearchQuery(true);
        step1 = query.addStep(images).setAlias(null);
        BasicAggregatedField field4a 
            = (BasicAggregatedField) query.addAggregatedField(
                step1, imagesTitle, AggregatedField.AGGREGATION_TYPE_COUNT)
                    .setAlias(null);
        strSql = instance.toSql(query, instance);
        assertTrue(strSql, strSql.equals(
        "SELECT COUNT(m_title) "
        + "FROM " + prefix + "images m_images"));
        
        // Distinct keyword avoided in aggregating query.
        query.setDistinct(true);
        strSql = instance.toSql(query, instance);
        assertTrue(strSql, strSql.equals(
        "SELECT COUNT(m_title) "
        + "FROM " + prefix + "images m_images"));
    }
    
    
    /** Test of appendQueryBodyToSql method, of class org.mmbase.storage.search.implementation.database.BasicSqlHandler. */
    public void testAppendQueryBodyToSql() throws Exception {
        BasicSearchQuery query = new BasicSearchQuery();
        
        FieldDefs imagesTitle = images.getField("title");
        FieldDefs insrelRNumber = insrel.getField("rnumber");
        FieldDefs newsTitle = news.getField("title");
        StringBuffer sb = new StringBuffer();
        BasicStep step1 = query.addStep(images).setAlias(null);
        
        // Query with one step (null alias) and one field (null alias).
        BasicStepField field1a 
            = query.addField(step1, imagesTitle).setAlias(null);
        instance.appendQueryBodyToSql(sb, query, instance);
        String strSql = sb.toString();
        assertTrue(strSql, strSql.equals(
        "m_title "
        + "FROM " + prefix + "images m_images"));
        
        // Set step alias.
        step1.setAlias("i");
        sb.setLength(0);
        instance.appendQueryBodyToSql(sb, query, instance);
        strSql = sb.toString();
        assertTrue(strSql, strSql.equals(
        "m_title "
        + "FROM " + prefix + "images m_i"));
        
        // Set field alias.
        field1a.setAlias("imageTitle");
        sb.setLength(0);
        instance.appendQueryBodyToSql(sb, query, instance);
        strSql = sb.toString();
        assertTrue(strSql, strSql.equals(
        "m_title AS m_imageTitle "
        + "FROM " + prefix + "images m_i"));
        
        // Add second field (default alias).
        FieldDefs imagesNumber = images.getField("number");
        BasicStepField field1b 
            = query.addField(step1, imagesNumber).setAlias(null);
        sb.setLength(0);
        instance.appendQueryBodyToSql(sb, query, instance);
        strSql = sb.toString();
        assertTrue(strSql, strSql.equals(
        "m_title AS m_imageTitle,"
        + "m_number "
        + "FROM " + prefix + "images m_i"));
        
        // Set alias for second field.
        field1b.setAlias("imageNumber");
        sb.setLength(0);
        instance.appendQueryBodyToSql(sb, query, instance);
        strSql = sb.toString();
        assertTrue(strSql, strSql.equals(
        "m_title AS m_imageTitle,"
        + "m_number AS imageNumber "
        + "FROM " + prefix + "images m_i"));
        
        // Set distinct true.
        query.setDistinct(true);
        sb.setLength(0);
        instance.appendQueryBodyToSql(sb, query, instance);
        strSql = sb.toString();
        assertTrue(strSql, strSql.equals(
        "m_title AS m_imageTitle,"
        + "m_number AS imageNumber "
        + "FROM " + prefix + "images m_i"));
        
        // Add sortorder (default direction).
        BasicSortOrder sortOrder1a = query.addSortOrder(field1a);
        sb.setLength(0);
        instance.appendQueryBodyToSql(sb, query, instance);
        strSql = sb.toString();
        assertTrue(strSql, strSql.equals(
        "m_title AS m_imageTitle,"
        + "m_number AS imageNumber "
        + "FROM " + prefix + "images m_i "
        + "ORDER BY m_title ASC"));
        
        // Set constraint.
        Constraint constraint1 = new BasicFieldValueConstraint(field1a, "abd");
        query.setConstraint(constraint1);
        sb.setLength(0);
        instance.appendQueryBodyToSql(sb, query, instance);
        strSql = sb.toString();
        assertTrue(strSql, strSql.equals(
        "m_title AS m_imageTitle,"
        + "m_number AS imageNumber "
        + "FROM " + prefix + "images m_i "
        + "WHERE m_title='abd' "
        + "ORDER BY m_title ASC"));
        
        // Set composite constraint.
        Constraint constraint2 
        = new BasicFieldValueConstraint(field1b, new Integer(123));
        BasicCompositeConstraint constraint3 
            = new BasicCompositeConstraint(CompositeConstraint.LOGICAL_AND)
                .addChild(constraint1)
                .addChild(constraint2);
        query.setConstraint(constraint3);
        sb.setLength(0);
        instance.appendQueryBodyToSql(sb, query, instance);
        strSql = sb.toString();
        assertTrue(strSql, strSql.equals(
        "m_title AS m_imageTitle,"
        + "m_number AS imageNumber "
        + "FROM " + prefix + "images m_i "
        + "WHERE m_title='abd' AND m_number=123 "
        + "ORDER BY m_title ASC"));
        
        // Set sortorder direction.
        sortOrder1a.setDirection(SortOrder.ORDER_DESCENDING);
        sb.setLength(0);
        instance.appendQueryBodyToSql(sb, query, instance);
        strSql = sb.toString();
        assertTrue(strSql, strSql.equals(
        "m_title AS m_imageTitle,"
        + "m_number AS imageNumber "
        + "FROM " + prefix + "images m_i "
        + "WHERE m_title='abd' AND m_number=123 "
        + "ORDER BY m_title DESC"));
        
        // Set sortorder direction.
        sortOrder1a.setDirection(SortOrder.ORDER_ASCENDING);
        sb.setLength(0);
        instance.appendQueryBodyToSql(sb, query, instance);
        strSql = sb.toString();
        assertTrue(strSql, strSql.equals(
        "m_title AS m_imageTitle,"
        + "m_number AS imageNumber "
        + "FROM " + prefix + "images m_i "
        + "WHERE m_title='abd' AND m_number=123 "
        + "ORDER BY m_title ASC"));
        
        // Set distinct false.
        query.setDistinct(false);
        sb.setLength(0);
        instance.appendQueryBodyToSql(sb, query, instance);
        strSql = sb.toString();
        assertTrue(strSql, strSql.equals(
        "m_title AS m_imageTitle,"
        + "m_number AS imageNumber "
        + "FROM " + prefix + "images m_i "
        + "WHERE m_title='abd' AND m_number=123 "
        + "ORDER BY m_title ASC"));
        
        // Add node constraint for first step (one node).
        step1.addNode(123);
        sb.setLength(0);
        instance.appendQueryBodyToSql(sb, query, instance);
        strSql = sb.toString();
        assertTrue(strSql, strSql.equals(
        "m_title AS m_imageTitle,"
        + "m_number AS imageNumber "
        + "FROM " + prefix + "images m_i "
        + "WHERE m_number IN (123) "
        + "AND (m_title='abd' AND m_number=123) "
        + "ORDER BY m_title ASC"));
        
        // Add second node to node constraint.
        step1.addNode(456);
        sb.setLength(0);
        instance.appendQueryBodyToSql(sb, query, instance);
        strSql = sb.toString();
        assertTrue(strSql, strSql.equals(
        "m_title AS m_imageTitle,"
        + "m_number AS imageNumber "
        + "FROM " + prefix + "images m_i "
        + "WHERE m_number IN (123,456) "
        + "AND (m_title='abd' AND m_number=123) "
        + "ORDER BY m_title ASC"));
        
        // Add relationstep (default directionality).
        BasicRelationStep step2 = (BasicRelationStep)
            query.addRelationStep(insrel,news).setAlias("insrel");
        BasicStep step3 = (BasicStep) step2.getNext();
        step3.setAlias("news");
        sb.setLength(0);
        instance.appendQueryBodyToSql(sb, query, instance);
        strSql = sb.toString();
        assertTrue(strSql, strSql.equals(
        "m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber "
        + "FROM " + prefix + "images m_i," + prefix + "insrel insrel," + prefix + "news news "
        + "WHERE m_i.m_number IN (123,456) "
        + "AND ((m_i.m_number=insrel.m_dnumber AND news.m_number=insrel.m_snumber) "
        + "OR (m_i.m_number=insrel.m_snumber AND news.m_number=insrel.m_dnumber)) "
        + "AND (m_i.m_title='abd' AND m_i.m_number=123) "
        + "ORDER BY m_i.m_title ASC"));
        
        // Set directionality for relationstep to DESTINATION.
        step2.setDirectionality(RelationStep.DIRECTIONS_DESTINATION);
        sb.setLength(0);
        instance.appendQueryBodyToSql(sb, query, instance);
        strSql = sb.toString();
        assertTrue(strSql, strSql.equals(
        "m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber "
        + "FROM " + prefix + "images m_i," + prefix + "insrel insrel," + prefix + "news news "
        + "WHERE m_i.m_number IN (123,456) "
        + "AND (m_i.m_number=insrel.m_snumber AND news.m_number=insrel.m_dnumber) "
        + "AND (m_i.m_title='abd' AND m_i.m_number=123) "
        + "ORDER BY m_i.m_title ASC"));
        
        // Set directionality for relationstep to SOURCE.
        step2.setDirectionality(RelationStep.DIRECTIONS_SOURCE);
        sb.setLength(0);
        instance.appendQueryBodyToSql(sb, query, instance);
        strSql = sb.toString();
        assertTrue(strSql, strSql.equals(
        "m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber "
        + "FROM " + prefix + "images m_i," + prefix + "insrel insrel," + prefix + "news news "
        + "WHERE m_i.m_number IN (123,456) "
        + "AND (m_i.m_number=insrel.m_dnumber AND news.m_number=insrel.m_snumber) "
        + "AND (m_i.m_title='abd' AND m_i.m_number=123) "
        + "ORDER BY m_i.m_title ASC"));
        
        // Set directionality for relationstep to BOTH.
        step2.setDirectionality(RelationStep.DIRECTIONS_BOTH);
        sb.setLength(0);
        instance.appendQueryBodyToSql(sb, query, instance);
        strSql = sb.toString();
        assertTrue(strSql, strSql.equals(
        "m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber "
        + "FROM " + prefix + "images m_i," + prefix + "insrel insrel," + prefix + "news news "
        + "WHERE m_i.m_number IN (123,456) "
        + "AND ((m_i.m_number=insrel.m_dnumber AND news.m_number=insrel.m_snumber) "
        + "OR (m_i.m_number=insrel.m_snumber AND news.m_number=insrel.m_dnumber)) "
        + "AND (m_i.m_title='abd' AND m_i.m_number=123) "
        + "ORDER BY m_i.m_title ASC"));
        
        // Add field for relationstep.
        StepField field2a = query.addField(step2, insrelRNumber).setAlias(null);
        sb.setLength(0);
        instance.appendQueryBodyToSql(sb, query, instance);
        strSql = sb.toString();
        assertTrue(strSql, strSql.equals(
        "m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber,"
        + "insrel.rnumber "
        + "FROM " + prefix + "images m_i," + prefix + "insrel insrel," + prefix + "news news "
        + "WHERE m_i.m_number IN (123,456) "
        + "AND ((m_i.m_number=insrel.m_dnumber AND news.m_number=insrel.m_snumber) "
        + "OR (m_i.m_number=insrel.m_snumber AND news.m_number=insrel.m_dnumber)) "
        + "AND (m_i.m_title='abd' AND m_i.m_number=123) "
        + "ORDER BY m_i.m_title ASC"));
        
        // Add field for third step.
        StepField field3a = query.addField(step3, newsTitle).setAlias(null);
        sb.setLength(0);
        instance.appendQueryBodyToSql(sb, query, instance);
        strSql = sb.toString();
        assertTrue(strSql, strSql.equals(
        "m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber,"
        + "insrel.rnumber,"
        + "news.m_title "
        + "FROM " + prefix + "images m_i," + prefix + "insrel insrel," + prefix + "news news "
        + "WHERE m_i.m_number IN (123,456) "
        + "AND ((m_i.m_number=insrel.m_dnumber AND news.m_number=insrel.m_snumber) "
        + "OR (m_i.m_number=insrel.m_snumber AND news.m_number=insrel.m_dnumber)) "
        + "AND (m_i.m_title='abd' AND m_i.m_number=123) "
        + "ORDER BY m_i.m_title ASC"));
        
        // Add second sortorder
        BasicSortOrder sortOrder3a = query.addSortOrder(field3a);
        sb.setLength(0);
        instance.appendQueryBodyToSql(sb, query, instance);
        strSql = sb.toString();
        assertTrue(strSql, strSql.equals(
        "m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber,"
        + "insrel.rnumber,"
        + "news.m_title "
        + "FROM " + prefix + "images m_i," + prefix + "insrel insrel," + prefix + "news news "
        + "WHERE m_i.m_number IN (123,456) "
        + "AND ((m_i.m_number=insrel.m_dnumber AND news.m_number=insrel.m_snumber) "
        + "OR (m_i.m_number=insrel.m_snumber AND news.m_number=insrel.m_dnumber)) "
        + "AND (m_i.m_title='abd' AND m_i.m_number=123) "
        + "ORDER BY m_i.m_title ASC,news.m_title ASC"));
        
        // Add third sortorder.
        BasicSortOrder sortOrder2a = query.addSortOrder(field2a);
        sb.setLength(0);
        instance.appendQueryBodyToSql(sb, query, instance);
        strSql = sb.toString();
        assertTrue(strSql, strSql.equals(
        "m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber,"
        + "insrel.rnumber,"
        + "news.m_title "
        + "FROM " + prefix + "images m_i," + prefix + "insrel insrel," + prefix + "news news "
        + "WHERE m_i.m_number IN (123,456) "
        + "AND ((m_i.m_number=insrel.m_dnumber AND news.m_number=insrel.m_snumber) "
        + "OR (m_i.m_number=insrel.m_snumber AND news.m_number=insrel.m_dnumber)) "
        + "AND (m_i.m_title='abd' AND m_i.m_number=123) "
        + "ORDER BY m_i.m_title ASC,news.m_title ASC,insrel.rnumber ASC"));
        
        // Add node constraint for second step (relation step).
        step2.addNode(789);
        sb.setLength(0);
        instance.appendQueryBodyToSql(sb, query, instance);
        strSql = sb.toString();
        assertTrue(strSql, strSql.equals(
        "m_i.m_title AS m_imageTitle,"
        + "m_i.m_number AS imageNumber,"
        + "insrel.rnumber,"
        + "news.m_title "
        + "FROM " + prefix + "images m_i," + prefix + "insrel insrel," + prefix + "news news "
        + "WHERE m_i.m_number IN (123,456) AND insrel.m_number IN (789) "
        + "AND ((m_i.m_number=insrel.m_dnumber AND news.m_number=insrel.m_snumber) "
        + "OR (m_i.m_number=insrel.m_snumber AND news.m_number=insrel.m_dnumber)) "
        + "AND (m_i.m_title='abd' AND m_i.m_number=123) "
        + "ORDER BY m_i.m_title ASC,news.m_title ASC,insrel.rnumber ASC"));
        
        // Aggregated query.
        query = new BasicSearchQuery(true);
        step1 = query.addStep(images).setAlias(null);
        BasicAggregatedField field4a 
            = (BasicAggregatedField) query.addAggregatedField(
                step1, imagesTitle, AggregatedField.AGGREGATION_TYPE_COUNT)
                    .setAlias(null);
        sb.setLength(0);
        instance.appendQueryBodyToSql(sb, query, instance);
        strSql = sb.toString();
        assertTrue(strSql, strSql.equals(
        "COUNT(m_title) "
        + "FROM " + prefix + "images m_images"));
        
        field4a.setAggregationType(
        AggregatedField.AGGREGATION_TYPE_COUNT_DISTINCT);
        sb.setLength(0);
        instance.appendQueryBodyToSql(sb, query, instance);
        strSql = sb.toString();
        assertTrue(strSql, strSql.equals(
        "COUNT(DISTINCT m_title) "
        + "FROM " + prefix + "images m_images"));
        
        field4a.setAggregationType(AggregatedField.AGGREGATION_TYPE_MIN);
        sb.setLength(0);
        instance.appendQueryBodyToSql(sb, query, instance);
        strSql = sb.toString();
        assertTrue(strSql, strSql.equals(
        "MIN(m_title) "
        + "FROM " + prefix + "images m_images"));
        
        field4a.setAggregationType(AggregatedField.AGGREGATION_TYPE_MAX);
        sb.setLength(0);
        instance.appendQueryBodyToSql(sb, query, instance);
        strSql = sb.toString();
        assertTrue(strSql, strSql.equals(
        "MAX(m_title) "
        + "FROM " + prefix + "images m_images"));
        
        field4a.setAlias("maxTitle");
        sb.setLength(0);
        instance.appendQueryBodyToSql(sb, query, instance);
        strSql = sb.toString();
        assertTrue(strSql, strSql.equals(
        "MAX(m_title) AS maxTitle "
        + "FROM " + prefix + "images m_images"));
        
        BasicAggregatedField field4b 
            = (BasicAggregatedField) query.addAggregatedField(
                step1, imagesNumber, AggregatedField.AGGREGATION_TYPE_COUNT)
                    .setAlias(null);
        sb.setLength(0);
        instance.appendQueryBodyToSql(sb, query, instance);
        strSql = sb.toString();
        assertTrue(strSql, strSql.equals(
        "MAX(m_title) AS maxTitle,"
        + "COUNT(m_number) "
        + "FROM " + prefix + "images m_images"));
        
        field4b.setAggregationType(AggregatedField.AGGREGATION_TYPE_GROUP_BY);
        sb.setLength(0);
        instance.appendQueryBodyToSql(sb, query, instance);
        strSql = sb.toString();
        assertTrue(strSql, strSql.equals(
        "MAX(m_title) AS maxTitle,"
        + "m_number "
        + "FROM " + prefix + "images m_images "
        + "GROUP BY m_number"));

        field4b.setAlias("imageNumber");
        sb.setLength(0);
        instance.appendQueryBodyToSql(sb, query, instance);
        strSql = sb.toString();
        assertTrue(strSql, strSql.equals(
        "MAX(m_title) AS maxTitle,"
        + "m_number AS imageNumber "
        + "FROM " + prefix + "images m_images "
        + "GROUP BY imageNumber"));
    }
    
    /** Test of appendConstraintToSql method, of class org.mmbase.storage.search.implementation.database.BasicSqlHandler. */
    public void testAppendConstraintToSql() {
        
        BasicSearchQuery query = new BasicSearchQuery();
        StringBuffer sb = new StringBuffer();
        BasicStep step1 = (BasicStep) query.addStep(images);
        step1.setAlias(null);
        FieldDefs imagesTitle = images.getField("title");
        StepField field1 = query.addField(step1, imagesTitle);
        FieldDefs imagesNumber = images.getField("number");
        StepField field2 = query.addField(step1, imagesNumber);
        BasicStep step2 = (BasicStep) query.addStep(news);
        step2.setAlias(null);
        FieldDefs newsNumber = news.getField("number");
        StepField field3 = query.addField(step2, newsNumber);
        FieldDefs newsTitle = news.getField("title");
        StepField field4 = query.addField(step2, newsTitle);
        
        // Test for BasicFieldNullConstraint
        BasicFieldNullConstraint constraint1 
        = new BasicFieldNullConstraint(field1);

        sb.setLength(0);
        instance.appendConstraintToSql(sb, constraint1, query, false, false);
        assertTrue(sb.toString(), sb.toString().equals(
        "m_images.m_title IS NULL"));
        
        sb.setLength(0);
        instance.appendConstraintToSql(sb, constraint1, query, false, true);
        assertTrue(sb.toString(), sb.toString().equals(
        "m_images.m_title IS NULL"));

        sb.setLength(0);
        instance.appendConstraintToSql(sb, constraint1, query, true, false);
        assertTrue(sb.toString(), sb.toString().equals(
        "m_images.m_title IS NOT NULL"));
        
        sb.setLength(0);
        constraint1.setInverse(true); // Set inverse.
        instance.appendConstraintToSql(sb, constraint1, query, true, false);
        assertTrue(sb.toString(), sb.toString().equals(
        "m_images.m_title IS NULL"));
        
        sb.setLength(0);
        instance.appendConstraintToSql(sb, constraint1, query, false, false);
        assertTrue(sb.toString(), sb.toString().equals(
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
        constraint2.addValue("AsDf");   // Add first value.
        instance.appendConstraintToSql(sb, constraint2, query, false, false);
        assertTrue(sb.toString(), sb.toString().equals(
        "m_images.m_title IN ('AsDf')"));

        sb.setLength(0);
        constraint2.setCaseSensitive(false);   // Case insensiteve
        instance.appendConstraintToSql(sb, constraint2, query, false, false);
        assertTrue(sb.toString(), sb.toString().equals(
        "LOWER(m_images.m_title) IN ('asdf')"));

        sb.setLength(0);
        constraint2.addValue("qWeR");   // Add second value.
        instance.appendConstraintToSql(sb, constraint2, query, false, false);
        assertTrue(sb.toString(), sb.toString().equals(
        "LOWER(m_images.m_title) IN ('asdf','qwer')"));

        sb.setLength(0);
        constraint2.setCaseSensitive(true);   // Case sensiteve
        instance.appendConstraintToSql(sb, constraint2, query, false, false);
        assertTrue(sb.toString(), sb.toString().equals(
        "m_images.m_title IN ('AsDf','qWeR')"));

        sb.setLength(0);
        instance.appendConstraintToSql(sb, constraint2, query, false, true);
        assertTrue(sb.toString(), sb.toString().equals(
        "m_images.m_title IN ('AsDf','qWeR')"));

        sb.setLength(0);
        instance.appendConstraintToSql(sb, constraint2, query, true, false);
        assertTrue(sb.toString(), sb.toString().equals(
        "m_images.m_title NOT IN ('AsDf','qWeR')"));

        sb.setLength(0);
        constraint2.setInverse(true);   // Set inverse.
        instance.appendConstraintToSql(sb, constraint2, query, true, false);
        assertTrue(sb.toString(), sb.toString().equals(
        "m_images.m_title IN ('AsDf','qWeR')"));

        sb.setLength(0);
        instance.appendConstraintToSql(sb, constraint2, query, false, false);
        assertTrue(sb.toString(), sb.toString().equals(
        "m_images.m_title NOT IN ('AsDf','qWeR')"));

        // Test for BasicFieldValueBetweenConstraint (String)
        BasicFieldValueBetweenConstraint constraint2a
            = new BasicFieldValueBetweenConstraint(field1, "AsDf", "jkLM");
        
        sb.setLength(0);
        instance.appendConstraintToSql(sb, constraint2a, query, false, false);
        assertTrue(sb.toString(), sb.toString().equals(
            "m_images.m_title BETWEEN 'AsDf' AND 'jkLM'"));
        
        sb.setLength(0);
        constraint2a.setInverse(true); // Set inverse.
        instance.appendConstraintToSql(sb, constraint2a, query, false, false);
        assertTrue(sb.toString(), sb.toString().equals(
            "m_images.m_title NOT BETWEEN 'AsDf' AND 'jkLM'"));
        
        sb.setLength(0);
        instance.appendConstraintToSql(sb, constraint2a, query, true, false);
        assertTrue(sb.toString(), sb.toString().equals(
            "m_images.m_title BETWEEN 'AsDf' AND 'jkLM'"));
        
        sb.setLength(0);
        constraint2a.setCaseSensitive(false); // Set case insensitive.
        instance.appendConstraintToSql(sb, constraint2a, query, true, false);
        assertTrue(sb.toString(), sb.toString().equals(
            "LOWER(m_images.m_title) BETWEEN 'asdf' AND 'jklm'"));
        
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
        
        assertTrue(sb.toString(), sb.toString().equals(
        "m_images.m_number IN (1234)"));

        sb.setLength(0);
        constraint3.addValue(new Integer(5678));   // Add second value.
        instance.appendConstraintToSql(sb, constraint3, query, false, false);
        assertTrue(sb.toString(), sb.toString().equals(
        "m_images.m_number IN (1234,5678)"));

        sb.setLength(0);
        instance.appendConstraintToSql(sb, constraint3, query, false, true);
        assertTrue(sb.toString(), sb.toString().equals(
        "m_images.m_number IN (1234,5678)"));

        sb.setLength(0);
        instance.appendConstraintToSql(sb, constraint3, query, true, false);
        assertTrue(sb.toString(), sb.toString().equals(
        "m_images.m_number NOT IN (1234,5678)"));

        sb.setLength(0);
        constraint3.setInverse(true);   // Set inverse.
        instance.appendConstraintToSql(sb, constraint3, query, true, false);
        assertTrue(sb.toString(), sb.toString().equals(
        "m_images.m_number IN (1234,5678)"));

        sb.setLength(0);
        instance.appendConstraintToSql(sb, constraint3, query, false, false);
        assertTrue(sb.toString(), sb.toString().equals(
        "m_images.m_number NOT IN (1234,5678)"));
        
        sb.setLength(0);
        constraint3.setCaseSensitive(false); // case insensitive, ignored
        instance.appendConstraintToSql(sb, constraint3, query, false, false);
        assertTrue(sb.toString(), sb.toString().equals(
        "m_images.m_number NOT IN (1234,5678)"));
        
        // Test for BasicFieldValueBetweenConstraint (integer)
        BasicFieldValueBetweenConstraint constraint3a
            = new BasicFieldValueBetweenConstraint(
                field2, new Integer(123), new Double(456.0));
        
        sb.setLength(0);
        instance.appendConstraintToSql(sb, constraint3a, query, false, false);
        assertTrue(sb.toString(), sb.toString().equals(
            "m_images.m_number BETWEEN 123 AND 456"));
        
        sb.setLength(0);
        constraint3a.setInverse(true); // Set inverse.
        instance.appendConstraintToSql(sb, constraint3a, query, false, false);
        assertTrue(sb.toString(), sb.toString().equals(
            "m_images.m_number NOT BETWEEN 123 AND 456"));
        
        sb.setLength(0);
        instance.appendConstraintToSql(sb, constraint3a, query, true, false);
        assertTrue(sb.toString(), sb.toString().equals(
            "m_images.m_number BETWEEN 123 AND 456"));
        
        sb.setLength(0);
        constraint3a.setCaseSensitive(false); // Set case insensitive, must be ignored.
        instance.appendConstraintToSql(sb, constraint3a, query, true, false);
        assertTrue(sb.toString(), sb.toString().equals(
            "m_images.m_number BETWEEN 123 AND 456"));
        
        // Test for BasicFieldValueConstraint (string).
        BasicFieldValueConstraint constraint6 
        = new BasicFieldValueConstraint(field1, "qWeRtY");

        sb.setLength(0);
        instance.appendConstraintToSql(sb, constraint6, query, false, false);
        assertTrue(sb.toString(), sb.toString().equals(
        "m_images.m_title='qWeRtY'"));

        sb.setLength(0);
        constraint6.setOperator(FieldCompareConstraint.LESS);
        instance.appendConstraintToSql(sb, constraint6, query, false, false);
        assertTrue(sb.toString(), sb.toString().equals(
        "m_images.m_title<'qWeRtY'"));

        sb.setLength(0);
        constraint6.setOperator(FieldCompareConstraint.LESS_EQUAL);
        instance.appendConstraintToSql(sb, constraint6, query, false, false);
        assertTrue(sb.toString(), sb.toString().equals(
        "m_images.m_title<='qWeRtY'"));

        sb.setLength(0);
        constraint6.setOperator(FieldCompareConstraint.EQUAL);
        instance.appendConstraintToSql(sb, constraint6, query, false, false);
        assertTrue(sb.toString(), sb.toString().equals(
        "m_images.m_title='qWeRtY'"));

        sb.setLength(0);
        constraint6.setOperator(FieldCompareConstraint.NOT_EQUAL);
        instance.appendConstraintToSql(sb, constraint6, query, false, false);
        assertTrue(sb.toString(), sb.toString().equals(
        "m_images.m_title<>'qWeRtY'"));

        sb.setLength(0);
        constraint6.setOperator(FieldCompareConstraint.GREATER);
        instance.appendConstraintToSql(sb, constraint6, query, false, false);
        assertTrue(sb.toString(), sb.toString().equals(
        "m_images.m_title>'qWeRtY'"));

        sb.setLength(0);
        constraint6.setOperator(FieldCompareConstraint.GREATER_EQUAL);
        instance.appendConstraintToSql(sb, constraint6, query, false, false);
        assertTrue(sb.toString(), sb.toString().equals(
        "m_images.m_title>='qWeRtY'"));

        sb.setLength(0);
        constraint6.setOperator(FieldCompareConstraint.LIKE);
        instance.appendConstraintToSql(sb, constraint6, query, false, false);
        assertTrue(sb.toString(), sb.toString().equals(
        "m_images.m_title LIKE 'qWeRtY'"));

        sb.setLength(0);
        constraint6.setInverse(true); // set inverse
        instance.appendConstraintToSql(sb, constraint6, query, false, false);
        assertTrue(sb.toString(), sb.toString().equals(
        "m_images.m_title NOT LIKE 'qWeRtY'"));

        sb.setLength(0);
        instance.appendConstraintToSql(sb, constraint6, query, true, false);
        assertTrue(sb.toString(), sb.toString().equals(
        "m_images.m_title LIKE 'qWeRtY'"));

        sb.setLength(0);
        constraint6.setCaseSensitive(false); // case insensitive
        instance.appendConstraintToSql(sb, constraint6, query, true, false);
        assertTrue(sb.toString(), sb.toString().equals(
        "LOWER(m_images.m_title) LIKE 'qwerty'"));

        // Test for BasicFieldValueConstraint (integer).
        BasicFieldValueConstraint constraint7 
        = new BasicFieldValueConstraint(field2, new Integer(9876));

        sb.setLength(0);
        instance.appendConstraintToSql(sb, constraint7, query, false, false);
        assertTrue(sb.toString(), sb.toString().equals(
        "m_images.m_number=9876"));

        sb.setLength(0);
        constraint7.setOperator(FieldCompareConstraint.LESS);
        instance.appendConstraintToSql(sb, constraint7, query, false, false);
        assertTrue(sb.toString(), sb.toString().equals(
        "m_images.m_number<9876"));

        sb.setLength(0);
        constraint7.setOperator(FieldCompareConstraint.LESS_EQUAL);
        instance.appendConstraintToSql(sb, constraint7, query, false, false);
        assertTrue(sb.toString(), sb.toString().equals(
        "m_images.m_number<=9876"));

        sb.setLength(0);
        constraint7.setOperator(FieldCompareConstraint.EQUAL);
        instance.appendConstraintToSql(sb, constraint7, query, false, false);
        assertTrue(sb.toString(), sb.toString().equals(
        "m_images.m_number=9876"));

        sb.setLength(0);
        constraint7.setOperator(FieldCompareConstraint.NOT_EQUAL);
        instance.appendConstraintToSql(sb, constraint7, query, false, false);
        assertTrue(sb.toString(), sb.toString().equals(
        "m_images.m_number<>9876"));

        sb.setLength(0);
        constraint7.setOperator(FieldCompareConstraint.GREATER);
        instance.appendConstraintToSql(sb, constraint7, query, false, false);
        assertTrue(sb.toString(), sb.toString().equals(
        "m_images.m_number>9876"));

        sb.setLength(0);
        constraint7.setOperator(FieldCompareConstraint.GREATER_EQUAL);
        instance.appendConstraintToSql(sb, constraint7, query, false, false);
        assertTrue(sb.toString(), sb.toString().equals(
        "m_images.m_number>=9876"));

        sb.setLength(0);
        constraint7.setInverse(true); // set inverse
        instance.appendConstraintToSql(sb, constraint7, query, false, false);
        assertTrue(sb.toString(), sb.toString().equals(
        "NOT m_images.m_number>=9876"));

        sb.setLength(0);
        instance.appendConstraintToSql(sb, constraint7, query, true, false);
        assertTrue(sb.toString(), sb.toString().equals(
        "m_images.m_number>=9876"));
        
        sb.setLength(0);
        constraint7.setCaseSensitive(false); // case insensitive, ignored
        instance.appendConstraintToSql(sb, constraint7, query, true, false);
        assertTrue(sb.toString(), sb.toString().equals(
        "m_images.m_number>=9876"));
        
        // Test for BasicCompareFieldsConstraint (integer)
        BasicCompareFieldsConstraint constraint8
        = new BasicCompareFieldsConstraint(field2, field3);
        
        sb.setLength(0);
        instance.appendConstraintToSql(sb, constraint8, query, false, false);
        assertTrue(sb.toString(), sb.toString().equals(
        "m_images.m_number=news.m_number"));

        sb.setLength(0);
        constraint8.setOperator(FieldCompareConstraint.LESS);
        instance.appendConstraintToSql(sb, constraint8, query, false, false);
        assertTrue(sb.toString(), sb.toString().equals(
        "m_images.m_number<news.m_number"));

        sb.setLength(0);
        constraint8.setOperator(FieldCompareConstraint.LESS_EQUAL);
        instance.appendConstraintToSql(sb, constraint8, query, false, false);
        assertTrue(sb.toString(), sb.toString().equals(
        "m_images.m_number<=news.m_number"));

        sb.setLength(0);
        constraint8.setOperator(FieldCompareConstraint.EQUAL);
        instance.appendConstraintToSql(sb, constraint8, query, false, false);
        assertTrue(sb.toString(), sb.toString().equals(
        "m_images.m_number=news.m_number"));

        sb.setLength(0);
        constraint8.setOperator(FieldCompareConstraint.NOT_EQUAL);
        instance.appendConstraintToSql(sb, constraint8, query, false, false);
        assertTrue(sb.toString(), sb.toString().equals(
        "m_images.m_number<>news.m_number"));

        sb.setLength(0);
        constraint8.setOperator(FieldCompareConstraint.GREATER);
        instance.appendConstraintToSql(sb, constraint8, query, false, false);
        assertTrue(sb.toString(), sb.toString().equals(
        "m_images.m_number>news.m_number"));

        sb.setLength(0);
        constraint8.setOperator(FieldCompareConstraint.GREATER_EQUAL);
        instance.appendConstraintToSql(sb, constraint8, query, false, false);
        assertTrue(sb.toString(), sb.toString().equals(
        "m_images.m_number>=news.m_number"));

        sb.setLength(0);
        constraint8.setInverse(true); // set inverse
        instance.appendConstraintToSql(sb, constraint8, query, false, false);
        assertTrue(sb.toString(), sb.toString().equals(
        "NOT m_images.m_number>=news.m_number"));

        sb.setLength(0);
        instance.appendConstraintToSql(sb, constraint8, query, true, false);
        assertTrue(sb.toString(), sb.toString().equals(
        "m_images.m_number>=news.m_number"));
        
        sb.setLength(0);
        constraint8.setCaseSensitive(false); // case insensitive, ignored
        instance.appendConstraintToSql(sb, constraint8, query, true, false);
        assertTrue(sb.toString(), sb.toString().equals(
        "m_images.m_number>=news.m_number"));

        // Test for BasicCompareFieldsConstraint (string)
        BasicCompareFieldsConstraint constraint9 =
        new BasicCompareFieldsConstraint(field1, field4);
        
        sb.setLength(0);
        instance.appendConstraintToSql(sb, constraint9, query, false, false);
        assertTrue(sb.toString(), sb.toString().equals(
        "m_images.m_title=news.m_title"));
        
        sb.setLength(0);
        constraint9.setOperator(FieldCompareConstraint.LESS);
        instance.appendConstraintToSql(sb, constraint9, query, false, false);
        assertTrue(sb.toString(), sb.toString().equals(
        "m_images.m_title<news.m_title"));

        sb.setLength(0);
        constraint9.setOperator(FieldCompareConstraint.GREATER);
        instance.appendConstraintToSql(sb, constraint9, query, false, false);
        assertTrue(sb.toString(), sb.toString().equals(
        "m_images.m_title>news.m_title"));

        sb.setLength(0);
        constraint9.setInverse(true); // set inverse
        instance.appendConstraintToSql(sb, constraint9, query, false, false);
        assertTrue(sb.toString(), sb.toString().equals(
        "NOT m_images.m_title>news.m_title"));

        sb.setLength(0);
        instance.appendConstraintToSql(sb, constraint9, query, true, false);
        assertTrue(sb.toString(), sb.toString().equals(
        "m_images.m_title>news.m_title"));
        
        sb.setLength(0);
        constraint9.setCaseSensitive(false); // case insensitive
        instance.appendConstraintToSql(sb, constraint9, query, true, false);
        assertTrue(sb.toString(), sb.toString().equals(
        "LOWER(m_images.m_title)>LOWER(news.m_title)"));

        // Test for composite constraint.
        BasicCompositeConstraint constraint10 =
        new BasicCompositeConstraint(CompositeConstraint.LOGICAL_AND);
        
        sb.setLength(0);
        try {
            // Composite constraint, should throw IllegalArgumentException.
            instance.appendConstraintToSql(sb, constraint10, query, false, false);
            fail("Composite constraint, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
        
        // Test for LegacyConstraint.
        BasicLegacyConstraint constraint11 
            = new BasicLegacyConstraint("a=b AND c=d");
        sb.setLength(0);
        instance.appendConstraintToSql(sb, constraint11, query, false, false);
        assertTrue(sb.toString(), sb.toString().equals(
        "a=b AND c=d"));
        
        sb.setLength(0);
        instance.appendConstraintToSql(sb, constraint11, query, true, false);
        assertTrue(sb.toString(), sb.toString().equals(
        "NOT (a=b AND c=d)"));
        
        sb.setLength(0);
        instance.appendConstraintToSql(sb, constraint11, query, false, true);
        assertTrue(sb.toString(), sb.toString().equals(
        "(a=b AND c=d)"));
        
        sb.setLength(0);
        instance.appendConstraintToSql(sb, constraint11, query, true, true);
        assertTrue(sb.toString(), sb.toString().equals(
        "NOT (a=b AND c=d)"));
        
        // Empty LegacyConstraint.
        sb.setLength(0);
        constraint11.setConstraint("   ");
        instance.appendConstraintToSql(sb, constraint11, query, true, true);
        assertTrue(sb.toString(), sb.toString().equals(""));
        
        sb.setLength(0);
        instance.appendConstraintToSql(sb, constraint11, query, true, false);
        assertTrue(sb.toString(), sb.toString().equals(""));
        
        sb.setLength(0);
        instance.appendConstraintToSql(sb, constraint11, query, false, true);
        assertTrue(sb.toString(), sb.toString().equals(""));
        
        sb.setLength(0);
        instance.appendConstraintToSql(sb, constraint11, query, false, false);
        assertTrue(sb.toString(), sb.toString().equals(""));
    }
    
    /** Test of getSupportLevel(int,SearchQuery), of class org.mmbase.storage.search.implementation.database.BasicSqlHandler. */
    public void testGetSupportLevel() throws Exception {
        BasicSearchQuery query = new BasicSearchQuery();
        
        // Support max number only when set to default (= -1).
        assertTrue(instance.getSupportLevel(SearchQueryHandler.FEATURE_MAX_NUMBER, query)
        == SearchQueryHandler.SUPPORT_OPTIMAL);
        query.setMaxNumber(100);
        assertTrue(instance.getSupportLevel(SearchQueryHandler.FEATURE_MAX_NUMBER, query)
        == SearchQueryHandler.SUPPORT_NONE);
        query.setMaxNumber(-1);
        assertTrue(instance.getSupportLevel(SearchQueryHandler.FEATURE_MAX_NUMBER, query)
        == SearchQueryHandler.SUPPORT_OPTIMAL);
        
        // Support offset only when set to default (= 0).
        assertTrue(instance.getSupportLevel(SearchQueryHandler.FEATURE_OFFSET, query)
        == SearchQueryHandler.SUPPORT_OPTIMAL);
        query.setOffset(100);
        assertTrue(instance.getSupportLevel(SearchQueryHandler.FEATURE_OFFSET, query)
        == SearchQueryHandler.SUPPORT_NONE);
        query.setOffset(0);
        assertTrue(instance.getSupportLevel(SearchQueryHandler.FEATURE_OFFSET, query)
        == SearchQueryHandler.SUPPORT_OPTIMAL);
    }
    
    /** Test of getSupportLevel(Constraint,SearchQuery), of class org.mmbase.storage.search.implementation.database.BasicSqlHandler. */
    public void testGetSupportLevel2() throws Exception {
        // Should return basic support level of constraint.
        SearchQuery query = new BasicSearchQuery();
        Constraint constraint = new TestConstraint(SearchQueryHandler.SUPPORT_NONE);
        assertTrue(instance.getSupportLevel(constraint, query) == SearchQueryHandler.SUPPORT_NONE);
        constraint = new TestConstraint(SearchQueryHandler.SUPPORT_WEAK);
        assertTrue(instance.getSupportLevel(constraint, query) == SearchQueryHandler.SUPPORT_WEAK);
        constraint = new TestConstraint(SearchQueryHandler.SUPPORT_NORMAL);
        assertTrue(instance.getSupportLevel(constraint, query) == SearchQueryHandler.SUPPORT_NORMAL);
        constraint = new TestConstraint(SearchQueryHandler.SUPPORT_OPTIMAL);
        assertTrue(instance.getSupportLevel(constraint, query) == SearchQueryHandler.SUPPORT_OPTIMAL);
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
            assertTrue(instance.getAllowedValue(disallowedValue).equals(allowedValue));
            
            // Allowed values.
            assertTrue(instance.getAllowedValue(allowedValue).equals(allowedValue));
            allowedValue += "_must_be_allowed_as_well";
            assertTrue(instance.getAllowedValue(allowedValue).equals(allowedValue));
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
        Step step1 = query.addStep(images).setAlias(null);
        FieldDefs imagesNumber = images.getField("number");
        StepField field1 = query.addField(step1, imagesNumber);
        Step step2 = query.addStep(news).setAlias(null);
        FieldDefs newsNumber = news.getField("number");
        StepField field2 = query.addField(step2, newsNumber);
        
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
        assertTrue(sb.toString(), sb.toString().equals(
        "NOT m_images.m_number>news.m_number"));
        
        sb.setLength(0);
        instance.appendCompositeConstraintToSql(sb, (CompositeConstraint) compositeConstraint, 
        query, true, false, instance);
        assertTrue(sb.toString(), sb.toString().equals(
        "m_images.m_number>news.m_number"));
        
        sb.setLength(0);
        instance.appendCompositeConstraintToSql(sb, (CompositeConstraint) compositeConstraint, 
        query, true, true, instance);
        assertTrue(sb.toString(), sb.toString().equals(
        "m_images.m_number>news.m_number"));
        
        sb.setLength(0);
        compositeConstraint.addChild(constraint1); // Add second child constraint.
        instance.appendCompositeConstraintToSql(sb, (CompositeConstraint) compositeConstraint, 
        query, false, false, instance);
        assertTrue(sb.toString(), sb.toString().equals(
        "NOT m_images.m_number>news.m_number AND NOT m_images.m_number>9876"));
        
        sb.setLength(0);
        instance.appendCompositeConstraintToSql(sb, (CompositeConstraint) compositeConstraint, 
        query, true, false, instance);
        assertTrue(sb.toString(), sb.toString().equals(
        "m_images.m_number>news.m_number OR m_images.m_number>9876"));
        
        sb.setLength(0);
        instance.appendCompositeConstraintToSql(sb, (CompositeConstraint) compositeConstraint, 
        query, true, true, instance);
        assertTrue(sb.toString(), sb.toString().equals(
        "(m_images.m_number>news.m_number OR m_images.m_number>9876)"));
        
        sb.setLength(0);
        instance.appendCompositeConstraintToSql(sb, (CompositeConstraint) compositeConstraint, 
        query, false, true, instance);
         assertTrue(sb.toString(), sb.toString().equals(
        "(NOT m_images.m_number>news.m_number AND NOT m_images.m_number>9876)"));
        
        sb.setLength(0);
        constraint1.setInverse(false); // Set second child not inverse.
        instance.appendCompositeConstraintToSql(sb, (CompositeConstraint) compositeConstraint, 
        query, false, false, instance);
         assertTrue(sb.toString(), sb.toString().equals(
        "NOT m_images.m_number>news.m_number AND m_images.m_number>9876"));
        
        sb.setLength(0);
        instance.appendCompositeConstraintToSql(sb, (CompositeConstraint) compositeConstraint, 
        query, true, false, instance);
        assertTrue(sb.toString(), sb.toString().equals(
        "m_images.m_number>news.m_number OR NOT m_images.m_number>9876"));
        
        // Composite with compositeConstraint as childs.
        sb.setLength(0);
        BasicCompositeConstraint composite2 =
            new BasicCompositeConstraint(CompositeConstraint.LOGICAL_AND)
                .addChild(compositeConstraint);
        instance.appendCompositeConstraintToSql(
            sb, composite2, query, false, false, instance);
        assertTrue(sb.toString(), sb.toString().equals(
        "NOT m_images.m_number>news.m_number AND m_images.m_number>9876"));
        
        // Composite with compositeConstraint as childs.
        sb.setLength(0);
        composite2.addChild(compositeConstraint);
        instance.appendCompositeConstraintToSql(
            sb, composite2, query, false, false, instance);
        assertTrue(sb.toString(), sb.toString().equals(
        "(NOT m_images.m_number>news.m_number AND m_images.m_number>9876) AND "
        + "(NOT m_images.m_number>news.m_number AND m_images.m_number>9876)"));
    }
    
    /** Test of appendFieldValue method, of class org.mmbase.storage.search.implementation.database.BasicSqlHandler. */
    public void testAppendFieldValue() {
        StringBuffer sb = new StringBuffer();
        instance.appendFieldValue(sb, "asd EFG", false, FieldDefs.TYPE_STRING);
        assertTrue(sb.toString(), sb.toString().equals("'asd EFG'"));
        
        sb.setLength(0);
        instance.appendFieldValue(sb, "asd EFG", true, FieldDefs.TYPE_STRING);
        assertTrue(sb.toString(), sb.toString().equals("'asd efg'"));
        
        sb.setLength(0);
        instance.appendFieldValue(sb, "asd EFG", false, FieldDefs.TYPE_XML);
        assertTrue(sb.toString(), sb.toString().equals("'asd EFG'"));
        
        sb.setLength(0);
        instance.appendFieldValue(sb, "asd EFG", true, FieldDefs.TYPE_XML);
        assertTrue(sb.toString(), sb.toString().equals("'asd efg'"));
        
        sb.setLength(0);
        instance.appendFieldValue(sb, "123.0", true, FieldDefs.TYPE_DOUBLE);
        assertTrue(sb.toString(), sb.toString().equals("123.0"));   
        
        sb.setLength(0);
        instance.appendFieldValue(sb, new Double(123.45), false, 
            FieldDefs.TYPE_DOUBLE);
        assertTrue(sb.toString(), sb.toString().equals("123.45"));

        sb.setLength(0);
        instance.appendFieldValue(sb, new Double(123.0), false, 
            FieldDefs.TYPE_DOUBLE);
        assertTrue(sb.toString(), sb.toString().equals("123"));
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(BasicSqlHandlerTest.class);
        
        return suite;
    }
    
    /** Test of useLower method, of class org.mmbase.storage.search.implementation.database.BasicSqlHandler. */
    public void testUseLower() {
        // Should always return true.
        assertTrue(instance.useLower(null));
    }
    
    /** Test of appendLikeOperator method, of class org.mmbase.storage.search.implementation.database.BasicSqlHandler. */
    public void testAppendLikeOperator() {
        // Should always append " LIKE ".
        StringBuffer sb = new StringBuffer();
        instance.appendLikeOperator(sb, true);
        assertTrue(sb.toString(), sb.toString().equals(" LIKE "));
        
        sb.setLength(0);
        instance.appendLikeOperator(sb, false);
        assertTrue(sb.toString(), sb.toString().equals(" LIKE "));
    }
    
    /** Test of appendField method, of class org.mmbase.storage.search.implementation.database.BasicSqlHandler. */
    public void testAppendField() {
        BasicSearchQuery query = new BasicSearchQuery();
        BasicStep step = query.addStep(images);
        FieldDefs fieldDefs = images.getField("number");
        
        StringBuffer sb = new StringBuffer();
        instance.appendField(sb, step, "number", false);
        assertTrue(sb.toString(), sb.toString().equals("m_number"));
        
        sb.setLength(0);
        instance.appendField(sb, step, "number", true);
        assertTrue(sb.toString(), sb.toString().equals("m_images.m_number"));
        
        sb.setLength(0);
        step.setAlias("imageNumber");
        instance.appendField(sb, step, "number", true);
        assertTrue(sb.toString(), sb.toString().equals("imageNumber.m_number"));
    }
    
}
