package org.mmbase.module.database.support;

import junit.framework.*;

/**
 * JUnit tests.
 *
 * @author Rob van Maris
 * @version $Revision: 1.1 $
 */
public class SupportSuite extends TestCase {
    
    public SupportSuite(java.lang.String testName) {
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
        TestSuite suite = new TestSuite("ImplementationSuite");
        suite.addTest(org.mmbase.module.database.support.MMSQL92NodeTest.suite());
        return suite;
    }
    
}
