package org.mmbase.applications.vprowizards;

import java.util.*;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.mmbase.applications.vprowizards.spring.WizardControllerActionTest;
import org.mmbase.applications.vprowizards.spring.action.CreateNodeActionTest;
import org.mmbase.applications.vprowizards.spring.util.PathBuilderTest;
import org.mmbase.tests.MMBaseTest;

public class AllTests {

    public static void main(String[] args) {
        try {
            junit.textui.TestRunner.run(suite());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static Test suite() throws Exception {
        MMBaseTest.startMMBase();

        // Create the test suite
        TestSuite suite= new TestSuite("Bridge Tests");
        suite.addTestSuite(WizardControllerActionTest.class);
        suite.addTestSuite(CreateNodeActionTest.class);
        suite.addTestSuite(PathBuilderTest.class);
        return suite;
    }
}