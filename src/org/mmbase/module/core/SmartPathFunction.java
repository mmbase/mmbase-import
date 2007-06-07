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
 * @move maybe to a different SmartPathFunction class?
 * @param documentRoot the root of the path to search
 * @param path the subpath of the path to search
 * @param nodeNumber the number or alias of the node to filter on
 * @param version the version number (or <code>null</code> if not applicable) to filter on
 * @return the found path as a <code>String</code>, or <code>null</code> if not found
 * This method should be added to the bridge so jsp can make use of it.
 * This method can be overriden to make an even smarter search possible.
 *
 * @since MMBase-1.8.5
 */
public class SmartPathFunction {
    private static final Logger log = Logging.getLoggerInstance(SmartPathFunction.class);

    private final MMObjectBuilder parent;
    private String nodeNumber;
    private String version;
    private String path;
    private String documentRoot;
    private ResourceLoader webRoot = ResourceLoader.getWebRoot();

    public SmartPathFunction(MMObjectBuilder p) {
        parent = p;
    }

    public void setNodeNumber(String nm) {
        log.debug("Setting " + nodeNumber);
        nodeNumber = nm;
    }

    public void setNode(org.mmbase.bridge.Node n) {
        if (nodeNumber == null) {
            nodeNumber = "" + n.getNumber();
        }
    }

    public void setVersion(String v) {
        version = v;
    }

    public void setRoot(String r) {
        documentRoot = r;
    }

    public void setLoader(ResourceLoader r) {
        webRoot = r;
    }
    public ResourceLoader getLoader() {
        return webRoot;
    }

    public void setPath(String p) {
        path = p;
    }
    
    public String smartpath() {
        log.debug("Determining smartpath for node " + nodeNumber + " " + parent.getTableName());
        if (webRoot != null) {
            ResourceLoader child = webRoot.getChildResourceLoader(path);
            String node = nodeNumber;
            if (version != null) node += "\\." + version;
            Set s = child.getChildContexts(Pattern.compile(".*\\D" + node + "\\D.*"), false);
            if (s.size() == 0) {
                return null;
            } else {
                return path + s.iterator().next() + "/";
            }                
        } else {
            // backwards-compatibility
            return parent.getSmartPath(documentRoot, path, nodeNumber, version);
        }
    }

}


