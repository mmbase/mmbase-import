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
 */

import java.util.List;
import java.util.Map;

public interface ImageConverter {

    void init(Map params);

    byte[] convertImage(byte[] input, String sourceFormat, List commands);


}
