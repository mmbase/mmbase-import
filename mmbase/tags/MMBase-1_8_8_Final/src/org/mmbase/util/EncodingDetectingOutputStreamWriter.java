/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package org.mmbase.util;

import java.io.*;

/**
 * Like {@link java.io.OutputStreamWriter} but it tries to autodetect the encoding of the
 * OutputStream. This works at least if the OutputStream is XML, which is a very common thing to be for Resources.
 *
 * For this to work at least the first part (e.g. the first 100 bytes) need to be buffered.
 *
 * If determining the encoding did not succeed it is supposed to be 'UTF-8', which is (should be) an
 * acceptable encoding, and also the default encoding for XML streams.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.8
 * @version $Id$
 * @todo  Is it named correctly?
 */
public class EncodingDetectingOutputStreamWriter extends Writer {

    private OutputStream outputStream;

    // Either wrapped or buffer is null, and the other one is currenlty in use.
    private Writer wrapped = null;
    private StringBuffer buffer = new StringBuffer(100);

    EncodingDetectingOutputStreamWriter(OutputStream os) {
        outputStream = os;
    }

    /**
     * Stop buffering, determine encoding, and start behaving as a normal OutputStreamWriter (by
     * wrapping one). Unless, this happened already.
     */
    private void wrap() throws IOException {
        if (wrapped == null) {
            String encoding = GenericResponseWrapper.getXMLEncoding(buffer.toString());
            if (encoding == null) {
                encoding = "UTF-8";
            }
            try {
                wrapped = new OutputStreamWriter(outputStream, encoding);
            } catch (UnsupportedEncodingException uee) {
            }
            wrapped.write(buffer.toString());
            buffer = null;
        }
    }
    public void close() throws IOException {
        wrap();
        wrapped.close();
    }
    public void flush() throws IOException {
        wrap();
        wrapped.flush();
    }

    public void write(char[] cbuf) throws IOException {
        if (wrapped != null) {
            wrapped.write(cbuf);
        } else {
            write(cbuf, 0, cbuf.length);
        }
    }

    public void write(int c) throws IOException {
        if (wrapped != null) { wrapped.write(c); } else { super.write(c); }
    }

    public void write(String str) throws IOException {
        if (wrapped != null) { wrapped.write(str); } else { super.write(str); }
    }

    public void write(String str, int off, int len) throws IOException {
        if (wrapped != null) { wrapped.write(str, off, len); } else { super.write(str, off, len); }

    }
    public void write(char[] cbuf, int off, int len) throws IOException {
        if (wrapped != null) {
            wrapped.write(cbuf, off, len);
        } else {
            for (int i = off; i < len + off; i++) {
                buffer.append(cbuf[i]);
                if (buffer.length() == 100) {
                    wrap();
                    i++;
                    if (i < len) {
                        wrapped.write(cbuf, i, len - (i - off));
                    }
                    break;
                }
            }
        }
    }
}
