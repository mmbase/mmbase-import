<%@ include file="../jspbase.jsp" %>
<mm:cloud>
<mm:content type="text/html" encoding="UTF-8" escaper="entities" expires="0">
<mm:import externid="forumid" />
<%@ include file="../thememanager/loadvars.jsp" %>
<html>
<head>
   <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
   <title>MMBob</title>
</head>
<body>
<mm:import externid="adminmode">false</mm:import>
<mm:import externid="pathtype">allposters</mm:import>
<mm:import externid="posterid" id="profileid" />
<mm:import externid="searchkey">*</mm:import>
<mm:import externid="page">0</mm:import>
<mm:import id="pagesize">25</mm:import>

<!-- login part -->
<%@ include file="../getposterid.jsp" %>
<!-- end login part -->

<mm:locale language="$lang">
<%@ include file="../loadtranslations.jsp" %>

<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="actions.jsp" />
</mm:present>
<!-- end action check -->

<div class="header">
    <mm:function set="mmbob" name="getForumHeaderPath" referids="forumid">
            <jsp:include page="../${_}"/>
    </mm:function>
</div>

<div class="bodypart">

<mm:nodefunction set="mmbob" name="getForumInfo" referids="forumid,posterid">
    <mm:import id="isadministrator"><mm:field name="isadministrator" /></mm:import>
    <mm:import id="logoutmodetype"><mm:field name="logoutmodetype" /></mm:import>
    <mm:import id="navigationmethod"><mm:field name="navigationmethod" /></mm:import>
    <mm:import id="active_nick"><mm:field name="active_nick" /></mm:import>
    <mm:include page="../path.jsp?type=subindex" referids="logoutmodetype,forumid,posterid,active_nick" />
</mm:nodefunction>

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 20px;" width="90%">

  <tr>
    <form action="<mm:url page="profiles.jsp" referids="forumid" />" method="post">
    <th colspan="4">
    Search : <input name="searchkey" size="20" value="<mm:write referid="searchkey" />" />
    </th>
    </form>
  </tr>
  <tr>
    <th><mm:write referid="mlg.Account" /></th>
    <th><mm:write referid="mlg.Location" /></th>
    <th><mm:write referid="mlg.Last_seen" /></th>
    <mm:compare referid="isadministrator" value="true">
      <th><mm:write referid="mlg.Admin_tasks"/></th>
    </mm:compare>
  </tr>

<mm:nodelistfunction set="mmbob" name="getPosters" referids="forumid,searchkey,page,pagesize">
  <tr>
    <td><a href="profile.jsp?forumid=<mm:write referid="forumid" />&posterid=<mm:field name="number" />&pathtype=allposters_poster"><mm:field name="firstname" /> <mm:field name="lastname" /> (<mm:field name="nick" />)</a></td>
    <td><mm:field name="location" /></td>
    <td><mm:field name="lastseen"><mm:time format="d MMMM, yyyy, HH:mm:ss" /></mm:field></td>
    <mm:compare referid="isadministrator" value="true">
      <td><a href="removeposter.jsp?forumid=<mm:write referid="forumid" />&removeposterid=<mm:field name="number" />"/><mm:write referid="mlg.Delete"/></a> /
      <mm:field name="blocked">
        <mm:compare value="false">
          <a href="disableposter.jsp?forumid=<mm:write referid="forumid" />&disableposterid=<mm:field name="number" />"/><mm:write referid="mlg.Disable"/></a>
        </mm:compare>
        <mm:compare value="true">
          <a href="enableposter.jsp?forumid=<mm:write referid="forumid" />&enableposterid=<mm:field name="number" />"/><mm:write referid="mlg.Enable"/></a>
        </mm:compare>
      </mm:field>
      </td>
    </mm:compare>
  </tr>
  <mm:last>
    <tr>
    <td align="right" colspan="2">
        <mm:field name="prevpage">
        <mm:compare value="-1" inverse="true">
        <mm:import id="page" reset="true"><mm:field name="prevpage" /></mm:import>
        <a href="<mm:url page="allposters.jsp" referids="forumid,searchkey,page" />"><<<--</a>
        </mm:compare>
        </mm:field>
    </td>
    <td align="left" colspan="2">
        <mm:field name="nextpage">
        <mm:compare value="-1" inverse="true">
        <mm:import id="page" reset="true"><mm:field name="nextpage" /></mm:import>
        <a href="<mm:url page="allposters.jsp" referids="forumid,searchkey,page" />">-->>></a>
        </mm:compare>
        </mm:field>
    </td>
    </tr>
  </mm:last>
  </mm:nodelistfunction>
</table>
</div>

<div class="footer">
    <mm:import id="footerpath" jspvar="footerpath"><mm:function set="mmbob" name="getForumFooterPath" referids="forumid"/></mm:import>
    <jsp:include page="<%=footerpath%>"/>
</div>

</mm:locale>

</body>
</html>
</mm:content>
</mm:cloud>
