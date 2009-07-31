<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud name="mmbase" method="http" rank="administrator">
<mm:import externid="forumid" jspvar="forumid">unknown</mm:import>
<%@ include file="../thememanager/loadvars.jsp" %>

	<%@ page import='nl.kennisnet.entreeng.gec.*' %>
	<%@ page import='nl.kennisnet.entreeng.gec.attributes.*' %>
	<% Persoon persoon = EntreeNGRequestHelper.getPersoonFromRequest(request);
		String EntreeID = persoon.getEntreeID();
		String gebruikersnaam = persoon.getGebruikersnaam();
		if (EntreeID==null && gebruikersnaam!=null) EntreeID=gebruikersnaam;

	 %>
	<mm:import id="entree" reset="true"><%=EntreeID%></mm:import>
	<mm:import id="firstname" reset="true"><%=persoon.getVoornaam()%></mm:import>
	<mm:import id="lastname" reset="true"><%=persoon.getAchternaam()%></mm:import>
	<mm:import id="email" reset="true"><%=persoon.getEmail()%></mm:import>

<mm:import externid="action" />
<mm:present referid="action">
<mm:compare value="newforum" referid="action">
	<mm:import externid="name" />
	<mm:import externid="description" />
	<mm:import externid="language" />
	<mm:import id="newaccount" externid="account" />
	<mm:import id="newpassword" externid="password" />
	<mm:import id="newnick" externid="nick"></mm:import>
	<mm:import externid="email" />
	<mm:nodefunction set="mmbob" name="newForum" referids="name,language,description,newaccount@account,newpassword@password,newnick@nick,email">
		<mm:import id="feedback">true</mm:import>
	</mm:nodefunction>
</mm:compare>
</mm:present>

<html>
<head>
   <link rel="stylesheet" type="text/css" href="<mm:write referid="theme.style_default" />" />
   <title>MMBob</title>
</head>


<mm:present referid="feedback">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="40%">
	<tr><th>Forum added</th></tr>
	</tr><td align="center"><form action="<mm:url page="../forums.jsp" referids="forumid" />" method="post"><input type="submit" value="OK" /></form></td></tr>
</table>
</mm:present>
<mm:notpresent referid="feedback">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="75%">
  <tr><th colspan="3">Nieuw forum aanmaken</th></tr>

  <form action="<mm:url page="newforum.jsp" />" method="post">
	<tr><th>Naam</th><td colspan="2">
	<input name="name" size="70" value="" style="width: 100%">
	</td></tr>
	<input type="hidden" name="language" size="2" value="df">
	<tr><th>Omschrijving</th><td colspan="2">
	<textarea name="description" rows="5" style="width: 100%"></textarea>
	</td></tr>
	<mm:compare referid="entree" value="null">
	<tr><th>Admin account</th><td colspan="2">
	<input name="account" size="70" value="" style="width: 100%">
	</td></tr>
	<tr><th>Nick name</th><td colspan="2">
	<input name="nick" size="70" value="" style="width: 100%">
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
	<input name="account" type="hidden" value="<mm:write referid="entree" />" style="width: 100%" /> <mm:write referid="entree" />
	</td></tr>
	<tr><th>Admin wachtwoord</th><td colspan="2">
	<input type="hidden" name="password" value="<mm:write referid="entree" />" style="width: 100%"> *********
	</td></tr>
	<tr><th>Nick name</th><td colspan="2">
	<input name="nick" size="70" value="" style="width: 100%">
	</td></tr>
	<tr><th>Admin email</th><td colspan="2">
	<input type="hidden" name="email" value="<mm:write referid="entree" />" style="width: 100%"> <mm:write referid="email" />
	</td></tr>
	</mm:compare>
	<input type="hidden" name="action" value="newforum">
	<tr><th>&nbsp;</th><td align="middle" >
	<input type="submit" value="Aanmaken">
  	</form>
	</td>
	<td align="center">
  	<form action="<mm:url page="../forums.jsp">
	</mm:url>"
 	method="post">
	<p />
	<input type="submit" value="Laat maar">
  	</form>
	</td>
	</tr>

</table>
</mm:notpresent>
</mm:cloud>

