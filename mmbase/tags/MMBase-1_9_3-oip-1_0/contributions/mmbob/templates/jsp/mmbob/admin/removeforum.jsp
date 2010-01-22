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

<div class="header">
</div>

<div class="bodypart">

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="40%">
  <form action="<mm:url page="removeforum_confirm.jsp"></mm:url>" method="post">
  <tr><th colspan="3">Selecteer forum dat je wilt verwijderen</th></tr>
  <tr><td colspan="3" align="middle">
    <select name="remforum">
    <mm:nodelistfunction set="mmbob" name="getForums">
        <option value="<mm:field name="id" />"><mm:field name="name" />
    </mm:nodelistfunction>
    </select>
  </td></tr>
  <tr><td>
    <p />
    <center>
    <input type="submit" value="Ja, Verwijderen">
        </center>
    </form>
    </td>
    <td>
    <form action="<mm:url page="../forums.jsp" />" method="post">
    <p />
    <center>
    <input type="submit" value="Oops, Nee">
        </center>
    </form>
    </td>
    </tr>

</table>

</div>

<div class="footer">
</div>

</body>
</html>

</mm:content>
</mm:cloud>

