<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
<mm:content type="text/html" encoding="UTF-8" escaper="entities" expires="0">
<mm:import externid="forumid" />
<%@ include file="thememanager/loadvars.jsp" %>

<mm:import externid="adminmode">false</mm:import>
<mm:import externid="pathtype">poster_index</mm:import>
<mm:import externid="postareaid" />
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

<mm:include page="path.jsp?type=$pathtype" />
<a href="<mm:url page="poster.jsp">
<mm:param name="forumid" value="$forumid" />
<mm:param name="postareaid" value="$postareaid" />
<mm:param name="posterid" value="$posterid" />
<mm:present referid="type"><mm:param name="type" value="$type" /></mm:present>
</mm:url>"><mm:write referid="mlg.personal" /></a> - 
<a href="<mm:url page="avatar.jsp">
<mm:param name="forumid" value="$forumid" />
<mm:param name="postareaid" value="$postareaid" />
<mm:param name="posterid" value="$posterid" />
<mm:present referid="type"><mm:param name="type" value="$type" /></mm:present>
</mm:url>"><mm:write referid="mlg.avatar" /></a>
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="40%">
<mm:compare referid="profileid" referid2="posterid" inverse="true">
<mm:node number="$profileid">
		<tr><th width="150" ><mm:write referid="mlg.Account" /></th><td><mm:field name="account" /></td></tr>
		<tr><th><mm:write referid="mlg.Firstname" /></th><td><mm:field name="firstname" /></td></tr>
		<tr><th><mm:write referid="mlg.Lastname" /></th><td><mm:field name="lastname" /></td></tr>
		<tr><th><mm:write referid="mlg.Email" /></th><td><mm:field name="email" /></td></tr>
		<tr><th><mm:write referid="mlg.Level" /></th><td><mm:field name="level" /></td></tr>
		<tr><th><mm:write referid="mlg.Gender" /></th><td><mm:field name="gender" /></td></tr>
		<tr><th><mm:write referid="mlg.Messages" /></th><td><mm:field name="postcount" /></td></tr>
		<tr><th><mm:write referid="mlg.Location" /></th><td><mm:field name="location" /></td></tr>

		<tr><th><mm:write referid="mlg.Member_since" /></th><td><mm:field name="firstlogin"><mm:time format="d MMMM, yyyy, HH:mm:ss" /></mm:field></td></tr>
		<tr><th><mm:write referid="mlg.Last_visit" /></th><td><mm:field name="lastseen"><mm:time format="d MMMM, yyyy, HH:mm:ss" /></mm:field></td></tr>
</mm:node>
</mm:compare>
<mm:compare referid="profileid" referid2="posterid">
<form action="<mm:url page="poster.jsp">
<mm:param name="forumid" value="$forumid" />
<mm:param name="postareaid" value="$postareaid" />
<mm:param name="posterid" value="$posterid" />
<mm:present referid="type"><mm:param name="type" value="$type" /></mm:present>
</mm:url>" method="post">
<mm:node number="$profileid">
		<tr><th width="150" ><mm:write referid="mlg.Account" /></th><td><mm:field name="account" /></td></tr>
		<tr><th><mm:write referid="mlg.Firstname" /></th><td>
			<input name="newfirstname" value="<mm:field name="firstname" />" style="width: 100%" />
			</td></tr>
		<tr><th><mm:write referid="mlg.Lastname" /></th><td>
			<input name="newlastname" value="<mm:field name="lastname" />" style="width: 100%" />
			</td></tr>
		<tr><th><mm:write referid="mlg.Email" /></th><td>
			<input name="newemail" value="<mm:field name="email" />" style="width: 100%" />
			</td></tr>
		<tr><th><mm:write referid="mlg.Location" /></th><td>
			<input name="newlocation" value="<mm:field name="location" />" style="width: 100%" />
			</td></tr>
		<tr><th><mm:write referid="mlg.Gender" /></th><td>
			<mm:field name="gender">
			<select name="newgender">
			<mm:compare value="male">
			<option value="male"><mm:write referid="mlg.Male" />
			<option value="female"><mm:write referid="mlg.Female" />
			</mm:compare>
			<mm:compare value="male" inverse="true">
			<option value="female"><mm:write referid="mlg.Male" />
			<option value="male"><mm:write referid="mlg.Female" />
			</mm:compare>
			</select>
			</mm:field>
		</td></tr>
		<tr><th><mm:write referid="mlg.Level" /></th><td><mm:field name="level" /></td></tr>
		<tr><th><mm:write referid="mlg.Messages" /></th><td><mm:field name="postcount" /></td></tr
		<tr><th><mm:write referid="mlg.Member_since" /></th><td><mm:field name="firstlogin"><mm:time format="d MMMM, yyyy, HH:mm:ss" /></mm:field></td></tr>
		<tr><th><mm:write referid="mlg.Last_seen" /></th><td><mm:field name="lastseen"><mm:time format="d MMMM, yyyy, HH:mm:ss" /></mm:field></td></tr>
</mm:node>
<tr><th colspan="2">
<input type="hidden" name="action" value="editposter">
<center><input type="submit" value="<mm:write referid="mlg.Save" />"></center>
</form>
</th></tr>
</mm:compare>
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
