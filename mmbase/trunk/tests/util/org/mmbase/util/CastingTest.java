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
 * @verion $Id: CastingTest.java,v 1.2 2008-09-12 13:10:43 michiel Exp $
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
        assertEquals("a,b,c", Casting.toString(list));
    }

    public void testInt() {
        assertEquals(1, Casting.toInt("1"));
        assertEquals(-1, Casting.toInt("asdfasdf"));
        assertEquals(-5, Casting.toInt("asdfasdf", -5));
        assertEquals(-5, Casting.toInt(null, -5));

        assertEquals(5, Casting.toInt("5.3"));
        assertEquals(5, Casting.toInt("5.6"));
        assertEquals(100, Casting.toInt("1e2"));
        assertEquals(-1, Casting.toInt(null));
        assertEquals(8, Casting.toInt(null, 8));
        assertEquals(8, Casting.toInt("bla bloe", 8));
        assertEquals(15, Casting.toInt("15", 8));

        assertEquals("15", Casting.toString(15));
    }
    public void testInteger() {
        assertEquals(new Integer(10), (Object) Casting.toInteger("10"));
        assertEquals(new Integer(10), (Object) Casting.toInteger("1e1"));
        assertEquals(new Integer(-1), (Object) Casting.toInteger(null));
    }

    public void testLong() {
        assertEquals(new Long(10), (Object) Casting.toLong("10"));
        assertEquals(new Long(10), (Object) Casting.toLong("1e1"));
        assertEquals(new Long(-1), (Object) Casting.toLong(null));
    }

    public void testFloat() {
        assertEquals(new Float(-1.0), (Object) Casting.toFloat(null));
    }

    public void testDouble() {
        assertEquals(new Double(-1.0), (Object) Casting.toDouble(null));
    }

    public void testBinary() {

    }


}
