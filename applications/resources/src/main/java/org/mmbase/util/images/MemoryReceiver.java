/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.images;
import java.io.*;


/**
 * The 'image conversion receiver' storing the result in a File
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9
 */
public class MemoryReceiver implements ImageConversionReceiver {

    private Dimension dim = Dimension.UNDETERMINED;
    private ByteArrayOutputStream stream;
    private InputStream in;
    private long size = -1;

    /**
     */
    public MemoryReceiver() {
    }

    public Dimension getDimension() {
        return dim;
    }

    @Override
    public OutputStream getOutputStream() {
        if (stream == null) {
            stream = new ByteArrayOutputStream();
        }
        return stream;
    }
    @Override
    public InputStream getInputStream() throws IOException {
        if (stream != null) {
            stream.flush();
            stream.close();
        }
        if (in == null) {
            in = new BytesInputStream(stream.toByteArray());
        }
        return in;
    }
    @Override
    public void setSize(long s) {
        size = s;
    }
    @Override
    public long getSize() {
        if (size < 0 && stream != null) {
            try {
                stream.flush();
            } catch (IOException ioe) {}
            size = stream.toByteArray().length;
        }
        return size;
    }

    @Override
    public boolean wantsDimension() {
        return true;
    }
    @Override
    public void setDimension(Dimension d) {
        if (d != null) {
            dim = d;
        }
    }

    @Override
    public void ready() throws IOException {
        if (stream != null) {
            stream.flush();
        }
        if (in != null) {
            in.close();
        }
        if (stream != null) {
            stream.close();
        }
    }

}