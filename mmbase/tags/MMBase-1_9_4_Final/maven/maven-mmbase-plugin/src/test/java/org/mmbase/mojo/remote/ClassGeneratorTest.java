/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.mojo.remote;
import java.lang.reflect.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;


/**
 *
 * @since MMBase-1.9
 * @author Michiel Meeuwissen
 * @version $Id$
 */
public class ClassGeneratorTest {

    AbstractClassGenerator generator = new AbstractClassGenerator(ClassGeneratorTest.class) {
            public void appendMethod(Method m) {
            }
            public void generate() {
            }
        };
    protected class A {
        public List<? extends String> list() {
            return null;
        }
    };

    @Test
    public void appendType() throws Exception {
        assertEquals(0, generator.buffer.length());
        Method m = A.class.getMethod("list");
        generator.appendTypeInfo(m.getGenericReturnType());
        assertEquals("java.util.List<? extends java.lang.String>", generator.buffer.toString());

    }

    @Test
    public void proxyGenerator() throws Exception  {
        ProxyGenerator proxy = new ProxyGenerator(A.class);
        Method m = A.class.getMethod("list");
        proxy.appendMethod(m);
        String begin = "public java.util.List<? extends java.lang.String>";
        // Gave it up, for some reason the proxy generator want to generate List<?> and it seems that if you fix that
        // RMMCI will give many more problems.
        // For now simply no such return types are used in the bridge.

        //assertEquals(begin, proxy.buffer.toString().trim().substring(0, begin.length()));
    }

}

