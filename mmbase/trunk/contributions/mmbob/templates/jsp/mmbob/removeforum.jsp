<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
<mm:content type="text/html" encoding="UTF-8" escaper="entities" expires="0">
<%@ include file="thememanager/loadvars.jsp" %>

<mm:import externid="forumid" />
<mm:import externid="postareaid" />

<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
 <mm:include page="actions.jsp" />
</mm:present>
<!-- end action check -->

<html>
<head>
   <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
   <title>MMBob</title>
</head>
<body>

<div class="header">
  <mm:import id="headerpath" jspvar="headerpath"><mm:write referid="theme_header" /></mm:import>
  <jsp:include page="<%=headerpath%>"/>
</div>
                                                                                              
<div class="bodypart">

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 50px;" width="40%">
  <form action="<mm:url page="removeforum_confirm.jsp"></mm:url>" method="post">
  <tr><th colspan="3">Selecteer forum dat je wilt verwijderen</th></tr>
  <tr><td colspan="3" align="middle">
	<select name="remforum">
  	<mm:nodelistfunction set="mmbob" name="getForums">
		<option value="<mm:field name="id" />"><mm:field name="name" />
	</mm:nodelistfunction>
	</select>
  </td></tr>
  <tr><td>
	<p />
	<center>
	<input type="submit" value="Ja, Verwijderen">
  	</form>
	</td>
	<td>
  	<form action="<mm:url page="forums.jsp">
	</mm:url>"
 	method="post">
	<p />
	<center>
	<input type="submit" value="Oops, Nee">
  	</form>
	</td>
	</tr>

</table>

</div>

<div class="footer">
  <mm:import id="footerpath" jspvar="footerpath"><mm:write referid="theme_footer" /></mm:import>
  <jsp:include page="<%=footerpath%>"/>
</div>
                                                                                              
</body>
</html>

</mm:content>
</mm:cloud>

