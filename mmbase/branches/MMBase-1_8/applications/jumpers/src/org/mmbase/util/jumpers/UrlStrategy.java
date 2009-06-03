/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.jumpers;

import org.mmbase.module.core.MMObjectNode;

/**
 * UrlStrategy.
 * This will return the url-field when the type of the object is 'urls'.
 *
 * @see org.mmbase.module.builders.Urls#getDefaultUrl(int)
 *
 * @author Marcel Maatkamp, VPRO Digitaal
 * @version $Id$
 */
public class UrlStrategy extends JumperStrategy {

    /**
     * {@inheritDoc}
     */
    public boolean contains(MMObjectNode node) {
        if(node == null) {
            throw new IllegalArgumentException("node is null!");
        }
        return node.getBuilder().getTableName().equals("urls");
    }

    /**
     * {@inheritDoc}
     */
    public String calculate(MMObjectNode node) {
        if(node == null) {
            throw new IllegalArgumentException("node is null!");
        }

        return node.getStringValue("url");
    }
}
