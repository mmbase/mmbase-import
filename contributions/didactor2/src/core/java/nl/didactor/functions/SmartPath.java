package nl.didactor.functions;

import java.io.*;
import java.util.*;
import java.util.regex.*;
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
 * @version $Id: SmartPath.java,v 1.2 2007-06-21 16:34:25 michiel Exp $
 */
public class SmartPath extends org.mmbase.module.core.SmartPathFunction {
    protected static Logger log = Logging.getLoggerInstance(SmartPath.class);

    protected final String spFieldNames[];
    protected final String spPathPrefix;

    public SmartPath(MMObjectBuilder parent) {
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

    public void setDidactor(String test) {
        // just to add 'didactor' to the parameter list, which makes the function easier to recognize.
    }

    /**
     */
    protected String getSmartPath() {
        if (log.isDebugEnabled()) {
            log.debug("starting getSmartPath(" + webRoot + "," + path + "," + nodeNumber + "," + version + ")");
        }
        if (spFieldNames == null || spFieldNames.length == 0) {
            return super.getSmartPath();
        }
        if (log.isDebugEnabled()) {
            log.debug("Path is '" + path + "', smartpath-prefix is '" + spPathPrefix + "'");
        }
        if (path == null) path = "";
        if (!spPathPrefix.equals("") && (path.equals("") || path.equals("/"))) {
            path = spPathPrefix;
        }
        ResourceLoader child = webRoot.getChildResourceLoader(path);

        String n = nodeNumber;
        if (version != null && ! version.equals("")) n += "\\." + version;       
        MMObjectNode node = parent.getNode(n);

        String magName = null;
        if (log.isDebugEnabled()) {
            log.debug("Going to test with fields " + Arrays.asList(spFieldNames));
        }
        if (node == null) {
            throw new RuntimeException("No node with number " + n + " found");
        }

        for (String spFieldName : spFieldNames) {
            magName = (String) node.getValue(spFieldName);
            if (log.isDebugEnabled()) {
                log.debug(spFieldName + " = '" + magName + "'");
            }
            if (magName != null && !"".equals(magName)) {
                log.debug("Got a not-null one! ('" + magName + "')");
                break;
            }
        }

        if (magName == null) {
            return null;
        }

        Set<String> s = child.getChildContexts(Pattern.compile(".*/" + magName), false);

        if (s.size() == 0) {
            log.debug("Not found " + magName + " in " + child);
            return null;
        }

        String result = path + s.iterator().next() + "/";
        if (log.isDebugEnabled()) {
            log.debug("Matching path: " + s + " -> " + result);
        }
        return result;
    }

}
