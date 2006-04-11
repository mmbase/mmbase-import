<%@include file="/taglibs.jsp" %>
<%@page import="nl.leocms.util.PropertiesUtil" %>
<mm:cloud jspvar='cloud' rank='basic user'>
<html>
<head>
    <link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
    <link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/wizard.css" type="text/css" rel="stylesheet"/>
    <title>Menu beheeromgeving</title>
    <script>
	     // *** refresh every X minutes , avoid session timeout ***
        function resubmit()
	     {
	        document.forms[0].submit();
	     }
	  </script>
     <style>
        td.fieldname {
            padding-left: 5px;
            padding-right: 5px;
            font-size: 14px;
        }
     </style>
</head>
<body onload="javascript:setTimeout('resubmit()',10*60000);" style="background-color:#E4F0F7;">
<mm:import externid="action"/>
<!-- We are going to set the referrer explicitely, because we don't wont to depend on the 'Referer' header (which is not mandatory) -->
<mm:import externid="language">nl</mm:import>
<mm:import id="referrer"><%=new java.io.File(request.getServletPath())%>?language=<mm:write  referid="language" /></mm:import>
<mm:import id="jsps"><%= editwizard_location %>/jsp/</mm:import>
<mm:import id="debug">false</mm:import>

<%
   String contentModusProperty = PropertiesUtil.getProperty("content.modus");
   if ((contentModusProperty != null) && (contentModusProperty.equals("on"))) {
      session.setAttribute("contentmodus", contentModusProperty);
   }
   else {
      session.removeAttribute("contentmodus.contentnodenumber");
      session.setAttribute("contentmodus", "off");
   }
   boolean isAdmin = cloud.getUser().getRank().equals("administrator");
   boolean isChiefEditor = cloud.getUser().getRank().equals("chiefeditor");
   String rubriekID = "";
   boolean hasEditwizards = false;
%>
<mm:listnodes type="users" constraints="<%= "[account]='" + cloud.getUser().getIdentifier() + "'" %>" max="1" id="thisuser">
   <mm:related path="rolerel,rubriek" max="1">
      <mm:node element="rubriek">
         <mm:aliaslist>
		      <mm:write jspvar="rubriek_alias" vartype="String" write="false">
               <% rubriekID = rubriek_alias; %>
            </mm:write>
         </mm:aliaslist>
      </mm:node>
   </mm:related>
   <mm:related path="gebruikt,editwizards" max="1">
      <% hasEditwizards = true; %>
   </mm:related>
</mm:listnodes>
<h1 style="text-align:center;width:100%;">Beheeromgeving <mm:node number="root" notfound="skipbody"><mm:field name="naam" /></mm:node></h1>
<table class="formcontent" style="background-color:#E4F0F7;width:auto;"><tr>
<%
if(rubriekID.equals("naardermeer")) { 
   %>
   <td class="fieldname"><a href="/naardermeer" target="_blank" class='menu'>Website</a></td>
   <td class="fieldname"><a href="paginamanagement/frames.jsp" target="bottompane" class='menu'>Pagina-editor</a></td>
   <td class="fieldname"><a href="usermanagement/changepassword.jsp" target="bottompane" class='menu'>Wijzig wachtwoord</a></td>
   <td class="fieldname"><a href="logout.jsp" target="_top" class='menu'>Uitloggen</a></td>
   <% 
} else if(rubriekID.equals("natuurin_rubriek")) {
   %>
   <td class="fieldname"><a href="/activiteiten" target="_blank" class='menu'>Website</a></td>
   <td class="fieldname"><a href="evenementen/evenementen.jsp" target="bottompane" class='menu'>Activiteiten</a></td>
   <% 
   if(hasEditwizards) {
      %><td class="fieldname"><a href="paginamanagement/frames.jsp" target="bottompane" class='menu'>Pagina-editor</a></td><%
   } %>
   <td class="fieldname"><a href="usermanagement/changepassword.jsp" target="bottompane" class='menu'>Wijzig wachtwoord</a></td>
   <td class="fieldname"><a href="logout.jsp" target="_top" class='menu'>Uitloggen</a></td>
   <% 
} else if(!rubriekID.equals("")) {
   %>
   <%--
   <td class="fieldname"><a href="signalering/takenlijst.jsp" target="bottompane" class='menu'>Takenlijst</a></td>
   <td lass="fieldname"><a href="../workflow/workflow.jsp" target="bottompane" class='menu'>Workflow</a></td>
   --%>
   <td class="fieldname"><a href="/index.jsp" target="_blank" class='menu'>Website</a></td>
   <td class="fieldname"><a href="beheerbibliotheek/index.jsp?refreshFrame=bottompane" target="bottompane" class='menu'>Bibliotheek</a></td>
   <td class="fieldname"><a href="evenementen/frames.jsp" target="bottompane" class='menu'>Activiteiten</a></td>
   <td class="fieldname"><a href="paginamanagement/frames.jsp" target="bottompane" class='menu'>Pagina-editor</a></td>
   <td class="fieldname"><a href="usermanagement/changepassword.jsp" target="bottompane" class='menu'>Wijzig wachtwoord</a></td>
   <td class="fieldname"><a href="logout.jsp" target="_top" class='menu'>Uitloggen</a></td>
   <%
} else {
   %>
   <td class="fieldname"><a href="/index.jsp" target="_blank" class='menu'>Website</a></td>
   <td class="fieldname"><a href="usermanagement/changepassword.jsp" target="bottompane" class='menu'>Wijzig wachtwoord</a></td>
   <td class="fieldname"><a href="logout.jsp" target="_top" class='menu'>Uitloggen</a></td>
   <td class="menu" style="color:red;">
         Er is geen rubriek voor u geselecteerd. Neem contact op met de webmasters om u een rol op één van de rubrieken te geven.
   </td>
   <%
}
%>
</tr><form name="dummy" method="post" target=""></form></table>
<div style="position:absolute;right:5px;top:5px;z-index:100"><small>
<li><a class="menu" target="bottompane" href="../doc/index.jsp" title="klik hier om de gebruikershandleidingen te bekijken of te downloaden">gebruikershandleiding</a><br>
<% String webmasterMail = ""; %>
<mm:listnodescontainer type="users"
         ><mm:constraint field="rank" operator="=" value="administrator" 
         /><mm:listnodes
            ><mm:first inverse="true"><% webmasterMail += ";"; %></mm:first
            ><mm:field name="emailadres" jspvar="dummy" vartype="String" write="false"
               ><% webmasterMail += dummy; 
            %></mm:field
         ></mm:listnodes
></mm:listnodescontainer>
<li><a class="menu" href="mailto:<%= webmasterMail %>" title="<%= webmasterMail %>">mail&nbsp;de&nbsp;webmasters</a><br>
<li><a class="menu" href="usermanagement/changepassword.jsp" target="bottompane" title="wijzig uw wachtwoord"><mm:node number="$thisuser">gebruiker:&nbsp;<mm:field name="voornaam"/>&nbsp;<mm:field name="tussenvoegsel"><mm:isnotempty><mm:write />&nbsp;</mm:isnotempty></mm:field><mm:field name="achternaam"
   /></mm:node></a></small></div>
</body>
</html>
</mm:cloud>
