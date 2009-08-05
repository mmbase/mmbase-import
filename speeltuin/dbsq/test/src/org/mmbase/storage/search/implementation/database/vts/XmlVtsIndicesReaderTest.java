package org.mmbase.storage.search.implementation.database.vts;

import junit.framework.*;
import java.io.*;
import java.util.*;
import org.mmbase.util.XMLBasicReader;
import org.w3c.dom.*;
import org.xml.sax.*;

/**
 * JUnit tests.
 *
 * @author Rob van Maris
 * @version $Revision: 1.2 $
 */
public class XmlVtsIndicesReaderTest extends TestCase {
    
    /** Test xml. */
    private static String TEST_XML =
        "<?xml version='1.0' encoding='UTF-8' ?>" +
        "<!DOCTYPE vtsindices PUBLIC \"-//MMBase//DTD vtsindices config//EN\"" + 
        "    \"http://www.mmbase.org/dtd/vtsindices.dtd\">" +
        "<!-- comment -->" +
        "<vtsindices nolog=\"TRUE\"><!-- comment -->" +
        "    <sbspace name='sbspace0'><!-- comment -->" +
        "        <vtsindex table='test_movies' field=\"title\"><!-- comment -->movies_title_vts</vtsindex>" +
        "        <!-- comment -->" +
        "        <vtsindex table='test_news' field='body'>news_body_testidx2_<!-- comment --></vtsindex>" +
        "    </sbspace>" +    
        "    <!-- comment -->" +
        "    <sbspace name='sbspace1'/>" +
        "</vtsindices>";    
    
    /** Test instance. */
    private XmlVtsIndicesReader instance = null;
        
    public XmlVtsIndicesReaderTest(java.lang.String testName) {
        super(testName);
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    /**
     * Sets up before each test.
     */
    public void setUp() throws Exception {
        instance = new XmlVtsIndicesReader(
            new InputSource(new StringReader(TEST_XML)));
    }
    
    /**
     * Tears down after each test.
     */
    public void tearDown() throws Exception {}
    
    /** Test of getNolog method, of class org.mmbase.storage.search.implementation.database.vts.XmlVtsIndicesReader. */
    public void testGetNolog() {
        assertTrue(instance.getNolog());
    }
    
    /** Test of getSbspaceElements method, of class org.mmbase.storage.search.implementation.database.vts.XmlVtsIndicesReader. */
    public void testGetSbspaceElements() {
        Enumeration eSbspaces = instance.getSbspaceElements();
        assertTrue(eSbspaces.hasMoreElements());
        Element sbspace = (Element) eSbspaces.nextElement();
        assertTrue(sbspace.getAttribute("name").equals("sbspace0"));
        assertTrue(eSbspaces.hasMoreElements());
        sbspace = (Element) eSbspaces.nextElement();
        assertTrue(sbspace.getAttribute("name").equals("sbspace1"));
        assertTrue(!eSbspaces.hasMoreElements());
    }
    
    /** Test of getSbspaceName method, of class org.mmbase.storage.search.implementation.database.vts.XmlVtsIndicesReader. */
    public void testGetSbspaceName() {
        Enumeration eSbspaces = instance.getSbspaceElements();
        Element sbspace = (Element) eSbspaces.nextElement();
        assertTrue(instance.getSbspaceName(sbspace).equals("sbspace0"));
        sbspace = (Element) eSbspaces.nextElement();
        assertTrue(instance.getSbspaceName(sbspace).equals("sbspace1"));
    }
    
    /** Test of getVtsindexElements method, of class org.mmbase.storage.search.implementation.database.vts.XmlVtsIndicesReader. */
    public void testGetVtsindexElements() {
        Element sbspace0 = instance.getElementByPath("vtsindices.sbspace");
        Enumeration eIndices = instance.getVtsindexElements(sbspace0);
        assertTrue(eIndices.hasMoreElements());
        Element index = (Element) eIndices.nextElement();
        assertTrue(instance.getVtsindexValue(index).equals("movies_title_vts"));
        assertTrue(eIndices.hasMoreElements());
        index = (Element) eIndices.nextElement();
        assertTrue(instance.getVtsindexValue(index).equals("news_body_testidx2_"));
        assertTrue(!eIndices.hasMoreElements());
    }
    
    /** Test of getVtsindexTable method, of class org.mmbase.storage.search.implementation.database.vts.XmlVtsIndicesReader. */
    public void testGetVtsindexTable() {
        Element sbspace0 = instance.getElementByPath("vtsindices.sbspace");
        Enumeration eIndices = instance.getVtsindexElements(sbspace0);
        Element index = (Element) eIndices.nextElement();
        assertTrue(instance.getVtsindexTable(index).equals("test_movies"));
        index = (Element) eIndices.nextElement();
        assertTrue(instance.getVtsindexTable(index).equals("test_news"));
    }
    
    /** Test of getVtsindexField method, of class org.mmbase.storage.search.implementation.database.vts.XmlVtsIndicesReader. */
    public void testGetVtsindexField() {
        Element sbspace0 = instance.getElementByPath("vtsindices.sbspace");
        Enumeration eIndices = instance.getVtsindexElements(sbspace0);
        Element index = (Element) eIndices.nextElement();
        assertTrue(instance.getVtsindexField(index).equals("title"));
        index = (Element) eIndices.nextElement();
        assertTrue(instance.getVtsindexField(index).equals("body"));
    }
    
    /** Test of getVtsindexValue method, of class org.mmbase.storage.search.implementation.database.vts.XmlVtsIndicesReader. */
    public void testGetVtsindexValue() {
        Element sbspace0 = instance.getElementByPath("vtsindices.sbspace");
        Enumeration eIndices = instance.getVtsindexElements(sbspace0);
        Element index = (Element) eIndices.nextElement();
        assertTrue(instance.getVtsindexValue(index).equals("movies_title_vts"));
        index = (Element) eIndices.nextElement();
        assertTrue(instance.getVtsindexValue(index).equals("news_body_testidx2_"));
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(XmlVtsIndicesReaderTest.class);
        
        return suite;
    }
    
}
