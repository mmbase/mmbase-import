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
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * BridgeServlet is an MMBaseServlet with a bridge Cloud in
 * it. Extending from this makes it easy to implement servlet
 * implemented with the MMBase bridge interfaces.
 *
 * An advantage of this is that security is used, which means that you
 * cannot unintentionly serve content to the whole world which should
 * actually be protected by the security mechanism.
 *
 * Another advantage is that implementation using the bridge is
 * easier/clearer.
 *
 * The query of a bridge servlet can possible start with
 * #<session-variable-name># in which case the cloud is taken from
 * that session attribute. If the query does not begin with #, an
 * 'anonymous' cloud is used. 
 *
 * @todo This #session# is not yet used anywhere (editors should be
 * aware of this). Things are not going to work nice in editors
 * because they use for example the 'gui()' function, which should
 * then produce an url to a servlet with #session# in it.
 *
 * @version $Id: BridgeServlet.java,v 1.2 2002-06-28 21:03:45 michiel Exp $
 * @author Michiel Meeuwissen
 * @since  MMBase-1.6
 */
public abstract class BridgeServlet extends  MMBaseServlet {
    private static Logger log;
    private Cloud cloud;

    protected String getCloudName() {
        return "mmbase";
    }

    protected Cloud getCloud() {        
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
     
    protected Node getNode(HttpServletRequest req, HttpServletResponse res) throws java.io.IOException {
        String query = req.getQueryString();        
        if (query == null) { // also possible to use /attachments/<number>
            query = new java.io.File(req.getRequestURI()).getName();
        }
        Cloud c;
        if (query.startsWith("#")) { // request to use cloud from session
            int until = query.indexOf('#');
            if (until == -1) {
                res.sendError(res.SC_NOT_FOUND, "Malformed URL");
            }
            String sessionName = query.substring(1, until);
            HttpSession session = req.getSession();
            query = query.substring(until + 1);            
            c = (Cloud) session.getAttribute(sessionName); 
        } else {
            c = getCloud();
        }

        Node node = null;
        try {
            node = c.getNode(query);
        } catch (org.mmbase.bridge.NotFoundException e) {
            res.sendError(res.SC_NOT_FOUND, "Node " + query + " does not exist");
        } catch (Exception e) {
            res.sendError(res.SC_NOT_FOUND, "Problem with Node " + query + " : " + e.toString());
        }
        return node;
    }

    /**
     */

    public void init() throws ServletException {
        super.init();
        log = Logging.getLoggerInstance(BridgeServlet.class.getName());
        cloud = LocalContext.getCloudContext().getCloud(getCloudName());
    }

}
