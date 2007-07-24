/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.jumpers;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.math.BigDecimal;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import org.mmbase.module.core.MMBase;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.module.builders.Jumpers;

import org.mmbase.bridge.*;

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
* @see #contains(MMObjectNode)
* @see #calculate(MMObjectNode)
*
* @author Marcel Maatkamp, VPRO Digitaal
* @version $Id: JumperStrategy.java,v 1.1 2007-07-24 12:53:18 michiel Exp $
*/
public abstract class JumperStrategy { 

    private static final Logger log = Logging.getLoggerInstance(JumperStrategy.class);

    protected Map<String, String> testset = new HashMap<String, String>();

    /**
    * signals whether this strategy can calculate an url for this node.
    *
    * It will query the database and return true if this strategy can procude an url
    * The factory will walk through its strategies like:
    *
    *   for all enables strategies AND url not found {
    *       if(strategy[x].contains(node))
    *           return strategy[x].calculate(node)
    *   }
    *
    * @param node node for which an url has to be calculated for
    * @return url for this node
    */
    public abstract boolean contains(MMObjectNode node);

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
