/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.images;

/**
 * Interface for classes that can convert images.
 *
 * @author Rico Jansen
 * @author Michiel Meeuwissen
 */

import java.util.List;
import java.util.Map;
import java.io.*;

public interface ImageConverter {

    void init(Map<String, String> params);

    byte[] convertImage(byte[] input, String sourceFormat, List<String> commands);
    /**
     * @since MMBase-1.9
     */
    int convertImage(InputStream input, String sourceFormat, OutputStream out, List<String> commands) throws IOException;


}
