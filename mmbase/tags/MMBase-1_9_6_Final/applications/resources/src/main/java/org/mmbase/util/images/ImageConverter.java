/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.images;

import java.util.List;
import java.util.Map;
import java.io.*;

/**
 * Interface for classes that can convert images.
 *
 * @author Rico Jansen
 * @author Michiel Meeuwissen
 */
public interface ImageConverter {

    void init(Map<String, String> params);

    /**
     * This functions converts an image by the given parameters
     * @param input an array of <code>byte</code> which represents the original image
     * @param sourceFormat original image format
     * @param commands a <code>List</code> of <code>String</code>s containing commands which are operations on the image which will be returned.
     * @return an array of <code>byte</code>s containing the new converted image.
     */
    byte[] convertImage(byte[] input, String sourceFormat, List<String> commands);

    /**
     * This functions converts an image by the given parameters
     * @param input stream of <code>byte</code> which represents the original image
     * @param sourceFormat original image format
     * @param out stream of <code>byte</code>s containing the new converted image.
     * @param commands a <code>List</code> of <code>String</code>s containing commands which are operations on the image which will be returned.
     * @return number of bytes of converted image.
     * @throws IOException When an error occurs when converting the image
     *
     * @since MMBase-1.9
     */
    long convertImage(InputStream input, String sourceFormat, OutputStream out, List<String> commands) throws IOException;


}
