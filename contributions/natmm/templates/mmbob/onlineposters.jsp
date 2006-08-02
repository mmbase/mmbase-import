<%-- !DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd" --%>
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
<%@ include file="thememanager/loadvars.jsp" %>
<%@ include file="settings.jsp" %>
<html>
<head>
   <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
   <title>MMBob</title>
</head>
<mm:import externid="adminmode">false</mm:import>
<mm:import externid="forumid" />
<mm:import externid="pathtype">onlineposters</mm:import>
<mm:import externid="posterid" id="profileid" />

<!-- login part -->
<%@ include file="getposterid.jsp" %>
<!-- end login part -->


<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="actions.jsp" />
</mm:present>
<!-- end action check -->

<center>
<mm:include page="path.jsp?type=$pathtype" />
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 20px;" width="90%">
<tr><th >Account</th><th>Location</th><th>Last Seen</th></tr>
    <mm:nodelistfunction set="mmbob" name="getPostersOnline" referids="forumid">
    <tr><td><a href="profile.jsp?forumid=<mm:write referid="forumid" />&posterid=<mm:field name="id" />&pathtype=onlineposters_poster"><mm:field name="firstname" /> <mm:field name="lastname" /> (<mm:field name="account" />)</a></td><td><mm:field name="location" /></td><td><mm:field name="lastseen"><mm:time format="<%= timeFormat %>" /></mm:field></td></tr>
    </mm:nodelistfunction>
</table>
</mm:cloud>
</center>
</html>
