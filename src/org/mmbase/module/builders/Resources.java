/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import org.mmbase.module.core.*;
import org.mmbase.util.ResourceLoader;

/**
 * The resources builder can be used by {@link org.mmbase.util.ResourceLoader} to load resources from
 * (configuration files, classes, resourcebundles).
 *
 * @author Michiel Meeuwissen
 * @version $Id: Resources.java,v 1.3 2005-10-17 17:32:18 michiel Exp $
 * @since   MMBase-1.8
 */
public class Resources extends Attachments {

    /**
     * Registers this builder in the ResourceLoader.
     * {@inheritDoc}
     */
    public boolean init() {
        boolean res = super.init();
        if (res) {
            //ResourceLoader.setResourceBuilder(this);
        } 
        return res;

    }

    /**
     * Implements virtual filename field.
     * {@inheritDoc}
     */
    public Object getValue(MMObjectNode node, String field) {
        if (field.equals(ResourceLoader.FILENAME_FIELD)) {
            String s = node.getStringValue(ResourceLoader.RESOURCENAME_FIELD);
            int i = s.lastIndexOf("/");
            if (i > 0) {
                return s.substring(i + 1);
            } else {
                return s;
            }
        } else {
            return super.getValue(node, field);
        }
    }

}
