<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:import id="entree" reset="true"><%= request.getHeader("aad_nummer") %></mm:import>
<mm:cloud name="mmbase" method="http" rank="administrator">
<mm:import externid="forumid" jspvar="forumid">unknown</mm:import>
<%@ include file="thememanager/loadvars.jsp" %>
<html>
<head>
   <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
   <title>MMBob</title>
</head>

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="75%">
  <tr><th colspan="3">Nieuw forum aanmaken</th></tr>

  <form action="<mm:url page="forums.jsp">
				</mm:url>" method="post">
	<tr><th>Naam</th><td colspan="2">
	<input name="name" size="70" value="" style="width: 100%">
	</td></tr>
	<tr><th>Taal</th><td colspan="2" align="left">
	<input name="language" size="2" value="en">
	</td></tr>
	<tr><th>Omschrijving</th><td colspan="2">
	<textarea name="description" rows="5" style="width: 100%"></textarea>
	</td></tr>
	<mm:compare referid="entree" value="null">
	<tr><th>Admin account</th><td colspan="2">
	<input name="account" size="70" value="" style="width: 100%">
	</td></tr>
	<tr><th>Admin wachtwoord</th><td colspan="2">
	<input type="password" name="password" size="70" value="" style="width: 100%">
	</td></tr>
	<tr><th>Admin email</th><td colspan="2">
	<input name="email" size="70" value="" style="width: 100%">
	</td></tr>
	</mm:compare>
	<mm:compare referid="entree" value="null" inverse="true">
	<tr><th>Admin account (entree)</th><td colspan="2">
	<input name="account" type="hidden" value="<%= request.getHeader("sm_user") %>" style="width: 100%" /> <%= request.getHeader("sm_user") %>
	</td></tr>
	<tr><th>Admin wachtwoord</th><td colspan="2">
	<input type="hidden" name="password" value="<%= request.getHeader("aad_nummer") %>" style="width: 100%"> *********
	</td></tr>
	<tr><th>Admin email</th><td colspan="2">
	<input type="hidden" name="email" value="<%= request.getHeader("aad_emailadres") %>" style="width: 100%"> *********
	</td></tr>
	</mm:compare>
	<input type="hidden" name="action" value="newforum">
	<tr><th>&nbsp;</th><td align="middle" >
	<input type="submit" value="Aanmaken">
  	</form>
	</td>
	<td align="center">
  	<form action="<mm:url page="forums.jsp">
	</mm:url>"
 	method="post">
	<p />
	<input type="submit" value="Laat maar">
  	</form>
	</td>
	</tr>

</table>
</mm:cloud>

