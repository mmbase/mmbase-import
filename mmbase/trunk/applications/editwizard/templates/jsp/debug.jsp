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
     * @version  $Id: debug.jsp,v 1.6 2002-08-19 16:18:58 michiel Exp $
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
    Map map = new HashMap();  
    map.put("session_byte_size", "" + org.mmbase.util.SizeOf.getByteSize(ewconfig));
    Utils.transformNode(doc, template, ewconfig.uriResolver, out,  map);
%>
<%!
    public void add(Document dest, Document src, String name) {

        Node n = dest.importNode(src.getDocumentElement().cloneNode(true), true);
        Utils.setAttribute(n, "debugname", name);
        dest.getDocumentElement().appendChild(n);
    }

%>

