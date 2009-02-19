<%@ include file="settings.jsp"
%><mm:content type="text/html" expires="0" language="<%=ewconfig.language%>">
<mm:cloud name="mmbase" jspvar="cloud" method="asis"
><%@ page import="org.mmbase.bridge.*,org.mmbase.bridge.util.*"
%><%@ page import="org.w3c.dom.Node"
%><%@ page import="org.mmbase.applications.editwizard.*"
%><%@ page import="org.mmbase.applications.editwizard.Config"
%>
<mm:import externid="objectnumber" vartype="string" jspvar="objectnumber" required="true" />
<mm:log jspvar="log">
<%
    /**
     * @since    MMBase-1.8.4
     * @version  $Id: unlinklistitem.jsp,v 1.2.2.5 2008-02-21 16:02:26 andre Exp $
     * @author   Michiel Meeuwissen
     */

    String wizard = "";
    Config.SubConfig con = (Config.SubConfig) ewconfig.subObjects.peek();
    wizard = con.wizard;

    Wizard wiz = new Wizard(request.getContextPath(), ewconfig.uriResolver, wizard, null, cloud);
    Node unlinkaction = Utils.selectSingleNode(wiz.getSchema(), "/*/action[@type='unlink']");
    String relationOriginNode = (String) con.getAttributes().get("relationOriginNode");
    if (relationOriginNode == null) relationOriginNode = (String) con.getAttributes().get("origin");
    String relationRole = (String) con.getAttributes().get("relationRole");
    String relationCreateDir = (String) con.getAttributes().get("relationCreateDir");
    if (unlinkaction != null) {
    // Ok. let's unlink this object.
        org.mmbase.bridge.Node n      = cloud.getNode(objectnumber);
        org.mmbase.bridge.Node origin = cloud.getNode(relationOriginNode);
        log.debug("objectnumber " + n.getNumber() + " " + origin.getNumber() + " " + relationOriginNode);
        
        RelationList l = SearchUtil.findRelations(n, origin, relationRole, relationCreateDir);
        log.debug("" + l);
        RelationIterator i = l.relationIterator();
        if (i.hasNext()) {
          Relation r = i.nextRelation();
          //log.info("deleting " + r);
          r.delete();
        }
        
        response.sendRedirect(response.encodeRedirectURL("list.jsp?proceed=true&sessionkey=" + sessionKey));
    } else {
        // No delete action defined in the wizard schema. We cannot delete.
        out.println("No delete action is defined in the wizard schema: '"+ wizard + "'. <br />You should place &lt;action type=\"delete\" /> in your schema so that delete actions will be allowed.");

    }
%>
</mm:log>
</mm:cloud>
</mm:content>
