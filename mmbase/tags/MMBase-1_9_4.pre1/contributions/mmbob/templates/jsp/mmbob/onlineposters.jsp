<%@ include file="jspbase.jsp" %>
<mm:cloud>
<mm:content type="text/html" encoding="UTF-8" escaper="entities" expires="0">
<mm:import externid="forumid" />
<%@ include file="thememanager/loadvars.jsp" %>

<mm:import externid="adminmode">false</mm:import>
<mm:import externid="pathtype">onlineposters</mm:import>
<mm:import externid="posterid" id="profileid" />

<!-- login part -->
<%@ include file="getposterid.jsp" %>
<!-- end login part -->

<mm:locale language="$lang">
<%@ include file="loadtranslations.jsp" %>

<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="actions.jsp" />
</mm:present>
<!-- end action check -->

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
<mm:include page="path.jsp?type=$pathtype" referids="logoutmodetype,posterid,forumid,active_nick" />
</mm:nodefunction>
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 20px;" width="90%">
<tr><th ><mm:write referid="mlg.Account" /></th><th><mm:write referid="mlg.Location" /></th><th><mm:write referid="mlg.Last_seen" /></th></tr>
    <mm:nodelistfunction set="mmbob" name="getPostersOnline" referids="forumid">
    <tr><td><a href="profile.jsp?forumid=<mm:write referid="forumid" />&posterid=<mm:field name="id" />&pathtype=onlineposters_poster"><mm:field name="firstname" /> <mm:field name="lastname" /> (<mm:field name="nick" />)</a></td><td><mm:field name="location" /></td><td><mm:field name="lastseen"><mm:time format="d MMMM, yyyy, HH:mm:ss" /></mm:field></td></tr>
    </mm:nodelistfunction>
</table>

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
