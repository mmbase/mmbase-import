/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.servlet;

import java.util.*;
import java.io.*;
import org.apache.commons.io.FileUtils;

import org.mmbase.util.*;

import org.junit.*;
import static org.junit.Assert.*;
import static org.mmbase.servlet.FileServlet.*;

/**
 *
 * @author Michiel Meeuwissen
 */

public class FileServletTest {


    @Test
    public void testRange() {
        assertEquals(10, new ChainedRange("0-9", 10000).available(0));
        assertEquals(500, new ChainedRange("500-600,601-999", 10000).available(500));
        assertEquals(500, new ChainedRange("500-600,601-999", 10000).notavailable(0));
        assertEquals(500, new ChainedRange("500-700,601-999", 10000).available(500));

        assertEquals(500, new ChainedRange("0-100,101-499", 10000).available(0));
        assertEquals(0,   new ChainedRange("0-100,101-999", 10000).notavailable(0));
        assertEquals(500, new ChainedRange("0-200,101-499", 10000).available(0));
        assertEquals(0, new ChainedRange("-500", 10000).available(0));
        assertEquals(9500, new ChainedRange("-500", 10000).notavailable(0));
        assertEquals(500, new ChainedRange("-500", 10000).available(9500));
    }
    @Test
    public void testRangeLength() {
        assertEquals(500, new ChainedRange("0-200,101-499", 10000).getLength());
        assertEquals(400, new ChainedRange("0-200,101-499", 400).getLength());
    }


    public void stream(ChainedRange range) throws Exception {
        File file = new File("src" + File.separator + "test" + File.separator + "images" + File.separator + "test.png");
        InputStream in = new FileInputStream(file);
        File outFile = File.createTempFile(FileServletTest.class.getName(), null);
        //System.out.println("" + outFile);
        OutputStream out = new FileOutputStream(outFile);
        FileServlet.stream(range, in, out);
        assertTrue(FileUtils.contentEquals(file, outFile));
    }

    @Test
    public void stream() throws Exception {
        stream(null);
    }


    @Test
    public void streamRange() throws Exception {
        File file = new File("src" + File.separator + "test" + File.separator + "images" + File.separator + "test.png");
        {
            ChainedRange range = new ChainedRange("0-100,101-499,500-" + file.length(), file.length());
            stream(range);
        }
        {
            ChainedRange range = new ChainedRange("0-100,101-499,500-", file.length());
            stream(range);
        }
    }

}
