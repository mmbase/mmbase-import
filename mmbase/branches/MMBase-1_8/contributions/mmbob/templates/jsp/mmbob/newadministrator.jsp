<%@ include file="jspbase.jsp" %>
<mm:cloud>
<mm:content type="text/html" encoding="UTF-8" escaper="entities" expires="0">
<mm:import externid="forumid" />
<%@ include file="thememanager/loadvars.jsp" %>


<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="actions.jsp" />
</mm:present>

<!-- login part -->
  <%@ include file="getposterid.jsp" %>
<!-- end login part -->

<mm:locale language="$lang">
<%@ include file="loadtranslations.jsp" %>

<!-- end action check -->

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
<mm:compare referid="isadministrator" value="true">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="65%">
  <tr><th colspan="3" align="left" ><mm:write referid="mlg.Add_moderator_for" /> : <mm:node number="$forumid"><mm:field name="name" /></mm:node>

  </th></tr>

    <tr><th align="left" width="200"><mm:write referid="mlg.Current_moderators" /></th><td colspan="2" align="left">
          <mm:nodelistfunction set="mmbob" name="getAdministrators" referids="forumid">
            <mm:field name="nick" /> (<mm:field name="firstname" /> <mm:field name="lastname" />)<br />
          </mm:nodelistfunction>
    <p />
    </td></tr>
    <tr><th align="left"><mm:write referid="mlg.Possible_moderators" /></th><td align="left" colspan="2">
          <mm:import externid="searchkey">*</mm:import>
          <form action="<mm:url page="newadministrator.jsp" referids="forumid" />" method="post">
            search <input name="searchkey" size="20" value="<mm:write referid="searchkey" />" />
          </form>
          <form action="<mm:url page="index.jsp"><mm:param name="forumid" value="$forumid" /><mm:param name="admincheck" value="true" /></mm:url>" method="post">
          <select name="newadministrator">
          <mm:nodelistfunction set="mmbob" name="getNonAdministrators" referids="forumid,searchkey">
                <option value="<mm:field name="id" />"><mm:field name="nick" /> (<mm:field name="firstname" /> <mm:field name="lastname" />)<br />
          </mm:nodelistfunction>
        </select>
    </td></tr>
    <input type="hidden" name="admincheck" value="true">
    <input type="hidden" name="action" value="newadministrator">
    <tr><th>&nbsp;</th><td align="middle" >
    <input type="submit" value="<mm:write referid="mlg.Add" />">
    </form>
    </td>
    <td align="center">
    <form action="<mm:url page="index.jsp">
    <mm:param name="forumid" value="$forumid" />
    </mm:url>"
    method="post">
    <p />
    <input type="submit" value="<mm:write referid="mlg.Cancel" />">
    </form>
    </td>
    </tr>

</table>
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

