/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.images;
import java.io.*;


/**
 * The 'image conversion receiver' storing result directly to an output stream.
 *
 * @author Michiel Meeuwissen
 * @version $Id: MemoryReceiver.java 35335 2009-05-21 08:14:41Z michiel $
 * @since MMBase-1.9.2
 */
public class OutputStreamReceiver implements ImageConversionReceiver {

    private final OutputStream stream;
    private long size = -1;
    /**
     */
    public OutputStreamReceiver(OutputStream s) {
        stream = s;
    }

    public OutputStream getOutputStream() {
        return stream;
    }
    public InputStream getInputStream() throws IOException {
        throw new UnsupportedOperationException();
    }
    public void setSize(long s) {
        size = s;
    }
    public long getSize() {
        return size;
    }

    public boolean wantsDimension() {
        return false;
    }
    public void setDimension(Dimension d) {
    }

    public void ready() throws IOException {

    }

}
