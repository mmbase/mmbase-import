/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

import junit.framework.*;

/**
 * TestSuite that runs all the bridge tests.
 *
 * @author Jaco de Groot
 */
public class AllTests {

    public static void main(String[] args) {
        try { 
            junit.textui.TestRunner.run(suite());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static Test suite() throws Exception {
        org.mmbase.tests.BridgeTest.startMMBase();

        // Create the test suite
        TestSuite suite= new TestSuite("Bridge Tests");
        suite.addTestSuite(CloudContextTest.class);
        suite.addTestSuite(EmptyNodeTest.class);
        suite.addTestSuite(EmptyNodeTestTransaction.class);
        suite.addTestSuite(FilledNodeTest.class);
        suite.addTestSuite(FilledNodeTestTransaction.class);
        suite.addTestSuite(NodeManagerTest.class);
        suite.addTestSuite(CloudTest.class);
		suite.addTestSuite(RelationTest.class);
        return suite;
    }
	
}
