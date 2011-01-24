<%@ include file="../jspbase.jsp" %>
<mm:cloud>
<mm:content type="text/html" encoding="UTF-8" escaper="entities">
<mm:import externid="forumid" />
<%@ include file="../thememanager/loadvars.jsp" %>
<mm:import externid="postareaid" />

<!-- login part -->
  <%@ include file="../getposterid.jsp" %>
<!-- end login part -->

<mm:locale language="$lang">
<%@ include file="../loadtranslations.jsp" %>

<mm:nodefunction set="mmbob" name="getForumInfo" referids="forumid,posterid">
  <mm:remove referid="adminmode" />
  <mm:import id="adminmode"><mm:field name="isadministrator" /></mm:import>
</mm:nodefunction>

<html>
<head>
   <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
   <title>MMBob</title>
</head>
<body>

<div class="header">
    <mm:import id="headerpath" jspvar="headerpath"><mm:function set="mmbob" name="getForumHeaderPath" referids="forumid"/></mm:import>
    <jsp:include page="<%=headerpath%>"/>
</div>

<div class="bodypart">
<mm:compare referid="adminmode" value="false">
</mm:compare>
<mm:compare referid="adminmode" value="true">

<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
  <mm:compare value="removepostarea" referid="action">
        <mm:import id="feedback"><mm:booleanfunction set="mmbob" name="removePostArea" referids="forumid,postareaid" /></mm:import>
  </mm:compare>
</mm:present>

<mm:present referid="feedback">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="40%">
    <tr><th><mm:write referid="mlg.Area" />  <mm:write referid="mlg.Delete"/></th></tr>
    </tr><td align="center"><form action="<mm:url page="../index.jsp" referids="forumid" />" method="post"><input type="submit" value="OK" /></form></td></tr>
</table>
</mm:present>
<mm:notpresent referid="feedback">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="40%">
  <tr><th colspan="3"><mm:write referid="mlg.Delete"/> <mm:write referid="mlg.Area" /> : <mm:node referid="postareaid"><mm:field name="name" /></mm:node> </th></tr>
   <tr><td colspan="3"><mm:write referid="mlg.Are_you_sure" /></td></tr>
  <tr><td>
  <form action="<mm:url page="removepostarea.jsp" referids="forumid,postareaid" />" method="post">
    <input type="hidden" name="admincheck" value="true">
    <input type="hidden" name="action" value="removepostarea">
    <p />
    <center>
    <input type="submit" value="<mm:write referid="mlg.Yes_delete"/>">
        </center>
    </form>
    </td>
    <td>
    <form action="<mm:url page="../postarea.jsp" referids="forumid,postareaid" />" method="post">
    <p />
    <center>
    <input type="submit" value="<mm:write referid="mlg.Cancel"/>">
        </center>
    </form>
    </td>
    </tr>

</table>
</mm:notpresent>
</mm:compare>

</div>

<div class="footer">
    <mm:import id="footerpath" jspvar="footerpath"><mm:function set="mmbob" name="getForumFooterPath" referids="forumid"/></mm:import>
    <jsp:include page="<%=footerpath%>"/>
</div>

</body>
</html>

</mm:locale>
</mm:content>
</mm:cloud>

