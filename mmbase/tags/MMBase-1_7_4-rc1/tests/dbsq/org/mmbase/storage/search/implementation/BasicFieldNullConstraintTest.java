package org.mmbase.storage.search.implementation;

import junit.framework.*;
import junit.textui.TestRunner;
import org.mmbase.module.core.*;
import org.mmbase.storage.search.Step;
import org.mmbase.module.corebuilders.FieldDefs;
import org.mmbase.storage.search.*;


/**
 * JUnit tests.
 *
 * @author Rob van Maris
 * @version $Revision: 1.1 $
 */
public class BasicFieldNullConstraintTest extends TestCase {
    
    private final static String BUILDER_NAME = "images";
    private final static String STRING_FIELD_NAME = "title";

    /** Test instance. */
    private BasicFieldNullConstraint instance = null;
    
    /** MMBase instance. */
    private MMBase mmbase = null;
    
    /** Field instances. */
    private BasicStepField stringField = null;
    
    /** Builder example. */
    private MMObjectBuilder builder = null;
    
    /** FieldDefs examples. */
    private FieldDefs stringFieldDefs = null;

    public BasicFieldNullConstraintTest(java.lang.String testName) {
        super(testName);
    }
    
    public static void main(java.lang.String[] args) {
        TestRunner.run(suite());
    }
    
    /**
     * Sets up before each test.
     */
    public void setUp() throws Exception {
        MMBaseContext.init();
        mmbase = MMBase.getMMBase();
        builder = mmbase.getBuilder(BUILDER_NAME);
        stringFieldDefs = builder.getField(STRING_FIELD_NAME);
        Step step = new BasicStep(builder);
        stringField = new BasicStepField(step, stringFieldDefs);
        instance = new BasicFieldNullConstraint(stringField);
    }
    
    /**
     * Tears down after each test.
     */
    public void tearDown() throws Exception {}
    
    /** Test of equals method, of class org.mmbase.storage.search.implementation.BasicFieldNullConstraint. */
    public void testEquals() {
        // TODO: implement test
    }
    
    /** Test of hashCode method, of class org.mmbase.storage.search.implementation.BasicFieldNullConstraint. */
    public void testHashCode() {
        // TODO: implement test
    }
    
    /** Test of toString method, of class org.mmbase.storage.search.implementation.BasicFieldNullConstraint. */
    public void testToString() {
        assertTrue(instance.toString(),
        instance.toString().equals("FieldNullConstraint(inverse:"
        + instance.isInverse() + ", field:"
        + instance.getField().getAlias() + ", casesensitive:"
        + instance.isCaseSensitive() + ")"));
        
        // Reverse inverse flag.
        instance.setInverse(!instance.isInverse());
        assertTrue(instance.toString(),
        instance.toString().equals("FieldNullConstraint(inverse:"
        + instance.isInverse() + ", field:"
        + instance.getField().getAlias() + ", casesensitive:"
        + instance.isCaseSensitive() + ")"));
       
        // Set field alias.
        stringField.setAlias("yyuiwe");
        assertTrue(instance.toString(),
        instance.toString().equals("FieldNullConstraint(inverse:"
        + instance.isInverse() + ", field:"
        + instance.getField().getAlias() + ", casesensitive:"
        + instance.isCaseSensitive() + ")"));
        
        // Reverse case sensitive.
        instance.setCaseSensitive(!instance.isCaseSensitive());
        assertTrue(instance.toString(),
        instance.toString().equals("FieldNullConstraint(inverse:"
        + instance.isInverse() + ", field:"
        + instance.getField().getAlias() + ", casesensitive:"
        + instance.isCaseSensitive() + ")"));
        
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(BasicFieldNullConstraintTest.class);
        
        return suite;
    }
    
}
