/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.datatypes;

import org.mmbase.util.*;
import java.util.*;
import java.io.*;

import org.junit.*;
import static org.junit.Assert.*;

/**
 * Test cases for DataTypes which can be done stand alone, without usage of an actually running MMBase.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-2.0
 * @version $Id: DataTypesTest.java 40400 2010-01-07 15:55:35Z michiel $
 */
public class BinaryDataTypeTest  {
    private static final byte[] GIF = new byte[]{
             0x47, 0x49, 0x46, 0x38, 0x39, 0x61, 0x01, 0x00, 0x01, 0x00,
             (byte)0x80, 0x00, 0x00, (byte)0xff, (byte)0xff, (byte)0xff,
             0x00, 0x00, 0x00, 0x21, (byte)0xf9, 0x04, 0x00, 0x00, 0x00,
             0x00, 0x00, 0x2c, 0x00, 0x00, 0x00, 0x00,
             0x01, 0x00, 0x01, 0x00, 0x40, 0x02, 0x02, 0x44, 0x01, 0x00,
             0x3b, 0x0a };

    @BeforeClass
    public static void setUp() throws Exception {
        LocalizedString.setDefault(new Locale("da"));
        DataTypes.initialize();
    }

    @Test
    public void getMimeType() {
        BinaryDataType dt = (BinaryDataType) DataTypes.getDataType("binary");

        assertTrue(new MimeType(MimeType.STAR, MimeType.STAR).matches(dt.getMimeType(null, null, null)));
        assertEquals(MimeType.OCTETSTREAM, dt.getMimeType(null, null, null));


        assertTrue(new MimeType("image", MimeType.STAR).matches(dt.getMimeType(GIF, null, null)));
        assertEquals(new MimeType("image", "gif"), dt.getMimeType(GIF, null, null));

        assertTrue(new MimeType("image", MimeType.STAR).matches(dt.getMimeType(new SerializableInputStream(GIF), null, null)));
        assertEquals(new MimeType("image", "gif"), dt.getMimeType(new SerializableInputStream(GIF), null, null));

        assertTrue(new MimeType("image", MimeType.STAR).matches(dt.getMimeType(new ByteArrayInputStream(GIF), null, null)));
        assertEquals(new MimeType("image", "gif"), dt.getMimeType(new ByteArrayInputStream(GIF), null, null)); //FAILS

        assertEquals(MimeType.OCTETSTREAM, dt.getMimeType(new byte[] {0, 1, 2}, null, null));
        assertEquals(MimeType.OCTETSTREAM, dt.getMimeType(new byte[] {1, 2, 3, 4}, null, null));
        assertEquals(MimeType.OCTETSTREAM, dt.getMimeType(new byte[0], null, null));
        assertEquals(MimeType.OCTETSTREAM, dt.getMimeType(new NullInputStream(100), null, null));

    }

    @Test
    public void restrictedBinary() {
        DataType restrictedBinary = DataTypes.getDataType("restricted_binary");
        assertNotNull(restrictedBinary);
        assertTrue(restrictedBinary instanceof BinaryDataType);
        assertEquals(0, restrictedBinary.validate(new byte[] { 0, 1, 2, }, null, null).size());
        assertEquals(1, restrictedBinary.validate(null, null, null).size());
        assertFalse(restrictedBinary.validate(new byte[0], null, null).size() == 0);
        assertFalse(restrictedBinary.validate(new NullInputStream(201), null, null).size() == 0);
        assertTrue(restrictedBinary.validate(new NullInputStream(199), null, null).size() == 0);
    }


    @Test
    public void mimeTypeRestrictedBinary() {
        BinaryDataType restrictedBinary = (BinaryDataType) DataTypes.getDataType("mimetype_restricted_binary");
        assertNotNull(restrictedBinary);

        assertFalse(restrictedBinary.mimeTypeRestriction.simpleValid(new byte[] { 0, 1, 2, }, null, null));
        assertEquals(1, restrictedBinary.castAndValidate(new byte[] { 0, 1, 2}, null, null).size());

    }




}
