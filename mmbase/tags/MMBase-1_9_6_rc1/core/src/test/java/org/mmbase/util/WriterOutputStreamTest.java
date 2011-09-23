/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import org.junit.*;
import static org.junit.Assert.*;
import java.io.*;
/**

 */
public class WriterOutputStreamTest {


    @Test
    public void one() throws IOException  {
        StringWriter w = new StringWriter();

        OutputStream os = new WriterOutputStream(w, System.getProperty("file.encoding"));

        os.write(65);
        os.close();
        assertEquals("A", w.toString());
    }

}
