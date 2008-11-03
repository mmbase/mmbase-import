/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.jumpers;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import org.mmbase.module.core.MMObjectNode;

/**
 * This is the baseclass for strategies.
 *
 * A strategy has to extend this class and provide its own methods for:
 *   - contains(MMObjectNode)
 *   - calculate(MMObjectNode)
 *
 * The contains(node) checks whether the strategy can/will handle this node.
 * For example, it can check whether the node has a certain type of relation,
 * is of a special type or is just of a type this strategy can handle.
 *
 * The calculate(node) will then try to calculate an url.
 *
 * An implementation for a JumperStrategy must be <em>stateless</em> because it is instantiated only once,
 * and may be accessed by more than one thread. So you <em>may not</em> use result of the
 * calculation of {@link #contains} in {@link #calculate}. For calculating actual jumper only {@link
 * #calculate} is called, which can rapidly return <code>null</code> if it cannot calculate for the
 * given Node.
 *
 *
 * @see #contains(MMObjectNode)
 * @see #calculate(MMObjectNode)
 *
 * @author Marcel Maatkamp, VPRO Digitaal
 * @version $Id: JumperStrategy.java,v 1.5 2008-02-03 17:33:57 nklasens Exp $
 */
public abstract class JumperStrategy {

    private static final Logger log = Logging.getLoggerInstance(JumperStrategy.class);


    /**
     * signals whether this strategy can calculate an url for this node.
     *
     * @param node node for which an url has to be calculated for
     * @return url for this node
    */
    public boolean contains(MMObjectNode node) {
        return calculate(node) != null;
    }

    /**
     * calculates an url for this node.
     *
     * call this method only when the contains(node) returns true.
     *
     * @see #contains(MMObjectNode)
     * @param node node for which an url has to be calculated for
     * @return the url for this node
     */
    public abstract String  calculate(MMObjectNode node);

}
