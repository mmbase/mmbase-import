/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import org.mmbase.module.core.*;

/**
 * @javadoc
 * @author Daniel Ockeloen
 * @version $Id$
 */
public class ImageAliases extends MMObjectBuilder {

    /**
     * @javadoc
     */
    public String getDefaultUrl(int src) {
        MMObjectNode node=getNode(src);
        return node.getStringValue("url");
    }
}
