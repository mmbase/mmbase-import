/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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
 * @version $Id: BridgeServlet.java,v 1.11 2003-11-06 16:23:22 michiel Exp $
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
    protected String readQuery(StringBuffer query, HttpServletResponse res) throws IOException  {
        String sessionName = "cloud_" + getCloudName();
        if (query.toString().indexOf("session=") >= 0) { 
            // indicated the session name in the query: session=<sessionname>+<nodenumber>
            
            int plus = query.toString().indexOf("+", 8);
            if (plus == -1) {
                res.sendError(HttpServletResponse.SC_NOT_FOUND, "Malformed URL");
                return null;
            }
            sessionName = query.toString().substring(8, plus);
            query.delete(0, plus + 1);                            
        }
        return sessionName;
    }

    protected Cloud getCloud(HttpServletRequest req, HttpServletResponse res, StringBuffer query) throws IOException {

        log.debug("getting a cloud");
        // trying to get a cloud from the session
        Cloud cloud = null;
         HttpSession session = req.getSession(false); // false: do not create a session, only use it
        if (session != null) { // there is a session
            log.debug("from session");
            String sessionName = readQuery(query, res);
            if (sessionName == null) return null;
            cloud = (Cloud) session.getAttribute(sessionName); 
        } 
        if (cloud == null) {
            // try anonymous
            try {
                cloud = ContextProvider.getDefaultCloudContext().getCloud(getCloudName());
            } catch (org.mmbase.security.SecurityException e) {
                log.debug("could not generate anonymous cloud");
                // give it up
                cloud = null;
            }
        }
        return cloud;
    }


    /**
     * Servlets would often need a node. This function will get one for you using the query string.
     * This is convenient, and also ensures that all this kind of servlets work uniformely.
     */
     
    protected Node getNode(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String q = req.getQueryString();
        StringBuffer query;
        if (q == null) { 
            // also possible to use /attachments/[session=abc+]<number>/filename.pdf
            //query = new StringBuffer(req.getRequestURI());
            Matcher m = FILE_PATTERN.matcher(req.getRequestURI());
            if (! m.matches()) {
                res.sendError(HttpServletResponse.SC_NOT_FOUND, "Malformed URL");
                return null;
            }
            query = new StringBuffer(m.group(1));
            
        } else {
            // attachment.db?[session=abc+]number
            query = new StringBuffer(q);
        }

        if (log.isDebugEnabled()) { 
            log.debug("query : " + query);
        }
        Cloud c = getCloud(req, res, query);
        if (c == null) return null;
        Node node = null;
        try {
            String qs = query.toString();
            if (c.hasNode(qs)) {
                node = c.getNode(qs);
            } else {
                // perhaps simply no session/expired session.
                if (readQuery(query, res) == null) return null;
                qs = query.toString();
                if (c.hasNode(qs)) {
                    node = c.getNode(qs);
                } else {                    
                    res.sendError(HttpServletResponse.SC_NOT_FOUND, "Node " + query + " does not exist" );
                }
            }
        } catch (org.mmbase.security.SecurityException e) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Permission denied: " + e.toString());
        } catch (Exception e) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND, "Problem with Node " + query + " : " + e.toString());
        }
        return node;
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


}
