<%@include file="header.jsp" %>
  <mm:cloud name="mmbase" method="http" jspvar="cloud">
  <% Stack states = (Stack)session.getValue("mmeditors_states");
     Properties state = (Properties)states.peek();
     String transactionID = state.getProperty("transaction");
     String nodeID = state.getProperty("node");
     Transaction trans = cloud.getTransaction(transactionID);
     Node node = trans.getNode(nodeID); 
     boolean isRelation = node.isRelation();
     Module mmlanguage = cloud.getCloudContext().getModule("mmlanguage");
  %>
  <head>
    <title>Editors</title>
    <link rel="stylesheet" href="css/mmeditors.css" type="text/css" />
    <style>
<%@include file="css/mmeditors.css" %>     
    </style>
  </head>
  <body>
    <table class="fieldeditor">
      <% if (isRelation) { %>
      <tr ><td class="fieldcaption"><%=mmlanguage.getInfo("GET-remove_relation")%></td></tr>
      <% } else { %>
      <tr ><td class="fieldcaption"><%=mmlanguage.getInfo("GET-remove_object")%></td></tr>
      <% } %>
      <tr>
        <td>
          <form method="post" action="editor.jsp" target="_top">
            <p><input type="image" class="button" name="action" value="deletenode" src="gfx/btn.red.gif" /><%=mmlanguage.getInfo("GET-yes")%>
            &nbsp;&nbsp;
            <input type="image" class="button" name="action" value="cancel" src="gfx/btn.green.gif" /><%=mmlanguage.getInfo("GET-no")%>
            </p>
          </form>
        </td>
      </tr>
    </table>
  </body>
  </mm:cloud>
</html>
