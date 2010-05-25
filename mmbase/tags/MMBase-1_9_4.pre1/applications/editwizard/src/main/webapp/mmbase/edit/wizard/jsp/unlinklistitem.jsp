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
     * @version  $Id$
     * @author   Michiel Meeuwissen
     */

    String wizard = "";
    Config.SubConfig con = (Config.SubConfig) ewconfig.subObjects.peek();
    wizard = con.wizard;

    Wizard wiz = new Wizard(request, ewconfig.uriResolver, wizard, null, cloud);
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
        // No unlink action defined in the wizard schema. We cannot unlink.
        out.println("No unlink action is defined in the wizard schema: '"+ wizard + "'. <br />You should place &lt;action type=\"unlink\" /> in your schema so that unlink actions will be allowed.");
    }
%>
</mm:log>
</mm:cloud>
</mm:content>
