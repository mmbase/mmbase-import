/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.util.*;

import org.mmbase.cache.Cache;
import org.mmbase.module.core.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;

/**
 * People builder, adds a replace() methods which retrieves a people objectnumber based on the current user's (SCAN) session.
 *
 * @application Basic [builder]
 * @author Daniel Ockeloen
 * @version $Id$
 */
public class People extends MMObjectBuilder {

    private static Logger log = Logging.getLoggerInstance(People.class.getName());

    /**
     * Cache for the most active people
     */
    private Cache<String, Integer> peopleCache = new Cache<String, Integer>(100) {
        public String getName()        { return "PeopleCache"; }
        public String getDescription() { return "Cache for most active people"; }
        };

    /**
     * Obtains a string value by performing the provided command.
     * The command can be called:
     * <ul>
     *   <li>by SCAN : $MOD-MMBASE-BUILDER-people-[command]</li>
     *   <li>in jsp : cloud.getNodeManager("people").getInfo(command);</li>
     * </lu>
     * The command recognized by the people builder :
     * <ul>
     *   <li>number : Get the object number for the current user's people object,
     *                based on the current user/cookie as defined by the key.
     *                Only works in SCAN, as it uses sessiondata from the scanpage.</li>
     * </lu>
     * @param sp The scanpage (containing http and user info) that calls the function
     * @param tok a list of strings that describe the (sub)command to execute
     * @return the result value as a <code>String</code>
     */
    public String replace(scanpage sp, StringTokenizer tok) {
        if (tok.hasMoreTokens()) {
            String cmd=tok.nextToken();
            if (cmd.equals("number")) {
                int i=getNumber(sp.getSessionName());
                if (i!=-1) {
                    return ""+i;
                }
            }
        }
        return "";
    }

    /**
     * Get the object number for the current user's people object, based on the current user/cookie as defined by the key.
     * Only works in SCAN, when used with the users builder.
     * @dependency users
     * @performance reference to users should be set during init() method.
     */
    public int getNumber(String key) {
        Integer n=peopleCache.get(key);
        if (n!=null) {
            log.debug("People - "+key+" people found in cache.");
            return n.intValue();
        }
        // obtain a number from users
        Users bul=(Users)mmb.getMMObject("users");
        if (bul!=null) {
            int i=bul.getNumber(key);
            if (i!=-1) {
                // lets find a related people object
                MMObjectNode node=getNode(i);
                Enumeration<MMObjectNode> e=node.getRelatedNodes("people").elements();
                if (e.hasMoreElements()) {
                    MMObjectNode node2 = e.nextElement();
                    if (node2!=null) {
                        int number=node2.getNumber();
                        peopleCache.put(key,new Integer(number));
                        return number;
                    }
                }
            }
        }
        return -1;
    }

    /**
     * flush caches of the (cookie defined) user
     * also signals the session module
     */
    public void flushCache(String key) {
        //bugfix #6583: NullPointerException when key = null
        if(key!=null) 
            peopleCache.remove(key);
    }
}
