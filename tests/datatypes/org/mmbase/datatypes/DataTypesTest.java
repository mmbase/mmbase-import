/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.datatypes;

import java.util.Locale;
import org.mmbase.bridge.Field;
import org.mmbase.util.LocalizedString;
import junit.framework.*;

/**
 * Test cases for DataTypes which can be done stand alone, with usage of an actually running MMBase.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.9
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
        } catch (IllegalStateException ise) {
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




}
