<%@page language="java" contentType="text/html;charset=UTF-8"
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><%@page import="java.io.*,java.util.*, org.mmbase.bridge.Cloud, org.mmbase.util.logging.Logger"
%><%@page import="org.mmbase.util.xml.URIResolver,org.mmbase.applications.editwizard.*"
%><%@page import="org.mmbase.applications.editwizard.Config"
%><%
/**
 * settings.jsp
 *
 * @since    MMBase-1.6
 * @version  $Id$
 * @author   Kars Veling
 * @author   Pierre van Rooden
 * @author   Michiel Meeuwissen
 * @author   Vincent van der Locht
 */

Config ewconfig = null;    // Stores the current configuration for the wizard as whole, so all open lists and wizards are stored in this struct.
Config.Configurator configurator = null; // Fills the ewconfig if necessary.

String popupId = "";  // default means: 'this is not a popup'
boolean popup = false;
String sessionKey = "editwizard";

 boolean done=false;
     Object closedObject=null;
%><mm:log jspvar="log"><%  // Will log to category: org.mmbase.PAGE.LOGTAG.<context>.<path-to-editwizard>.jsp.<list|wizard>.jsp

log.trace("start of settings.jsp");
// Set session timeout
session.setMaxInactiveInterval(1 * 60 * 60); // 1 hour;

// It is possible to specify an alternatvie 'sessionkey'
// The sessionkey is used as a key for the session.
sessionKey = request.getParameter("sessionkey");
request.setAttribute(Wizard.RESPONSE_KEY, response);
if (sessionKey == null) sessionKey = "editwizard";


// proceed with the current wizard only if explicitly stated,
// or if this page is a debug page

boolean proceed = "true".equals(request.getParameter("proceed")) || (request.getRequestURI().endsWith("debug.jsp"));

// Look if there is already a configuration in the session.
Object configObject = session.getAttribute(sessionKey);

if (proceed && configObject == null) {
    throw new WizardException("Your data cannot be found anymore, you waited too long (more than an hour), or the server was restarted");
}

if (configObject == null || ! (configObject instanceof Config) || ! (proceed)) { // nothing (ok) in the session
    if (log.isDebugEnabled()) {
        if (proceed) {
            log.debug("creating new configuration (in session is " + configObject + ")");
        } else {
            log.debug("creating new configuration (missing proceed parameter)");
        }
    }
    ewconfig = new Config();
    session.setAttribute(sessionKey, ewconfig);  // put it in the session (if not a search window)

} else {
    log.debug("using configuration from session");
    ewconfig = (Config) configObject;
}


popupId = request.getParameter("popupid");
if (popupId == null) popupId = "";
popup = ! "".equals(popupId);
if (popup) {
    log.debug("this is a popup");
    String replace = request.getParameter("replace");
    if ("true".equals(replace)) { // searchlists (and other?)  popups must be replaced, otherwise they 'inherit' properites of othere searclists on the page...
        if (! ewconfig.subObjects.empty()) {
            Config.SubConfig topObj = (Config.SubConfig) ewconfig.subObjects.peek();
            topObj.popups.remove(popupId);
            if (log.isDebugEnabled()) log.debug("Removing the '" + popupId + "' popup");
        }
     }
} else {
    log.debug("this is not a popup");
}

%><mm:import externid="loginsessionname" from="parameters" ></mm:import><%

String refer = ewconfig.backPage;
if (log.isDebugEnabled()) log.trace("backpage in root-config is " + refer);

if (request.getParameter("logout") != null) {
    // what to do if 'logout' is requested?
    // return to the deeped backpage and clear the session.
    log.debug("logout parameter given, clearing session");
    session.removeAttribute(sessionKey);
    log.debug("Redirecting to " + refer);
    %>
   <mm:redirect referids="loginsessionname" page="logout.jsp"><mm:param name="refer" value="<%=refer%>" /></mm:redirect>
  <%
    return;
}
ewconfig.sessionKey = sessionKey;
configurator = new Config.Configurator(pageContext, ewconfig);

// removing top page from the session
if (request.getParameter("remove") != null) {

    if (log.isDebugEnabled()) log.debug("Removing top object requested from " + configurator.getBackPage());
    if(! ewconfig.subObjects.empty()) {
        if (! popup) { // remove inline
            log.debug("popping one of subObjects " + ewconfig.subObjects);
            closedObject = ewconfig.subObjects.pop();
        } else { //popup
            log.debug("a separate running popup, so remove sessiondata for " + popupId);
            Config.SubConfig top = (Config.SubConfig) ewconfig.subObjects.peek();
            Stack stack =  (Stack) top.popups.get(popupId);
            closedObject = stack.pop();
            if (stack.size() == 0) {
                top.popups.remove(popupId);
                log.debug("going to close this window");
%>
<html>
<script language="javascript">
 try { // Mac IE doesn't always support window.opener.
<%
 if (closedObject instanceof Config.WizardConfig && ((Config.WizardConfig) closedObject).wiz.committed()) {
   // XXXX I find all this stuff in wizard.jsp too. Why??


   log.debug("A popup was closed (commited)");
   String sendCmd = "";
   String objnr = "";
   Config.WizardConfig popupWiz= (Config.WizardConfig) closedObject;
   // we move from a popup sub-wizard to a parent wizard...
   // with an inline popupwizard we should like to pass the newly created or updated
   // item to the 'lower' wizard.
   objnr=popupWiz.objectNumber;
   if ("new".equals(objnr)) {
     // obtain new object number
     objnr=popupWiz.wiz.getObjectNumber();
     if (log.isDebugEnabled()) log.debug("Objectnumber was 'new', now " + objnr);
     String parentFid = popupWiz.parentFid;
     if ((parentFid!=null) && (!parentFid.equals(""))) {
       log.debug("Settings. Sending an add-item command ");
       String parentDid = popupWiz.parentDid;
       sendCmd="cmd/add-item/"+parentFid+"/"+parentDid+"//";
     }
   } else {
     if (log.isDebugEnabled()) log.debug("Aha, this was existing, send an 'update-item' cmd for object " + objnr);
     sendCmd="cmd/update-item////";
   }
   if (log.isDebugEnabled()) log.debug("Sending command " + sendCmd + " , " + objnr);
   %>
   window.opener.saveScroll();
   window.opener.doSendCommand("<%=sendCmd%>","<%=objnr%>");
<%          } %>
 } catch (e) {}
 window.close();
</script>
</html>
<%
            done = true;
            }
        } // popup
    } // not subObject empty

    if (ewconfig.subObjects.empty()) { // it _is_ empty? Then we are ready.
        log.debug("last object cleared, redirecting");
        if (! refer.startsWith("http:") && ! refer.startsWith("https:")) {
            refer = response.encodeRedirectURL(request.getContextPath() + refer);
        }
        log.debug("Redirecting to " + refer);
        response.sendRedirect(refer);
        done = true;
    } else if (ewconfig.subObjects.peek() instanceof Config.ListConfig) {
        log.debug("Redirecting to list");
        response.sendRedirect(response.encodeRedirectURL("list.jsp?proceed=true&sessionkey="+sessionKey));
        done = true;
    }
}


if (!done) {
    if (log.isDebugEnabled()) {
        log.debug("Stack "            + ewconfig.subObjects);
        if (! ewconfig.subObjects.empty()) {
            Config.SubConfig topObj = (Config.SubConfig) ewconfig.subObjects.peek();
            log.debug("Popups "           + topObj.popups  );
        }
        log.debug("URIResolver "      + ewconfig.uriResolver.getPrefixPath());
    }
    log.service("end of settings.jsp");// meaning that the rest of the list/wizard page will be done (those include setting.jsp).
}
%></mm:log><%
    if (done) return;
%><mm:import externid="loginmethod" from="parameters"></mm:import>

