package org.mmbase.module.database.support;

import junit.framework.*;
import java.util.*;
import java.io.*;
import java.sql.*;
import org.mmbase.storage.database.UnsupportedDatabaseOperationException;
import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;

/**
 * JUnit tests.
 *
 * @author Rob van Maris
 * @version $Revision: 1.1 $
 */
public class MMSQL92NodeTest extends TestCase {
    
    /** Test instance. */
    private MMSQL92Node instance = null;
    
    private MMBase mmbase = null;
    private MMObjectBuilder builder = null;
    
    public MMSQL92NodeTest(java.lang.String testName) {
        super(testName);
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    /**
     * Sets up before each test.
     */
    public void setUp() throws Exception {
        MMBaseContext.init();
        mmbase = MMBase.getMMBase();
        builder = mmbase.getBuilder("images");
        instance = (MMSQL92Node) mmbase.getDatabase();
    }
    
    /**
     * Tears down after each test.
     */
    public void tearDown() throws Exception {}
    
    public static Test suite() {
        TestSuite suite = new TestSuite(MMSQL92NodeTest.class);
        
        return suite;
    }
    
    /** Test of getMMNodeSearch2SQL method, of class org.mmbase.module.database.support.MMSQL92Node. */
    public void testGetMMNodeSearch2SQL() {
        String result = null;
        result = instance.getMMNodeSearch2SQL("MMNODE number=Evalue", builder);
        assertTrue(result, result.equals("number=value"));
        result = instance.getMMNodeSearch2SQL("MMNODE number=Evalue", builder);
        assertTrue(result, result.equals("number=value"));
        result = instance.getMMNodeSearch2SQL("MMNODE number=Evalue+title==value", builder);
        assertTrue(result, result.equals("number=value AND lower(title) LIKE '%value%'"));
        result = instance.getMMNodeSearch2SQL("MMNODE number=Evalue-title==value", builder);
        assertTrue(result, result.equals("number=value AND NOT lower(title) LIKE '%value%'"));
        result = instance.getMMNodeSearch2SQL("MMNODE number=Evalue\ntitle==value", builder);
        assertTrue(result, result.equals("number=value AND NOT lower(title) LIKE '%value%'"));
        result = instance.getMMNodeSearch2SQL("MMNODE number=Evalue\rtitle==value", builder);
        assertTrue(result, result.equals("number=value AND NOT lower(title) LIKE '%value%'"));
    }
    
    /** Test of parseFieldPart method, of class org.mmbase.module.database.support.MMSQL92Node. */
    public void testParseFieldPart() {
        String result = null;
        
        result = instance.parseFieldPart("field1", FieldDefs.TYPE_STRING, "=value");
        assertTrue(result, result.equals("lower(field1) LIKE '%value%'"));
        result = instance.parseFieldPart("field1", FieldDefs.TYPE_STRING, "Evalue");
        assertTrue(result, result.equals("lower(field1) LIKE '%value%'"));
        result = instance.parseFieldPart("field1", FieldDefs.TYPE_STRING, "=*alu*");
        assertTrue(result, result.equals("lower(field1) LIKE '%alu%'"));
        result = instance.parseFieldPart("field1", FieldDefs.TYPE_STRING, "E*alu*");
        assertTrue(result, result.equals("lower(field1) LIKE '%alu%'"));
        result = instance.parseFieldPart("field1", FieldDefs.TYPE_STRING, "Nvalue");
        assertTrue(result, result.equals(""));
        // same for TYPE_XML
        
        result = instance.parseFieldPart("field1", FieldDefs.TYPE_LONG, "=value");
        assertTrue(result, result.equals("field1=value"));
        result = instance.parseFieldPart("field1", FieldDefs.TYPE_LONG, "Evalue");
        assertTrue(result, result.equals("field1=value"));
        result = instance.parseFieldPart("field1", FieldDefs.TYPE_LONG, "=*alu*");
        assertTrue(result, result.equals("field1=alu"));    // not intentional
        result = instance.parseFieldPart("field1", FieldDefs.TYPE_LONG, "E*alu*");
        assertTrue(result, result.equals("field1=alu"));    // not intentional
        result = instance.parseFieldPart("field1", FieldDefs.TYPE_LONG, "Nvalue");
        assertTrue(result, result.equals("field1<>value"));
        result = instance.parseFieldPart("field1", FieldDefs.TYPE_LONG, "Gvalue");
        assertTrue(result, result.equals("field1>value"));
        result = instance.parseFieldPart("field1", FieldDefs.TYPE_LONG, "gvalue");
        assertTrue(result, result.equals("field1>=value"));
        result = instance.parseFieldPart("field1", FieldDefs.TYPE_LONG, "Svalue");
        assertTrue(result, result.equals("field1<value"));
        result = instance.parseFieldPart("field1", FieldDefs.TYPE_LONG, "svalue");
        assertTrue(result, result.equals("field1<=value"));
        result = instance.parseFieldPart("field1", FieldDefs.TYPE_LONG, "Lvalue");
        assertTrue(result, result.equals(""));
        // same for TYPE_NODE and TYPE_INTEGER
    }
    
}
