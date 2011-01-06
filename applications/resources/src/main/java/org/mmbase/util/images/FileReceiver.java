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
public class FileReceiver implements ImageConversionReceiver {

    private final File file;
    private Dimension dim = Dimension.UNDETERMINED;
    private OutputStream stream;
    private InputStream in;

    /**
     */
    public FileReceiver(File f) {
        file = f;
    }

    public Dimension getDimension() {
        return dim;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        if (stream == null) {
            stream = new FileOutputStream(file);
        }
        return stream;
    }
    @Override
    public InputStream getInputStream() throws IOException {
        if (stream != null) {
            stream.close();
        }
        if (in == null) {
            in = new FileInputStream(file);
        }
        return in;
    }
    @Override
    public void setSize(long s) {
    }
    @Override
    public long getSize() {
        return file.length();
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
            stream.close();
        }
        if (in != null) {
            in.close();
        }
    }

    @Override
    public int hashCode() {
        return  file.hashCode();
    }
    @Override
    public boolean equals(Object o) {
        if (o instanceof FileReceiver) {
            FileReceiver r = (FileReceiver) o;
            return file.equals(r.file);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return file.toString();
    }
}