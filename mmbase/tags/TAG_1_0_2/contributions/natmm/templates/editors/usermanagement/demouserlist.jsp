<%@include file="/taglibs.jsp" %>
<html>
<head>
<link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
<title>Gebruikers</title>
</head>
<body>
<mm:cloud method="http" rank="administrator" jspvar="cloud">
<h1>Alle gebruikers</h1>
<table>
<tr><th>Account</th><th>Naam</th><th>&nbsp;</th></tr>
<mm:listnodes type='users' orderby='account'>
<tr>
<td><a href="UserInitAction.eb?id=<mm:field name='number'/>"><mm:field name="account"/></a></td>
<td><mm:field name="voornaam"/>
<mm:field name="tussenvoegsel"/>
<mm:field name="achternaam"/>
</td>
<td>
<%
   if (cloud.getUser().getIdentifier().equals("admin")) {
%>
<mm:maydelete>
<a href="DeleteUserAction.eb?id=<mm:field name='number'/>"><img src="../img/remove.gif" border='0' onClick="return confirm('Gebruiker verwijderen?')"/></a>
</mm:maydelete>
<%
   }
%>
</td>
</tr>
</mm:listnodes>
</table><BR>
<a href="UserInitAction.eb"><img src="../img/new.gif" border='0' align='middle'/>Nieuwe gebruiker</a>
</mm:cloud>
</body>
</html>