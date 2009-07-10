/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import org.mmbase.util.images.*;
import org.mmbase.module.core.*;

/**
 * Builders with want to use the ImageCaches 'icaches' builders, need to implement this interface
 *
 * @author Michiel Meeuwissen
 * @version $Id: AbstractImages.java 35615 2009-06-03 13:20:55Z michiel $
 * @since   MMBase-1.9.2
 */
public interface ImagesInterface  {

    /**
     * Gets the dimension of given node. Also when the fields are missing, it will result a warning then.
     */
    Dimension getDimension(MMObjectNode node);


    String getImageFormat(MMObjectNode node);

    java.io.InputStream getBinary(MMObjectNode node);


}
