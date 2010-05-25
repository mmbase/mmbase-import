<%@ include file="../jspbase.jsp" %>
<mm:import id="entree" reset="true"><%= request.getHeader("aad_nummer") %></mm:import>
<mm:cloud name="mmbase" method="http" rank="administrator">
<mm:import externid="forumid" jspvar="forumid">unknown</mm:import>
<%@ include file="../thememanager/loadvars.jsp" %>

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
   <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
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
    <input name="account" type="hidden" value="<%= request.getHeader("sm_user") %>" style="width: 100%" /> <%= request.getHeader("sm_user") %>
    </td></tr>
    <tr><th>Admin wachtwoord</th><td colspan="2">
    <input type="hidden" name="password" value="<%= request.getHeader("aad_nummer") %>" style="width: 100%"> *********
    </td></tr>
    <tr><th>Nick name</th><td colspan="2">
    <input name="nick" size="70" value="" style="width: 100%">
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

