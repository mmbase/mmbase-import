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

import java.util.*;
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
 * @version $Id: BridgeServlet.java,v 1.3 2002-06-30 20:15:52 michiel Exp $
 * @author Michiel Meeuwissen
 * @since  MMBase-1.6
 */
public abstract class BridgeServlet extends  MMBaseServlet {
    private static Logger log;
    private Cloud cloud;

    /**
     * Returns known functions which can be performed with bridge-functionality
     * specialisations would increase the priority for their specific goal.
     */


    protected String getCloudName() {
        return "mmbase";
    }

    private Cloud getCloud() {
        if (! cloud.getUser().isValid()) { 
            log.debug("Cloud was invalid, making new one");
            cloud = LocalContext.getCloudContext().getCloud(getCloudName());
        }
        return cloud;

    }


    /**
     * Servlets would often need a node. This function will get one for you using the query string.
     * This is convenient, and also ensures that all this kind of servlet work uniformely.
     */
     
    protected Node getNode(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String query = req.getQueryString();        
        if (query == null) { // also possible to use /attachments/<number>
            query = new java.io.File(req.getRequestURI()).getName();
        }

        Cloud c;

        // trying to get a cloud from the session
        HttpSession session = req.getSession(false); // false: do not create a session, only use it
        if (session != null) { // there is a session
            String sessionName = "cloud_" + getCloudName();
            if (query.startsWith("session=")) { // indicated the session name in the query: session=<sessionname>+<nodenumber>
                int plus = query.indexOf('+', 8);
                if (plus == -1) {
                    res.sendError(res.SC_NOT_FOUND, "Malformed URL");
                    return null;
                }
                sessionName = query.substring(8, plus);
                query = query.substring(plus + 1);                            
            } 
            c = (Cloud) session.getAttribute(sessionName); 

            // not found, simply take anonymous.
            if (c == null) c = getCloud();

        } else {
            // no session, take anonymous.
            c = getCloud();
        }

        Node node = null;
        try {
            node = c.getNode(query);
        } catch (org.mmbase.bridge.NotFoundException e) {
            res.sendError(res.SC_NOT_FOUND, "Node " + query + " does not exist");
        } catch (org.mmbase.security.SecurityException e) {
            res.sendError(res.SC_FORBIDDEN, "Permission denied: " + e.toString());
        } catch (Exception e) {
            res.sendError(res.SC_NOT_FOUND, "Problem with Node " + query + " : " + e.toString());
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
        log = Logging.getLoggerInstance(BridgeServlet.class.getName());
        cloud = LocalContext.getCloudContext().getCloud(getCloudName());
    }


}
