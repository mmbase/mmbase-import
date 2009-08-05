import junit.framework.*;

/**
 * JUnit tests.
 *
 * @author Rob van Maris
 * @version $Revision: 1.2 $
 */
public class BaseSuite extends TestCase {
    
    public BaseSuite(java.lang.String testName) {
        super(testName);
    }
    
    // Takes mmbase.config value as arg.
    public static void main(java.lang.String[] args) {
        if (args.length == 1) {
           System.setProperty("mmbase.config", args[0]);
        }
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
        TestSuite suite = new TestSuite("BaseSuite");
        suite.addTest(org.mmbase.module.core.CoreSuite.suite());
        suite.addTest(org.mmbase.module.database.support.SupportSuite.suite());
        suite.addTest(org.mmbase.storage.search.implementation.ImplementationSuite.suite());
        suite.addTest(org.mmbase.storage.search.legacy.LegacySuite.suite());
        suite.addTest(org.mmbase.util.UtilSuite.suite());
        return suite;
    }
    
}
