/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.jumpers;

import java.util.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import org.mmbase.module.core.MMObjectNode;

import org.mmbase.bridge.*;

/**

* @author Michiel Meeuwissen
* @version $Id: ChainedJumperStrategy.java,v 1.2 2007-07-24 15:09:21 michiel Exp $
*/
public class ChainedJumperStrategy extends JumperStrategy { 

    private static final Logger log = Logging.getLoggerInstance(ChainedJumperStrategy.class);

    private final List<JumperStrategy> chain = new ArrayList<JumperStrategy>();

    public void add(JumperStrategy j) {
        chain.add(j);
    }
    public void clear() {
        chain.clear();
    }

    public boolean contains(MMObjectNode node) {
        for (JumperStrategy s : chain) {
            if (s.contains(node)) return true;
        }
        return false;
    }
    public String  calculate(MMObjectNode node) {
        for (JumperStrategy s : chain) {
            String r = s.contains(node) ? s.calculate(node) : null;
            if (r != null) return r;
        }
        return null;
        
    }

    public String toString() {
        return chain.toString();
    }

}
