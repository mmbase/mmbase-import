<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
<mm:content type="text/html" encoding="UTF-8" escaper="entities" expires="0">
<%@ include file="thememanager/loadvars.jsp" %>
<html>
<head>
   <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
   <title>MMBob</title>
</head>
<body>
<mm:import externid="adminmode">false</mm:import>
<mm:import externid="forumid" />
<mm:import externid="pathtype">allposters</mm:import>
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

<div class="header">
</div>
                                                                                              
<div class="bodypart">

<mm:nodefunction set="mmbob" name="getForumInfo" referids="forumid,posterid">
    <mm:import id="isadministrator"><mm:field name="isadministrator" /></mm:import>
</mm:nodefunction>

<mm:include page="path.jsp?type=$pathtype" />
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 20px;" width="90%" align="center">

<mm:node referid="forumid">
  <tr>
    <th><mm:write referid="mlg.Account" /></th>
    <th><mm:write referid="mlg.Location" /></th>
    <th><mm:write referid="mlg.Last_seen" /></th>
    <mm:compare referid="isadministrator" value="true">
      <th><mm:write referid="mlg.Admin_tasks"/></th>
    </mm:compare>
  </tr>

	<mm:related path="forposrel,posters">
	<mm:node element="posters">
  <tr>
    <td><a href="profile.jsp?forumid=<mm:write referid="forumid" />&posterid=<mm:field name="number" />&pathtype=allposters_poster"><mm:field name="firstname" /> <mm:field name="lastname" /> (<mm:field name="account" />)</a></td>
    <td><mm:field name="location" /></td>
    <td><mm:field name="lastseen"><mm:time format="MMMM d, yyyy, HH:mm:ss" /></mm:field></td>
    <mm:compare referid="isadministrator" value="true">
      <td><a href="removeposter.jsp?forumid=<mm:write referid="forumid" />&removeposterid=<mm:field name="number" />"/><mm:write referid="mlg.Delete"/></a> / 
      <mm:field name="state">
        <mm:compare value="1" inverse="true"> 
          <a href="disableposter.jsp?forumid=<mm:write referid="forumid" />&disableposterid=<mm:field name="number" />"/><mm:write referid="mlg.Disable"/></a>
        </mm:compare>
        <mm:compare value="1">
          <a href="enableposter.jsp?forumid=<mm:write referid="forumid" />&enableposterid=<mm:field name="number" />"/><mm:write referid="mlg.Enable"/></a>
        </mm:compare>
      </mm:field> 
      </td>
    </mm:compare>
  </tr>
	</mm:node>
	</mm:related>
</table>
</mm:node>
</div>

<div class="footer">
</div>

</mm:locale>
                                                                                              
</body>
</html>
</mm:content>
</mm:cloud>
