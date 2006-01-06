<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud name="mmbase" method="http" rank="administrator">
<mm:import externid="forumid" jspvar="forumid">unknown</mm:import>
<%@ include file="thememanager/loadvars.jsp" %>
<HTML>
<HEAD>
   <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
   <title>MMBob</title>
</head>

<mm:import externid="action" />
<mm:present referid="action">
<mm:compare value="newforum" referid="action">
	<mm:import externid="name" />
	<mm:import externid="description" />
	<mm:import externid="language" />
	<mm:import id="newaccount" externid="account" />
	<mm:import id="newpassword" externid="password" />
	<mm:import externid="email" />
	<mm:nodefunction set="mmbob" name="newForum" referids="name,language,description,newaccount@account,newpassword@password,email">
	</mm:nodefunction>
</mm:compare>


<mm:compare value="changeconfigs" referid="action">
	<mm:import externid="loginmodetype" />
	<mm:import externid="logoutmodetype" />
	<mm:import externid="guestreadmodetype" />
	<mm:import externid="guestwritemodetype" />
	<mm:import externid="avatarsuploadenabled" />
	<mm:import externid="avatarsgalleryenabled" />
	<mm:import externid="contactinfoenabled" />
	<mm:import externid="smileysenabled" />
	<mm:import externid="privatemessagesenabled" />
	<mm:import externid="postingsperpage" />
	<mm:booleanfunction set="mmbob" name="changeForumsConfig" referids="loginmodetype,logoutmodetype,guestreadmodetype,guestwritemodetype,avatarsuploadenabled,avatarsgalleryenabled,contactinfoenabled,smileysenabled,privatemessagesenabled,postingsperpage" >
	</mm:booleanfunction>
</mm:compare>

<mm:compare value="removeforum" referid="action">
	<mm:import externid="remforum" />
	<mm:booleanfunction set="mmbob" name="removeForum" referids="remforum">
	</mm:booleanfunction>
</mm:compare>

</mm:present>
<!-- end action check -->

<body>

<div class="header">
</div>
                                                                                                              
<div class="bodypart">

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="95%" align="center">
   <tr><th>Forum naam</th><th>berichten</th><th>views</th><th>gebruikers</th><th>threadsloaded</th><th>memory size</th></tr>
  <mm:nodelistfunction set="mmbob" name="getForums">
			<tr>
			<td align="left"><a href="index.jsp?forumid=<mm:field name="id" />"><mm:field name="name" /></a></td>
			<td><mm:field name="postcount" /></td>
			<td><mm:field name="viewcount" /></td>
			<td><mm:field name="posterstotal" /></td>
			<td><mm:field name="postthreadloadedcount" /></td>
                        <td><mm:field name="memorysize" /></td>
			</tr>
  </mm:nodelistfunction>
</table>
	<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="95%" align="center">
	<tr><th align="left">Administratie Functies</th></tr>
	<td align="left">
	<p />
	<a href="<mm:url page="newforum.jsp"></mm:url>">Forum toevoegen</a><br />
	<a href="<mm:url page="removeforum.jsp"></mm:url>">Forum verwijderen</a><br />
	<a href="<mm:url page="changeforums.jsp"></mm:url>">Forums settings</a><br />
	<a href="<mm:url page="generate/index.jsp"></mm:url>">Generate tools</a><br />
	<a href="<mm:url page="stats.jsp"></mm:url>">Forums statistics</a><br />
	<p />
	</td>
	</tr>
	</table>
</mm:cloud>
</div>
                                                                                                              
<div class="footer">
</div>

</body>
</html>
