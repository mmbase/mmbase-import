/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.datatypes;

import junit.framework.*;

/**
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.8
 */
public class DataTypesTest extends TestCase {

    public void setUp() throws Exception {
        DataTypes.initialize();
    }

    public void testString() {

        DataType<?> dt = DataTypes.getDataType("string");

        assertTrue("" + dt.getClass(), dt instanceof StringDataType);

        StringDataType sdt = (StringDataType) dt;

        assertEquals("string", sdt.getName());

        assertTrue(sdt.isFinished());

        assertFalse (sdt.isRequired());

        assertNull(sdt.getDefaultValue());

        try {
            sdt.setRequired(true);
        } catch (IllegalStateException ise) {
        }

        assertEquals("foo", sdt.cast("foo", null, null));

    }



}
