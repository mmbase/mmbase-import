package org.mmbase.storage.search.implementation.database.vts;

import junit.framework.*;

/**
 * JUnit tests.
 *
 * @author Rob van Maris
 * @version $Revision: 1.2 $
 */
public class VtsSuite extends TestCase {
    
    public VtsSuite(java.lang.String testName) {
        super(testName);
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
        System.exit(0);
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
        
        TestSuite suite = new TestSuite("VtsSuite");
        suite.addTest(org.mmbase.storage.search.implementation.database.vts.VtsSqlHandlerTest.suite());
        suite.addTest(org.mmbase.storage.search.implementation.database.vts.XmlVtsIndicesReaderTest.suite());
        //:JUNIT--
        //This value MUST ALWAYS be returned from this function.
        return suite;
    }
    
}
