<%@ include file="../jspbase.jsp" %>
<mm:cloud name="mmbase" method="http" rank="administrator">
<mm:content type="text/html" encoding="UTF-8" escaper="entities" expires="0">
<mm:import externid="forumid" jspvar="forumid">unknown</mm:import>
<%@ include file="../thememanager/loadvars.jsp" %>
<html>
<head>
   <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
   <title>MMBob</title>
</head>
<body>
<mm:import externid="remforum" />

<mm:import externid="action" />
<mm:compare value="removeforum" referid="action">
    <mm:import id="feedback"><mm:booleanfunction set="mmbob" name="removeForum" referids="remforum" /></mm:import>
</mm:compare>

<div class="header">
</div>

<div class="bodypart">

<mm:present referid="feedback">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="40%">
    <tr><th>Forum deleted</th></tr>
    </tr><td align="center"><form action="<mm:url page="../forums.jsp" />" method="post"><input type="submit" value="OK" /></form></td></tr>
</table>
</mm:present>

<mm:notpresent referid="feedback">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="40%">
  <tr><th colspan="3" align="left">Echt forum <mm:node number="$remforum"><mm:field name="name" /></mm:node> verwijderen ?
    <p />
    Dit houd in dat alle gebieden, onderwerpen, reacties en posters worden weg gegooit !!!
  </th></tr>
  </td></tr>
  <tr><td>
  <form action="<mm:url page="removeforum_confirm.jsp" />" method="post">
    <p />
    <center>
    <input type="hidden" name="remforum" value="<mm:write referid="remforum" />" /> <input type="hidden" name="action" value="removeforum" />
    <input type="submit" value="Ja, Echt Verwijderen">
    </form>
    </td>
    <td>
    <form action="<mm:url page="../forums.jsp" />" method="post">
    <p />
    <center>
    <input type="submit" value="Oops, Nee toch niet">
    </form>
    </td>
    </tr>

</table>
</mm:notpresent>
</div>

<div class="footer">
</div>

</body>
</html>
</mm:content>
</mm:cloud>

