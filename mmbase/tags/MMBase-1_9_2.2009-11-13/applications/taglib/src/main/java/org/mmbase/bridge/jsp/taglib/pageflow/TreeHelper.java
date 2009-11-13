/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */

package org.mmbase.bridge.jsp.taglib.pageflow;

import java.io.*;
import java.util.StringTokenizer;
import java.util.Stack;
import java.net.*;
import javax.servlet.jsp.JspTagException;
import javax.servlet.http.HttpSession;
import org.mmbase.bridge.*;
import org.mmbase.util.ResourceLoader;
import org.mmbase.util.functions.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.module.core.MMBaseContext;

/**
 * This helper-class has all Tree- and Leaf-related functionality. The algorithms that find the
 * page to include in the directory structure are implemented here.
 *
 * The 'TreeInclude', 'LeafInclude', 'TreeFile' and 'LeafFile' all use this helper class. See their
 * documentation for more information about the algorithms.
 *
 * @author Johannes Verelst
 * @author Rob Vermeulen (VPRO)
 * @version $Id$
 * @todo   MM: This class is not actually related to Taglib. I propose to move it to  org.mmbase.util, or org.mmbase.bridge.util
 */

public class TreeHelper {
    /*
        Idea:
            - we have a list of objectnumbers
            - find the builder for every object
            - call 'getSmartPath() for every object', using the cumulative
              path as 'relative' path for the next getSmartPath() call
            - if a path doesn't exist, we will (if we are LeafParting) look at
              the builder-names to continue the path-walking
            - walk the list backwards and try to find 'page' in the
              path, if found, return that page. If not found, continue

        Needed:
            - How to find a builder for an objectnumber? Use bridge

     */

    private Cloud cloud;
    private boolean backwardsCompatible = true;
    private static final Logger log = Logging.getLoggerInstance(TreeHelper.class);
    private static final ResourceLoader htmlRoot = ResourceLoader.getWebRoot();

    public void setCloud(Cloud cl) {
        cloud = cl;
    }
    public void setBackwardsCompatible(boolean b) {
        backwardsCompatible = b;
    }

    /**
     * Method to find the file to 'LeafInclude' given a list of objectnumbers
     * @param includePage The page to include (relative path, may include URL parameters)
     * @param objectlist The list of objectnumbers (comma-seperated) that is used to find the correct file to include
     * @param session The session context can contain version information (used in getVerion).
     */
    protected String findLeafFile(String includePage, String objectlist, HttpSession session) throws JspTagException, IOException {
        if ("".equals(objectlist)) {
            return encodedPath(includePage);
        }
        String lf = getLeafFile("/", objectlist, includePage, true, session);
        if (log.isDebugEnabled()) {
            log.debug("findLeafFile = [" + lf + "]");
        }
        return encodedPath(lf);
    }

    /**
     * Return the path to the include file. This path will start with the given prefix, appended by data calculated using
     * the objectlist. If mayStartpath is true, then smartpath() will be called on objects in the objectlist,
     * otherwise only their buildernames will be used.
     * @param includePage The page to include (relative path, may include URL parameters)
     * @param objectlist The list of objectnumbers (comma-seperated) that is used to find the correct file to include
     * @param session The session context can contain version information (used in getVerion).
     * @param maySmartpath Boolean indicating whether or not getLeafFile may call a 'getSmartpath' on the given objects
     * @param prefix The path that was already established by previous calls to getLeafFile, deeper in the recursion tree.
     */
    protected String getLeafFile(String prefix, String objectlist, final String includePage, boolean maySmartpath, HttpSession session) throws JspTagException, IOException {

        if (log.isDebugEnabled()) {
            log.debug("prefix: " + prefix + " objectlist: " + objectlist + " includePage " + includePage);
        }
        if (objectlist.length() == 0) {
            String nudePage = includePage;
            if (nudePage.indexOf('?') != -1) {
                nudePage = nudePage.substring(0, nudePage.indexOf('?'));
            }

            String fileName = concatpath(prefix, nudePage);
            if (log.isDebugEnabled()) {
                log.debug("Check file: " + fileName + " in root " + htmlRoot);
            }

            if (htmlRoot.getResource(fileName).openConnection().getDoInput()) {
                return concatpath(prefix, includePage);
            } else {
                return "";
            }
        }

        int firstComma = objectlist.indexOf(',');
        String firstObject = null;
        String otherObjects = null;

        if (firstComma > 0) {
            firstObject = objectlist.substring(0, firstComma);
            otherObjects = objectlist.substring(firstComma + 1, objectlist.length());
            if (log.isDebugEnabled()) {
                log.debug("Splitting '" + objectlist + "' into '" + firstObject + "' and '" + otherObjects + "'");
            }
        } else {
            firstObject = objectlist;
            otherObjects = "";
            if (log.isDebugEnabled()) {
                log.debug("Only one object left: '" + firstObject + "'");
            }
        }

        String finalfile = null;

        // It can be the case that the first object here is not a number,
        // but a intermediate path. In that case we concatenate this intermediate
        // path with the path we already have (prefix) and continue with the recursive
        // loop
        if (! cloud.hasNode(firstObject)) {
            log.debug("'" + firstObject + "' is not an object; seeing it as a path)");
            return getLeafFile (concatpath(prefix, firstObject), otherObjects, includePage, maySmartpath, session);
        }

        // Try to find the best file (so starting with the best option)
        // We walk the first object in the objectlist, and evaluate its
        // smartpath. We will append that to the prefix, and continue recursively.

        if (maySmartpath) {
            String newprefix = prefix;
            String smartpath = getSmartPath(firstObject, newprefix, session);
            if (log.isDebugEnabled()) {
                log.debug("getSmartPath(" + firstObject + "," + newprefix + "," + session + ") = " + smartpath);
            }
            if (!(smartpath == null || smartpath.length() == 0)) {
                newprefix = smartpath;
                finalfile = getLeafFile(newprefix, otherObjects, includePage, true, session);
            }
        }

        // In case the recursive call failed, or the 'maySmartPath' was false,
        // we create a list of buildernames for this object; the builder of the
        // object with the parents of that builder. We then recurse again for
        // all these names, but we put the 'maySmartpath' to false for these
        // recursive calls.

        if (finalfile == null || "".equals(finalfile)) {
            NodeManager nm = cloud.getNode(firstObject).getNodeManager();
            while (nm != null) {
                finalfile = getLeafFile(concatpath(prefix, nm.getName()) + '/', otherObjects, includePage, false, session);
                if (!(finalfile == null || "".equals(finalfile)))
                    return finalfile;
                try {
                    nm = nm.getParent();
                } catch (NotFoundException e) {
                    nm = null;
                }
            }
        } else {
            return finalfile;
        }
        return "";
    }
    /**
     * Method to find the file to 'TreeInclude' given a list of objectnumbers
     * @param includePage The page to include (relative path, may include URL parameters)
     * @param objectlist The list of objectnumbers (comma-seperated) that is used to find the correct file to include
     * @param session The session context can contain version information (used in getVerion).
     * TODO: add support for 'intermediate paths' as LeafInclude has.
     */
    public String findTreeFile(String includePage, String objectlist, HttpSession session) throws JspTagException, IOException {
        if (cloud == null) {
            throw new JspTagException("Cloud was not defined");
        }
        if (log.isDebugEnabled()) {
            log.debug("Finding tree-file for " + includePage + " " + objectlist);
        }

        // We have to find a specific page, so we must remove any arguments
        String nudePage;
        if (includePage.indexOf('?') != -1) {
            nudePage = includePage.substring(0, includePage.indexOf('?'));
        } else {
            nudePage = includePage;
        }

        // Initialize the variables
        StringTokenizer st      = new StringTokenizer(objectlist, ",");
        int numberTokens        = st.countTokens();
        Stack<String> objectPaths       = new Stack<String>(); // synchronized for nothing

        int objectNumbers[]     = new int[numberTokens];
        String pathNow = "/";

        // Move all the objectnumbers into the array
        for (int i = 0; i < numberTokens; i++) {
            objectNumbers[i] = Integer.parseInt(st.nextToken());
        }

        // Find the paths for all the nodes in the nodelist
        for (int i = 0; i < numberTokens; i++) {
            int objectNo = objectNumbers[i];
            String field = getSmartPath("" + objectNo, pathNow, session);

            if (field == null || field.length() == 0) {
                break;
            }

            pathNow = field;
            objectPaths.push(pathNow);
        }

        //  We now have a list of paths in a stack, we must now find the best one.
        //  this means we walk the stack backwards (wow, what an amazing data-type :)
        //  We return the first path we find that != null and that contains 'page'

        pathNow = "";
        while (!objectPaths.empty()) {
            String path = objectPaths.pop();

            URL pathTest = htmlRoot.getChildResourceLoader(path).getResource(nudePage);

            if (log.isDebugEnabled()) {
                log.debug("Check file: " + pathTest);
            }
            if (pathTest.openConnection().getDoInput()) {
                // Make sure that the path is correctly encoded, if it contains spaces these must be
                // changed into '%20' etc.
                log.debug("" + pathTest + " is a file");
                return encodedPath(concatpath(path, includePage));
            }
        }

        // Check if the file exists in the 'root'
        if (log.isDebugEnabled()) {
            log.debug("Check file: " + htmlRoot.getResource(nudePage));
        }
        if (htmlRoot.getResource(nudePage).openConnection().getDoInput()) {
            return includePage;
        } else {
            return "";
        }
    }

    /**
     * A version can be set to easily make copies of websites.
     * By setting a sessions variable with name = object type and value = number,
     * will affect the getSmartpath method by adding the number to the end of the found smartpath.
     * Only if the object type corresponds to the object type of which the smartpath is evaluated.
     * @param objectnumber the objectnumber used in the smartpath
     * @return a versionnumber, or an empty string otherwise.
     */
    private String getVersion(String objectnumber, HttpSession session) throws JspTagException {
        if (session == null) {
            // No session variable set
            return "";
        }
        String versionnumber = (String)session.getAttribute(getBuilderName(objectnumber) + "version");
        if (versionnumber == null) {
            // The session variable was not set.
            return "";
        }
        return versionnumber;
    }

    /**
     * gets the object type name of an object.
     * @param objectnumber the object number of which you want the object type name
     * @return the object type
     */
    private String getBuilderName(String objectnumber) throws JspTagException {
        return cloud.getNode(objectnumber).getNodeManager().getName();
    }

    /**
     * get the smartpath of a certain object
     * @param objectnummer the object of which you want to evaluate the smartpath.
     * @param middle the path already evaluated (this is not used in current code).
     * @return the smartpath
     */
    private String getSmartPath(String objectNumber, String middle, HttpSession session) throws JspTagException {
        Node n = cloud.getNode(objectNumber);
        String version = getVersion(objectNumber, session);
        Function f = n.getFunction("smartpath");
        Parameters params = f.createParameters();
        params.set("root", MMBaseContext.getHtmlRoot());
        params.set("path", middle);
        if (version.length() != 0) {
            params.set("version", version);
        }
        params.set("nodeNumber", objectNumber);
        params.setIfDefined(Parameter.NODE, n);
        params.set("loader",   ResourceLoader.getWebRoot());
        params.set("backwardsCompatible",  backwardsCompatible);

        if (log.isDebugEnabled()) {
            log.debug("Using " + params);
        }
        return (String) f.getFunctionValue(params);
    }

    /**
     * Rewrite a path that points to a file on the filesystem to an URL path.
     * - Split the path on 'File.seperator'
     * - except for the last part (the file), escape all the parts
     * - combine the parts with a '/'
     *
     * @param fileSystemPath the path on a filesystem pointing to a files
     * @returns the URL-escaped version of the path
     */
    private String encodedPath(String fileSystemPath) {
        String fp = fileSystemPath;
        if (fp == null) {
            fp = "";
        }
        File f = new File(fp);
        String result = f.getName();
        f = f.getParentFile();
        while (f != null) {
            String thisPart = f.getName();
            result = org.mmbase.util.Encode.encode("ESCAPE_URL", thisPart) + "/" + result;
            f = f.getParentFile();
        }
        return result;
    }

    /**
     * Concatenate two paths; possibly adding or removing File.separator characters
     * Return path1/path2
     */
    private  String concatpath(String path1, String path2) {
        if (path1 == null && path2 == null) {
            return "";
        } else if (path1 == null) {
            return path2;
        } else if (path2 == null) {
            return path1;
        }
        if (path1.endsWith("/") && path2.startsWith("/")) {
            // we remove the "/" from the 2nd path element
            return path1 + path2.substring("/".length());
        } else if (!path1.endsWith("/") && !path2.startsWith("/")) {
            return path1 + "/" + path2;
        } else {
            return path1 + path2;
        }
    }

    public void doFinally() {
        cloud = null;
    }
}
