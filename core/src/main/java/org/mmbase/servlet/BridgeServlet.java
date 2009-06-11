/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.servlet;

import java.io.IOException;
import java.util.regex.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.*;

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
 * Object can only be accessed by alias if a mapping on query string is used (so not e.g. /images/*,
 * but /img.db). Normally this is no problem, because the alias is resolved by the image-tag. But if
 * for some reason you need aliases to be working on the URL, you must map to URL's with a question mark.
 *
 * @version $Id$
 * @author Michiel Meeuwissen
 * @since  MMBase-1.6
 */
public abstract class BridgeServlet extends  MMBaseServlet {
    public static final String MESSAGE_ATTRIBUTE = "org.mmbase.servlet.error.message"; // javax.servlet.error.message is a bit short normally

    /**
     * Pattern used for the 'filename' part of the request. The a node-identifying string may be
     * present in it, and it the one capturing group.
     * It is a digit optionially followed by +.* (used in ImageServlet for url-triggered icache production)
     */

    public static final Pattern FILE_PATTERN = Pattern.compile(".*?\\D((?:session=.*?\\+)?\\d+(?:\\+.+?)?)(/.*)?");
    // some examples captured by this regexp:
    //   /mmbase/images/session=mmbasesession+1234+s(100)/image.jpg
    //   /mmbase/images/1234+s(100)/image.jpg
    //   /mmbase/images/1234/image.jpg
    //   /mmbase/images/1234
    //   /mmbase/images?1234  (1234 not captured by regexp, but is in query!)


    // may not be digits in servlet mapping itself!


    private static Logger log;

    /**
     * This is constant after init.
     */
    private static int contextPathLength = -1;


    private String lastModifiedField = null;

    /**
     * The name of the mmbase cloud which must be used. At the moment this is not supported (every
     * mmbase cloud is called 'mmbase').
     */
    protected String getCloudName() {
        return "mmbase";
    }



    /**
     * Creates a QueryParts object which wraps request and response and the parse result of them.
     * @return A QueryParts or <code>null</code> if something went wrong (in that case an error was sent, using the response).
     */
    protected QueryParts readQuery(HttpServletRequest req, HttpServletResponse res) throws IOException  {
        QueryParts qp = (QueryParts) req.getAttribute("org.mmbase.servlet.BridgeServlet$QueryParts");
        if (qp != null) {
            log.trace("no need parsing query");
            if (qp.getResponse() == null && res != null) {
                qp.setResponse(res);
            }
            return qp;
        }
        if (log.isTraceEnabled()) {
            log.trace("parsing query ");
        }

        String q = req.getQueryString();

        if (q == null || "".equals(q)) { // should be null if no query string, but http://issues.apache.org/bugzilla/show_bug.cgi?id=38113, there is version of tomcat in which it isn't.
            qp = readQueryFromRequestURI(req, res);
        } else {
            if(log.isDebugEnabled()) {
                log.debug("using query " + q + " to find node number");
            }
            // attachment.db?[session=abc+]number
            qp = readQuery(q);
            if (qp == null && res != null) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Malformed URL: No node number found after session.");
                req.setAttribute(MESSAGE_ATTRIBUTE, "Malformed URL: No node number found after session.");
            }

        }

        if (qp == null) return null;

        qp.setRequest(req);
        qp.setResponse(res);

        req.setAttribute("org.mmbase.servlet.BridgeServlet$QueryParts", qp);
        return qp;
    }



    protected QueryParts readQueryFromRequestURI(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        QueryParts qp;
        // also possible to use /attachments/[session=abc+]<number>/filename.pdf
        if (contextPathLength == -1) {
            contextPathLength = req.getContextPath().length();
        }
        String reqString = req.getRequestURI().substring(contextPathLength); // substring needed, otherwise there may not be digits in context path.

        // some silly application-servers leave jsession id it the requestURI. Take if off again, because we'll be very confused by it.
        if (req.isRequestedSessionIdFromURL()) {
            int jsessionid = reqString.indexOf(";jsessionid=");
            if (jsessionid != -1) {
                reqString = reqString.substring(0, jsessionid);
            }
        }

        if(log.isDebugEnabled()) {
            log.debug("using servlet URI " + reqString + " to find node number");
        }

        qp = readServletPath(reqString);
        if (qp == null) {
            log.debug("Did not match");
            if(res != null) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Malformed URL: '" + reqString + "' does not match '"  + FILE_PATTERN.pattern() + "'.");
                req.setAttribute(MESSAGE_ATTRIBUTE, "Malformed URL: '" + reqString + "' does not match '"  + FILE_PATTERN.pattern() + "'.");
            } else {
                log.error("Malformed URL: '" + reqString + "' does not match '"  + FILE_PATTERN.pattern() + "'.");
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("found " + qp);
            }
        }
        return qp;
    }


    /**
     *
     * @since MMBase-1.7.4
     */
    public static QueryParts readServletPath(String servletPath) {
        Matcher m = FILE_PATTERN.matcher(servletPath);
        if (! m.matches()) {
            return null;
        }
        QueryParts qp = readQuery(m.group(1));
        qp.setFileName(m.group(2));
        return qp;
    }

    /**
     *
     * @since MMBase-1.7.4
     */
    public static QueryParts readQuery(String query) {
        String sessionName = null; // "cloud_" + getCloudName();
        String nodeIdentifier;
        if (query.startsWith("session=")) {
            // indicated the session name in the query: session=<sessionname>+<nodenumber>

            int plus = query.indexOf("+", 8);
            if (plus == -1) {
                sessionName = "";
                nodeIdentifier = query;
            } else {
                sessionName = query.substring(8, plus);
                nodeIdentifier  = query.substring(plus + 1);
            }
        } else {
            nodeIdentifier  = query;
        }
        return new QueryParts(sessionName, nodeIdentifier);

    }


    /**
     * Obtains a cloud object, using a QueryParts object.
     * @return A Cloud or <code>null</code> if unsuccessful (this may not be fatal).
     */
    final protected Cloud getCloud(QueryParts qp) throws IOException {
        log.debug("getting a cloud");
        // trying to get a cloud from the session
        Cloud cloud = null;
        HttpSession session = qp.getRequest().getSession(false); // false: do not create a session, only use it
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

    /**
     * Obtains an 'anonymous' cloud.
     */
    final protected Cloud getAnonymousCloud() {
        try {
            return ContextProvider.getDefaultCloudContext().getCloud(getCloudName());
        } catch (org.mmbase.security.SecurityException e) {
            log.debug("could not generate anonymous cloud");
            // give it up
            return null;
        }
    }

    /**
     * Obtains a cloud using 'class' security. If e.g. you authorize org.mmbase.servlet.ImageServlet
     * by class-security for read all rights, it will be used.
     * @since MMBase-1.8
     */
    protected Cloud getClassCloud() {
        try {
            return ContextProvider.getDefaultCloudContext().getCloud(getCloudName(), "class", null); // testing Class Security
        } catch (java.lang.SecurityException e) {
            log.debug("could not generate class cloud");
            // give it up
            return null;
        }
    }



    /**
     * Tries to find a Cloud which can read the given node.
     * @since MMBase-1.8
     */
    protected Cloud findCloud(Cloud c, String nodeNumber, QueryParts query) throws IOException {

        if (c == null || ! (c.mayRead(nodeNumber))) {
            c = getClassCloud();
        }

        if (c == null || ! (c.mayRead(nodeNumber))) {
            c = getCloud(query);
        }
        if (c == null || ! (c.mayRead(nodeNumber)))  { // cannot find any cloud what-so-ever,
            HttpServletResponse res = query.getResponse();
            if (res != null) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Permission denied to anonymous for node '" + nodeNumber + "'");
            }
            return null;
        }
        return c;
    }

    /**
     * Servlets would often need a node. This function provides it.
     * @param query A QueryParts object, which you must have obtained by {@link #readQuery}
     */

    final protected Node getNode(QueryParts query)  throws IOException {
        try {
            if (log.isDebugEnabled()) {
                log.debug("query : " + query);
            }

            if (query == null) {
                return null;
            } else {
                Node n = query.getNode();
                if (n != null) {
                    return n;
                }
            }

            Cloud c = getAnonymousCloud(); // first try anonymously always, because then session has not to be used

            String nodeNumber = java.net.URLDecoder.decode(query.getNodeNumber(), "UTF-8");

            if (c != null && ! c.hasNode(nodeNumber)) {
                // ok, support for 'title' aliases too....
                Node desperateNode = desperatelyGetNode(c, nodeNumber);
                if (desperateNode != null) {
                    query.setNode(desperateNode);
                    return desperateNode;
                }
                HttpServletResponse res = query.getResponse();
                if (res != null) {
                    res.sendError(HttpServletResponse.SC_NOT_FOUND, "Node '" + nodeNumber + "' does not exist");
                    query.getRequest().setAttribute(MESSAGE_ATTRIBUTE, "Node '" + nodeNumber + "' does not exist");
                }
                return null;
            }

            c = findCloud(c, nodeNumber, query);
            if (c == null) {
                return null;
            }

            Node n = c.getNode(nodeNumber);
            query.setNode(n);
            return n;
        } catch (Exception e) {
            HttpServletResponse res = query.getResponse();
            if (res != null) {
                query.getResponse().sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.toString());
            }
            return null;
        }
    }

    /**
     * Extensions can override this, to produce a node, even if cloud.hasNode failed. ('title aliases' e.g.).
     * @since MMBase-1.7.5
     */
    protected Node desperatelyGetNode(Cloud cloud, String nodeIdentifier) {
        return null;
    }

    /**
     * If the node associated with the resonse is another node then the node associated with the request.\
     * (E.g. a icache based on a url with an image node).
     * @param qp A QueryParts object, which you must have obtained by {@link #readQuery}
     * @param node The node which is specified on the URL (obtained by {@link #getNode}
     * @since MMBase-1.7.4
     */
    protected Node getServedNode(QueryParts qp, Node node) throws IOException {
        return node;
    }

    /**
     * The idea is that a 'bridge servlet' on default serves 'nodes', and that there could be
     * defined a 'last modified' time for nodes. This can't be determined right now, so 'now' is
     * returned.
     *
     * This function is defined in HttpServlet
     * {@inheritDoc}
     **/
    @Override
    protected long getLastModified(HttpServletRequest req) {
        if (lastModifiedField == null) return -1;
        try {
            QueryParts query = readQuery(req, null);
            Node node = getServedNode(query, getNode(query));
            if (node != null) { // && node.getNodeManager().hasField(lastModifiedField)) {
                return node.getDateValue(lastModifiedField).getTime();
            } else {
                return -1;
            }
        } catch (IOException ieo) {
            return -1;
        }
    }

    /**
     * Inits lastmodifiedField.
     * {@inheritDoc}
     */

    @Override
    public void init() throws ServletException {
        super.init();
        lastModifiedField = getInitParameter("lastmodifiedfield");
        if ("".equals(lastModifiedField)) lastModifiedField = null;
        log = Logging.getLoggerInstance(BridgeServlet.class);
        if (lastModifiedField != null) {
            log.service("Field '" + lastModifiedField + "' will be used to calculate lastModified");
        }
    }

    /**
     * Keeps track of determined information, to avoid redetermining it.
     */
    final static public  class QueryParts {
        private String sessionName;
        private String nodeIdentifier;
        private HttpServletRequest req;
        private HttpServletResponse res;
        private Node node;
        private Node servedNode;
        private String fileName;
        QueryParts(String sessionName, String nodeIdentifier) {
            this.sessionName = sessionName;
            this.nodeIdentifier = nodeIdentifier; 

        }
        void setNode(Node node) {
            this.node = node;
        }
        Node getNode() {
            return node;
        }
        void setServedNode(Node node) {
            this.servedNode = node;
        }
        Node getServedNode() {
            return servedNode;
        }
        void setFileName(String fn) {
            fileName = fn;
        }
        public String getFileName() {
            return fileName;
        }
        public String getSessionName() {
            return sessionName;
        }
        public String getNodeNumber() {
            int i = nodeIdentifier.indexOf('+');
            if (i > 0) {
                return nodeIdentifier.substring(0, i);
            } else {
                return nodeIdentifier;
            }
        }
        void setRequest(HttpServletRequest req) {
            this.req = req;
        }
        void setResponse(HttpServletResponse res) {
            this.res = res;
        }

        HttpServletRequest getRequest() {
            return req;
        }
        HttpServletResponse getResponse() {
            return res;
        }

        /**
         * @since MMBase-1.7.4
         */
        public String getNodeIdentifier() {
            return nodeIdentifier;
        }

        @Override
        public  String toString() {
            return sessionName == null ? nodeIdentifier : "session=" + sessionName + "+" + nodeIdentifier;
        }


    }

    /**
     * Just to test to damn regexp
     */
    public static void main(String[] argv) {

        Matcher m = FILE_PATTERN.matcher(argv[0]);
        if (! m.matches()) {
            System.out.println("Didn't match");
        } else {
            System.out.println("Found node " + m.group(1));
        }
    }

}
