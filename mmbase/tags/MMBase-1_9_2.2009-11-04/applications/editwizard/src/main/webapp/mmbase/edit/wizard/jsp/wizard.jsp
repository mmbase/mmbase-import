<%@ include file="settings.jsp"
%><mm:content postprocessor="none" type="text/html" expires="0" language="<%=ewconfig.language%>"><mm:cloud method="$loginmethod" rank="basic user"  loginpage="login.jsp" sessionname="$loginsessionname" jspvar="cloud">
<mm:import externid="xmlmode" />
<mm:present referid="xmlmode">
  <mm:param name="org.mmbase.xml-mode" value="$xmlmode" />
</mm:present>
<mm:log jspvar="log"><%
    /**
     * wizard.jsp
     *
     * @since    MMBase-1.6
     * @version  $Id$
     * @author   Kars Veling
     * @author   Michiel Meeuwissen
     * @author   Pierre van Rooden
     */

Config.WizardConfig wizardConfig = null;
Config.SubConfig    top = null;

if (! ewconfig.subObjects.empty()) {
    top  = (Config.SubConfig) ewconfig.subObjects.peek();
    if (! popup) {
        log.debug("This is not a popup");
        if (top instanceof Config.WizardConfig) {
            log.debug("checking configuration");
            wizardConfig = (Config.WizardConfig) top;
        } else {
            log.debug("not a wizard on the stack?");
        }

    } else {
        log.debug("this is a popup");
        Stack stack = (Stack) top.popups.get(popupId);
        if (stack == null) {
            log.debug("no configuration found for popup wizard");
            stack = new Stack();
            top.popups.put(popupId, stack);
            wizardConfig = null;
        } else {
            if (stack.empty()) {
                log.error("Empty stack?");
            } else {
                wizardConfig = (Config.WizardConfig) stack.peek();
            }
        }
    }
} else {
    log.debug("nothing found on stack");
    if (popup) {
        throw new WizardException("Popup without parent");
    }
}

if (wizardConfig != null) {
    log.debug("checking configuration");
    Config.WizardConfig checkConfig = new Config.WizardConfig();
    if (log.isDebugEnabled()) log.trace("checkConfig" + configurator);
    configurator.config(checkConfig);
    if (checkConfig.objectNumber != null &&
        (checkConfig.objectNumber.equals("new") ||
         !checkConfig.objectNumber.equals(wizardConfig.objectNumber))) {
        log.debug("found wizard is for other other object (" + checkConfig.objectNumber + "!= " + wizardConfig.objectNumber + ")");
        wizardConfig = null;
    } else {
        if ((closedObject instanceof Config.WizardConfig) &&
            ((Config.WizardConfig)closedObject).wiz.committed()) {

            log.trace("we move from a inline sub-wizard to a parent wizard...");
            Config.WizardConfig inlineWiz=(Config.WizardConfig)closedObject;
            // with an inline popupwizard we should like to pass the newly created or updated
            // item to the 'lower' wizard.
            String objnr=inlineWiz.objectNumber;
            if ("new".equals(objnr)) {
                log.trace("obtain new object number");
                objnr=inlineWiz.wiz.getObjectNumber();
                String parentFid = inlineWiz.parentFid;
                if ((parentFid!=null) && (!parentFid.equals(""))) {
                    String parentDid = inlineWiz.parentDid;
                    WizardCommand wc = new WizardCommand("cmd/add-item/"+parentFid+"/"+parentDid+"//", objnr);
                    wizardConfig.wiz.processCommand(wc);
                }
            } else {
                log.trace("update-item for " + objnr);
                WizardCommand wc = new WizardCommand("cmd/update-item////", objnr);
                wizardConfig.wiz.processCommand(wc);
            }
        } else {
          log.trace("closed object not commited");
        }
        log.debug("processing request");
        wizardConfig.wiz.processRequest(request);
    }
}

if (wizardConfig == null) {
    log.trace("creating new wizard");
    wizardConfig =  configurator.createWizard(cloud);
    wizardConfig.parentFid = request.getParameter("fid");
    wizardConfig.parentDid = request.getParameter("did");
    wizardConfig.popupId   = popupId;
    if (! popup) {
        if (log.isDebugEnabled()) log.trace("putting new wizard on the stack for object " + wizardConfig.objectNumber);
        ewconfig.subObjects.push(wizardConfig);
    } else {
        if (log.isDebugEnabled()) log.trace("putting new wizard in popup map  for object " + wizardConfig.objectNumber);
        Stack stack = (Stack) top.popups.get(popupId);
        stack.push(wizardConfig);
    }
}

if (wizardConfig.wiz.startWizard()) {
    log.debug("Starting a wizard " );
    WizardCommand cmd=wizardConfig.wiz.getStartWizardCommand();
    String parentFid = cmd.getFid();
    if (parentFid==null) parentFid="";
    String parentDid = cmd.getDid();
    if (parentDid==null) parentDid="";
    String objectnumber = cmd.getParameter(2);
    String origin = cmd.getParameter(3);

    String wizardname = cmd.getValue();
    String redirectTo = response.encodeRedirectURL("wizard.jsp?fid=" + parentFid +
                                 "&did=" + parentDid +
                                 "&proceed=true&wizard=" + wizardname +
                                 "&sessionkey=" + ewconfig.sessionKey +
                                 "&origin=" + origin +
                                 "&objectnumber=" + objectnumber +
                                 "&popupid=" + popupId);
    log.debug("Redirecting to " + redirectTo);
    response.sendRedirect(redirectTo);
} else if (wizardConfig.wiz.mayBeClosed()) {
    log.trace("Closing this wizard");
    response.sendRedirect(response.encodeRedirectURL("wizard.jsp?sessionkey=" + ewconfig.sessionKey +
                                             "&proceed=true" +
                                             "&remove=true" +
                                             "&popupid=" + popupId ));
} else {
    log.trace("Send html back");
    wizardConfig.wiz.writeHtmlForm(out, wizardConfig.wizard);
}
%></mm:log></mm:cloud></mm:content>
