/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.util.List;
import java.util.Map;

/**
 * A 'Dummy' converter for converting images when Imagemagick and JAI are not available.
 * This class simply returns an image unchanged.
 *
 * @since MMBase 1.6.3
 * @author Gerard van de Looi
 * @version $Id: ConvertDummy.java,v 1.2 2003-04-01 14:03:28 pierre Exp $
 */
public class ConvertDummy implements ImageConvertInterface {

    /**
     * Constructor for ConvertDummy.
     */
    public ConvertDummy() {
        super();
    }

    /**
     * @see ImageConvertInterface#init(Map)
     */
    public void init(Map params) {
    }

    /**
     * Call for converting a specified image (byte array) using a list of (string) commands
     * This dummy method ignores any passed commands, and simply returns the inputed list.
     * @see ImageConvertInterface#ConvertImage(byte[], List)
     * @deprecated Use convertImage.
     */
    public byte[] ConvertImage(byte[] input, List commands) {
        return convertImage(input, commands);
    }

    /**
     * Call for converting a specified image (byte array) using a list of (string) commands
     * This dummy method ignores any passed commands, and simply returns the inputed list.
     * @see ImageConvertInterface#convertImage(byte[], List)
     */
    public byte[] convertImage(byte[] input, List commands) {
        return input;
    }

}
