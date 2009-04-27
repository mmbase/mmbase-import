/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.datatypes;

import java.util.Locale;
import org.mmbase.bridge.Field;
import junit.framework.*;

/**
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.8
 */
public class DataTypesTest extends TestCase {

    private static boolean setup = false;
    public void setUp() throws Exception {
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

    private StringDataType getStringClone() {
        return getString().clone("clone");
    }

    public void testName() {
        assertEquals("string", getString().getName());
        assertEquals("clone", getStringClone().getName());
    }
    public void testGUIName() {
        assertEquals("Tekst", getString().getGUIName(new Locale("nl")));
        assertEquals("Text", getString().getGUIName(new Locale("en")));
        assertEquals("Tekst", getStringClone().getGUIName(new Locale("nl")));
        assertEquals("Text", getStringClone().getGUIName(new Locale("en")));
    }

    public void testOrigin() {
        assertNull(getString().getOrigin());
        assertEquals(getString(), getStringClone().getOrigin());
    }

    public void testBaseTypeIdentifier() {
        assertEquals("string", getString().getBaseTypeIdentifier());
        assertEquals("string", getStringClone().getBaseTypeIdentifier());
    }

    public void testBaseType() {
        assertEquals(Field.TYPE_STRING, getString().getBaseType());
        assertEquals(Field.TYPE_STRING, getStringClone().getBaseType());
    }


    public void testGetTypeAsClass() {
        assertEquals(String.class, getString().getTypeAsClass());
        assertEquals(String.class, getStringClone().getTypeAsClass());
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
        getString().checkType("foo");
        getStringClone().checkType("foo");
    }

    public void testCast() {
        assertEquals("foo", getString().cast("foo", null, null));
        assertEquals("foo", getStringClone().cast("foo", null, null));
        assertEquals("1", getString().cast(new Integer(1), null, null));
        assertEquals("1", getStringClone().cast(new Integer(1), null, null));

    }

    public void testPreCast() {
        assertEquals("foo", getString().preCast("foo", null, null));
        assertEquals("foo", getStringClone().preCast("foo", null, null));

    }

    public void testDefaultValue() {
        assertNull(getString().getDefaultValue());
        assertNull(getStringClone().getDefaultValue());
    }


    public void testFinished() {
        assertTrue(getString().isFinished());
        assertFalse(getStringClone().isFinished());

        try {
            getString().setRequired(true);
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
