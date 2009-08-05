<%@page import="nl.leocms.util.*, nl.leocms.authorization.*, org.mmbase.bridge.*" %>
<%@include file="/taglibs.jsp" %>
<% 
String referrer = request.getParameter("referrer");
if(referrer==null) { 
   referrer = request.getHeader("referer"); // html-specs are wrong
   if(referrer==null) { 
      referrer = "";
   }
}
String parentFrame = "frames.jsp";
if(referrer.indexOf("paginamanagement")>-1) {
   parentFrame = "/editors/paginamanagement/frames.jsp";
}
%>
<html>
<head>
<link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
<link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/wizard.css" type="text/css" rel="stylesheet"/>
<title>Verwijder rubriek</title>
<style>
input { width: 100px;}
</style>
<script type="text/javascript">
      function refreshParentFrameAndClose() {
         opener.top.bottompane.location = "<%= parentFrame %>";
         window.close();
      }
   </script>
</head>

<mm:cloud jspvar="cloud" rank="chiefeditor" method='http'>
<%
RubriekHelper rubriekHelper = new RubriekHelper(cloud);
String number = request.getParameter("number");
String remove = request.getParameter("remove");
if ((remove != null) && (remove.equals("ja"))) {
   // remove rubriek
   rubriekHelper.removeRubriek(number);
   %>
   <cache:flush scope="application"/>
   <body onload="refreshParentFrameAndClose()">
   <%
} else {
   %>
   <body>   
   <%
      Node rubriekNode = cloud.getNode(number);
   %>
   <h2>Rubriek verwijderen</h2>
   <h3>Titel: <%= rubriekNode.getStringValue("naam") %></h3>
   <%
      String account = cloud.getUser().getIdentifier();
      AuthorizationHelper authorizationHelper = new AuthorizationHelper(cloud);
      UserRole role = authorizationHelper.getRoleForUser(authorizationHelper.getUserNode(account), cloud.getNode(number));
      if (rubriekHelper.isRubriekRemovable(rubriekNode)) {
         %>
         <p>Deze rubriek kan niet verwijderd worden, aangezien er nog steeds verwijzingen zijn naar pagina's en/of contentelementen.</p>
         <%
         NodeList contentElements = rubriekHelper.getContentElements(rubriekNode);
         if (contentElements != null) {
         out.println("<b>Contentelementen</b><br/>");
            for(Iterator it = contentElements.iterator(); it.hasNext();){
            	Node node = (Node) it.next();
               	String objecttype = cloud.getNode(node.getNumber()).getNodeManager().getName();
               	%>
              	<%= node.getStringValue((new ContentHelper(cloud)).getTitleField(objecttype))%>
              	&nbsp;(<%= objecttype %>)
               <br/>
               <%
            }
         }
         %><br/>
         <input type="button" value="Annuleren" onclick="window.close()"/>
         <%
      } else { 
         if ((role!= null) && (role.getRol() >= nl.leocms.authorization.Roles.EINDREDACTEUR)) {
            %>
            Weet u het zeker dat u deze rubriek wilt verwijderen?
            <form action="delete_rubriek.jsp">
               <input type="hidden" name="referrer" value="<%=referrer%>">
               <input type="hidden" name="number" value="<%=number%>">
               <input type="submit" name="remove" value="ja"/>&nbsp;
               <input type="button" value="nee" onclick="window.close()"/>
            </form>
            <%
         } else {
            %>
            U heeft geen rechten om deze rubriek te verwijderen!
            <%
         }
      }
}
%>
</mm:cloud>
</body>
</html>