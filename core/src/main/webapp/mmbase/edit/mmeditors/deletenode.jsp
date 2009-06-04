<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@include file="header.jsp" %><html xmlns="http://www.w3.org/1999/xhtml">
  <mm:cloud name="mmbase" method="http" jspvar="cloud" rank="$rank">
  <% Stack states = (Stack)session.getValue("mmeditors_states");
     Properties state = (Properties)states.peek();
     String transactionID = state.getProperty("transaction");
     String nodeID = state.getProperty("node");
     Transaction trans = cloud.getTransaction(transactionID);
     Node node = trans.getNode(nodeID);
     boolean isRelation = node.isRelation();
     Module mmlanguage;
     try {
        mmlanguage = cloud.getCloudContext().getModule("mmlanguage");
     } catch (NotFoundException nfe) {
        mmlanguage = null;
     }
  %>
  <head>
    <title>Editors</title>
    <link rel="stylesheet" href="css/mmeditors.css" type="text/css" />
  </head>
  <body>
    <table class="fieldeditor">
      <% if (isRelation) { %>
      <tr ><td class="fieldcaption"><%=translate(mmlanguage,"remove_relation")%></td></tr>
      <% } else { %>
      <tr ><td class="fieldcaption"><%=translate(mmlanguage,"remove_object")%></td></tr>
      <% } %>
      <tr>
        <td>
          <form method="post" action="editor.jsp" target="_top">
            <p><input type="image" class="button" name="action" value="deletenode" src="gfx/btn.red.gif" /><%=translate(mmlanguage,"yes")%>
            &nbsp;&nbsp;
            <input type="image" class="button" name="action" value="cancel" src="gfx/btn.green.gif" /><%=translate(mmlanguage,"no")%>
            </p>
          </form>
        </td>
      </tr>
    </table>
  </body>
  </mm:cloud>
</html>
