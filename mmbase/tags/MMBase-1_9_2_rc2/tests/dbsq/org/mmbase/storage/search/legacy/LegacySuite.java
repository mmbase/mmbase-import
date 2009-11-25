package org.mmbase.storage.search.legacy;

import junit.framework.*;

/**
 * JUnit tests.
 *
 * @author Rob van Maris
 * @version $Revision: 1.2 $
 */
public class LegacySuite extends TestCase {

    public LegacySuite(java.lang.String testName) {
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
        TestSuite suite = new TestSuite("LegacySuite");
        suite.addTest(org.mmbase.storage.search.legacy.ConstraintParserTest.suite());
        suite.addTest(org.mmbase.storage.search.legacy.QueryConvertorTest.suite());
        return suite;
    }

}
