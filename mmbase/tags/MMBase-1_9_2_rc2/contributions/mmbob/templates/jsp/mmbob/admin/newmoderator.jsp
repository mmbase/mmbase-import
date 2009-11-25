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
<!-- end action check -->

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
  <mm:nodefunction set="mmbob" name="getForumInfo" referids="forumid,posterid">
  <mm:import id="logoutmodetype"><mm:field name="logoutmodetype" /></mm:import>
  <mm:import id="navigationmethod"><mm:field name="navigationmethod" /></mm:import>
  <mm:import id="active_nick"><mm:field name="active_nick" /></mm:import>
  </mm:nodefunction>
  <mm:include page="../path.jsp?type=postarea2" referids="logoutmodetype,forumid,posterid,active_nick" />
<mm:compare referid="adminmode" value="true">

<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
   <mm:import externid="newmoderator" />
   <mm:import id="feedback"><mm:booleanfunction set="mmbob" name="newModerator" referids="forumid,postareaid,posterid,newmoderator" /></mm:import>
</mm:present>

<mm:present referid="feedback">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="40%">
    <tr><th>moderator added</th></tr>
    </tr><td align="center"><form action="<mm:url page="../postarea.jsp" referids="forumid,postareaid" />" method="post"><input type="submit" value="OK" /></form></td></tr>
</table>
</mm:present>
<mm:notpresent referid="feedback">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="75%">
  <tr><th colspan="3" align="left" ><mm:write referid="mlg.Add_moderator_for" /> : <mm:node number="$postareaid"><mm:field name="name" /></mm:node>

  </th></tr>

    <tr><th align="left" width="25%"><mm:write referid="mlg.Current_moderators" /></th><td colspan="2" align="left">
          <mm:nodelistfunction set="mmbob" name="getModerators" referids="forumid,postareaid">
            <mm:field name="nick" /> (<mm:field name="firstname" /> <mm:field name="lastname" />)<br />
          </mm:nodelistfunction>
    <p />
    </td></tr>
    <tr><th align="left"><mm:write referid="mlg.Possible_moderators" /></th><td colspan="2" align="left">
          <mm:import externid="searchkey">*</mm:import>
                  <form action="<mm:url page="newmoderator.jsp" referids="forumid,postareaid" />" method="post">
                        search <input name="searchkey" size="20" value="<mm:write referid="searchkey" />" />
                  </form>
          <form action="<mm:url page="newmoderator.jsp" referids="forumid,postareaid" />" method="post">
          <select name="newmoderator">
          <mm:nodelistfunction set="mmbob" name="getNonModerators" referids="forumid,postareaid,searchkey">
                <option value="<mm:field name="id" />"><mm:field name="nick" /> (<mm:field name="firstname" /> <mm:field name="lastname" />)<br />
          </mm:nodelistfunction>
        </select>
    </td></tr>
    <input type="hidden" name="admincheck" value="true">
    <input type="hidden" name="action" value="newmoderator">
    <tr><th>&nbsp;</th><td align="middle" >
    <input type="submit" value="<mm:write referid="mlg.Add" />">
    </form>
    </td>
    <td align="center">
    <form action="<mm:url page="../postarea.jsp" referids="forumid,postareaid" />" method="post">
    <p />
    <input type="submit" value="<mm:write referid="mlg.Cancel" />">
    </form>
    </td>
    </tr>

</table>
</mm:notpresent>
</mm:compare>
<mm:compare referid="adminmode" value="false">
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

