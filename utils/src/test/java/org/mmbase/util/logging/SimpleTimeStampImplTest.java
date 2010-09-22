/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.logging;

import org.junit.*;
import static org.junit.Assert.*;
import java.util.regex.*;
import java.io.*;
/**

 */
public class SimpleTimeStampImplTest {

    private static ByteArrayOutputStream stdout = new ByteArrayOutputStream();
    private static PrintStream          ORIG = System.out;

    @BeforeClass
    public static void setup() {
        System.setOut(new PrintStream(stdout));
    }

    @AfterClass
    public static void shutdown() {
        System.setOut(ORIG);
        System.out.println("shut down");
    }


    @Test
    public void configure() throws IOException  {

        SimpleTimeStampImpl.configure("\n:stdout,warn\nA:stdout,debug B:stdout,service");
        Logger A = SimpleTimeStampImpl.getLoggerInstance("A");
        Logger B = SimpleTimeStampImpl.getLoggerInstance("B");
        Logger C = SimpleTimeStampImpl.getLoggerInstance("C");
        A.debug("a1");
        B.debug("b1");
        B.service("b2");
        C.service("c1");
        C.warn("c2");
        SimpleTimeStampImpl.configure("A:stdout,debug B:stdout,debug");
        B.debug("b3");
        SimpleTimeStampImpl.configure("A:service B:stdout,debug");
        A.debug("a2");
        A.service("a3");
        String result = new String(stdout.toByteArray());
        Pattern p = Pattern.compile("DEBUG \\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2},\\d{3} a1\\n" +
                                    "SERVICE \\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2},\\d{3} b2\\n" +
                                    "WARN \\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2},\\d{3} c2\\n" +
                                    "DEBUG \\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2},\\d{3} b3\\n" +
                                    "SERVICE \\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2},\\d{3} a3\\n"
                                    );
        assertTrue(p + "\n" + result, p.matcher(result).matches());

    }

}
