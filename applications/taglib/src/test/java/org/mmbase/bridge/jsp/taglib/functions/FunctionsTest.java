/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.jsp.taglib.functions;

import javax.servlet.jsp.*;
import org.springframework.mock.web.*;
import java.util.*;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

/**
 * @version $Id: StringListTagTest.java 39892 2009-11-24 09:13:41Z michiel $
 */

public  class FunctionsTest {

    @BeforeClass
    public static void setup() {
        org.mmbase.util.LocalizedString.setDefault(new Locale("en", "US"));
    }

    @Test
    public void reformatDate() throws Exception {
        assertEquals("2010-11-01", Functions.reformatDate("01/11/2010", "dd/MM/yyyy", "yyyy-MM-dd"));
        assertEquals("2010-10-21", Functions.reformatDate("Thu Oct 21 09:58:10 +0000 2010", "EE MMM dd HH:mm:ss Z yyyy", "yyyy-MM-dd"));
    }

}
