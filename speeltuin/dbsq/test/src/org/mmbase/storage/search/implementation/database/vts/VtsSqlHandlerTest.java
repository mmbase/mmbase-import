package org.mmbase.storage.search.implementation.database.vts;

import junit.framework.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;
import java.util.*;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.storage.search.implementation.database.*;
import java.io.*;
import org.mmbase.module.database.support.*;
import org.mmbase.util.logging.*;
import org.w3c.dom.*;
import org.xml.sax.*;

/**
 * JUnit tests.
 * 
 * Requires a vtsindices configuration file specifying vtsindices
 * on images.title and pools.name.
 *
 * @author Rob van Maris
 * @version $Revision: 1.4 $
 */
public class VtsSqlHandlerTest extends TestCase {
    
    /** Test instance. */
    private VtsSqlHandler instance = null;
    
    private MMBase mmbase = null;
    private MMObjectBuilder images = null;
    private InsRel insrel = null;
    private MMObjectBuilder pools = null;
    private FieldDefs imagesDescription = null;
    private FieldDefs imagesTitle = null;
    private FieldDefs poolsName = null;
    private BasicSearchQuery query = null;
    private BasicStep imagesStep = null;
    private BasicStep poolsStep = null;
    private StepField imagesDescriptionField = null;
    private StepField imagesTitleField = null;
    private StepField poolsNameField = null;
    private BasicStringSearchConstraint constraint1 = null;
    private BasicStringSearchConstraint constraint2 = null;
    private BasicCompositeConstraint composite = null;
    
    public VtsSqlHandlerTest(java.lang.String testName) {
        super(testName);
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    /**
     * Sets up before each test.
     */
    public void setUp() throws Exception {
        try {
        MMBaseContext.init();
        mmbase = MMBase.getMMBase();
        images = mmbase.getBuilder("images");
        insrel = mmbase.getInsRel();
        pools = mmbase.getBuilder("pools");
        imagesDescription = images.getField("description");
        imagesTitle = images.getField("title");
        poolsName = pools.getField("name");
        query = new BasicSearchQuery();
        query.setMaxNumber(100)
            .setOffset(1);
        imagesStep = query.addStep(images);
        poolsStep = query.addStep(pools);
        imagesDescriptionField = query.addField(imagesStep, imagesDescription);
        imagesTitleField = query.addField(imagesStep, imagesTitle);
        poolsNameField = query.addField(poolsStep, poolsName);
        // TODO (later): simulate VTS indices on these fields.
        constraint1 = new BasicStringSearchConstraint(poolsNameField, 
        StringSearchConstraint.SEARCH_TYPE_WORD_ORIENTED, 
        StringSearchConstraint.MATCH_TYPE_LITERAL, "sdj kjjo kjoje");
        constraint2 = new BasicStringSearchConstraint(imagesTitleField, 
        StringSearchConstraint.SEARCH_TYPE_WORD_ORIENTED, 
        StringSearchConstraint.MATCH_TYPE_LITERAL, "jdkdk keijc");
        query.setConstraint(constraint1);
        composite 
        = new BasicCompositeConstraint(CompositeConstraint.LOGICAL_AND);
        SqlHandler sqlHandler = new BasicSqlHandler(new HashMap());
        instance = new VtsSqlHandler(sqlHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Tears down after each test.
     */
    public void tearDown() throws Exception {}
    
    /** Test of getSupportLevel(int,query) method, of class org.mmbase.storage.search.implementation.database.VtsSqlHandler. */
    public void testGetSupportLevel() throws Exception {
        // Max number is supported if only constraint is StringSearchConstraint.
        assertTrue(
        instance.getSupportLevel(SearchQueryHandler.FEATURE_MAX_NUMBER, query)
        == SearchQueryHandler.SUPPORT_OPTIMAL);
        
        // Offset not supported. 
        assertTrue(
        instance.getSupportLevel(SearchQueryHandler.FEATURE_OFFSET, query)
        == SearchQueryHandler.SUPPORT_NONE);
        
        // Max number not supported for composite constraints.
        composite.addChild(constraint1);
        query.setConstraint(composite);
        assertTrue(
        instance.getSupportLevel(SearchQueryHandler.FEATURE_MAX_NUMBER, query)
        == SearchQueryHandler.SUPPORT_NONE);
    }
    
    /** Test of getSupportLevel(constraint,query) of class org.mmbase.storage.search.implementation.database.VtsSqlHandler. */
    public void testGetSupportLevel2() throws Exception {
        // StringSearchConstraint is supported for VTS field.
        assertTrue(instance.getSupportLevel(constraint1, query) 
        == SearchQueryHandler.SUPPORT_OPTIMAL);
        
        // Composite constraint.
        // Supported if it contains exactly one supported StringSearchConstraint.
        assertTrue(instance.getSupportLevel(composite, query) 
        == SearchQueryHandler.SUPPORT_OPTIMAL);
        
        composite.addChild(constraint2);
        assertTrue(instance.getSupportLevel(composite, query)
        == SearchQueryHandler.SUPPORT_NONE);
    }
    
    /** Test of hasVtsIndex method, of class org.mmbase.storage.search.implementation.database.VtsSqlHandler. */
    public void testHasVtsIndex() {
        assertTrue(!instance.hasVtsIndex(imagesDescriptionField));
        assertTrue(instance.hasVtsIndex(imagesTitleField));
    }
    
    /** Test of hasAdditionalConstraints method, of class org.mmbase.storage.search.implementation.database.VtsSqlHandler. */
    public void testHasAdditionalConstraints() {
        assertTrue(!instance.hasAdditionalConstraints(query));
        
        imagesStep.addNode(1);
        assertTrue(instance.hasAdditionalConstraints(query));
        
        query = new BasicSearchQuery();
        imagesStep= query.addStep(images);
        assertTrue(!instance.hasAdditionalConstraints(query));
        
        query.addRelationStep(insrel, pools);
        assertTrue(instance.hasAdditionalConstraints(query));
    }
    
    /** Test of containsOtherStringSearchConstraints method, of class org.mmbase.storage.search.implementation.database.VtsSqlHandler. */
    public void testContainsOtherStringSearchConstraints() {
        assertTrue(!instance.containsOtherStringSearchConstraints(composite, constraint1));
        assertTrue(!instance.containsOtherStringSearchConstraints(composite, constraint2));
        
        composite.addChild(constraint1);
        assertTrue(!instance.containsOtherStringSearchConstraints(composite, constraint1));
        assertTrue(instance.containsOtherStringSearchConstraints(composite, constraint2));
        
        composite.addChild(constraint2);
        assertTrue(instance.containsOtherStringSearchConstraints(composite, constraint1));
        assertTrue(instance.containsOtherStringSearchConstraints(composite, constraint2));
    }
    
    /** Test of toBuilderField method, of class org.mmbase.storage.search.implementation.database.vts.VtsSqlHandler. */
    public void testToBuilderField() {
        String prefix = mmbase.getBaseName() + "_";
        
        try {
            // Basename without prefix, should throw IllegalArgumentException.
            VtsSqlHandler.toBuilderField("images", "title");
            fail("Basename without prefix, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
        
        try {
            // Unknown builder name, should throw IllegalArgumentException.
            VtsSqlHandler.toBuilderField(prefix + "sqk749", "title");
            fail("Unknown builder name, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
            
        try {
            // Unknown field name, should throw IllegalArgumentException.
            VtsSqlHandler.toBuilderField(prefix + "images", "sqk749");
            fail("Unknown field name, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
        
        assertTrue(VtsSqlHandler.toBuilderField(prefix + "images", "title").
            equals("images.title"));
    }
    
    /** Test of appendConstraintToSql method, of class org.mmbase.storage.search.implementation.database.vts.VtsSqlHandler. */
    public void testAppendConstraintToSql() {
        // TODO: implement
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(VtsSqlHandlerTest.class);
        
        return suite;
    }
    
}
