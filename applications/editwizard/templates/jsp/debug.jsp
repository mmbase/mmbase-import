<%@ page errorPage="exception.jsp" %>
<%@ page import="org.mmbase.applications.editwizard.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.io.Writer" %>
<%@ page import="org.w3c.dom.Document" %>
<%@ page import="org.w3c.dom.Node" %>

<%@ include file="settings.jsp" %>

<%

	Enumeration names = session.getAttributeNames();
	
	Document doc = Utils.parseXML("<debugdata/>");
	
	String name=null;
	while (names.hasMoreElements()) {
		name = (String)names.nextElement();
		if (name.indexOf("Wizard_")==0) {
			// a wizard instance!
			Wizard wiz = (Wizard) session.getValue(name);
			if (wiz!=null) {
				add(doc, wiz.data, name.substring(7));
				add(doc, wiz.schema, name.substring(7));
				add(doc, wiz.preform, name.substring(7));
			}
		}
	}
	
	Utils.transformNode(doc, settings_basedir + "/xsl/debug.xsl", out);
%>

<%!
	public void add(Document dest, Document src, String name) {
	
		Node n = dest.importNode(src.getDocumentElement().cloneNode(true), true);
		Utils.setAttribute(n, "debugname", name);
		dest.getDocumentElement().appendChild(n);
	}
	
%>

