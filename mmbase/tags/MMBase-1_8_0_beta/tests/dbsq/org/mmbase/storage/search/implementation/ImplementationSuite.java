package org.mmbase.storage.search.implementation;

import junit.framework.*;

/**
 * JUnit tests.
 *
 * @author Rob van Maris
 * @version $Revision: 1.2 $
 */
public class ImplementationSuite extends TestCase {
    
    public ImplementationSuite(java.lang.String testName) {
        super(testName);
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    /**
     * Sets up before each test.
     */
    public void setUp() throws Exception {}
    
    /**
     * Tears down after each test.
     */
    public void tearDown() throws Exception {}
    
    public static Test suite() {
        //--JUNIT:
        //This block was automatically generated and can be regenerated again.
        //Do NOT change lines enclosed by the --JUNIT: and :JUNIT-- tags.
        
        TestSuite suite = new TestSuite("ImplementationSuite");
        suite.addTest(org.mmbase.storage.search.implementation.BasicAggregatedFieldTest.suite());
        suite.addTest(org.mmbase.storage.search.implementation.BasicCompareFieldsConstraintTest.suite());
        suite.addTest(org.mmbase.storage.search.implementation.BasicCompositeConstraintTest.suite());
        suite.addTest(org.mmbase.storage.search.implementation.BasicConstraintTest.suite());
        suite.addTest(org.mmbase.storage.search.implementation.BasicFieldCompareConstraintTest.suite());
        suite.addTest(org.mmbase.storage.search.implementation.BasicFieldConstraintTest.suite());
        suite.addTest(org.mmbase.storage.search.implementation.BasicFieldNullConstraintTest.suite());
//        yet to be implemented:
//        suite.addTest(org.mmbase.storage.search.implementation.BasicFieldValueBetweenConstraintTest.suite()); 
        suite.addTest(org.mmbase.storage.search.implementation.BasicFieldValueConstraintTest.suite());
        suite.addTest(org.mmbase.storage.search.implementation.BasicFieldValueInConstraintTest.suite());
        suite.addTest(org.mmbase.storage.search.implementation.BasicLegacyConstraintTest.suite());
        suite.addTest(org.mmbase.storage.search.implementation.BasicRelationStepTest.suite());
        suite.addTest(org.mmbase.storage.search.implementation.BasicSearchQueryTest.suite());
        suite.addTest(org.mmbase.storage.search.implementation.BasicSortOrderTest.suite());
        suite.addTest(org.mmbase.storage.search.implementation.BasicStepTest.suite());
        suite.addTest(org.mmbase.storage.search.implementation.BasicStepFieldTest.suite());
        suite.addTest(org.mmbase.storage.search.implementation.BasicStringSearchConstraintTest.suite());
        suite.addTest(org.mmbase.storage.search.implementation.database.DatabaseSuite.suite());
        suite.addTest(org.mmbase.storage.search.implementation.ModifiableQueryTest.suite());
        suite.addTest(org.mmbase.storage.search.implementation.NodeSearchQueryTest.suite());
        //:JUNIT--
        //This value MUST ALWAYS be returned from this function.
        return suite;
    }
    
}
