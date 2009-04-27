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
 * @verion $Id: LocalizedStringTest.java,v 1.1 2009-04-27 14:45:09 michiel Exp $
 */
public class LocalizedStringTest extends TestCase {

    public static final Locale NL = new Locale("nl");
    public static final Locale BE = new Locale("nl", "BE");
    public static final Locale BE_VAR = new Locale("nl", "BE", "a_b");
    public static final Locale EN = new Locale("en", "GB");
    public static final Locale DK = new Locale("dk");
    public static final Locale EO = new Locale("eo");


    protected LocalizedString getInstance() {
        LocalizedString.setDefault(DK);
        LocalizedString fun = new LocalizedString("funny");
        fun.set("leuk", NL);
        fun.set("plezant", BE);
        fun.set("amuza", EO);
        return fun;
    }

    public void testBasic() {
        LocalizedString fun = getInstance();
        assertEquals(fun.get(null), "funny");
        assertEquals(fun.get(EO), "amuza");
        assertEquals(fun.get(BE_VAR), "plezant");
        assertEquals(fun.get(NL), "leuk");

    }

    public void testClone() {
        LocalizedString fun = getInstance().clone();
        assertEquals(fun.get(null), "funny");
        assertEquals(fun.get(EO), "amuza");
        assertEquals(fun.get(BE_VAR), "plezant");
        assertEquals(fun.get(NL), "leuk");

    }




}
