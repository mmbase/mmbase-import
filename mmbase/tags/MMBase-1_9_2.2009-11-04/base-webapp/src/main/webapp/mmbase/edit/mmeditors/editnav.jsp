<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@include file="header.jsp" %><html xmlns="http://www.w3.org/1999/xhtml">
<mm:cloud name="mmbase" method="http" jspvar="cloud" rank="$rank">
  <head>
    <title>Editors</title>
    <link rel="stylesheet" href="css/mmeditors.css" type="text/css" />
<% if ("R".equals(session.getValue("mmeditors_reload"))) { %>
  <script type="text/javascript" language="javascript">
<!-- 
   window.setTimeout('document.location="editnav.jsp"',125000);
// -->
  </script>
<% } %>
  </head>
  <body class="header">
    <table>
      <tr>
        <td>
            <a href="<mm:url page="index.jsp" />" target="_top"><img src="gfx/arrow.gif" /></a>&nbsp;&nbsp;editors</font>
            <%  Stack states = (Stack)session.getValue("mmeditors_states");
                if (states != null) {
                  for (Iterator i = states.iterator(); i.hasNext(); ) {
                    Properties state = (Properties)i.next();
                    String managerName = (String)state.get("manager");
                    NodeManager manager = cloud.getNodeManager(managerName);
            %>&nbsp;<img alt="" src="gfx/arrow.gif" />&nbsp;<%=manager.getGUIName()%>
            <%
                    String role = (String)state.get("role");
                    if (role != null) {
                        RelationManager relman = cloud.getRelationManager(role);
            %> (<%=relman.getForwardGUIName()%>)
            <%
                    }
                  }
                }
            %>
        </td>
        <td class="status">
          (<%=cloud.getUser().getIdentifier() %>)
        </td>
      </tr>
    </table>
  </body>
</mm:cloud>
</html>

