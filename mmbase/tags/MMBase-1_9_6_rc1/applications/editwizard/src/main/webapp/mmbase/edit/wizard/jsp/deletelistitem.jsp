.<%@ include file="settings.jsp"
%><mm:content type="text/html" expires="0" language="<%=ewconfig.language%>">
<mm:cloud name="mmbase" rank="basic user" jspvar="cloud"
><%@ page import="org.mmbase.bridge.*"
%><%@ page import="org.w3c.dom.Node"
%><%@ page import="org.mmbase.applications.editwizard.*"
%><%@ page import="org.mmbase.applications.editwizard.Config"
%><%
    /**
     * deletelistitem.jsp
     *
     * @since    MMBase-1.6
     * @version  $Id$
     * @author   Pierre van Rooden
     * @author   Michiel Meeuwissen
     */

    String wizard="";
    Object con=ewconfig.subObjects.peek();
    if (con instanceof Config.SubConfig) {
        wizard=((Config.SubConfig)con).wizard;
    }

    Wizard wiz = new Wizard(request, ewconfig.uriResolver, wizard, null, cloud);
    Node deleteaction = Utils.selectSingleNode(wiz.getSchema(), "/*/action[@type='delete']");
    if (deleteaction != null) {
        // Ok. let's delete this object.
        org.mmbase.bridge.Node obj = cloud.getNode(request.getParameter("objectnumber"));
        obj.delete(true);
        response.sendRedirect(response.encodeRedirectURL("list.jsp?proceed=true&sessionkey=" + sessionKey));
    } else {
        // No delete action defined in the wizard schema. We cannot delete.
        out.println("No delete action is defined in the wizard schema: '"+ wizard + "'. <br />You should place &lt;action type=\"delete\" /> in your schema so that delete actions will be allowed.");

    }
%>
</mm:cloud>
</mm:content>
