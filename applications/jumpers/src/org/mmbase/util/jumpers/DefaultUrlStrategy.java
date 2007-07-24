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
 * @author Michiel Meeuwissen
 * @version $Id: DefaultUrlStrategy.java,v 1.1 2007-07-24 12:53:18 michiel Exp $
 */
public class DefaultUrlStrategy extends JumperStrategy { 

    /** 
     * {@inheritDoc} 
     */
    public boolean contains(MMObjectNode node) {
        if(node == null) {
            throw new IllegalArgumentException("node is null!");
        }
        return true;
    }

    /** 
     * {@inheritDoc} 
     */
    public String calculate(MMObjectNode node) { 
        if(node == null) {
            throw new IllegalArgumentException("node is null!");
        }

        return node.getBuilder().getDefaultUrl(node.getNumber());
    }
}
