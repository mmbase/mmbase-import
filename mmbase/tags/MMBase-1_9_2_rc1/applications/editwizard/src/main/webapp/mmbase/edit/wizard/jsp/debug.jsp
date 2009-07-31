<%@ page errorPage="exception.jsp" %><%@ include file="settings.jsp"
%><%@ page import="org.mmbase.applications.editwizard.*"
%><%@ page import="org.mmbase.applications.editwizard.Config"
%><%@ page import="java.util.*"
%><%@ page import="java.net.URL"
%><%@ page import="java.io.Writer"
%><%@ page import="org.w3c.dom.Document"
%><%@ page import="org.w3c.dom.Node"
 %><mm:import externid="type">text/html</mm:import>
<mm:content type="${type}" expires="0" language="en">
<%
    /**
     * debug.jsp
     *
     * @since    MMBase-1.6
     * @version  $Id$
     * @author   Kars Veling
     * @author   Michiel Meeuwissen
     */
    String wizard="";

    Object con = null;
    if (!ewconfig.subObjects.empty()) {
      con = ewconfig.subObjects.peek();
      if (popup) {
        Stack stack = (Stack) ((Config.SubConfig)con).popups.get(popupId);
        if ((stack != null) && !stack.empty()) {
            con = stack.peek();
        }
      }
    }
    Document doc = null;
%><mm:compare referid="type" value="text/html">
<%

    doc = Utils.parseXML("<debugdata/>");
    if (con instanceof Config.WizardConfig) {
        wizard=((Config.WizardConfig)con).wizard;
        add(doc, ((Config.WizardConfig)con).wiz.getData(), wizard);
        add(doc, ((Config.WizardConfig)con).wiz.getSchema(), wizard);
        add(doc, ((Config.WizardConfig)con).wiz.getPreForm(), wizard);
    }
    URL template = ewconfig.uriResolver.resolveToURL("xsl/debug.xsl", null);
    Map map = new HashMap();
    map.put("session_byte_size", "" + org.mmbase.util.SizeOf.getByteSize(ewconfig));
    Utils.transformNode(doc, template, ewconfig.uriResolver, out,  map, (Cloud) null);
%>
</mm:compare>
<mm:compare referid="type" value="text/xml">
  <mm:import externid="xml" jspvar="xml" vartype="string">schema</mm:import><%
    if (xml.equals("schema")) doc = ((Config.WizardConfig)con).wiz.getSchema();
    if (xml.equals("data")) doc = ((Config.WizardConfig)con).wiz.getData();
    if (xml.equals("preform")) doc = ((Config.WizardConfig)con).wiz.getPreForm();
    URL template = ewconfig.uriResolver.resolveToURL("xslt/copy.xslt", null);
    Utils.transformNode(doc, template, ewconfig.uriResolver, out);
%></mm:compare><%!

    public void add(Document dest, Document src, String name) {

        Node n = dest.importNode(src.getDocumentElement().cloneNode(true), true);
        Utils.setAttribute(n, "debugname", name);
        dest.getDocumentElement().appendChild(n);
    }

%></mm:content>
