/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.datatypes;

import org.mmbase.datatypes.util.xml.*;
import java.util.Locale;
import org.mmbase.bridge.Field;
import org.mmbase.util.LocalizedString;
import org.mmbase.util.xml.DocumentReader;
import org.mmbase.util.xml.XMLWriter;

import org.xml.sax.InputSource;
import org.w3c.dom.*;
import junit.framework.*;

/**
 * Test cases for DataTypes which can be done stand alone, without usage of an actually running MMBase.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.9
 * @version $Id$
 */
public class DataTypesTest extends TestCase {

    private static boolean setup = false;
    public void setUp() throws Exception {
        LocalizedString.setDefault(new Locale("dk"));
        if (! setup) {
            DataTypes.initialize();
            setup = true;
        }
    }

    private StringDataType getString() {
        DataType<?> dt = DataTypes.getDataType("string");
        assertTrue("" + dt.getClass(), dt instanceof StringDataType);
        return (StringDataType) dt;
    }
    private StringDataType getLine() {
        DataType<?> dt = DataTypes.getDataType("eline");
        assertTrue("" + dt.getClass(), dt instanceof StringDataType);
        return (StringDataType) dt;
    }

    private StringDataType getStringClone() {
        return getString().clone("clone");
    }

    public void testName() {
        assertEquals("string", getString().getName());
        assertEquals("clone", getStringClone().getName());
        assertEquals("eline", getLine().getName());
    }
    public void testGUIName() {
        assertEquals("Tekst", getString().getGUIName(new Locale("nl")));
        assertEquals("Text", getString().getGUIName(new Locale("en")));
        assertEquals("string", getString().getGUIName());

        StringDataType clone = getStringClone();
        assertEquals("Tekst", clone.getGUIName(new Locale("nl")));
        assertEquals("Text", clone.getGUIName(new Locale("en")));
        assertEquals("clone", clone.getLocalizedGUIName().getKey());
        assertEquals(clone.getLocalizedGUIName().getDebugString(), "clone", clone.getLocalizedGUIName().get(null));
        assertEquals("clone", getStringClone().getGUIName(null));
        assertEquals("clone", getStringClone().getGUIName());

        assertEquals("Tekst", getLine().getGUIName(new Locale("nl")));
        assertEquals("Text", getLine().getGUIName(new Locale("en")));
        assertEquals("eline", getLine().getGUIName());

    }

    public void testOrigin() {
        assertNull(getString().getOrigin());
        assertEquals(getString(), getStringClone().getOrigin());
        assertEquals(getString(), getLine().getOrigin());
    }

    public void testBaseTypeIdentifier() {
        assertEquals("string", getString().getBaseTypeIdentifier());
        assertEquals("string", getStringClone().getBaseTypeIdentifier());
        assertEquals("string", getLine().getBaseTypeIdentifier());
    }

    public void testBaseType() {
        assertEquals(Field.TYPE_STRING, getString().getBaseType());
        assertEquals(Field.TYPE_STRING, getStringClone().getBaseType());
        assertEquals(Field.TYPE_STRING, getLine().getBaseType());
    }


    public void testGetTypeAsClass() {
        assertEquals(String.class, getString().getTypeAsClass());
        assertEquals(String.class, getStringClone().getTypeAsClass());
        assertEquals(String.class, getLine().getTypeAsClass());
    }

    public void testCheckType() {
        try {
            getString().checkType(Integer.valueOf(1));
            fail();
        } catch (IllegalArgumentException iae) {
        }
        try {
            getStringClone().checkType(Integer.valueOf(1));
            fail();
        } catch (IllegalArgumentException iae) {
        }
        try {
            getLine().checkType(Integer.valueOf(1));
            fail();
        } catch (IllegalArgumentException iae) {
        }
        getString().checkType("foo");
        getStringClone().checkType("foo");
        getLine().checkType("foo");
    }

    public void testCast() {
        assertEquals("foo", getString().cast("foo", null, null));
        assertEquals("foo", getStringClone().cast("foo", null, null));
        assertEquals("1", getString().cast(new Integer(1), null, null));
        assertEquals("1", getStringClone().cast(new Integer(1), null, null));
        assertEquals("1", getLine().cast(new Integer(1), null, null));

    }

    public void testPreCast() {
        assertEquals("foo", getString().preCast("foo", null, null));
        assertEquals("foo", getStringClone().preCast("foo", null, null));

    }

    public void testDefaultValue() {
        assertNull(getString().getDefaultValue());
        assertNull(getStringClone().getDefaultValue());
        assertNull(getLine().getDefaultValue());
    }


    public void testFinished() {
        assertTrue(getString().isFinished());
        assertFalse(getStringClone().isFinished());

        try {
            getString().setRequired(true);
            fail();
        } catch (IllegalStateException ise) {
        }
        try {
            getString().getLocalizedGUIName().set("bla", new Locale("nl"));
            fail();
        } catch (UnsupportedOperationException ise) {
        }
        getStringClone().setRequired(true);
    }

    public void testRequired() {
        assertFalse(getString().isRequired());
        assertFalse(getStringClone().isRequired());
        StringDataType clone = getStringClone();
        clone.setRequired(true);
        assertTrue(clone.isRequired());
    }


    public void testEnumerationValues() {
        assertNull(getString().getEnumerationValues(null, null, null, null));
        assertNull(getStringClone().getEnumerationValues(null, null, null, null));
    }

    public void testEnumerationValue() {
        assertNull(getString().getEnumerationValue(null, null, null, null, "foo"));
        assertNull(getStringClone().getEnumerationValue(null, null, null, null, "foo"));
    }

    public void testEnumerationFactory() {
        assertNotNull(getString().getEnumerationFactory());
        assertNotNull(getStringClone().getEnumerationFactory());
    }
    public void testEnumerationRestriction() {
        assertNotNull(getString().getEnumerationRestriction());
        assertNotNull(getStringClone().getEnumerationRestriction());
    }

    public void testGetProcessor() {
        // TODO
    }

    protected boolean equals(String s1, String s2) {
        return s1 == null ? s2 == null : s1.equals(s2);
    }
    protected boolean xmlEquivalent(Node el1, Node el2) {
        if (! equals(el1.getNodeName(), el2.getNodeName())) {
            return false;
        }
        NodeList nl1 = el1.getChildNodes();
        NodeList nl2 = el2.getChildNodes();
        if (nl1.getLength() != nl2.getLength()) return false;
        for (int i = 0 ; i < nl1.getLength(); i++) {
            Node child1 = nl1.item(i);
            Node child2 = nl2.item(i);
            if (! xmlEquivalent(child1, child2)) return false;
        }
        return true;
    }

    protected void testXml(String xml, boolean mustBeEqual) throws Exception {
        DocumentReader reader = new DocumentReader(new InputSource(new java.io.StringReader(xml)), true, DataTypeReader.class);
        DataType dt = DataTypeReader.readDataType(reader.getDocument().getDocumentElement(), null, null).dataType.clone();
        Element toXml = dt.toXml();
        boolean equiv = xmlEquivalent(reader.getDocument().getDocumentElement(), toXml);;

        if (mustBeEqual) {
            assertTrue("" + xml + " != " + XMLWriter.write(toXml), equiv);
        } else {
            assertFalse("" + xml + " == " + XMLWriter.write(toXml), equiv);
        }
        if (equiv) {
            System.out.println("" + xml + " " + XMLWriter.write(toXml));
        }
    }


    public void testXml1() throws Exception {
        testXml("<datatype base='string' />", true);
    }
    public void testXml2() throws Exception {
        testXml("<datatype base='string'><name>foo</name></datatype>", true);
    }
    public void testXml3() throws Exception {
        testXml("<datatype base='string'><description>bar</description></datatype>", true);
    }

    public void testXml4() throws Exception {
        testXml("<datatype base='string'><description>bar</description><enumeration><entry value='a' /></enumeration></datatype>", true);
    }

    public void testXml5() throws Exception {
        testXml("<datatype base='string'><description>bar</description><default value='bar' /><unique value='true' /></datatype>", true);
    }

    public void testXml6() throws Exception {
        testXml("<datatype base='string'><description>bar</description><default value='bar' /><unique value='true' /><required value='true' /></datatype>", true);
    }

    public void testXml7() throws Exception {
        testXml("<datatype base='string'><description>bar</description><default value='bar' /><required value='true' /><unique value='true' /></datatype>", false);
    }




}
