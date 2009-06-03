/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util;

import java.io.*;

/**
 * Oddly enough, Java does not provide this itself. It is necessary
 * though in GenericResponseWrapper, because you sometimes are stuck
 * with a writer, while an outputStream is requested.
 * Code inspired by  http://www.koders.com/java/fid5A2897DDE860FCC1D9D9E0EA5A2834CC62A87E85.aspx
 * @author	Michiel Meeuwissen
 * @since	MMBase-1.7.4
 */
public class WriterOutputStream extends OutputStream {
    protected Writer writer;
    protected String encoding;
    private byte[] buf = new byte[1];

    public WriterOutputStream(Writer writer, String encoding) {
        this.writer = writer;
        this.encoding = encoding;
    }
    public void close() throws IOException {
        writer.close();
        writer = null;
        encoding = null;
    }


    public void flush() throws IOException {
        writer.flush();
    }

    public void write(byte[] b) throws IOException {
        if (encoding == null) {
            writer.write(new String(b));
        } else {
            writer.write(new String(b, encoding));
        }

    }

    public void write(byte[] b, int off, int len) throws IOException {
        if (encoding == null) {
            writer.write(new String(b, off, len));
        } else {
            writer.write(new String(b, off, len, encoding));
        }
    }
    public synchronized void write(int b) throws IOException {
        buf[0] = (byte)b;
        write(buf);
    }
}


