package org.mmbase.storage.search.implementation.database.vts;

import junit.framework.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import org.w3c.dom.*;
import org.xml.sax.*;

/**
 * JUnit tests.
 *
 * @author Rob van Maris
 * @version $Revision: 1.1 $
 */
public class VtsIndexCreatorTest extends TestCase {
    
    public VtsIndexCreatorTest(java.lang.String testName) {
        super(testName);
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
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
        TestSuite suite = new TestSuite(VtsIndexCreatorTest.class);
        
        return suite;
    }
    
}
