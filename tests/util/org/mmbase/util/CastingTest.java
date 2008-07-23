/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;
import org.mmbase.util.Casting;
import org.mmbase.util.transformers.*;
import java.util.*;
import junit.framework.TestCase;

/**
 *
 * @author Michiel Meeuwissen
 * @verion $Id: CastingTest.java,v 1.1 2008-07-23 17:15:48 michiel Exp $
 */
public class CastingTest extends TestCase {


    public void testNull() {
        assertEquals("", Casting.toString(null));
        assertEquals("", Casting.wrap(null, new Xml()).toString());
    }
    public void testList() {
        List<String> list = new ArrayList<String>();
        list.add("a");
        list.add("b");
        list.add("c");
        assertEquals(list, Casting.toList("a,b,c"));
        assertEquals(list, Casting.toList("a , b , c"));
    }

    public void testInt() {
        assertEquals(1, Casting.toInt("1"));
        assertEquals(-1, Casting.toInt("asdfasdf"));
        assertEquals(5, Casting.toInt("5.3"));
        assertEquals(5, Casting.toInt("5.6"));
        assertEquals(100, Casting.toInt("1e2"));
        assertEquals(-1, Casting.toInt(null));
        assertEquals(8, Casting.toInt(null, 8));
        assertEquals(8, Casting.toInt("bla bloe", 8));
        assertEquals(15, Casting.toInt("15", 8));
    }
    public void testInteger() {
        assertEquals(-1, (Object) Casting.toInteger(null));
    }


}
