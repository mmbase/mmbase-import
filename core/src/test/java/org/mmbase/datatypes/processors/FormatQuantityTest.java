/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.processors;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**


 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9
 */

public class FormatQuantityTest {


    @BeforeClass
    public static void setUp() throws Exception {
        org.mmbase.util.LocalizedString.setDefault(new java.util.Locale("dk"));
    }


    @Test
    public void fileSize() {
        FormatQuantity fileSize = new FormatFileSize();
        assertEquals("123 B", "" + fileSize.process(null, null, "123"));
        assertEquals("123 B", "" + fileSize.process(null, null, 123));
        assertEquals("3.0 KiB", "" + fileSize.process(null, null, "3061"));
        assertEquals("3.2 KiB", "" + fileSize.process(null, null, "3261"));
        assertEquals("0 B", "" + fileSize.process(null, null, 0));
        assertEquals(null, fileSize.process(null, null, null));
        assertEquals("-1 B", "" + fileSize.process(null, null, -1));
        assertEquals("0 B", "" + fileSize.process(null, null, 0.1));
    }


    @Test
    public void fileSizeClassical() {
        FormatFileSize fileSize = new FormatFileSize();
        fileSize.setClassical(true);
        assertEquals("123 byte", "" + fileSize.process(null, null, "123"));
        assertEquals("123 byte", "" + fileSize.process(null, null, 123));
        assertEquals("3.0 kbyte", "" + fileSize.process(null, null, "3061"));
        assertEquals("0 byte", "" + fileSize.process(null, null, 0));
        assertEquals(null, fileSize.process(null, null, null));
        assertEquals("-1 byte", "" + fileSize.process(null, null, -1));
        assertEquals("0 byte", "" + fileSize.process(null, null, 0.1));
    }

    @Test
    public void unitLessInteger() {
        FormatQuantity def = new FormatQuantity();
        def.setInteger(true);
        assertEquals("123", "" + def.process(null, null, "123"));
        assertEquals("123", "" + def.process(null, null, 123));
        assertEquals("3.0 k", "" + def.process(null, null, 3011));
        assertEquals("3.1 k", "" + def.process(null, null, 3061));
        assertEquals("4 k", "" + def.process(null, null, 3861));
        assertEquals(null, def.process(null, null, null));
        assertEquals("-1", "" + def.process(null, null, -1));
    }



}
