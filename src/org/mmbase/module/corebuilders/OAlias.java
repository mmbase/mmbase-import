/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.corebuilders;

import java.util.Enumeration;
import org.mmbase.cache.Cache;
import org.mmbase.module.core.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * The OAlias builder is an optional corebuilder used to associate aliases with nodes.
 * Each OAlias object contains a name field (the alias), and a destination field (the number of
 * the object referenced).
 * This builder is not used directly. If you add aliases, use {@link MMObjectBuilder.createAlias} instead
 * of the builder's insert method.
 * MMBase will run without this builder, but most applications use aliases.
 *
 * @author Rico Jansen
 * @version $Id: OAlias.java,v 1.12 2002-11-26 10:02:21 pierre Exp $
 */

public class OAlias extends MMObjectBuilder {

    // logging
    private static Logger log = Logging.getLoggerInstance(OAlias.class.getName());
    // cache
    private Cache numberCache = new Cache(128) {
        public String getName()        { return "AliasCache"; }
        public String getDescription() { return "Cache for node aliases"; }
        };

    public OAlias() {
        numberCache.putCache();
    }

    public boolean init() {
        boolean res=super.init();
        if (res) checkAddTmpField("_destination");
        return res;
    }

    /**
     * Obtain the number of a node through its alias
     * @param alias the alias of the desired node
     * @return the number of the node, or -1 if the alias does not exist
     * @see #getAliasedNode
     */
    public int getNumber(String name) {
        int rtn=-1;
        MMObjectNode node = (MMObjectNode)numberCache.get(name);
        if (node==null) {
            Enumeration e=search("name=='"+name+"'");
            if (e.hasMoreElements()) {
                node=(MMObjectNode)e.nextElement();
                rtn=node.getIntValue("destination");
                numberCache.put(name,node);
            }
        } else {
            rtn=node.getIntValue("destination");
        }
        return rtn;
    }

    /**
     * Obtain the alias of a node. If a node has more aliases, it returns only one.
     * Which one is not specified.
     * @param number the number of the node
     * @return the alias of the node, or null if it does not exist
     * @see #getNumber
     */
    public String getAlias(int number) {
        Enumeration e=search("destination=="+number);
        if (e.hasMoreElements()) {
            MMObjectNode node = (MMObjectNode)e.nextElement();
            return node.getStringValue("name");
        } else {
            return null;
        }
    }

    /**
     * Obtain a node from the cloud through its alias
     * @param alias the alias of the desired node
     * @return the node, or null if the alias does not exist
     * @throws RuntimeException if the alias exists but the node itself doesn't (this indicates
     *                          an inconsistency in the database)
     * @see #getNumber
     */
    public MMObjectNode getAliasedNode(String alias) {
        MMObjectNode node=null;
        int nr=getNumber(alias);
        if (nr>0) {
            try {
                node=getNode(nr);
            } catch (RuntimeException e) {
                log.error("Alias '"+alias+"' points to non-existing node with number "+nr);
                throw e;
            }
        }
        return node;
    }

    /**
     * Remove a node from the cloud and uopdate the cache
     * @param node The node to remove.
     */
    public void removeNode(MMObjectNode node) {
        String name=node.getStringValue("name");
        super.removeNode(node);
        numberCache.remove(name);
    }
}
