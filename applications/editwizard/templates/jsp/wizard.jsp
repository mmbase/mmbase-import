<%@ include file="settings.jsp"
%><mm:cloud name="mmbase" method="http" jspvar="cloud"><%@ page errorPage="exception.jsp"
%><mm:log jspvar="log"><%
    /**
     * wizard.jsp
     *
     * @since    MMBase-1.6
     * @version  $Id: wizard.jsp,v 1.12 2002-07-23 14:50:06 pierre Exp $
     * @author   Kars Veling
     * @author   Michiel Meeuwissen
     * @author   Pierre van Rooden
     */

Config.WizardConfig wizardConfig = null;
if (ewconfig.subObjects.size() > 0) {
    if (ewconfig.subObjects.peek() instanceof Config.WizardConfig) {
        log.debug("checking configuration");
        wizardConfig = (Config.WizardConfig) ewconfig.subObjects.peek();
        Config.WizardConfig checkConfig = new Config.WizardConfig();
        log.trace("checkConfig" + configurator);
        configurator.config(checkConfig);
        if (checkConfig.objectNumber != null &&
            (checkConfig.objectNumber.equals("new") ||
             !checkConfig.objectNumber.equals(wizardConfig.objectNumber))) {
            log.debug("found wizard is for other other object (" + checkConfig.objectNumber + "!= " + wizardConfig.objectNumber + ")");
            wizardConfig = null;
        } else {
            if ((closedObject instanceof Config.WizardConfig) &&
                ((Config.WizardConfig)closedObject).wiz.committed()) {
                // we move from a inline sub-wizard to a parent wizard...
                Config.WizardConfig inlineWiz=(Config.WizardConfig)closedObject;
                // with an inline popupwizard we should like to pass the newly created or updated
                // item to the 'lower' wizard.
                String objnr=inlineWiz.objectNumber;
                if ("new".equals(objnr)) {
                    // obtain new object number
                    objnr=inlineWiz.wiz.getObjectNumber();
                    String parentFid = inlineWiz.parentFid;
                    if ((parentFid!=null) && (!parentFid.equals(""))) {
                        String parentDid = inlineWiz.parentDid;
                        WizardCommand wc = new WizardCommand("cmd/add-item/"+parentFid+"/"+parentDid+"//", objnr);
                        wizardConfig.wiz.processCommand(wc);
                    }
                } else {
                    WizardCommand wc = new WizardCommand("cmd/update-item////",objnr);
                    wizardConfig.wiz.processCommand(wc);
                }
            }
            log.debug("processing request");
            wizardConfig.wiz.processRequest(request);
        }
    }
}

if (wizardConfig == null) {
    log.trace("creating new wizard");
    wizardConfig =  configurator.createWizard(cloud);
    wizardConfig.parentFid=request.getParameter("fid");
    wizardConfig.parentDid=request.getParameter("did");
    log.trace("putting new wizard on the stack for object " + wizardConfig.objectNumber);
    ewconfig.subObjects.push(wizardConfig);
}

if (wizardConfig.wiz.startWizard()) {
    WizardCommand cmd=wizardConfig.wiz.getStartWizardCommand();
    String parentFid = cmd.getFid();
    if (parentFid==null) parentFid="";
    String parentDid = cmd.getDid();
    if (parentDid==null) parentDid="";
    String objectnumber = cmd.getParameter(2);
    String origin = cmd.getParameter(3);
    String wizardname = cmd.getValue();
    response.sendRedirect(
              response.encodeURL("wizard.jsp?fid="+parentFid+
                                 "&did="+parentDid+
                                 "&proceed=true&wizard="+wizardname+
                                 "&sessionkey="+ewconfig.sessionKey+
                                 "&objectnumber="+objectnumber+
                                 "&origin="+origin));
} else if (wizardConfig.wiz.mayBeClosed()) {
    log.trace("Closing this wizard");
    response.sendRedirect(response.encodeURL("wizard.jsp?sessionkey="+ewconfig.sessionKey+"&proceed=true&remove=true"));
} else {
    log.trace("Send html back");
    wizardConfig.wiz.writeHtmlForm(out, wizardConfig.wizard);
}
%>
</mm:log></mm:cloud>

