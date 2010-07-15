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
    @Override
    public void setUp() throws Exception {}

    /**
     * Tears down after each test.
     */
    @Override
    public void tearDown() throws Exception {}

    public static Test suite() {
        //--JUNIT:
        //This block was automatically generated and can be regenerated again.
        //Do NOT change lines enclosed by the --JUNIT: and :JUNIT-- tags.

        TestSuite suite = new TestSuite("ImplementationSuite");
        suite.addTest(org.mmbase.storage.search.implementation.database.DatabaseSuite.suite());
        //:JUNIT--
        //This value MUST ALWAYS be returned from this function.
        return suite;
    }

}
