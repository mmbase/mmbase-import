package org.mmbase.storage.search.implementation.database;

import junit.framework.*;

/**
 * JUnit tests.
 *
 * @author Rob van Maris
 * @version $Revision: 1.2 $
 */
public class DatabaseSuite extends TestCase {
    
    public DatabaseSuite(java.lang.String testName) {
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
        
        TestSuite suite = new TestSuite("DatabaseSuite");
        suite.addTest(org.mmbase.storage.search.implementation.database.BasicQueryHandlerTest.suite());
        suite.addTest(org.mmbase.storage.search.implementation.database.BasicSqlHandlerTest.suite());
        suite.addTest(org.mmbase.storage.search.implementation.database.ChainedSqlHandlerTest.suite());
        suite.addTest(org.mmbase.storage.search.implementation.database.InformixSqlHandlerTest.suite());
        suite.addTest(org.mmbase.storage.search.implementation.database.MySqlSqlHandlerTest.suite());
        suite.addTest(org.mmbase.storage.search.implementation.database.PostgreSqlSqlHandlerTest.suite());
        //:JUNIT--
        //This value MUST ALWAYS be returned from this function.
        return suite;
    }
    
}
