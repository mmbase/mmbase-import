<% String title = "Log in"; %>
<%@ include file="inc_top.jsp" %>
<?xml version="1.0" encoding="iso-8859-1"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/2000/REC-xhtml1-20000126/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<title>my_editors - <%= title %></title>
	<link rel="stylesheet" href="my_editors.css" type="text/css" />
	<link href="img/favicon.ico" rel="icon" type="image/x-icon">
	<link href="img/favicon.ico" rel="shortcut icon" type="image/x-icon">
</head>
<body bgcolor="#FFFFFF">
<div style="margin-left: 50px; margin-right: 50px; margin-top: 50px; margin-bottom: 50px;" align="center">
<mm:import externid="username" from="parameters" />
<mm:import externid="reason">please</mm:import>
<mm:import externid="referrer">index.jsp</mm:import>
<mm:compare referid="reason" value="failed">
	<p class="message">You failed to log in. Try again.</p>
	<p>&nbsp;</p>
</mm:compare>
<form method="post" action="<mm:url page="$referrer" />">
<input type="hidden" name="command" value="login">
<input type="hidden" name="cloud" value="mmbase"><!-- also default -->
<input type="hidden" name="authenticate" value="name/password">
<table border="0" cellspacing="0" cellpadding="4" class="table-left">
  <tr>
	<td width="50"><img src="img/mmbase-edit-40.gif" alt="my_editors" width="41" height="40" border="0" hspace="4" vspace="4" /></td>
	<td>
	  <div class="top-title">my_editors</div>
	  <div class="top-links">Generic JSP editors for MMBase</div>
	</td>
  </tr>
  <tr><td>&nbsp;</td><td><b>Please login</b></td></tr>
  <tr><td class="name">Name</td><td><input type="text" name="username" /></td></tr>
  <tr><td class="name">Password</td><td><input type="password" name="password" /></td></tr>
  <tr><td>&nbsp;</td><td><input type="submit" name="Login" value="login" /></td></tr>
</table>
</form>
</div>
</body>
</html>
