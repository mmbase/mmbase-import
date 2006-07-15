<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
<mm:content type="text/html" encoding="UTF-8" escaper="entities" expires="0">
<mm:import externid="forumid" />
<mm:import externid="sub">info</mm:import>
<%@ include file="../thememanager/loadvars.jsp" %>


<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="actions.jsp" />
</mm:present>
<!-- end action check -->

<!-- login part -->
  <%@ include file="../getposterid.jsp" %>
<!-- end login part -->                                                                                                                      
<mm:locale language="$lang">
<%@ include file="../loadtranslations.jsp" %>

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
  </mm:nodefunction>
  <mm:include page="../path.jsp?type=subindex" referids="logoutmodetype,forumid,posterid,active_nick" />

<mm:nodefunction set="mmbob" name="getForumInfo" referids="forumid,posterid">
	<mm:import id="isadministrator"><mm:field name="isadministrator" /></mm:import>
</mm:nodefunction>

<mm:compare referid="isadministrator" value="true">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 30px;" width="75%" align="center">
	<tr>
	<mm:compare referid="sub" value="info">
	<th align="center"><a href="<mm:url page="changeforum.jsp" referids="forumid"><mm:param name="sub" value="info" /></mm:url>">info</a></th>
	<td align="center"><a href="<mm:url page="changeforum.jsp" referids="forumid"><mm:param name="sub" value="layout" /></mm:url>">layout</a></td>
	<td align="center"><a href="<mm:url page="changeforum.jsp" referids="forumid"><mm:param name="sub" value="theme" /></mm:url>">theme</a></td>
	<td align="center"><a href="<mm:url page="changeforum.jsp" referids="forumid"><mm:param name="sub" value="login" /></mm:url>">login</a></td>
	<td align="center"><a href="<mm:url page="changeforum.jsp" referids="forumid"><mm:param name="sub" value="rules" /></mm:url>">rules</a></td>
	<td align="center"><a href="<mm:url page="changeforum.jsp" referids="forumid"><mm:param name="sub" value="filter" /></mm:url>">filter</a></td>
	</mm:compare>
	<mm:compare referid="sub" value="layout">
	<td align="center"><a href="<mm:url page="changeforum.jsp" referids="forumid"><mm:param name="sub" value="info" /></mm:url>">info</a></td>
	<th align="center"><a href="<mm:url page="changeforum.jsp" referids="forumid"><mm:param name="sub" value="layout" /></mm:url>">layout</a></th>
	<td align="center"><a href="<mm:url page="changeforum.jsp" referids="forumid"><mm:param name="sub" value="theme" /></mm:url>">theme</a></td>
	<td align="center"><a href="<mm:url page="changeforum.jsp" referids="forumid"><mm:param name="sub" value="login" /></mm:url>">login</a></td>
	<td align="center"><a href="<mm:url page="changeforum.jsp" referids="forumid"><mm:param name="sub" value="rules" /></mm:url>">rules</a></td>
	<td align="center"><a href="<mm:url page="changeforum.jsp" referids="forumid"><mm:param name="sub" value="filter" /></mm:url>">filter</a></td>
	</mm:compare>
	<mm:compare referid="sub" value="theme">
	<td align="center"><a href="<mm:url page="changeforum.jsp" referids="forumid"><mm:param name="sub" value="info" /></mm:url>">info</a></td>
	<td align="center"><a href="<mm:url page="changeforum.jsp" referids="forumid"><mm:param name="sub" value="layout" /></mm:url>">layout</a></td>
	<th align="center"><a href="<mm:url page="changeforum.jsp" referids="forumid"><mm:param name="sub" value="theme" /></mm:url>">theme</a></th>
	<td align="center"><a href="<mm:url page="changeforum.jsp" referids="forumid"><mm:param name="sub" value="login" /></mm:url>">login</a></td>
	<td align="center"><a href="<mm:url page="changeforum.jsp" referids="forumid"><mm:param name="sub" value="rules" /></mm:url>">rules</a></td>
	<td align="center"><a href="<mm:url page="changeforum.jsp" referids="forumid"><mm:param name="sub" value="filter" /></mm:url>">filter</a></td>
	</mm:compare>
	<mm:compare referid="sub" value="login">
	<td align="center"><a href="<mm:url page="changeforum.jsp" referids="forumid"><mm:param name="sub" value="info" /></mm:url>">info</a></td>
	<td align="center"><a href="<mm:url page="changeforum.jsp" referids="forumid"><mm:param name="sub" value="layout" /></mm:url>">layout</a></td>
	<td align="center"><a href="<mm:url page="changeforum.jsp" referids="forumid"><mm:param name="sub" value="theme" /></mm:url>">theme</a></td>
	<th align="center" align="center"><a href="<mm:url page="changeforum.jsp" referids="forumid"><mm:param name="sub" value="login" /></mm:url>">login</a></th>
	<td align="center"><a href="<mm:url page="changeforum.jsp" referids="forumid"><mm:param name="sub" value="rules" /></mm:url>">rules</a></td>
	<td align="center"><a href="<mm:url page="changeforum.jsp" referids="forumid"><mm:param name="sub" value="filter" /></mm:url>">filter</a></td>
	</mm:compare>
	<mm:compare referid="sub" value="rules">
	<td align="center"><a href="<mm:url page="changeforum.jsp" referids="forumid"><mm:param name="sub" value="info" /></mm:url>">info</a></td>
	<td align="center"><a href="<mm:url page="changeforum.jsp" referids="forumid"><mm:param name="sub" value="layout" /></mm:url>">layout</a></td>
	<td align="center"><a href="<mm:url page="changeforum.jsp" referids="forumid"><mm:param name="sub" value="theme" /></mm:url>">theme</a></td>
	<td align="center"><a href="<mm:url page="changeforum.jsp" referids="forumid"><mm:param name="sub" value="login" /></mm:url>">login</a></td>
	<th align="center"><a href="<mm:url page="changeforum.jsp" referids="forumid"><mm:param name="sub" value="rules" /></mm:url>">rules</a></th>
	<td align="center"><a href="<mm:url page="changeforum.jsp" referids="forumid"><mm:param name="sub" value="filter" /></mm:url>">filter</a></td>
	</mm:compare>
	<mm:compare referid="sub" value="filter">
	<td align="center"><a href="<mm:url page="changeforum.jsp" referids="forumid"><mm:param name="sub" value="info" /></mm:url>">info</a></td>
	<td align="center"><a href="<mm:url page="changeforum.jsp" referids="forumid"><mm:param name="sub" value="layout" /></mm:url>">layout</a></td>
	<td align="center"><a href="<mm:url page="changeforum.jsp" referids="forumid"><mm:param name="sub" value="theme" /></mm:url>">theme</a></td>
	<td align="center"><a href="<mm:url page="changeforum.jsp" referids="forumid"><mm:param name="sub" value="login" /></mm:url>">login</a></td>
	<td align="center"><a href="<mm:url page="changeforum.jsp" referids="forumid"><mm:param name="sub" value="rules" /></mm:url>">rules</a></td>
	<th align="center"><a href="<mm:url page="changeforum.jsp" referids="forumid"><mm:param name="sub" value="filter" /></mm:url>">filter</a></th>
	</mm:compare>
	</tr>
</table>

<mm:compare referid="sub" value="info">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 25px;" width="55%" align="center">
  <tr><th colspan="3"><mm:write referid="mlg.Change_existing_forum" /></th></tr>

  <mm:node number="$forumid">
  <form action="<mm:url page="changeforum.jsp">
        <mm:param name="forumid" value="$forumid" />
				</mm:url>" method="post">
	<tr><th><mm:write referid="mlg.Name"/></th><td colspan="2">
	<input name="name" size="70" value="<mm:field name="name" />" style="width: 100%">
	</td></tr>
	<tr><th><mm:write referid="mlg.Language"/></th><td colspan="2" align="left">
	<mm:import id="tmpl"><mm:field name="language" /></mm:import>
	<select name="newlang">
	<mm:import id="tmpname">mmbob</mm:import>
	<mm:nodelistfunction set="mlg" name="getLanguagesInSet" referids="tmpname@setname">
	<mm:field name="name">
	<option <mm:compare referid2="tmpl">selected</mm:compare>><mm:field name="name" />
	</mm:field>
	</mm:nodelistfunction>
	</select>
	</td></tr>
	<tr><th><mm:write referid="mlg.Description"/></th><td colspan="2">
	<textarea name="description" rows="5" style="width: 100%"><mm:field name="description" /></textarea>
	</td></tr>
        <input type="hidden" name="admincheck" value="true">
	<input type="hidden" name="action" value="changeforum">
	<tr><th>&nbsp;</th>
<td align="center">
	<input type="submit" value="<mm:write referid="mlg.Save"/>">
  	</form>
	</td>
	</mm:node>
  	<form action="<mm:url page="changeforum.jsp">
        <mm:param name="forumid" value="$forumid" />
	</mm:url>"
 	method="post">
	<td align="center">
	<input type="submit" value="<mm:write referid="mlg.Cancel"/>">
	</td>
  	</form>
	</tr>
</table>
</mm:compare>


<mm:compare referid="sub" value="layout">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 25px;" width="55%" align="center">
  <tr><th colspan="3">Layout settings</th></tr>

  <form action="<mm:url page="changeforum.jsp" referids="forumid,sub" />" method="post" />
	<tr><th>Number of msg per page</th><td>
	<input name="forumpostingsperpage" value="<mm:function set="mmbob" name="getForumPostingsPerPage" referids="forumid,posterid" />" size="3">
	</td></tr>
	<tr><th>Postings overflow postarea</th><td>
	<input name="forumpostingsoverflowpostarea" value="<mm:function set="mmbob" name="getForumPostingsOverflowPostArea" referids="forumid,posterid" />" size="3">
	</td></tr>

	<tr><th>Postings overflow threadpage</th><td>
	<input name="forumpostingsoverflowthreadpage" value="<mm:function set="mmbob" name="getForumPostingsOverflowThreadPage" referids="forumid,posterid" />" size="3">
	</td></tr>

	<tr><th>Speedpost time</th><td>
	<input name="forumspeedposttime" value="<mm:function set="mmbob" name="getForumSpeedPostTime" referids="forumid,posterid" />" size="3">
	</td></tr>

	<tr><th>Reply on each page</th><td>
	<select name="forumreplyoneachpage">
		<mm:booleanfunction set="mmbob" name="getForumReplyOnEachPage" referids="forumid,posterid">
		<option value="true">True
		<option value="false">False
		</mm:booleanfunction>
		<mm:booleanfunction inverse="true" set="mmbob" name="getForumReplyOnEachPage" referids="forumid,posterid">
		<option value="false">False
		<option value="true">True
		</mm:booleanfunction>
	</select>
	</td></tr>

        <input type="hidden" name="admincheck" value="true">
	<input type="hidden" name="action" value="changelayout">
	<tr colspan="2">
<td align="center">
	<input type="submit" value="<mm:write referid="mlg.Save"/>">
  	</form>
	</td>
  	<form action="<mm:url page="changeforum.jsp">
        <mm:param name="forumid" value="$forumid" />
	</mm:url>"
 	method="post">
	<td align="center">
	<input type="submit" value="<mm:write referid="mlg.Cancel"/>">
	</td>
  	</form>
	</tr>
</table>
</mm:compare>


<mm:compare referid="sub" value="theme">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 25px;" width="55%" align="center">
  <tr><th colspan="3">Theme settings for <mm:write referid="themename" /> ( <mm:write referid="themeid" /> ) </th></tr>

	<tr><th width="20%">Background color</th><td>
	<mm:import id="sename" reset="true">default/body/background</mm:import><mm:include page="themeitemselect.jsp" referids="forumid,sub,themename,sename" />
	</td></tr>
	<tr><th>Font</th><td>
	<mm:import id="sename" reset="true">default/body/font-family</mm:import><mm:include page="themeitemselect.jsp" referids="forumid,sub,themename,sename" />
	</td></tr>
	<tr><th>Font Size</th><td>
	<mm:import id="sename" reset="true">default/body/font-size</mm:import><mm:include page="themeitemselect.jsp" referids="forumid,sub,themename,sename" />
	</td></tr>
	<tr><th>Font Color</th><td>
	<mm:import id="sename" reset="true">default/body/color</mm:import><mm:include page="themeitemselect.jsp" referids="forumid,sub,themename,sename" />
	</td></tr>
</table>
</mm:compare>

<mm:compare referid="sub" value="login">
<mm:nodefunction set="mmbob" name="getForumConfig" referids="forumid,posterid">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 25px;" width="55%" align="center">
  <tr><th colspan="3">Login instellingen</th></tr>
  <form action="<mm:url page="changeforum.jsp" referids="forumid,sub" />" method="post">
        <input type="hidden" name="admincheck" value="true">
	<input type="hidden" name="action" value="changeconfig">
	<mm:import id="name">loginsystem</mm:import>
	<mm:function set="mmbob" name="getGuiEdit" referids="forumid,name">
	<mm:compare value="false">
		<input type="hidden" name="loginsystemtype" value="fixed" />
	</mm:compare>
	<mm:compare value="true">
	<tr><th width="30%">Login System</th>
		<td colspan="2" align="left"><select name="loginsystemtype">
		<mm:field name="loginsystemtype">
		<mm:compare value="http">
		<option>http
		<option>entree
		<option>entree-ng
		<option>default
		</mm:compare>
		<mm:compare value="entree">
		<option>entree
		<option>entree-ng
		<option>http
		<option>default
		</mm:compare>
		<mm:compare value="default">
		<option>default
		<option>entree
		<option>entree-ng
		<option>default
		</mm:compare>
		<mm:compare value="entree-ng">
		<option>default
		<option>entree
		<option>entree-ng
		<option>default
		</mm:compare>
		</mm:field>
		</select>
		</td>
 	</tr>
	</mm:compare>
	</mm:function>

	<mm:import id="name" reset="true">loginmode</mm:import>
	<mm:function set="mmbob" name="getGuiEdit" referids="forumid,name">
	<mm:compare value="false">
		<input type="hidden" name="loginmodetype" value="fixed" />
	</mm:compare>
	<mm:compare value="true">
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
	</mm:compare>
	</mm:function>

	<mm:import id="name" reset="true">logoutmode</mm:import>
	<mm:function set="mmbob" name="getGuiEdit" referids="forumid,name">
	<mm:compare value="false">
		<input type="hidden" name="logoutmodetype" value="fixed" />
	</mm:compare>
	<mm:compare value="true">
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
	</mm:compare>
	</mm:function>

	<mm:import id="name" reset="true">guestreadmode</mm:import>
	<mm:function set="mmbob" name="getGuiEdit" referids="forumid,name">
	<mm:compare value="false">
		<input type="hidden" name="guestreadmodetype" value="fixed" />
	</mm:compare>
	<mm:compare value="true">
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
	</mm:compare>
	</mm:function>

	<mm:import id="name" reset="true">guestwritemode</mm:import>
	<mm:function set="mmbob" name="getGuiEdit" referids="forumid,name">
	<mm:compare value="false">
		<input type="hidden" name="guestwritemodetype" value="fixed" />
	</mm:compare>
	<mm:compare value="true">
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
	</mm:compare>
	</mm:function>

	<mm:import id="name" reset="true">avatarsupload</mm:import>
	<mm:function set="mmbob" name="getGuiEdit" referids="forumid,name">
	<mm:compare value="false">
		<input type="hidden" name="avatarsuploadenabled" value="fixed" />
	</mm:compare>
	<mm:compare value="true">
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
	</mm:compare>
	</mm:function>

	<mm:import id="name" reset="true">avatarsgallery</mm:import>
	<mm:function set="mmbob" name="getGuiEdit" referids="forumid,name">
	<mm:compare value="false">
		<input type="hidden" name="avatarsgalleryenabled" value="fixed" />
	</mm:compare>
	<mm:compare value="true">
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
	</mm:compare>
	</mm:function>

	<mm:import id="name" reset="true">navigationmethod</mm:import>
	<mm:function set="mmbob" name="getGuiEdit" referids="forumid,name">
	<mm:compare value="false">
		<input type="hidden" name="navigationmethod" value="fixed" />
	</mm:compare>
	<mm:compare value="true">
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
	</mm:compare>
	</mm:function>
	<tr><th>Url Alias</th>
		<td colspan="2" align="left"><input name="alias" value="<mm:field name="alias" />" size="15" />
		</td>
	</tr>
  <th>&nbsp;</th>
<td align="center" >
	<input type="submit" value="<mm:write referid="mlg.Save"/>">
  	</form>
	</td>
  	<form action="<mm:url page="changeforum.jsp">
        <mm:param name="forumid" value="$forumid" />
	</mm:url>"
 	method="post">
        <td align="center">
	<input type="submit" value="<mm:write referid="mlg.Cancel"/>">
  </form>
  </td>
  </tr>
</table>
</mm:nodefunction>
</mm:compare>


<mm:compare referid="sub" value="rules">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 25px;" width="55%" align="center">
  <tr><th colspan="3">Change forum rules</th></tr>

  <mm:node number="$forumid">
  <form action="<mm:url page="changeforum.jsp">
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
        <td align="center" >
        <input type="hidden" name="admincheck" value="true">
	<input type="submit" value="<mm:write referid="mlg.Save"/>">
  	</form>
	</td>
	</mm:node>
	<td align="center">
  	<form action="<mm:url page="changeforum.jsp">
        <mm:param name="forumid" value="$forumid" />
	</mm:url>"
 	method="post">
	<input type="submit" value="<mm:write referid="mlg.Cancel"/>">
	</td>
  	</form>
	</tr>
</table>
</mm:compare>

<mm:compare referid="sub" value="filter">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 25px;" width="55%" align="center">
  <tr><th colspan="4">Forum filter</th></tr>
    <mm:nodelistfunction set="mmbob" name="getFilterWords" referids="forumid">
    <form action="<mm:url page="changeforum.jsp" referids="forumid,sub" />" method="post">
    <input type="hidden" name="admincheck" value="true">
    <input type="hidden" name="action" value="addwordfilter">
    <tr>
    <th><mm:field name="name" />
       <input type="hidden" name="name" value="<mm:field name="name" />">
    </th>
    <td> 
       <input name="value" style="width: 98%" value="<mm:field name="value" />">
    </td>
    <td> 
	<input type="submit" value="<mm:write referid="mlg.Save"/>" />
    </td>
    </form>
    <form action="<mm:url page="changeforum.jsp" referids="forumid,sub" />" method="post">
    <input type="hidden" name="admincheck" value="true">
    <input type="hidden" name="action" value="removewordfilter">
       <input type="hidden" name="name" value="<mm:field name="name" />">
    <td> 
	<input type="submit" value="<mm:write referid="mlg.Delete"/>" />
    </td>
    </form>
    </tr>
   </mm:nodelistfunction>
   <form action="<mm:url page="changeforum.jsp" referids="forumid,sub" />" method="post">
   <tr>
        <input type="hidden" name="admincheck" value="true">
	<input type="hidden" name="action" value="addwordfilter">
	<th><input name="name" style="width: 98%"></th>
	<td><input name="value" style="width: 98%"></td>
        <td><input type="submit" value="<mm:write referid="mlg.Save"/>" /></td>
        <td>&nbsp;</td>
   </tr>
   </form>
</table>
</mm:compare>

</mm:compare>
<mm:compare referid="isadministrator" value="false">
	<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 40px;" width="55%" align="center">
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

