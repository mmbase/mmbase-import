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

import java.util.*;

public interface ImageConvertInterface {
    
    public void init(Hashtable params);
    /**
     * @deprecated Use convertImage.
     */
    public byte[] ConvertImage(byte[] input,Vector commands);
    public byte[] convertImage(byte[] input,Vector commands);
}
