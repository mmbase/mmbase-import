<%@page import="java.util.*" %>
<%@page import="nl.leocms.pagina.*,nl.leocms.util.ContentHelper,nl.leocms.authorization.*, org.mmbase.bridge.*" %>
<%@include file="/taglibs.jsp" %>
<html>
<head>
<link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
<link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/wizard.css" type="text/css" rel="stylesheet"/>
<title>Verwijder pagina</title>
<style>
input { width: 100px;}
</style>
<script type="text/javascript">
      function refreshParentFrameAndClose() {
         opener.top.bottompane.location = "frames.jsp";
         window.close();
      }
   </script>
</head>
<mm:cloud jspvar="cloud" rank="basic user" method='http'>
<%
   PaginaUtil paginaUtil = new PaginaUtil(cloud);
   String number = request.getParameter("number");
   String remove = request.getParameter("remove");
   if ((remove != null) && (remove.equals("ja"))) {

      // remove pagina
      paginaUtil.removePagina(number);
      %>
      <cache:flush scope="application"/>
      <body onload="refreshParentFrameAndClose()">
      <%

   } else {
      %>
      <body style="overflow:auto;" >
      <%

         Node pageNode = cloud.getNode(number);
      %>

      <h2>Pagina verwijderen</h2>
      <h3>Titel: <%= pageNode.getStringValue("titel") %></h3>
      <%
      String account = cloud.getUser().getIdentifier();
      AuthorizationHelper authorizationHelper = new AuthorizationHelper(cloud);
      UserRole role = authorizationHelper.getRoleForUserWithPagina(authorizationHelper.getUserNode(account), number);

      HashSet hsetRelatedContentElements = paginaUtil.doesPageContainContentElements(pageNode);
      if (hsetRelatedContentElements.size() > 0){
         %>
         <p>Deze pagina kan niet verwijderd worden, aangezien de pagina gebruik maakt van de volgende contentelementen.</p>
         <input type="button" value="Annuleren" onclick="window.close()"/>
         <table class="formcontent"  border="1" cellpadding="3" cellspacing="0">
         <%
            for(Iterator it = hsetRelatedContentElements.iterator(); it.hasNext();){
               // Use cloud.getNode(node.getNumber()) to get the node that is the extension of the contentelement.
               // E.g. artikel or dossier instead of contentelement
               Node node = (Node) it.next();
               String objecttype = cloud.getNode(node.getNumber()).getNodeManager().getName();
               %><tr>
                  <td class="fieldname"><%= objecttype %></td>
                  <td><%= node.getStringValue((new ContentHelper(cloud)).getTitleField(objecttype))%></td>
               </tr><%
            }
         %>
         </table>
         <br/><br/>
         <%

      } else {

         if ((role!= null) && (role.getRol() >= nl.leocms.authorization.Roles.EINDREDACTEUR)) {
            %>
            Weet u het zeker dat u deze pagina wilt verwijderen?
            <form action="delete_pagina.jsp"><input type="hidden" name="number" value="<%=number%>"><input type="submit" name="remove" value="ja"/>&nbsp;<input type="button" value="nee" onclick="window.close()"/></form>
            <%
         } else {
            %>
            U heeft geen rechten om deze pagina te verwijderen!
            <%
         }
       }
    }
%>
</mm:cloud>
</body>
</html>
