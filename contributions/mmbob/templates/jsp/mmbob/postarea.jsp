<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
<mm:content type="text/html" encoding="UTF-8" escaper="entities" expires="0">
<%@ include file="thememanager/loadvars.jsp" %>
<html>
<head>
   <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
   <title>MMBase Forum</title>
</head>
<body>

<mm:import externid="adminmode">false</mm:import>
<mm:import externid="forumid" />
<mm:import externid="postareaid" />
<mm:import externid="page">1</mm:import>

<!-- login part -->
<%@ include file="getposterid.jsp" %>
<!-- end login part -->


<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="actions.jsp" />
</mm:present>
<!-- end action check -->

<mm:locale language="$lang"> 
<%@ include file="loadtranslations.jsp" %>

<div class="header">
  <%@ include file="header.jsp" %>
</div>
                                                                                                              
<div class="bodypart">

<mm:include page="path.jsp?type=postarea" />
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="95%">
  		  <mm:nodefunction set="mmbob" name="getPostAreaInfo" referids="forumid,postareaid,posterid,page">
			<mm:import id="navline"><mm:field name="navline" /></mm:import>
			<mm:import id="pagecount"><mm:field name="pagecount" /></mm:import>
			<tr><th colspan="2" align="left">
					<mm:compare referid="image_logo" value="" inverse="true">
					<center><img src="<mm:write referid="image_logo" />" width="100%" ></center>
					<br />
					</mm:compare>
			<b><mm:write referid="mlg_Area_name"/></b> : <mm:field name="name" /> <b><mm:write referid="mlg_Topics"/></b> : <mm:field name="postthreadcount" /> <b><mm:write referid="mlg_Messages"/></b> : <mm:field name="postcount" /> <b><mm:write referid="mlg_Views"/></b> : <mm:field name="viewcount" /><br />
			<b><mm:write referid="mlg_Last_posting"/></b> : <mm:field name="lastposttime"><mm:compare value="-1" inverse="true"><mm:field name="lastposttime"><mm:time format="MMMM d, yyyy, HH:mm:ss" /></mm:field> <b><mm:write referid="mlg_by"/></b> <mm:field name="lastposter" /> <b> : '</b><mm:field name="lastsubject" /><b>'</b></mm:compare><mm:compare value="-1"><mm:write referid="mlg_no_messages"/></mm:compare></mm:field><br />
			<mm:import id="isadministrator"><mm:field name="isadministrator" /></mm:import>
                        <mm:import id="ismoderator"><mm:field name="ismoderator" /></mm:import>
		  </mm:nodefunction>
	<br />
	<b><mm:write referid="mlg_Moderators"/></b> :
  		  <mm:nodelistfunction set="mmbob" name="getModerators" referids="forumid,postareaid">
			<mm:field name="account" /> (<mm:field name="firstname" /> <mm:field name="lastname" />)<br />
		  </mm:nodelistfunction>
	</td>
	</tr>
</table>

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="95%">
  <tr>
    <th width="15">&nbsp;</th>
    <th width="15">&nbsp;</th>
    <th><mm:write referid="mlg_topic"/></th>
    <th><mm:write referid="mlg_author"/></th>
    <th><mm:write referid="mlg_replies"/></th>
    <th><mm:write referid="mlg_views"/></th>
    <th><mm:write referid="mlg_last_posting"/></th>
    <mm:compare referid="ismoderator" value="true">
      <th><mm:write referid="mlg_moderator"/></th>
    </mm:compare>
  </tr>

  <mm:nodelistfunction set="mmbob" name="getPostThreads" referids="forumid,postareaid,posterid,page">
  <tr>
    <td><mm:field name="state"><mm:write referid="image_state_$_" /></mm:field></td>
    <td><mm:field name="mood"><mm:write referid="image_mood_$_" /></mm:field></td>
    <td align="left"><a href="thread.jsp?forumid=<mm:write referid="forumid" />&postareaid=<mm:write referid="postareaid" />&postthreadid=<mm:field name="id" />"><mm:field name="name" /></a> <mm:field name="navline" /></td>
    <td align="left"><mm:field name="creator" /></td>
    <td align="left"><mm:field name="replycount" /></td>
    <td align="left"><mm:field name="viewcount" /></td>
    <td align="left">
      <mm:field name="lastposttime"><mm:time format="MMMM d, yyyy, HH:mm:ss" /></mm:field> 
      <mm:write referid="mlg_by"/>
      <mm:field name="lastposternumber">
        <mm:compare value="-1" inverse="true">
          <a href="profile.jsp?forumid=<mm:write referid="forumid" />&posterid=<mm:field name="lastposternumber" />"><mm:field name="lastposter" /></a>
        </mm:compare>
        <mm:compare value="-1" ><mm:field name="lastposter" /></mm:compare>
      </mm:field>
    </td>
    <mm:compare referid="ismoderator" value="true">
    <td>
      <a href="<mm:url page="removepostthread.jsp" referids="forumid,postareaid"><mm:param name="postthreadid"><mm:field name="id" /></mm:param></mm:url>">X</a> / <a href="<mm:url page="editpostthread.jsp" referids="forumid,postareaid"><mm:param name="postthreadid"><mm:field name="id" /></mm:param></mm:url>">E</a>
    </td>
    </mm:compare>
  </tr>
  </mm:nodelistfunction>
</table>

<mm:compare referid="pagecount" value="1" inverse="true">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 5px; margin-right : 30px;" align="right">
	<tr>
	<td>
	<mm:write referid="mlg_Pages"/> : <mm:write referid="navline" />
	</td></tr>
</table>
</mm:compare>
<table cellpadding="0" cellspacing="0" style="margin-top : 5px; margin-left : 25px" align="left">
	<tr><td><a href="<mm:url page="newpost.jsp"><mm:param name="forumid" value="$forumid" /><mm:param name="postareaid" value="$postareaid" /></mm:url>"><img src="<mm:write referid="image_newmsg" />" border="0" /></a> 
	</td></tr>
</table>
<br />
<br />
<br />
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 5px; margin-left : 30px" align="left">
	<tr><td align="left">
	<br />
	<mm:write referid="image_state_normal" /> <mm:write referid="mlg_open_topic"/><p />
	<mm:write referid="image_state_normalnew" /> <mm:write referid="mlg_open_topic_unread"/><p />
	<mm:write referid="image_state_hot" /> <mm:write referid="mlg_open_topic_popular"/><p />
	<mm:write referid="image_state_hotnew" /> <mm:write referid="mlg_open_topic_popular_unread"/><p />
	<mm:write referid="image_state_pinned" /> <mm:write referid="mlg_pinned_topic"/><p />
	<mm:write referid="image_state_closed" /> <mm:write referid="mlg_closed_topic"/><p />
	<mm:write referid="image_state_normalme" /> <mm:write referid="mlg_topic_to_which_you_have_contributed"/><p />
	</td></tr>
</table>
<br /><br />
<br /><br />
<br /><br />
<br /><br /><br />
<br /><br /><br />
<br /><br /><br />
<br /><br /><br />
<mm:compare referid="isadministrator" value="true">
        <table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;margin-left : 20px;" width="95%" align="left">
        <tr><th align="left"><mm:write referid="mlg_Admin_tasks"/></th></tr>
        <td>
        <p />
  				<a href="<mm:url page="changepostarea.jsp" referids="forumid,postareaid" />"><mm:write referid="mlg_change_area"/></a><br />

  				<a href="<mm:url page="removepostarea.jsp" referids="forumid,postareaid" />"><mm:write referid="mlg_remove_area"/></a><br />

  				<a href="<mm:url page="newmoderator.jsp">
				<mm:param name="forumid" value="$forumid" />
				<mm:param name="postareaid" value="$postareaid" />
				</mm:url>"><mm:write referid="mlg_add_moderator"/></a><br />
  				<a href="<mm:url page="removemoderator.jsp">
				<mm:param name="forumid" value="$forumid" />
				<mm:param name="postareaid" value="$postareaid" />
				</mm:url>"><mm:write referid="mlg_remove_moderator"/></a><br />
	</td></tr>
</table>
</mm:compare>

</div>

<div class="footer">
  <%@ include file="footer.jsp" %>
</div>

</body>
</html>

</mm:locale>
</mm:content>
</mm:cloud>
