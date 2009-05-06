/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.jumpers;

import org.mmbase.module.core.MMObjectNode;

/**
 * This strategory completely depends on {@link org.mmbase.module.core.MMObjectBuilder#getDefaultUrl(int)}.
 *
 * @author Michiel Meeuwissen
 * @version $Id: DefaultUrlStrategy.java,v 1.2 2007-07-24 13:25:27 michiel Exp $
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
