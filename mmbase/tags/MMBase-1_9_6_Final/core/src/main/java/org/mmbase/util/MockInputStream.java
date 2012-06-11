/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util;

import java.io.*;
import org.mmbase.util.logging.*;

/**
 *
 * @since MMBase-1.9.2
 * @author Michiel Meeuwissen
 * @version $Id$
 */
public abstract class MockInputStream extends InputStream {

    protected boolean closed = false;
    private final int length;
    private int cursor = 0;

    public MockInputStream(int l) {
        length = l;
    }

    protected abstract int oneByte();

    protected abstract void fillArray(byte[] data, int offset, int l);


    @Override
    public int read() throws IOException {
        checkOpen();
        if (cursor >= length) return -1;
        cursor++;
        return oneByte();
    }


    @Override
    public int read(byte[] data, int offset, int l) throws IOException {
        checkOpen();
        if (cursor >= length) return -1;
        cursor += l;
        if (cursor > length) {
            l -= (cursor - length);
            cursor = length;
        }
        fillArray(data, offset, l);
        return l;
    }




    @Override
    public long skip(long bytesToSkip) throws IOException {
        checkOpen();
        cursor +=  bytesToSkip;
        if (cursor > length) {
            bytesToSkip -= (cursor - length);
            cursor = length;
        }
        return bytesToSkip;
    }

    @Override
    public void close() {
        this.closed = true;
    }

    private void checkOpen() throws IOException {
        if (closed) {
            throw new IOException(getClass().getName() + ": Input stream closed");
        }
    }

    @Override
    public int available() {
        return length - cursor;
    }
}
