/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security.implementation.cloudcontext.builders;

import org.mmbase.security.implementation.cloudcontext.*;
import java.util.*;
import org.mmbase.module.core.*;
import org.mmbase.security.Rank;
import org.mmbase.security.SecurityException;
import org.mmbase.util.Encode;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This MMObjectBuilder implementation belongs to the object type
 * 'mmbaseusers' It contains functionality to MD5 encode passwords,
 * and so on.
 * 
 * @author Michiel Meeuwissen
 * @version $Id: Ranks.java,v 1.2 2003-05-23 12:05:13 michiel Exp $
 * @since MMBase-1.7
 */
public class Ranks extends MMObjectBuilder {

    private static final Logger log = Logging.getLoggerInstance(Ranks.class.getName());

    public Ranks() {
        super();        
    }

    /**
     * Returns the Ranks builder.
     */
    public static Ranks getBuilder() {
        return (Ranks) MMBase.getMMBase().getBuilder("mmbaseranks");
    }

    // javadoc inherited
    public boolean init() {
        mmb.addLocalObserver(getTableName(),  CacheInvalidator.getInstance());
        mmb.addRemoteObserver(getTableName(), CacheInvalidator.getInstance());
        return super.init();
    }

    /**
     * Converts this MMObjectNode to a real rank.
     */
    public Rank getRank(MMObjectNode node) {
        int rank = node.getIntValue("rank");
        if (rank == -1) {
            throw new SecurityException("odd rank " + rank);
        } else {
            String name = node.getStringValue("name");
            Rank r = Rank.getRank(name);
            if (r == null) { // unknown rank?
                r = Rank.registerRank(rank, name);
            }
            return r;
        }
    }

    //javadoc inherited
    public boolean setValue(MMObjectNode node, String field, Object originalValue) {
        return true;
    }

    //javadoc inherited
    public void setDefaults(MMObjectNode node) {
        
    }

}
