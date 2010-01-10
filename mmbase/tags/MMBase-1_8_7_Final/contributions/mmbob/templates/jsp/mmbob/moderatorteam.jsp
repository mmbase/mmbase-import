<%@ include file="jspbase.jsp" %>
<mm:cloud>
<mm:content type="text/html" encoding="UTF-8" escaper="entities" expires="0">
<mm:import externid="forumid" />
<%@ include file="thememanager/loadvars.jsp" %>

<mm:import externid="adminmode">false</mm:import>
<mm:import externid="pathtype">moderatorteam</mm:import>
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

<mm:node referid="forumid">
    <%--show the adimistrators for this forum--%>
    <table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="90%">
        <tr>
            <th><mm:write referid="mlg.Administrators" /></th>
            <th><mm:write referid="mlg.Location" /></th><th><mm:write referid="mlg.Last_seen" /></th>
        </tr>
            <mm:relatednodes path="rolerel,posters" constraints="rolerel.role like '%administrato%'" element="posters">
                <tr>
                    <td>
                        <mm:import id="someposterid" reset="true"><mm:field name="number" /></mm:import>
                        <mm:remove referid="p"/>
                        <mm:nodefunction name="getPosterInfo" set="mmbob" referids="forumid,someposterid@posterid" id="p">
                            <c:choose>
                                <c:when test="${p.shareprofile == 'true' && posterid != '-1'}">
                                    <a href="profile.jsp?forumid=${forumid}&posterid=${posterid}&pathtype=moderatorteam_poster">${p.identifier}</a>
                                </c:when>
                                <c:otherwise>${p.identifier} </c:otherwise>
                            </c:choose>
                        </mm:nodefunction>
                        <%--
                        <a href="profile.jsp?forumid=<mm:write referid="forumid" />&posterid=<mm:field name="number" />&pathtype=moderatorteam_poster"><mm:field name="firstname" /> <mm:field name="lastname" /></a>
                        --%>
                    </td>
                    <td><mm:field name="location" /></td>
                    <td><mm:field name="lastseen"><mm:time format="d MMMM, yyyy, HH:mm:ss" /></mm:field></td>
                </tr>
        </mm:relatednodes>
    </table>


    <table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="90%">
        <tr>
            <th><mm:write referid="mlg.Moderators" /></th>
            <th><mm:write referid="mlg.Location" /></th>
            <th><mm:write referid="mlg.Last_seen" /></th>
        </tr>
        <mm:relatednodes type="postareas">
            <tr>
                <th><mm:field name="name" /></th>
                <th></th>
                <th></th>
            </tr>
            <mm:relatednodes element="posters" path="rolerel,posters" constraints="rolerel.role like '%moderator%'">
                <mm:first><mm:import id="foundresult" /></mm:first>
                <tr>
                    <td>
                        <mm:import id="someposterid" reset="true"><mm:field name="number" /></mm:import>
                        <mm:remove referid="p"/>
                        <mm:nodefunction name="getPosterInfo" set="mmbob" referids="forumid,someposterid@posterid" id="p">
                            <c:choose>
                                <c:when test="${p.shareprofile == 'true' && posterid != '-1'}">
                                    <a href="profile.jsp?forumid=${forumid}&posterid=${posterid}&pathtype=moderatorteam_poster">${p.identifier}</a>
                                </c:when>
                                <c:otherwise>${p.identifier} </c:otherwise>
                            </c:choose>
                        </mm:nodefunction>
                        <%--
                        <a href="profile.jsp?forumid=<mm:write referid="forumid" />&posterid=<mm:field name="number" />&pathtype=moderatorteam_poster"><mm:field name="firstname" /> <mm:field name="lastname" /></a>
                    --%>

                    </td>
                    <td><mm:field name="location" /></td>
                    <td><mm:field name="lastseen"><mm:time format="d MMMM, yyyy, HH:mm:ss" /></mm:field></td>
                </tr>
            </mm:relatednodes>
            <mm:notpresent referid="foundresult">
                <tr>
                    <td colspan="3">Geen <mm:write referid="mlg.Moderators" /> </td>
                </tr>
            </mm:notpresent>
            <mm:remove referid="foundresult" />
        </mm:relatednodes>
    </table>

</mm:node>

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
