<?xml version="1.0" encoding="iso-8859-1"?>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<title>XML Import - Log in</title>
    <link rel="stylesheet" href="css/mmbase.css" type="text/css" />
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
<form method="post" action="<mm:write referid="referrer" jspvar="r" vartype="string"><%=response.encodeURL(r)%></mm:write>" >
<table border="0" cellspacing="0" cellpadding="4" class="table-left">
  <tr>
	<td width="50">&nbsp;</td>
	<td>
	  <div class="top-title"><h2>XML Import</h2></div>
	  <div class="top-links"></div>
	</td>
  </tr>
  <tr><td>&nbsp;</td><td class="name"><b>Please login</b></td></tr>
  <tr><td class="name" align="right">Name</td><td><input type="text" name="username" /></td></tr>
  <tr><td class="name" align="right">Password</td><td><input type="password" name="password" /></td></tr>
  <tr><td>&nbsp;</td><td><input type="submit" name="command" value="login" /></td></tr>
</table>
</form>
</div>
</body>
</html>
