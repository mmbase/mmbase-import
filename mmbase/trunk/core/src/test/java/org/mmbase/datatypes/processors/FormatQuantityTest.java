/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.processors;

import org.mmbase.bridge.*;
import org.junit.*;
import static org.junit.Assert.*;


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
    }



}
