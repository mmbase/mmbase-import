/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import org.mmbase.module.corebuilders.InsRel;
import org.mmbase.module.core.MMObjectNode;

/**
 * Like InsRel, only with a GUIIndicator tailored for a relation with a 'pos' field.
 *
 * @author Rico Jansen
 * @author Michiel Meeuwissen.
 * @version $Id: PosRel.java,v 1.1 2008-06-12 10:04:38 michiel Exp $
 */
public class PosRel extends InsRel {

    public PosRel() {
    }

    public String getGUIIndicator(MMObjectNode node) {
        return super.getGUIIndicator(node) + ":" + node.getStringValue("pos");
    }


}
