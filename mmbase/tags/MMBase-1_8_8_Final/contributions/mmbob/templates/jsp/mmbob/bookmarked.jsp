<%--  show the bookmarked post threads by the current user--%>
<%@ include file="jspbase.jsp" %>
<mm:cloud>
<mm:content type="text/html" encoding="UTF-8" escaper="entities" expires="0">
<mm:import externid="forumid" />
<%@ include file="thememanager/loadvars.jsp" %>

<mm:import externid="postareaid">-1</mm:import>
<mm:import externid="page">1</mm:import>
<mm:import externid="pagesize">100</mm:import>
<mm:import externid="pathtype">bookmarked</mm:import>
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

<mm:nodefunction set="mmbob" name="getForumConfig" referids="forumid,posterid">
  <mm:import id="pagesize" reset="true"><mm:field name="postingsperpage" /></mm:import>
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
<mm:include page="path.jsp?type=$pathtype" referids="logoutmodetype,posterid,forumid,active_nick" />
</mm:nodefunction>
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="90%" >

    <mm:nodelistfunction set="mmbob" name="getBookmarkedThreads" referids="forumid,postareaid,posterid,page,pagesize">
    <mm:first>
    <mm:import id="resultfound" />
    <tr>
        <th>&nbsp;</th>
        <th>&nbsp;</th>
     <th><mm:write referid="mlg.topic"/></th>
     <th><mm:write referid="mlg.area_name"/></th>
     <th><mm:write referid="mlg.author"/></th>
     <th><mm:write referid="mlg.replies"/></th>
     <th><mm:write referid="mlg.views"/></th>
        <th><mm:write referid="mlg.last_posting"/></th>
    </tr>
    </mm:first>
    <tr>

            <td><mm:field name="state"><mm:write referid="image_state_$_" /></mm:field></td>
            <td><mm:field name="mood"><mm:write referid="image_mood_$_" /></mm:field></td>
        <td><a href="<mm:url page="thread.jsp" referids="forumid">
             <mm:param name="postareaid"><mm:field name="postareaid" /></mm:param>
             <mm:param name="postthreadid"><mm:field name="postthreadid" /></mm:param>
             </mm:url>"><mm:field name="lastsubject" /></a> <mm:field name="navline" /> </td>
        <td><mm:field name="postareaname" /></td>
        <td><mm:field name="creator" /></td>
        <td><mm:field name="replycount" /></td>
        <td><mm:field name="viewcount" /></td>

            <td align="left">
            <mm:field name="lastposttime"><mm:time format="d MMMM, yyyy, HH:mm:ss" /></mm:field>
             <mm:write referid="mlg.by"/>
              <mm:field name="lastposternumber">
             <mm:compare value="-1" inverse="true">
                <a href="profile.jsp?forumid=<mm:write referid="forumid" />&posterid=<mm:field name="lastposternumber" />"><mm:field name="lastposter" /></a>
             </mm:compare>
              <mm:compare value="-1" ><mm:field name="lastposter" /></mm:compare>
             </mm:field>
        <a href="thread.jsp?forumid=<mm:write referid="forumid" />&postareaid=<mm:field name="postareaid" />&postthreadid=<mm:field name="postthreadid" />&page=<mm:field name="pagecount" />#reply">></a>
         </td>
    </tr>
    </mm:nodelistfunction>
    <mm:notpresent referid="resultfound">
    <th><mm:write referid="mlg.warning" /></th>
    <tr><td colspan="8"><b><mm:write referid="mlg.NoBookmarksFound"/></b></td></tr>
    </mm:notpresent>
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
