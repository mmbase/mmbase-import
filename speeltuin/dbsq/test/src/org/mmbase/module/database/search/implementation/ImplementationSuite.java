package org.mmbase.module.database.search.implementation;

import junit.framework.*;

/**
 * JUnit tests.
 *
 * @author Rob van Maris
 * @version $Revision: 1.1 $
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
        suite.addTest(org.mmbase.module.database.search.implementation.BasicCompareFieldsConstraintTest.suite());
        suite.addTest(org.mmbase.module.database.search.implementation.BasicCompositeConstraintTest.suite());
        suite.addTest(org.mmbase.module.database.search.implementation.BasicConstraintTest.suite());
        suite.addTest(org.mmbase.module.database.search.implementation.BasicFieldCompareConstraintTest.suite());
        suite.addTest(org.mmbase.module.database.search.implementation.BasicFieldConstraintTest.suite());
        suite.addTest(org.mmbase.module.database.search.implementation.BasicFieldNullConstraintTest.suite());
        suite.addTest(org.mmbase.module.database.search.implementation.BasicFieldValueConstraintTest.suite());
        suite.addTest(org.mmbase.module.database.search.implementation.BasicFieldValueInConstraintTest.suite());
        suite.addTest(org.mmbase.module.database.search.implementation.BasicQueryHandlerTest.suite());
        suite.addTest(org.mmbase.module.database.search.implementation.BasicRelationStepTest.suite());
        suite.addTest(org.mmbase.module.database.search.implementation.BasicSearchQueryTest.suite());
        suite.addTest(org.mmbase.module.database.search.implementation.BasicSortOrderTest.suite());
        suite.addTest(org.mmbase.module.database.search.implementation.BasicSqlGeneratorTest.suite());
        suite.addTest(org.mmbase.module.database.search.implementation.BasicSqlHandlerTest.suite());
        suite.addTest(org.mmbase.module.database.search.implementation.BasicStepTest.suite());
        suite.addTest(org.mmbase.module.database.search.implementation.BasicStepFieldTest.suite());
        suite.addTest(org.mmbase.module.database.search.implementation.ChainedSqlHandlerTest.suite());
        suite.addTest(org.mmbase.module.database.search.implementation.InformixSqlHandlerTest.suite());
        suite.addTest(org.mmbase.module.database.search.implementation.MySqlSqlHandlerTest.suite());
        //:JUNIT--
        //This value MUST ALWAYS be returned from this function.
        return suite;
    }
    
}
