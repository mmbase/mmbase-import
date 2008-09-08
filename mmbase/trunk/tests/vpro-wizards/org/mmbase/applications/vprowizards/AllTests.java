package org.mmbase.applications.vprowizards;

import java.util.*;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.mmbase.applications.vprowizards.spring.ErrorTest;
import org.mmbase.applications.vprowizards.spring.WizardControllerActionTest;
import org.mmbase.applications.vprowizards.spring.action.*;
import org.mmbase.applications.vprowizards.spring.util.PathBuilderTest;
import org.mmbase.tests.MMBaseTest;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public class AllTests {
	
	private static Logger log = Logging.getLoggerInstance(AllTests.class);

    public static void main(String[] args) {
        try {
            junit.textui.TestRunner.run(suite());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static Test suite() throws Exception {
        MMBaseTest.startMMBase(true);

        // Create the test suite
        TestSuite suite= new TestSuite("Vpro Wizard Tests");

        //utility tests
//        suite.addTestSuite(ErrorTest.class);
//        suite.addTestSuite(PathBuilderTest.class);
        
        //general spring component tests
//        suite.addTestSuite(WizardControllerActionTest.class);
        
        //action tests
//        suite.addTestSuite(CreateNodeActionTest.class);
//        suite.addTestSuite(CreateRelationActionTest.class);
        suite.addTestSuite(SortRelationActionTest.class);
        //suite.addTestSuite(ToggleRelationTest.class);
        return suite;
    }
}