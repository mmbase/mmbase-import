package nl.didactor.builders;

import java.io.*;
import org.mmbase.util.*;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.util.logging.*;

/**
 * This builder implements the 'getSmartPath()' method (used by 
 * TreeInclude / Leafinclude) by using values in the fields that
 * are specified in the builder XML file.
 *
 * @author Michiel Meeuwissen
 * @author Johannes Verelst &lt;johannes.verelst@eo.nl&gt;
 * @since Didactor-2.3
 * @version $Id: SmartPathFunction.java,v 1.2 2007-06-07 17:04:50 michiel Exp $
 */
public class SmartPathFunction extends org.mmbase.module.core.SmartPathFunction {
    protected static Logger log = Logging.getLoggerInstance(SmartPathFunction.class);

    protected final String spFieldNames[];
    protected final String spPathPrefix;

    public SmartPathFunction(MMObjectBuilder parent) {
        super(parent);
        String spFieldName = parent.getInitParameter("smartpathfield");

        if (spFieldName == null || spFieldName.equals("")) {
            throw new RuntimeException("You must specify the 'smartpathfield' property in your <properties> block for this builder " + parent);
        } 
        spFieldNames = spFieldName.split("\\s*,\\s*");

        String p = parent.getInitParameter("pathprefix");
        if (p == null) p = "";
        spPathPrefix = p;
    }

    /**
     * @TODO Make it work in a WAR (so don't use java.io.File)
     */
    public String smartpath() {
        if (log.isDebugEnabled()) {
            log.debug("starting getSmartPath(" + documentRoot + "," + path + "," + nodeNumber + "," + version + ")");
        }
        if (spFieldNames == null || spFieldNames.length == 0) {
            return super.smartpath();
        }
        if (log.isDebugEnabled()) {
            log.debug("Path is '" + path + "', smartpath-prefix is '" + spPathPrefix + "'");
        }
        if (path == null) path = "";
        if (!spPathPrefix.equals("") && (path.equals("") || path.equals(File.separator))) {
            path = spPathPrefix;
        }
        File dir = new File(documentRoot + path);
        if (version != null) nodeNumber += "." + version;
	
        MMObjectNode node = parent.getNode(nodeNumber);
        String magName = null;
        if (log.isDebugEnabled()) {
            log.debug("Going to test " + spFieldNames.length + " fieldnames");
        }
        if (node == null) {
            throw new RuntimeException("No node with number " + nodeNumber + " found");
        }

        for (int i=0; i<spFieldNames.length; i++) {
            magName = (String)node.getValue(spFieldNames[i]);
            log.debug(spFieldNames[i] + " = '" + magName + "'");
            if (magName != null && !"".equals(magName)) {
                log.debug("Got a not-null one!");
                break;
            }
        }

        if (magName == null)
            return null;

        String[] matches = dir.list(new ExactFileMatcher(magName));
        
        if ((matches == null) || (matches.length <= 0))
            return null;
        log.debug("Matching path: " + matches[0]);
        return path + matches[0] + File.separator;
    }

    /**
     * FilenameFilter that matches only files that exactly match the
     * given filename. 
     */
    private class ExactFileMatcher implements java.io.FilenameFilter {
        private String filename = "";
        
        public ExactFileMatcher(String filename) {
            this.filename = filename;
        }

        public boolean accept(File dir, String pathname) {
            return pathname.equals(filename); 
        }
    }
}
