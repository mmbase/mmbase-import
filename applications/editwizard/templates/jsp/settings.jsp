<%@page language="java" contentType="text/html; charset=utf-8"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><%@page import="java.io.*,java.util.*, org.mmbase.bridge.Cloud, org.mmbase.util.logging.Logger"
%><%@page import="org.mmbase.util.xml.URIResolver,org.mmbase.applications.editwizard.*"
%><%@ page import="org.mmbase.applications.editwizard.SecurityException,org.mmbase.applications.editwizard.Config"
%><%!
    /**
     * settings.jsp
     *
     * @since    MMBase-1.6
     * @version  $Id: settings.jsp,v 1.10 2002-05-17 13:31:53 pierre Exp $
     * @author   Kars Veling
     * @author   Michiel Meeuwissen
     */

    /**
     * @see org.mmbase.applications.editwizard.Config
     */
    class Configurator extends Config.Configurator {

        Configurator(HttpServletRequest req, HttpServletResponse res, Config c) {
            super(req, res, c);
            c.wizard    = getParam("wizard", c.wizard);
            c.maxupload = getParam("maxsize", new Integer(4 * 1024 * 1024)).intValue(); // 1 MByte max uploadsize
        }

        // which parameters to use to configure a list page
        public void config(Config.ListConfig c) {
            c.title       = getParam("title", c.title);
            c.template    = ewconfig.uriResolver.resolveToFile(getParam("template", "xsl/list.xsl"));
            c.startNodes  = getParam("startnodes", c.startNodes);
            c.nodePath    = getParam("nodepath", c.nodePath);
            c.fields      = getParam("fields", c.fields);
            c.age         = getParam("age", new Integer(c.age)).intValue();
            c.start       = getParam("start", new Integer(c.start)).intValue();
            c.constraints = getParam("constraints", c.constraints);
            c.directions  = getParam("directions", c.directions);
            c.orderBy     = getParam("orderby", c.orderBy);
            c.distinct    = getParam("distinct", new Boolean(true)).booleanValue();
        }

        // which parameter to use to configure a wizard page
        public void config(Config.WizardConfig c) {
            c.objectNumber = getParam("objectnumber");
        }
    }

Config ewconfig = null;    // Stores the current configuration for the wizard as whole, so all open lists and wizards are stored in this struct.
Configurator configurator; // Fills the ewconfig if necessary.

%><% boolean done=false; %><mm:log jspvar="log"><%  // Will log to category: org.mmbase.PAGE.LOGTAG.<context>.<path-to-editwizard>.jsp.<list|wizard>.jsp

log.trace("start of settings.jsp");
// Add some header to make sure these pages are not cached anywhere.
response.addHeader("Cache-Control","no-cache");
response.addHeader("Pragma","no-cache");

// Set session timeout
session.setMaxInactiveInterval(60 * 60 * 24); // 24 hours;

// and make every page expired ASAP.
String now = org.mmbase.util.RFC1123.makeDate(new Date());
response.addHeader("Expires",       now);
response.addHeader("Last-modified", now);

//response.addHeader("Date",          now); // Jetty doesn't like if you set this.
log.trace("done setting headers");

// It is possible to specify an alternatvie 'sessionkey'
// The sessionkey is used as a key for the session.
String sessionKey = request.getParameter("sessionkey");
if (sessionKey == null) sessionKey = "editwizard";

// proceed with the current wizard only if explicitly stated,
// or if this page is a debug page

boolean proceed = "true".equals(request.getParameter("proceed")) ||
                  (request.getRequestURI().endsWith("debug.jsp"));

// Look if there is already a configuration in the session.
Object configObject = session.getAttribute(sessionKey);
if (configObject == null || ! (configObject instanceof Config) || ! (proceed)) { // nothing (ok) in the session
    log.debug("creating new configuration (in session is " + configObject + ")");
    ewconfig = new Config();
    session.setAttribute(sessionKey, ewconfig);  // put it in the session
} else {
    log.debug("using configuration from session");
    ewconfig = (Config) configObject;
}
String refer = ewconfig.backPage;
log.trace("backpage in config is " + refer);

if (request.getParameter("logout") != null) {
    %><mm:cloud method="logout" /><%
    // what to do if 'logout' is requested?
    // return to the deeped backpage and clear the session.
    log.debug("logout parameter given, clearing session");
    session.removeAttribute(sessionKey);
    log.debug("Redirecting to " + refer);
    if (! refer.startsWith("http:")) {
        refer = response.encodeURL(request.getContextPath() + refer);
    }
    response.sendRedirect(refer);
    return;
}
ewconfig.sessionKey = sessionKey;
configurator = new Configurator(request, response, ewconfig);

// removing top page from the session
if (request.getParameter("remove") != null) {

    log.debug("Removing top object requested from " + configurator.getBackPage());

    if(ewconfig.subObjects.size() > 0) {
        // remove popupwizard
        // pass the result of the popupwizard to the calling wizard
        // (how do we do this ???)
        ewconfig.subObjects.pop();
    }
    if (ewconfig.subObjects.size() == 0) {
        if (sessionKey.indexOf("|popup")>0) {
        // a separate running popup, so remove sessiondata
            session.removeAttribute(sessionKey);
%>
<html><script language="javascript">window.close();</script></html>
<%
        } else {
            if (! refer.startsWith("http:")) {
                refer = response.encodeURL(request.getContextPath() + refer);
            }
            log.debug("Redirecting to " + refer);
            response.sendRedirect(refer);
        }
        done=true;
    } else if (ewconfig.subObjects.peek() instanceof Config.ListConfig) {
        log.debug("Redirecting to list");
        response.sendRedirect(response.encodeURL("list.jsp?proceed=true&sessionkey="+sessionKey));
        done=true;
    }

}
if (!done) {
    log.debug("Stack "            + ewconfig.subObjects);
    log.debug("URIResolver "      + ewconfig.uriResolver.getPrefixPath());

    log.service("end of settings.jsp");// meaning that the rest of the list/wizard page will be done (those include setting.jsp).
}
%></mm:log><%
    if (done) return;
%>

