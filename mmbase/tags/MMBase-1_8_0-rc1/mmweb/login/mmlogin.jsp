<% String title = "Log in"; %>
<%@ include file="inc_top.jsp" %>
<?xml version="1.0" encoding="iso-8859-1"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/2000/REC-xhtml1-20000126/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<link rel="stylesheet" href="<%= request.getContextPath() %>/mmbase/my_editors/my_editors.css" type="text/css" />
	<title>mmbase.org - <%= title %></title>
</head>
<body bgcolor="#FFFFFF">
<div style="margin-left: 50px; margin-right: 50px; margin-top: 50px; margin-bottom: 50px;" align="center">
<mm:import externid="username" from="parameters" />
<mm:import externid="referrer"><%= request.getContextPath() %>/dologin.jsp</mm:import>
<% if(request.getParameter("reason") != null) { %>
	<p class="message"><%= request.getParameter("reason") %> </p>
	<p>&nbsp;</p>
<% } %>
<form method="post" action="<mm:write referid="referrer" jspvar="r" vartype="string"><%=response.encodeURL(r)%></mm:write>" >
<% String orgLocation = request.getParameter("orgLocation");
if(orgLocation != null) { %>
<input type="hidden" name="orgLocation" value="<%=orgLocation%>">
<% } %>
<table border="0" cellspacing="0" cellpadding="4" class="table-left">
  <tr>
	<td width="50"><img src="/mmbase/my_editors/img/mmbase-edit-40.gif" alt="my_editors" width="41" height="40" border="0" hspace="4" vspace="4" /></td>
	<td>
	  <div class="top-title">mmbase.org</div>
	</td>
  </tr>
<% if(request.getParameter("message") != null) { %>
	<tr><td>&nbsp;</td><td class="name"><b><%= request.getParameter("message") %></b></td></tr>
<% } %>
  <tr><td>&nbsp;</td><td class="name"><b>Please login</b></td></tr>
  <tr><td class="name" align="right">Name</td><td><input class="mmbase" type="text" name="username" /></td></tr>
  <tr><td class="name" align="right">Password</td><td><input class="mmbase" type="password" name="password" /></td></tr>
  <tr><td>&nbsp;</td><td><input type="submit" name="command" value="login" /></td></tr>
  <tr><td>&nbsp;</td><td class="name"><b>Forgot your password?</b></td></tr>
  <tr><td class="name" align="right">Email</td><td><input class="mmbase" type="text" name="email" /></td></tr>
  <tr><td>&nbsp;</td><td><input type="submit" name="forgotten" value="forgotten email" /></td></tr>
</table>
</form>
</div>
</body>
</html>
