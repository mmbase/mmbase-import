/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util;

import java.io.*;
import java.util.*;

/**
 * Like unix' 'tee'. Delegates all write operation to several others.
 *
 *
 * @author	Michiel Meeuwissen
 * @since	MMBase-2.0
 * @version $Id: ChainedWriter.java 44739 2011-01-12 08:52:31Z michiel $
 */
public class ChainedOutputStream extends OutputStream {

    private final List<OutputStream> streams = new ArrayList<OutputStream>();
    public ChainedOutputStream(OutputStream... ls) {
        for (OutputStream o : ls) {
            addOutputStream(o);
        }
    }

    public final ChainedOutputStream addOutputStream(OutputStream o) {
        streams.add(o);
        return this;
    }
    @Override
    public final void close() throws IOException {
        for (OutputStream o : streams ) {
            o.close();
        }
    }
    @Override
    public final void flush() throws IOException {
        for (OutputStream o : streams) {
            o.flush();
        }
    }
    @Override
    public void write(byte[] b) throws IOException {
        for (OutputStream o : streams ) {
            o.write(b);
        }
    }
    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        for (OutputStream o : streams ) {
            o.write(b, off, len);
        }
    }
    @Override
    public void write(int b) throws IOException {
        for (OutputStream o : streams ) {
            o.write(b);
        }
    }
    @Override
    public String toString() {
        return streams.toString();
    }
}
