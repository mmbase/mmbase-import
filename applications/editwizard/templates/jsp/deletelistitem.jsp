<%@ include file="settings.jsp"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><mm:cloud name="mmbase" method="http" jspvar="cloud"
><%@ page import="org.mmbase.bridge.*"
%><%@ page import="org.w3c.dom.Node"
%><%@ page import="org.mmbase.applications.editwizard.*"
%><%
    /**
     * deletelistitem.jsp
     *
     * @since    MMBase-1.6
     * @version  $Id: deletelistitem.jsp,v 1.5 2002-05-28 14:15:14 pierre Exp $
     * @author   Pierre van Rooden
     * @author   Michiel Meeuwissen
     */
    Wizard wiz = new Wizard(request.getContextPath(), ewconfig.uriResolver, ewconfig.wizard, null, cloud);
    Node deleteaction = Utils.selectSingleNode(wiz.getSchema(), "/*/action[@type='delete']");
    if (deleteaction != null) {
        // Ok. let's delete this object.
        org.mmbase.bridge.Node obj = cloud.getNode(request.getParameter("objectnumber"));
        obj.delete(true);
        response.sendRedirect(response.encodeURL("list.jsp?proceed=true"));
    } else {
        // No delete action defined in the wizard schema. We cannot delete.
        out.println("No delete action is defined in the wizard schema: '"+ ewconfig.wizard + "'. <br />You should place &lt;action type=\"delete\" /> in your schema so that delete actions will be allowed.");

    }
%>
</mm:cloud>