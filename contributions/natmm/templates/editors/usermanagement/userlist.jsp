<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@include file="/taglibs.jsp" %>
<%@page import="nl.leocms.authorization.forms.UserForm" %>
<html>
<head>
<link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
<link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/wizard.css" type="text/css" rel="stylesheet"/>
<title>Gebruikers</title>
</head>
<body style="overflow:auto;">
<mm:cloud method="http" rank="administrator" jspvar="cloud">
<% ApplicationHelper ap = new ApplicationHelper(cloud); %>
<h1>Alle gebruikers</h1>
<a href="UserInitAction.eb"><img src="../img/new.gif" border='0' align='middle'/>Nieuwe gebruiker</a> 
<table class="formcontent" style="width:auto;">
<tr>
   <th style="width:10%;">Account</th>
   <th style="width:10%;">Naam</th>
   <th style="width:10%;">Rank</th>
   <th style="width:10%;">&nbsp;</th>
   <th style="width:30%;">Rollen</th>
	<%
	if(ap.isInstalled("NatMM")) {
		%>
   	<th style="width:30%;">Afdeling (tbv authorisatie in CAD)</th>
		<% 
	} %>
</tr>
<mm:listnodes type='users' orderby='account'>
<tr>
   <td style="vertical-align:top;"><a href="UserInitAction.eb?id=<mm:field name='number'/>"><mm:field name="account"/></a></td>
   <td style="vertical-align:top;"><nobr><mm:field name="voornaam"/> <mm:field name="tussenvoegsel"/> <mm:field name="achternaam"/></nobr></td>
   <td style="vertical-align:top;"><nobr><mm:field name="rank">
         <mm:compare value="anonymous"><%= UserForm.ANONYMOUS %></mm:compare>
         <mm:compare value="basic user"><%= UserForm.BASIC_USER %></mm:compare>
         <mm:compare value="chiefeditor"><%= UserForm.CHIEFEDITOR %></mm:compare>
         <mm:compare value="administrator"><%= UserForm.ADMINISTRATOR %></mm:compare>
       </mm:field></nobr>
   </td>
   <td>
      <mm:maydelete>
         <a href="DeleteUserAction.eb?id=<mm:field name='number'/>"><img src="../img/remove.gif" border='0' alt="Gebruiker verwijderen" onClick="return confirm('Gebruiker verwijderen?')"/></a>
      </mm:maydelete>
      <%-- hh
      <a href="ChatModeratorAction.eb?id=<mm:field name='number'/>"><img src="../img/chatmod.gif" border='0' alt="Maak gebruiker chat moderator" onClick="return confirm('Maak gebruiker chat moderator?')"/></a>
      --%>
   </td>
   <td style="vertical-align:top;"><mm:related path="rolerel,rubriek" orderby="rubriek.naam">
         <mm:first inverse="true">, </mm:first>
         <mm:field name="rubriek.naam" />
         (<mm:field name="rolerel.rol">
            <mm:compare value="-1">-</mm:compare>
            <mm:compare value="0">Lezer</mm:compare>
            <mm:compare value="1">Schrijver</mm:compare>
            <mm:compare value="2">Redacteur</mm:compare>
            <mm:compare value="3">Eindredacteur</mm:compare>
            <mm:compare value="100">Webmaster</mm:compare>
         </mm:field>)
       </mm:related>
   </td>
	<%
	if(ap.isInstalled("NatMM")) {
		%>
		<td style="vertical-align:top;"><mm:related path="rolerel,afdelingen" orderby="afdelingen.naam">
				<mm:first inverse="true">, </mm:first>
				<mm:field name="afdelingen.naam" />
			 </mm:related>
		</td>
		<% 
	} %>
</tr>
</mm:listnodes>
</table>
<br/>
<a href="UserInitAction.eb"><img src="../img/new.gif" border='0' align='middle'/>Nieuwe gebruiker</a> 
<br/><br/>
</mm:cloud>
</body>
</html>