<%@ include file="settings.jsp"
%><mm:cloud name="mmbase" method="http" jspvar="cloud"><%@ page errorPage="exception.jsp"
%><mm:log jspvar="log"><%
    /**
     * wizard.jsp
     *
     * @since    MMBase-1.6
     * @version  $Id: wizard.jsp,v 1.3 2002-05-07 13:37:57 michiel Exp $
     * @author   Kars Veling
     * @author   Michiel Meeuwissen
     */

Config.WizardConfig wizardConfig = null;
if (ewconfig.subObjects.size() > 0) {
    if (ewconfig.subObjects.peek() instanceof Config.WizardConfig) {
        log.debug("checking configuration");
        wizardConfig = (Config.WizardConfig) ewconfig.subObjects.peek();
        Config.WizardConfig checkConfig = new Config.WizardConfig();
        log.trace("checkConfig" + configurator);
        configurator.config(checkConfig);
        if (checkConfig.objectNumber != null && (!checkConfig.objectNumber.equals(wizardConfig.objectNumber))) {
            log.debug("found wizard is for other other object (" + checkConfig.objectNumber + "!= " + wizardConfig.objectNumber + ")");
            wizardConfig = null;
        } else {
            log.debug("processing request");
            wizardConfig.wiz.processRequest(request);
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
    response.sendRedirect(response.encodeURL("wizard.jsp?proceed=yes&remove=true"));
} else {
    log.trace("Send html back");
    wizardConfig.wiz.writeHtmlForm(out, ewconfig.wizard);
}    
%>
</mm:log></mm:cloud>

