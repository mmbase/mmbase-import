/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

import java.util.Set;
import java.util.regex.Pattern;
import java.io.File;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;

/**
 * Returns the path to use for TREEPART, TREEFILE, LEAFPART and LEAFFILE.
 * The system searches in a provided base path for a filename that matches the supplied number/alias of
 * a node (possibly extended with a version number). See the documentation on the TREEPART SCAN command for more info.
 *
 * This class can be overriden to make an even smarter search possible.
 *
 * @since MMBase-1.8.5
 * @version $Id: SmartPathFunction.java,v 1.3 2007-06-07 13:22:06 michiel Exp $
 */
public class SmartPathFunction {
    private static final Logger log = Logging.getLoggerInstance(SmartPathFunction.class);

    private final MMObjectBuilder parent;
    private String nodeNumber;
    private String version;
    private String path;
    private String documentRoot;
    private boolean backwardsCompatible = true;

    private ResourceLoader webRoot = ResourceLoader.getWebRoot();

    public SmartPathFunction(MMObjectBuilder p) {
        parent = p;
    }

    /**
     * The number or alias of the node to filter on
     */
    public void setNodeNumber(String nm) {
        log.debug("Setting " + nodeNumber);
        nodeNumber = nm;
    }

    public void setNode(org.mmbase.bridge.Node n) {
        if (nodeNumber == null) {
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
     * @deprecated Use {@link #setLoader(ResourceLoder)}.
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
    public void setPath(String p) {
        path = p;
    }

    public void setBackwardsCompatible(boolean b) {
        backwardsCompatible = b;
    }
    public boolean getBackwardsCompatible() {
        return backwardsCompatible;
    }
    /**
     * The found path as a <code>String</code>, or <code>null</code> if not found    
     */
    public String smartpath() {
        log.debug("Determining smartpath for node " + nodeNumber + " " + parent.getTableName());
        if (backwardsCompatible) {
            return parent.getSmartPath(documentRoot, path, nodeNumber, version);
        } else {
            log.info("Doing NEW way");
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


