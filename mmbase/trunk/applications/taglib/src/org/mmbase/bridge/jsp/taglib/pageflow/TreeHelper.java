/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.bridge.jsp.taglib.pageflow;

import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.util.StringTokenizer;
import java.util.Stack;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspTagException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.mmbase.bridge.*;
import org.mmbase.bridge.jsp.taglib.*;
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
            - How to find out in which path we are now? Use getRealPath
     */
    
    private Cloud cloud;
    private static Logger log = Logging.getLoggerInstance(TreeHelper.class.getName());
    private static String htmlroot = MMBaseContext.getHtmlRoot();
    
    public void setCloud(Cloud cl) {
        cloud = cl;
    }
    
    /**
     * Method to find the file to 'LeafInclude' given a list of objectnumbers
     * @param includePage The page to include (relative path, may include URL parameters)
     * @param objectlist The list of objectnumbers (comma-seperated) that is used to find the correct file to include
     * @param session The session context can contain version information (used in getVerion).
     */
    protected String findLeafFile(String includePage, String objectlist, HttpSession session) throws JspTagException {
        String nudePage = includePage;
        if (nudePage.indexOf('?') != -1) {
            nudePage = nudePage.substring(0, nudePage.indexOf('?'));
        }
        
        // Create a list with object numbers (or aliases)
        StringTokenizer st = new StringTokenizer(objectlist, ",");
        int numberTokens = st.countTokens();
        String objectNumbers[] = new String[numberTokens];
        for (int i=0; i<numberTokens; i++) {
            objectNumbers[i] = ""+st.nextToken();
        }
        
        // Try to find the best file (so starting with the best option)
        for (int i=numberTokens; i>=0; i--) {
            String middle = "/";
            for (int u=0; u<numberTokens; u++) {
                if(u<i) {
                    String smartpath = getSmartPath(objectNumbers[u],middle,session);
                    if (smartpath==null || smartpath.equals("")) {
                        // If smartpath doesn't exist use object number
                        middle+=objectNumbers[u] + File.separator;
                    } else {
                        // The smartpath overrides all previous smartpaths
                        middle=smartpath;
                    }
                } else {
                    // Use the object type name
                    middle+=getBuilderName(objectNumbers[u]) + File.separator;
                }
            }
            // make sure the middle url ends with a file separator or not
            // double file separators, or none, leads to errors.
            // I choice here to be sure the middle ends always with a file separator
            // This means that the page attribute in the jsp page can cannot start with a file separator
            if (!middle.endsWith(File.separator)) {
                middle+=File.separator;
            }
            
            // Make sure that during concatenation of root+path, they are seperated with a File.seperator
            String extraSep = "";
            if (!htmlroot.endsWith(File.separator) && !middle.startsWith(File.separator) ) {
                extraSep = File.separator;
            }

            // Check if the file exists
            log.debug("Check file: "+htmlroot+extraSep+middle+nudePage);
            if (new File(htmlroot+extraSep+middle+nudePage).exists()) {
                return middle+includePage;
            }
        }
        return null;
    }
    
    
    
    
    /**
     * Method to find the file to 'TreeInclude' given a list of objectnumbers
     * @param includePage The page to include (relative path, may include URL parameters)
     * @param objectlist The list of objectnumbers (comma-seperated) that is used to find the correct file to include
     * @param session The session context can contain version information (used in getVerion).
     */
    protected String findTreeFile(String includePage, String objectlist, HttpSession session) throws JspTagException {
        if (cloud == null)
            throw new JspTagException("Cloud was not defined");
        
        // We have to find a specific page, so we must remove any arguments
        String nudePage;
        if (includePage.indexOf('?') != -1)
            nudePage = includePage.substring(0, includePage.indexOf('?'));
        else
            nudePage = includePage;
        
        // Initialize the variables
        StringTokenizer st      = new StringTokenizer(objectlist, ",");
        int numberTokens        = st.countTokens();
        Stack objectPaths       = new Stack();
        
        int objectNumbers[]     = new int[numberTokens];
        String pathNow = "/";
        
        // Move all the objectnumbers into the array
        for (int i=0; i<numberTokens; i++) {
            objectNumbers[i] = Integer.parseInt(st.nextToken());
        }
        
        // Find the paths for all the nodes in the nodelist
        for (int i=0; i<numberTokens; i++) {
            int objectNo = objectNumbers[i];
            String field = getSmartPath(""+objectNo, pathNow, session);
            
            if (field == null || field.equals("")) {
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
            String path = (String)objectPaths.pop();
            // Make sure path always end with a file separator
            if (!path.endsWith(File.separator)) {
                path+=File.separator;
            }

            // Make sure that during concatenation of root+path, they are seperated with a File.seperator
            String extraSep = "";
            if (!htmlroot.endsWith(File.separator) && !path.startsWith(File.separator) ) {
                extraSep = File.separator;
            }

            log.debug("Check file: "+htmlroot+extraSep+path+nudePage);
            if ((new File(htmlroot + extraSep + path + nudePage)).isFile()) {
                return path + includePage;
            }
        }
        
        // Check if the file exists in the 'root'
        log.debug("Check file: "+htmlroot+File.separator+nudePage);
        if (new File(htmlroot + nudePage).isFile()) {
            return includePage;
        } else {
            return null;
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
        if(session==null) {
            // No session variable set
            return "";
        }
        String versionnumber = (String)session.getAttribute(getBuilderName(objectnumber)+"version");
        if (versionnumber==null) {
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
    private String getSmartPath(String objectnumber, String middle, HttpSession session) throws JspTagException {
        return (String)cloud.getNode(objectnumber).getValue("smartpath(" + htmlroot + "," + middle + ","+getVersion(objectnumber, session)+")");
    }
}
