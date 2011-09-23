package org.mmbase.storage.search.implementation;

import junit.framework.*;
import junit.textui.TestRunner;

import org.mmbase.module.core.*;


/**
 * JUnit tests.
 *
 * @author Rob van Maris
 * @version $Revision: 1.4 $
 */
public class BasicFieldNullConstraintTest extends TestCase {

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
        MMBase.getMMBase();
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

    public static Test suite() {
        TestSuite suite = new TestSuite(BasicFieldNullConstraintTest.class);

        return suite;
    }

}
