/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;
import org.mmbase.util.xml.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import java.io.*;
import java.util.*;
import junit.framework.TestCase;

/**
 *
 * @author Michiel Meeuwissen
 * @verion $Id$
 */
public class LocalizedEntryListFactoryTest extends TestCase {

    public static final Locale NL = new Locale("nl");
    public static final Locale BE = new Locale("nl", "BE");
    public static final Locale BE_VAR = new Locale("nl", "BE", "a_b");
    public static final Locale EN = new Locale("en", "GB");
    public static final Locale DK = new Locale("dk");
    public static final Locale EO = new Locale("eo");

    public void testExplicitEntries() {
        LocalizedEntryListFactory fact = new LocalizedEntryListFactory();
        fact.add(NL, "a", "hallo");
        fact.add(BE, "b", "saluut");
        fact.add(EN, "a", "hello");
        fact.add(EO, "a", "saluton");
        fact.add(NL, "b", "hoi");
        fact.add(EN, "b", "hi");
        assertTrue(fact.size() == 2); // a and b.
        {
            List<Object> col  = Arrays.asList(new Object[] { new Entry("a", "hallo"), new Entry("b", "hoi")});
            assertEquals(col, fact.get(NL));
        }
        {
            List<Object> col  = Arrays.asList(new Object[] { new Entry("a", "hallo"), new Entry("b", "saluut")});
            assertEquals(col, fact.get(BE));
            assertEquals(col, fact.get(BE_VAR));

        }
        {
            Collection<Object> col  = Arrays.asList(new Object[] { new Entry("a", "hello"), new Entry("b", "hi")});
            assertEquals(col, fact.get(EN));
        }
        assertEquals("a", fact.castKey("a"));
        assertEquals("xxxx", fact.castKey("xxxx"));
    }

    public void testBundleEntries() {
        LocalizedEntryListFactory fact = new LocalizedEntryListFactory();
        String resource1 = "org.mmbase.datatypes.resources.boolean.truefalse";
        fact.addBundle(resource1, null, null, Boolean.class, SortedBundle.NO_COMPARATOR);
        assertTrue(fact.size() == 2); // false and true
        {
            Collection<Object> col  = Arrays.asList(new Object[] { new Entry(Boolean.FALSE, "onwaar"), new Entry(Boolean.TRUE, "waar")});
            assertEquals(col, fact.get(NL));
            assertEquals(col, fact.get(BE));
        }
        {
            Collection<Object> col  = Arrays.asList(new Object[] { new Entry(Boolean.FALSE, "false"), new Entry(Boolean.TRUE, "true")});
            assertEquals(col, fact.get(EN));
        }
    }

    public void testCastKey() {
        LocalizedEntryListFactory fact = new LocalizedEntryListFactory();
        String resource1 = "org.mmbase.datatypes.resources.boolean.truefalse";
        fact.addBundle(resource1, null, null, Boolean.class, SortedBundle.NO_COMPARATOR);

        assertEquals(Boolean.FALSE, fact.castKey("false"));
        assertEquals(Boolean.FALSE, fact.castKey("0"));
        assertEquals(Boolean.TRUE,  fact.castKey("true"));
        assertEquals(Boolean.TRUE,  fact.castKey("1"));
        assertEquals("xxxx",        fact.castKey("xxxx")); // not recognized, don't touch
    }
    public void testCastKey1() {
        LocalizedEntryListFactory fact = new LocalizedEntryListFactory();
        String resource1 = "org.mmbase.datatypes.resources.boolean.truefalse";
        fact.addBundle(resource1, null, null, String.class, SortedBundle.NO_COMPARATOR);

        assertEquals("false", fact.castKey("false"));
        assertEquals("0",     fact.castKey("0"));
        assertEquals("true",  fact.castKey("true"));
        assertEquals("1",     fact.castKey("1"));
        assertEquals("xxxx",  fact.castKey("xxxx"));
    }

    public void testReadXml() throws org.xml.sax.SAXException, java.io.IOException {
        LocalizedEntryListFactory fact = new LocalizedEntryListFactory();
        String config =
            "<enumeration>" +
            "  <entry value='' display='unfilled' />" +
            "  <entry value='1' display='one' />" +
            "  <entry value='2' display='two' />" +
            "</enumeration>";
        Document doc = DocumentReader.getDocumentBuilder(false).parse(new InputSource(new StringReader(config)));
        fact.fillFromXml(doc.getDocumentElement(), null);
        assertTrue(fact.size() == 3); // '' '1' and '2'

        assertEquals("", fact.castKey(""));
        assertEquals(null, fact.castKey(null));
        assertEquals(new Integer(1), fact.castKey(new Integer(1)));

    }
    public void testReadXml2() throws org.xml.sax.SAXException, java.io.IOException {
        LocalizedEntryListFactory fact = new LocalizedEntryListFactory();
        String config =
            "<enumeration>" +
            "  <entry basename='org.mmbase.datatypes.resources.boolean.truefalse' />" +
            "</enumeration>";
        Document doc = DocumentReader.getDocumentBuilder(false).parse(new InputSource(new StringReader(config)));
        fact.fillFromXml(doc.getDocumentElement(), Boolean.class);

        assertEquals(Boolean.FALSE, fact.castKey("false"));
        assertEquals(Boolean.FALSE, fact.castKey("0"));
        assertEquals(Boolean.TRUE,  fact.castKey("true"));
        assertEquals(Boolean.TRUE,  fact.castKey("1"));
        assertEquals("xxxx"       , fact.castKey("xxxx"));

        // now change wrap type

        Element xml = fact.toXml();
        fact.clear();
        fact.fillFromXml(xml, String.class);
        assertEquals("false", fact.castKey("false"));
        assertEquals("0",     fact.castKey("0"));
        assertEquals("true",  fact.castKey("true"));
        assertEquals("1",     fact.castKey("1"));
        assertEquals("xxxx",  fact.castKey("xxxx"));

    }
    public void testReadXml3() throws org.xml.sax.SAXException, java.io.IOException {
        LocalizedEntryListFactory fact = new LocalizedEntryListFactory();
        String config =
            "<enumeration>" +
            "<entry basename='org.mmbase.datatypes.resources.states'" +
            "       javaconstants='org.mmbase.datatypes.resources.StateConstants'" +
            "       />" +
            "</enumeration>";
        Document doc = DocumentReader.getDocumentBuilder(false).parse(new InputSource(new StringReader(config)));
        fact.fillFromXml(doc.getDocumentElement(), Integer.class);

        assertEquals(new Integer(-1), fact.castKey("UNKNOWN"));
        assertEquals(new Integer(-1), fact.castKey("unknown"));
        assertEquals(new Integer(3),  fact.castKey("error"));
        assertEquals("blabla",        fact.castKey("blabla"));
    }

}
