<%@ page errorPage="exception.jsp" %><%@ include file="settings.jsp" %>
<%@ page import="org.mmbase.applications.editwizard.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.io.Writer" %>
<%@ page import="org.w3c.dom.Document" %>
<%@ page import="org.w3c.dom.Node" %>
<%
    /**
     * debug.jsp
     *
     * @since    MMBase-1.6
     * @version  $Id: debug.jsp,v 1.5 2002-07-09 14:12:53 pierre Exp $
     * @author   Kars Veling
     * @author   Michiel Meeuwissen
     */
    String wizard="";
    Object con=ewconfig.subObjects.peek();
    if (con instanceof Config.SubConfig) {
        wizard=((Config.SubConfig)con).wizard;
    }
    Document doc = Utils.parseXML("<debugdata/>");
    if (ewconfig.subObjects.size() > 0 && ewconfig.subObjects.peek() instanceof Config.WizardConfig) {
        Config.WizardConfig  wizardConfig = (Config.WizardConfig) ewconfig.subObjects.peek();
        add(doc, wizardConfig.wiz.getData(),    wizard);
        add(doc, wizardConfig.wiz.getSchema(),  wizard);
        add(doc, wizardConfig. wiz.getPreform(),wizard);
    }
    File template = ewconfig.uriResolver.resolveToFile("xsl/debug.xsl");
    Utils.transformNode(doc, template, ewconfig.uriResolver, out,  null);
%>
<%!
    public void add(Document dest, Document src, String name) {

        Node n = dest.importNode(src.getDocumentElement().cloneNode(true), true);
        Utils.setAttribute(n, "debugname", name);
        dest.getDocumentElement().appendChild(n);
    }

%>

