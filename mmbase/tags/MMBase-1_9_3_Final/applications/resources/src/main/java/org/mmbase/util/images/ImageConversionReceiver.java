/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.images;
import java.io.*;

/**
 * An image conversion receiver receives the actual result of a image conversion. This can for
 * example be an MMBase 'icaches' node. But you can e.g, also transform to memory if there is no
 * need to store the result.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.9
 */
public interface ImageConversionReceiver {
    OutputStream getOutputStream() throws IOException;
    InputStream getInputStream() throws IOException ;
    void setSize(long s);
    long getSize();
    boolean wantsDimension();
    void  setDimension(Dimension dim);
    void ready() throws IOException;
}