package org.mmbase.util;

import junit.framework.*;
import java.io.*;
import java.util.*;
import org.w3c.dom.*;
import org.mmbase.module.corebuilders.FieldDefs;
import org.mmbase.module.database.support.dTypeInfo;
import org.mmbase.module.database.support.dTypeInfos;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.xml.sax.*;

/**
 * JUnit tests.
 *
 * @author Rob van Maris
 * @version $Revision: 1.1 $
 */
public class XMLDatabaseReaderTest extends TestCase {
    
    private static String TEST_HANDLER = 
        "org.mmbase.storage.search.implementation.database.MySqlSqlHandler";
    private final static String TEST_CHAINEDSQLHANDLER1 = "skljdfiejn";
    private final static String TEST_CHAINEDSQLHANDLER2 = "einvlidlje";
    
    /** Test xml. */
    private static String TEST_XML =
        "<?xml version='1.0' encoding='UTF-8'?>" + 
        "<!DOCTYPE database PUBLIC '-//MMBase/DTD database config 1.2//EN' " + 
        "    'http://www.mmbase.org/dtd/database_1_2.dtd'>" + 
        "<database>" + 
        "  <name>MySQL</name>" + 
        "  <mmbasedriver>org.mmbase.module.database.support.MMMysql42Node</mmbasedriver>" + 
        "  <sqlhandler>" + TEST_HANDLER + "</sqlhandler>" + 
        "  <chainedsqlhandler>" + TEST_CHAINEDSQLHANDLER1 + "</chainedsqlhandler>" + 
        "  <chainedsqlhandler>" + TEST_CHAINEDSQLHANDLER2 + "</chainedsqlhandler>" + 
        "</database>";

    /** Test instance. */
    private XMLDatabaseReader instance = null;
        
    public XMLDatabaseReaderTest(java.lang.String testName) {
        super(testName);
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    /**
     * Sets up before each test.
     */
    public void setUp() throws Exception {
        instance = new XMLDatabaseReader(
            new InputSource(new StringReader(TEST_XML)));
    }
    
    /**
     * Tears down after each test.
     */
    public void tearDown() throws Exception {}
    
    public static Test suite() {
        TestSuite suite = new TestSuite(XMLDatabaseReaderTest.class);
        
        return suite;
    }
    
    /** Test of getSqlHandler method, of class org.mmbase.util.XMLDatabaseReader. */
    public void testGetSqlHandler() {
        assertTrue(instance.getSqlHandler().equals(TEST_HANDLER));
    }
    
    /** Test of getChainedSqlHandlers method, of class org.mmbase.util.XMLDatabaseReader. */
    public void testGetChainedSqlHandlers() {
        List handlers = instance.getChainedSqlHandlers();
        assertTrue(handlers.size() == 2);
        assertTrue(handlers.get(0).equals(TEST_CHAINEDSQLHANDLER1));
        assertTrue(handlers.get(1).equals(TEST_CHAINEDSQLHANDLER2));
    }
}
