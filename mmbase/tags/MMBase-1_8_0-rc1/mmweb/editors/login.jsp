<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/2000/REC-xhtml1-20000126/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<title>MMBase - Login</title>
	<link rel="stylesheet" href="/css/mmbase.css" media="screen" />
</head>
<body>
<div style="margin-left: 50px; margin-right: 50px; margin-top: 50px; margin-bottom: 50px;" align="center">
<mm:import externid="username" from="parameters" />
<mm:import externid="reason">please</mm:import>
<mm:import externid="referrer">index.jsp</mm:import>
<form method="post" action="<mm:write referid="referrer" jspvar="r" vartype="string"><%=response.encodeURL(r)%></mm:write>" >
<table border="0" cellspacing="0" cellpadding="4">
  <tr>
	<td width="50"><img src="/mmbase/style/logo.gif" alt="MMBase" width="40" height="50" border="0" hspace="4" vspace="4" /></td>
	<td>
	<h2>MMBase.org editors</h2>
	<mm:compare referid="reason" value="failed">
		<p class="message">You failed to log in. Try again.</p>
	</mm:compare>
	</td>
  </tr>
  <tr><td>&nbsp;</td><td class="name"><b>Please login</b></td></tr>
  <tr><td align="right"><strong>Name</strong></td><td><input type="text" name="username" /></td></tr>
  <tr><td align="right"><strong>Password</strong></td><td><input type="password" name="password" /></td></tr>
  <tr><td>&nbsp;</td><td><input type="submit" name="command" value="login" /></td></tr>
</table>
</form>
</div>
</body>
</html>
