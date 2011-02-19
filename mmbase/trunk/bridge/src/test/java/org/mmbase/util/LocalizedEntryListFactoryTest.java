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
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Michiel Meeuwissen
 * @verion $Id$
 */
public class LocalizedEntryListFactoryTest {

    public static final Locale NL = new Locale("nl");
    public static final Locale BE = new Locale("nl", "BE");
    public static final Locale BE_VAR = new Locale("nl", "BE", "a_b");
    public static final Locale EN = new Locale("en", "GB");
    public static final Locale DK = new Locale("dk");
    public static final Locale EO = new Locale("eo");

    @Test
    public void explicitEntries() {
        LocalizedEntryListFactory fact = new LocalizedEntryListFactory();
        fact.add(NL, "a", "hallo");
        fact.add(BE, "b", "saluut");
        fact.add(EN, "a", "hello");
        fact.add(EO, "a", "saluton");
        fact.add(NL, "b", "hoi");
        fact.add(EN, "b", "hi");
        assertTrue(fact.size() == 2); // a and b.
        {
            List<? extends Map.Entry> col  = Arrays.asList(new Entry("a", "hallo"), new Entry("b", "hoi"));
            assertEquals(col, fact.get(NL));
        }
        {
            List<? extends Map.Entry> col  = Arrays.asList(new Entry("a", "hallo"), new Entry("b", "saluut"));
            assertEquals(col, fact.get(BE));
            assertEquals(col, fact.get(BE_VAR));

        }
        {
            Collection<? extends Map.Entry> col  = Arrays.asList(new Entry("a", "hello"), new Entry("b", "hi"));
            assertEquals(col, fact.get(EN));
        }
        assertEquals("a", fact.castKey("a"));
        assertEquals("xxxx", fact.castKey("xxxx"));
    }

    @Test
    public void bundleEntries() {
        LocalizedEntryListFactory fact = new LocalizedEntryListFactory();
        String resource1 = "org.mmbase.datatypes.resources.boolean.truefalse";
        fact.addBundle(resource1, null, null, Boolean.class, SortedBundle.NO_COMPARATOR);
        assertTrue(fact.size() == 2); // false and true
        {
            Collection<? extends Map.Entry> col  = Arrays.asList(new Entry(Boolean.FALSE, "onwaar"), new Entry(Boolean.TRUE, "waar"));
            assertEquals(col, fact.get(NL));
            assertEquals(col, fact.get(BE));
        }
        {
            Collection<? extends Map.Entry> col  = Arrays.asList(new Entry(Boolean.FALSE, "false"), new Entry(Boolean.TRUE, "true"));
            assertEquals(col, fact.get(EN));
        }
    }

    @Test
    public void castKey() {
        LocalizedEntryListFactory fact = new LocalizedEntryListFactory();
        String resource1 = "org.mmbase.datatypes.resources.boolean.truefalse";
        fact.addBundle(resource1, null, null, Boolean.class, SortedBundle.NO_COMPARATOR);

        assertEquals(Boolean.FALSE, fact.castKey("false"));
        assertEquals(Boolean.FALSE, fact.castKey("0"));
        assertEquals(Boolean.TRUE,  fact.castKey("true"));
        assertEquals(Boolean.TRUE,  fact.castKey("1"));
        assertEquals("xxxx",        fact.castKey("xxxx")); // not recognized, don't touch
    }
    @Test
    public void castKey1() {
        LocalizedEntryListFactory fact = new LocalizedEntryListFactory();
        String resource1 = "org.mmbase.datatypes.resources.boolean.truefalse";
        fact.addBundle(resource1, null, null, String.class, SortedBundle.NO_COMPARATOR);

        assertEquals("false", fact.castKey("false"));
        assertEquals("0",     fact.castKey("0"));
        assertEquals("true",  fact.castKey("true"));
        assertEquals("1",     fact.castKey("1"));
        assertEquals("xxxx",  fact.castKey("xxxx"));
    }

    @Test
    public void readXml() throws org.xml.sax.SAXException, java.io.IOException {
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
        assertEquals(1, fact.castKey(1));

    }
    @Test
    public void readXml2() throws org.xml.sax.SAXException, java.io.IOException {
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
    @Test
    public void readXml3() throws org.xml.sax.SAXException, java.io.IOException {
        LocalizedEntryListFactory fact = new LocalizedEntryListFactory();
        String config =
            "<enumeration>" +
            "<entry basename='org.mmbase.datatypes.resources.states'" +
            "       javaconstants='org.mmbase.datatypes.resources.StateConstants'" +
            "       />" +
            "</enumeration>";
        Document doc = DocumentReader.getDocumentBuilder(false).parse(new InputSource(new StringReader(config)));
        fact.fillFromXml(doc.getDocumentElement(), Integer.class);

        assertEquals(-1, fact.castKey("UNKNOWN"));
        assertEquals(-1, fact.castKey("unknown"));
        assertEquals(3,  fact.castKey("error"));
        assertEquals("blabla",        fact.castKey("blabla"));
    }


    @Test
    public void readEnum() throws org.xml.sax.SAXException, java.io.IOException {
        LocalizedEntryListFactory fact = new LocalizedEntryListFactory();
        String config =
            "<enumeration>" +
            "<entry " +
            "       javaconstants='org.mmbase.framework.WindowState'" +
            "       />" +
            "</enumeration>";
        Document doc = DocumentReader.getDocumentBuilder(false).parse(new InputSource(new StringReader(config)));
        fact.fillFromXml(doc.getDocumentElement(), Integer.class);

        assertEquals(3, fact.size());
        assertEquals(3, fact.size(null));

    }



    public static void main(String argv[]) {
        LocalizedEntryListFactory fact = new LocalizedEntryListFactory();
        String resource1 = "org.mmbase.datatypes.resources.boolean.onoff";
        String resource2 = "org.mmbase.datatypes.resources.boolean.yesno";
        Locale nl = new Locale("nl");
        Locale en = new Locale("en");
        Locale dk = new Locale("dk");
        Locale eo = new Locale("eo");
        fact.add(nl, "a", "hallo");
        System.out.println("nou " + fact);
        fact.add(new Locale("nl"), "b", "daag");
        fact.add(en, "b", "hello");
        fact.add(en, "a", "good bye");
        fact.addBundle(resource1, null, null, Boolean.class, SortedBundle.NO_COMPARATOR);
        fact.add(nl, "c", "doegg");
        fact.add(dk, 5, "dk");
        fact.add(null, "e", "oi");
        fact.addBundle(resource2, null, null, String.class, SortedBundle.NO_COMPARATOR);

        System.out.println("size: " + fact.size() + " " + fact);
        System.out.println("en" + fact.get(en));
        System.out.println("nl" + fact.get(nl));
        System.out.println("dk" + fact.get(dk));
        System.out.println("eo" + fact.get(eo));

        /** TODO need anothe rtest for this.
        LocalizedEntryListFactory fact2 = new LocalizedEntryListFactory();
        fact2.addBundle("org.mmbase.datatypes.resources.states", null, org.mmbase.module.builders.MMServers.class, SortedBundle.NO_WRAPPER, SortedBundle.NO_COMPARATOR);

        System.out.println("size: " + fact2.size());
        System.out.println("" + fact2.get(en));
        System.out.println("" + fact2.get(nl));
        Object error = fact2.castKey("ERROR", null);
        System.out.println("ERROR=" + error.getClass().getName() + " " + error);
        */

    }

}
