<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
<mm:content type="text/html" encoding="UTF-8" escaper="entities">
<%@ include file="thememanager/loadvars.jsp" %>
<html>
<head>
   <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
   <title>MMBob</title>
</head>
<body>
<mm:import externid="forumid" jspvar="forumid">unknown</mm:import>

<mm:compare referid="forumid" value="unknown">
	<table align="center" cellpadding="0" cellspacing="0" class="list" style="margin-top : 40px;" width="75%">
		<tr><th>MMBob system error</th></tr>
		<tr><td height="40"><b>ERROR: </b> No forum id is provided, if this is a new install try <a href="forums.jsp">forums.jsp</a> instead to create a new forum.</td></tr>
	</table>
</mm:compare>

<mm:compare referid="forumid" value="unknown" inverse="true">
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
    <%@ include file="header.jsp" %>
</div>

<div class="bodypart">

  <mm:include page="path.jsp?type=index" />

  <table cellpadding="0" cellspacing="0" class="list"  style="margin-top : 10px;" width="95%">
    <mm:nodefunction set="mmbob" name="getForumInfo" referids="forumid,posterid">
      <mm:import id="adminmode"><mm:field name="isadministrator" /></mm:import>
      <tr>
      <mm:compare referid="posterid" value="-1">
        <th width="100"><a href="newposter.jsp?forumid=<mm:write referid="forumid" />"><img src="images/guest.gif" border="0"></a></th>
      <td align="left">
        <form action="login.jsp?forumid=<mm:write referid="forumid" />" method="post">
          <mm:present referid="loginfailed">
            <br />
            <center>
              <h4><mm:write referid="mlg_login_failed" /></h4>
            </center>
            <center>
              <a href="<mm:url page="remail.jsp" referids="forumid" />"><mm:write referid="mlg_forgot_your_password" /></a>
            </center>
            <p />
          </mm:present>
          <mm:notpresent referid="loginfailed">
            <mm:field name="description" />
            <p /><b><mm:write referid="mlg_login" /></b><p />
          </mm:notpresent>
          <mm:write referid="mlg_account" /> : <input size="12" name="account">
          <mm:write referid="mlg_password" /> : <input size="12" type="password" name="password">
          <input type="submit" value="inloggen" />
        </form>
        <p />
      </mm:compare>
      <mm:compare referid="posterid" value="-1" inverse="true">
        <th width="100">
          <a href="profile.jsp?forumid=<mm:write referid="forumid" />&posterid=<mm:write referid="posterid" />">
            <mm:field name="active_account" /><br />
            <mm:field name="active_avatar">
              <mm:compare value="-1" inverse="true">
                <mm:node number="$_">
                  <img src="<mm:image template="s(80x80)" />" width="80" border="0">
                </mm:node>
              </mm:compare>
            </mm:field>
          </a>
          <a href="logout.jsp?forumid=<mm:write referid="forumid" />"><mm:write referid="mlg_Logout" /></a>
        </th>
        <td align="left" valign="top">
          <mm:compare referid="image_logo" value="" inverse="true">
            <br />
            <center>
              <img src="<mm:write referid="image_logo" />" width="98%">
            </center>
            <br />
          </mm:compare>
          <mm:compare referid="image_logo" value="">
            <h4><mm:write referid="mlg_Welcome" /> <mm:field name="active_firstname" /> <mm:field name="active_lastname" /> (<mm:field name="active_account" />) <br /> <mm:write referid="mlg_on_the" /> <mm:field name="name" /> <mm:write referid="mlg_forum" /> !</h4>
            <p />
          </mm:compare>

          <mm:write referid="mlg_last_time_logged_in" /> : 
          <mm:field name="active_lastseen">
            <mm:compare value="" inverse="true">
              <mm:field name="active_lastseen"><mm:time format="MMMM d, yyyy, HH:mm:ss" /></mm:field>
            </mm:compare>
          </mm:field>

          <br />
          <mm:write referid="mlg_member_since" /> : 
          <mm:field name="active_firstlogin">
            <mm:compare value="" inverse="true">
              <mm:field name="active_firstlogin"><mm:time format="MMMM d, yyyy, HH:mm:ss" /></mm:field>
            </mm:compare>
          </mm:field>
 
          <br />
          <mm:write referid="mlg_number_of_messages" /> : <mm:field name="active_postcount" /> 
          <mm:write referid="mlg_Level" /> : <mm:field name="active_level" />

          <p>
            <br />
            <mm:import id="mailboxid">Inbox</mm:import>
            <mm:nodefunction set="mmbob" name="getMailboxInfo" referids="forumid,posterid,mailboxid">
                <b><mm:write referid="mlg_you_have"/> <mm:field id="messagecount" name="messagecount" /> 
                <a href="<mm:url page="privatemessages.jsp" referids="forumid" />"> <mm:compare referid="messagecount" value="1"> <mm:write referid="mlg_private_message"/> </mm:compare><mm:compare referid="messagecount" value="1" inverse="true"> <mm:write referid="mlg_private_messages"/> </mm:compare></a> (<mm:field name="messagenewcount" /> <mm:write referid="mlg_new"/> <mm:write referid="mlg_and"/> <mm:field name="messageunreadcount" /> <mm:write referid="mlg_unread"/>) </b>
            </mm:nodefunction>

            <h4><mm:write referid="mlg_At_the_moment" /> : <mm:field id="postersonline" name="postersonline" /> <mm:compare referid="postersonline" value="1"> <mm:write referid="mlg_member" /> </mm:compare> <mm:compare referid="postersonline" value="1" inverse="true"><mm:write referid="mlg_members" /> </mm:compare> <mm:write referid="mlg_online" />.</h4>
          </p>
        </mm:compare>
      </td>
      <th width="250" align="left" valign="top">
        <b><mm:write referid="mlg_Areas" /></b> : <mm:field name="postareacount" /> <b><mm:write referid="mlg_Topics" /></b> : <mm:field name="postthreadcount" /><br />
        <b><mm:write referid="mlg_Messages" /></b> : <mm:field name="postcount" /> <b><mm:write referid="mlg_Views" /> </b> : <mm:field name="viewcount" /><br />
        <b><mm:write referid="mlg_Members" /></b> : <mm:field name="posterstotal" /> <b><mm:write referid="mlg_New" /></b> : <mm:field name="postersnew" /> <b><mm:write referid="mlg_Online"/></b> : <mm:field name="postersonline" /><p />
        <b><mm:write referid="mlg_Last_posting"/></b> : <mm:field name="lastposttime"><mm:compare value="-1" inverse="true"><mm:field name="lastposttime"><mm:time format="MMMM d, yyyy, HH:mm:ss" /></mm:field> <mm:write referid="mlg_by"/> <mm:field name="lastposter" /> '<mm:field name="lastsubject" />'</mm:compare><mm:compare value="-1"><mm:write referid="mlg_no_messages"/></mm:compare></mm:field>
      </th>
    </tr>
  </mm:nodefunction>
</table>

<table cellpadding="0" cellspacing="0" style="margin-top : 10px;" width="95%">
  <tr>
   <td align="right">
	<a href="<mm:url page="moderatorteam.jsp" referids="forumid" />"><mm:write referid="mlg_The_moderator_team" /></a> | <a href="<mm:url page="onlineposters.jsp" referids="forumid" />"><mm:write referid="mlg_Members_online" /> | <a href="<mm:url page="allposters.jsp" referids="forumid" />"><mm:write referid="mlg_All_members" /></a></a>
   </td>
  </tr>
</table>

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 2px;" width="95%">
   <tr><th><mm:write referid="mlg_area_name" /></th><th><mm:write referid="mlg_topics" /></th><th><mm:write referid="mlg_messages" /></th><th><mm:write referid="mlg_views" /></th><th><mm:write referid="mlg_last_posting" /></th></tr>
  		  <mm:nodelistfunction set="mmbob" name="getPostAreas" referids="forumid,posterid">
			<tr><td align="left"><a href="postarea.jsp?forumid=<mm:write referid="forumid" />&postareaid=<mm:field name="id" />"><mm:field name="name" /></a>
			<p/>
			<mm:field name="description" />
			<p />
			<mm:write referid="mlg_Moderators" /> : <mm:field name="moderators" />
			<p />
			 </td>
				<td><mm:field name="postthreadcount" /></td>
				<td><mm:field name="postcount" /></td>
				<td><mm:field name="viewcount" /></td>
				<td align="left" valign="top"><mm:field name="lastposttime"><mm:compare value="-1" inverse="true"><mm:field name="lastposttime"><mm:time format="MMMM d, yyyy, HH:mm:ss" /></mm:field> <mm:write referid="mlg_by" /> <a href="profile.jsp?forumid=<mm:write referid="forumid" />&posterid=<mm:field name="lastposternumber" />"><mm:field name="lastposter" /></a><p /><mm:field name="lastsubject" /></mm:compare><mm:compare value="-1"><mm:write referid="mlg_no_messages" /></mm:compare></mm:field></td>
			</tr>
		  </mm:nodelistfunction>
</table>
  <mm:compare referid="adminmode" value="true">
	<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="95%">
	<tr><th align="left"><mm:write referid="mlg_Admin_tasks" /></th></tr>
	<td>
	<p />
	<a href="<mm:url page="changeforum.jsp">
                  <mm:param name="forumid" value="$forumid" />
                 </mm:url>"><mm:write referid="mlg_change_forum" /></a><br />
	<a href="<mm:url page="newpostarea.jsp">
                  <mm:param name="forumid" value="$forumid" />
                 </mm:url>"><mm:write referid="mlg_add_new_area" /></a>
	<p />
	</td>
	</tr>
	</table>
  </mm:compare>
</mm:locale>
</mm:compare>

</div>                                                                                                                           
<div class="footer">
  <%@ include file="footer.jsp" %> 
</div> 

</body>
</html>

</mm:content>
</mm:cloud>
