<%@ include file="settings.jsp"
%><mm:cloud name="mmbase" method="http" jspvar="cloud"><%@ page errorPage="exception.jsp"
%><mm:log jspvar="log"><%@ page import="org.mmbase.applications.editwizard.*, java.util.*, java.io.*"%><%
    /**
     * wizard.jsp
     *
     * @since    MMBase-1.6
     * @version  $Id: wizard.jsp,v 1.2 2002-04-22 14:37:34 michiel Exp $
     * @author   Kars Veling
     * @author   Michiel Meeuwissen
     */

    Config.WizardConfig wizardConfig = null;
if (ewconfig.subObjects.size() > 0) {
    if (ewconfig.subObjects.peek() instanceof Config.WizardConfig) {
        log.debug("checking configuration");
        wizardConfig = (Config.WizardConfig) ewconfig.subObjects.peek();
        Config.WizardConfig checkConfig = new Config.WizardConfig();
        configurator.config(checkConfig);
        if (wizardConfig.objectNumber != null && wizardConfig.objectNumber.equals(checkConfig.objectNumber)) {
            log.debug("processing request");
            wizardConfig.wiz.processRequest(request);
        } else {
            log.debug("found wizard is for other other object");
            wizardConfig = null;
        }
    }
} 

if (wizardConfig == null) {
    log.trace("creating new wizard");
    wizardConfig =  configurator.createWizard(cloud);
    log.trace("putting new wizard on the stack for object " + wizardConfig.objectNumber);
    ewconfig.subObjects.push(wizardConfig);
}

if (wizardConfig.wiz.mayBeClosed()) {
    log.trace("Closing this wizard");
    response.sendRedirect(response.encodeURL("wizard.jsp?remove=true"));
} else {
    log.trace("Send html back");
    wizardConfig.wiz.writeHtmlForm(out, ewconfig.wizard);
}    
%>
</mm:log></mm:cloud>

