package org.mmbase.storage.search.implementation.database;

import junit.framework.*;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.storage.search.*;
import java.util.List;

/**
 * JUnit tests.
 *
 * @author Rob van Maris
 * @version $Revision: 1.1 $
 */
public class VtsQueryHandlerTest extends TestCase {
    
    public VtsQueryHandlerTest(java.lang.String testName) {
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
        TestSuite suite = new TestSuite(VtsQueryHandlerTest.class);
        
        return suite;
    }
    
}
