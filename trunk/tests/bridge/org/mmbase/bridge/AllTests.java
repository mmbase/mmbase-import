/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

import org.mmbase.tests.BridgeTest;
import org.mmbase.bridge.util.*;
import org.mmbase.bridge.implementation.*;
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
        BridgeTest.startMMBase();

        // Create the test suite
        TestSuite suite= new TestSuite("Bridge Tests");
        suite.addTestSuite(CloudContextTest.class);
        suite.addTestSuite(EmptyNodeTest.class);
        suite.addTestSuite(EmptyNodeTestTransaction.class);
        suite.addTestSuite(EmptiedNodeTest.class);
        suite.addTestSuite(EmptyNotNullNodeTest.class);
        suite.addTestSuite(EmptyNotNullNodeTestTransaction.class);
        suite.addTestSuite(FilledNodeTest.class);
        suite.addTestSuite(FilledNodeTestTransaction.class);
        suite.addTestSuite(ErroneousFilledNodeTest.class);
        suite.addTestSuite(NodeManagerTest.class);
        suite.addTestSuite(CloudTest.class);
        suite.addTestSuite(TransactionTest.class);
        suite.addTestSuite(RelationTest.class);
        suite.addTestSuite(DataTypesTest.class);
        suite.addTestSuite(DataTypesTestTransaction.class);
        suite.addTestSuite(FunctionsTest.class);
        suite.addTestSuite(QueriesTest.class);
        suite.addTestSuite(SerializableTest.class);
        suite.addTestSuite(ToStringTest.class);
        suite.addTestSuite(ProcessorTest.class);
        suite.addTestSuite(VirtualNodeTest.class);
        suite.addTest(BridgeTest.SHUTDOWN);
        return suite;
    }
}
