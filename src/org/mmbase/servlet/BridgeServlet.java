/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import org.mmbase.bridge.*;

import java.io.IOException;
import java.io.File;

import java.util.regex.*;


import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * BridgeServlet is an MMBaseServlet with a bridge Cloud in it. Extending from this makes it easy to
 * implement servlet implemented with the MMBase bridge interfaces.
 *
 * An advantage of this is that security is used, which means that you cannot unintentionly serve
 * content to the whole world which should actually be protected by the security mechanism.
 *
 * Another advantage is that implementation using the bridge is easier/clearer.
 *
 * The query of a bridge servlet can possible start with session=<session-variable-name> in which case the
 * cloud is taken from that session attribute with that name. Otherewise 'cloud_mmbase' is
 * supposed. All this is only done if there was a session active at all. If not, or the session
 * variable was not found, that an anonymous cloud is used.
 *
 * @version $Id: BridgeServlet.java,v 1.12 2003-11-11 22:02:33 michiel Exp $
 * @author Michiel Meeuwissen
 * @since  MMBase-1.6
 */
public abstract class BridgeServlet extends  MMBaseServlet {


    private static final Pattern FILE_PATTERN = Pattern.compile(".*?((?:session=.*\\+)?\\d+)(?:/.*)?");
    private static Logger log;

    /**
     */
    protected String getCloudName() {
        return "mmbase";
    }

    

    /**
     * Remove session information from query object, and returns session-name (or null)
     */
    final protected QueryParts readQuery(HttpServletRequest req, HttpServletResponse res) throws IOException  {
        String q = req.getQueryString();
        String query;
        if (q == null) { 
            // also possible to use /attachments/[session=abc+]<number>/filename.pdf
            //query = new StringBuffer(req.getRequestURI());
            Matcher m = FILE_PATTERN.matcher(req.getRequestURI());
            if (! m.matches()) {
                res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Malformed URL");
                return null;
           }
            query = m.group(1);
            
        } else {
            // attachment.db?[session=abc+]number
            query = q;
        }

        String sessionName = null; // "cloud_" + getCloudName();
        String nodeNumber;
        if (query.startsWith("session=")) { 
            // indicated the session name in the query: session=<sessionname>+<nodenumber>
            
            int plus = query.indexOf("+", 8);
            if (plus == -1) {
                res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Malformed URL");
                return null;
            }
            sessionName = query.substring(8, plus);
            nodeNumber  = query.substring(plus + 1);
        } else {
            nodeNumber  = query;
        }
        return new QueryParts(sessionName, nodeNumber);
    }

    final protected Cloud getCloud(HttpServletRequest req, HttpServletResponse res, QueryParts qp) throws IOException {
        log.debug("getting a cloud");
        // trying to get a cloud from the session
        Cloud cloud = null;
         HttpSession session = req.getSession(false); // false: do not create a session, only use it
        if (session != null) { // there is a session
            log.debug("from session");
            String sessionName = qp.getSessionName();
            if (sessionName != null) {
                cloud = (Cloud) session.getAttribute(sessionName); 
            } else { // desperately searching for a cloud, perhaps someone forgot to specify 'session_name' to enforce using the session?
                cloud = (Cloud) session.getAttribute("cloud_" + getCloudName()); 
            }
        } 
        return cloud;
    }

    final protected Cloud getAnonymousCloud() {
        Cloud cloud;
        try {
            cloud = ContextProvider.getDefaultCloudContext().getCloud(getCloudName());
        } catch (org.mmbase.security.SecurityException e) {
            log.debug("could not generate anonymous cloud");
            // give it up
            cloud = null;
        }
        return cloud;
    }


    /**
     * Servlets would often need a node. This function will get one for you using the query string.
     * This is convenient, and also ensures that all this kind of servlets work uniformely.
     */
     
    final protected Node getNode(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            QueryParts query = readQuery(req, res);
            if (query == null) return null;
            if (log.isDebugEnabled()) { 
                log.debug("query : " + query);
            }
            
            Cloud c = getAnonymousCloud(); // first try anonymously always, because then session has not to be used

            String nodeNumber = query.getNodeNumber();
            
            if (! c.hasNode(nodeNumber)) {
                res.sendError(HttpServletResponse.SC_NOT_FOUND, "Node '" + nodeNumber + "' does not exist");
                return null;
            }
            
            if (! c.mayRead(nodeNumber)) { // node may not be read by anonymous, try with a 'real' cloud now
                c = getCloud(req, res, query);
            }
            if (c == null)  { // cannot find any cloud what-so-ever, 
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Cannot find or create cloud '" + getCloudName() + "'");
                return null; 
            }        
            
            if (! c.mayRead(nodeNumber)) { // still not allowed? Give it up.
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Permission denied for node '" + nodeNumber + "'");
                return null;
            }
            return c.getNode(nodeNumber);
        } catch (Exception e) {
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.toString());           
            return null;
        }
    }

    /**
     * The idea is that a 'bridge servlet' on default serves 'nodes', and that there could be
     * defined a 'last modified' time for nodes. This can't be determined right now, so 'now' is
     * returned.
     *
     * This function is defined in HttpServlet
     **/
    protected long getLastModified(HttpServletRequest req) {
        // return getNode().getLastModified(); // pseudo-code
        return System.currentTimeMillis();
    }

    /**
     */

    public void init() throws ServletException {
        super.init();
        log = Logging.getLoggerInstance(BridgeServlet.class);
    }

    final class QueryParts {
        private String sessionName;
        private String nodeNumber;
        QueryParts(String sn, String nm) {
            sessionName = sn;
            nodeNumber  = nm;
        }
        String getSessionName() { return sessionName; }
        String getNodeNumber() { return nodeNumber; }

        public  String toString() {
            return sessionName == null ? nodeNumber : "session=" + sessionName + "+" + nodeNumber;
        }
                   
                   
    }

}
