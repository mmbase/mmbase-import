<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
<mm:content type="text/html" encoding="UTF-8" escaper="entities" expires="0">
<mm:import externid="forumid" jspvar="forumid">unknown</mm:import>
<mm:compare referid="forumid" value="unknown">
		<mm:import id="key"><%= request.getHeader("host") %></mm:import>
		<mm:import id="forumalias"><mm:function set="mmbob" name="getForumAlias" referids="key" /></mm:import>
		<mm:compare referid="forumalias" value="unknown" inverse="true">
			<mm:import id="forumid" reset="true" jspvar="forumid"><mm:write referid="forumalias" /></mm:import>
		</mm:compare>
</mm:compare>
<%@ include file="thememanager/loadvars.jsp" %>
<html>
<head>
   <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
   <title>MMBob</title>
</head>
<body>

<mm:compare referid="forumid" value="unknown">
	<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 40px;" width="75%" align="center">
		<tr><th>MMBob system error <mm:write referid="forumalias" /></th></tr>
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
    <mm:import id="headerpath" jspvar="headerpath"><mm:function set="mmbob" name="getForumHeaderPath" referids="forumid"/></mm:import>
    <jsp:include page="<%=headerpath%>"/>
</div>

<div class="bodypart">

  <mm:nodefunction set="mmbob" name="getForumInfo" referids="forumid,posterid">
  <mm:import id="loginsystemtype"><mm:field name="loginsystemtype" /></mm:import>
  <mm:import id="logoutmodetype"><mm:field name="logoutmodetype" /></mm:import>
  <mm:import id="navigationmethod"><mm:field name="navigationmethod" /></mm:import>
  <mm:import id="active_nick"><mm:field name="active_nick" /></mm:import>
  <mm:include page="path.jsp?type=index" referids="logoutmodetype,forumid,posterid,active_nick" />

  <table cellpadding="0" cellspacing="0" class="list"  style="margin-top : 10px;" width="95%">
      <mm:import id="adminmode"><mm:field name="isadministrator" /></mm:import>
      <tr>
      <mm:compare referid="posterid" value="-1">
     <mm:compare referid="loginsystemtype" value="http">
        <th width="100"><mm:field name="accountcreationtype"><mm:compare value="open"><a href="newposter.jsp?forumid=<mm:write referid="forumid" />"><mm:write referid="image_guest"/></a></mm:compare></mm:field></th>
      <td align="left">
        <form action="login.jsp?forumid=<mm:write referid="forumid" />" method="post">
          <mm:present referid="loginfailed">
            <br />
            <center>
              <h4>
               <mm:write referid="loginfailedreason">
                 <mm:compare value="account blocked">
                   ** <mm:write referid="mlg.Account_disabled"/> **
                 </mm:compare>
                 <mm:compare value="account not valid">
                   ** <mm:write referid="mlg.Account_not_found" /> **
                 </mm:compare>
                 <mm:compare value="password not valid">
                   ** <mm:write referid="mlg.Wrong_password" /> **
                 </mm:compare>
               </mm:write>
              </h4>
            </center>
            </mm:present>

          <mm:notpresent referid="loginfailed">
            <h4><mm:write referid="mlg.Welcome" /> <mm:write referid="mlg.on_the" /> <mm:field name="name" /> <mm:write referid="mlg.forum" /> !</h4>
            <p /><b><mm:write referid="mlg.login" /></b><p />
          </mm:notpresent>
            <center>
              <a href="<mm:url page="remail.jsp" referids="forumid" />"><mm:write referid="mlg.forgot_your_password" /></a>
            </center>
            <p />
          <mm:write referid="mlg.account" /> : <input size="12" name="account">
          <mm:write referid="mlg.password" /> : <input size="12" type="password" name="password">
          <input type="submit" value="<mm:write referid="mlg.login"/>" />
        </form>
        <p />
      </mm:compare>
     <mm:compare referid="loginsystemtype" value="entree">
        <th width="100">
	</th>
      <td align="left">
        <form action="entree.jsp?forumid=<mm:write referid="forumid" />" method="post">
          <mm:present referid="loginfailed">
            <br />
            <center>
              <h4>
               <mm:write referid="loginfailedreason">
                 <mm:compare value="account blocked">
                   ** <mm:write referid="mlg.Account_disabled"/> **
                 </mm:compare>
                 <mm:compare value="account not valid">
                   <center> ** Geen lid van dit forum ** </center>
                 </mm:compare>
                 <mm:compare value="password not valid">
                   ** <mm:write referid="mlg.Wrong_password" /> **
                 </mm:compare>
               </mm:write>
              </h4>
            </center>
            </mm:present>

          <mm:notpresent referid="loginfailed">
          </mm:notpresent>
          <center><input type="submit" value="Inloggen via Entree" /></center>
        </form>
        <p />
      </mm:compare>

      </mm:compare>
      <mm:compare referid="posterid" value="-1" inverse="true">
        <th width="100">
          <a href="profile.jsp?forumid=<mm:write referid="forumid" />&posterid=<mm:write referid="posterid" />">
            <mm:field name="active_nick" /><br />
            <mm:field name="active_avatar">
              <mm:compare value="-1" inverse="true">
                <mm:node number="$_">
                  <img src="<mm:image template="s(80x80)" />" width="80" border="0">
                </mm:node>
              </mm:compare>
            </mm:field>
          </a>
	  <mm:compare referid="logoutmodetype" value="open">
          <a href="logout.jsp?forumid=<mm:write referid="forumid" />"><mm:write referid="mlg.Logout" /></a>
	  </mm:compare>
        </th>
        <td align="left" valign="top">
          <mm:compare referid="image_logo" value="" inverse="true">
            <center>
              <img src="<mm:write referid="image_logo" />" width="98%">
            </center>
          </mm:compare>
          <mm:compare referid="image_logo" value="">
            <h4><mm:write referid="mlg.Welcome" /> <mm:field name="active_firstname" /> <mm:field name="active_lastname" /> (<mm:field name="active_nick" />) <br /> <mm:write referid="mlg.on_the" /> <mm:field name="name" /> <mm:write referid="mlg.forum" /> !</h4>
            <p />
          </mm:compare>

          <mm:write referid="mlg.last_time_logged_in" /> : 
          <mm:field name="active_lastseen">
            <mm:compare value="" inverse="true">
              <mm:field name="active_lastseen"><mm:time format="MMMM d, yyyy, HH:mm:ss" /></mm:field>
            </mm:compare>
          </mm:field>

          <br />
          <mm:write referid="mlg.member_since" /> : 
          <mm:field name="active_firstlogin">
            <mm:compare value="" inverse="true">
              <mm:field name="active_firstlogin"><mm:time format="MMMM d, yyyy, HH:mm:ss" /></mm:field>
            </mm:compare>
          </mm:field>
 
          <br />
          <mm:write referid="mlg.number_of_messages" /> : <mm:field name="active_postcount" /> 
          <%-- TODO: not yet implemented 
          <mm:write referid="mlg.Level" /> : <mm:field name="active_level" />--%>

          <p>
            <br />
            <mm:import id="mailboxid">Inbox</mm:import>
            <mm:field name="privatemessagesenabled"><mm:compare value="true">
            <mm:nodefunction set="mmbob" name="getMailboxInfo" referids="forumid,posterid,mailboxid">
                <b><mm:write referid="mlg.you_have"/> 
                <mm:field name="messagecount">
                  <mm:compare value="">0 <a href="<mm:url page="privatemessages.jsp" referids="forumid" />"><mm:write referid="mlg.private_messages"/></a></mm:compare>
                  <mm:compare value="" inverse="true">
                    <mm:field id="messagecount" name="messagecount" /> 
                    <a href="<mm:url page="privatemessages.jsp" referids="forumid" />"> <mm:compare referid="messagecount" value="1"> <mm:write referid="mlg.private_message"/> </mm:compare><mm:compare referid="messagecount" value="1" inverse="true"> <mm:write referid="mlg.private_messages"/> </mm:compare></a> (<mm:field name="messagenewcount" /> <mm:write referid="mlg.new"/> <mm:write referid="mlg.and"/> <mm:field name="messageunreadcount" /> <mm:write referid="mlg.unread"/>) </b>
                  </mm:compare>
                </mm:field>
            </mm:nodefunction>
	    </mm:compare></mm:field>
            <h4><mm:write referid="mlg.At_the_moment" /> : <mm:field id="postersonline" name="postersonline" /> <mm:compare referid="postersonline" value="1"> <mm:write referid="mlg.member" /> </mm:compare> <mm:compare referid="postersonline" value="1" inverse="true"><mm:write referid="mlg.members" /> </mm:compare> <mm:write referid="mlg.online" />.</h4>
          </p>
        </mm:compare>
      </td>
      <th width="250" align="left" valign="top">
        <b><mm:write referid="mlg.Areas" /></b> : <mm:field name="postareacount" /> <b><mm:write referid="mlg.Topics" /></b> : <mm:field name="postthreadcount" /><br />
        <b><mm:write referid="mlg.Messages" /></b> : <mm:field name="postcount" /> <b><mm:write referid="mlg.Views" /> </b> : <mm:field name="viewcount" /><br />
        <b><mm:write referid="mlg.Members" /></b> : <mm:field name="posterstotal" /> <b><mm:write referid="mlg.New" /></b> : <mm:field name="postersnew" /> <b><mm:write referid="mlg.Online"/></b> : <mm:field name="postersonline" /><p />
        <b><mm:write referid="mlg.Last_posting"/></b> : <mm:field name="lastposttime"><mm:compare value="-1" inverse="true"><mm:field name="lastposttime"><mm:time format="MMMM d, yyyy, HH:mm:ss" /></mm:field> <mm:write referid="mlg.by"/> <mm:field name="lastposter" /> '<mm:field name="lastsubject" />'</mm:compare><mm:compare value="-1"><mm:write referid="mlg.no_messages"/></mm:compare></mm:field>
      </th>
    </tr>
  </mm:nodefunction>
</table>

<table cellpadding="0" cellspacing="0" style="margin-top : 10px;" width="95%">
  <tr>
   <td align="right">
        <mm:compare referid="posterid" value="-1" inverse="true"><a href="<mm:url page="profile.jsp" referids="forumid,posterid" />"><mm:write referid="mlg.Profile_settings" /> |</mm:compare>
	<mm:node referid="forumid"><mm:relatednodes type="forumrules"><a href="<mm:url page="rules.jsp" referids="forumid"><mm:param name="rulesid"><mm:field name="number" /></mm:param></mm:url>"><mm:write referid="mlg.Forum_rules" /> | </a></mm:relatednodes></mm:node><a href="<mm:url page="moderatorteam.jsp" referids="forumid" />"><mm:write referid="mlg.The_moderator_team" /></a> | <a href="<mm:url page="onlineposters.jsp" referids="forumid" />"><mm:write referid="mlg.Members_online" /> | <a href="<mm:url page="allposters.jsp" referids="forumid" />"><mm:write referid="mlg.All_members" /></a> | <a href="<mm:url page="bookmarked.jsp" referids="forumid" />">Bookmarked</a> | <a href="<mm:url page="search.jsp" referids="forumid" />"><mm:write referid="mlg.Search" /></a>
   </td>
  </tr>
</table>

<mm:compare referid="navigationmethod" value="tree">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 2px;" width="95%">
   <tr><th><mm:write referid="mlg.area_name" /></th><th><mm:write referid="mlg.topics" /></th><th><mm:write referid="mlg.messages" /></th><th><mm:write referid="mlg.views" /></th><th><mm:write referid="mlg.last_posting" /></th></tr>
    <mm:import externid="tree">root</mm:import>
    <mm:compare referid="tree" value="root" inverse="true">
    <tr><th colspan="5" align="left" height="25"><mm:write referid="tree" /></th></tr>
    </mm:compare>
    <mm:nodelistfunction set="mmbob" name="getTreePostAreas" referids="forumid,posterid,tree">
			<mm:field name="nodetype">
			<mm:compare value="area">
		        <mm:import id="guestreadmodetype" reset="true"><mm:field name="guestreadmodetype" /></mm:import>
		        <mm:compare referid="posterid" value="-1" inverse="true">
		        <mm:import id="guestreadmodetype" reset="true">open</mm:import>  </mm:compare>
			<mm:compare referid="guestreadmodetype" value="open">
			<tr><td align="left"><a href="postarea.jsp?forumid=<mm:write referid="forumid" />&postareaid=<mm:field name="id" />"><mm:field name="shortname" /></a>
			<p/>
			<mm:field name="description" />
			<p />
			<mm:write referid="mlg.Moderators" /> : <mm:field name="moderators" />
			<p />
			 </td>
				<td><mm:field name="postthreadcount" /></td>
				<td><mm:field name="postcount" /></td>
				<td><mm:field name="viewcount" /></td>
				<td align="left" valign="top"><mm:field name="lastposttime"><mm:compare value="-1" inverse="true"><mm:field name="lastposttime"><mm:time format="MMMM d, yyyy, HH:mm:ss" /></mm:field> <mm:write referid="mlg.by" />  
  <mm:field name="lastposternumber">
    <mm:compare value="-1" inverse="true">
       <a href="profile.jsp?forumid=<mm:write referid="forumid" />&posterid=<mm:field name="lastposternumber" />"><mm:field name="lastposter" /></a>
    </mm:compare>
    <mm:compare value="-1" ><mm:field name="lastposter" /></mm:compare>
  </mm:field>
  <p /><mm:field name="lastsubject" /></mm:compare><mm:compare value="-1"><mm:write referid="mlg.no_messages" /></mm:compare></mm:field> <mm:field name="lastpostthreadnumber"><mm:compare value="-1" inverse="true"><a href="thread.jsp?forumid=<mm:write referid="forumid" />&postareaid=<mm:field name="id" />&postthreadid=<mm:field name="lastpostthreadnumber" />&page=-1#reply">></a></mm:compare></mm:field></td>
			</tr>
			</mm:compare>
			</mm:compare>
			<mm:compare value="subarea">
			<tr>
				<th colspan="5" align="left" height="25"><a href="<mm:url page="index.jsp" referids="forumid"><mm:param name="tree"><mm:field name="name" /></mm:param></mm:url>"><mm:field name="name" /></a> &nbsp;&nbsp;&nbsp;( <mm:field name="areacount" /> <mm:write referid="mlg.Areas" />, <mm:field name="postthreadcount" /> <mm:write referid="mlg.topics" />, <mm:field name="postcount" /> <mm:write referid="mlg.messages" />, <mm:field name="viewcount" /> <mm:write referid="mlg.views" /> )</th>
			</tr>
			</mm:compare>
			</mm:field>
    </mm:nodelistfunction>
</table>
</mm:compare>
<mm:compare referid="navigationmethod" value="list">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 2px;" width="95%">
   <tr><th><mm:write referid="mlg.area_name" /></th><th><mm:write referid="mlg.topics" /></th><th><mm:write referid="mlg.messages" /></th><th><mm:write referid="mlg.views" /></th><th><mm:write referid="mlg.last_posting" /></th></tr>
  		  <mm:nodelistfunction set="mmbob" name="getPostAreas" referids="forumid,posterid">
		        <mm:import id="guestreadmodetype" reset="true"><mm:field name="guestreadmodetype" /></mm:import>
		        <mm:compare referid="posterid" value="-1" inverse="true">
		        <mm:import id="guestreadmodetype" reset="true">open</mm:import>  </mm:compare>
			<mm:compare referid="guestreadmodetype" value="open">
			<tr><td align="left"><a href="postarea.jsp?forumid=<mm:write referid="forumid" />&postareaid=<mm:field name="id" />"><mm:field name="name" /></a>
			<p/>
			<mm:field name="description" />
			<p />
			<mm:write referid="mlg.Moderators" /> : <mm:field name="moderators" />
			<p />
			 </td>
				<td><mm:field name="postthreadcount" /></td>
				<td><mm:field name="postcount" /></td>
				<td><mm:field name="viewcount" /></td>
				<td align="left" valign="top"><mm:field name="lastposttime"><mm:compare value="-1" inverse="true"><mm:field name="lastposttime"><mm:time format="MMMM d, yyyy, HH:mm:ss" /></mm:field> <mm:write referid="mlg.by" />  
  <mm:field name="lastposternumber">
    <mm:compare value="-1" inverse="true">
       <a href="profile.jsp?forumid=<mm:write referid="forumid" />&posterid=<mm:field name="lastposternumber" />"><mm:field name="lastposter" /></a>
    </mm:compare>
    <mm:compare value="-1" ><mm:field name="lastposter" /></mm:compare>
  </mm:field>
  <p /><mm:field name="lastsubject" /></mm:compare><mm:compare value="-1"><mm:write referid="mlg.no_messages" /></mm:compare></mm:field> <mm:field name="lastpostthreadnumber"><mm:compare value="-1" inverse="true"><a href="thread.jsp?forumid=<mm:write referid="forumid" />&postareaid=<mm:field name="id" />&postthreadid=<mm:field name="lastpostthreadnumber" />&page=-1#reply">></a></mm:compare></mm:field></td>
			</tr>
			</mm:compare>
		  </mm:nodelistfunction>
</table>
</mm:compare>

  <mm:compare referid="adminmode" value="true">
	<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="95%">
	<tr><th align="left"><mm:write referid="mlg.Admin_tasks" /></th></tr>
	<td align="left">
	<p />
	<a href="<mm:url page="admin/changeforum.jsp">
                  <mm:param name="forumid" value="$forumid" />
                 </mm:url>"><mm:write referid="mlg.change_forum" /></a><br />
  		<a href="<mm:url page="admin/newadministrator.jsp">
		<mm:param name="forumid" value="$forumid" />
		</mm:url>">Add administrator</a><br />
  		<a href="<mm:url page="admin/removeadministrator.jsp">
		<mm:param name="forumid" value="$forumid" />
		</mm:url>">Remove administrator</a><br />
	<a href="<mm:url page="admin/newpostarea.jsp">
                  <mm:param name="forumid" value="$forumid" />
                 </mm:url>"><mm:write referid="mlg.add_new_area" /></a>
	<p />
	</td>
	</tr>
	</table>
  </mm:compare>

</div>                                                                                                                           
<div class="footer">
    <mm:import id="footerpath" jspvar="footerpath"><mm:function set="mmbob" name="getForumFooterPath" referids="forumid"/></mm:import>
    <jsp:include page="<%=footerpath%>"/>
</div> 

</mm:locale>
</mm:compare>

</body>
</html>

</mm:content>
</mm:cloud>
