<%@page contentType="text/html; charset=utf-8"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %><%@page import="java.io.*"  
%><%@page import="java.util.*, org.mmbase.bridge.Cloud, org.mmbase.util.logging.Logger"
%><%@page import="org.mmbase.util.xml.URIResolver"
%><%@ page import="org.mmbase.applications.editwizard.*"
%><%@ page import="org.mmbase.applications.editwizard.SecurityException,org.mmbase.applications.editwizard.Config"
%><%!
    /**
     * settings.jsp
     *
     * @since    MMBase-1.6
     * @version  $Id: settings.jsp,v 1.3 2002-05-03 14:44:35 michiel Exp $
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

        public void config(Config.ListConfig c) {
            c.template = ewconfig.uriResolver.resolveToFile(getParam("template", "xsl/list.xsl"));
            c.nodePath = getParam("nodepath", c.nodePath);
            c.fields   = getParam("fields", c.fields);
            c.age      = getParam("age", new Integer(c.age)).intValue();
            c.start    = getParam("start", new Integer(c.start)).intValue();
            c.constraints = getParam("constraints", c.constraints);
            c.directions  = getParam("directions", c.directions);
            c.orderBy     = getParam("orderby", c.orderBy);
            c.distinct    = getParam("distinct", new Boolean(true)).booleanValue();
        }
        public void config(Config.WizardConfig c) {
            c.objectNumber = getParam("objectnumber");        
        }
    }
        

Config ewconfig = null;
Configurator configurator;

%><mm:log jspvar="log"><%

log.trace("start of settings.jsp");
// Add some header to make sure these pages are not cached anywhere.
response.addHeader("Cache-Control","no-cache");
response.addHeader("Pragma","no-cache");

// set session timeout
session.setMaxInactiveInterval(60 * 60 * 24); // 24 hours;
String now = org.mmbase.util.RFC1123.makeDate(new Date());
response.addHeader("Expires",       now);
//response.addHeader("Date",          now);
response.addHeader("Last-modified", now);

log.trace("done setting headers");
String instanceName = request.getParameter("instanceName");
if (instanceName == null) instanceName = "editwizard";

Object configObject = session.getAttribute(instanceName);

if (configObject == null || ! (configObject instanceof Config)) {
    log.debug("creating new configuration (in session is " + configObject + ")");
    ewconfig = new Config();
    session.setAttribute(instanceName, ewconfig);    
} else {
    log.debug("using configuration from session");
    ewconfig = (Config) configObject;
}
String refer = ewconfig.backPage;
log.trace("backpage in config is " + refer);

if (request.getParameter("logout") != null) {
    log.debug("logout parameter given, clearing session");
    session.removeAttribute(instanceName);
    ewconfig = new Config();
    //config.backPage = refer;
    session.setAttribute(instanceName, ewconfig);
} 

ewconfig.sessionKey = instanceName;
configurator = new Configurator(request, response, ewconfig);

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
log.service("Stack " + ewconfig.subObjects);
log.service("URIResolver " + ewconfig.uriResolver.getPrefixPath());

log.service("end of settings.jsp");
%></mm:log>
