/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util;

import java.io.*;

/**
 * Oddly enough, Java does not provide this itself. Code is nearly identical to java.io.StringWriter.
 *

 * @author	Michiel Meeuwissen
 * @since	MMBase-1.7
 * @see         java.io.StringWriter
 */

public class StringBufferWriter extends Writer {

    protected StringBuffer buf;

    /**
     * Create a new stringbufferwriter
     */
    public StringBufferWriter(StringBuffer buffer) {
	buf = buffer;
	lock = buf;
    }


    /**
     * Write a single character.
     */
    public void write(int c) {
	buf.append((char) c);
    }

    /**
     * Write a portion of an array of characters.
     *
     * @param  cbuf  Array of characters
     * @param  off   Offset from which to start writing characters
     * @param  len   Number of characters to write
     */
    public void write(char cbuf[], int off, int len) {
        if ((off < 0) || (off > cbuf.length) || (len < 0) ||
            ((off + len) > cbuf.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return;
        }
        buf.append(cbuf, off, len);
    }

    /**
     * Write a string.
     */
    public void write(String str) {
	buf.append(str);
    }

    /**
     * Write a portion of a string.
     *
     * @param  str  String to be written
     * @param  off  Offset from which to start writing characters
     * @param  len  Number of characters to write
     */
    public void write(String str, int off, int len)  {
	buf.append(str.substring(off, off + len));
    }

    /**
     * Return the buffer's current value as a string.
     */
    public String toString() {
	return buf.toString();
    }

    /**
     * Return the string buffer itself.
     *
     * @return StringBuffer holding the current buffer value.
     */
    public StringBuffer getBuffer() {
	return buf;
    }

    /**
     * Flush the stream.
     */
    public void flush() { 
    }

    /**
     * Closing a <tt>StringWriter</tt> has no effect. The methods in this
     * class can be called after the stream has been closed without generating
     * an <tt>IOException</tt>.
     */
    public void close() throws IOException {
    }

}
