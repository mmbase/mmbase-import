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
 * Sometimes you need an InputStream to be Serializable. This wraps
 * another InputStream.
 * @since MMBase-1.9
 * @author Michiel Meeuwissen
 * @version $Id: SerializableInputStream.java,v 1.2 2009-04-16 13:46:11 michiel Exp $
 * @todo IllegalStateException or so, if the inputstreas is used (already).
 */

public class SerializableInputStream  extends InputStream implements Serializable {

    private static final long serialVersionUID = 1;

    private static final Logger log = Logging.getLoggerInstance(SerializableInputStream.class);

    private final long size;

    private boolean used = false;

    public static byte[] toByteArray(InputStream stream) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            byte[] buf = new byte[1024];
            int n;
            while ((n = stream.read(buf)) > -1) {
                bos.write(buf, 0, n);
            }
        } catch (IOException ioe) {
            log.error(ioe);
        }
        return bos.toByteArray();
    }

    private void use() {
        if (! used) {
            if (log.isTraceEnabled()) {
                log.trace("Using " + this + " because ", new Exception());
            }
            used = true;
        }
    }


    private InputStream wrapped;

    public SerializableInputStream(InputStream wrapped, long s) {
        this.wrapped = wrapped;
        this.size = s;
    }

    public SerializableInputStream(byte[] array) {
        this.wrapped = new ByteArrayInputStream(array);
        this.size = array.length;
    }

    public long getSize() {
        return size;
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        wrapped.reset();
        out.writeObject(toByteArray(wrapped));
    }
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        byte[] b = (byte[]) in.readObject();
        wrapped = new ByteArrayInputStream(b);
    }
    public int available() throws IOException { return wrapped.available(); }
    public void mark(int readlimit) {  wrapped.mark(readlimit); }
    public boolean markSupported() { return wrapped.markSupported(); }
    public int read() throws IOException { use(); return wrapped.read(); }
    public int read(byte[] b) throws IOException { use(); return wrapped.read(b); }
    public int read(byte[] b, int off, int len) throws IOException { use(); return wrapped.read(b, off, len); }
    public void reset() throws IOException { wrapped.reset() ; }
    public long skip(long n) throws IOException { return wrapped.skip(n); }

    public String toString() {
        return "SERIALIZABLE " + wrapped + (used ? " (used)" :  "");
    }
}
