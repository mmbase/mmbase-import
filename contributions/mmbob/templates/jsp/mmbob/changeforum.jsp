<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
<mm:content type="text/html" encoding="UTF-8" escaper="entities" expires="0">
<mm:import externid="forumid" />
<%@ include file="thememanager/loadvars.jsp" %>


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
    <mm:import id="headerpath" jspvar="headerpath"><mm:function set="mmbob" name="getForumHeaderPath" referids="forumid"/></mm:import>
    <jsp:include page="<%=headerpath%>"/>
</div>
                                                                                              
<div class="bodypart">

<mm:nodefunction set="mmbob" name="getForumInfo" referids="forumid,posterid">
	<mm:import id="isadministrator"><mm:field name="isadministrator" /></mm:import>
</mm:nodefunction>

<mm:compare referid="isadministrator" value="true">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="75%">
  <tr><th colspan="3"><mm:write referid="mlg.Change_existing_forum" /></th></tr>

  <mm:node number="$forumid">
  <form action="<mm:url page="index.jsp">
        <mm:param name="forumid" value="$forumid" />
				</mm:url>" method="post">
	<tr><th><mm:write referid="mlg.Name"/></th><td colspan="2">
	<input name="name" size="70" value="<mm:field name="name" />" style="width: 100%">
	</td></tr>
	<tr><th><mm:write referid="mlg.Language"/></th><td colspan="2" align="left">
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

<mm:nodefunction set="mmbob" name="getForumConfig" referids="forumid,posterid">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="55%">
  <tr><th colspan="3">Login instellingen</th></tr>
  <form action="<mm:url page="index.jsp">
        <mm:param name="forumid" value="$forumid" />
				</mm:url>" method="post">
        <input type="hidden" name="admincheck" value="true">
	<input type="hidden" name="action" value="changeconfig">
	<tr><th width="30%">Login Mode</th>
		<td colspan="2" align="left"><select name="loginmodetype">
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
		</select>
		</td>
 	</tr>
	<tr><th>LogoutMode</th>
		<td colspan="2" align="left"><select name="logoutmodetype">
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
		</select>
		</td>
 	</tr>
	<tr><th>GuestReadMode</th>
		<td colspan="2" align="left"><select name="guestreadmodetype">
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
		</select>
		</td>
 	</tr>
	<tr><th>GuestWriteMode</th>
		<td colspan="2" align="left"><select name="guestwritemodetype">
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
		</select>
		</td>
 	</tr>
	<tr><th>AvatarUpload</th>
		<td colspan="2" align="left"><select name="avatarsuploadenabled">
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
		</select>
		</td>
 	</tr>
	<tr><th>AvatarGallery</th>
		<td colspan="2" align="left"><select name="avatarsgalleryenabled">
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
		</select>
		</td>
 	</tr>

	<tr><th>Navigation Method</th>
		<td colspan="2" align="left"><select name="navigationmethod">
		<mm:field name="navigationmethod">
		<mm:compare value="list">
		<option value="list">list
		<option value="tree">tree
		</mm:compare>
		<mm:compare value="tree">
		<option value="tree">tree
		<option value="list">list
		</mm:compare>
		</mm:field>
		</select>
		</td>
 	</tr>
	<tr><th>Url Alias</th>
		<td colspan="2" align="left"><input name="alias" value="<mm:field name="alias" />" size="15" />
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


<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="75%">
  <tr><th colspan="3">Change forum rules</th></tr>

  <mm:node number="$forumid">
  <form action="<mm:url page="index.jsp">
        <mm:param name="forumid" value="$forumid" />
				</mm:url>" method="post">
	<mm:import id="rulesid">-1</mm:import>
	<mm:relatednodes type="forumrules">
		<mm:import id="rulesid" reset="true"><mm:field name="number" /></mm:import>
	<tr><th>Title</th><td colspan="2"><input name="title" value="<mm:field name="title" />" size="70" style="width: 100%"></td></tr>
	<tr><th>Rules</th><td colspan="2">
	<textarea name="body" rows="15" style="width: 100%"><mm:field name="body" /></textarea>
	</td></tr>
	<input type="hidden" name="rulesid" value="<mm:write referid="rulesid"/>" />
	<input type="hidden" name="action" value="changerules" />
	<tr><th>&nbsp;</th>
	</mm:relatednodes>
	<mm:compare referid="rulesid" value="-1">
	<tr><th>Title</th><td colspan="2"><input name="title" value="" size="70" style="width: 100%"></td></tr>
	<tr><th>Rules</th><td colspan="2">
	<textarea name="body" rows="15" cols="70"></textarea>
	</td></tr>
	<input type="hidden" name="rulesid" value="<mm:write referid="rulesid"/>" />
	<input type="hidden" name="action" value="addrules" />
	<tr><th>&nbsp;</th>
	</mm:compare>
        <td align="middle" >
        <input type="hidden" name="admincheck" value="true">
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
</mm:compare>
<mm:compare referid="isadministrator" value="false">
	<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 40px;" width="75%" align="center">
		<tr><th>MMBob system error</th></tr>
		<tr><td height="40"><b>ERROR: </b> action not allowed by this user </td></tr>
	</table>
</mm:compare>

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

