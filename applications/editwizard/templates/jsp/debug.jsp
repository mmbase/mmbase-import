<%@ page errorPage="exception.jsp" %>
<%@ page import="org.mmbase.applications.editwizard.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.io.Writer" %>
<%@ page import="org.w3c.dom.Document" %>
<%@ page import="org.w3c.dom.Node" %>

<%@ include file="settings.jsp" %>

<%

    Document doc = Utils.parseXML("<debugdata/>");	
   if (ewconfig.subObjects.size() > 0 && ewconfig.subObjects.peek() instanceof Config.WizardConfig) {
      Config.WizardConfig  wizardConfig = (Config.WizardConfig) ewconfig.subObjects.peek();
      add(doc, wizardConfig.wiz.getData(),    ewconfig.wizard);
      add(doc, wizardConfig.wiz.getSchema(),  ewconfig.wizard);
      add(doc, wizardConfig. wiz.getPreform(), ewconfig.wizard);
   }	
   Utils.transformNode(doc, "xsl/debug.xsl", ewconfig.uriResolver, out,  null);
%>
<%!
	public void add(Document dest, Document src, String name) {
	
		Node n = dest.importNode(src.getDocumentElement().cloneNode(true), true);
		Utils.setAttribute(n, "debugname", name);
		dest.getDocumentElement().appendChild(n);
	}
	
%>

