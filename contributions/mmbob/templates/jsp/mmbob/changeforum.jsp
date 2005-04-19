<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
<mm:content type="text/html" encoding="UTF-8" escaper="entities" expires="0">
<%@ include file="thememanager/loadvars.jsp" %>

<mm:import externid="forumid" />

<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="actions.jsp" />
</mm:present>
<!-- end action check -->

<!-- login part -->
  <%@ include file="getposterid.jsp" %>
<!-- end login part -->                                                                                                                      
<mm:locale language="$lang">
<%@ include file="loadtranslations.jsp" %>

<html>
<head>
   <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
   <title>MMBob</title>
</head>
<body>

<div class="header">
</div>
                                                                                              
<div class="bodypart">

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="75%">
  <tr><th colspan="3"><mm:write referid="mlg.Change_existing_forum" /></th></tr>

  <mm:node number="$forumid">
  <form action="<mm:url page="index.jsp">
        <mm:param name="forumid" value="$forumid" />
				</mm:url>" method="post">
	<tr><th><mm:write referid="mlg.Name"/></th><td colspan="2">
	<input name="name" size="70" value="<mm:field name="name" />" style="width: 100%">
	</td></tr>
	<tr><th><mm:write referid="mlg.Language"/></th><td colspan="2">
	<input name="newlang" size="2" value="<mm:field name="language" />" >
	</td></tr>
	<tr><th><mm:write referid="mlg.Description"/></th><td colspan="2">
	<textarea name="description" rows="5" style="width: 100%"><mm:field name="description" /></textarea>
	</td></tr>
        <input type="hidden" name="admincheck" value="true">
	<input type="hidden" name="action" value="changeforum">
	<tr><th>&nbsp;</th>
<td align="middle" >
	<input type="submit" value="<mm:write referid="mlg.Save"/>">
  	</form>
	</td>
	</mm:node>
	<td>
  	<form action="<mm:url page="index.jsp">
        <mm:param name="forumid" value="$forumid" />
	</mm:url>"
 	method="post">
	<p />
	<input type="submit" value="<mm:write referid="mlg.Cancel"/>">
  	</form>
	</td>
	</tr>
</table>
</div>

<mm:nodefunction set="mmbob" name="getForumConfig" referids="forumid,posterid">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="55%">
  <tr><th colspan="3">Login instellingen</th></tr>
  <form action="<mm:url page="index.jsp">
        <mm:param name="forumid" value="$forumid" />
				</mm:url>" method="post">
        <input type="hidden" name="admincheck" value="true">
	<input type="hidden" name="action" value="changeconfig">
	<tr><th>Login Mode</th>
		<td colspan="2"><select name="loginmodetype">
		<mm:field name="loginmodetype">
		<mm:compare value="open">
		<option>open 
		<option>closed
		<option>default
		</mm:compare>
		<mm:compare value="closed">
		<option>closed
		<option>open 
		<option>default
		</mm:compare>
		<mm:compare value="default">
		<option>default
		<option>closed
		<option>open 
		</mm:compare>
		</mm:field>
		</td>
 	</tr>
	<tr><th>LogoutMode</th>
		<td colspan="2"><select name="logoutmodetype">
		<mm:field name="logoutmodetype">
		<mm:compare value="open">
		<option>open 
		<option>closed
		<option>default
		</mm:compare>
		<mm:compare value="closed">
		<option>closed
		<option>open 
		<option>default
		</mm:compare>
		<mm:compare value="default">
		<option>default
		<option>closed
		<option>open 
		</mm:compare>
		</mm:field>
		</td>
 	</tr>
	<tr><th>GuestReadMode</th>
		<td colspan="2"><select name="guestreadmodetype">
		<mm:field name="guestreadmodetype">
		<mm:compare value="open">
		<option>open 
		<option>closed
		<option>default
		</mm:compare>
		<mm:compare value="closed">
		<option>closed
		<option>open 
		<option>default
		</mm:compare>
		<mm:compare value="default">
		<option>default
		<option>closed
		<option>open 
		</mm:compare>
		</mm:field>
		</td>
 	</tr>
	<tr><th>GuestWriteMode</th>
		<td colspan="2"><select name="guestwritemodetype">
		<mm:field name="guestwritemodetype">
		<mm:compare value="open">
		<option>open 
		<option>closed
		<option>default
		</mm:compare>
		<mm:compare value="closed">
		<option>closed
		<option>open 
		<option>default
		</mm:compare>
		<mm:compare value="default">
		<option>default
		<option>closed
		<option>open 
		</mm:compare>
		</mm:field>
		</td>
 	</tr>
	<tr><th>AvatarUpload</th>
		<td colspan="2"><select name="avatarsuploadenabled">
		<mm:field name="avatarsuploadenabled">
		<mm:compare value="true">
		<option value="true">on
		<option value="false">off
		<option value="default">default
		</mm:compare>
		<mm:compare value="false">
		<option value="false">off
		<option value="true">on
		<option value="default">default
		</mm:compare>
		<mm:compare value="default">
		<option value="default">default
		<option value="false">off
		<option value="true">on
		</mm:compare>
		</mm:field>
		</td>
 	</tr>
	<tr><th>AvatarGallery</th>
		<td colspan="2"><select name="avatarsgalleryenabled">
		<mm:field name="avatarsgalleryenabled">
		<mm:compare value="true">
		<option value="true">on
		<option value="false">off
		<option value="default">default
		</mm:compare>
		<mm:compare value="false">
		<option value="false">off
		<option value="true">on
		<option value="default">default
		</mm:compare>
		<mm:compare value="default">
		<option value="default">default
		<option value="false">off
		<option value="true">on
		</mm:compare>
		</mm:field>
		</td>
 	</tr>
  <th>&nbsp;</th>
<td align="middle" >
	<input type="submit" value="<mm:write referid="mlg.Save"/>">
  	</form>
	</td>
  <td>
  	<form action="<mm:url page="index.jsp">
        <mm:param name="forumid" value="$forumid" />
	</mm:url>"
 	method="post">
	<p />
	<input type="submit" value="<mm:write referid="mlg.Cancel"/>">
  </form>
  </td>
  </tr>
</table>
</mm:nodefunction>

<div class="footer">
</div>
                                                                                              
</body>
</html>

</mm:locale>
</mm:content>
</mm:cloud>

