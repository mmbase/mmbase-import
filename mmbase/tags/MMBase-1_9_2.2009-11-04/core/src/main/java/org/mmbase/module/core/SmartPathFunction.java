/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

import java.util.Set;
import java.util.regex.Pattern;
import org.mmbase.util.*;
import org.mmbase.cache.Cache;
import org.mmbase.util.logging.*;
import org.mmbase.util.functions.Required;


/**
 * Returns the path to use for TREEPART, TREEFILE, LEAFPART and LEAFFILE.
 * The system searches in a provided base path for a filename that matches the supplied number/alias of
 * a node (possibly extended with a version number). See the documentation on the TREEPART SCAN command for more info.
 *
 * This class can be overriden to make an even smarter search possible.
 *
 * @since MMBase-1.8.5
 * @version $Id$
 */
public class SmartPathFunction {
    private static final Logger log = Logging.getLoggerInstance(SmartPathFunction.class);

    protected static Cache<String, String> smartPathCache = new Cache<String, String>(100) {
            public String getName() {
                return "SmartPathCache";
            }
            public String getDescription() {
                return "Caches the result of the 'smartpath' function";
            }
        };
    static {
        smartPathCache.putCache();
    }


    protected final MMObjectBuilder parent;
    protected String nodeNumber;
    protected String version;
    protected String path;
    protected String documentRoot;
    protected boolean backwardsCompatible = true;

    protected ResourceLoader webRoot = ResourceLoader.getWebRoot();

    public SmartPathFunction(MMObjectBuilder p) {
        parent = p;
    }

    /**
     * The number or alias of the node to filter on
     */
    public void setNodeNumber(String nm) {
        log.debug("Setting " + nodeNumber);
        if (nm != null && ! nm.equals("")) {
            nodeNumber = nm;
        }
    }

    public void setNode(org.mmbase.bridge.Node n) {
        if (nodeNumber == null || "".equals(nodeNumber)) {
            nodeNumber = "" + n.getNumber();
        }
    }
    /**
     * The version number (or <code>null</code> if not applicable) to filter on
     */
    public void setVersion(String v) {
        version = v;
    }

    /**
     * the root of the path to search.
     * @deprecated Use {@link #setLoader(ResourceLoader)}.
     */
    public void setRoot(String r) {
        documentRoot = r;
    }

    public void setLoader(ResourceLoader r) {
        webRoot = r;
    }
    public ResourceLoader getLoader() {
        return webRoot;
    }
    /**
     * The subpath of the path to search
     */
    @Required public void setPath(String p) {
        path = p;
    }

    public void setBackwardsCompatible(boolean b) {
        backwardsCompatible = b;
    }
    public boolean getBackwardsCompatible() {
        return backwardsCompatible;
    }

    public String smartKey() {
        return path + '.' + nodeNumber + '.' + version;
    }

    /**
     * The found path as a <code>String</code>, or <code>null</code> if not found
     */
    public final String smartpath() {
        String result;
        String key = null;
        if (smartPathCache.isActive()) {
            key = smartKey();
            result = smartPathCache.get(key);
            if (result != null || smartPathCache.containsKey(key)) {
                return result;
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("Determining smartpath for node " + nodeNumber + " " + parent.getTableName());
        }
        result = getSmartPath();

        if (key != null) {
            smartPathCache.put(key, result);
        }
        return result;
    }

    /**
     * The found path as a <code>String</code>, or <code>null</code> if not found
     */
    protected String getSmartPath() {
        log.debug("Determining smartpath for node " + nodeNumber + " " + parent.getTableName());
        if (backwardsCompatible) {
            return parent.getSmartPath(documentRoot, path, nodeNumber, version);
        } else {
            log.debug("Doing NEW way");
            ResourceLoader child = webRoot.getChildResourceLoader(path);
            String node = nodeNumber;
            if (version != null) node += "\\." + version;
            Set s = child.getChildContexts(Pattern.compile(".*\\D" + node + "\\D.*"), false);
            if (s.size() == 0) {
                return null;
            } else {
                return path + s.iterator().next() + "/";
            }
        }
    }

}


