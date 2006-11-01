package nl.didactor.builders;

import java.io.*;
import org.mmbase.util.*;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.util.logging.*;

/**
 * This builder implements the 'getSmartPath()' method (used by 
 * TreeInclude / Leafinclude) by using values in the fields that
 * are specified in the builder XML file. Only the getSmartPath()
 * method is implemented, extending classes should call the 
 * {@link #setSmartpathFields setSmartpathFields()} method.
 * @author Johannes Verelst &lt;johannes.verelst@eo.nl&gt;
 */
public abstract class AbstractSmartpathBuilder extends DidactorBuilder {
    protected static Logger log = Logging.getLoggerInstance("nl.didactor.builders.AbstractSmartpathBuilder");

    protected String spFieldNames[];
    protected String spPathPrefix = "";

    public AbstractSmartpathBuilder() {
    }

    public void setSmartpathFields(String[] newfield) {
        spFieldNames = newfield;
    }

    public boolean init() {
      return super.init();
    }

    /**
     * Returns the path to use for TREEPART, TREEFILE, LEAFPART and LEAFFILE.
     * The system searches in a provided base path for a filename that matches the supplied number/alias of
     * a node (possibly extended with a version number). See the documentation on the TREEPART SCAN command for more info.
     * @param documentRoot the root of the path to search
     * @param path the subpath of the path to search
     * @param nodeNumber the numbve ror alias of the node to filter on
     * @param version the version number (or <code>null</code> if not applicable) to filter on
     * @return the found path as a <code>String</code>, or <code>null</code> if not found
     * This method should be added to the bridge so jsp can make use of it.
     * This method can be overriden to make an even smarter search possible.
     */
    public String getSmartPath(String documentRoot, String path, String nodeNumber, String version) {
        log.debug("starting getSmartPath(" + documentRoot + "," + path + "," + nodeNumber + "," + version + ")");
        if (spFieldNames == null || spFieldNames.length == 0) {
            return super.getSmartPath(documentRoot, path, nodeNumber, version);
        }
        log.debug("Path is '" + path + "', smartpath-prefix is '" + spPathPrefix + "'");
        if (spPathPrefix == null) spPathPrefix = "";
        if (path == null) path = "";
        if (!spPathPrefix.equals("") && (path.equals("") || path.equals(File.separator))) {
            path = spPathPrefix;
        }
        File dir = new File(documentRoot + path);
        if (version != null) nodeNumber += "." + version;
	
        MMObjectNode node = getNode(nodeNumber);
        String magName = null;
        log.debug("Going to test " + spFieldNames.length + " fieldnames");

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
