<%@ include file="settings.jsp"
%><mm:cloud name="mmbase" method="http" jspvar="cloud"><%@ page errorPage="exception.jsp"
%><mm:log jspvar="log"><%@ page import="org.mmbase.applications.editwizard.*, java.util.*, java.io.*"%><%
    /**
     * wizard.jsp
     *
     * @since    MMBase-1.6
     * @version  $Id: wizard.jsp,v 1.1 2002-04-19 19:52:08 michiel Exp $
     * @author   Kars Veling
     * @author   Michiel Meeuwissen
     */

    Config.WizardConfig wizardConfig = null;
if (config.subObjects.size() > 0) {
    if (config.subObjects.peek() instanceof Config.WizardConfig) {
        log.debug("checking configuration");
        wizardConfig = (Config.WizardConfig) config.subObjects.peek();
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
    config.subObjects.push(wizardConfig);
}

if (wizardConfig.wiz.mayBeClosed()) {
    log.trace("Closing this wizard");
    response.sendRedirect(response.encodeURL("wizard.jsp?remove=true"));
} else {
    log.trace("Send html back");
    wizardConfig.wiz.writeHtmlForm(out, config.wizard);
}    
%>
</mm:log></mm:cloud>

