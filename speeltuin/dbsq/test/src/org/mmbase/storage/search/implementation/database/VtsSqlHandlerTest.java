package org.mmbase.storage.search.implementation.database;

import junit.framework.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;
import java.util.*;
import org.mmbase.module.core.MMObjectBuilder;

/**
 * JUnit tests.
 *
 * @author Rob van Maris
 * @version $Revision: 1.1 $
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
    private BasicSearchQuery query = null;
    private BasicStep step1 = null;
    private StepField field1 = null;
    private StepField field2 = null;
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
        MMBaseContext.init();
        mmbase = MMBase.getMMBase();
        images = mmbase.getBuilder("images");
        insrel = mmbase.getInsRel();
        pools = mmbase.getBuilder("pools");
        imagesDescription = images.getField("description");
        imagesTitle = images.getField("title");
        query = new BasicSearchQuery();
        query.setMaxNumber(100);
        query.setOffset(1);
        step1 = query.addStep(images);
        field1 = query.addField(step1, imagesDescription);
        field2 = query.addField(step1, imagesTitle);
        // TODO (later): simulate VTS indices on these fields.
        constraint1 = new BasicStringSearchConstraint(field1, 
        StringSearchConstraint.SEARCH_TYPE_WORD_ORIENTED, 
        StringSearchConstraint.MATCH_TYPE_LITERAL, "sdj kjjo kjoje");
        constraint2 = new BasicStringSearchConstraint(field2, 
        StringSearchConstraint.SEARCH_TYPE_WORD_ORIENTED, 
        StringSearchConstraint.MATCH_TYPE_LITERAL, "jdkdk keijc");
        query.setConstraint(constraint1);
        composite 
        = new BasicCompositeConstraint(CompositeConstraint.LOGICAL_AND);
        instance = new VtsSqlHandler(new BasicSqlHandler(new HashMap()));
    }
    
    /**
     * Tears down after each test.
     */
    public void tearDown() throws Exception {}
    
    /** Test of getSupportLevel(int,query) method, of class org.mmbase.storage.search.implementation.database.VtsSqlHandler. */
    public void testGetSupportLevel() throws Exception {
        // Max number is supported if only constraint is StringSearchConstraint.
        assert(
        instance.getSupportLevel(SearchQueryHandler.FEATURE_MAX_NUMBER, query)
        == SearchQueryHandler.SUPPORT_OPTIMAL);
        
        // Offset not supported. 
        assert(
        instance.getSupportLevel(SearchQueryHandler.FEATURE_OFFSET, query)
        == SearchQueryHandler.SUPPORT_NONE);
        
        // Max number not supported for composite constraints.
        composite.addChild(constraint1);
        query.setConstraint(composite);
        assert(
        instance.getSupportLevel(SearchQueryHandler.FEATURE_MAX_NUMBER, query)
        == SearchQueryHandler.SUPPORT_NONE);
    }
    
    /** Test of getSupportLevel(constraint,query) of class org.mmbase.storage.search.implementation.database.VtsSqlHandler. */
    public void testGetSupportLevel2() throws Exception {
        // StringSearchConstraint is supported for VTS field.
        assert(instance.getSupportLevel(constraint1, query) 
        == SearchQueryHandler.SUPPORT_OPTIMAL);
        
        // Composite constraint.
        // Supported if it contains exactly one supported StringSearchConstraint.
        assert(instance.getSupportLevel(composite, query) 
        == SearchQueryHandler.SUPPORT_OPTIMAL);
        
        composite.addChild(constraint2);
        assert(instance.getSupportLevel(composite, query)
        == SearchQueryHandler.SUPPORT_NONE);
    }
    
    /** Test of hasVtsIndex method, of class org.mmbase.storage.search.implementation.database.VtsSqlHandler. */
    public void testHasVtsIndex() {
        // TODO (later): implement
    }
    
    /** Test of hasAdditionalConstraints method, of class org.mmbase.storage.search.implementation.database.VtsSqlHandler. */
    public void testHasAdditionalConstraints() {
        assert(!instance.hasAdditionalConstraints(query));
        
        step1.addNode(1);
        assert(instance.hasAdditionalConstraints(query));
        
        query = new BasicSearchQuery();
        step1= query.addStep(images);
        assert(!instance.hasAdditionalConstraints(query));
        
        query.addRelationStep(insrel, pools);
        assert(instance.hasAdditionalConstraints(query));
    }
    
    /** Test of containsOtherStringSearchConstraints method, of class org.mmbase.storage.search.implementation.database.VtsSqlHandler. */
    public void testContainsOtherStringSearchConstraints() {
        assert(!instance.containsOtherStringSearchConstraints(composite, constraint1));
        assert(!instance.containsOtherStringSearchConstraints(composite, constraint2));
        
        composite.addChild(constraint1);
        assert(!instance.containsOtherStringSearchConstraints(composite, constraint1));
        assert(instance.containsOtherStringSearchConstraints(composite, constraint2));
        
        composite.addChild(constraint2);
        assert(instance.containsOtherStringSearchConstraints(composite, constraint1));
        assert(instance.containsOtherStringSearchConstraints(composite, constraint2));
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(VtsSqlHandlerTest.class);
        
        return suite;
    }
    
}
