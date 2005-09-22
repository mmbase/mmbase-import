/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package org.mmbase.module.builders;

import java.util.*;

import org.mmbase.core.event.NodeEvent;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;

/**
 * @javadoc
 * @author Daniel Ockeloen
 * @version $Id: Versions.java,v 1.15 2005-09-22 20:53:21 michiel Exp $
 */
public class Versions extends MMObjectBuilder implements MMBaseObserver {

    private static final Logger log = Logging.getLoggerInstance(Versions.class);

    private Hashtable cacheVersionHandlers = new Hashtable();

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
     * @return the node that contains version information about "name", "type" or null if no version
     * information is avaiable
     * @throws SearchQueryException
     * @since MMBase-1.7
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
            retval = (MMObjectNode) i.next();
        }
        //should not happend
        if (i.hasNext()) {
            StringBuffer sb = new StringBuffer();
            MMObjectNode curent = retval;
            sb.append("versions node[number,version,maintainer]:");
            while (curent != null) {
                sb.append("[");
                sb.append(curent.getNumber());
                sb.append(",");
                sb.append(curent.getIntValue("version"));
                sb.append(",");
                sb.append(curent.getStringValue("maintainer"));
                sb.append("]");
                if (i.hasNext()) {
                    curent = (MMObjectNode) i.next();
                } else {
                    curent = null;
                }
            }
            log.warn("more than one version was found for " + type + " with name " + name + " ." + sb.toString());
        }
        return retval;

    }

    /**
     * @javadoc
     */
    public int getInstalledVersion(String name, String type) throws SearchQueryException {
        MMObjectNode node = getVersionNode(name, type);
        if (node == null) { return -1; }
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
            cacheVersionHandlers = parser.getCacheVersions(cacheVersionHandlers);
        }
        for (Enumeration e = cacheVersionHandlers.keys(); e.hasMoreElements();) {
            String bname = (String) e.nextElement();
            mmb.addLocalObserver(bname, this);
            mmb.addRemoteObserver(bname, this);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.mmbase.module.core.MMObjectBuilder#notify(org.mmbase.core.event.NodeEvent)
     */
    public void notify(NodeEvent event) {
        if (log.isDebugEnabled()) {
            log.debug("Changed " + event.getMachine() + " " + event.getNode().getNumber() + " "
                + event.getNode().getBuilder().getTableName() + " " + NodeEvent.newTypeToOldType(event.getType()));
        }
        String builder = event.getNode().getBuilder().getTableName();
        Vector subs = (Vector) cacheVersionHandlers.get(builder);
        int inumber = event.getNode().getNumber();
        if (subs != null) {
            for (Enumeration e = subs.elements(); e.hasMoreElements();) {
                VersionCacheNode cnode = (VersionCacheNode) e.nextElement();
                cnode.handleChanged(builder, inumber);
            }
        }
        super.notify(event);
    }
}
