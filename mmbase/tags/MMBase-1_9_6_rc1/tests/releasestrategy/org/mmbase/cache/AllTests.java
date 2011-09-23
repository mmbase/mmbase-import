/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.cache;

import junit.framework.*;

/**
 * TestSuite that runs all the release strategy tests.
 *
 * @author Ernst Bunders
 * @version $Id$
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
        //MMBaseTest.startMMBase();
        // Create the test suite
        TestSuite suite= new TestSuite("Strategy Tests");
        suite.addTestSuite(ConstraintMatcherTest.class);
        suite.addTestSuite(ReleaseStrategyTest.class);
        return suite;
    }

}
