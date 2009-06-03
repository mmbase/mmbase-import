<%@ include file="../jspbase.jsp" %>
<mm:cloud>
<mm:content type="text/html" encoding="UTF-8" escaper="entities" expires="0">
<mm:import externid="forumid" />
<%@ include file="../thememanager/loadvars.jsp" %>

<mm:import externid="postareaid" />

<!-- login part -->
  <%@ include file="../getposterid.jsp" %>
<!-- end login part -->

<mm:locale language="$lang">
<%@ include file="../loadtranslations.jsp" %>

<mm:nodefunction set="mmbob" name="getForumInfo" referids="forumid,posterid">
        <mm:import id="isadministrator"><mm:field name="isadministrator" /></mm:import>
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
  <mm:nodefunction set="mmbob" name="getForumInfo" referids="forumid,posterid">
  <mm:import id="logoutmodetype"><mm:field name="logoutmodetype" /></mm:import>
  <mm:import id="navigationmethod"><mm:field name="navigationmethod" /></mm:import>
  <mm:import id="active_nick"><mm:field name="active_nick" /></mm:import>
  </mm:nodefunction>
  <mm:include page="../path.jsp?type=postarea2" referids="logoutmodetype,forumid,posterid,active_nick" />
<mm:compare referid="isadministrator" value="true">
<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
<mm:compare value="removemoderator" referid="action">
    <mm:import externid="remmoderator" />
    <mm:import id="feedback"><mm:booleanfunction set="mmbob" name="removeModerator" referids="forumid,postareaid,posterid,remmoderator" /></mm:import>
</mm:compare>
</mm:present>
<!-- end action check -->

<mm:present referid="feedback">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="40%">
    <tr><th>moderator removed</th></tr>
    </tr><td align="center"><form action="<mm:url page="../postarea.jsp" referids="forumid,postareaid" />" method="post"><input type="submit" value="OK" /></form></td></tr>
</table>
</mm:present>
<mm:notpresent referid="feedback">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="75%" align="center">
  <tr><th colspan="3" align="left" ><mm:write referid="mlg.Remove_moderator_for" /> : <mm:node number="$postareaid"><mm:field name="name" /></mm:node>

  </th></tr>

  <form action="<mm:url page="removemoderator.jsp" referids="forumid,postareaid" />" method="post">
    <tr><th align="left" width="25%"><mm:write referid="mlg.Current_moderators" /></th><td colspan="2" align="left">
          <mm:nodelistfunction set="mmbob" name="getModerators" referids="forumid,postareaid">
            <mm:field name="nick" /> (<mm:field name="firstname" /> <mm:field name="lastname" />)<br />
          </mm:nodelistfunction>
    <p />
    </td></tr>
    <tr><th align="left"><mm:write referid="mlg.Possible_moderators" /></th><td colspan="2" align="left">
          <select name="remmoderator">
          <mm:nodelistfunction set="mmbob" name="getModerators" referids="forumid,postareaid">
                <option value="<mm:field name="id" />"><mm:field name="nick" /> (<mm:field name="firstname" /> <mm:field name="lastname" />)<br />
          </mm:nodelistfunction>
        </select>
    </td></tr>
    <input type="hidden" name="admincheck" value="true">
    <input type="hidden" name="action" value="removemoderator">
    <tr><th>&nbsp;</th><td align="middle" >
    <input type="submit" value="<mm:write referid="mlg.Delete" />">
    </form>
    </td>
    <td>
    <form action="<mm:url page="../postarea.jsp" referids="forumid,postareaid" />" method="post">
    <p />
    <center>
    <input type="submit" value="<mm:write referid="mlg.Cancel" />">
    </form>
    </td>
    </tr>

</table>
</mm:notpresent>
</mm:compare>
<mm:compare referid="isadministrator" value="false">
    <table cellpadding="0" cellspacing="0" class="list" style="margin-top : 40px;" width="75%" align="center">
        <tr><th>MMBob system error</th></tr>
        <tr><td height="40"><b>ERROR: </b> action not allowed by this user </td></tr>
    </table>
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

