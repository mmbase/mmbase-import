/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

/**
 * Interface for classes that can convert images.
 *
 * @author Rico Jansen
 */

import java.util.List;
import java.util.Map;

public interface ImageConvertInterface {

    public void init(Map params);
    /**
     * @deprecated Use convertImage.
     */
    public byte[] ConvertImage(byte[] input,List commands);
    public byte[] convertImage(byte[] input,List commands);
}
