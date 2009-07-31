<%@ include file="../jspbase.jsp" %>
<mm:cloud>
<mm:content type="text/html" encoding="UTF-8" escaper="entities" expires="0">
<mm:import externid="forumid" />
<%@ include file="../thememanager/loadvars.jsp" %>

<!-- login part -->
  <%@ include file="../getposterid.jsp" %>
<!-- end login part -->


<mm:nodefunction set="mmbob" name="getForumInfo" referids="forumid,posterid">
  <mm:remove referid="adminmode" />
  <mm:import id="adminmode"><mm:field name="isadministrator" /></mm:import>
</mm:nodefunction>


<mm:locale language="$lang">
<%@ include file="../loadtranslations.jsp" %>

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

<mm:nodefunction set="mmbob" name="getForumInfo" referids="forumid,posterid">
    <mm:import id="isadministrator"><mm:field name="isadministrator" /></mm:import>
    <mm:import id="logoutmodetype"><mm:field name="logoutmodetype" /></mm:import>
    <mm:import id="navigationmethod"><mm:field name="navigationmethod" /></mm:import>
    <mm:import id="active_nick"><mm:field name="active_nick" /></mm:import>
    <mm:include page="../path.jsp?type=subindex" referids="logoutmodetype,forumid,posterid,active_nick" />
</mm:nodefunction>

<mm:compare referid="adminmode" value="true">

<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
<mm:compare value="newpostarea" referid="action">
    <mm:import externid="name" />
    <mm:import externid="description" />
    <mm:nodefunction set="mmbob" name="newPostArea" referids="forumid,name,description,posterid@activeid">
        <mm:import id="feedback"><mm:field name="feedback" /></mm:import>
    </mm:nodefunction>
</mm:compare>
</mm:present>
<!-- end action check -->


<mm:present referid="feedback">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="40%">
    <tr><th><mm:write referid="mlg.Area" />  <mm:write referid="mlg.New" /></th></tr>
    </tr><td align="center"><form action="<mm:url page="../index.jsp" referids="forumid" />" method="post"><input type="submit" value="OK" /></form></td></tr>
</table>
</mm:present>
<mm:notpresent referid="feedback">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="75%">
  <tr><th colspan="3"><mm:write referid="mlg.Add_new_postarea" /></th></tr>

  <form action="<mm:url page="newpostarea.jsp">
                    <mm:param name="forumid" value="$forumid" />
                </mm:url>" method="post">
    <tr><th><mm:write referid="mlg.Name" /></th><td colspan="2">
    <input name="name" size="70" value="" style="width: 100%">
    </td></tr>
    <tr><th><mm:write referid="mlg.Description" /></th><td colspan="2">
    <textarea name="description" rows="5" style="width: 100%"></textarea>
    </td></tr>
    <input type="hidden" name="admincheck" value="true">
    <input type="hidden" name="action" value="newpostarea">
    <tr><th>&nbsp;</th><td align="middle" >
    <input type="submit" value="<mm:write referid="mlg.Add" />">
    </form>
    </td>
    <td align="center">
    <form action="<mm:url page="../index.jsp">
    <mm:param name="forumid" value="$forumid" />
    </mm:url>"
    method="post">
    <p />
    <input type="submit" value="<mm:write referid="mlg.Cancel" />">
    </form>
    </td>
    </tr>

</table>
</mm:notpresent>
</div>
</mm:compare>
<mm:compare referid="adminmode" value="false">
    <table cellpadding="0" cellspacing="0" class="list" style="margin-top : 40px;" width="75%" align="center">
        <tr><th>MMBob system error</th></tr>
        <tr><td height="40"><b>ERROR: </b> action not allowed by this user </td></tr>
    </table>
</mm:compare>

<div class="footer">
    <mm:import id="footerpath" jspvar="footerpath"><mm:function set="mmbob" name="getForumFooterPath" referids="forumid"/></mm:import>
    <jsp:include page="<%=footerpath%>"/>
</div>

</body>
</html>

</mm:locale>
</mm:content>
</mm:cloud>

