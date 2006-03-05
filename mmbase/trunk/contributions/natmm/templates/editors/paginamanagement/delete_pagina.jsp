<%@page import="nl.leocms.pagina.*,nl.leocms.authorization.*, org.mmbase.bridge.*" %>
<%@include file="/taglibs.jsp" %>
<html>
<head>
<link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
<link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/wizard.css" type="text/css" rel="stylesheet"/>
<title>Verwijder pagina</title>
<style>
input { width: 100px;}
</style>
<script language="JavaScript1.1">
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
      <body onload="refreshParentFrameAndClose()">
<%
   }
   else {
%>
      <body>   
   <%
      
      Node pageNode = cloud.getNode(number);
   %>
   
   <h2>Pagina verwijderen</h2>
   <h3>Titel: <%= pageNode.getStringValue("titel") %></h3>
   <%
      String account = cloud.getUser().getIdentifier();
   //   System.out.println("account = " + account);
      AuthorizationHelper authorizationHelper = new AuthorizationHelper(cloud);
      UserRole role = authorizationHelper.getRoleForUserWithPagina(authorizationHelper.getUserNode(account), number);
      if (paginaUtil.doesPageContainContentElements(pageNode)) {
   %>
         <p>Deze pagina kan niet verwijderd worden, aangezien er nog steeds verwijzingen zijn naar contentelementen.</p>
         <input type="button" value="Annuleren" onclick="window.close()"/>
   <%
       }
       else { 
         if ((role!= null) && (role.getRol() >= nl.leocms.authorization.Roles.EINDREDACTEUR)) {
   %>
          Weet u het zeker dat u deze pagina wilt verwijderen?
          <form action="delete_pagina.jsp"><input type="hidden" name="number" value="<%=number%>"><input type="submit" name="remove" value="ja"/>&nbsp;<input type="button" value="nee" onclick="window.close()"/></form>
   <%
         }
         else {
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