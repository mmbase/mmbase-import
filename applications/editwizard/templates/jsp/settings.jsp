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
     * @version  $Id: settings.jsp,v 1.5 2002-05-15 09:51:37 pierre Exp $
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
            c.template    = ewconfig.uriResolver.resolveToFile(getParam("template", "xsl/list.xsl"));
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

%><mm:log jspvar="log"><%  // Will log to category: org.mmbase.PAGE.LOGTAG.<context>.<path-to-editwizard>.jsp.<list|wizard>.jsp

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

// It is possible to specify an alternatvie 'instanceName'
// The instanceName is used as a key for the session.
String instanceName = request.getParameter("instanceName");
if (instanceName == null) instanceName = "editwizard";

// proceed with the current wizard only if explicitly stated,
// if this page is a popup, or if this page is a debug page

boolean proceed = "yes".equals(request.getParameter("proceed")) ||
                  (request.getParameter("popup") != null) ||
                  (request.getRequestURI().endsWith("debug.jsp"));

// Look if there is already a configuration in the session.
Object configObject = session.getAttribute(instanceName);
if (configObject == null || ! (configObject instanceof Config) || ! (proceed)) { // nothing (ok) in the session
    log.debug("creating new configuration (in session is " + configObject + ")");
    ewconfig = new Config();
    session.setAttribute(instanceName, ewconfig);  // put it in the session
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
    session.removeAttribute(instanceName);
    log.debug("Redirecting to " + refer);
    if (! refer.startsWith("http:")) {
        refer = response.encodeURL(request.getContextPath() + refer);
    }
    response.sendRedirect(refer);
    return;
}

ewconfig.sessionKey = instanceName;
configurator = new Configurator(request, response, ewconfig);

// removing top page from the session
if (request.getParameter("remove") != null) {
    log.debug("Removing top object requested from " + request.getHeader("Referer"));
    if(ewconfig.subObjects.size() > 0) ewconfig.subObjects.pop();
    String redir;
    if (configurator.getBackPage().startsWith("http:")) {
        redir = configurator.getBackPage();
    } else {
        redir = response.encodeURL(request.getContextPath() + configurator.getBackPage());
    }
    if ("true".equals(request.getParameter("popup"))) {
        log.trace("This was a popup-wizard. Close it.");
        out.write("<html><script language=\"javascript\">window.close();</script></html>");
    }
    log.debug("Redirecting to " + redir);
    response.sendRedirect(redir);
    return;
}

log.service("Doing for wizard " + ewconfig.wizard);
log.debug("Stack "            + ewconfig.subObjects);
log.debug("URIResolver "      + ewconfig.uriResolver.getPrefixPath());

log.service("end of settings.jsp");// meaning that the rest of the list/wizard page will be done (those include setting.jsp).

%></mm:log>
