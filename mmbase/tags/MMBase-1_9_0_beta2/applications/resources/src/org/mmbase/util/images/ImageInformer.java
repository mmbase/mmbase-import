/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.images;

import java.util.Map;

import java.io.InputStream;

/**
 * The ImageInformer interface defines the function {@link #getDimension}, which returns a {@link
 * Dimension} object given a certain Image byte array.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.7.4
 */


public interface ImageInformer {

    void init(Map<String,String> params);

    Dimension getDimension(byte[] input) throws java.io.IOException;
    /**
     * @since MMBase-1.9
     */
    Dimension getDimension(InputStream input) throws java.io.IOException;

}
