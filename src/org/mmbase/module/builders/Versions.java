/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.util.*;

import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;


/**
 * @javadoc
 * @author Daniel Ockeloen
 * @version $Id: Versions.java,v 1.10 2003-05-19 07:50:15 kees Exp $
 */
public class Versions extends MMObjectBuilder implements MMBaseObserver {

    private static Logger log = Logging.getLoggerInstance(Versions.class.getName());

    private Hashtable CacheVersionHandlers = new Hashtable();

    /**
     * @javadoc
     */
    public boolean init() {
        super.init();
        startCacheTypes();
        return true;
    }

    /**
     * @param name the name of the component we want to get know the version information about 
     * @param type the type of tye component we want to get information about (application/builder)
     * @return the node that contains version information about "name", "type" or null if no version information is avaiable
     * @throws SearchQueryException
     */
    public MMObjectNode getVersionNode(String name, String type) throws SearchQueryException {
        MMObjectNode retval = null;
        NodeSearchQuery query = new NodeSearchQuery(this);
        BasicCompositeConstraint constraints = new BasicCompositeConstraint(CompositeConstraint.LOGICAL_AND);
        constraints.addChild(new BasicFieldValueConstraint(query.getField(getField("name")), name));
        constraints.addChild(new BasicFieldValueConstraint(query.getField(getField("type")), type));
        query.setConstraint(constraints);

        Iterator i = getNodes(query).iterator();

        if (i.hasNext()) {
            retval = (MMObjectNode)i.next();
        }
        //should not happend
        if (i.hasNext()) {
            log.warn("More then one version was found for (name,type)()" + name + "," + type + ")");
        }
        return retval;

    }
    /**
     * @javadoc
     */
    public int getInstalledVersion(String name, String type) throws SearchQueryException {
        MMObjectNode node = getVersionNode(name, type);
        if (node == null) {
            return -1;
        }
        return node.getIntValue("version");
    }

    /**
     * @javadoc
     */
    public void setInstalledVersion(String name, String type, String maintainer, int version)
        throws SearchQueryException {

        MMObjectNode node = getVersionNode(name, type);
        if (node == null) {
            node = getNewNode("system");
            node.setValue("name", name);
            node.setValue("type", type);
            node.setValue("maintainer", maintainer);
            node.setValue("version", version);
            insert("system", node);
        } else {
            node.setValue("maintainer", maintainer);
            node.setValue("version", version);
            node.commit();
        }
    }

    /**
     * @javadoc
     */
    public void updateInstalledVersion(String name, String type, String maintainer, int version)
        throws SearchQueryException {
        setInstalledVersion(name, type, maintainer, version);
    }

    /**
     * @javadoc
     */
    public void startCacheTypes() {
        // is there a CacheVersion file ?
        String cacheversionfile = getInitParameter("cacheversionfile");

        if (cacheversionfile != null && !cacheversionfile.equals("")) {
            VersionXMLCacheNodeReader parser = new VersionXMLCacheNodeReader(cacheversionfile);
            parser.setBuilder(this);
            CacheVersionHandlers = parser.getCacheVersions(CacheVersionHandlers);
        }
        for (Enumeration e = CacheVersionHandlers.keys(); e.hasMoreElements();) {
            String bname = (String)e.nextElement();
            mmb.addLocalObserver(bname, this);
            mmb.addRemoteObserver(bname, this);
        }
    }

    /**
     * @javadoc
     */
    private boolean nodeChanged(String machine, String number, String builder, String ctype) {
        if (log.isDebugEnabled()) {
            log.debug("Versions -> signal change on " + number + " " + builder + " ctype=" + ctype);
        }
        Vector subs = (Vector)CacheVersionHandlers.get(builder);
        try {
            int inumber = Integer.parseInt(number);
            if (subs != null) {
                for (Enumeration e = subs.elements(); e.hasMoreElements();) {
                    VersionCacheNode cnode = (VersionCacheNode)e.nextElement();
                    cnode.handleChanged(builder, inumber);
                }
            }
        } catch (Exception e) {
            log.error(Logging.stackTrace(e));
        }
        return true;
    }

    /**
     * @javadoc
     */
    public boolean nodeLocalChanged(String machine, String number, String builder, String ctype) {
        getNode(number); // to make sure cache is valid
        super.nodeLocalChanged(machine, number, builder, ctype);
        return nodeChanged(machine, number, builder, ctype);
    }

    /**
     * @javadoc
     */
    public boolean nodeRemoteChanged(String machine, String number, String builder, String ctype) {
        super.nodeRemoteChanged(machine, number, builder, ctype);
        return nodeChanged(machine, number, builder, ctype);
    }
}
